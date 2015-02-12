package be.ac.ulb.infof307.g03.utils.parser;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.jme3.math.Vector3f;

public class DAEParser extends Parser {
	Document document = null;
	int [] mappings;

	public DAEParser(String fileName){ 
		try{ 
			// creation d'une fabrique de documents 
			DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance(); 

			// creation d'un constructeur de documents 
			DocumentBuilder constructeur = fabrique.newDocumentBuilder(); 

			// lecture du contenu d'un fichier XML avec DOM 
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

	public void addPosition(String data) {
		String[] d = data.split(" ");
		for (int i=0; i<d.length; i+=3) {
			this.vertices.addElement(new Vector3f(Float.parseFloat(d[i]),Float.parseFloat(d[i+1]),Float.parseFloat(d[i+2])));
		}
	}
	
	
	public void addMapping(String data) {
		String[] d = data.split(" ");
		this.mappings = new int[d.length];
		for (int i=0; i<d.length; i+=3) {
			this.mappings[i] = Integer.parseInt(d[i]);
		}
	}
	
	public void parseVertices() {
		NodeList nodeList = document.getElementsByTagName("geometry");
		NodeList vertexNode = document.getElementsByTagName("float_array");
		NodeList mappingNode = document.getElementsByTagName("p");
		
		System.out.println(nodeList.getLength());
		System.out.println(vertexNode.getLength());
		System.out.println(mappingNode.getLength());
		
		for (int i=0; i<mappingNode.getLength(); ++i) {
			String primitiveName = nodeList.item(i).getAttributes().getNamedItem("name").toString();
			addPosition(vertexNode.item(i*3).getTextContent());
			addMapping(mappingNode.item(i).getTextContent());
			
		}
	}

	public static void main(String[] args) {
		DAEParser d = new DAEParser("/Users/julianschembri/Downloads/test.dae");
		d.parseVertices();
		System.out.println(d.vertices);
		System.out.println(d.mappings);

	}
	
}
