/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.sql.SQLException;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

/**
 * @author Titouan Christophe
 * @brief A line between two Points
 */

@DatabaseTable()
public class Line implements Shape {
	@DatabaseField(generatedId = true)
	private int _id;

	@DatabaseField(canBeNull = false, foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
	private Point _p1;
	@DatabaseField(canBeNull = false, foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
	private Point _p2;

	public static void migrate(ConnectionSource database) throws SQLException {
		TableUtils.createTableIfNotExists(database, Line.class);
		Point.migrate(database);
	}
	
	public Line() {
	}

	public Line(Point p1, Point p2) {
		_p1 = p1;
		_p2 = p2;
	}

	/**
	 * @return The line's ID
	 */
	int getId() {
		return _id;
	}

	/**
	 * @return Length of the line (distance between two points)
	 */
	public double length() {
		double dx = _p1.getX() - _p2.getX();
		double dy = _p1.getY() - _p2.getY();
		double dz = _p1.getZ() - _p2.getZ();
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}
}
