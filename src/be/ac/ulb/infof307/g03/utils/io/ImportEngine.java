package be.ac.ulb.infof307.g03.utils.io;

import java.io.File;
import java.sql.SQLException;
import java.util.Vector;

import be.ac.ulb.infof307.g03.models.Entity;
import be.ac.ulb.infof307.g03.models.MasterDAO;
import be.ac.ulb.infof307.g03.models.Primitive;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.utils.Log;
import be.ac.ulb.infof307.g03.utils.parser.DAEParser;
import be.ac.ulb.infof307.g03.utils.parser.PrimitiveData;

public class ImportEngine {

	private MasterDAO dao = null;
	private Project project = null;
	private Entity entity = null;
	
	public ImportEngine(MasterDAO dao, Project project) {
		this.dao = dao;
		this.project = project;
	}
	
	public void handleImport(String fileName, String path) {
		String name = getName(fileName);
		this.entity = new Entity(name);
		try {
			this.dao.getDao(Entity.class).insert(this.entity);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
		this.project.config("entity.current", entity.getUID());
		this.project.config("edition.mode", "object");
		this.dao.notifyObservers();
		
	}
	
	private String getName(String fileName) {
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			return fileName.substring(0, i);
		}
		return ""; 
	}
	
	private String getExtension(String fileName) {
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			return fileName.substring(i+1);
		}
		return "";
	}
	
	private void handleDae(String fileName) {
		Log.debug("Handle dae");
		DAEParser parser = new DAEParser(fileName,this.entity,this.dao);
	}
}

