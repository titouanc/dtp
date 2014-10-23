/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.sql.SQLException;

import javax.vecmath.Vector3f;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import com.j256.ormlite.support.ConnectionSource;

/**
 * @author Titouan Christophe
 * @brief Primitive saveable object for 3D building
 */
@DatabaseTable()
public class Point {
	/**
	 * @brief Point id
	 */
	@DatabaseField(generatedId = true)
	private int _id = 0;
	
	/**
	 * @brief first coordinate
	 */
	@DatabaseField()
	private double _x = 0;
	
	/**
	 * @brief second coordinate
	 */
	@DatabaseField()
	private double _y = 0;
	
	/**
	 * @brief third coordinate
	 */
	@DatabaseField()
	private double _z = 0;
	
	public static void migrate(ConnectionSource database) throws SQLException {
		TableUtils.createTableIfNotExists(database, Point.class);
	}
	
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

	void setX(double x) {
		this._x = x;
	}

	void setY(double y) {
		this._y = y;
	}

	void setZ(double z) {
		this._z = z;
	}
}
