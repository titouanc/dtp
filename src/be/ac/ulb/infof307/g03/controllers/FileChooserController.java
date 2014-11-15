/**
 * 
 */
package be.ac.ulb.infof307.g03.controllers;

import java.awt.Component;
import java.io.File;

import be.ac.ulb.infof307.g03.views.FileChooserView;

/**
 * @author pierre
 *
 */
public class FileChooserController {
	private FileChooserView _view;
	private Component _parent;
	
	/**
	 * @param parent 
	 * 
	 */
	public FileChooserController(Component parent){
		_parent = parent;
		_view = new FileChooserView(this);
		
	}
	
	/**
	 * 
	 */
	public void notifyDisplayOpen(){
		_view.displayOpen(_parent);
		
	}
	
	/**
	 * 
	 */
	public void notifyDisplaySave(){
		_view.displaySave(_parent);
		
	}
	
	/**
	 * @param fileToOpen
	 */
	public void open(File fileToOpen){
		System.out.println("You chose to open this file: " + fileToOpen.getName());
		
	}
	
	/**
	 * @param fileToSave
	 */
	public void save(File fileToSave){
		System.out.println("You chose to save as this file: " + fileToSave.getName());
		
	}

}
