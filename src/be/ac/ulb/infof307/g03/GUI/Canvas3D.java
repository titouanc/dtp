/**
 * 
 */
package be.ac.ulb.infof307.g03.GUI;

import java.sql.SQLException;

import be.ac.ulb.infof307.g03.models.Project;
import be.ac.ulb.infof307.g03.models.GeometryDAO;
import be.ac.ulb.infof307.g03.models.Shape;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Mesh;
import com.jme3.scene.Geometry;

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
			
			//Obtain a Data Access Object on geometric models
			GeometryDAO dao = _project.getGeometryDAO();
			
			Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
			mat.setColor("Color", ColorRGBA.Blue);
			mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
			
			for (Shape shape : dao.getRootNodes()){
				Mesh mesh = dao.getShapeAsMesh(shape, 5);
				Geometry walls = new Geometry(shape.toString(), mesh);
				walls.setMaterial(mat);
				rootNode.attachChild(walls);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
