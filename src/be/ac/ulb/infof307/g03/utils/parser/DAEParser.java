package be.ac.ulb.infof307.g03.utils.parser;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;

import be.ac.ulb.infof307.g03.models.GeometricDAO;
import be.ac.ulb.infof307.g03.models.MasterDAO;
import be.ac.ulb.infof307.g03.models.Primitive;
import be.ac.ulb.infof307.g03.models.Triangle;
import be.ac.ulb.infof307.g03.models.Vertex;
import be.ac.ulb.infof307.g03.utils.Log;

public class DAEParser extends Parser {
	Document document = null;
	Vector<Vertex> vertices = null;
	Vector<String> nodesName = new Vector<String>();
	Vector<Matrix4f> transformationMatrix = new Vector<Matrix4f>();
	
	public DAEParser(String fileName, MasterDAO dao) throws IOException, SQLException{
		super(fileName, dao);
		try{ 
			DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance(); 
			DocumentBuilder constructeur = fabrique.newDocumentBuilder(); 
			File xml = new File(fileName); 
			this.document = constructeur.parse(xml);
		} catch(ParserConfigurationException pce){ 
			System.out.println("Erreur de configuration du parseur DOM"); 
			System.out.println("lors de l'appel a fabrique.newDocumentBuilder();"); 
		} catch(SAXException se){ 
			System.out.println("Erreur lors du parsing du document"); 
			System.out.println("lors de l'appel a construteur.parse(xml)"); 
		} catch(IOException ioe){ 
			System.out.println("Erreur d'entree/sortie"); 
			System.out.println("lors de l'appel a construteur.parse(xml)"); 
		} 
	}
	
	public DAEParser(String fileName, MasterDAO dao, InputStream stream) throws IOException, SQLException{
		super(fileName, dao);
		try{ 
			DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance(); 
			DocumentBuilder constructeur = fabrique.newDocumentBuilder(); 
			this.document = constructeur.parse(stream);
		} catch(ParserConfigurationException pce){ 
			System.out.println("Erreur de configuration du parseur DOM"); 
			System.out.println("lors de l'appel a fabrique.newDocumentBuilder();"); 
		} catch(SAXException se){ 
			System.out.println("Erreur lors du parsing du document"); 
			System.out.println("lors de l'appel a construteur.parse(xml)"); 
		} catch(IOException ioe){ 
			System.out.println("Erreur d'entree/sortie"); 
			System.out.println("lors de l'appel a construteur.parse(xml)"); 
		}
	}

	public void addVertices(String data) throws SQLException {
		//Log.debug("addVertices : "+data);
		String[] d = data.split(" ");
		GeometricDAO<Vertex> vertexDao = this.daoFactory.getDao(Vertex.class);
		this.vertices = new Vector<Vertex>();
		for (int i=0; i<d.length; i+=3) {
			Vertex newVert = new Vertex(this.primitive, Float.parseFloat(d[i]), Float.parseFloat(d[i+1]), Float.parseFloat(d[i+2]));
			newVert.setIndex(i/3);
			vertexDao.create(newVert);
			vertices.add(newVert);
		}
	}
	
	public void addIndexes(String data, int offset, int period) throws SQLException {
		//Log.debug("addIndexes : "+data);
		String[] d = data.split(" ");
		GeometricDAO<Triangle> triangleDao = this.daoFactory.getDao(Triangle.class);
		for (int i=offset; i<d.length; i+=period*3) {
			Vertex[] vertices = new Vertex[3];
			for (int j=0; j<3; j++){
				int index = Integer.parseInt(d[i + j*period]);
				assert 0 <= index && index < this.vertices.size();
				vertices[j] = this.vertices.get(index);
			}
			Triangle triangle = new Triangle(this.primitive, vertices);
			triangle.setIndex((i-offset)/(period*3));
			triangleDao.create(triangle);
		}
	}
	
	private void parseIndexes(NodeList nodeList) throws DOMException, SQLException {
		int offset = 0, period = 0;
		for (int l=0; l<nodeList.getLength(); ++l) {
			NodeList inputNodeList = ((Element) nodeList.item(l)).getElementsByTagName("input");
			for (int o=0; o<inputNodeList.getLength(); ++o) {
				if (inputNodeList.item(o).getAttributes().getNamedItem("semantic").equals("VERTEX")) {
					offset = Integer.valueOf(inputNodeList.item(o).getAttributes().getNamedItem("offset").getTextContent());
				}
			}
			period = inputNodeList.getLength();
			NodeList pNodeList = ((Element) nodeList.item(l)).getElementsByTagName("p");
			addIndexes(pNodeList.item(0).getTextContent(),offset,period);
		}
	}
	

	
	@Override
	public void parse() throws DOMException, SQLException {
		Log.debug("Parse");
		
		NodeList nodeList = document.getElementsByTagName("node");
		for (int i=0; i<nodeList.getLength(); ++i) {
			NodeList instGeoNodeList = ((Element) nodeList.item(i)).getElementsByTagName("instance_geometry");
			if (instGeoNodeList.getLength()>0){
				nodesName.addElement(instGeoNodeList.item(0).getAttributes().getNamedItem("url").getTextContent().substring(1));
				NodeList matrixNodeList = ((Element) nodeList.item(i)).getElementsByTagName("matrix");
				if (matrixNodeList.getLength()>0) {
					String matrixInput = matrixNodeList.item(0).getTextContent();
					Log.debug("Matrix : "+matrixInput);
					String[] indexList = matrixInput.split(" ");
					float[] floatList = new float[indexList.length];
					for (int k=0; k<floatList.length; ++k) {
						floatList[k] = Float.parseFloat(indexList[k]);
					}
					Matrix4f m = new Matrix4f();
					m.readFloatBuffer(FloatBuffer.wrap(floatList));
					
					this.transformationMatrix.addElement(m);
				} else {
					this.transformationMatrix.addElement(null);
				}
			}
		}
			
		for (int j=0;j<this.nodesName.size(); ++j) {
			
			NodeList geometryNodeList = document.getElementsByTagName("geometry");
			for (int i=0; i<geometryNodeList.getLength(); ++i) {
				if (geometryNodeList.item(i).getAttributes().getNamedItem("id").getTextContent().equals(this.nodesName.elementAt(j))) {	
					if (i>0) {
						this.primitive = new Primitive(this.primitive.getEntity(), Primitive.IMPORTED);
					} 
					if (this.transformationMatrix.elementAt(j) != null) {
						Matrix4f m = this.transformationMatrix.elementAt(j);
						Log.debug("Matrix4f : "+m.toString());
						Log.debug("Scale : "+m.toScaleVector().toString());
						this.primitive.setScale(m.toScaleVector());
						Log.debug("Translation : "+m.toTranslationVector().toString());
						this.primitive.setTranslation(m.toTranslationVector());
						Log.debug("Rot : "+m.toRotationMatrix().toString());
						this.primitive.setRotation(m.toRotationMatrix());
					}
					if (i>0) {
						this.daoFactory.getDao(Primitive.class).create(this.primitive);
					} else {
						this.daoFactory.getDao(Primitive.class).update(this.primitive);
					}
								
					Node verticesNode = ((Element) geometryNodeList.item(i)).getElementsByTagName("vertices").item(0);
					NodeList inputNodeList = ((Element)verticesNode).getElementsByTagName("input");
					Vector<String> srcNames = new Vector<String>();
					for (int k=0; k<inputNodeList.getLength(); ++k ) {
						srcNames.addElement(inputNodeList.item(k).getAttributes().getNamedItem("source").getTextContent().substring(1));
					}
					
					NodeList srcNodeList = ((Element)geometryNodeList.item(i)).getElementsByTagName("source");
					for (int k=0; k<srcNodeList.getLength(); ++k) {
						if (srcNames.contains(srcNodeList.item(k).getAttributes().getNamedItem("id").getTextContent())) {
							addVertices(((Element) srcNodeList.item(k)).getElementsByTagName("float_array").item(0).getTextContent());
						}
					}
					
					NodeList polyNodeList = ((Element) geometryNodeList.item(i)).getElementsByTagName("polylist");
					if (polyNodeList.getLength()>0) {
						parseIndexes(polyNodeList);
					} else {
						NodeList trianglesNodeList = ((Element) geometryNodeList.item(i)).getElementsByTagName("triangles");
						parseIndexes(trianglesNodeList);
					}
				}
			}
		}
	}
	
}
