package be.ac.ulb.infof307.g03.utils.exporter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;

import be.ac.ulb.infof307.g03.models.Entity;
import be.ac.ulb.infof307.g03.models.Primitive;
import be.ac.ulb.infof307.g03.utils.Log;


/**
 * @author pierre
 *
 */
public class OBJExporter {

	/**
	 *  Constructor of OBJExporter
	 */
	public OBJExporter(){

	}
	
	/**
	 * Export to file
	 * @param fileToExport The file in which the object will be write
	 * @param entity The entity to be exported
	 */
	public void export(File fileToExport, Entity entity){
		try {
			PrintWriter file = new PrintWriter(fileToExport,"UTF-8");
			file.print("# Created with HomePlans");
			file.println();
			file.print("# www.supayr.ninja");
			file.println();
			for (Primitive primitive : entity.getPrimitives()) {
				//Vectrices
				float[] vertices = primitive.getVertices();
				int verticesNumber = vertices.length;
				for (int i=0; i<verticesNumber; i+=3) {
					file.print("v ");
					file.print(String.valueOf(vertices[i])+" ");
					file.print(String.valueOf(vertices[i+1])+" ");
					file.print(String.valueOf(vertices[i+2])+" ");
					file.println();
				}
				file.println();
				//Faces
				int[] faces = primitive.getIndexes();
				int facesNumber = faces.length;
				for (int i=0; i<facesNumber; i+=3) {
					file.print("f ");
					file.print(String.valueOf(faces[i]+1)+" ");
					file.print(String.valueOf(faces[i+1]+1)+" ");
					file.print(String.valueOf(faces[i+2]+1)+" ");
					file.println();
				}			
			}
			file.close();
		} catch (FileNotFoundException e) {
			Log.log(Level.FINEST, "[ERROR] File couldn't be exported");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			Log.log(Level.FINEST, "[ERROR] File couldn't be exported - Encoding error");
			e.printStackTrace();
		}

	}
}
