/**
 * 
 */
package be.ac.ulb.infof307.g03.views;

import java.sql.SQLException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import be.ac.ulb.infof307.g03.controllers.WorldController;
import be.ac.ulb.infof307.g03.models.*;

import com.jme3.app.SimpleApplication;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Sphere;

/**
 * This class is a jMonkey canvas that can be added in a Swing GUI.
 * @author fhennecker, julianschembri, brochape, Titouan
 */
public class WorldView extends SimpleApplication implements Observer, ActionListener {	
	
	private GeometryDAO _model = null;
	private WorldController _controller; 
	protected Vector<Geometry> shapes = new Vector<Geometry>();
	
	static private final String _SELECTOBJECT 	= "SelectObject";

	/**
	 * Constructor of WorldView
	 * @param newController The view's controller
	 */
	public WorldView(WorldController newController, GeometryDAO model){
		super();
		_controller = newController;
		_model = model;
		_model.addObserver(this);
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
		_controller.getCameraModeController().setToRemove(this);
		
		//Change the default background
		viewPort.setBackgroundColor(ColorRGBA.White);
		
		//render the scene
		_makeScene();

		// listen for clicks on the canvas
		setInput();
		
		// Notify our controller that initialisation is done
		_controller.onViewCreated();
		this.setPauseOnLostFocus(false);
	}
	
	public InputManager getInputManager(){
		return inputManager;
	}
	
	private void setInput(){
		inputManager.addMapping(_SELECTOBJECT, new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
		inputManager.addListener(this, _SELECTOBJECT);
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
	 * Redraw the 3D scene (first shot, still to be optimized)
	 */
	private void _makeScene(){
		//Generates the grid
		attachGrid();
		
		//Generate the axes
		_attachAxes();
		
		try {
			for (Wall wall : _model.getWalls())
				_drawWall(wall);
			for (Ground gnd : _model.getGrounds())
				_drawGround(gnd);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
		try {
			Mesh mesh = _model.getWallAsMesh(wall);
			Geometry node = new Geometry(wall.getUID(), mesh);
			node.setMaterial(_makeBasicMaterial(_getColor(wall)));
			rootNode.attachChild(node);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void _drawGround(Ground gnd){
		try {
			Mesh mesh = _model.getGroundAsMesh(gnd);
			Geometry node = new Geometry(gnd.getUID(), mesh);
			node.setMaterial(_makeBasicMaterial(_getColor(gnd)));
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
	
	private ColorRGBA _getColor(Grouped grouped){
		return grouped.isSelected() ? 
				ColorRGBA.Green : 
				(grouped instanceof Wall) ? 
						ColorRGBA.Gray : 
						ColorRGBA.LightGray;
	}
	
	private void _updatePoint(Change change){
		Point point = (Point) change.getItem();
		
		if (change.isUpdate()){
			if (point.isSelected()){
				Geometry newSphere = new Geometry(point.getUID(), new Sphere(32, 32, 1.0f));
				newSphere.setLocalTranslation(point.toVector3f());
				newSphere.setMaterial(this._makeBasicMaterial(ColorRGBA.Red));
				rootNode.attachChild(newSphere);
			} else {
				rootNode.detachChildNamed(point.getUID());
			}
		}
	}
	
	private void _updateGrouped(Change change){
		Grouped grouped = (Grouped) change.getItem();
		
		if (change.isDeletion())
			rootNode.detachChildNamed(grouped.getUID());
		else if (change.isUpdate()){
			Geometry toUpdate = (Geometry) rootNode.getChild(grouped.getUID());
			Material mat = toUpdate.getMaterial();
			if (mat != null)
				mat.setColor("Color", _getColor(grouped));
		} else if (change.isCreation()){
			if (grouped instanceof Wall)
				_drawWall((Wall) grouped);
			else if (grouped instanceof Ground)
				_drawGround((Ground) grouped);
		}
	}
	
	/**
	 * Called when the model fires a change notification
	 */
	@Override
	public void update(Observable arg0, Object msg) {
		if (msg == null)
			return;
		for (Change change : (List<Change>) msg){
			System.out.println("[3D View] " + change.toString());
			if (change.getItem() instanceof Grouped)
				_updateGrouped(change);
			if (change.getItem() instanceof Point)
				_updatePoint(change);
		}
	}

	@Override
	public void onAction(String arg0, boolean arg1, float arg2) {
		if (arg0.equals(_SELECTOBJECT) && arg1){
            _controller.selectObject(inputManager.getCursorPosition());
        }	
	}

}
