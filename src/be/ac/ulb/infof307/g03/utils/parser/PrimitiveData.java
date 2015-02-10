package be.ac.ulb.infof307.g03.utils.parser;

import java.awt.List;
import java.util.Vector;

import com.jme3.math.Vector3f;

/**
 * @author brochape
 *
 */
public class PrimitiveData {

	protected Vector<Vector3f> vertices = new Vector<Vector3f>();
	protected Vector<Integer> indexes = new Vector<Integer>();
	protected String name = null;

	public void appendVertex(Vector3f vertex) {
		vertices.addElement(vertex);
	}
	
	public void appendIndex(int index) {
		indexes.addElement(index);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the vertices list
	 */
	public Vector3f [] getVertices(){
		return (Vector3f[]) vertices.toArray();
	}
	
	/**
	 * @return the normals list
	 */
	public int[] getIndexes(){
		int[] ret = new int[indexes.size()];
	    int i = 0;
	    for (Integer e : indexes)  
	        ret[i++] = e.intValue();
	    return ret;
	}

	
}