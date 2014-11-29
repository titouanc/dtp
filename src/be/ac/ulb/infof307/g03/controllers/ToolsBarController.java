package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.utils.Log;
import be.ac.ulb.infof307.g03.views.ToolsBarView;
import be.ac.ulb.infof307.g03.models.Config;
import be.ac.ulb.infof307.g03.models.Floor;
import be.ac.ulb.infof307.g03.models.GeometryDAO;
import be.ac.ulb.infof307.g03.models.Project;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;

import javax.swing.JOptionPane;

/**
 * @author fhennecker, pierre, wmoulart
 * @brief Controller of the ToolsBar at the top of the application.
 */
public class ToolsBarController implements ActionListener, Observer {
	// Attributes
	private ToolsBarView _view;
	private Project _project;
	
	// Buttons actions alias
	static final public String NEWELEMENT = "TB_NewElement";

	static final public String FLOOR_UP   = "TB_FloorUp";
	static final public String FLOOR_DOWN = "TB_FloorDown";
	static final public String FLOOR_NEW  = "TB_FloorNew";

	static final public String VIEW2D = "TB_2D";
	static final public String VIEW3D = "TB_3D";

	static final public String ROTATE = "TB_Rotate";	
	static final public String HAND = "TB_Grab";  
	static final public String CURSOR = "TB_Cursor";
	
	static final public String WORLD = "TB_World";
	static final public String OBJECT = "TB_Object";

	static final public String CUBE = "TB_Cube";
	static final public String SPHERE = "TB_Sphere";
	
	// Edition mode alias
	static final private String _WORLDMODE = "world";
	static final private String _OBJECTMODE = "object";
	
	private String _currentObjectMode = null;
	
	/**
	 * Constructor of ToolsBarController.
	 * It creates the ToolsBar view
	 * @param aProject The main project
	 */
	public ToolsBarController(Project aProject){	
		_project = aProject;  
		aProject.addObserver(this);
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
			dao.createFloorOnTop(7);
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
    	Log.info("Switch to 2D mode");
    	_project.config("world.mode", CameraModeController._VIEW2D);
  
    }
    
    /**
     * The private method is called when the button 3D
     * is clicked. It will communicate with the controller
     */
    public void on3d() {
    	Log.info("Switch to 3D mode");
    	_project.config("world.mode", CameraModeController._VIEW3D);
    }
    
    /**
     * The private method is called when the rotation button 
     * is clicked. It will communicate with the controller
     */ 
    public void onDragRotateMode(){
    	Log.info("Switch to rotate mode");
    	_project.config("mouse.mode", "dragRotate");
    }

    /**
     * The private method is called when the cursor button 
     * is clicked. It will communicate with the controller
     */ 
    public void onDragSelectMode(){
    	Log.info("Switch to drag-select mode");
    	_project.config("mouse.mode", "dragSelect");
    }
    
    /**
     * The private method is called when the hand button 
     * is clicked. It will communicate with the controller
     */ 
    public void onDragMoveMode(){
    	Log.info("Switch to drag-move mode");
    	_project.config("mouse.mode", "dragMove");
    }
    
    /**
     * The private method is called when the hand button 
     * is clicked. It will communicate with the controller
     */ 
    public void onConstruction(){
    	Log.info("Switch to construction mode");
    	_project.config("mouse.mode", "construct");
    }
    
	private void updateEditionMode(String value) {
		if (_currentObjectMode!=value)  {
			if (value.equals(_WORLDMODE)) {
				_view.setWorldModeSelected();
				_view.setWorldEditionModuleVisible(true);
				_view.setObjectEditionModuleVisible(false);
			} else if (value.equals(_OBJECTMODE)) {
				_view.setObjectModeSelected();
				_view.setWorldEditionModuleVisible(false);
				_view.setObjectEditionModuleVisible(true);
			}
			_currentObjectMode = value;
		}
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
        } else if (cmd.equals(WORLD)) {
        	onWorldMode();
        } else if (cmd.equals(OBJECT)) {
        	onObjectMode();
        } else if (cmd.equals(CUBE)) {
        	onCubeCreation();
        } else if (cmd.equals(SPHERE)) {
        	onSphereCreation();
        }

	}

	private void onObjectMode() {
		Log.log(Level.FINEST,"[DEBUG] User clicked on : object");
		_project.config("edition.mode","object");
	}

	private void onWorldMode() {
		Log.log(Level.FINEST,"[DEBUG] User clicked on : world");
		_project.config("edition.mode", "world");	
	}
	
	private void onCubeCreation() {
		Log.log(Level.FINEST,"[DEBUG] User clicked on : cube");
		_project.config("mouse.mode", "cube");	
	}
	
	private void onSphereCreation() {
		Log.log(Level.FINEST,"[DEBUG] User clicked on : sphere");
		_project.config("mouse.mode", "sphere");	
	}

	@Override
	public void update(Observable obs, Object obj) {
		if (obs instanceof Project) {
			Config config = (Config) obj;
			if (config.getName().equals("edition.mode")) {
				updateEditionMode(config.getValue());
			}
		}
		
	}

}
