/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * @author Titouan Christophe
 * @brief A line between two Points
 */
public class Line extends Shape {
	/**
	 * @brief First point of the line
	 */
	@DatabaseField(canBeNull = false, foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
	private Point _p1;
	/**
	 * @brief second point of the line
	 */
	@DatabaseField(canBeNull = false, foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
	private Point _p2;

	/**
	 * @brief Create the required tables for this model
	 * @param database
	 *            A databse connection
	 * @throws SQLException
	 */
	public static void migrate(ConnectionSource database) throws SQLException {
		TableUtils.createTableIfNotExists(database, Line.class);
		Point.migrate(database);
	}

	public Line() {
		super();
	}

	public Line(Point p1, Point p2) {
		super();
		_p1 = p1;
		_p2 = p2;
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

	public List<Point> getPoints() {
		List<Point> res = new ArrayList<Point>(2);
		res.add(_p1);
		res.add(_p2);
		return res;
	}

	@Override
	public Boolean equalsByContent(Shape other) {
		if (other.getClass() != Line.class)
			return false;
		List<Point> pOther = ((Line) other).getPoints();
		return _p1.equals(pOther.get(0)) && _p2.equals(pOther.get(1));
	}
}
