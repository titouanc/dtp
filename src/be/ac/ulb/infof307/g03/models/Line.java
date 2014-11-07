/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.util.ArrayList;
import java.util.List;
import com.j256.ormlite.field.DatabaseField;

/**
 * @author Titouan Christophe
 * @brief A line between two Points
 */
public class Line extends Shape {
	/**
	 * First point of the line
	 */
	@DatabaseField(canBeNull = false, foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
	private Point _p1 = new Point();
	/**
	 * Second point of the line
	 */
	@DatabaseField(canBeNull = false, foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
	private Point _p2 = new Point();

	/**
	 * Create a new Line with (0,0,0) points
	 */
	public Line() {
		super();
	}
	
	/**
	 * Create a new Line with 2 points
	 * @param p1 The first point
	 * @param p2 The second point
	 */
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

	/**
	 * @return A list of the 2 points constituting the line
	 */
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
	
	@Override
	public String toString(){
		return String.format("line %d", getId());
	}
}
