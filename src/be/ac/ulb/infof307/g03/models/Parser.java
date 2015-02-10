/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.util.Vector;

import com.jme3.math.Vector3f;

/**
 * @author brochape
 *
 */
public abstract class Parser {
	protected Vector<Vector3f> vertices;
	
	protected int[] indexes ;
	
	/**
	 * @return the vertices list
	 */
	public Vector<Vector3f> getVertices(){
		return vertices;
	}
	
	/**
	 * @return the order the vertices will be meshed with
	 */
	public int[] getIndexes(){
		return indexes;
	}
	

	
}
