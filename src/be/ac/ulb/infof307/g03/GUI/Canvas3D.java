/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

import java.sql.SQLException;

import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.models.Geometry;
import be.ac.ulb.infof307.g03.models.Shape;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Mesh;
import com.jme3.scene.shape.Box;

/**
 * This class is a jMonkey canvas that can be added in a Swing GUI.
 * @author fhennecker
 */
public class Canvas3D extends SimpleApplication {
	private Project _project;
	
	public Canvas3D(Project proj){
		super();
		_project = proj;
	}

	/**
	 * @see com.jme3.app.SimpleApplication#simpleInitApp()
	 */
	@Override
	public void simpleInitApp() {
		try {
			flyCam.setDragToRotate(true);
			Geometry geo = _project.getGeometry();
			
			//Retrieve a shape from the model...
			Shape firstTopLevelShape = geo.getRootNodes().get(0);
			
			Mesh mesh = geo.getShapeAsMesh(firstTopLevelShape, 5);
			com.jme3.scene.Geometry walls = new com.jme3.scene.Geometry("Walls", mesh);
			
			Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
			mat.setColor("Color", ColorRGBA.Blue);
			mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
			
			walls.setMaterial(mat);
			rootNode.attachChild(walls);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
