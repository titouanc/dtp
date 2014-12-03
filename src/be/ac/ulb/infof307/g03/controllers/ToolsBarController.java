package be.ac.ulb.infof307.g03.controllers;

import be.ac.ulb.infof307.g03.utils.Log;
import be.ac.ulb.infof307.g03.views.ToolsBarView;
import be.ac.ulb.infof307.g03.models.Config;
import be.ac.ulb.infof307.g03.models.Entity;
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
	static final public String PYRAMID = "TB_Pyramid";
	static final public String CYLINDER = "TB_Cylinder";
	
	// Edition mode alias
	static final private String WORLDMODE = "world";
	static final private String OBJECTMODE = "object";
	
	private String currentObjectMode = null;
	
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
        
        this.currentObjectMode = _project.config("edition.mode");
        if (this.currentObjectMode.equals(""))
        	this.currentObjectMode = WORLDMODE;
        else
        	updateEditionMode();
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
		} catch (SQLException ex) {
			Log.exception(ex);
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
		} catch (SQLException ex) {
			Log.exception(ex);
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
		} catch (SQLException ex) {
			Log.exception(ex);
		}
    }
    
    /**
     * The private method is called when the button 2D
     * is clicked. It will communicate with the controller
     */
    public void on2d(){
    	Log.info("Switch to 2D mode");
    	_project.config("camera.mode", "2D");
  
    }
    
    /**
     * The private method is called when the button 3D
     * is clicked. It will communicate with the controller
     */
    public void on3d() {
    	Log.info("Switch to 3D mode");
    	_project.config("camera.mode", "3D");
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
		if (this.currentObjectMode!=value)  {
			this.currentObjectMode = value;
			updateEditionMode();
		}
	}
	
	private void updateEditionMode() {
		if (this.currentObjectMode.equals(WORLDMODE)) {
			_view.setWorldModeSelected();
			_view.setWorldEditionModuleVisible(true);
			_view.setObjectEditionModuleVisible(false);
		} else if (this.currentObjectMode.equals(OBJECTMODE)) {
			_view.setObjectModeSelected();
			_view.setWorldEditionModuleVisible(false);
			_view.setObjectEditionModuleVisible(true);
		}
		_project.config("mouse.mode", "dragSelect");
		_view.setDragSelectSelected(true);
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
        	String aName = JOptionPane.showInputDialog("New object name ?");
        	onObjectMode(aName);
        } else if (cmd.equals(CUBE)) {
        	onCubeCreation();
        } else if (cmd.equals(SPHERE)) {
        	onSphereCreation();
        } else if (cmd.equals(PYRAMID)) {
        	onPyramidCreation();
        } else if (cmd.equals(CYLINDER)) {
        	onCylinderCreation();
        }
     }

     private void onObjectMode(String aName) {
    	 Log.log(Level.FINEST,"[DEBUG] User clicked on : object");
    	 if (aName != null) {
    		 Entity entity = new Entity(aName);
    		 try {
    			 GeometryDAO dao = _project.getGeometryDAO();
    			 dao.create(entity);
    			 dao.notifyObservers();
    		 } catch (SQLException e) {
    			 // TODO Auto-generated catch block
    			 e.printStackTrace();
    		 }
    		 _project.config("entity.current", entity.getUID());
    		 _project.config("edition.mode", "object");
    	 } else {
    		 _view.setWorldModeSelected();
    	 }
     }

	private void onWorldMode() {
		Log.log(Level.FINEST,"[DEBUG] User clicked on : world");
		_project.config("mouse.mode", "dragSelect");
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
	
	private void onPyramidCreation() {
		Log.log(Level.FINEST,"[DEBUG] User clicked on : pyramid");
		_project.config("mouse.mode", "pyramid");
	}
	
	private void onCylinderCreation() {
		Log.log(Level.FINEST,"[DEBUG] User clicked on : cylinder");
		_project.config("mouse.mode", "cylinder");
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
