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
	/**
	 * @brief Point id
	 */
	@DatabaseField(generatedId = true)
	private int _id = 0;
	
	/**
	 * @brief first coordinate
	 */
	@DatabaseField(uniqueCombo = true)
	private double _x = 0;
	
	/**
	 * @brief second coordinate
	 */
	@DatabaseField(uniqueCombo = true)
	private double _y = 0;
	
	/**
	 * @brief third coordinate
	 */
	@DatabaseField(uniqueCombo = true)
	private double _z = 0;
		
	public Point() {
	}

	public Point(double x, double y, double z) {
		this._x = x;
		this._y = y;
		this._z = z;
	}

	public Point(Vector3f xyz) {
		this._x = xyz.x;
		this._y = xyz.y;
		this._z = xyz.z;
	}

	int getId() {
		return this._id;
	}

	double getX() {
		return this._x;
	}

	double getY() {
		return this._y;
	}

	double getZ() {
		return this._z;
	}
	
	void copyFrom(Point other){
		this._id = other.getId();
		setX(other.getX());
		setY(other.getY());
		setZ(other.getZ());
	}

	void setX(double x) {
		this._x = x;
	}

	void setY(double y) {
		this._y = y;
	}

	void setZ(double z) {
		this._z = z;
	}
	
	public String toString(){
		return String.format("(%f,%f,%f)", _x, _y, _z);
	}
	
	public Boolean equals(Point other){
		Boolean by_id = true;
		if (getId() != 0 && other.getId() != 0)
			by_id = other.getId() == getId();
		return by_id && other.getX() == getX() && other.getY() == getY() && other.getZ() == getZ();
	}
	
	public Vector3f toVector3f(){
		return new Vector3f((float) _x, (float) _y, (float) _z);
	}
}
