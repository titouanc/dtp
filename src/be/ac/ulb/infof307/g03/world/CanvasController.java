package be.ac.ulb.infof307.g03.world;


import java.sql.SQLException;

import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;

import be.ac.ulb.infof307.g03.camera.CameraContext;
import be.ac.ulb.infof307.g03.models.Area;
import be.ac.ulb.infof307.g03.models.Geometric;
import be.ac.ulb.infof307.g03.models.GeometricDAO;
import be.ac.ulb.infof307.g03.models.MasterDAO;
import be.ac.ulb.infof307.g03.models.Meshable;
import be.ac.ulb.infof307.g03.models.Project;

/**
 * @author pierre
 *
 */
public abstract class CanvasController {
	protected WorldView view = null;
	protected AppSettings appSettings = null;
	protected Project project = null;
	protected CameraContext cameraContext = null;
    protected Geometric movingGeometric = null;
    protected String mouseMode;
	
	/**
	 * The canvas controller constructor
	 * @param view
	 * @param appSettings
	 */
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
    
    /**
     * @param cameraContext Set a new camera mode controller
     */
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
			MasterDAO master = project.getGeometryDAO();
			for (Class className : master.areaClasses){
				GeometricDAO<? extends Area> dao = master.getDao(className);
				for (Area area : dao.queryForEq("selected", true)){
					area.deselect();
					dao.modify(area);
				}
			}
			master.notifyObservers();
		} catch (SQLException e) {
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
		GeometricDAO<? extends Meshable> dao = this.project.getGeometryDAO().getDao(clickedItem.getClass());
		dao.modify(clickedItem);
		this.project.getGeometryDAO().notifyObservers();
	}

	/**
	 * Called when the mouse move
	 * @param value the position
	 */
	abstract public void mouseMoved(float value);
	
	/**
	 * Called when user click left on the canvas
	 */
	abstract public void onLeftClick();
	
	/**
	 * Called when user release the left click on the canvas
	 */
	abstract public void onLeftRelease();
	
	/**
	 * Called when user right click on the canvas
	 */
	abstract public void onRightClick();
	
	/**
	 * @return The selectionned object
	 */
	abstract public Geometric getClickedObject();
}
