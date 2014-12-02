/**
 * 
 */
package be.ac.ulb.infof307.g03.views;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.concurrent.Callable;

import be.ac.ulb.infof307.g03.controllers.CameraContext;
import be.ac.ulb.infof307.g03.controllers.WorldController;
import be.ac.ulb.infof307.g03.models.*;
import be.ac.ulb.infof307.g03.utils.Log;

import com.j256.ormlite.dao.ForeignCollection;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetLocator;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.input.InputManager;
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
import com.jme3.scene.debug.Grid;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

/**
 * This class is a jMonkey canvas that can be added in a Swing GUI.
 * @author fhennecker, julianschembri, brochape, Titouan, wmoulart
 */
public class WorldView extends SimpleApplication implements Observer {	
	
	private Project project = null;
	private GeometryDAO dao = null;
	private WorldController controller; 
	private LinkedList<Change> queuedChanges = null;
	protected Vector<Geometry> shapes = new Vector<Geometry>();
	private boolean isCreated = false;
	
	
	/**
	 * WorldView's Constructor
	 * @param newController The view's controller
	 * @param model The DAO pattern model class
	 */
	public WorldView(WorldController newController, Project project){
		super();
		this.controller = newController;
		this.project = project;
		this.queuedChanges = new LinkedList<Change>();
		try {
			this.dao= project.getGeometryDAO();
			this.dao.addObserver(this);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.setDisplayStatView(false);
		this.project.addObserver(this);

	}
	
	/**
	 * @see com.jme3.app.SimpleApplication#simpleInitApp()
	 */
	@Override
	public void simpleInitApp() {
		// !!! cam only exist when this function is called !!!
		flyCam.setEnabled(false);
		
		// Update the camera mode
		this.controller.setCameraContext(new CameraContext(this.project,cam,inputManager, this));
		
		// Update the edition mode
		this.controller.updateEditionMode();
		
		// Change the default background
		viewPort.setBackgroundColor(ColorRGBA.White);

		// listen for clicks on the canvas
		this.controller.inputSetUp(inputManager);
		
		// Notify our controller that initialisation is done
		this.setPauseOnLostFocus(false);
		this.setCreated();
		this.assetManager.registerLocator(System.getProperty("user.dir") +"/src/be/ac/ulb/infof307/g03/assets/", FileLocator.class);

	}
	
	/**
	 * Creates a directional light across the whole world. 
	 */
	private void _addSun(){
		DirectionalLight sun = new DirectionalLight();
		sun.setColor(ColorRGBA.Gray);
		sun.setDirection(new Vector3f(-.5f,-.5f,-1f).normalizeLocal());
		rootNode.addLight(sun);
		
		AmbientLight ambient = new AmbientLight();
		ambient.setColor(ColorRGBA.White.mult(1.3f));
		rootNode.addLight(ambient);
	}
	
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
		gridGeo.setMaterial(_makeBasicMaterial(ColorRGBA.LightGray));
		
		//The quaternion defines the rotation
		Quaternion roll90 = new Quaternion(); 
		roll90.fromAngleAxis( FastMath.PI/2 , new Vector3f(1,0,0));
		gridGeo.rotate(roll90);
		
		//Moves the center of the grid 
		gridGeo.center().move(new Vector3f(0,-50,-0.01f));
		rootNode.attachChild(gridGeo);
	}
	
	/**
	 * This method creates a correct material
	 * @param color
	 * @return The material created
	 */
	private Material _makeBasicMaterial(ColorRGBA color){
		Material res = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		res.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
		res.setColor("Color", color);
		return res;
	}
	
	/**
	 * Create the materials with their texture
	 * @param mesh
	 * @param texture
	 * @return
	 */
	private Material _makeMaterial(Meshable mesh){	
		 String texture=mesh.getTexture();
		 if (texture.equals("Gray")){
			texture="Colors/Gray";
		}
		Material res= new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		res.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		res.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
		res.setBoolean("UseMaterialColors", true);
		ColorRGBA color = new ColorRGBA(ColorRGBA.Gray);
		res.setColor("Diffuse", color);
		res.setColor("Ambient", color);
		res.setColor("Specular",color); 
		 if (mesh.isSelected()){
				res.setColor("Ambient",new ColorRGBA(0f,1.2f,0f, 0.5f));
			}
		res.setTexture("DiffuseMap",assetManager.loadTexture(texture+".png"));
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
	    		_attachAxes();
	    		
	    		// Add a bit of sunlight into our lives
	    		_addSun();
	    		
	    		try {
					for (Floor floor : dao.getFloors()){
						for (Room room : floor.getRooms()){
							if (room.getGround() != null)
								_drawGround(room.getGround());
							if (room.getWall() != null)
								_drawWall(room.getWall());
							if (room.getRoof() != null)
								_drawRoof(room.getRoof());
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
	            return null;
	        }
	    });
	}
	
	public void makeScene(final Entity entity) {
		enqueue(new Callable<Object>() {
	        public Object call() {
	        	drawEntity(entity);
	            return null;
	        }
	    });
	}
	
	/**
	 * Cleans the entire scene. Removes all children and lights.
	 */
	public void cleanScene(){
		enqueue(new Callable<Object>() {
	        public Object call() {
	        	rootNode.detachAllChildren();
	        	for (Light light : rootNode.getWorldLightList()) {
					rootNode.removeLight(light);
				}
	        	//Generates the grid
	    		attachGrid();
	    		
	    		//Generate the axes
	    		_attachAxes();
	    		
	    		// Add a bit of sunlight into our lives
	    		_addSun();
	            return null;
	        }
	    });
	}

	/**
	 * Method used to generate the XYZ Axes
	 */
	private void _attachAxes(){
		Vector3f origin = new Vector3f(0,0,0);
		Vector3f xAxis = new Vector3f(50,0,0);
		Vector3f yAxis = new Vector3f(0,50,0);
		Vector3f zAxis = new Vector3f(0,0,50);
		
		_attachAxis(origin, xAxis,ColorRGBA.Red);
		_attachAxis(origin, yAxis,ColorRGBA.Green);
		_attachAxis(origin, zAxis,ColorRGBA.Blue);
	}
	
	private void _drawWall(Wall wall){
		if (! wall.isVisible())
			return;
		Material material = _makeMaterial(wall);
		rootNode.attachChild(wall.toSpatial(material));
	}
	
	private void _drawGround(Ground gnd){
		if (! gnd.isVisible())
			return;
		Material material = _makeMaterial(gnd);
		rootNode.attachChild(gnd.toSpatial(material));
	}
	
	private void _drawRoof(Roof roof){
		if (! roof.isVisible())
			return;
		Material material = _makeMaterial(roof);
		rootNode.attachChild(roof.toSpatial(material));
	}
	
	private void drawEntity(Entity entity) {
		Node ent = new Node(entity.getUID());
		for (Primitive primitive : entity.getPrimitives()) {
			Material mat = _makeMaterial(primitive);
			ent.attachChild(primitive.toSpatial(mat));
		}
		rootNode.attachChild(ent);
	}
	
	private void drawPrimitive(Primitive primitive) {
		if (! primitive.isVisible()) 
			return;
		Material mat = _makeMaterial(primitive);
		rootNode.attachChild(primitive.toSpatial(mat));
	}
	
	/**
	 * Method used to generate one axe from start to end in a certain color
	 * @param start Start of the vector
	 * @param end End of the vector
	 * @param color Color of the vector
	 */
	private void _attachAxis(Vector3f start, Vector3f end,ColorRGBA color){		
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
	private void _updatePoint(Change change){
		Point point = (Point) change.getItem();
		Floor floor = this.controller.getCurrentFloor();
		rootNode.detachChildNamed(point.getUID());
		if (point.isSelected()){			
			Sphere mySphere = new Sphere(32,32, 1.0f);
		    Geometry sphere = new Geometry(point.getUID(), mySphere);
		    mySphere.setTextureMode(Sphere.TextureMode.Projected);
		    Material sphereMat = new Material(assetManager,"Common/MatDefs/Light/Lighting.j3md");
		    sphereMat.setBoolean("UseMaterialColors",true);    
		    sphereMat.setColor("Diffuse",new ColorRGBA(0.8f,0.9f,0.2f,0.5f));
		    sphereMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		    sphere.setMaterial(sphereMat);
		    sphere.setLocalTranslation(point.toVector3f().setZ((float) floor.getBaseHeight()));
		    rootNode.attachChild(sphere);
			for (Room room : point.getBoundRooms()){
				try {
					this.dao.refresh(room);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				for (Meshable meshable : room.getMeshables())
					_updateMeshable(Change.update(meshable));
			}
		}
	}
	
	/**
	 * Update view when a Meshable has changed
	 * @param change
	 */
	private void _updateMeshable(Change change){
		Meshable meshable = (Meshable) change.getItem();
		
		/* 3D object don't exist yet if it is a creation */
		if (! change.isCreation())
			rootNode.detachChildNamed(meshable.getUID());
		
		/* No need to redraw if it is a deletion */
		if (! change.isDeletion()){
			if (meshable instanceof Wall)
				_drawWall((Wall) meshable);
			else if (meshable instanceof Ground)
				_drawGround((Ground) meshable);
			else if (meshable instanceof Roof)
				_drawRoof((Roof) meshable);
			else if (meshable instanceof Primitive)
				drawPrimitive((Primitive) meshable);
		}
		
		/* Conclusion: updates will do both (detach & redraw) */
	}
	
	private void _updateFloor(Change change){
		Floor floor = (Floor) change.getItem();
		cleanScene();
		makeScene();
	}
	
	public boolean isCreated() {
		return this.isCreated;
	}

	public void setCreated() {
		this.isCreated = !this.isCreated;
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
	
	/**
	 * Called when the model fires a change notification
	 * Enqueues Changes, they should be applied in render thread
	 */
	@Override
	public void update(Observable obs, Object msg) {
		if (msg == null)
			return;
		if (obs instanceof GeometryDAO) {
			synchronized (this.queuedChanges) {
				this.queuedChanges.addAll((List<Change>) msg);
			}
		}
	}
	
	/**
	 * Modify scene in render thread, if any Change
	 */
	@Override
	public void simpleUpdate(float t){
		
		synchronized (this.queuedChanges){
			if (this.queuedChanges.size() > 0){
				for (Change change : this.queuedChanges){
					if (change.isDeletion())
						rootNode.detachChildNamed(change.getItem().getUID());
					else if (change.getItem() instanceof Meshable)
						_updateMeshable(change);
					else if (change.getItem() instanceof Point)
						_updatePoint(change);
					else if (change.getItem() instanceof Floor)
						_updateFloor(change);
				}		
				this.queuedChanges.clear();
			}
		}
	}

}
