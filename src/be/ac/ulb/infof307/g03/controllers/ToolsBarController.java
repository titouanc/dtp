package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.views.ToolsBarView;

/**
 * @author fhennecker, pierre
 * @brief Controller of the ToolsBar at the top of the application.
 */
public class ToolsBarController {
	private ToolsBarView _view;
	
	
	/**
	 * Constructor of ToolsBarController.
	 * It creates the ToolsBar view
	 */
	public ToolsBarController(){
		_view = new ToolsBarView(this);
	}
	
	/**
	 * @return The controller view 
	 */
	public ToolsBarView getView(){
		return _view;
	}
	
	/**
     * The private method is called when the button undo
     * is clicked. It will communicate with the controller
     */
    public void onUndo(){
    	System.out.println("[DEBUG] User clicked on : undo");
        
    }
    
    /**
     * The private method is called when the button redo
     * is clicked. It will communicate with the controller
     */ 
    public void onRedo(){
    	System.out.println("[DEBUG] User clicked on : redo");
    	
    }
    /**
     * The private method is called when the button line
     * is clicked. It will communicate with the controller
     */
    public void onLine(){
    	System.out.println("[DEBUG] User clicked on : line");
    }
    
    /**
     * The private method is called when the button group
     * is clicked. It will communicate with the controller
     */
    public void onGroup(){
    	System.out.println("[DEBUG] User clicked on : group");
    }
    
    /**
     * The private method is called when the button floor up
     * is clicked. It will communicate with the controller
     */
    public void onFloorUp(){
    	System.out.println("[DEBUG] User clicked on : floorUp");	
    }
    
    /**
     * The private method is called when the button floor down
     * is clicked. It will communicate with the controller
     */
    public void onFloorDown(){
    	System.out.println("[DEBUG] User clicked on : floor down");
    }
    
    /**
     * The private method is called when the button 2D
     * is clicked. It will communicate with the controller
     */
    public void on2d(){
    	System.out.println("[DEBUG] User clicked on : go2D");
    }
    
    /**
     * The private method is called when the button 3D
     * is clicked. It will communicate with the controller
     */
    public void on3d(){
    	System.out.println("[DEBUG] User clicked on : go3D");
    }

}
