/**
 * 
 */
package be.ac.ulb.infof307.g03.controllers;

import java.sql.SQLException;

import be.ac.ulb.infof307.g03.models.*;
import be.ac.ulb.infof307.g03.views.WorldView;

import com.jme3.collision.CollisionResults;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Plane;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;

/**
 * @author fhennecker,pierre
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
    }
    
    /**
     * Convert a click position to clicked item
     * @param cursorPosition The clicked position, on the screen
     * @return The clicked Geometric item, or null if not found
     */
    public Geometric getClickedObject(Vector2f cursorPosition){
    	float mouseX = cursorPosition.getX();
        float mouseY = cursorPosition.getY();
        GeometryDAO dao = null;
        try {
            dao = _project.getGeometryDAO();
        } catch (SQLException e1) {
            return null;
        }
        
        Vector3f camPos = _view.getCamera().getWorldCoordinates(new Vector2f(mouseX, mouseY), 0f).clone();
        Vector3f camDir = _view.getCamera().getWorldCoordinates(new Vector2f(mouseX, mouseY), 1f).subtractLocal(camPos);
        Ray ray = new Ray(camPos, camDir);
        
        CollisionResults results = new CollisionResults();
        _view.getRootNode().collideWith(ray, results);
        if (results.size() > 0){
        	// Get 3D object from scene
            Geometry selected = results.getClosestCollision().getGeometry();
            // Get associated Geometric from database
            return dao.getByUID(selected.getName());
        }
        return null;
    }
    
    /**
     * Drop the currently moving point:
     * - Compute final position
     * - Update in database and notify
     * - Set current moving point to null
     * @param cursorPosition The position of the cursor when the button has been released
     */
    public void dropMovingPoint(Vector2f cursorPosition){
    	if (_movingPoint == null)
    		return;
    	
    	/* We need the projection of a ray from camera on the plane defined by the height of the moving point */
    	Plane collidePlane = new Plane(new Vector3f(0, 0, 1), (float) _movingPoint.getZ());
    	if (collidePlane.isOnPlane(_movingPoint.toVector3f())){
    		System.out.println("PLAN BIEN DEFINI");
    	} else {
    		System.out.println("PLAN MAL DEFINI");
    	}
    	
    	float mouseX = cursorPosition.getX();
        float mouseY = cursorPosition.getY();
        Vector3f camPos = _view.getCamera().getWorldCoordinates(new Vector2f(mouseX, mouseY), 0f).clone();
        Vector3f camDir = _view.getCamera().getWorldCoordinates(new Vector2f(mouseX, mouseY), 1f).subtractLocal(camPos);
        Ray ray = new Ray(camPos, camDir);
        CollisionResults results = new CollisionResults();
        ray.collideWith(collidePlane, results);
    }
    
    /**
     * Method used to select an object when the user right-clicked on the canvas
     * @param cursorPosition The position of the click on the canvas
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
		if (command.equals(WorldView.CLICK)){
			Vector2f clickPos = _view.getInputManager().getCursorPosition();
			
			/* We're moving a point, and mouse button up: stop the point here */
			if (_movingPoint != null && ! mouseDown){
            	System.out.println("[WorldController] Stopping point " + _movingPoint.toString() + " at " + clickPos.toString());
            	dropMovingPoint(clickPos);
            }
			
			/* Find the Geometric object where we clicked */
            Geometric clicked = getClickedObject(clickPos);
            
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
            	System.out.println("[WorldController] Moving point " + clicked.toString());
        		_movingPoint = (Point) clicked;
            }
		}
	}
}
