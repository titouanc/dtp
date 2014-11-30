/**
 * 
 */
package be.ac.ulb.infof307.g03.controllers;

import java.awt.Component;
import java.io.File;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import be.ac.ulb.infof307.g03.models.DemoProject;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.utils.Log;
import be.ac.ulb.infof307.g03.views.FileChooserView;
import be.ac.ulb.infof307.g03.views.GUI;

/**
 * @author pierre
 *
 */
public class FileChooserController {
	private FileChooserView _view;
	private Component _parent;
	private Project _project;
	private GUI _gui;
	
	/**
	 * @param parent The parent of the controller to be linked
	 * @param project The main project
	 * @param gui The main GUI for .dispose
	 * 
	 */
	public FileChooserController(Component parent,Project project,GUI gui){
		_parent = parent;
		_project = project;
		_gui = gui;
	}
	
	/**
	 * @author fhennecker
	 * Run the FileChooser GUI
	 */
	public void run(){
		initView();
	}
	
	/**
	 * This method initiate the view
	 */
	public void initView(){
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
	 * This method is called by the view when the user want to see the demo
	 */
	public void openDemo(){
		_gui.dispose();
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run(){
				try{
					Project prj = new Project();
					prj = DemoProject.create();
					new GUI(prj);
					
				}catch (SQLException e) {
					JOptionPane.showMessageDialog(_parent, "Unable to load demo : :" + e.toString());
				}
			}
		});	
	}
	
	/**
	 * This method is called by the view when the user has chosen a file to open
	 * @param fileToOpen The file to be opened
	 */
	public void openProject(File fileToOpen){
		Log.info("Open %s", fileToOpen.getName());
		final String filename = fileToOpen.getAbsolutePath();
		_gui.dispose();
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run(){
				try{
					Project prj = new Project();
					prj.load(filename);
					// save the path of the current project to the BootController
					BootController bc = new BootController();
					bc.saveCurrentProjectPath(filename);
					new GUI(prj);
					
				} catch (SQLException e) {
					JOptionPane.showMessageDialog(_parent, "Unable to open project named " + filename + ": " + e.toString());
				}
			}
		});	
	}
	
	/**
	 * This method is called by the view when the user has chosen a new file to create
	 * @param fileToCreate The new project file to be created
	 */
	public void newProject(File fileToCreate){
		Log.info("New project %s", fileToCreate.getName());
		final String filename = fileToCreate.getAbsolutePath() + ".hpj";
		_gui.dispose();
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run(){
				try{
					Project prj = new Project();
					prj.create(filename);
					// save the path of the current project to the BootController
					BootController bc = new BootController();
					bc.saveCurrentProjectPath(filename);
					new GUI(prj);
					
				}catch (SQLException e) {
					JOptionPane.showMessageDialog(_parent, "Unable to create a new project named " + filename + ": " + e.toString());
				}
			}
		});
		
	}

	/**
	 * This method is called by the view when the user has chosen a new file to saves
	 * @param fileToSave The File to be saved as a new file
	 */
	public void saveAsProject(File fileToSave) {
		Log.info("Save as %s", fileToSave.getName());
		String filename = fileToSave.getAbsolutePath() + ".hpj";
		try {
			_project.saveAs(filename);
			// save the path of the current project to the BootController
			BootController bc = new BootController();
			bc.saveCurrentProjectPath(filename);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(_parent, "Unable to save as " + filename + ": " + e.toString());
		}
	}
}
