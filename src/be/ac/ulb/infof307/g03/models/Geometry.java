/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.dao.CloseableIterable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

/**
 * @author Titouan Christophe
 * The geometry class is a DAO for all geometry related models.
 */
public class Geometry {
	private Dao<Line, Integer> _lines = null;
	private Dao<Group, Integer> _groups = null;
	private Dao<Point, Integer> _points = null;
	
	/**
	 * Migrate all needed tables to a database
	 * @param database The database connection
	 * @throws SQLException
	 */
	public static void migrate(ConnectionSource database) throws SQLException{
		Line.migrate(database);
		Point.migrate(database);
		Group.migrate(database);
	}
	
	public Geometry(ConnectionSource database) throws SQLException{
		_lines = DaoManager.createDao(database, Line.class);
		_groups = DaoManager.createDao(database, Group.class);
		_points = DaoManager.createDao(database, Point.class);
	}
	
	/**
	 * Create a shape in the database
	 * @param shape
	 * @return
	 * @throws SQLException
	 */
	public int create(Shape shape) throws SQLException{
		if (shape.getClass() == Line.class)
			return _lines.create((Line) shape);
		else if (shape.getClass() == Group.class)
			return _groups.create((Group) shape);
		return 0;
	}
	
	public int update(Shape shape) throws SQLException {
		if (shape.getClass() == Line.class)
			return _lines.update((Line) shape);
		else if (shape.getClass() == Group.class)
			return _groups.update((Group) shape);
		return 0;
	}
	
	public int delete(Shape shape) throws SQLException {
		if (shape.getClass() == Line.class)
			return _lines.delete((Line) shape);
		else if (shape.getClass() == Group.class)
			return _groups.delete((Group) shape);
		return 0;
	}
	
	public Line getLine(int line_id) throws SQLException{
		return _lines.queryForId(line_id);
	}
	
	public Group getGroup(int group_id) throws SQLException{
		return _groups.queryForId(group_id);
	}
	
	public List<Shape> getShapesForGroup(Group grp) throws SQLException{
		List<Shape> res = new LinkedList<Shape>();
		res.addAll(_lines.queryForEq("_group_id", grp.getId()));
		res.addAll(_groups.queryForEq("_group_id", grp.getId()));
		return res;
	}
	
	public List<Point> getPointsForShape(Shape shape) throws SQLException{
		if (shape.getClass() == Line.class){
			Line l = (Line) shape;
			return l.getPoints();
		}
		
		List<Point> res = new LinkedList<Point>();
		if (shape.getClass() == Group.class)
			for (Shape s : getShapesForGroup((Group) shape))
				for (Point p : getPointsForShape(s))
					if (res.isEmpty() || ! p.equals(res.get(res.size() - 1)))
						res.add(p);
		return res;
	}
	
	public void addShapeToGroup(Group grp, Shape shape) throws SQLException{
		shape.addToGroup(grp);
		if (shape.getId() != 0)
			update(shape);
		else
			create(shape);
	}
}
