/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

import java.io.File;
import java.sql.SQLException;
import java.util.prefs.Preferences;
import be.ac.ulb.infof307.g03.models.DemoProject;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.utils.Log;

/**
 * @author pierre
 * This class will try to open the last projet.
 */
public class BootController {
	
	static final private String LAST_PROJECT = "LastOpenedProject";
	private Preferences prefs = null; 
	
	/**
	 * Constructor of the class Bootcontroller
	 */
	public BootController(){
		 this.prefs = Preferences.userRoot().node("HomePlans");
	}
	
	/**
	 * @param path Save the path of the current project to path
	 * 
	 */
	public void saveCurrentProjectPath(String path){
		this.prefs.put(LAST_PROJECT, path);
	}
	
	/**
	 * @return A string containing the path of the last project opened
	 */
	public String getLastProjectPath(){
		String path = this.prefs.get(LAST_PROJECT, null);
		return path;
	}
	
	/**
	 * @return Return the last project opened
	 * @throws SQLException
	 */
	public Project loadLastProject() throws SQLException{
		String lastProjectPath = getLastProjectPath(); 
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
	 * @return The project to be opened by the program
	 */
	public Project initProject() {
		Project proj = null;
		try {
			proj = loadLastProject();
		} catch(SQLException e){
			Log.warn("Unable to load last project %s", getLastProjectPath());
		}
		
		if (proj == null)
			try {
				proj = DemoProject.create();
			} catch(SQLException e){
				Log.error("Unable to load the demo project %s", getLastProjectPath());
			}
		return proj;
	}
}
