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
	 * @return
	 */
	public abstract List getVertices();
	
	/**
	 * @return
	 */
	public abstract List getNormals();

}
