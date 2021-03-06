/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.jme3.math.Vector3f;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Primitive for all geometric constructions
 * @author Titouan Christophe
 */
@DatabaseTable(daoClass=GeometricDAO.class)
public class Point extends Geometric {
	@DatabaseField(uniqueCombo = true)
	private double x = 0;
	@DatabaseField(uniqueCombo = true)
	private double y = 0;
	@DatabaseField(uniqueCombo = true)
	private double z = 0;
	@ForeignCollectionField(eager = false, orderColumnName = "room_id")
    private ForeignCollection<Binding> bindings;
	
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
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Create a new point with given coordinates
	 * @param xyz Coordinates in {x,y,z} format
	 */
	public Point(Vector3f xyz) {
		this.x = xyz.x;
		this.y = xyz.y;
		this.z = xyz.z;
	}

	/**
	 * @return x coordinate
	 */
	public double getX() {
		return this.x;
	}

	/**
	 * @return y coordinate
	 */
	public double getY() {
		return this.y;
	}

	/**
	 * @return z coordinate
	 */
	public double getZ() {
		return this.z;
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
		this.x = x;
	}

	/**
	 * Set coordinate
	 * @param y coordinate
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * Set coordinate
	 * @param z coordinate
	 */
	public void setZ(double z) {
		this.z = z;
	}
	
	/**
	 * Set the point position
	 * @param v Vec3f 
	 */
	public void setPosition(Vector3f v){
		this.x = v.getX();
		this.y = v.getY();
		this.z = v.getZ();
	}
	
	public String toString(){
		return String.format("(%f,%f,%f)", this.x, this.y, this.z);
	}
	
	/**
	 * Compare two points for equality
	 * @note two points are == if X1Y1Z1 == X2Y2Z2. If they have Id's, they should be == too.
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
		return new Vector3f((float) this.x, (float) this.y, (float) this.z);
	}
	
	/**
	 * @return All bindings of this point
	 */
	public ForeignCollection<Binding> getBindings(){
		return this.bindings;
	}
	
	/**
	 * @return All rooms bound to this point
	 */
	public List<Room> getBoundRooms(){
		List<Room> res = new LinkedList<Room>();
		if (this.bindings != null){
			for (Binding b : this.bindings)
				res.add(b.getRoom());
		}
		return res;
	}

	@Override
	public String getUIDPrefix() {
		return "pnt";
	}
	
	
	/**
	 * Create a query to find a point near this, within maximum distance
	 * @param dao A Data Access Object to build the query
	 * @param maxDistance The maximum distance for a valid point
	 * @return A query, executable in given DAO
	 * @throws SQLException
	 */
	public final PreparedQuery<Point> getQueryForNear(Dao<Point, Integer> dao, double maxDistance) throws SQLException {
		double xmin = this.getX() - maxDistance, xmax = this.getX() + maxDistance;
		double ymin = this.getY() - maxDistance, ymax = this.getY() + maxDistance;
		double zmin = this.getZ() - maxDistance, zmax = this.getZ() + maxDistance;
	
		return dao.queryBuilder().where().
				ge("x", xmin).and().le("x", xmax).and().
				ge("y", ymin).and().le("y", ymax).and().
				ge("z", zmin).and().le("z", zmax).prepare();
	}
}
