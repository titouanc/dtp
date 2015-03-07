package be.ac.ulb.infof307.g03.io.exporter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
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
			file.println("# Created with HomePlans");
			file.println("# www.supayr.ninja");
			
			// global vertex index
			List<Primitive> allPrimitives = new ArrayList<Primitive>(entity.getPrimitives());
			for (Primitive primitive : allPrimitives) {
				//Vertices
				float[] vertices = primitive.getVertices();
				int verticesNumber = vertices.length;
				for (int i=0; i<verticesNumber; i+=3) {
					file.print("v ");
					for (int j=0; j<3; j++)
						file.print(String.valueOf(vertices[i+j])+" ");
					file.println();
				}
				file.println();
			}
			
			int vIndex = 0;
			for (Primitive primitive : allPrimitives){
				//Faces
				int[] faces = primitive.getIndexes();
				int facesNumber = faces.length;
				for (int i=0; i<facesNumber; i+=3) {
					file.print("f ");
					for (int j=0; j<3; j++)
						file.print(String.valueOf(vIndex + faces[i+j] + 1) + " ");
					file.println();
				}
				file.println();
				
				vIndex += primitive.getVertices().length/3;
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
