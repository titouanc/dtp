/**
 * 
 */
package be.ac.ulb.infof307.g03.controllers;


import javax.swing.JOptionPane;

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

	/**
	 * Handler launcher when menu item "Keybindings" clicked
	 */
	public void onKeybindings() {
		String keybindingsMessage = "General \nCtrl + N : Create a new project\n"
				+ "Ctrl + O : Open a new Project \n"
				+ "Ctrl + S : Save current project \n"
				+ "Ctrl + A : Save As..\n"
				+ "Ctrl + Q : Quit \n"
				+ "Ctrl + Z : Undo \n"
				+ "Ctrl + Y : Redo\n"
				+ "Ctrl + K : Show this text box \n"
				+ "Ctrl + H : Show the tools help\n"
				+ "Arrows : Move\n\n"
				+ "Mouse wheel : Zoom in/out\n"
				+ "2D Mode\n O/P : Rotate Left/Right";
		JOptionPane.showMessageDialog(_view, keybindingsMessage);
	}

	public void onTools() {
		String helpMessage = "Floors\n"
				+ "+ : Go one floor upper\n"
				+ "- : Go one floor lower\n"
				+ "new Floor : Create a new floor\n"
				+ "\n"
				+ "Dimension\n"
				+ "2D : Switch to 2D\n"
				+ "3D : Switch to 3D\n"
				+ "\n"
				+ "Cursor Tools\n"
				+ "Rotation Mode : Drag with left click to rotate\n"
				+ "Grab Mode : Drag with left click to move\n"
				+ "Simple Cursor Mode : Used to select\n"
				+ "New Room : Used to create new rooms; Left click to create corners, Right click to confirm";
		JOptionPane.showMessageDialog(_view, helpMessage);
	}
	public void onAbout() {
		String aboutMessage = "HomePlans v1.0.0\n\n"
				+ "Made by F. Hennecker, T. Christophe, J. Schembri, P. Gerard, W. Moulart, B. Rocha Pereira";
		JOptionPane.showMessageDialog(_view, aboutMessage);
		
	}
}
