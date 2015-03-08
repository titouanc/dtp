/**
 * 
 */
package be.ac.ulb.infof307.g03.io.importer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;


import be.ac.ulb.infof307.g03.models.MasterDAO;
/**
 * @author Walter
 *
 */
public class KmzParser extends Parser {
	private ZipFile zipFile;
    private InputStream stream;
    private String path;
    private MasterDAO master = null;

	/**
	 * Parse the kmz archive file and calls the kml parser
	 * @param path : the source file with the kmz
	 * @param dao : the DAO factory
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public KmzParser(String path, MasterDAO dao) throws IOException, SQLException{ // Open kmz file and take the .kml file to parse it
		super();
		this.master = dao;
		this.path = path;
		File file=new File(path);
        try {
			this.zipFile = new ZipFile(file);
			this.stream = this.findDaeFile(); // Here we got the content of the KML File
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
    public InputStream findDaeFile() throws IOException
    {
        Enumeration<? extends ZipEntry> zipEntries = this.zipFile.entries();
        while (zipEntries.hasMoreElements())
        {
            ZipEntry entry = zipEntries.nextElement();
            if (entry.getName().toLowerCase().endsWith(".dae"))
            {
                return this.zipFile.getInputStream(entry);
            }
        }
        return null;
    }

	public void parse() throws SQLException, IOException {
		DAEParser parser = new DAEParser(this.path, this.master, this.stream);
		parser.parse();
	}
}
