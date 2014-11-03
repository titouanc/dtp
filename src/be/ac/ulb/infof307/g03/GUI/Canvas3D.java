/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 * This class is a jMonkey canvas that can be added in a Swing GUI.
 * @author fhennecker
 */
public class Canvas3D extends SimpleApplication {

	/**
	 * @see com.jme3.app.SimpleApplication#simpleInitApp()
	 */
	@Override
	public void simpleInitApp() {
		flyCam.setDragToRotate(true);
		Box box = new Box(1, 1, 1);
		Geometry cube = new Geometry("Cube", box);
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		cube.setMaterial(mat);
		rootNode.attachChild(cube);
	}
}
