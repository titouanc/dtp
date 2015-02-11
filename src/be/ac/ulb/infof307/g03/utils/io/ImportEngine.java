package be.ac.ulb.infof307.g03.utils.io;

import java.io.File;

import be.ac.ulb.infof307.g03.models.GeometryDAO;

public class ImportEngine {

	GeometryDAO dao = null;
	
	public ImportEngine(GeometryDAO dao) {
		this.dao = dao;
	}
	
	public void handleImport(String fileName, String path) {
		String extension = getExtension(fileName);
		File file = new File(path+fileName);
		if (extension.equals("dae")) {
			// TODO handle dae import
		} else if (extension.equals("obj")) {
			// TODO handle obj import
		} else if (extension.equals("3ds")) {
			// TODO handle 3ds import
		} else if (extension.equals("kmz")) {
			// TODO handle kmz import
		}
	}
	
	private String getExtension(String fileName) {
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			return fileName.substring(i+1);
		}
		return "";
	}
}

