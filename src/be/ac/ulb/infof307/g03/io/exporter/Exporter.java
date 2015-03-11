package be.ac.ulb.infof307.g03.io.exporter;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import be.ac.ulb.infof307.g03.models.Entity;
import be.ac.ulb.infof307.g03.models.Project;

/**
 * @author pierre
 *
 */
public abstract class Exporter {
	Project project = null;
	
	/**
	 * Constructor of exporter
	 * @param project The main project
	 */
	public Exporter(Project project) {
		this.project = project;
	}

	/**
	 * Export
	 * @param fileToExport The file in which the object is save
	 * @param entity The entity to export
	 * @throws SQLException
	 * @throws IOException
	 */
	public abstract void export(File fileToExport, Entity entity) throws SQLException, IOException;
}
