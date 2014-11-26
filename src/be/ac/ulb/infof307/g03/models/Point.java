/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.util.LinkedList;
import java.util.List;

import com.jme3.math.Vector3f;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Primitive for all geometric constructions
 * @author Titouan Christophe
 */
@DatabaseTable
public class Point extends Geometric {
	@DatabaseField(uniqueCombo = true)
	private double _x = 0;
	@DatabaseField(uniqueCombo = true)
	private double _y = 0;
	@DatabaseField(uniqueCombo = true)
	private double _z = 0;
	@DatabaseField
	private Boolean _selected = false;
	@ForeignCollectionField(eager = false, orderColumnName = "_room_id")
    private ForeignCollection<Binding> _bindings;
	
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
	 * @return x coordinate
	 */
	public double getX() {
		return this._x;
	}

	/**
	 * @return y coordinate
	 */
	public double getY() {
		return this._y;
	}

	/**
	 * @return z coordinate
	 */
	public double getZ() {
		return this._z;
	}
	
	/**
	 * Copy content of other into this point
	 * @param other A point to copy
	 */
	public void copyFrom(Point other){
		setId(other.getId());
		setX(other.getX());
		setY(other.getY());
		setZ(other.getZ());
	}

	/**
	 * Set coordinate
	 * @param x coordinate
	 */
	public void setX(double x) {
		this._x = x;
	}

	/**
	 * Set coordinate
	 * @param y coordinate
	 */
	public void setY(double y) {
		this._y = y;
	}

	/**
	 * Set coordinate
	 * @param z coordinate
	 */
	public void setZ(double z) {
		this._z = z;
	}
	
	/**
	 * Select point
	 */
	public void select(){
		_selected = true;
	}
	
	/**
	 * Deselect point
	 */
	public void deselect(){
		_selected = false;
	}
	
	/**
	 * Selects point if it is deselected
	 * Deselects point if it is selected
	 */
	public void toggleSelect(){
		_selected = !_selected;
	}
	
	/**
	 * Is the point selected?
	 * @return true if this Point is selected
	 */
	public Boolean isSelected(){
		return _selected;
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
	@Override
	public boolean equals(Object otherObject){
		if (otherObject == null || ! (otherObject instanceof Point))
			return false;
		Point other = (Point) otherObject;
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
	
	/**
	 * @return All bindings of this point
	 */
	public ForeignCollection<Binding> getBindings(){
		return _bindings;
	}
	
	/**
	 * @return All rooms bound to this point
	 */
	public List<Room> getBoundRooms(){
		List<Room> res = new LinkedList<Room>();
		if (_bindings != null){
			for (Binding b : _bindings)
				res.add(b.getRoom());
		}
		return res;
	}

	@Override
	public String getUIDPrefix() {
		return "pnt";
	}
}
