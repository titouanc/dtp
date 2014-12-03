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
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
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
    private Geometric movingGeometric = null;
    private List<Point> inConstruction ;
    private Floor currentFloor = null;
	private String currentEditionMode;
	private String mouseMode;
	
	private Vector2f savedCenter = null;
	private boolean leftClickPressed = false;
	private Primitive builtPrimitive = null;
	private Entity currentEntity = null;
    
    // Edition mode alias
	static final private String WORLDMODE = "world";
	static final private String OBJECTMODE = "object";

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
        List<Floor> listFloor = project.getGeometryDAO().getFloors();
        if (this.currentFloor == null && listFloor.size()>0)
        	this.currentFloor = listFloor.get(0);
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
    	Geometric clicked = null;
        CollisionResults results = new CollisionResults();
        this.view.getRootNode().collideWith(getRayForMousePosition(), results);
        
        if (results.size() > 0){
        	// Get 3D object from scene
            Geometry selected = results.getClosestCollision().getGeometry();
            
            try {
            	GeometryDAO dao = this.project.getGeometryDAO();
                // Get associated Geometric from database
                clicked = dao.getByUID(selected.getName());
                
        		if (this.currentEditionMode.equals(WORLDMODE) && clicked instanceof Primitive){
        			/* In world mode, select the whole Item (not only one of its Primitive) */
        			Node parentNode = selected.getParent();
        			clicked = dao.getByUID(parentNode.getName());
        		}
            } catch (SQLException e1) {
            	Log.exception(e1);
            }
        }
        return clicked;
    }
    
    /**
     * Drop the currently moving point:
     * - Compute final position
     * - Update in database and notify
     * - Set current moving point to null
     */
    public void dropMovingPoint(boolean finalMove){
    	Point movingPoint = (Point) movingGeometric;
    	if (movingPoint == null)
    		return;
    	
    	getXYForMouse(movingPoint);
        
        try {
        	GeometryDAO dao = this.project.getGeometryDAO();
        	dao.update(movingPoint);
        	if (finalMove) 
        		movingGeometric = null;
        	dao.notifyObservers();
        } catch (SQLException err){
        	Log.exception(err);
        }
    }
    
    public void dropMovingPrimitive(boolean finalMove) {
    	Primitive movingPrimitive = (Primitive) movingGeometric;
    	if (movingPrimitive == null)
    		return;
    	
    	Vector2f v = getXYForMouse(0);
    	movingPrimitive.setTranslation(new Vector3f(v.x,v.y,movingPrimitive.getTranslation().z));

    	try {
    		GeometryDAO dao = this.project.getGeometryDAO();
    		dao.update(movingPrimitive);
    		if (finalMove) 
    			movingGeometric = null;
    		dao.notifyObservers(movingPrimitive);
    	} catch (SQLException err){
    		Log.exception(err);
    	}
    }
    
    private void dropMovingItem(boolean finalMove) {
		Item moving = (Item) movingGeometric;
		if (moving == null)
			return;
		
		Vector2f pos = getXYForMouse(moving.getAbsolutePositionVector().z);
		moving.setPosition(new Vector3f(pos.x, pos.y, moving.getPositionVector().z));
		try {
    		GeometryDAO dao = this.project.getGeometryDAO();
    		dao.update(moving);
    		if (finalMove) 
    			movingGeometric = null;
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
     * Toggle selection for a Area item, save to database and notify observers
     * @param area The Area item to select
     */
	public void selectArea(Area area) {
		try {
			area.toggleSelect();
			GeometryDAO dao = this.project.getGeometryDAO();
			for (Point p : area.getPoints()){
				if (area.isSelected())
					p.select();
				else
					p.deselect();
				dao.update(p);
			}
			dao.update(area);
			dao.notifyObservers(area);
			String floorUID = area.getRoom().getFloor().getUID();
			if (! this.project.config("floor.current").equals(floorUID))
				this.project.config("floor.current", floorUID);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
     * Toggle selection for an Item, save to database and notify observers
     * @param item The Item to select
     */
	public void selectItem(Item item) {
		try {
			item.toggleSelect();
			GeometryDAO dao = this.project.getGeometryDAO();
			dao.update(item);
			dao.notifyObservers();
			
			String floorUID = item.getFloor().getUID();
			if (! currentFloor.getUID().equals(floorUID)){
				this.project.config("floor.current", floorUID);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void selectPrimitive(Primitive primitive) {
		try {
			primitive.toggleSelect();
			GeometryDAO dao = this.project.getGeometryDAO();
			dao.update(primitive);
			dao.notifyObservers(primitive);
			Vector3f center = primitive.getTranslation();
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
		float dn = dist / FastMath.pow(3, 0.3333f);
    	this.builtPrimitive.setScale(new Vector3f(dn,dn,dn));
    	if (!this.builtPrimitive.getType().equals(Primitive.PYRAMID)) {
    		if (this.builtPrimitive.getType().equals(Primitive.SPHERE)) {
    			this.builtPrimitive.setTranslation(new Vector3f(this.savedCenter.x,this.savedCenter.y,dn));
    		} else {
    			this.builtPrimitive.setTranslation(new Vector3f(this.savedCenter.x,this.savedCenter.y,dn/2));
    		}
    	}
    	try {
			GeometryDAO dao = this.project.getGeometryDAO();
			dao.update(this.builtPrimitive);
			dao.notifyObservers(this.builtPrimitive);
		} catch (SQLException ex) {
			Log.exception(ex);
		}
    	
    	
    	if (finalUpdate){
    		this.builtPrimitive = null;
    		this.savedCenter = null;
    	}
    }
    
    private void mouseMoved(float value) {
    	if (movingGeometric != null) {
    		if (movingGeometric instanceof Point)
    			dropMovingPoint(false);
    		else if (movingGeometric instanceof Primitive)
    			dropMovingPrimitive(false);
    		else if (movingGeometric instanceof Item)
    			dropMovingItem(false);
    	} else if (this.builtPrimitive != null) {
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
		
		inputManager.addListener(this, RIGHTCLICK, LEFTCLICK, UP, DOWN, LEFT, RIGHT);
	}
    
    private void dragSelectHandlerW() {
    	/* Find the Geometric object where we clicked */
        Geometric clicked = getClickedObject();
        
        /* We're not interested if no object */
        if (clicked == null)
        	return;
        
        /* If it is an Area (Wall, Ground, Roof): select it */
        if (clicked instanceof Area)
        	selectArea((Area) clicked);
        
        else if (clicked instanceof Item){
        	selectItem((Item) clicked);
        	this.movingGeometric = clicked;
        }
        
        /* If it is a Point: initiate drag'n drop */
        else if (clicked instanceof Point)
    		this.movingGeometric = clicked;
    }
    
    private void dragSelectHandlerO() {
    	/* Find the Geometric object where we clicked */
        Geometric clicked = getClickedObject();
        
        /* We're not interested if no object */
        if (clicked == null)
        	return;
        
        /* If it is a Primitive : select it */
        if (clicked instanceof Primitive) {
        	selectPrimitive((Primitive) clicked);
        	this.movingGeometric = (Primitive) clicked;
        }
    }
    
    public void initShape(String type) {
    	this.savedCenter = getXYForMouse(0f);
    	try {
			GeometryDAO dao = this.project.getGeometryDAO();
			this.builtPrimitive = new Primitive(this.currentEntity,type);
			this.builtPrimitive.setScale(new Vector3f(0,0,0));
			this.builtPrimitive.setTranslation(new Vector3f(this.savedCenter.x,this.savedCenter.y,0));
			dao.create(this.builtPrimitive);
			dao.notifyObservers(this.builtPrimitive);
		} catch (SQLException ex) {
			Log.exception(ex);
		}
    }
    
    /**
     * Handle click
     */
    @Override
	public void onAction(String name, boolean value, float tpf) {	
		if (name.equals(LEFTCLICK)) {
	    	this.leftClickPressed = value;
			if (value) { // on click
				if (currentEditionMode.equals(WorldController.WORLDMODE)) {
					if (this.mouseMode.equals("construct")) { /* We're in construct mode and left-click: add a point */
						construct();
					} else if (this.mouseMode.equals("dragSelect")) {
						dragSelectHandlerW();
					} 
				} else if (currentEditionMode.equals(WorldController.OBJECTMODE)) {
					if (this.mouseMode.equals("dragSelect")) {
						dragSelectHandlerO();
					} else if (this.mouseMode.equals("pyramid")) {
						initShape(Primitive.PYRAMID);
					} else if (this.mouseMode.equals("cylinder")) {
						initShape(Primitive.CYLINDER);
					} else if (this.mouseMode.equals("sphere")) {
						initShape(Primitive.SPHERE);
					} else if (this.mouseMode.equals("cube")) {
						initShape(Primitive.CUBE);
					}
				}
				
			} else { // on release
				if (movingGeometric != null) { // We're moving a point, and mouse button up: stop the point here
					if (movingGeometric instanceof Point)
						dropMovingPoint(true);
					else if (movingGeometric instanceof Primitive) 
						dropMovingPrimitive(true);
					else if (movingGeometric instanceof Item)
						dropMovingItem(true);
				} else if (this.builtPrimitive != null) {
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
				if (this.currentFloor != null && newUID.equals(this.currentFloor.getUID()))
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
					updateEditionMode();
				} catch (SQLException ex) {
					Log.exception(ex);
				}
			}
		}
	}

	@Override
	public void onAnalog(String name, float value, float tpf) {
		if (name.equals(UP) || name.equals(DOWN) || name.equals(LEFT) || name.equals(RIGHT)) {
			mouseMoved(value);
		}
	}
}
