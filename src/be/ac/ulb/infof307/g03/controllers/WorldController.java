/**
 * 
 */
package be.ac.ulb.infof307.g03.controllers;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import be.ac.ulb.infof307.g03.models.*;
import be.ac.ulb.infof307.g03.views.WorldView;

import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;

/**
 * @author fhennecker,pierre,wmoulart
 * @brief Controller of the jMonkeyEngine canvas. It handles both the 3D and 2D view.
 */
public class WorldController implements ActionListener, AnalogListener, Observer {
    
	// Attributes
    private WorldView _view;
    private Project _project;
    private CameraModeController _cameraModeController = null;
    private Point _movingPoint = null;
    private List<Point> _inConstruction ;
    private Floor _currentFloor = null;
    
    // Input alias
    static private final String _RIGHTCLICK 	= "WC_SelectObject";
	static private final String _LEFTCLICK 		= "WC_Select";
	static private final String _LEFT 			= "WC_Left";
	static private final String _RIGHT			= "WC_Right";
	static private final String _UP				= "WC_Up";
	static private final String _DOWN			= "WC_Down";
    
    /**
     * Constructor of WorldController.
     * It creates the controller view.
     * @param settings The jMonkey application settings
     * @param project The main project
     * @throws SQLException 
     */
    public WorldController(AppSettings settings, Project project) throws SQLException{
        _view = new WorldView(this, project);
        _view.setSettings(settings);
        _view.createCanvas();
        _cameraModeController = new CameraModeController(project);
        _project = project;
        _inConstruction = new LinkedList <Point>();
        _currentFloor = (Floor) project.getGeometryDAO().getByUID(project.config("floor.current"));
        if (_currentFloor == null)
        	_currentFloor = project.getGeometryDAO().getFloors().get(0);
        project.addObserver(this);
    }

	/**
     * @return the world view.
     */
    public WorldView getView(){
        return _view;
    }
    
    public Floor getCurrentFloor(){
    	return _currentFloor;
    }
    
    /**
     * @return the project
     */
    public Project getProject(){
    	return _project;
    }
    
    /**
     * @return The view context.
     */
    public JmeContext getViewContext(){
        return _view.getContext();
    }
    
    /** 
     * @return The camera mode controller.
     */
    public CameraModeController getCameraModeController() {
        return _cameraModeController;
    }
    
    /**
     * Start the view canvas.
     */
    public void startViewCanvas(){
        _view.startCanvas();
    }
    
    /**
     * Return current mouse position as a Ray object, usable for collisions in 3D scenes.
     * @return The Ray corresponding to the mouse pointer as seen by the camera
     */
    public Ray getRayForMousePosition(){
    	Vector2f cursorPosition = _view.getInputManager().getCursorPosition();
        Vector3f camPos = _view.getCamera().getWorldCoordinates(cursorPosition, 0f).clone();
        Vector3f camDir = _view.getCamera().getWorldCoordinates(cursorPosition, 1f).subtractLocal(camPos);
        return new Ray(camPos, camDir);
    }
    
    /**
     * Convert a click position to clicked item
     * @return The clicked Geometric item, or null if not found
     */
    public Geometric getClickedObject(){
        CollisionResults results = new CollisionResults();
        _view.getRootNode().collideWith(getRayForMousePosition(), results);
        
        if (results.size() > 0){
        	// Get 3D object from scene
            Geometry selected = results.getClosestCollision().getGeometry();         
            GeometryDAO dao = null;
            try {
                dao = _project.getGeometryDAO();
                // Get associated Geometric from database
                return dao.getByUID(selected.getName());
            } catch (SQLException e1) {
            	e1.printStackTrace();
            }
        }
        return null;
    }
    
    /**
     * Drop the currently moving point:
     * - Compute final position
     * - Update in database and notify
     * - Set current moving point to null
     */
    public void dropMovingPoint(boolean finalDrop){
    	if (_movingPoint == null)
    		return;
    	
    	getXYForMouse(_movingPoint);
        
        try {
        	GeometryDAO dao = _project.getGeometryDAO();
        	dao.update(_movingPoint);
        	if (finalDrop) 
        		_movingPoint = null;
        	dao.notifyObservers();
        } catch (SQLException err){
        	err.printStackTrace();
        }
    }
    
    /**
     * Return X and Y position when user click on the screen.
     * @param Z
     * @return Vector of coordinates
     */
	public Vector2f getXYForMouse(float Z){
    	Ray ray = getRayForMousePosition();
        Vector3f pos = ray.getOrigin();
        Vector3f dir = ray.getDirection();
        /* Get the position of the point along the ray, given its Z coordinate */
        float t = (Z - pos.getZ())/dir.getZ();
        Vector3f onPlane = pos.add(dir.mult(t));
        return new Vector2f(onPlane.getX(),onPlane.getY());
    }
	
	/**
	 * @param p Get coordinates X and Y into Point
	 */
	public void getXYForMouse(Point p){
		Vector2f myVector = getXYForMouse((float) _currentFloor.getBaseHeight());
		p.setX(myVector.getX());
		p.setY(myVector.getY());
	}
    
    /**
     * Toggle selection for a Meshable item, save to database and notify observers
     * @param meshable The Meshable item to select
     */
    public void selectObject(Meshable meshable) {
	        try {
	        	meshable.toggleSelect();
	        	GeometryDAO dao = _project.getGeometryDAO();
	        	for (Point p : meshable.getPoints()){
	        		if (meshable.isSelected())
	        			p.select();
	        		else
	        			p.deselect();
	        		dao.update(p);
	        	}
	            dao.update(meshable);
	            dao.notifyObservers(meshable);
	            String floorUID = meshable.getRoom().getFloor().getUID();
	            if (! _project.config("floor.current").equals(floorUID))
	            	_project.config("floor.current", floorUID);
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

    /**
     * Add the points in the Point List when user click to create his wall
     */
    public void construct(){
    	Point lastPoint=new Point();
		lastPoint.setZ(0);
		getXYForMouse(lastPoint);
		lastPoint.select();
		
		try {
        	GeometryDAO dao = _project.getGeometryDAO();
        	Point found = dao.findClosePoint(lastPoint, 1);
        	if (found != null){
        		lastPoint = found;
        		lastPoint.select();
        		dao.update(found);
        	} else {
        		dao.create(lastPoint);
        	}
        	dao.notifyObservers();
        } catch (SQLException err){
        	err.printStackTrace();
        }
		_inConstruction.add(lastPoint);
    }
    
    /**
     * Mesh the points together for the walls creation
     */
    public void finalizeConstruct(){
    	if (_inConstruction.size() >= 3){
			try {
				GeometryDAO dao = _project.getGeometryDAO();
				dao.createRoom(_currentFloor, _inConstruction);
		    	dao.notifyObservers();
			} catch (SQLException e) {
				e.printStackTrace();
			}
    	} else {
    		
    	}
		_inConstruction.clear();
    }
    
    private void mouseMoved(float value) {
    	if (_movingPoint != null) {
    		dropMovingPoint(false);
    	}
    }
    
	public void inputSetUp(InputManager inputManager){
		// Mouse event mapping
		inputManager.addMapping(_RIGHTCLICK, 	new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
		inputManager.addMapping(_LEFTCLICK,  	new MouseButtonTrigger(MouseInput.BUTTON_LEFT ));

		inputManager.addMapping(_UP, 			new MouseAxisTrigger(1, false));
		inputManager.addMapping(_DOWN, 			new MouseAxisTrigger(1, true));
		inputManager.addMapping(_LEFT,			new MouseAxisTrigger(0, true));
		inputManager.addMapping(_RIGHT,			new MouseAxisTrigger(0, false));
		
		inputManager.addListener(this, 
										_RIGHTCLICK, 
										_LEFTCLICK,
										
										_UP,
										_DOWN,
										_LEFT,
										_RIGHT
								);
	}
    
    private void dragSelectHandler() {
    	/* Find the Geometric object where we clicked */
        Geometric clicked = getClickedObject();
        
        /* We're not interested if no object */
        if (clicked == null  )
        	return;
        
        /* If it is a Meshable (Wall, Ground): select it */
        if (clicked instanceof Meshable)
        	selectObject((Meshable) clicked);
        
        /* If it is a Point: initiate drag'n drop */
        else if (clicked instanceof Point)
    		_movingPoint = (Point) clicked;
    }
    
    /**
     * Handle click
     */
    @Override
	public void onAction(String name, boolean value, float tpf) {
    	// TODO check if it's not better to keep this in an attribute updated when it's modified with observer/observable
    	String mouseMode = _project.config("mouse.mode");
		
		if (name.equals(_LEFTCLICK)) {
			if (value) { // on click
				if (mouseMode.equals("construct")) { /* We're in construct mode and left-click: add a point */
					construct();
				} else if (mouseMode.equals("dragSelect")) {
					dragSelectHandler();
				}
			} else { // on release
				if (_movingPoint != null) { // We're moving a point, and mouse button up: stop the point here
					dropMovingPoint(true);
				}
			}
		} else if (name.equals(_RIGHTCLICK)) {
			if (value) { // on click
				if (_inConstruction.size() > 0) { // We're building a shape, and right-click: finish shape
					finalizeConstruct(); 
				}
			} else { // on release
				
			}
		}
		
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		Config changed = (Config) arg1;
		if (changed.getName().equals("floor.current")){
			String newUID = changed.getValue();
			if (newUID.equals(_currentFloor.getUID()))
				return;
	        try {
	        	_currentFloor = (Floor) _project.getGeometryDAO().getByUID(changed.getValue());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onAnalog(String name, float value, float tpf) {
		if (name.equals(_UP)) {
			mouseMoved(value);
		} else if (name.equals(_DOWN)) {
			mouseMoved(value);
		} else if (name.equals(_LEFT)) {
			mouseMoved(value);
		} else if (name.equals(_RIGHT)) {
			mouseMoved(value);
		}
	}
}
