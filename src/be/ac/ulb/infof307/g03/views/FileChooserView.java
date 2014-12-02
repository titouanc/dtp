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
	JFileChooser chooser;
	FileChooserController controller;
	
	/**
	 * @param controller  The view's controller
	 * 
	 */
	public FileChooserView(FileChooserController controller){
		this.controller = controller;
		this.chooser = new JFileChooser();
	    FileNameExtensionFilter filter = new FileNameExtensionFilter("Home Plans Project (hpj)", "hpj");
	    this.chooser.setFileFilter(filter);
	}
	
	/**
	 * This method display a dialog to save a file.
	 * When the user has chosen a filename and path, it ask the controller 
	 * to process it.
	 * @param parent Parent of the dialog window.
	 */
	public void displayNew(Component parent){
	    int returnVal = this.chooser.showDialog(parent, "New project");
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	this.controller.newProject(this.chooser.getSelectedFile());
	    }
		
	}
	
	/**
	 * This method display a dialog to open a file.
	 * When the user has chosen a file, it ask the controller 
	 * to process it.
	 * @param parent Parent of the dialog window.
	 */
	public void displayOpen(Component parent){
		int returnVal = this.chooser.showOpenDialog(parent);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	this.controller.openProject(this.chooser.getSelectedFile());
	    }
		
	}
	
	/**
	 * This method display a dialog to save as a file.
	 * When the user has chosen a file, it ask the controller 
	 * to process it.
	 * @param parent Parent of the dialog window.
	 */
	public void displaySaveAs(Component parent){
		int returnVal = this.chooser.showSaveDialog(parent);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	this.controller.saveAsProject(this.chooser.getSelectedFile());
	    }
		
	}
	

}
