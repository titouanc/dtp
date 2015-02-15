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
	Entity entity = null;
	MasterDAO dao = null;
	
	public DAEParser(String fileName, Entity entity, MasterDAO dao){
		this.entity = entity ;
		this.dao = dao;
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
			vertices.add(new Vertex(this.primitives.lastElement(), new Vector3f(Float.parseFloat(d[i]),Float.parseFloat(d[i+1]),Float.parseFloat(d[i+2]))));
			vertices.lastElement().setIndex(i/3);
			try {
				this.dao.getDao(Vertex.class).create(vertices.lastElement());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void addIndexes(String data) {
		Log.debug("addIndexes : "+data);
		String[] d = data.split(" ");
		for (int i=0; i<d.length; i+=3) {
			try {
				Primitive p = this.primitives.lastElement();
				Vertex v1 = this.vertices.get(Integer.parseInt(d[i]));
				Vertex v2 = this.vertices.get(Integer.parseInt(d[i+1]));
				Vertex v3 = this.vertices.get(Integer.parseInt(d[i+2]));
				Triangle triangle = new Triangle(p,v1,v2,v3);
				triangle.setIndex(i/3);
				this.dao.getDao(Triangle.class).create(triangle);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ArrayIndexOutOfBoundsException e) {
				//e.printStackTrace();
			}
		}
	}
	
	public void parse() {
		Log.debug("Parse");
		NodeList nodeList = document.getElementsByTagName("geometry");
		for (int i=0; i<nodeList.getLength(); ++i) {
			primitives.addElement(new Primitive(this.entity,Primitive.IMPORTED));
			try {
				this.dao.getDao(Primitive.class).insert(this.primitives.lastElement());
			} catch (SQLException e) {
				e.printStackTrace();
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
			
			NodeList polyNodeList = ((Element) nodeList.item(i)).getElementsByTagName("p");
			for (int k=0; k<polyNodeList.getLength(); ++k ) {
				addIndexes(polyNodeList.item(k).getTextContent());
			}
		}
	}
	
}
