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
	
	public static void migrate(ConnectionSource database) throws SQLException{
		ShapeRecord.migrate(database);
		Line.migrate(database);
		Point.migrate(database);
		Group.migrate(database);
	}
	
	public Geometry(ConnectionSource database) throws SQLException{
		_lines = DaoManager.createDao(database, Line.class);
		_groups = DaoManager.createDao(database, Group.class);
	}
	
	/**
	 * @return an iterator on all the Lines in the database
	 */
	public CloseableIterable<Line> getLines(){
		return _lines;
	}
	
	/**
	 * Add a new Line to the database
	 * @param newLine The line to add
	 * @return the inserted line, its line ID is now set
	 * @throws SQLException
	 */
	public Line addLine(Line newLine) throws SQLException{
		_lines.create(newLine);
		return newLine;
	}
	
	public Group createGroup() throws SQLException {
		Group res = new Group();
		_groups.create(res);
		return _groups.queryForSameId(res);
	}
	
	/**
	 * Return all points constituing given ShapeRecord
	 * @param shapeId The Shape unique ID
	 * @return All points for this Shape
	 */
	public List<Point> getPointsForShape(int shapeId){
		Shape shape = getShape(shapeId);
		
		if (shape.getClass() == Line.class){
			Line l = (Line) shape;
			return l.getPoints();
		}
		
		List<Point> res = new LinkedList<Point>();
		if (shape.getClass() == Group.class){
			Group g = (Group) shape;
			for (ShapeRecord subshape : g.getShapes()){
				int start_at = 0;
				List<Point> subpoints = getPointsForShape(subshape.getId());
				while (res.size() > 0 && start_at < subpoints.size() && subpoints.get(start_at).equals(res.get(res.size()-1)))
					start_at ++;
				for (int i=start_at; i < subpoints.size(); i++)
					res.add(subpoints.get(i));
			}
		}
		return res;
	}
	
	/**
	 * Update database record for a Shape
	 * @param shape The Shape to update in database
	 * @throws SQLException
	 */
	public void update(Shape shape) throws SQLException{
		if (shape.getClass() == Line.class)
			_lines.update((Line) shape);
		else if (shape.getClass() == Group.class)
			_groups.update((Group) shape);
	}
	
	/**
	 * Return a Shape, or null if not in database.
	 * @param shape_id The shape unique ID
	 * @return An initialized subclass of Shape
	 */
	public Shape getShape(int shape_id){
		try {
			return _getShapeAsLine(shape_id);
		} catch (SQLException e) {}
		
		try {
			return _getShapeAsGroup(shape_id);
		} catch (SQLException e) {}
		
		return null;
	}
	
	/**
	 * Attempt to retrieve a Shape as a Line
	 * @param shape_id The Shape unique ID
	 * @return an initialized Line
	 * @throws SQLException
	 */
	private Line _getShapeAsLine(int shape_id) throws SQLException{
		QueryBuilder<Line, Integer> queryBuilder = _lines.queryBuilder();
		
		Where<Line, Integer> where = queryBuilder.where();
		SelectArg selectArg = new SelectArg();
		
		where.eq("_shaperec_id", shape_id);
		PreparedQuery<Line> preparedQuery = queryBuilder.prepare();
		
		selectArg.setValue(shape_id);
		List<Line> lines = _lines.query(preparedQuery);
		if (! lines.isEmpty())
			return lines.get(0);
		throw new SQLException();
	}
	
	/**
	 * Attempt to retrieve Shape as a group
	 * @param shape_id The Shape unique ID
	 * @return an initialized Group
	 * @throws SQLException
	 */
	private Group _getShapeAsGroup(int shape_id) throws SQLException {
		QueryBuilder<Group, Integer> queryBuilder = _groups.queryBuilder();
		
		Where<Group, Integer> where = queryBuilder.where();
		SelectArg selectArg = new SelectArg();
		
		where.eq("_shaperec_id", shape_id);
		PreparedQuery<Group> preparedQuery = queryBuilder.prepare();
		
		selectArg.setValue(shape_id);
		List<Group> groups = _groups.query(preparedQuery);
		if (! groups.isEmpty())
			return groups.get(0);
		throw new SQLException();
	}
}
