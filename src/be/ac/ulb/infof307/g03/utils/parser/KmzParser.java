/**
 * 
 */
package be.ac.ulb.infof307.g03.utils.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import be.ac.ulb.infof307.g03.models.MasterDAO;
/**
 * @author Walter
 *
 */
public class KmzParser extends Parser {

	private ZipFile zipFile;
    private InputStream stream;
    private Document document = null;

	/**
	 * Parse the kmz archive file and calls the kml parser
	 * @param filename
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public KmzParser(String path, MasterDAO dao) throws IOException, SQLException{ // Open kmz file and take the .kml file to parse it
		super(path,dao);
		File file=new File(path);
        try {
			this.zipFile = new ZipFile(file);
			stream = this.getKMLStream(); // Here we got the content of the KML File
			this.parseKMLFile();		  // We parse the KML File
			this.getCoordinates();
		} catch (ZipException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Parse the KML File given an input stream
	 */
	 private void parseKMLFile() {
		try{ 
			// création d'une fabrique de documents 
			DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance(); 

			// création d'un constructeur de documents 
			DocumentBuilder constructeur = fabrique.newDocumentBuilder(); 
			
			this.document = constructeur.parse(this.stream); 
			
		} catch(ParserConfigurationException pce){ 
			System.out.println("Erreur de configuration du parseur DOM"); 
			System.out.println("lors de l'appel : fabrique.newDocumentBuilder();"); 
		} catch(SAXException se){ 
			System.out.println("Erreur lors du parsing du document"); 
			System.out.println("lors de l'appel : construteur.parse(stream)"); 
		} catch(IOException ioe){ 
			System.out.println("Erreur d'entrée/sortie"); 
			System.out.println("lors de l'appel : construteur.parse(stream)"); 
		} 
	}
		

	/**
     * Returns an input stream to the first KML file in the KMZ file.
     * @return first KML file or null if there is no KML files
	 * @throws IOException 
     */
    public synchronized InputStream getKMLStream() throws IOException
    {
        Enumeration<? extends ZipEntry> zipEntries = this.zipFile.entries();
        while (zipEntries.hasMoreElements())
        {
            ZipEntry entry = zipEntries.nextElement();
            if (entry.getName().toLowerCase().endsWith(".kml"))
            {
                return this.zipFile.getInputStream(entry);
            }
        }
        return null;
    }
    
    /**
     * Get coordinates
     */
    public void getCoordinates() {
    	//Vector <String> name = new Vector<String>();
 		NodeList nameList = document.getElementsByTagName("Placemark");
 		System.out.println(nameList.getLength());
 		
 		for (int i=0 ; i<nameList.getLength(); ++i){
 			System.out.println(nameList.item(i));
 		}	 
	}
  
	@Override
	public void parse() throws SQLException, IOException {
		// TODO Auto-generated method stub
		
	}
}
