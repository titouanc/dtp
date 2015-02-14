package be.ac.ulb.infof307.g03.utils.io;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;

import be.ac.ulb.infof307.g03.models.Entity;
import be.ac.ulb.infof307.g03.models.MasterDAO;
import be.ac.ulb.infof307.g03.models.Primitive;
import be.ac.ulb.infof307.g03.utils.Log;

public class ExportEngine {
	MasterDAO dao = null;
	Entity exportable = null;
	
	public ExportEngine(MasterDAO daoFactory) {
		this.dao = daoFactory;
	}
	
	private String getExtension(String fileName) {
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			return fileName.substring(i+1);
		}
		return "";
	}
	
	public void handleExport(Entity entity, String fileName, String path) {
		this.exportable = entity;
		String extension = getExtension(fileName);
		if (extension.equals("dae")) {
			handleDae(path+"/"+fileName);
		} else if (extension.equals("obj")) {
			handleObj(path+"/"+fileName);
		} else if (extension.equals("3ds")) {
			// TODO handle 3ds import
		} else if (extension.equals("kmz")) {
			// TODO handle kmz import
		}
	}
	
	public void handleDae(String fileName) {
		try {
			PrintWriter file = new PrintWriter(fileName,"UTF-8");
			for (Primitive primitive : this.exportable.getPrimitives()) {
				float[] vertices = primitive.getVertices();
				for (int i=0; i<vertices.length; ++i) {
					file.print(String.valueOf(vertices[i])+" ");
				}
				file.println();
				int[] indexes = primitive.getIndexes();
				for (int i=0; i<indexes.length; ++i) {
					file.print(String.valueOf(indexes[i])+" ");
				}
				file.println();
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
	
	public void handleObj(String fileName){
		try {
			PrintWriter file = new PrintWriter(fileName,"UTF-8");
			file.print("# Blender v2.73 (sub 0) OBJ File: ''");
			file.print("# www.blender.org");
			for (Primitive primitive : this.exportable.getPrimitives()) {
				//Vectrices
				float[] vertices = primitive.getVertices();
				int verticesNumber = vertices.length/3;
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
				int facesNumber = faces.length/3;
				for (int i=0; i<facesNumber; i+=3) {
					file.print("v ");
					file.print(String.valueOf(faces[i])+" ");
					file.print(String.valueOf(faces[i+1])+" ");
					file.print(String.valueOf(faces[i+2])+" ");
					file.println();
				}
				
				
			}
		} catch (FileNotFoundException e) {
			Log.log(Level.FINEST, "[ERROR] File couldn't be exported");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			Log.log(Level.FINEST, "[ERROR] File couldn't be exported - Encoding error");
			e.printStackTrace();
		}
		
	}
}
