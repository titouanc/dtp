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
    private String daeFilename;
    private Document document = null;
    private String path;

	/**
	 * Parse the kmz archive file and calls the kml parser
	 * @param filename
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public KmzParser(String path, MasterDAO master) throws IOException, SQLException{ // Open kmz file and take the .kml file to parse it
		super(path, master);
		File file=new File(path);
        try {
			this.zipFile = new ZipFile(file);
			daeFilename = this.findDaeFile(); // Here we got the content of the KML File

		} catch (ZipException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
     * Returns an input stream to the first KML file in the KMZ file.
     * @return first KML file or null if there is no KML files
	 * @throws IOException 
     */
    public String findDaeFile() throws IOException
    {
        Enumeration<? extends ZipEntry> zipEntries = this.zipFile.entries();
        while (zipEntries.hasMoreElements())
        {
            ZipEntry entry = zipEntries.nextElement();
            if (entry.getName().toLowerCase().endsWith(".dae"))
            {
                return entry.getName();
            }
        }
        return null;
    }

	@Override
	public void parse() throws SQLException, IOException {
		// TODO use DAE Parser with this.daeFilename
	}
}
