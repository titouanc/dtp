/**
 * 
 */
package be.ac.ulb.infof307.g03.controllers;

import java.sql.SQLException;

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
 * @author fhennecker, pierre
 * @brief Controller of the jMonkeyEngine canvas. It handles both the 3D and 2D view.
 */
public class WorldController implements ActionListener {
    
    private WorldView _view;
    private Project _project;
    private boolean _isViewCreated = false;
    CameraModeController _cameraModeController = null;
    private Point _movingPoint = null;
    
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
    	
    	Ray ray = getRayForMousePosition();
        Vector3f pos = ray.getOrigin();
        Vector3f dir = ray.getDirection();
        
        /* Get the position of the point along the ray, given its Z coordinate */
        float t = ((float) _movingPoint.getZ() - pos.getZ())/dir.getZ();
        Vector3f onPlane = pos.add(dir.mult(t));
        _movingPoint.setX(onPlane.getX());
        _movingPoint.setY(onPlane.getY());
        
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
     * Move the currently moving point
     */
    public void dragMovingPoint(){
    	if (_movingPoint == null)
    		return;
    	
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
     * Handle click
     */
    @Override
	public void onAction(String command, boolean mouseDown, float arg2) {
    	System.out.println("[WorldController] onAction: " + command + " - " + (mouseDown ? "press" : "release"));
    	
    	if (command.equals(WorldView.CLICK) && _project.config("mouse.mode").equals("dragSelect")){
			/* We're moving a point, and mouse button up: stop the point here */
			if (_movingPoint != null && ! mouseDown){
            	System.out.println("[WorldController] Stopping point " + _movingPoint.getUID() + _movingPoint.toString());
            	dropMovingPoint();
            }
			
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
            else if (clicked instanceof Point && mouseDown){
            	System.out.println("[WorldController] Moving point " + clicked.getUID() + clicked.toString());
        		_movingPoint = (Point) clicked;
            }
		}
	}
}
