/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import com.jme3.math.Vector3f;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author Titouan Christophe
 * @brief Primitive saveable object for 3D building
 */
@DatabaseTable
public class Point implements Geometric {
	@DatabaseField(generatedId = true)
	private int _id = 0;
	@DatabaseField(uniqueCombo = true)
	private double _x = 0;
	@DatabaseField(uniqueCombo = true)
	private double _y = 0;
	@DatabaseField(uniqueCombo = true)
	private double _z = 0;
	
	/**
	 * Create a new (0,0,0) point
	 */
	public Point() {
	}

	/**
	 * Create a new point with given coordinates
	 * @param x coordinate
	 * @param y coordinate
	 * @param z coordinate
	 */
	public Point(double x, double y, double z) {
		this._x = x;
		this._y = y;
		this._z = z;
	}

	/**
	 * Create a new point with given coordinates
	 * @param xyz Coordinates in {x,y,z} format
	 */
	public Point(Vector3f xyz) {
		this._x = xyz.x;
		this._y = xyz.y;
		this._z = xyz.z;
	}

	/**
	 * @return Identifier of this point
	 */
	int getId() {
		return this._id;
	}

	/**
	 * @return x coordinate
	 */
	double getX() {
		return this._x;
	}

	/**
	 * @return y coordinate
	 */
	double getY() {
		return this._y;
	}

	/**
	 * @return z coordinate
	 */
	double getZ() {
		return this._z;
	}
	
	/**
	 * Copy content of other into this point
	 * @param other A point to copy
	 */
	void copyFrom(Point other){
		this._id = other.getId();
		setX(other.getX());
		setY(other.getY());
		setZ(other.getZ());
	}

	/**
	 * Set coordinate
	 * @param x coordinate
	 */
	void setX(double x) {
		this._x = x;
	}

	/**
	 * Set coordinate
	 * @param y coordinate
	 */
	void setY(double y) {
		this._y = y;
	}

	/**
	 * Set coordinate
	 * @param z coordinate
	 */
	void setZ(double z) {
		this._z = z;
	}
	
	public String toString(){
		return String.format("(%f,%f,%f)", _x, _y, _z);
	}
	
	/**
	 * Compare two points for equality
	 * @note two points are == if X1Y1Z1 == X2Y2Z2. If they have Id's, they should be == too.
	 * @param other Another point
	 * @return True if the two points are actually the same
	 */
	public Boolean equals(Point other){
		Boolean by_id = true;
		if (getId() != 0 && other.getId() != 0)
			by_id = other.getId() == getId();
		return by_id && other.getX() == getX() && other.getY() == getY() && other.getZ() == getZ();
	}
	
	/**
	 * Convert a point to a Vector3f (type used in jMonkey)
	 * @return The point as Vector3f
	 */
	public Vector3f toVector3f(){
		return new Vector3f((float) _x, (float) _y, (float) _z);
	}
	
	public Boolean isLeaf() {
		return true;
	}
}
