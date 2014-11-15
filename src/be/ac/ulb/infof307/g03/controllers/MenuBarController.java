/**
 * 
 */
package be.ac.ulb.infof307.g03.controllers;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.views.MenuBarView;

/**
 * @author fhennecker, julian, pierre
 * @brief Controller of the MenuBar
 */
public class MenuBarController {
	private MenuBarView _view;
	private FileChooserController _fileController;
	private Project _project;
	
	/**
	 * Constructor of MenuBarController.
	 * It creates the view associated with the controller.
	 * @param project 
	 */
	public MenuBarController(Project project){
		_project = project;
		_view = new MenuBarView(this);
		_fileController = new FileChooserController(_view);
		
	}
	
	/**
	 * @return the controller's view
	 */
	public MenuBarView getView(){
		return _view;
	}
	
	/**
	 * Handler launched when menu item "New" is clicked
	 */
	public void onNew() {
		_fileController.notifyDisplayNew();
	}
	/**
	 * Handler launched when menu item "Open" is clicked
	 */
	public void onOpen() {
		_fileController.notifyDisplayOpen();
		
	}
	/**
	 * Handler launched when menu item "Save" clicked
	 */
	public void onSave() {
		System.out.println("[DEBUG] User clicked on new");
	}
	/**
	 * Handler launched when menu item "Quit" clicked
	 */
	public void onQuit() {
		System.exit(0);
	}
	/**
	 * Handler launched when menu item "Undo" clicked
	 */
	public void onUndo() {
		System.out.println("[DEBUG] User clicked on undo");
	}
	/**
	 * Handler launched when menu item "Redo" clicked
	 */
	public void onRedo() {
		System.out.println("[DEBUG] User clicked on redo");
	}
}
