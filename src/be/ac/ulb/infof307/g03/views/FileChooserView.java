/**
 * 
 */
package be.ac.ulb.infof307.g03.views;

import java.awt.Component;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import be.ac.ulb.infof307.g03.controllers.FileChooserController;
import be.ac.ulb.infof307.g03.models.Entity;

/**
 * @author pierre
 * This class is a view of a file dialog
 */
public class FileChooserView {
	JFileChooser chooserProject;
	JFileChooser chooserImport;
	JFileChooser chooserExport;
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
	    
	    FileNameExtensionFilter filterImportObj = new FileNameExtensionFilter("Wavefront file (.obj)", "obj");
	    FileNameExtensionFilter filterImportDae = new FileNameExtensionFilter("COLLADA file (.dae)", "dae");
	    FileNameExtensionFilter filterImport3ds = new FileNameExtensionFilter("Autodesk 3ds Max file (.3ds)", "3ds");
	    FileNameExtensionFilter filterImportKmz = new FileNameExtensionFilter("Keyhole Markup Language Archive (.kmz)", "kmz");
	    
	    this.chooserExport = new JFileChooser();
	    chooserExport.setAcceptAllFileFilterUsed(false);
	    this.chooserExport.setFileFilter(filterImportObj);
	    this.chooserExport.setFileFilter(filterImportDae);
	    this.chooserExport.setFileFilter(filterImport3ds);
	    this.chooserExport.setFileFilter(filterImportKmz);
	    
	    this.chooserImport = new JFileChooser();
	    this.chooserImport.setFileFilter(filterImportObj);
	    this.chooserImport.setFileFilter(filterImportDae);
	    this.chooserImport.setFileFilter(filterImport3ds);
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

	/**
	 * Display the file dialog for export
	 * @param parent The parent window
	 * @param selectedEntity The object to be exported
	 */
	public void displayExport(Component parent, Entity selectedEntity) {
		int returnVal = this.chooserExport.showSaveDialog(parent);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	this.controller.exportObject(this.chooserExport.getSelectedFile(),selectedEntity);
	    }
		
	}
	

}
