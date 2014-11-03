/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.input.KeyInput;;
/**
 * @author fhennecker
 *
 */
public class Canvas3D extends SimpleApplication {

	/* (non-Javadoc)
	 * @see com.jme3.app.SimpleApplication#simpleInitApp()
	 */
	
	private CameraNode camNode;
	
	@Override
	public void simpleInitApp() {
		Box box = new Box(1, 1, 1);
		flyCam.setDragToRotate(true);
		Geometry cube = new Geometry("Cube", box);
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		cube.setMaterial(mat);
		rootNode.attachChild(cube);
		To2D();
	}
	
	
	
	public void To3D(){	
	}
	public void To2D(){
		float high = 10;
		cam.setLocation(new Vector3f(0, 0, 0));
		cam.setParallelProjection(true);
		float aspect = (float) cam.getWidth() / cam.getHeight();
		cam.setFrustum(-1000, 1000, -aspect * high, aspect * high, high, -high);
		Quaternion rot = new Quaternion(new float[] { FastMath.DEG_TO_RAD * 35.264f, FastMath.QUARTER_PI, 0 });
		getCamera().setRotation(rot);
		camNode = new CameraNode("camNode",cam);
		camNode.setLocalTranslation(0, 0, -100);
	}
	

}
