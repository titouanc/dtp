/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

import java.util.Vector;

import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

/**
 * @author fhennecker
 *
 */
public class WorldController {
	
	WorldView view;
	
	protected Vector<Geometry> shapes = new Vector<Geometry>();
	
	WorldController(){
		view = new WorldView(this);
		createDemoGeometry();
	}
	
	public void createDemoGeometry(){
		
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

		System.out.println("ICI");
		Geometry walls = new Geometry("Walls",wallsMesh);
		Material mat = new Material(view.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
		mat.setColor("Color", ColorRGBA.Blue);
		walls.setMaterial(mat);
		shapes.add(walls);
		view.getRootNode().attachChild(walls);
		
		Geometry ground = new Geometry("Groundmesh",groundMesh);
		Material mat2 = new Material(view.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		mat2.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
		mat2.setColor("Color", ColorRGBA.Red);
		ground.setMaterial(mat2);
		shapes.add(ground);
		view.getRootNode().attachChild(ground);
	}
}
