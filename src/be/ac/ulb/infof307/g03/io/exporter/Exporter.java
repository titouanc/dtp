package be.ac.ulb.infof307.g03.io.exporter;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import be.ac.ulb.infof307.g03.models.Entity;
import be.ac.ulb.infof307.g03.models.Project;

public abstract class Exporter {
	Project project = null;
	
	public Exporter(Project project) {
		this.project = project;
	}

	public abstract void export(File fileToExport, Entity entity) throws SQLException, IOException;
}
