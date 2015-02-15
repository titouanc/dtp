/**
 * 
 */
package be.ac.ulb.infof307.g03.utils.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
/**
 * @author Walter
 *
 */
public class KmzParser extends Parser {

	private ZipFile zipFile;
    private InputStream stream;
    private Document document = null;

	/**
	 * Parse the kmz archive file, unzip it
	 * @param filename
	 */
	public KmzParser(String path){ // Open kmz file and take the .kml file to parse it
		File file=new File(path);
        try {
			this.zipFile = new ZipFile(file);
			String daeFile = this.getDAEFilename();
			if (daeFile != null){
				
			}
		} catch (ZipException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
     * Returns the filename of the first DAE file's name found, null if no DAE file
     * @return filename of the first DAE file's name found or null
	 * @throws IOException 
     */
    public synchronized String getDAEFilename() throws IOException
    {
        Enumeration<? extends ZipEntry> zipEntries = this.zipFile.entries();
        while (zipEntries.hasMoreElements()) {
            ZipEntry entry = zipEntries.nextElement();
            if (entry.getName().toLowerCase().endsWith(".dae")) {
                return entry.getName();
            }
        }
        return null;
    }
}
