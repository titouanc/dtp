/**
 * 
 */
package be.ac.ulb.infof307.g03.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.prefs.Preferences;

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
		 _prefs = Preferences.userRoot().node(this.getClass().getName());
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
	 * @throws IOException 
	 */
	public Project loadLastProject() throws SQLException, IOException{
		Project proj = new Project();
		String lastProjectPath = getLastProjectPath(); 
		if (lastProjectPath != null){
			proj.create(lastProjectPath);
		}else{
			// TODO demo ?
		}
		return proj;	
	}
}
