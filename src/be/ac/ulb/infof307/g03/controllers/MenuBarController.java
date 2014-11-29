/**
 * 
 */
package be.ac.ulb.infof307.g03.controllers;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.utils.Log;
import be.ac.ulb.infof307.g03.views.FileChooserView;
import be.ac.ulb.infof307.g03.views.GUI;
import be.ac.ulb.infof307.g03.views.MenuBarView;

/**
 * @author fhennecker, julian, pierre
 * @brief Controller of the MenuBar
 */
public class MenuBarController implements ActionListener {
	private MenuBarView _view;
	private FileChooserController _fileController;
	private GUI _gui; // to dispose when top left red cross is clicked
	private Project _project;
	
	/**
	 * New alias
	 */
	static public final String NEW  = "new" ;
	/**
	 * Open alias
	 */
	static public final String OPEN = "open";
	/**
	 * Demo alias
	 */
	static public final String DEMO = "demo";
	/**
	 * Save alias
	 */
	static public final String SAVE = "save";
	/**
	 * SaveAs alias
	 */
	static public final String SAVE_AS = "saveAs";
	/**
	 * Quit alias
	 */
	static public final String QUIT = "quit";
	/**
	 * KeyBinding alias
	 */
	static public final String KEYBINDINGS = "keybindings";
	/**
	 * Tools alias
	 */
	static public final String TOOLS = "tools";
	/**
	 * About alias
	 */
	static public final String ABOUT = "about";
	
	/**
	 * Constructor of MenuBarController.
	 * It creates the view associated with the controller.
	 * @param project The main project
	 * @param gui The main gui frame (for .dispose())
	 */
	public MenuBarController(Project project,GUI gui){
		_gui = gui;
		_project = project;
	}
	
	/**
	 * @author fhennecker
	 * Runs the MenuBar GUI
	 */
	public void run(){
		_view = new MenuBarView(this);
		_fileController = new FileChooserController(_view, _project, _gui);
		_fileController.run();
	}
	
	/**
	 * This method initiate the view
	 */
	public void initView(){
		_view = new MenuBarView(this);
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
	 * Handler launched when menu item "Demo" is clicked
	 */
	public void onDemo() {
		_fileController.openDemo();
	}
	
	/**
	 * Handler launched when menu item "Save" is clicked
	 */
	public void onSave() {
		if (_project.isOnDisk())
			JOptionPane.showMessageDialog(_view,"Project saved. (auto-save is enabled)", "Information", JOptionPane.PLAIN_MESSAGE);
		else
			onSaveAs();
	}
	
	/**
	 * Handler launched when menu item "Save" is clicked
	 */
	public void onSaveAs() {
		_fileController.notifyDisplaySaveAs();
	}
	
	/**
	 * Handler launched when menu item "Quit" is clicked
	 */
	public void onQuit() {
		_gui.dispatchEvent(new WindowEvent(_gui, WindowEvent.WINDOW_CLOSING));
	}

	/**
	 * Handler launched when menu item "Keybindings" is clicked
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

	/**
	 * Handler launched when menu item "Tools" is clicked
	 */
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
	
	/**
	 * Handler launched when menu item "About" is clicked
	 * Display about infos
	 */
	public void onAbout() {
		String aboutMessage = "HomePlans v1.0.0\n\n"
				+ "Made by F. Hennecker, T. Christophe, J. Schembri, P. Gerard, W. Moulart, B. Rocha Pereira";
		JOptionPane.showMessageDialog(_view, aboutMessage);
		
	}
	
	 /**
     * Inherited method from interface ActionListener
     * @param event A mouse click
     */ 
    @Override
	public void actionPerformed(ActionEvent event) {
		String cmd = event.getActionCommand();
		if (cmd.equals(NEW)) {
			onNew();
		} else if (cmd.equals(OPEN)) {
			onOpen();
		} else if (cmd.equals(DEMO)) {
			onDemo();
		} else if (cmd.equals(SAVE)) {
			onSave();
		} else if (cmd.equals(SAVE_AS)) {
			onSaveAs();
		} else if (cmd.equals(QUIT)) {
			onQuit();
		} else if(cmd.equals(KEYBINDINGS)) {
			onKeybindings();
		} else if(cmd.equals(TOOLS)) {
			onTools();
		}else if(cmd.equals(ABOUT)) {
			onAbout();
		}
	} 
}
