package be.ac.ulb.infof307.g03.utils.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.sql.SQLException;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import be.ac.ulb.infof307.g03.models.Entity;
import be.ac.ulb.infof307.g03.models.MasterDAO;
import be.ac.ulb.infof307.g03.models.Primitive;
import be.ac.ulb.infof307.g03.models.Triangle;
import be.ac.ulb.infof307.g03.models.Vertex;
import be.ac.ulb.infof307.g03.utils.Log;

import com.j256.ormlite.dao.ForeignCollection;
import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;

public class DAEParser extends Parser {
	Document document = null;
	Vector<Vertex> vertices = null;
	
	public DAEParser(String fileName, MasterDAO dao) throws IOException, SQLException{
		super(fileName, dao);
		try{ 
			DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance(); 
			DocumentBuilder constructeur = fabrique.newDocumentBuilder(); 
			File xml = new File(fileName); 
			this.document = constructeur.parse(xml); 
			parse();
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

	public void addVertices(String data) {
		Log.debug("addVertices : "+data);
		String[] d = data.split(" ");
		this.vertices = new Vector<Vertex>();
		for (int i=0; i<d.length; i+=3) {
			vertices.add(new Vertex(this.primitive, new Vector3f(Float.parseFloat(d[i]),Float.parseFloat(d[i+1]),Float.parseFloat(d[i+2]))));
			vertices.lastElement().setIndex(i/3);
			try {
				this.daoFactory.getDao(Vertex.class).create(vertices.lastElement());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void addIndexes(String data, int offset, int period) {
		Log.debug("addIndexes : "+data);
		String[] d = data.split(" ");
		for (int i=offset; i<d.length; i+=period*3) {
			try {
				Vertex v1 = this.vertices.get(Integer.parseInt(d[i]));
				Vertex v2 = this.vertices.get(Integer.parseInt(d[i+period]));
				Vertex v3 = this.vertices.get(Integer.parseInt(d[i+(2*period)]));
				Triangle triangle = new Triangle(this.primitive,v1,v2,v3);
				triangle.setIndex((i-offset)/(period*3));
				this.daoFactory.getDao(Triangle.class).create(triangle);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ArrayIndexOutOfBoundsException e) {
				//e.printStackTrace();
			}
		}
	}
	
	private void parseIndexes(NodeList nodeList) {
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
	
	public void parse() {
		Log.debug("Parse");
		NodeList nodeList = document.getElementsByTagName("geometry");
		for (int i=0; i<nodeList.getLength(); ++i) {
			if (i>0) {
				primitive = new Primitive(this.primitive.getEntity(),Primitive.IMPORTED);
				try {
					this.daoFactory.getDao(Primitive.class).insert(this.primitive);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
						
			Node verticesNode = ((Element) nodeList.item(i)).getElementsByTagName("vertices").item(0);
			NodeList inputNodeList = ((Element)verticesNode).getElementsByTagName("input");
			Vector<String> srcNames = new Vector<String>();
			for (int k=0; k<inputNodeList.getLength(); ++k ) {
				srcNames.addElement(inputNodeList.item(k).getAttributes().getNamedItem("source").getTextContent().substring(1));
			}
			
			NodeList srcNodeList = ((Element)nodeList.item(i)).getElementsByTagName("source");
			for (int k=0; k<srcNodeList.getLength(); ++k) {
				if (srcNames.contains(srcNodeList.item(k).getAttributes().getNamedItem("id").getTextContent())) {
					addVertices(((Element) srcNodeList.item(k)).getElementsByTagName("float_array").item(0).getTextContent());
				}
			}
			
			NodeList polyNodeList = ((Element) nodeList.item(i)).getElementsByTagName("polylist");
			if (polyNodeList.getLength()>0) {
				parseIndexes(polyNodeList);
			} else {
				NodeList trianglesNodeList = ((Element) nodeList.item(i)).getElementsByTagName("triangles");
				parseIndexes(trianglesNodeList);
			}
			
		}
	}
	
}
