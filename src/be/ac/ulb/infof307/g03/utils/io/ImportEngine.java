package be.ac.ulb.infof307.g03.utils.io;

import java.io.File;
import java.sql.SQLException;
import java.util.Vector;

import be.ac.ulb.infof307.g03.models.Entity;
import be.ac.ulb.infof307.g03.models.MasterDAO;
import be.ac.ulb.infof307.g03.models.Primitive;
import be.ac.ulb.infof307.g03.utils.parser.DAEParser;
import be.ac.ulb.infof307.g03.utils.parser.PrimitiveData;

public class ImportEngine {

	MasterDAO dao = null;
	
	public ImportEngine(MasterDAO dao) {
		this.dao = dao;
	}
	
	public void handleImport(String fileName, String path) {
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
	
	private String getExtension(String fileName) {
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			return fileName.substring(i+1);
		}
		return "";
	}
	
	private void handleDae(String fileName) {
		Entity entity = new Entity("plop");
		DAEParser parser = new DAEParser(fileName,entity,this.dao);
		try {
			this.dao.getDao(Entity.class).insert(entity);
			this.dao.notifyObservers();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

