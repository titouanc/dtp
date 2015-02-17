package be.ac.ulb.infof307.g03.utils.io;

import java.io.File;

import be.ac.ulb.infof307.g03.models.Entity;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.utils.exporter.A3DSExporter;
import be.ac.ulb.infof307.g03.utils.exporter.DAEExporter;
import be.ac.ulb.infof307.g03.utils.exporter.KMZExporter;
import be.ac.ulb.infof307.g03.utils.exporter.OBJExporter;


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
	 */
	public void handleExport(Entity entity, File fileToExport) {
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
