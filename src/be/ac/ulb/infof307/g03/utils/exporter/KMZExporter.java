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
	public void export(File fileToExport, Entity entity){
		try {
			PrintWriter file = new PrintWriter(fileToExport,"UTF-8");
			file.println("<Model id=\"ID\">");
			file.println("  <Link>");
			file.println("    <href>"+entity.getName()+".dae</href>");
			file.println("  </Link>");
			file.println("</Model>");		
			file.close();
			this.addFileToJar(fileToExport); // Add the file with the kml content into the jar(kmz)
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
			FileOutputStream toBeExported = new FileOutputStream("exportedKMZ.kmz");
			ZipOutputStream zip = new ZipOutputStream(toBeExported);

			addToZipFile(fileToExport.getName(), zip);

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
	 * @param fileName
	 * @param zip
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void addToZipFile(String fileName, ZipOutputStream zip) throws FileNotFoundException, IOException {

		File file = new File(fileName);
		FileInputStream fis = new FileInputStream(file);
		ZipEntry zipEntry = new ZipEntry(fileName);
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


