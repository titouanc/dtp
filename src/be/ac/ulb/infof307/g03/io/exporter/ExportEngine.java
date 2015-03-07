package be.ac.ulb.infof307.g03.io.exporter;

import java.io.File;
import java.io.IOException;

import be.ac.ulb.infof307.g03.models.Entity;
import be.ac.ulb.infof307.g03.models.Project;


/**
 * Handle Export
 * @author pierre, Walter
 *
 */
public class ExportEngine {

	Project project = null;

	/**
	 * Constructor of the class
	 * @param project The main project
	 */
	public ExportEngine(Project project) {
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
		String fileName = fileToExport.getName();
		String extension = getExtension(fileName);
		if (extension.equals("dae")) {
			DAEExporter exporter = new DAEExporter(this.project);
			exporter.export(fileToExport, entity);
		} else if (extension.equals("obj")) {
			OBJExporter exporter = new OBJExporter();
			exporter.export(fileToExport, entity);
		} else if (extension.equals("3ds")) {
			A3DSExporter exporter = new A3DSExporter();
			exporter.export(fileToExport, entity);
		} else if (extension.equals("kmz")) {
			KMZExporter exporter = new KMZExporter(this.project);
			exporter.export(fileToExport, entity);
		}
	}
}
