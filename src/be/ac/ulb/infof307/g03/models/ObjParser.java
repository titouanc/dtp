/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.awt.List;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Spatial;

/**
 * @author Bruno
 *
 */
public class ObjParser extends Parser {

	/* (non-Javadoc)
	 * @see be.ac.ulb.infof307.g03.models.Parser#getVertices()
	 */
	public ObjParser(String filename){
		//Spatial teapot = assetManager.loadModel("Models/Teapot/Teapot.obj");
		//-> access Vertices and normals or import directly?
		// TODO : Material mat_default = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
	}
	
	@Override
	public List getVertices() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see be.ac.ulb.infof307.g03.models.Parser#getNormals()
	 */
	@Override
	public List getNormals() {
		// TODO Auto-generated method stub
		return null;
	}
	
    
    

}
