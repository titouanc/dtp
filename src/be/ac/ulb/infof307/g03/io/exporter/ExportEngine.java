package be.ac.ulb.infof307.g03.io.exporter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import be.ac.ulb.infof307.g03.io.importer.A3DSParser;
import be.ac.ulb.infof307.g03.io.importer.DAEParser;
import be.ac.ulb.infof307.g03.io.importer.KmzParser;
import be.ac.ulb.infof307.g03.io.importer.ObjParser;
import be.ac.ulb.infof307.g03.io.importer.Parser;
import be.ac.ulb.infof307.g03.models.Entity;
import be.ac.ulb.infof307.g03.models.MasterDAO;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.utils.Log;


/**
 * Handle Export
 * @author pierre, Walter
 *
 */
public class ExportEngine {

	private static Map<String, Class> exporterMap = new HashMap();
	Project project = null;

	/**
	 * Constructor of the class
	 * @param project The main project
	 */
	public ExportEngine(Project project) {
		if (exporterMap.isEmpty()){
			exporterMap.put("3ds", A3DSExporter.class);
			exporterMap.put("obj", OBJExporter.class);
			exporterMap.put("dae", DAEExporter.class);
			exporterMap.put("kmz", KMZExporter.class);
		}
		this.project = project;
	}

	private String getExtension(String fileName) {
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			return fileName.substring(i+1);
		}
		return "";
	}

	/**
	 * The method that call the correct sub-method depending on the format
	 * @param entity The entity to export
	 * @param fileToExport The file in which the entity will be saved
	 * @throws IOException 
	 */
	public void handleExport(Entity entity, File fileToExport) throws IOException {
		String extension = getExtension(fileToExport.getName());
		if (exporterMap.containsKey(extension)){
			this.export(entity, fileToExport.getAbsolutePath(), exporterMap.get(extension));
		} else {
			Log.error("Unknown extension %s", extension);
			JOptionPane.showMessageDialog(null,"File extension " + extension + " is not supported","Export error",JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void export(Entity entity, String fileName, Class<? extends Exporter> exporterClass) {
		try {
			Constructor<? extends Exporter> constr = exporterClass.getConstructor(Project.class);
			Exporter exporter = constr.newInstance(this.project);
			exporter.export(new File(fileName), entity);
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("Unable to export %s", fileName);
			JOptionPane.showMessageDialog(null,"Could not export "+ fileName,"Export error",JOptionPane.ERROR_MESSAGE);
		}
	}
}
