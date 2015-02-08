/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

/**
 * @author Bruno
 *
 */
public class A3DSObject {
    private String name;
    private float[] vertices;
    private short[] polygons;
    //private float[] textureCoordinates; TOTO : later

    /**
     * @param name
     */
    public A3DSObject(String name) {
        this.name = name;
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

	/**
	 * @return
	 */
	public float[] getVertices() {
		return vertices;
	}

	/**
	 * @param vertices
	 */
	public void setVertices(float[] vertices) {
		this.vertices = vertices;
	}

	/**
	 * @return the polygons
	 */
	public short[] getPolygons() {
		return polygons;
	}

	/**
	 * @param polygons the polygons to set
	 */
	public void setPolygons(short[] polygons) {
		this.polygons = polygons;
	}
}
