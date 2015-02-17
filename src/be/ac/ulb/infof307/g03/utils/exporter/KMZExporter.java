package be.ac.ulb.infof307.g03.utils.exporter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import be.ac.ulb.infof307.g03.models.Entity;


/**
 * @author pierre,walter
 *
 */
public class KMZExporter {
	
	/**
	 *  Constructor of KMZExporter
	 */
	public KMZExporter(){
	}
	
	/**
	 * Write the entity into the kml file
	 * @param fileToExport The file in which the object will be write
	 * @param entity The entity to be exported
	 */
	public void export(File fileToExport, Entity entity){ //fileToExport is a .kmz
		try {
			// Get the filename without the extension (kmz !=kml)
			String filename = fileToExport.getAbsolutePath().replace(".kmz", ".kml");

			PrintWriter file = new PrintWriter(filename,"UTF-8");
			file.println("<Model id=\"ID\">");
			file.println("  <Link>");
			file.println("    <href>"+entity.getName()+".dae</href>");
			file.println("  </Link>");
			file.println("</Model>");		
			file.close(); // We wrote the content of the kml file
			
			File kml= new File(filename);		
			this.addFileToJar(kml); // Add the file with the kml content into the jar(kmz)		
			kml.delete();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * Add the kml/DAE file to the zip
	 * @param fileToExport
	 */
	public void addFileToJar(File fileToExport){
		try {
			String filename=fileToExport.getAbsolutePath().replace(".kml",".kmz");
			FileOutputStream toBeExported = new FileOutputStream(filename);
			ZipOutputStream zip = new ZipOutputStream(toBeExported);
			
			addToZipFile(fileToExport, zip);

			zip.close();
			toBeExported.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Add a file to a ZIP
	 * @param kmlFile
	 * @param zip
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void addToZipFile(File kmlFile, ZipOutputStream zip) throws FileNotFoundException, IOException {

		FileInputStream fis = new FileInputStream(kmlFile);
		ZipEntry zipEntry = new ZipEntry(kmlFile.getName());
		zip.putNextEntry(zipEntry);
	
		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zip.write(bytes, 0, length);
		}
		zip.closeEntry();
		fis.close();
	}
}


