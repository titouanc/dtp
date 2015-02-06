/**
 * 
 */
package be.ac.ulb.infof307.g03.views;

import java.awt.Component;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import be.ac.ulb.infof307.g03.controllers.FileChooserController;

/**
 * @author pierre
 * This class is a view of a file dialog
 */
public class FileChooserView {
	JFileChooser chooserProject;
	JFileChooser chooserImport;
	FileChooserController controller;
	
	/**
	 * @param controller  The view's controller
	 * 
	 */
	public FileChooserView(FileChooserController controller){
		this.controller = controller;
		this.chooserProject = new JFileChooser();
	    FileNameExtensionFilter filterProject = new FileNameExtensionFilter("Home Plans Project (hpj)", "hpj");
	    this.chooserProject.setFileFilter(filterProject);
	    
	    this.chooserImport = new JFileChooser();
	    FileNameExtensionFilter filterImportObj = new FileNameExtensionFilter("Obj Object", "obj");
	    this.chooserImport.setFileFilter(filterImportObj);
	    FileNameExtensionFilter filterImportDae = new FileNameExtensionFilter("DAE Object", "dae");
	    this.chooserImport.setFileFilter(filterImportDae);
	    FileNameExtensionFilter filterImport3ds = new FileNameExtensionFilter("3DS Object", "3ds");
	    this.chooserImport.setFileFilter(filterImport3ds);
	    FileNameExtensionFilter filterImportKmz = new FileNameExtensionFilter("KMZ Object", "kmz");
	    this.chooserImport.setFileFilter(filterImportKmz);
	}
	
	/**
	 * This method display a dialog to save a file.
	 * When the user has chosen a filename and path, it ask the controller 
	 * to process it.
	 * @param parent Parent of the dialog window.
	 */
	public void displayNew(Component parent){
	    int returnVal = this.chooserProject.showDialog(parent, "New project");
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	this.controller.newProject(this.chooserProject.getSelectedFile());
	    }
		
	}
	
	/**
	 * This method display a dialog to open a file.
	 * When the user has chosen a file, it ask the controller 
	 * to process it.
	 * @param parent Parent of the dialog window.
	 */
	public void displayOpen(Component parent){
		int returnVal = this.chooserProject.showOpenDialog(parent);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	this.controller.openProject(this.chooserProject.getSelectedFile());
	    }
		
	}
	
	/**
	 * This method display a dialog to save as a file.
	 * When the user has chosen a file, it ask the controller 
	 * to process it.
	 * @param parent Parent of the dialog window.
	 */
	public void displaySaveAs(Component parent){
		int returnVal = this.chooserProject.showSaveDialog(parent);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	this.controller.saveAsProject(this.chooserProject.getSelectedFile());
	    }
		
	}

	/**
	 * Display the file dialog for import
	 * @param parent The parent window
	 */
	public void displayImport(Component parent) {
		int returnVal = this.chooserImport.showOpenDialog(parent);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	this.controller.importObject(this.chooserImport.getSelectedFile());
	    }
		
	}
	

}
