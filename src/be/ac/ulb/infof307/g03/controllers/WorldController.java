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
import be.ac.ulb.infof307.g03.utils.Log;
import be.ac.ulb.infof307.g03.views.WorldView;

import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;

/**
 * @author fhennecker,pierre,wmoulart
 * @brief Controller of the jMonkeyEngine canvas. It handles both the 3D and 2D view.
 */
public class WorldController implements ActionListener, AnalogListener, Observer {
    
	// Attributes
    private WorldView view;
    private Project project;
    private CameraContext cameraContext = null;
    private Point movingPoint = null;
    private List<Point> inConstruction ;
    private Floor currentFloor = null;
	private String currentEditionMode;
	private String mouseMode;
	
	private Vector2f savedCenter = null;
	private boolean leftClickPressed = false;
	private Geometry builtGeometric = null;
	private Entity currentEntity = null;
    
    // Edition mode alias
	static final private String WORLDMODE = "world";
	static final private String OBJECTMODE = "object";

    private double currentHeight;
    private AppSettings appSettings;
    
    // Input alias
    static private final String RIGHTCLICK 	= "WC_SelectObject";
	static private final String LEFTCLICK 		= "WC_Select";
	static private final String LEFT 			= "WC_Left";
	static private final String RIGHT			= "WC_Right";
	static private final String UP				= "WC_Up";
	static private final String DOWN			= "WC_Down";
    
    /**
     * Constructor of WorldController.
     * It creates the controller view.
     * @param settings The jMonkey application settings
     * @param project The main project
     * @throws SQLException 
     */
    public WorldController(AppSettings settings, Project project) throws SQLException{

    	this.appSettings = settings;

        this.project = project;
        this.inConstruction = new LinkedList <Point>();

        String floorUID = project.config("floor.current");
        this.currentFloor = (Floor) project.getGeometryDAO().getByUID(floorUID);
        if (this.currentFloor == null)
        	this.currentFloor = project.getGeometryDAO().getFloors().get(0);
        this.currentEditionMode = project.config("edition.mode");
        if (this.currentEditionMode.equals("")) // set as default for the first time
        	this.currentEditionMode = WORLDMODE;
        project.addObserver(this);
        this.mouseMode = project.config("mouse.mode");
    }
    
    public void run(){
    	initView(this.project);
        this.view.setSettings(this.appSettings);
        this.view.createCanvas();
    }
    
	/**
	 * This method initiate the view
	 * @param project 
	 */
	public void initView(Project project){
		this.view = new WorldView(this, project);
	}

	/**
     * @return the world view.
     */
    public WorldView getView(){
        return this.view;
    }
    
    public Floor getCurrentFloor(){
    	return this.currentFloor;
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
     * @return The camera mode controller.
     */
    public CameraContext getCameraModeController() {
        return this.cameraContext;
    }
    
    public void setCameraContext(CameraContext cameraContext) {
    	this.cameraContext = cameraContext;
    }
    
    /**
     * Start the view canvas.
     */
    public void startViewCanvas(){
        this.view.startCanvas();
    }
    
    
    /**
     * Update the screen according to the current edition mode.
     * @param mode A string who's a valid mode.
     */
    public void updateEditionMode() {
    	if (this.currentEditionMode.equals(WORLDMODE) ){
    		this.view.cleanScene();
    		this.view.makeScene();
    	} else if (this.currentEditionMode.equals(OBJECTMODE)) {
    		this.view.cleanScene();
    		this.view.makeScene(this.currentEntity);
    	}
	}
    
    public void updateEditionMode(String mode) {
    	if (mode!=this.currentEditionMode) {
    		this.currentEditionMode = mode;
    		updateEditionMode();
    	}
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
     * Convert a click position to clicked item
     * @return The clicked Geometric item, or null if not found
     */
    public Geometric getClickedObject(){
        CollisionResults results = new CollisionResults();
        this.view.getRootNode().collideWith(getRayForMousePosition(), results);
        
        if (results.size() > 0){
        	// Get 3D object from scene
            Geometry selected = results.getClosestCollision().getGeometry();         
            GeometryDAO dao = null;
            try {
                dao = this.project.getGeometryDAO();
                // Get associated Geometric from database
                return dao.getByUID(selected.getName());
            } catch (SQLException e1) {
            	Log.exception(e1);
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
    public void dropMovingPoint(boolean finalPoint){
    	if (this.movingPoint == null)
    		return;
    	
    	getXYForMouse(this.movingPoint);
        
        try {
        	GeometryDAO dao = this.project.getGeometryDAO();
        	dao.update(this.movingPoint);
        	if (finalPoint) 
        		this.movingPoint = null;
        	dao.notifyObservers();
        } catch (SQLException err){
        	Log.exception(err);
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
		Vector2f myVector = getXYForMouse((float) this.currentFloor.getBaseHeight());
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
	        	GeometryDAO dao = this.project.getGeometryDAO();
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
	            if (! this.project.config("floor.current").equals(floorUID))
	            	this.project.config("floor.current", floorUID);
	        } catch (SQLException ex) {
	        	Log.exception(ex);
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
        	GeometryDAO dao = this.project.getGeometryDAO();
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
        	Log.exception(err);
        }
		this.inConstruction.add(lastPoint);
    }
    
    /**
     * Mesh the points together for the walls creation
     */
    public void finalizeConstruct(){
    	try {
			GeometryDAO dao = this.project.getGeometryDAO();
	    	if (this.inConstruction.size() >= 3){
	    		dao.createRoom(this.currentFloor, this.inConstruction);
		    	dao.notifyObservers();
	    	} else {
	    		for (Point p : this.inConstruction){
	    			if (p.getBindings().size() == 0){
	    				dao.delete(p);
	    			} else {
		    			p.deselect();
		    			dao.update(p);
	    			}
	    		}
	    		dao.notifyObservers();
	    	}
	    	this.inConstruction.clear();
		} catch (SQLException ex) {
			Log.exception(ex);
		}
    }
    
    private void updateShapeDisplay(boolean finalUpdate) {
    	Vector2f currPos = getXYForMouse(0);
    	float dist = currPos.distance(this.savedCenter);
    	
    	if (this.mouseMode.equals("cube")) {
    		float d = dist / FastMath.sqr(2);
    		Vector3f center = new Vector3f(this.savedCenter.x-d,this.savedCenter.y-d,dist/2);
    		this.builtGeometric.setLocalTranslation(center);
    		this.builtGeometric.setLocalScale(dist); // h^2 = 2a^2 <=> h = sqrt(2) a <=> a = h/sqrt(2)
    	} else if (this.mouseMode.equals("sphere")) {
    		Vector3f center = new Vector3f(this.savedCenter.x,this.savedCenter.y,dist);
    		this.builtGeometric.setLocalTranslation(center);
    		this.builtGeometric.setLocalScale(dist);
    	}
    	
    	try {
			GeometryDAO dao = this.project.getGeometryDAO();
			Primitive primitive = (Primitive) dao.getByUID(this.builtGeometric.getName());
			primitive.setScale(this.builtGeometric.getLocalScale());
			primitive.setTranslation(this.builtGeometric.getLocalTranslation());
			dao.update(primitive);
		} catch (SQLException ex) {
			Log.exception(ex);
		}
    	
    	
    	if (finalUpdate){
    		this.builtGeometric = null;
    	}
    }
    
    private void mouseMoved(float value) {
    	if (this.movingPoint != null) {
    		dropMovingPoint(false);
    	} else if (this.savedCenter != null) {
    		if (this.leftClickPressed)
    			updateShapeDisplay(false);
    	}
    }
    
	public void inputSetUp(InputManager inputManager){
		// Mouse event mapping
		inputManager.addMapping(RIGHTCLICK, 	new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
		inputManager.addMapping(LEFTCLICK,  	new MouseButtonTrigger(MouseInput.BUTTON_LEFT ));

		inputManager.addMapping(UP, 			new MouseAxisTrigger(1, false));
		inputManager.addMapping(DOWN, 			new MouseAxisTrigger(1, true));
		inputManager.addMapping(LEFT,			new MouseAxisTrigger(0, true));
		inputManager.addMapping(RIGHT,			new MouseAxisTrigger(0, false));
		
		inputManager.addListener(this, 
										RIGHTCLICK, 
										LEFTCLICK,
										
										UP,
										DOWN,
										LEFT,
										RIGHT
								);
	}
    
    private void dragSelectHandler() {
    	/* Find the Geometric object where we clicked */
        Geometric clicked = getClickedObject();
        
        /* We're not interested if no object */
        if (clicked == null)
        	return;
        
        /* If it is a Meshable (Wall, Ground): select it */
        if (clicked instanceof Meshable)
        	selectObject((Meshable) clicked);
        
        /* If it is a Point: initiate drag'n drop */
        else if (clicked instanceof Point)
    		this.movingPoint = (Point) clicked;
    }
    
    public void initSphere() {
    	Sphere sphere = new Sphere(32,32,1f);
		try {
			GeometryDAO dao = this.project.getGeometryDAO();
			Primitive primitive = new Primitive(this.currentEntity,Primitive.SPHERE);
			dao.create(primitive);
			this.builtGeometric = new Geometry(primitive.getUID(), sphere);
		} catch (SQLException ex) {
			Log.exception(ex);
		}
    	this.savedCenter = getXYForMouse(0f);
		
		sphere.setTextureMode(Sphere.TextureMode.Projected);
		Material sphereMat = new Material(this.view.getAssetManager(),"Common/MatDefs/Light/Lighting.j3md");
		sphereMat.setBoolean("UseMaterialColors",true);    
		sphereMat.setColor("Diffuse",new ColorRGBA(0.8f,0.9f,0.2f,0.5f));
		sphereMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		this.builtGeometric.setMaterial(sphereMat);
		this.builtGeometric.setLocalScale(0);
		this.view.getRootNode().attachChild(this.builtGeometric);
    }
    
    public void initCube() {
    	Box box = new Box(0.5f,0.5f,0.5f);
    	try {
			GeometryDAO dao = this.project.getGeometryDAO();
			Primitive primitive = new Primitive(this.currentEntity,Primitive.CUBE);
			dao.create(primitive);
			this.builtGeometric = new Geometry(primitive.getUID(), box);
		} catch (SQLException ex) {
			Log.exception(ex);
		}
    	
    	this.savedCenter = getXYForMouse(0f);
		Material boxMat = new Material(this.view.getAssetManager(),"Common/MatDefs/Light/Lighting.j3md");
		boxMat.setBoolean("UseMaterialColors",true);    
		boxMat.setColor("Diffuse",new ColorRGBA(0.8f,0.9f,0.2f,0.5f));
		boxMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		this.builtGeometric.setMaterial(boxMat);
		this.builtGeometric.setLocalScale(0);
		this.view.getRootNode().attachChild(this.builtGeometric);
    }
    
    /**
     * Handle click
     */
    @Override
	public void onAction(String name, boolean value, float tpf) {	
		if (name.equals(LEFTCLICK)) {
	    	this.leftClickPressed = value;
			if (value) { // on click
				if (this.mouseMode.equals("construct")) { /* We're in construct mode and left-click: add a point */
					construct();
				} else if (this.mouseMode.equals("dragSelect")) {
					dragSelectHandler();
				} else if(this.mouseMode.equals("sphere")){
					initSphere();
				} else if(this.mouseMode.equals("cube")){
					initCube();
				}
				
			} else { // on release
				if (this.movingPoint != null) { // We're moving a point, and mouse button up: stop the point here
					dropMovingPoint(true);
				} else if (this.builtGeometric != null) {
	    			updateShapeDisplay(true);
					
				}
			}
		} else if (name.equals(RIGHTCLICK)) {
			if (value) { // on click
				if (this.inConstruction.size() > 0) { // We're building a shape, and right-click: finish shape
					finalizeConstruct();
				}
				else if (this.mouseMode.equals("dragSelect")){
					Geometric clicked = getClickedObject();
					if (clicked instanceof Meshable){
						try {
							setTexture((Meshable)clicked,this.project.config("texture.selected"));
						} catch (SQLException ex) {
							Log.exception(ex);
						}
						
					}
				}
			} else { // on release
				
			}
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
    
	@Override
	public void update(Observable obs, Object msg) {
		if (obs instanceof Project) {
			Config config = (Config) msg;
			if (config.getName().equals("edition.mode")) {
				updateEditionMode(config.getValue());
			} else if (config.getName().equals("floor.current")){
				String newUID = config.getValue();
				if (newUID.equals(this.currentFloor.getUID()))
					return;
				try {
					this.currentFloor = (Floor) this.project.getGeometryDAO().getByUID(config.getValue());
				} catch (SQLException ex) {
					Log.exception(ex);
				}
			} else if (config.getName().equals("mouse.mode")) {
				this.mouseMode = config.getValue();
			} else if (config.getName().equals("entity.current")) {
				try {
					this.currentEntity = (Entity) this.project.getGeometryDAO().getByUID(config.getValue());
				} catch (SQLException ex) {
					Log.exception(ex);
				}
			}
		}
	}

	@Override
	public void onAnalog(String name, float value, float tpf) {
		if (name.equals(UP)) {
			mouseMoved(value);
		} else if (name.equals(DOWN)) {
			mouseMoved(value);
		} else if (name.equals(LEFT)) {
			mouseMoved(value);
		} else if (name.equals(RIGHT)) {
			mouseMoved(value);
		}
	}
}
