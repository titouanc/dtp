/**
 * 
 */
package be.ac.ulb.infof307.g03.views;

import java.sql.SQLException;
import java.util.Observable;
import java.util.Observer;
import java.io.IOException;
import java.util.Vector;

import be.ac.ulb.infof307.g03.controllers.WorldController;
import be.ac.ulb.infof307.g03.models.*;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.shape.Line;
import com.jme3.util.BufferUtils;
import com.jme3.util.SkyFactory;

/**
 * This class is a jMonkey canvas that can be added in a Swing GUI.
 * @author fhennecker, julianschembri, brochape
 */
public class WorldView extends SimpleApplication implements Observer {	
	
	private GeometryDAO _model = null;
	private WorldController _controller; 
	protected Vector<Geometry> shapes = new Vector<Geometry>();

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

		//Generates the grid
		attachGrid();
		
		//Generate the axes
		attachAxes();
		
		//Sets up the demo project
		createDemoGeometry();
		
		//Change the default background
		viewPort.setBackgroundColor(ColorRGBA.White);
		_makeScene();
		// Notify our controller that initialisation is done
		_controller.onViewCreated();
	}

	
	/**
	 * Creates a world demo
	 */
	public void createDemoGeometry() {
		
		Vector3f [] vertices = new Vector3f[8];
		vertices[0] = new Vector3f(0,0,0);
		vertices[1] = new Vector3f(-1,1,0);
		vertices[2] = new Vector3f(0,6,0);
		vertices[3] = new Vector3f(3,5,0);
		vertices[4] = new Vector3f(0,0,2);
		vertices[5] = new Vector3f(-1,1,2);
		vertices[6] = new Vector3f(0,6,2);
		vertices[7] = new Vector3f(3,5,2);


		int n = 4;
		int[] order = new int[(n-1)*2*3];//4 points -> 3 walls; each wall -> 2 triangles; each triangle ->3 points
		int i;
		for(i=0;i<(n-1);++i){
			order[6*i] = i;
			order[6*i+1] = i+1;
			order[6*i+2] = i+n;
			order[6*i+3] = i+1;
			order[6*i+4] = i+n+1;
			order[6*i+5] = i+n;
		}

		Mesh wallsMesh = new Mesh();
		wallsMesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
		wallsMesh.setBuffer(Type.Index,    3, BufferUtils.createIntBuffer(order));
		wallsMesh.updateBound();

		int[] groundOrder = new int[(n-2)*3];

		for(int j = 0; j< n-2;++j){
			groundOrder[3*j] = 0;
			groundOrder[3*j+1] = j+2;
			groundOrder[3*j+2] = j+1;      
		}

		Mesh groundMesh = new Mesh();
		groundMesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
		groundMesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(groundOrder));

		Geometry walls = new Geometry("Walls",wallsMesh);
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
		mat.setColor("Color", ColorRGBA.Gray);
		walls.setMaterial(mat);
		rootNode.attachChild(walls);
		
		Geometry ground = new Geometry("Groundmesh",groundMesh);
		Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat2.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
		mat2.setColor("Color", ColorRGBA.LightGray);
		ground.setMaterial(mat2);
		shapes.add(ground);
		rootNode.attachChild(ground);
		
		
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

		try {
			for (Wall wall : _model.getWalls()){
				Mesh mesh = _model.getWallAsMesh(wall);
				Geometry node = new Geometry(wall.toString(), mesh);
				node.setMaterial(_makeBasicMaterial(ColorRGBA.Gray));
				rootNode.attachChild(node);
				System.out.println("Rendering " + wall.toString());
			}
			for (Ground gnd : _model.getGrounds()){
				Mesh mesh = _model.getGroundAsMesh(gnd);
				Geometry node = new Geometry(gnd.toString(), mesh);
				node.setMaterial(_makeBasicMaterial(ColorRGBA.LightGray));
				rootNode.attachChild(node);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Method used to generate the XYZ Axes
	 */
	private void attachAxes(){
		Vector3f origin = new Vector3f(0,0,0);
		Vector3f xAxis = new Vector3f(50,0,0);
		Vector3f yAxis = new Vector3f(0,50,0);
		Vector3f zAxis = new Vector3f(0,0,50);
		
		attachAxis(origin, xAxis,ColorRGBA.Red);
		attachAxis(origin, yAxis,ColorRGBA.Green);
		attachAxis(origin, zAxis,ColorRGBA.Blue);
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
	 * Called when the model fires a change notification
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		_makeScene();
	}

	

}
