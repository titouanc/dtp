package be.ac.ulb.infof307.g03.utils.io;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import be.ac.ulb.infof307.g03.models.Entity;
import be.ac.ulb.infof307.g03.models.MasterDAO;
import be.ac.ulb.infof307.g03.models.Primitive;

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
			// TODO handle obj import
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
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
