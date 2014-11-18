package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.views.ToolsBarView;
import be.ac.ulb.infof307.g03.models.Config;
import be.ac.ulb.infof307.g03.models.Floor;
import be.ac.ulb.infof307.g03.models.GeometryDAO;
import be.ac.ulb.infof307.g03.models.Project;

import java.sql.SQLException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JOptionPane;

/**
 * @author fhennecker, pierre, wmoulart
 * @brief Controller of the ToolsBar at the top of the application.
 */
public class ToolsBarController {
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
		_project.addObserver(_view);
        //Sets the default mode
        this.onDragSelectMode();
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
    	String currentFloorUID = _project.config("floor.current");
		try {
			GeometryDAO dao = _project.getGeometryDAO();
			Floor floor = (Floor) dao.getByUID(currentFloorUID);
	    	Floor nextFloor = dao.getNextFloor(floor);
	    	if (nextFloor != null)
	    		_project.config("floor.current", nextFloor.getUID());
	    	else
	    		JOptionPane.showMessageDialog(_view, "No floor above");
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * The private method is called when the button floor down
     * is clicked. It will communicate with the controller
     */
    public void onFloorDown(){
    	String currentFloorUID = _project.config("floor.current");
		try {
			GeometryDAO dao = _project.getGeometryDAO();
			Floor floor = (Floor) dao.getByUID(currentFloorUID);
	    	Floor prevFloor = dao.getPreviousFloor(floor);
	    	if (prevFloor != null)
	    		_project.config("floor.current", prevFloor.getUID());
	    	else
	    		JOptionPane.showMessageDialog(_view, "No floor below");
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Callback for "new floor" button
     */
    public void onFloorNew(){
		try {
			GeometryDAO dao = _project.getGeometryDAO();
			List<Floor>floors = dao.getFloors();
			Floor newFloor = new Floor(7);
	    	newFloor.setPrevious(floors.get(floors.size() - 1));
	    	dao.create(newFloor);
	    	dao.notifyObservers();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * The private method is called when the button 2D
     * is clicked. It will communicate with the controller
     */
    public void on2d(){
    	System.out.println("[DEBUG] User clicked on : go2D");
    	_project.config("world.mode", CameraModeController._VIEW2D);
  
    }
    
    /**
     * The private method is called when the button 3D
     * is clicked. It will communicate with the controller
     */
    public void on3d() {
    	System.out.println("[DEBUG] User clicked on : go3D");
    	_project.config("world.mode", CameraModeController._VIEW3D);
    }
    
    /**
     * The private method is called when the rotation button 
     * is clicked. It will communicate with the controller
     */ 
    public void onDragRotateMode(){
    	System.out.println("[DEBUG] User clicked on : rotate");
    	_project.config("mouse.mode", "dragRotate");
    }

    /**
     * The private method is called when the cursor button 
     * is clicked. It will communicate with the controller
     */ 
    public void onDragSelectMode(){
    	System.out.println("[DEBUG] User clicked on : cursor");
    	_project.config("mouse.mode", "dragSelect");
    }
    
    /**
     * The private method is called when the hand button 
     * is clicked. It will communicate with the controller
     */ 
    public void onDragMoveMode(){
    	System.out.println("[DEBUG] User clicked on : hand");
    	_project.config("mouse.mode", "dragMove");
    }
    
    /**
     * The private method is called when the hand button 
     * is clicked. It will communicate with the controller
     */ 
    public void onConstruction(){
    	System.out.println("[DEBUG] User clicked on : new Element");
    	_project.config("mouse.mode", "construct");
    }
    

}
