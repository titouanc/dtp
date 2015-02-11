/**
 * 
 */
package be.ac.ulb.infof307.g03.utils.parser;

import java.awt.List;
import java.util.Vector;

import com.jme3.math.Vector3f;

/**
 * @author brochape
 *
 */
public abstract class Parser {
	protected Vector<Vector3f> vertices;
	int [] indexes;
	
	/**
	 * @return the vertices list
	 */
	public Vector<Vector3f> getVertices(){
		return vertices;
	}
	
	/**
	 * @return the normals list
	 */
	public int[] getIndexes(){
		return indexes;
	}

	
}
