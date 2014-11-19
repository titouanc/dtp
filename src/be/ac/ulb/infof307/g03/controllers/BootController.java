/**
 * 
 */
package be.ac.ulb.infof307.g03.controllers;

import java.io.File;
import java.sql.SQLException;
import java.util.prefs.Preferences;

import be.ac.ulb.infof307.g03.models.DemoProject;
import be.ac.ulb.infof307.g03.models.Project;

/**
 * @author pierre
 *
 */
public class BootController {
	static final private String LAST_PROJECT = "LastOpenedProject";
	private Preferences _prefs;
	
	/**
	 * 
	 */
	public BootController(){
		 _prefs = Preferences.userRoot().node("be.ac.ulb.infof307.g03.HomePlans");
	}
	
	/**
	 * @param path 
	 * 
	 */
	public void saveCurrentProjectPath(String path){
		_prefs.put(LAST_PROJECT, path);
	}
	
	/**
	 * @return A string containing the path of the last project opened
	 */
	public String getLastProjectPath(){
		String path = _prefs.get(LAST_PROJECT, null);
		return path;
	}
	
	/**
	 * @return Return the last project opened
	 * @throws SQLException
	 */
	public Project loadLastProject() throws SQLException{
		String lastProjectPath = getLastProjectPath(); 
		System.out.println("[DEBUG] Last project path : " + lastProjectPath);
		if (lastProjectPath != null){
			if (new File(lastProjectPath).exists()){
				Project proj = new Project();
				proj.load(lastProjectPath);
				return proj;
			}
		}
		return null;
	}

	/**
	 * @return a
	 * @throws SQLException
	 */
	public Project initProject() throws SQLException {
		Project proj = null;
		try {proj = loadLastProject();}
		catch(SQLException e){}
		
		if (proj == null)
			proj = DemoProject.create();
		
		return proj;
	}
}
