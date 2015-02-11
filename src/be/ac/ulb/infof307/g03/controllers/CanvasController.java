package be.ac.ulb.infof307.g03.controllers;

import java.sql.SQLException;

import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;

import be.ac.ulb.infof307.g03.models.Area;
import be.ac.ulb.infof307.g03.models.Floor;
import be.ac.ulb.infof307.g03.models.Geometric;
import be.ac.ulb.infof307.g03.models.GeometryDAO;
import be.ac.ulb.infof307.g03.models.Meshable;
import be.ac.ulb.infof307.g03.models.Point;
import be.ac.ulb.infof307.g03.models.Primitive;
import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.utils.Log;
import be.ac.ulb.infof307.g03.views.WorldView;

public abstract class CanvasController {
	protected WorldView view = null;
	protected AppSettings appSettings = null;
	protected Project project = null;
	protected CameraContext cameraContext = null;
    protected Geometric movingGeometric = null;
    protected String mouseMode;
	
	public CanvasController(WorldView view, AppSettings appSettings) {
		this.view = view;
		this.project = view.getProject();
		this.appSettings = appSettings;
        this.mouseMode = project.config("mouse.mode");
	}
	
	 /** 
     * @return The camera mode controller.
     */
    public CameraContext getCameraModeController() {
        return this.cameraContext;
    }
    
    public void setCameraContext(CameraContext cameraContext) {
    	this.cameraContext = cameraContext;
    }
    
    /**
     * @return the world view.
     */
    public WorldView getView(){
    	return this.view;
    }

    /**
     * @return the project
     */
    public Project getProject(){
    	return this.project;
    }

    /**
     * @return The view context.
     */
    public JmeContext getViewContext(){
    	return this.view.getContext();
    }


    /**
     * Start the view canvas.
     */
    public void startViewCanvas(){
    	this.view.startCanvas();
    }
    
    /**
     * Return current mouse position as a Ray object, usable for collisions in 3D scenes.
     * @return The Ray corresponding to the mouse pointer as seen by the camera
     */
    public Ray getRayForMousePosition(){
    	Vector2f cursorPosition = this.view.getInputManager().getCursorPosition();
        Vector3f camPos = this.view.getCamera().getWorldCoordinates(cursorPosition, 0f).clone();
        Vector3f camDir = this.view.getCamera().getWorldCoordinates(cursorPosition, 1f).subtractLocal(camPos);
        return new Ray(camPos, camDir);
    }
    
    /**
     * Return X and Y position when user click on the screen.
     * @param Z
     * @return Vector of coordinates
     */
	public Vector3f getXYForMouse(float Z){
    	Ray ray = getRayForMousePosition();
        Vector3f pos = ray.getOrigin();
        Vector3f dir = ray.getDirection();
        /* Get the position of the point along the ray, given its Z coordinate */
        float t = (Z - pos.getZ())/dir.getZ();
        Vector3f onPlane = pos.add(dir.mult(t));
        return new Vector3f(onPlane.getX(),onPlane.getY(), Z);
    }
    
    protected void deselectAll() {
		try {
			GeometryDAO dao = project.getGeometryDAO();
			for (Area area : dao.getSelectedMeshables()) {
				for (Point p : area.getPoints()) {
					p.deselect();
					dao.update(p);
				}
				area.deselect();
				dao.update(area);
				dao.notifyObservers(area);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
    
    /**
	 * @param clickedItem
	 * @param newTexture
	 * @throws SQLException 
	 */
	public void setTexture(Meshable clickedItem,String newTexture) throws SQLException {
		clickedItem.setTexture(newTexture);
		this.project.getGeometryDAO().update(clickedItem);
		this.project.getGeometryDAO().notifyObservers();
	}

	abstract public void mouseMoved(float value);
	abstract public void onLeftClick();
	abstract public void onLeftRelease();
	abstract public void onRightClick();
	abstract public Geometric getClickedObject();
}
