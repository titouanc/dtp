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
    private WorldView _view;
    private Project _project;
    private CameraContext _cameraContext = null;
    private Point _movingPoint = null;
    private List<Point> _inConstruction ;
    private Floor _currentFloor = null;
	private String _currentEditionMode;
	private String _mouseMode;
	
	private Vector2f _savedCenter = null;
	private boolean _leftClickPressed = false;
	private Geometry _builtGeometric = null;
	private Entity _currentEntity = null;
    
    // Edition mode alias
	static final private String _WORLDMODE = "world";
	static final private String _OBJECTMODE = "object";

    private double _currentHeight;
    private AppSettings _appSettings;
    
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

    	_appSettings = settings;

        _project = project;
        _inConstruction = new LinkedList <Point>();

        String floorUID = project.config("floor.current");
        _currentFloor = (Floor) project.getGeometryDAO().getByUID(floorUID);
        if (_currentFloor == null)
        	_currentFloor = project.getGeometryDAO().getFloors().get(0);
        _currentEditionMode = project.config("edition.mode");
        if (_currentEditionMode.equals("")) // set as default for the first time
        	_currentEditionMode = _WORLDMODE;
        project.addObserver(this);
        _mouseMode = project.config("mouse.mode");
    }
    
    public void run(){
    	initView(_project);
        _view.setSettings(_appSettings);
        _view.createCanvas();
    }
    
	/**
	 * This method initiate the view
	 * @param project 
	 */
	public void initView(Project project){
		_view = new WorldView(this, project);
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
    public CameraContext getCameraModeController() {
        return _cameraContext;
    }
    
    public void setCameraContext(CameraContext cameraContext) {
    	_cameraContext = cameraContext;
    }
    
    /**
     * Start the view canvas.
     */
    public void startViewCanvas(){
        _view.startCanvas();
    }
    
    
    /**
     * Update the screen according to the current edition mode.
     * @param mode A string who's a valid mode.
     */
    public void updateEditionMode() {
    	if (_currentEditionMode.equals(_WORLDMODE) ){
    		_view.cleanScene();
    		_view.makeScene();
    	} else if (_currentEditionMode.equals(_OBJECTMODE)) {
    		_view.cleanScene();
    		_view.makeScene(_currentEntity);
    	}
	}
    
    public void updateEditionMode(String mode) {
    	if (mode!=_currentEditionMode) {
    		_currentEditionMode = mode;
    		updateEditionMode();
    	}
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
    public void dropMovingPoint(boolean finalPoint){
    	if (_movingPoint == null)
    		return;
    	
    	getXYForMouse(_movingPoint);
        
        try {
        	GeometryDAO dao = _project.getGeometryDAO();
        	dao.update(_movingPoint);
        	if (finalPoint) 
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
    	try {
			GeometryDAO dao = _project.getGeometryDAO();
	    	if (_inConstruction.size() >= 3){
	    		dao.createRoom(_currentFloor, _inConstruction);
		    	dao.notifyObservers();
	    	} else {
	    		for (Point p : _inConstruction){
	    			if (p.getBindings().size() == 0){
	    				dao.delete(p);
	    			} else {
		    			p.deselect();
		    			dao.update(p);
	    			}
	    		}
	    		dao.notifyObservers();
	    	}
	    	_inConstruction.clear();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    private void updateShapeDisplay(boolean finalUpdate) {
    	Vector2f currPos = getXYForMouse(0);
    	float dist = currPos.distance(_savedCenter);
    	
    	if (_mouseMode.equals("cube")) {
    		float d = dist / FastMath.sqr(2);
    		Vector3f center = new Vector3f(_savedCenter.x-d,_savedCenter.y-d,dist/2);
    		_builtGeometric.setLocalTranslation(center);
    		_builtGeometric.setLocalScale(dist); // h^2 = 2a^2 <=> h = sqrt(2) a <=> a = h/sqrt(2)
    	} else if (_mouseMode.equals("sphere")) {
    		Vector3f center = new Vector3f(_savedCenter.x,_savedCenter.y,dist);
    		_builtGeometric.setLocalTranslation(center);
    		_builtGeometric.setLocalScale(dist);
    	}
    	
    	try {
			GeometryDAO dao = _project.getGeometryDAO();
			Primitive primitive = (Primitive) dao.getByUID(_builtGeometric.getName());
			primitive.setScale(_builtGeometric.getLocalScale());
			primitive.setTranslation(_builtGeometric.getLocalTranslation());
			dao.update(primitive);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	if (finalUpdate){
    		_builtGeometric = null;
    	}
    }
    
    private void mouseMoved(float value) {
    	if (_movingPoint != null) {
    		dropMovingPoint(false);
    	} else if (_savedCenter != null) {
    		if (_leftClickPressed)
    			updateShapeDisplay(false);
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
        if (clicked == null)
        	return;
        
        /* If it is a Meshable (Wall, Ground): select it */
        if (clicked instanceof Meshable)
        	selectObject((Meshable) clicked);
        
        /* If it is a Point: initiate drag'n drop */
        else if (clicked instanceof Point)
    		_movingPoint = (Point) clicked;
    }
    
    public void initSphere() {
    	Sphere sphere = new Sphere(32,32,1f);
		try {
			GeometryDAO dao = _project.getGeometryDAO();
			Primitive primitive = new Primitive(_currentEntity,Primitive.SPHERE);
			dao.create(primitive);
			_builtGeometric = new Geometry(primitive.getUID(), sphere);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	_savedCenter = getXYForMouse(0f);
		
		sphere.setTextureMode(Sphere.TextureMode.Projected);
		Material sphereMat = new Material(_view.getAssetManager(),"Common/MatDefs/Light/Lighting.j3md");
		sphereMat.setBoolean("UseMaterialColors",true);    
		sphereMat.setColor("Diffuse",new ColorRGBA(0.8f,0.9f,0.2f,0.5f));
		sphereMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		_builtGeometric.setMaterial(sphereMat);
		_builtGeometric.setLocalScale(0);
		_view.getRootNode().attachChild(_builtGeometric);
    }
    
    public void initCube() {
    	Box box = new Box(0.5f,0.5f,0.5f);
    	try {
			GeometryDAO dao = _project.getGeometryDAO();
			Primitive primitive = new Primitive(_currentEntity,Primitive.CUBE);
			dao.create(primitive);
			_builtGeometric = new Geometry(primitive.getUID(), box);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	_savedCenter = getXYForMouse(0f);
		Material boxMat = new Material(_view.getAssetManager(),"Common/MatDefs/Light/Lighting.j3md");
		boxMat.setBoolean("UseMaterialColors",true);    
		boxMat.setColor("Diffuse",new ColorRGBA(0.8f,0.9f,0.2f,0.5f));
		boxMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		_builtGeometric.setMaterial(boxMat);
		_builtGeometric.setLocalScale(0);
		_view.getRootNode().attachChild(_builtGeometric);
    }
    
    /**
     * Handle click
     */
    @Override
	public void onAction(String name, boolean value, float tpf) {	
		if (name.equals(_LEFTCLICK)) {
	    	_leftClickPressed = value;
			if (value) { // on click
				if (_mouseMode.equals("construct")) { /* We're in construct mode and left-click: add a point */
					construct();
				} else if (_mouseMode.equals("dragSelect")) {
					dragSelectHandler();
				} else if(_mouseMode.equals("sphere")){
					initSphere();
				} else if(_mouseMode.equals("cube")){
					initCube();
				}
				
			} else { // on release
				if (_movingPoint != null) { // We're moving a point, and mouse button up: stop the point here
					dropMovingPoint(true);
				} else if (_builtGeometric != null) {
	    			updateShapeDisplay(true);
					
				}
			}
		} else if (name.equals(_RIGHTCLICK)) {
			if (value) { // on click
				if (_inConstruction.size() > 0) { // We're building a shape, and right-click: finish shape
					finalizeConstruct();
				}
				else if (_mouseMode.equals("dragSelect")){
					Geometric clicked = getClickedObject();
					if (clicked instanceof Meshable){
						try {
							setTexture((Meshable)clicked,_project.config("texture.selected"));
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
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
		_project.getGeometryDAO().update(clickedItem);
		_project.getGeometryDAO().notifyObservers();
	}
    
	@Override
	public void update(Observable obs, Object msg) {
		if (obs instanceof Project) {
			Config config = (Config) msg;
			if (config.getName().equals("edition.mode")) {
				updateEditionMode(config.getValue());
			} else if (config.getName().equals("floor.current")){
				String newUID = config.getValue();
				if (newUID.equals(_currentFloor.getUID()))
					return;
				try {
					_currentFloor = (Floor) _project.getGeometryDAO().getByUID(config.getValue());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (config.getName().equals("mouse.mode")) {
				_mouseMode = config.getValue();
			} else if (config.getName().equals("entity.current")) {
				try {
					_currentEntity = (Entity) _project.getGeometryDAO().getByUID(config.getValue());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
