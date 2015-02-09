/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.awt.List;

/**
 * @author brochape
 *
 */
public abstract class Parser {
	private List vertices;
	
	private List normals;
	/**
	 * @return the vertices list
	 */
	public List getVertices(){
		return vertices;
	}
	
	/**
	 * @return the normals list
	 */
	public List getNormals(){
		return normals;
	}

	
}
