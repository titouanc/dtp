/**
 * 
 */
package be.ac.ulb.infof307.g03.controllers;

import java.awt.Component;
import java.io.File;

import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.views.FileChooserView;

/**
 * @author pierre
 *
 */
public class FileChooserController {
	private FileChooserView _view;
	private Component _parent;
	private Project _project;
	
	/**
	 * @param parent The parent of the controller to be linked
	 * @param project The main project
	 * 
	 */
	public FileChooserController(Component parent,Project project){
		_parent = parent;
		_project = project;
		_view = new FileChooserView(this);
		
	}
	
	/**
	 * Notify the view to view to display the open window
	 */
	public void notifyDisplayOpen(){
		_view.displayOpen(_parent);
		
	}
	
	/**
	 * Notify the view to view to display the new project window
	 */
	public void notifyDisplayNew(){
		_view.displayNew(_parent);
		
	}
	
	/**
	 * Notify the view to view to display the save as window
	 */
	public void notifyDisplaySaveAs() {
		_view.displaySaveAs(_parent);
		
	}
	
	/**
	 * This method is called by the view when the user has chosen a file to open
	 * @param fileToOpen The file to be opened
	 */
	public void openProject(File fileToOpen){
		System.out.println("You chose to open this file: " + fileToOpen.getName());
		
	}
	
	/**
	 * This method is called by the view when the user has chosen a new file to create
	 * @param fileToCreate The new project file to be created
	 */
	public void newProject(File fileToCreate){
		System.out.println("You chose to create a new project named: " + fileToCreate.getName());
		
	}

	/**
	 * This method is called by the view when the user has chosen a new file to saves
	 * @param fileToSave The File to be saved as a new file
	 */
	public void saveAsProject(File fileToSave) {
		System.out.println("You chose to save as this file: " + fileToSave.getName());
		
	}



}
