package be.ac.ulb.infof307.g03.utils.io;

import java.io.File;
import java.lang.reflect.Constructor;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import be.ac.ulb.infof307.g03.models.Entity;
import be.ac.ulb.infof307.g03.models.MasterDAO;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.utils.Log;
import be.ac.ulb.infof307.g03.utils.parser.A3DSParser;
import be.ac.ulb.infof307.g03.utils.parser.DAEParser;
import be.ac.ulb.infof307.g03.utils.parser.KmzParser;
import be.ac.ulb.infof307.g03.utils.parser.ObjParser;
import be.ac.ulb.infof307.g03.utils.parser.Parser;

/**
 * Handle Import
 * @author pierre
 *
 */
public class ImportEngine {
	private static Map<String, Class> parserMap = new HashMap();
	private MasterDAO dao = null;
	private Project project = null;
	private Entity entity = null;
	
	/**
	 * Constructor of the class
	 * @param project The main project
	 */
	public ImportEngine(Project project) {
		if (parserMap.isEmpty()){
			parserMap.put("3ds", A3DSParser.class);
			parserMap.put("obj", ObjParser.class);
			parserMap.put("dae", DAEParser.class);
			parserMap.put("kmz", KmzParser.class);
		}
		try {
			this.dao = project.getGeometryDAO();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.project = project;
	}
	
	/**
	 * The method that call the correct sub-method depending on the format
	 * @param fileToImport The file containing the object
	 */
	public void handleImport(File fileToImport) {
		String extension = getExtension(fileToImport.getName());
		if (parserMap.containsKey(extension)){
			this.parse(fileToImport.getAbsolutePath(), parserMap.get(extension));
			this.dao.notifyObservers();
		} else {
			Log.error("Unknown extension %s", extension);
		}
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
	
	private void parse(String filename, Class<? extends Parser> parseClass) {
		try {
			Constructor<? extends Parser> constr = parseClass.getConstructor(String.class, MasterDAO.class);
			Parser parser = constr.newInstance(filename, this.dao);
			parser.parse();
		} catch (Exception e) {
			Log.error("Unable to parse %s", filename);
			e.printStackTrace();
		}
	}
}

