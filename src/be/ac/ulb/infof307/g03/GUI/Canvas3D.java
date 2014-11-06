/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

import java.util.Vector;
 
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

/**
 * This class is a jMonkey canvas that can be added in a Swing GUI.
 * @author fhennecker, julianschembri, brochape
 */
public class Canvas3D extends SimpleApplication {	
	
	protected Geometry meshes;
	protected Geometry ground;
	protected Vector<Geometry> shapes = new Vector<Geometry>();
	
	private boolean _freeCam = false;
	private Camera2D _cam2D = new Camera2D();
	private Camera3D _cam3D = new Camera3D();
	private CameraController _cameraController = new CameraController(_cam2D,_cam3D);;
	
	public Canvas3D() {
		super();
		this.setDisplayStatView(false);
	}
	
	/**
	 * @see com.jme3.app.SimpleApplication#simpleInitApp()
	 */
	@Override
	public void simpleInitApp() {
		flyCam.setEnabled(false);
		_cam2D.setCam(cam);
		_cam3D.setCam(cam);
		_cam2D.setInputManager(inputManager);
		_cam3D.setInputManager(inputManager);
		
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

		Mesh mesh = new Mesh();
		mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
		mesh.setBuffer(Type.Index,    3, BufferUtils.createIntBuffer(order));
		mesh.updateBound();

		int[] groundOrder = new int[(n-2)*3];

		for(int j = 0; j< n-2;++j){
			groundOrder[3*j] = 0;
			groundOrder[3*j+1] = j+2;
			groundOrder[3*j+2] = j+1;      
		}

		Mesh groundMesh = new Mesh();
		groundMesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
		groundMesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(groundOrder));

		meshes = new Geometry("Mesh",mesh);
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
		mat.setColor("Color", ColorRGBA.Blue);
		meshes.setMaterial(mat);
		shapes.add(meshes);
		rootNode.attachChild(meshes);

		ground = new Geometry("Groundmesh",groundMesh);
		Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat2.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
		mat2.setColor("Color", ColorRGBA.Red);
		ground.setMaterial(mat2);
		shapes.add(ground);
		rootNode.attachChild(ground);
		
	}
	
	public CameraController getCameraController(){
		return _cameraController;
	}

}
