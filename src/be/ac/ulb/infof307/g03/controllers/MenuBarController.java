/**
 * 
 */
package be.ac.ulb.infof307.g03.controllers;


import javax.swing.JOptionPane;

import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.views.GUI;
import be.ac.ulb.infof307.g03.views.MenuBarView;

/**
 * @author fhennecker, julian, pierre
 * @brief Controller of the MenuBar
 */
public class MenuBarController {
	private MenuBarView _view;
	private FileChooserController _fileController;
	private Project _project;
	private GUI _gui;
	
	/**
	 * Constructor of MenuBarController.
	 * It creates the view associated with the controller.
	 * @param project 
	 */
	public MenuBarController(Project project,GUI gui){
		_project = project;
		_gui = gui;
		_view = new MenuBarView(this);
		_fileController = new FileChooserController(_view, project);
		
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
		// TODO define if this is a violation of MVC ?
		JOptionPane.showMessageDialog(_view,"Project saved. (auto-save is enabled)", "Information", JOptionPane.PLAIN_MESSAGE);
	}
	
	/**
	 * Handler launched when menu item "Save" clicked
	 */
	public void onSaveAs() {
		_fileController.notifyDisplaySaveAs();
	}
	
	/**
	 * Handler launched when menu item "Quit" clicked
	 */
	public void onQuit() {
		//System.exit(0);
		_gui.dispose();
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
