package be.ac.ulb.infof307.g03.utils.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;

public class DAEParser extends Parser {
	Document document = null;

	public DAEParser(String fileName){
		try{ 
			DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance(); 
			DocumentBuilder constructeur = fabrique.newDocumentBuilder(); 
			File xml = new File(fileName); 
			this.document = constructeur.parse(xml); 
		} catch(ParserConfigurationException pce){ 
			System.out.println("Erreur de configuration du parseur DOM"); 
			System.out.println("lors de l'appel à fabrique.newDocumentBuilder();"); 
		} catch(SAXException se){ 
			System.out.println("Erreur lors du parsing du document"); 
			System.out.println("lors de l'appel à construteur.parse(xml)"); 
		} catch(IOException ioe){ 
			System.out.println("Erreur d'entrée/sortie"); 
			System.out.println("lors de l'appel à construteur.parse(xml)"); 
		} 
	}

	public void addVertices(String data, int pos) {
		String[] d = data.split(" ");
		for (int i=0; i<d.length; i+=3) {
			this.datas.elementAt(pos).appendVertex(new Vector3f(Float.parseFloat(d[i]),Float.parseFloat(d[i+1]),Float.parseFloat(d[i+2])));
		}
	}
	
	public void addIndexes(String data, int pos) {
		String[] d = data.split(" ");
		for (int i=0; i<d.length; ++i) {
			this.datas.elementAt(pos).appendIndex(Integer.parseInt(d[i]));
		}
	}
	
	private InputStream nodeContentToInputStream(String nodeContent) {
		return new ByteArrayInputStream(nodeContent.getBytes());
	}
	
	public void parseVertices() {
		NodeList nodeList = document.getElementsByTagName("geometry");
		for (int i=0; i<nodeList.getLength(); ++i) {
			datas.addElement(new PrimitiveData());
			int pos = datas.size()-1;
			
			datas.elementAt(pos).setName(nodeList.item(i).getAttributes().getNamedItem("name").toString());
			
			NodeList polyNodeList = ((Element) nodeList.item(i)).getElementsByTagName("p");
			for (int k=0; k<polyNodeList.getLength(); ++k ) {
				addIndexes(polyNodeList.item(k).getTextContent(),pos);
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
					addVertices(((Element) srcNodeList.item(k)).getElementsByTagName("float_array").item(0).getTextContent(),pos);
				}
			}
		}
	}

	public static void main(String[] args) {
		
		DAEParser d = new DAEParser("/Users/julianschembri/Downloads/test.dae");
		d.parseVertices();
		for (int i=0; i<d.datas.size(); ++i) {
			
			System.out.println(d.datas.elementAt(i).name);
			System.out.println(d.datas.elementAt(i).indexes);
			System.out.println(d.datas.elementAt(i).vertices);
		}
		
	}
	
}
