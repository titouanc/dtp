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
	JFileChooser _chooser;
	FileChooserController _controller;
	
	/**
	 * @param controller  The view's controller
	 * 
	 */
	public FileChooserView(FileChooserController controller){
		_controller = controller;
		_chooser = new JFileChooser();
	    FileNameExtensionFilter filter = new FileNameExtensionFilter("Home Plans Project (hpj)", "hpj");
	    _chooser.setFileFilter(filter);
	}
	
	/**
	 * This method display a dialog to save a file.
	 * When the user has chosen a filename and path, it ask the controller 
	 * to process it.
	 * @param parent Parent of the dialog window.
	 */
	public void displayNew(Component parent){
	    int returnVal = _chooser.showDialog(parent, "New project");
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	_controller.newProject(_chooser.getSelectedFile());
	    }
		
	}
	
	/**
	 * This method display a dialog to open a file.
	 * When the user has chosen a file, it ask the controller 
	 * to process it.
	 * @param parent Parent of the dialog window.
	 */
	public void displayOpen(Component parent){
		int returnVal = _chooser.showOpenDialog(parent);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	_controller.openProject(_chooser.getSelectedFile());
	    }
		
	}
	
	/**
	 * This method display a dialog to save as a file.
	 * When the user has chosen a file, it ask the controller 
	 * to process it.
	 * @param parent Parent of the dialog window.
	 */
	public void displaySaveAs(Component parent){
		int returnVal = _chooser.showSaveDialog(parent);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	_controller.saveAsProject(_chooser.getSelectedFile());
	    }
		
	}
	

}
