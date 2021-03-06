/**
 * 
 */
package be.ac.ulb.infof307.g03.world;

import java.awt.SplashScreen;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.concurrent.Callable;

import be.ac.ulb.infof307.g03.camera.CameraContext;
import be.ac.ulb.infof307.g03.models.*;
import be.ac.ulb.infof307.g03.utils.Log;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;

/**
 * This class is a jMonkey canvas that can be added in a Swing GUI.
 * @author fhennecker, julianschembri, brochape, Titouan, wmoulart
 */
public class WorldView extends SimpleApplication implements Observer, ActionListener, AnalogListener {	
	
	private Project project = null;
	private MasterDAO daoFactory = null;
	private CanvasController controller = null; 
	protected Vector<Geometry> shapes = new Vector<Geometry>();
	private String classPath = getClass().getResource("WorldView.class").toString();
	private LinkedList<Change> queuedChanges = null;
	
	// Input Aliases
	static private final String RIGHTCLICK 		= "WC_SelectObject";
	static private final String LEFTCLICK 		= "WC_Select";
	static private final String LEFT 			= "WC_Left";
	static private final String RIGHT			= "WC_Right";
	static private final String UP				= "WC_Up";
	static private final String DOWN			= "WC_Down";
	static private final String SHIFT			= "Shift";
	
	/**
	 * WorldView's Constructor
	 * @param project Model of the view.
	 * @param settings Settings of the SimpleApplication
	 */
	public WorldView(Project project, AppSettings settings){
		super();
		this.project = project;
		this.queuedChanges = new LinkedList<Change>();
		this.setSettings(settings);
		try {
			this.daoFactory= project.getMasterDAO();
			this.daoFactory.addObserver(this);
		} catch (SQLException ex) {
			Log.exception(ex);
		}
		this.setDisplayStatView(false);
		this.project.addObserver(this);
	}
	
	/**
	 * @return The model
	 */
	public Project getProject() {
		return this.project;
	}
	
	/**
	 * @see com.jme3.app.SimpleApplication#simpleInitApp()
	 */
	@Override
	public void simpleInitApp() {
		// !!! cam only exist when this function is called !!!
		flyCam.setEnabled(false);
		
		// Update the edition mode
		updateController(this.project.config("edition.mode"));
		
		// Update the camera mode
		this.controller.setCameraContext(new CameraContext(this.project,cam,inputManager, this));
		
		// Change the default background
		viewPort.setBackgroundColor(ColorRGBA.White);

		// listen for clicks on the canvas
		this.inputSetUp(inputManager);
		
		// Notify our controller that initialisation is done
		this.setPauseOnLostFocus(false);
		if(!(classPath.subSequence(0, 3).equals("rsr"))){		
			this.assetManager.registerLocator(System.getProperty("user.dir") +"/src/be/ac/ulb/infof307/g03/assets/", FileLocator.class);
		}
        final SplashScreen splash = SplashScreen.getSplashScreen();
        try{
        	splash.close();
        }
        catch (NullPointerException ex){
        	Log.exception(ex, "No splashscreen");
        }
        	
        		

	}
	
	/**
	 * Creates a directional light across the whole world. 
	 */
	private void addSun(){
		DirectionalLight sun = new DirectionalLight();
		sun.setColor(ColorRGBA.White);
		sun.setDirection(new Vector3f(.45f,.35f,-1f).normalizeLocal());
		rootNode.addLight(sun);
		
		AmbientLight ambient = new AmbientLight();
		ambient.setColor(ColorRGBA.White.mult(2.3f));
		rootNode.addLight(ambient);
	}
	
	/**
	 * @return The shapes of the 3D environment
	 */
	public Vector<Geometry> getShapes(){
		shapes = new Vector<Geometry>();
		this.generateShapesList(rootNode);
		return shapes;
	}
	
	public InputManager getInputManager(){
		return inputManager;
	}
	
	/**
	 * This methods created the grid and adds it to the background
	 */
	private void attachGrid(){
		//Grid size
		int gridLength = 1000;
		int gridWidth = 1000;
		int squareSpace = 1;
		
		//Sets a material to the grid (needed by jme)
		Grid grid = new Grid(gridLength,gridWidth,squareSpace);
		Geometry gridGeo = new Geometry("Grid", grid);
		gridGeo.setMaterial(makeBasicMaterial(ColorRGBA.LightGray));
		
		//The quaternion defines the rotation
		Quaternion roll90 = new Quaternion(); 
		roll90.fromAngleAxis( FastMath.PI/2 , new Vector3f(1,0,0));
		gridGeo.rotate(roll90);
		
		//Moves the center of the grid 
		gridGeo.center().move(new Vector3f(0.5f,-50.5f,-0.01f));
		rootNode.attachChild(gridGeo);
	}
	
	/**
	 * This method creates a correct material
	 * @param color
	 * @return The material created
	 */
	public Material makeBasicMaterial(ColorRGBA color){
		Material res = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		res.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
		res.setColor("Color", color);
		return res;
	}

	/**
	 * Redraw the entire 3D scene
	 */
	public void makeScene(){
		this.enqueue(new Callable<Object>() {
	        public Object call() {
	        	//Generates the grid
	    		attachGrid();
	    		
	    		//Generate the axes
	    		attachAxes();
	    		
	    		// Add a bit of sunlight into our lives
	    		addSun();
	    		
	    		try {
					for (Floor floor : daoFactory.getDao(Floor.class).queryForAll()){
						/* Draw rooms areas */
						for (Room room : floor.getRooms()){
							for (Meshable meshable : room.getAreas()){
								drawMeshable(rootNode,meshable);
							}
						}
						/* Draw objects */
						for (Item item : floor.getItems()){
							drawMeshable(rootNode, item);
						}
					}
				} catch (SQLException ex) {
					Log.exception(ex);
				}
	            return null;
	        }
	    });
	}
	
	/**
	 * Draw the 3D scene from an entity
	 * @param entity The entity to display
	 */
	public void makeScene(final Entity entity) {
		enqueue(new Callable<Object>() {
	        public Object call() {
	        	//Generates the grid
	    		attachGrid();
	    		
	    		//Generate the axes
	    		attachAxes();
	    		
	    		// Add a bit of sunlight into our lives
	    		addSun();
	        	
	        	drawMeshable(rootNode, entity);
	            return null;
	        }
	    });
	}
	
	/**
	 * Cleans the entire scene. Removes all children and lights.
	 */
	public void cleanScene(){
		this.project.getSelectionManager().unselectAll();
		enqueue(new Callable<Object>() {
	        public Object call() {
	        	rootNode.detachAllChildren();
	        	for (Light light : rootNode.getWorldLightList()) {
					rootNode.removeLight(light);
				}
	            return null;
	        }
	    });
	}

	/**
	 * Method used to generate the XYZ Axes
	 */
	private void attachAxes(){
		Vector3f origin = new Vector3f(0,0,0);
		Vector3f xAxis = new Vector3f(500,0,0);
		Vector3f yAxis = new Vector3f(0,500,0);
		Vector3f zAxis = new Vector3f(0,0,500);
		
		attachAxis(origin, xAxis,ColorRGBA.Red);
		attachAxis(origin, yAxis,ColorRGBA.Green);
		attachAxis(origin, zAxis,ColorRGBA.Blue);
	}
	
	private void drawMeshable(Node parent, Meshable meshable){
		if (! meshable.isVisible())
			return;
		Spatial spatial = meshable.toSpatial(assetManager);
		parent.attachChild(spatial);
	}
	
	/**
	 * Method used to generate one axe from start to end in a certain color
	 * @param start Start of the vector
	 * @param end End of the vector
	 * @param color Color of the vector
	 */
	private void attachAxis(Vector3f start, Vector3f end,ColorRGBA color){		
		Line axis = new Line(start,end);
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		Geometry axisGeo = new Geometry("Axis", axis);
		axisGeo.setMaterial(mat);
		mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
		mat.setColor("Color", color);
		rootNode.attachChild(axisGeo);
	}

	/**
	 * Update view when a Point has changed
	 * @param change
	 */
	private void updatePoint(Change change){
		Point point = (Point) change.getItem();
		Floor floor = null;
		if (this.controller instanceof WorldController) {
			floor = ((WorldController) this.controller).getCurrentFloor();
		}
		drawOnePoint(point, floor);
		for (Room room: point.getBoundRooms()){
			try {
				GeometricDAO<Room> dao = this.daoFactory.getDao(Room.class);
				dao.refresh(room);
				for (Meshable meshable : room.getAreas())
					updateArea(Change.update(meshable));
			} catch (SQLException ex) {
				Log.exception(ex);
			}
		}
	}
	
	/**
	 * Show points as spheres around a room
	 * @param change
	 */
	private void showPoints(Room room){
		for (Point point : room.getPoints()){
			this.drawOnePoint(point, room.getFloor());
		}
	}
	
	private void drawOnePoint(Point point, Floor floor){
		Spatial node = this.rootNode.getChild(point.getUID());
		if (node != null){
			node.setLocalTranslation(point.toVector3f().setZ((float) floor.getBaseHeight()));
		}
		else {
			Sphere mySphere = new Sphere(25,25, 0.7f);
		    Geometry sphere = new Geometry(point.getUID(), mySphere);
		    mySphere.setTextureMode(Sphere.TextureMode.Projected);
		    Material sphereMat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
		    sphereMat.setColor("Color",new ColorRGBA(0.8f,0.9f,0.2f,0.99f));
		    sphereMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		    sphere.setMaterial(sphereMat);
		    sphere.setLocalTranslation(point.toVector3f().setZ((float) floor.getBaseHeight()));
		    rootNode.attachChild(sphere);
		    sphere.setCullHint(CullHint.Never);
		}
	}
	
	/**
	 * @param change
	 */
	public void updatePrimitive(Change change) {
		Primitive primitive = (Primitive) change.getItem();
		if (primitive.isVisible()) 
			if (change.isCreation()) {
				 drawMeshable(rootNode, primitive);
			} else {
				this.redrawPrimitive(primitive);
			}
	}
	
	/**
	 * Re-draw the primitive
	 * @param primitive The primitive to be drawn
	 */
	public void redrawPrimitive(Primitive primitive){
		Spatial spatial = rootNode.getChild(primitive.getUID());
		if (spatial != null){
			spatial.getParent().detachChild(spatial);
			drawMeshable(rootNode, primitive);
			spatial.setLocalTranslation(primitive.getTranslation());
		}
	}

	
	/**
	 * Update view when a Meshable has changed
	 * @param change
	 */
	private void updateArea(Change change){
		Meshable meshable = (Meshable) change.getItem();
		this.redrawMeshable(meshable);
	}
	
	private void redrawMeshable(Meshable meshable){
		Spatial node = rootNode.getChild(meshable.getUID());
		Node parent = rootNode;
		if (node != null){
			parent = node.getParent();
			/* 3D object don't exist yet if it is a creation */
			parent.detachChild(node);
		}
		
		/* No need to redraw if it is a deletion */
		drawMeshable(parent,meshable);
			
		/* Conclusion: updates will do both (detach & redraw) */
	}
	
	private void updateFloor(Change change){
		Log.info("updateFloor");
		cleanScene();
		makeScene();
	}
	
	private void updateRoom(Change change){
		Room room = (Room) change.getItem();
		if (room.isSelected()){
			this.showPoints(room);
		} else {
			for (Point point : room.getPoints()){
				Spatial node = this.rootNode.getChild(point.getUID());
				if (node != null)
					this.rootNode.detachChild(node);
			}
		}
		for (Meshable meshable : room.getAreas()){
			this.redrawMeshable(meshable);
		}
	}
	
	/**
	 * @param node
	 */
	public void generateShapesList(Node node){
		java.util.List<Spatial> children = node.getChildren();
		for(int i = 0; i < children.size(); ++i){
			Spatial child = children.get(i);
			if(child instanceof Geometry && !(child.getName().equals("Grid")) && !(child.getName().equals("Axis"))){
				shapes.add((Geometry) child);
			}
			else if(child instanceof Node){
				generateShapesList((Node) child);
			}
		}
	}
	
	private void deleteMeshable(Meshable meshable) {
		if (meshable instanceof Primitive) {
			Spatial node = rootNode.getChild(meshable.getUID());
			if (node != null){
				node.getParent().detachChild(node);
			}
		} else 
			rootNode.detachChildNamed(meshable.getUID());
	}
	
	/**
	 * Modify scene in render thread, if any Change
	 */
	@Override
	public void simpleUpdate(float t){
		synchronized (this.queuedChanges){
			if (this.queuedChanges.size() > 0){
				for (Change change : this.queuedChanges){
					if (change.isDeletion()){ // handle all deletion
						if (change.getItem() instanceof Meshable)
							deleteMeshable((Meshable) change.getItem());
						else if (change.getItem() instanceof Point){
							Spatial node = this.rootNode.getChild(change.getItem().getUID());
							if (node != null)
								this.rootNode.detachChild(node);
						}
						else if (change.getItem() instanceof Room){
							// Do nothing, the room's Areas will delete themselves
						}
					}
					else if (change.getItem() instanceof Item) 
						updateItem(change);
					else if (change.getItem() instanceof Primitive) 
						updatePrimitive(change);
					else if (change.getItem() instanceof Area)
						updateArea(change);
					else if (change.getItem() instanceof Point)
						updatePoint(change);
					else if (change.getItem() instanceof Floor) // when new floor or floor deleted
						updateFloor(change);
					else if (change.getItem() instanceof Room) // for example if a room has been selected
						updateRoom(change);
				}		
				this.queuedChanges.clear();
			}
		}
	}

	/**
	 * @param change
	 */
	public void updateItem(Change change) {
		Item item = (Item) change.getItem();
		Spatial node = rootNode.getChild(item.getUID());
		if (item.isVisible()) 
			if (node==null) {
				drawMeshable(rootNode,item);
			} else {
				node.setLocalTranslation(item.getAbsolutePositionVector());
			}
	}

	/**
	 * Set up all the key event
	 * @param inputManager 
	 */
	public void inputSetUp(InputManager inputManager){
		// Mouse event mapping
		inputManager.addMapping(RIGHTCLICK, 	new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
		inputManager.addMapping(LEFTCLICK,  	new MouseButtonTrigger(MouseInput.BUTTON_LEFT ));

		inputManager.addMapping(UP, 			new MouseAxisTrigger(1, false));
		inputManager.addMapping(DOWN, 			new MouseAxisTrigger(1, true));
		inputManager.addMapping(LEFT,			new MouseAxisTrigger(0, true));
		inputManager.addMapping(RIGHT,			new MouseAxisTrigger(0, false));
		
        inputManager.addMapping(SHIFT, 			new KeyTrigger(KeyInput.KEY_LSHIFT));
        inputManager.addMapping(SHIFT,          new KeyTrigger(KeyInput.KEY_RSHIFT));
		
		inputManager.addListener(this, RIGHTCLICK, LEFTCLICK, UP, DOWN, LEFT, RIGHT,SHIFT);
	}
	
	/**
     * Update the screen according to the current edition mode.
     * @param mode A string who's a valid mode.
     */    
    public void updateController(String mode) {
    	cleanScene();
    	if (mode.equals("world")) {
    		this.controller = new WorldController(this, this.settings);
    	} else if (mode.equals("object")) {
    		this.controller = new ObjectController(this, this.settings);
    	}
    }
	
	@Override
	public void update(Observable obs, Object msg) {
		if (obs instanceof Project) {
			Config config = (Config) msg;
			if (config.getName().equals("edition.mode")) {
				updateController(config.getValue());
			}
		} else if (obs instanceof MasterDAO) {
			synchronized (this.queuedChanges) {
				this.queuedChanges.addAll((List<Change>) msg);
			}
		}
	}

	@Override
	public void onAnalog(String name, float value, float tpf) {
		if (name.equals(UP) || name.equals(DOWN) || name.equals(LEFT) || name.equals(RIGHT) ) {
			this.controller.mouseMoved(value);
		}
	}
	
	/**
     * Handle click
     */
    @Override
	public void onAction(String name, boolean value, float tpf) {	
		if (name.equals(LEFTCLICK)) {
			if (value) { // on click
				this.controller.onLeftClick();
			} else { // on release
				this.controller.onLeftRelease();
			}
		} else if (name.equals(RIGHTCLICK)) {
			if (value) { // on click
				this.controller.onRightClick();
			} else { // on release
				
			}
		}
		else if (name.equals(SHIFT)){ // If Shift is Pressed
			this.controller.setSnapToGrid(value);
		}
	}
	
}
