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



import be.ac.ulb.infof307.g03.controllers.WorldController;
import be.ac.ulb.infof307.g03.models.*;
import be.ac.ulb.infof307.g03.utils.Log;

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
	
	private Project _project = null;
	private GeometryDAO _dao = null;
	private WorldController _controller; 
	private LinkedList<Change> _queuedChanges = null;
	protected Vector<Geometry> shapes = new Vector<Geometry>();
	private boolean _isCreated = false;
	
	
	/**
	 * WorldView's Constructor
	 * @param newController The view's controller
	 * @param model The DAO pattern model class
	 */
	public WorldView(WorldController newController, Project project){
		super();
		_controller = newController;
		_project = project;
		_queuedChanges = new LinkedList<Change>();
		try {
			_dao= project.getGeometryDAO();
			_dao.addObserver(this);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.setDisplayStatView(false);
		_project.addObserver(this);

	}
	
	/**
	 * @see com.jme3.app.SimpleApplication#simpleInitApp()
	 */
	@Override
	public void simpleInitApp() {
		// !!! cam only exist when this function is called !!!
		flyCam.setEnabled(false);
		// Add the attributes to the cameraModeController (only existing at this time)
		_controller.getCameraModeController().setCamera(cam);
		_controller.getCameraModeController().setInputManager(inputManager);
		_controller.getCameraModeController().setWorldView(this);
		// Update the camera mode
		_controller.getCameraModeController().updateMode();
		
		// Update the edition mode
		_controller.updateEditionMode();
		
		// Change the default background
		viewPort.setBackgroundColor(ColorRGBA.White);

		// listen for clicks on the canvas
		_controller.inputSetUp(inputManager);
		
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
		gridGeo.center().move(new Vector3f(0,-50,0));
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
	 * This creates a basic colored material which is going to be affected by lighting
	 * @param color
	 * @return The material created
	 */
	private Material _makeLightedMaterial(ColorRGBA color){
		Material res = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		res.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		res.setBoolean("UseMaterialColors", true);
		res.setColor("Diffuse", color);
		res.setColor("Ambient", color);
		res.setColor("Specular", color);
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
					for (Floor floor : _dao.getFloors()){
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
		Material material = _makeLightedMaterial(_getColor(wall));
		
		Texture red = assetManager.loadTexture("PineFull.png");	
        material.setTexture("DiffuseMap",red); 
        
		rootNode.attachChild(wall.toSpatial(material));
	}
	
	private void _drawGround(Ground gnd){
		if (! gnd.isVisible())
			return;
/*<<<<<<< HEAD
		try {
			Mesh mesh = _dao.getGroundAsMesh(gnd);
			Geometry node = new Geometry(gnd.getUID(), mesh);
			Material mat;
			mat=_makeBasicMaterial(_getColor(gnd));
			mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
			node.setMaterial(mat);
			rootNode.attachChild(node);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			//JOptionPane.showMessageDialog(null, "Not enough point to draw a ground.", "Error", JOptionPane.WARNING_MESSAGE);
			Log.log(Level.INFO, "User try to draw a wall with not enough point");
			//e.printStackTrace();
		}
=======*/
		Material mat = _makeBasicMaterial(_getColor(gnd));
		rootNode.attachChild(gnd.toSpatial(mat));
/*>>>>>>> refs/remotes/origin/merge-ref_models*/
	}
	
	private void _drawRoof(Roof roof){
		if (! roof.isVisible())
			return;
/*<<<<<<< HEAD
		try {
			Mesh mesh = _dao.getRoofAsMesh(roof);
			Geometry node = new Geometry(roof.getUID(), mesh);
			Material mat = _makeBasicMaterial(_getColor(roof));
			mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
			node.setMaterial(mat);
			rootNode.attachChild(node);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			Log.log(Level.INFO, "User tried to create a wall with not enough point");
		}
=======*/
		Material mat = _makeBasicMaterial(_getColor(roof));
		rootNode.attachChild(roof.toSpatial(mat));
/*>>>>>>> refs/remotes/origin/merge-ref_models*/
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
	 * @param meshable a Meshable item
	 * @return The color it should have in 3D view
	 * @throws SQLException 
	 */
	private ColorRGBA _getColor(Meshable meshable) {
		ColorRGBA color = ColorRGBA.Gray;
		if (meshable.isSelected()){
			color = new ColorRGBA(0f,1.2f,0f, 0.5f);
		}
		else if (meshable instanceof Ground) {
			color = ColorRGBA.LightGray;	
		}
		else if (meshable instanceof Roof){
			Roof roof = (Roof) meshable;
			int hash = roof.getRoom().getFloor().getId();
			double r = Math.sin(hash)/4 + 0.25;
			double g = Math.sin(hash + Math.PI/3)/4 + 0.25;
			double b = Math.sin(hash + 2*Math.PI/3)/4 + 0.25;
			color = new ColorRGBA((float)r, (float)g, (float)b, 0.3f);
		}
		return color;
	}
	
	/**
	 * Update view when a Point has changed
	 * @param change
	 */
	private void _updatePoint(Change change){
		Point point = (Point) change.getItem();
		Floor floor = _controller.getCurrentFloor();
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
					_dao.refresh(room);
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
		}
		
		/* Conclusion: updates will do both (detach & redraw) */
	}
	
	private void _updateFloor(Change change){
		Floor floor = (Floor) change.getItem();
		cleanScene();
		makeScene();
	}
	
	public boolean isCreated() {
		return _isCreated;
	}

	public void setCreated() {
		this._isCreated = !this._isCreated;
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
			synchronized (_queuedChanges) {
				_queuedChanges.addAll((List<Change>) msg);
			}
		}
	}
	
	/**
	 * Modify scene in render thread, if any Change
	 */
	@Override
	public void simpleUpdate(float t){
		
		synchronized (_queuedChanges){
			if (_queuedChanges.size() > 0){
				for (Change change : _queuedChanges){
					if (change.isDeletion())
						rootNode.detachChildNamed(change.getItem().getUID());
					else if (change.getItem() instanceof Meshable)
						_updateMeshable(change);
					else if (change.getItem() instanceof Point)
						_updatePoint(change);
					else if (change.getItem() instanceof Floor)
						_updateFloor(change);
				}		
				_queuedChanges.clear();
			}
		}
	}

}
