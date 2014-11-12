package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.views.ToolsBarView;
import be.ac.ulb.infof307.g03.models.Project;
import java.sql.SQLException;
import java.util.Observable;
import java.util.Observer;

/**
 * @author fhennecker, pierre, wmoulart
 * @brief Controller of the ToolsBar at the top of the application.
 */
public class ToolsBarController implements Observer {
	private ToolsBarView _view;
	private Project _project;
	
	/**
	 * Constructor of ToolsBarController.
	 * It creates the ToolsBar view
	 * @param aProject A project object
	 */
	public ToolsBarController(Project aProject){
		_view = new ToolsBarView(this);
		_project = aProject;
		_project.addObserver(this);
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
    	try {
    		_project.config("world.mode", CameraModeController.VIEW2D);
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
  
    }
    
    /**
     * The private method is called when the button 3D
     * is clicked. It will communicate with the controller
     */
    public void on3d() {
    	System.out.println("[DEBUG] User clicked on : go3D");
    	try {
    		_project.config("world.mode", CameraModeController.VIEW3D);
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    }
    
    /**
     * The private method is called when the rotation button 
     * is clicked. It will communicate with the controller
     */ 
    public void onDragRotateMode(){
    	System.out.println("[DEBUG] User clicked on : rotate");
    	try {
			_project.config("mouse.mode", "dragRotate");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * The private method is called when the cursor button 
     * is clicked. It will communicate with the controller
     */ 
    public void onDragSelectMode(){
    	System.out.println("[DEBUG] User clicked on : cursor");
    	try {
			_project.config("mouse.mode", "dragSelect");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * The private method is called when the hand button 
     * is clicked. It will communicate with the controller
     */ 
    public void onDragMoveMode(){
    	System.out.println("[DEBUG] User clicked on : hand");
    	try {
			_project.config("mouse.mode", "dragMove");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
	@Override
	public void update(Observable o, Object arg) {
		
	}

}
