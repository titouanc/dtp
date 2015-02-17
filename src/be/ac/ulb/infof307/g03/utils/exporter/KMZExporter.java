package be.ac.ulb.infof307.g03.utils.exporter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
	 * Export to file
	 * @param fileToExport The file in which the object will be write
	 * @param entity The entity to be exported
	 */
	public void export(File fileToExport, Entity entity){

	}
	
	/**
	 * Add the kml/ DAE file to the zip
	 * @param fileToExport
	 */
	public void addFileToJar(File fileToExport){
		try {
			FileOutputStream toBeExported = new FileOutputStream("exportedKMZ.kmz");
			ZipOutputStream zip = new ZipOutputStream(toBeExported);

			String file1Name = "exportedObject.kml";
			addToZipFile(file1Name, zip);

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
	 * @param zos
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


