/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

/**
 * @author Titouan Christophe
 * The geometry class is a DAO for all geometry related models.
 * Handle CRUD operations, and the associations logic
 */
public class GeometryDAO extends Observable {
	private Dao<Line, Integer> _lines = null;
	private Dao<Group, Integer> _groups = null;
	private Dao<Wall, Integer> _walls = null;
	private Dao<Ground, Integer> _grounds = null;
	
	/**
	 * Migrate all needed tables to a database
	 * @param database The database connection
	 * @throws SQLException
	 */
	public static void migrate(ConnectionSource database) throws SQLException{
		TableUtils.createTableIfNotExists(database, Point.class);
		TableUtils.createTableIfNotExists(database, Line.class);
		TableUtils.createTableIfNotExists(database, Group.class);
		TableUtils.createTableIfNotExists(database, Ground.class);
		TableUtils.createTableIfNotExists(database, Wall.class);
	}
	
	public GeometryDAO(ConnectionSource database) throws SQLException{
		_lines = DaoManager.createDao(database, Line.class);
		_groups = DaoManager.createDao(database, Group.class);
		_grounds = DaoManager.createDao(database, Ground.class);
		_walls = DaoManager.createDao(database, Wall.class);
	}
	
	/**
	 * Create a new item in the database
	 * @param object The object to create in database
	 * @return The number of modified rows
	 * @throws SQLException
	 */
	public int create(Geometric object) throws SQLException{
		int res = 0;
		if (object.getClass() == Line.class)
			res = _lines.create((Line) object);
		else if (object.getClass() == Group.class)
			res = _groups.create((Group) object);
		else if (object.getClass() == Ground.class)
			res = _grounds.create((Ground) object);
		else if (object.getClass() == Wall.class)
			res = _walls.create((Wall) object);
		if (res != 0)
			setChanged();
		return res;
	}
	
	public int refresh(Geometric object) throws SQLException{
		int res = 0;
		if (object.getClass() == Line.class)
			res = _lines.refresh((Line) object);
		else if (object.getClass() == Group.class)
			res = _groups.refresh((Group) object);
		else if (object.getClass() == Ground.class)
			res = _grounds.refresh((Ground) object);
		else if (object.getClass() == Wall.class)
			res = _walls.refresh((Wall) object);
		if (res != 0)
			setChanged();
		return res;
	}
	
	/**
	 * Update a shape in database (permanently save in-memory modifications)
	 * @param shape The shape to save
	 * @return The number of rows updated in the database
	 * @throws SQLException
	 */
	public int update(Geometric object) throws SQLException{
		int res = 0;
		if (object.getClass() == Line.class)
			res = _lines.update((Line) object);
		else if (object.getClass() == Group.class)
			res = _groups.update((Group) object);
		else if (object.getClass() == Ground.class)
			res = _grounds.update((Ground) object);
		else if (object.getClass() == Wall.class)
			res = _walls.update((Wall) object);
		if (res != 0)
			setChanged();
		return res;
	}
	
	/**
	 * Delete a shape from the database
	 * @param shape The shape to remove
	 * @return The number of rows that have been modified
	 * @throws SQLException
	 */
	public int delete(Geometric object) throws SQLException{
		int res = 0;
		if (object.getClass() == Line.class)
			res = _lines.delete((Line) object);
		else if (object.getClass() == Group.class)
			res = _groups.delete((Group) object);
		else if (object.getClass() == Ground.class)
			res = _grounds.delete((Ground) object);
		else if (object.getClass() == Wall.class)
			res = _walls.delete((Wall) object);
		if (res != 0)
			setChanged();
		return res;
	}
	
	/**
	 * Get a line object from the database
	 * @param line_id The line identifier
	 * @return The line
	 * @throws SQLException
	 */
	public Line getLine(int line_id) throws SQLException{
		return _lines.queryForId(line_id);
	}
	
	/**
	 * Get a group object from the database
	 * @param group_id The group identifier
	 * @return The group
	 * @throws SQLException
	 */
	public Group getGroup(int group_id) throws SQLException{
		return _groups.queryForId(group_id);
	}
	
	/**
	 * Get a group object from the database
	 * @param name The group name
	 * @return The group
	 * @throws SQLException
	 */
	public Group getGroup(String name) throws SQLException {
		return _groups.queryForFirst(
			_groups.queryBuilder().where().eq("_name", name).prepare()
		);
	}
	
	/**
	 * Get a Floor object from the database
	 * @param floor_id The Floor identifier
	 * @return The floor
	 * @throws SQLException
	 */
	public Ground getGround(int ground_id) throws SQLException{
		return _grounds.queryForId(ground_id);
	}
	
	/**
	 * Get a Wall object from the database
	 * @param wall_id The Wall identifier
	 * @return a Wall object
	 * @throws SQLException
	 */
	public Wall getWall(int wall_id) throws SQLException{
		return _walls.queryForId(wall_id);
	}
	
	/**
	 * Get all shapes contained in given group
	 * @param grp A group of shapes
	 * @return All toplevel shapes of grp
	 * @throws SQLException
	 */
	public List<Shape> getShapesForGroup(Group grp) throws SQLException{
		List<Shape> res = new LinkedList<Shape>();
		res.addAll(_lines.queryForEq("_group_id", grp.getId()));
		res.addAll(_groups.queryForEq("_group_id", grp.getId()));
		return res;
	}
	
	/**
	 * Recursively get all points constituting a shape
	 * @param shape A shape (might be a group)
	 * @return A list of points
	 * @throws SQLException
	 */
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
	
	/**
	 * Add a shape to a group
	 * @param grp Destination group
	 * @param shape Shape to add to the group
	 * @throws SQLException
	 */
	public void addShapeToGroup(Group grp, Shape shape) throws SQLException{
		shape.addToGroup(grp);
		if (shape.getId() != 0)
			update(shape);
		else
			create(shape);
		setChanged();
	}

	public List<Wall> getWalls() throws SQLException{
		return _walls.queryForAll();
	}
	
	public List<Ground> getGrounds() throws SQLException{
		return _grounds.queryForAll();
	}
	
	/**
	 * Get all orphan shapes from the database
	 * @return All Shapes in project that are not in a group
	 * @throws SQLException 
	 */
	public List<Shape> getRootNodes() throws SQLException{
		QueryBuilder<Line, Integer> lineQB = _lines.queryBuilder();
		Where<Line, Integer> lineW = lineQB.where();
		lineW.isNull("_group_id");
		PreparedQuery<Line> lineQ = lineQB.prepare();
		
		QueryBuilder<Group, Integer> groupQB = _groups.queryBuilder();
		Where<Group, Integer> groupW = groupQB.where();
		groupW.isNull("_group_id");
		PreparedQuery<Group> groupQ = groupQB.prepare();
		
		List<Shape> res = new LinkedList<Shape>();
		res.addAll(_lines.query(lineQ));
		res.addAll(_groups.query(groupQ));
		return res;
	}
	
	/**
	 * Transform a Wall object into a Mesh (3D object usable in jMonkey)
	 * @param wall The wall to transform
	 * @return The Mesh
	 * @throws SQLException
	 */
	public Mesh getWallAsMesh(Wall wall) throws SQLException{
		List<Point> all_points = getPointsForShape(wall.getGroup());
		Vector3f height = new Vector3f(0, 0, (float) wall.getHeight());
		
		int shape_n_points = all_points.size();
		int volume_n_points = 2 * all_points.size(); //floor && ceil
		
		/* 0) Closed polygon ? -> we don't need to store both first && last */
		Point firstPoint = all_points.get(0);
		Point lastPoint = all_points.get(shape_n_points - 1);
		if (firstPoint.equals(lastPoint)){
			shape_n_points--;
			volume_n_points -= 2;
		}
		
		/* 1) Build an array of all points */
		Vector3f vertices[] = new Vector3f[volume_n_points];
		for (int i=0; i<shape_n_points; i++){
			vertices[i] = all_points.get(i).toVector3f(); // floor
			vertices[i + shape_n_points] = all_points.get(i).toVector3f().add(height); //ceil
		}
		
		/* 2) Build an array of indexes on vertices (triangle edges).
		 *    3 points forms a triangle, 2 triangle per line + elevation. 
		 *    In case of closed polygon, overflow index to get first point. */
		int n_triangles = 6 * (all_points.size() - 1);
		int edges[] = new int[n_triangles];
		for (int i=0; i<shape_n_points-1; i++){
			edges[6*i] = i;
			edges[6*i+1] = (i + shape_n_points + 1) % shape_n_points;
			edges[6*i+2] = (i + shape_n_points) % shape_n_points;
			edges[6*i+3] = i;
			edges[6*i+4] = (i+1) % shape_n_points;
			edges[6*i+5] = (i + shape_n_points + 1) % shape_n_points;
		}
		
		/* 3) Pack everything in a Mesh object */
		Mesh mesh = new Mesh();
	  	mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
	  	mesh.setBuffer(Type.Index,    3, BufferUtils.createIntBuffer(edges));
	  	mesh.updateBound();
	  	
	  	return mesh;
	}
}
