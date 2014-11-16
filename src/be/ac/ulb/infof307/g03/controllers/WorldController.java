/**
 * 
 */
package be.ac.ulb.infof307.g03.controllers;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import be.ac.ulb.infof307.g03.models.*;
import be.ac.ulb.infof307.g03.views.WorldView;

import com.jme3.collision.CollisionResults;
import com.jme3.input.controls.ActionListener;
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
public class WorldController implements ActionListener {
    
    private WorldView _view;
    private Project _project;
    private boolean _isViewCreated = false;
    CameraModeController _cameraModeController = null;
    private Point _movingPoint = null;
    private List<Point> _inConstruction ;
    
    /**
     * Constructor of WorldController.
     * It creates the controller view.
     * @param settings The jMonkey application settings
     * @param project The main project
     * @throws SQLException 
     */
    public WorldController(AppSettings settings, Project project) throws SQLException{
        _view = new WorldView(this, project.getGeometryDAO());
        _view.setSettings(settings);
        _view.createCanvas();
        _cameraModeController = new CameraModeController(project);
        _project = project;
        _inConstruction = new LinkedList <Point> () ;
    }
    
    /**
     * @return the world view.
     */
    public WorldView getView(){
        return _view;
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
     * Gets called when the view is initialized.
     */
    public void onViewCreated(){
        _isViewCreated = true;
        _cameraModeController.get2DCam().resetDirection();
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
    public void dropMovingPoint(){
    	if (_movingPoint == null)
    		return;
    	
    	getXYForMouse(_movingPoint);
        
        try {
        	GeometryDAO dao = _project.getGeometryDAO();
        	dao.update(_movingPoint);
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
		Vector2f myVector= getXYForMouse((float) p.getZ());
		p.setX(myVector.getX());
		p.setY(myVector.getY());
	}
    
    /**
     * Toggle selection for a Grouped item, save to database and notify observers
     * @param grouped The Grouped item to select
     */
    public void selectObject(Grouped grouped) {
        grouped.toggleSelect();
        try {
        	GeometryDAO dao = _project.getGeometryDAO();
            dao.update(grouped);
            dao.notifyObservers(grouped);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Add the points in the Point List when user click to create his wall
     */
    public void construct(){
    	Point lastPoint=new Point();
		lastPoint.setZ(1);
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
    	Group room = new Group();
    	GeometryDAO dao;
		try {
			dao = _project.getGeometryDAO();
			dao.create(room);
			room.setName(room.getUID());
			dao.update(room);
	    	for (int i=0; i<_inConstruction.size(); i++){
	    		_inConstruction.get(i).deselect();
	    		dao.update(_inConstruction.get(i));
				dao.addShapeToGroup(room, new Line(_inConstruction.get(i), _inConstruction.get((i+1)%_inConstruction.size())));
	    	}
	    	dao.create(new Wall(room));
	    	dao.create(new Ground(room));
	    	dao.addGroupToFloor((Floor) dao.getByUID("flr-1"), room);
	    	dao.notifyObservers();
	    	_inConstruction.clear();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		_project.config("mouse.mode","dragSelect");
    }
    
    /**
     * Handle click
     */
    @Override
	public void onAction(String command, boolean mouseDown, float arg2) {
    	String mouseMode = _project.config("mouse.mode");
		boolean leftClick = command.equals(WorldView.LEFT_CLICK);
		boolean rightClick = command.equals(WorldView.RIGHT_CLICK);
		boolean mouseUp = ! mouseDown;
		
		/* We're moving a point, and mouse button up: stop the point here */
		if (_movingPoint != null && mouseUp)
        	dropMovingPoint();
		
		/* We're building a shape, and right-click: finish shape */
    	else if (mouseDown && rightClick && _inConstruction.size() > 0)
			finalizeConstruct();
		
		/* We're in construct mode and left-click: add a point */
    	else if (mouseDown && leftClick && mouseMode.equals("construct"))
    		construct();
		
		/* Different things can happen in dragSelect mode */
		else if (leftClick && mouseMode.equals("dragSelect")){
			/* Find the Geometric object where we clicked */
            Geometric clicked = getClickedObject();
            
            /* We're not interested if no object */
            if (clicked == null)
            	return;
            
            /* If it is a Grouped (Wall, Ground): select it */
            if (clicked instanceof Grouped && mouseDown){
            	System.out.println("[WorldController] Select " + clicked.toString());
            	selectObject((Grouped) clicked);
            } 
            /* If it is a Point: initiate drag'n drop */
            else if (clicked instanceof Point && mouseDown)
        		_movingPoint = (Point) clicked;
		}  	
	}
}
