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

import javax.swing.JOptionPane;

import be.ac.ulb.infof307.g03.controllers.WorldController;
import be.ac.ulb.infof307.g03.models.*;

import com.jme3.app.SimpleApplication;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.TangentBinormalGenerator;

/**
 * This class is a jMonkey canvas that can be added in a Swing GUI.
 * @author fhennecker, julianschembri, brochape, Titouan,wmoulart
 */
public class WorldView extends SimpleApplication implements Observer {	
	
	private GeometryDAO _model = null;
	private WorldController _controller; 
	private LinkedList<Change> _queuedChanges = null;
	protected Vector<Geometry> shapes = new Vector<Geometry>();
	
	/**
	 * Constructor of WorldView
	 * @param newController The view's controller
	 * @param model The DAO pattern model class
	 */
	public WorldView(WorldController newController, GeometryDAO model){
		super();
		_controller = newController;
		_model = model;
		_model.addObserver(this);
		_queuedChanges = new LinkedList<Change>();
		this.setDisplayStatView(false);
	}
	
	/**
	 * @see com.jme3.app.SimpleApplication#simpleInitApp()
	 */
	@Override
	public void simpleInitApp() {
		flyCam.setEnabled(false);
		_controller.getCameraModeController().setCamera(cam);
		_controller.getCameraModeController().setInputManager(inputManager);
		_controller.getCameraModeController().setWorldView(this);
		
		//Change the default background
		viewPort.setBackgroundColor(ColorRGBA.White);
		
		//render the scene
		_makeScene();

		// listen for clicks on the canvas
		_controller.inputSetUp(inputManager);
		
		// Notify our controller that initialisation is done
		_controller.onViewCreated();
		this.setPauseOnLostFocus(false);
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
	 * Transform a Wall object into a Node containing Boxes (3D object usable in jMonkey)
	 * @param wall The wall to transform
	 * @return The Node
	 * @throws SQLException
	 */
	public Node getWallAsNode(Wall wall) throws SQLException{
		Node res = new Node(wall.getUID());
		List<Point> allPoints = _model.getPointsForShape(wall.getGroup());
		
		float height = (float) _model.getFloor(wall.getGroup()).getHeight();
		float elevation = (float) _model.getBaseHeight(_model.getFloor(wall.getGroup()));
		
		for (int i=0; i<allPoints.size()-1; i++){
			// 1) Build a box the right length, width and height
			Vector3f a = allPoints.get(i).toVector3f();
			Vector3f b = allPoints.get(i+1).toVector3f();
			float w = (float) wall.getWidth();
			Vector2f segment = new Vector2f(b.x-a.x, b.y-a.y);
			Box box = new Box(	new Vector3f(-w/2,-w/2,elevation), new Vector3f(segment.length()+w/2, 
																		w/2, elevation+height));
			Geometry wallGeometry = new Geometry(wall.getUID(), box);
			wallGeometry.setMaterial(_makeLightedMaterial(wall.isSelected() ? new ColorRGBA(0f,1.2f,0f, 0.5f) : ColorRGBA.Gray));
			// 2) Place the wall at the right place
			wallGeometry.setLocalTranslation(a);
			 
			// 3) Rotate the wall at the right orientation
			Quaternion rot = new Quaternion();
			rot.fromAngleAxis(-segment.angleBetween(new Vector2f(1,0)), new Vector3f(0,0,1));
			wallGeometry.setLocalRotation(rot);
			
			// 4) Attach it to the node
			res.attachChild(wallGeometry);
		}
		return res;
	}

	/**
	 * Redraw the entire 3D scene
	 */
	private void _makeScene(){
		//Generates the grid
		attachGrid();
		
		//Generate the axes
		_attachAxes();
		
		// Add a bit of sunlight into our lives
		_addSun();
		
		try {
			for (Wall wall : _model.getWalls())
				_drawWall(wall);
			for (Ground gnd : _model.getGrounds())
				_drawGround(gnd);
			for (Roof roof : _model.getRoofs())
				_drawRoof(roof);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
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
		try {
			rootNode.attachChild(getWallAsNode(wall));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void _drawGround(Ground gnd){
		if (! gnd.isVisible())
			return;
		try {
			Mesh mesh = _model.getGroundAsMesh(gnd);
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
			JOptionPane.showMessageDialog(null, "Not enough point to draw a ground.", "Error", JOptionPane.WARNING_MESSAGE);
			System.out.println("[DEBUG] User try to draw a wall with not enough point");
			//e.printStackTrace();
		}
	}
	
	private void _drawRoof(Roof roof){
		if (! roof.isVisible())
			return;
		try {
			Mesh mesh = _model.getRoofAsMesh(roof);
			Material mat;
			Geometry node = new Geometry(roof.getUID(), mesh);
			mat=_makeBasicMaterial(_getColor(roof));
			mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
			node.setMaterial(mat);
			rootNode.attachChild(node);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AssertionError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	 * @param grouped a Grouped item
	 * @return The color it should have in 3D view
	 * @throws SQLException 
	 */
	private ColorRGBA _getColor(Grouped grouped) throws SQLException{
		ColorRGBA color;
		if (grouped.isSelected()){
			color=new ColorRGBA(0f,1.2f,0f, 0.5f);
		}
		else if (grouped instanceof Wall){
			color=new ColorRGBA(0f,1.2f,0f, 0.5f);
		}
		else if (grouped instanceof Roof){
			int choice=_model.getFloors().size()%3;
			float nbFloor=(float) ((_model.getFloors().size()/10)+0.1);
			if (choice==1){
				color=new ColorRGBA(nbFloor,0f,0f, 0.5f);
			}
			else if (choice==2){
				color=new ColorRGBA(0f,nbFloor,0f, 0.5f);
			}
			else{
				color=new ColorRGBA(0f,0f,nbFloor, 0.5f);
			}
		}
		else{
			color=ColorRGBA.LightGray;	
		}
		return color;
	}
	
	/**
	 * Update view when a Point has changed
	 * @param change
	 */
	private void _updatePoint(Change change){
		Point point = (Point) change.getItem();
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
		    sphere.setLocalTranslation(point.toVector3f());
		    rootNode.attachChild(sphere);
			try {
				for (Grouped grouped : _model.getGroupedForPoint(point))
					_updateGrouped(Change.update(grouped));
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Update view when a Grouped has changed
	 * @param change
	 */
	private void _updateGrouped(Change change){
		Grouped grouped = (Grouped) change.getItem();
		
		/* 3D object don't exist yet if it is a creation */
		if (! change.isCreation())
			rootNode.detachChildNamed(grouped.getUID());
		
		/* No need to redraw if it is a deletion */
		if (! change.isDeletion()){
			if (grouped instanceof Wall)
				_drawWall((Wall) grouped);
			else if (grouped instanceof Ground)
				_drawGround((Ground) grouped);
			else if (grouped instanceof Roof)
				_drawRoof((Roof) grouped);
		}
		
		/* Conclusion: updates will do both (detach & redraw) */
	}
	
	private void _updateFloor(Change change){
		Floor floor = (Floor) change.getItem();
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
	public void update(Observable arg0, Object msg) {
		if (msg == null)
			return;
		synchronized (_queuedChanges) {
			_queuedChanges.addAll((List<Change>) msg);
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
					if (change.getItem() instanceof Grouped)
						_updateGrouped(change);
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
