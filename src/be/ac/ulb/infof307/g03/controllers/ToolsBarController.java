package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.utils.Log;
import be.ac.ulb.infof307.g03.views.FileChooserView;
import be.ac.ulb.infof307.g03.views.ToolsBarView;
import be.ac.ulb.infof307.g03.models.Floor;
import be.ac.ulb.infof307.g03.models.GeometryDAO;
import be.ac.ulb.infof307.g03.models.Project;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JOptionPane;

/**
 * @author fhennecker, pierre, wmoulart
 * @brief Controller of the ToolsBar at the top of the application.
 */
public class ToolsBarController implements ActionListener {
	// Attributes
	private ToolsBarView _view;
	private Project _project;
	
	// Buttons actions alias
	static final public String NEWELEMENT	= "NewElement";

	static final public String FLOOR_UP   = "FloorUp";
	static final public String FLOOR_DOWN = "FloorDown";
	static final public String FLOOR_NEW  = "FloorNew";

	static final public String VIEW2D 		= "2D";
	static final public String VIEW3D 		= "3D";

	static final public String ROTATE  	= "Rotate";	
	static final public String HAND 		= "Grab";  
	static final public String CURSOR		= "Cursor";
	
	/**
	 * Constructor of ToolsBarController.
	 * It creates the ToolsBar view
	 * @param aProject The main project
	 */
	public ToolsBarController(Project aProject){	
		_project = aProject;     
	}
	
	/**
	 * @author fhennecker
	 * Run the ToolsBar GUI
	 */
	public void run(){
		initView(_project);
		_project.addObserver(_view);
		//Sets the default mode
        this.onDragSelectMode();
	}
	
	/**
	 * This method initiate the view
	 * @param aProject The main project
	 */
	public void initView(Project aProject){
		_view = new ToolsBarView(this,aProject);
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
    	Log.log(Level.INFO, "User clicked on : undo");
        
    }
    
    /**
     * The private method is called when the button redo
     * is clicked. It will communicate with the controller
     */ 
    public void onRedo(){
    	Log.log(Level.INFO, "User clicked on : redo");
    	
    }
    /**
     * The private method is called when the button line
     * is clicked. It will communicate with the controller
     */
    public void onLine(){
    	Log.log(Level.INFO, "User clicked on : line");
    }
    
    /**
     * The private method is called when the button group
     * is clicked. It will communicate with the controller
     */
    public void onGroup(){
    	Log.log(Level.INFO, "User clicked on : group");
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
			if (floors.size()>0) 
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
    	Log.log(Level.INFO, "User clicked on : go2D");
    	_project.config("world.mode", CameraModeController._VIEW2D);
  
    }
    
    /**
     * The private method is called when the button 3D
     * is clicked. It will communicate with the controller
     */
    public void on3d() {
    	Log.log(Level.INFO, "User clicked on : go3D");
    	_project.config("world.mode", CameraModeController._VIEW3D);
    }
    
    /**
     * The private method is called when the rotation button 
     * is clicked. It will communicate with the controller
     */ 
    public void onDragRotateMode(){
    	Log.log(Level.INFO, "User clicked on : rotate");
    	_project.config("mouse.mode", "dragRotate");
    }

    /**
     * The private method is called when the cursor button 
     * is clicked. It will communicate with the controller
     */ 
    public void onDragSelectMode(){
    	Log.log(Level.INFO, "User clicked on : cursor");
    	_project.config("mouse.mode", "dragSelect");
    }
    
    /**
     * The private method is called when the hand button 
     * is clicked. It will communicate with the controller
     */ 
    public void onDragMoveMode(){
    	Log.log(Level.INFO, "User clicked on : hand");
    	_project.config("mouse.mode", "dragMove");
    }
    
    /**
     * The private method is called when the hand button 
     * is clicked. It will communicate with the controller
     */ 
    public void onConstruction(){
    	Log.log(Level.INFO, "User clicked on : new Element");
    	_project.config("mouse.mode", "construct");
    }
    
    /**
     * Inherited method from ActionListener abstract class
     * @param action A mouse click
     */ @Override
	public void actionPerformed(ActionEvent action) {
		String cmd = action.getActionCommand();
		if (cmd.equals(NEWELEMENT)) {
        	onConstruction();
        } else if (cmd.equals(FLOOR_DOWN)) {
        	onFloorDown() ;
        } else if (cmd.equals(FLOOR_UP)) {
        	onFloorUp();
        } else if (cmd.equals(FLOOR_NEW)){
        	onFloorNew();
        	onFloorUp();
        } else if (cmd.equals(VIEW2D)) {
        	on2d();
        } else if (cmd.equals(VIEW3D)) {
        	on3d();
        } else if (cmd.equals(ROTATE)){
        	onDragRotateMode();
        } else if (cmd.equals(HAND)){
        	onDragMoveMode();
        } else if (cmd.equals(CURSOR)){
        	onDragSelectMode();
        }

	}

}
