/**
 * 
 */
package be.ac.ulb.infof307.g03.utils.parser;

import java.util.Vector;

import com.jme3.math.Vector3f;

/**
 * @author Bruno
 *
 */
public class A3DSObject {
    private String name;
    private Vector<Vector3f> vertices;
    private int[] polygons;
    //private float[] textureCoordinates; TOTO : later

    /**
     * @param name
     */
    public A3DSObject(String name) {
        this.name = name;
    }

    /**
     * @return The object name
     */
    public String getName() {
        return name;
    }

	/**
	 * @return The vector of vertices
	 */
	public Vector<Vector3f> getVertices() {
		return vertices;
	}

	/**
	 * @param vertices
	 */
	public void setVertices(Vector<Vector3f> vertices) {
		this.vertices = vertices;
	}

	/**
	 * @return the polygons
	 */
	public int[] getPolygons() {
		return polygons;
	}

	/**
	 * @param faces the polygons to set
	 */
	public void setPolygons(int[] faces) {
		this.polygons = faces;
	}
}
