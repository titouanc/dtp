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

import be.ac.ulb.infof307.g03.models.GeometricDAO;
import be.ac.ulb.infof307.g03.models.MasterDAO;
import be.ac.ulb.infof307.g03.models.Primitive;
import be.ac.ulb.infof307.g03.models.Triangle;
import be.ac.ulb.infof307.g03.models.Vertex;
import be.ac.ulb.infof307.g03.utils.Log;

/**
 * @author julian
 *
 */
public class DAEParser extends Parser {
	Document document = null;
	Vector<Vertex> vertices = null;
	Vector<String> nodesName = new Vector<String>();
	Vector<Matrix4f> transformationMatrix = new Vector<Matrix4f>();
	
	/**
	 * This parser parses a COLLADA file and constructs a Model with the vertices and the faces found
	 * @param fileName : the path to the COLLADA source file
	 * @param dao the DAO factory
	 * @throws IOException
	 * @throws SQLException
	 */
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
	
	/**
	 * This parser parses a COLLADA file and constructs a Model with the vertices and the faces found.
	 * This parser is used for parsing KMZ
	 * @param fileName : the path to the COLLADA source file
	 * @param dao the dao factory
	 * @param stream : the stream created by the kmz parser
	 * @throws IOException
	 * @throws SQLException
	 */
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

	/**
	 * Gets the vertices and adds them to the DAO
	 * @param data : the data parsed
	 * @throws SQLException
	 */
	public void addVertices(String data) throws SQLException {
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
	
	/**
	 * Gets indexes and adds to the DAO 
	 * @param data : the datas containing the faces
	 * @param offset : offset needed to find the interesting data package 
	 * @param period : size of the package
	 * @throws SQLException
	 */
	public void addIndexes(String data, int offset, int period) throws SQLException {
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
	/**
	 * Parses the nodes looking for the indexes
	 * @param nodeList : List of XML nodes
	 * @throws DOMException
	 * @throws SQLException
	 */
	private void parseIndexes(NodeList nodeList) throws DOMException, SQLException {
		int offset = 0, period = 0;
		for (int i=0; i<nodeList.getLength(); ++i) {
			NodeList inputNodeList = findAllNodes(nodeList.item(i), "input");
			for (int j=0; j<inputNodeList.getLength(); ++j) {
				if (attributeContent(inputNodeList.item(j), "semantic").equals("VERTEX")) {
					offset = Integer.valueOf(attributeContent(inputNodeList.item(j), "offset"));
				}
			}
			period = inputNodeList.getLength();
			NodeList pNodeList = findAllNodes(nodeList.item(i), "p");
			addIndexes(pNodeList.item(0).getTextContent(),offset,period);
		}
	}
	
	private NodeList findAllNodes(Node node, String tagName) {
		return ((Element) node).getElementsByTagName(tagName);
	}
	
	private String attributeContent(Node node,String attributeName) {
		return node.getAttributes().getNamedItem(attributeName).getTextContent();
	}
	
	@Override
	public void parse() throws DOMException, SQLException {
		NodeList nodeList = document.getElementsByTagName("node");
		// find all the primitives to draw in the scene
		for (int i=0; i<nodeList.getLength(); ++i) {
			NodeList instGeoNodeList = findAllNodes(nodeList.item(i), "instance_geometry");
			if (instGeoNodeList.getLength()>0){
				// save the primitive name
				nodesName.addElement(attributeContent(instGeoNodeList.item(0),"url").substring(1));
				NodeList matrixNodeList = findAllNodes(nodeList.item(i),"matrix");
				if (matrixNodeList.getLength()>0) {
					// save the transformation matrix
					String matrixInput = matrixNodeList.item(0).getTextContent();
					String[] indexList = matrixInput.split(" ");
					float[] floatList = new float[indexList.length];
					for (int j=0; j<floatList.length; ++j) {
						floatList[j] = Float.parseFloat(indexList[j]);
					}
					Matrix4f m = new Matrix4f();
					m.readFloatBuffer(FloatBuffer.wrap(floatList));
					this.transformationMatrix.addElement(m);
				} else {
					this.transformationMatrix.addElement(null);
				}
			}
		}
		// find and create the geometry found in the scene
		for (int i=0;i<this.nodesName.size(); ++i) {
			NodeList geometryNodeList = document.getElementsByTagName("geometry");
			for (int j=0; j<geometryNodeList.getLength(); ++j) {
				if (attributeContent(geometryNodeList.item(j),"id").equals(this.nodesName.elementAt(i))) {	
					if (j>0) {
						this.primitive = new Primitive(this.primitive.getEntity(), Primitive.IMPORTED);
						this.daoFactory.getDao(Primitive.class).create(this.primitive);
					} 
					// apply the transformation to the primitive
					if (this.transformationMatrix.elementAt(i) != null) {
						Matrix4f m = this.transformationMatrix.elementAt(i);
						this.primitive.setScale(m.toScaleVector());
						this.primitive.setTranslation(m.toTranslationVector());
						this.primitive.setRotation(m.toRotationMatrix());
						
						this.daoFactory.getDao(Primitive.class).update(this.primitive);
					}
					
					// find vertices id
					Node verticesNode = findAllNodes(geometryNodeList.item(j),"vertices").item(0);
					NodeList inputNodeList = findAllNodes(verticesNode,"input");
					Vector<String> srcNames = new Vector<String>();
					for (int k=0; k<inputNodeList.getLength(); ++k ) {
						srcNames.addElement(attributeContent(inputNodeList.item(k),"source").substring(1));
					}
					
					// find the corresponding id in the geometry's sources
					NodeList srcNodeList = findAllNodes(geometryNodeList.item(j), "source");
					for (int k=0; k<srcNodeList.getLength(); ++k) {
						if (srcNames.contains(attributeContent(srcNodeList.item(k),"id"))) {
							addVertices(findAllNodes(srcNodeList.item(k), "float_array").item(0).getTextContent());
						}
					}
					
					// parse the indexes
					NodeList polyNodeList = findAllNodes(geometryNodeList.item(j),"polylist");
					if (polyNodeList.getLength()>0) {
						parseIndexes(polyNodeList);
					} else {
						NodeList trianglesNodeList = findAllNodes(geometryNodeList.item(j), "triangles");
						parseIndexes(trianglesNodeList);
					}
				}
			}
		}
	}
	
}
