/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
	private Dao<Point, Integer> _points = null;
	private Dao<Floor, Integer> _floors = null;
	private List<Change> _changes = null;
	
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
		TableUtils.createTableIfNotExists(database, Floor.class);
	}
	
	/**
	 * Create a new geometric data acess object that relies on given database
	 * @param database A valid connection for ORMLite
	 * @throws SQLException
	 */
	public GeometryDAO(ConnectionSource database) throws SQLException{
		_lines = DaoManager.createDao(database, Line.class);
		_groups = DaoManager.createDao(database, Group.class);
		_grounds = DaoManager.createDao(database, Ground.class);
		_walls = DaoManager.createDao(database, Wall.class);
		_points = DaoManager.createDao(database, Point.class);
		_floors = DaoManager.createDao(database, Floor.class);
		_changes = new LinkedList<Change>();
	}
	
	/**
	 * Create a new item in the database
	 * @param object The object to create in database
	 * @return The number of modified rows
	 * @throws SQLException
	 */
	public int create(Geometric object) throws SQLException{
		int res = 0;
		if (object instanceof Point){
			Point p = (Point) object;
			try {_points.create(p);}
			catch (SQLException err){
				// Not unique: find existing point and copy its data
				p.copyFrom(getPoint(p.getX(), p.getY(), p.getZ()));
			}
		}
		else if (object instanceof Line){
			Line line = (Line) object;
			for (Point p : line.getPoints())
				create(p);
			res = _lines.create(line);
		}
		else if (object instanceof Group)
			res = _groups.create((Group) object);
		else if (object instanceof Ground)
			res = _grounds.create((Ground) object);
		else if (object instanceof Wall)
			res = _walls.create((Wall) object);
		else if (object instanceof Floor)
			res = _floors.create((Floor) object);
		if (res != 0){
			setChanged();
			_changes.add(Change.create(object));
		}
		return res;
	}
	
	/**
	 * Refresh an in-memory object from database
	 * @param object The geometric object to refresh
	 * @return The number of affected rows
	 * @throws SQLException
	 */
	public int refresh(Geometric object) throws SQLException{
		int res = 0;
		if (object instanceof Point)
			res = _points.refresh((Point) object);
		else if (object instanceof Line)
			res = _lines.refresh((Line) object);
		else if (object instanceof Group)
			res = _groups.refresh((Group) object);
		else if (object instanceof Ground)
			res = _grounds.refresh((Ground) object);
		else if (object instanceof Wall)
			res = _walls.refresh((Wall) object);
		else if (object instanceof Floor)
			res = _floors.refresh((Floor) object);
		return res;
	}
	
	/**
	 * Update a shape in database (permanently save in-memory modifications)
	 * @param object The geometric object to update
	 * @return The number of rows updated in the database
	 * @throws SQLException
	 */
	public int update(Geometric object) throws SQLException{
		int res = 0;
		if (object instanceof Point)
			res = _points.update((Point) object);
		else if (object instanceof Line)
			res = _lines.update((Line) object);
		else if (object instanceof Group)
			res = _groups.update((Group) object);
		else if (object instanceof Ground)
			res = _grounds.update((Ground) object);
		else if (object instanceof Wall)
			res = _walls.update((Wall) object);
		else if (object instanceof Floor)
			res = _floors.update((Floor) object);
		if (res != 0){
			setChanged();
			_changes.add(Change.update(object));
		}
		return res;
	}
	
	private int deleteGroup(Group grp) throws SQLException{
		Ground gnd = getGround(grp);
		if (gnd != null)
			delete(gnd);
		
		Wall wall = getWall(grp);
		if (wall != null)
			delete(wall);
			
		for (Shape shape : getShapesForGroup(grp))
			delete(shape);
		return _groups.delete(grp);
	}
	
	/**
	 * Delete a shape from the database
	 * @param object The geometric object to remove
	 * @return The number of rows that have been modified
	 * @throws SQLException
	 */
	public int delete(Geometric object) throws SQLException{
		int res = 0;
		if (object instanceof Point)
			res = _points.delete((Point) object);
		else if (object instanceof Line)
			res = _lines.delete((Line) object);
		else if (object instanceof Group)
			res = deleteGroup((Group) object);
		else if (object instanceof Ground)
			res = _grounds.delete((Ground) object);
		else if (object instanceof Wall)
			res = _walls.delete((Wall) object);
		else if (object instanceof Floor)
			res = _floors.delete((Floor) object);
		if (res != 0){
			setChanged();
			_changes.add(Change.delete(object));
		}
		return res;
	}
	
	/**
	 * Get a Geometric object from database
	 * @param uid Unique object id
	 * @return A Geometric object, or null if not found
	 */
	public Geometric getByUID(String uid){
		String[] parts = uid.split("-");
		Geometric res = null;
		try{
			if (parts.length == 2){
				Integer id = new Integer(parts[1]);
				if (parts[0].equals("gnd"))
					res = getGround(id);
				else if (parts[0].equals("grp"))
					res = getGroup(id);
				else if (parts[0].equals("lin"))
					res = getLine(id);
				else if (parts[0].equals("pnt"))
					res = getPoint(id);
				else if (parts[0].equals("wal"))
					res = getWall(id);
				else if (parts[0].equals("flr"))
					res = getFloor(id);
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		return res;
		
	}
	
	/**
	 * Retrieve all lines attached to a given point
	 * @param p The point from which we search lines
	 * @return A list of lines (might be empty)
	 * @throws SQLException
	 */
	public List<Line> getLinesForPoint(Point p) throws SQLException {
		int pid = p.getId();
		return _lines.query(
			_lines.queryBuilder().where().eq("_p1_id", pid).or().eq("_p2_id", pid).prepare()
		);
	}
	
	/**
	 * Retrieve all Grouped item containing a given point
	 * @param p The point from which we search grouped items
	 * @return A list of grouped items (might be empty)
	 * @throws SQLException
	 */
	public List<Grouped> getGroupedForPoint(Point p) throws SQLException{
		/* 
		 * A point could be contained in multiple Lines that belong to the same Grouped
		 * Hash them by UID to have a unique set
		 */
		Map<String, Grouped> res = new HashMap<String, Grouped>();
		for (Line l : getLinesForPoint(p)){
			Group grp = l.getGroup();
			while (grp != null){
				Wall wall = getWall(grp);
				if (wall != null && ! res.containsKey(wall.getUID()))
					res.put(wall.getUID(), wall);
				
				Ground gnd = getGround(grp);
				if (gnd != null && ! res.containsKey(gnd.getUID())) 
					res.put(gnd.getUID(), gnd);
				
				/* Iterate over parent group */
				grp = grp.getGroup();
			}
		}
		return new ArrayList<Grouped>(res.values());
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
	 * @param ground_id The Floor identifier
	 * @return The floor
	 * @throws SQLException
	 */
	public Ground getGround(int ground_id) throws SQLException{
		return _grounds.queryForId(ground_id);
	}
	
	/**
	 * Get a Ground object associated with a Group from database
	 * @param group The associated group
	 * @return A Ground object, or null
	 * @throws SQLException
	 */
	public Ground getGround(Group group) throws SQLException {
		return _grounds.queryForFirst(
			_grounds.queryBuilder().where().eq("_group_id", group.getId()).prepare()
		);
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
	 * Get a Wall object associated with a Group from database
	 * @param group The associated group
	 * @return A Wall object, or null
	 * @throws SQLException
	 */
	public Wall getWall(Group group) throws SQLException {
		return _walls.queryForFirst(
			_walls.queryBuilder().where().eq("_group_id", group.getId()).prepare()
		);
	}
	
	public Floor getFloor(int floor_id) throws SQLException{
		return _floors.queryForId(floor_id);
	}
	
	public Floor getFloor(Group group) throws SQLException{
		return _floors.queryForFirst(
			_floors.queryBuilder().where().eq("_group_id", group.getId()).prepare()
		);
	}
	
	/**
	 * Retrieve a point from the database, given its identifier
	 * @param point_id The point identifier
	 * @return an in-memory Point object
	 * @throws SQLException
	 */
	public Point getPoint(int point_id) throws SQLException{
		return _points.queryForId(point_id);
	}
	
	/**
	 * Retrieve a point from the database, given its coordinates
	 * @param x coordinate
	 * @param y coordinate
	 * @param z coordinate
	 * @return A Point object
	 * @throws SQLException
	 */
	public Point getPoint(double x, double y, double z) throws SQLException{
		return _points.queryForFirst(
			_points.queryBuilder().where().eq("_x", x).and().eq("_y", y).and().eq("_z", z).prepare()
		);
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

	public List<Floor> getFloors() throws SQLException{
		return _floors.queryForAll();
	}
	
	/**
	 * Retrieve all Walls from database
	 * @return A list of all project's walls
	 * @throws SQLException
	 */

	public List<Wall> getWalls() throws SQLException{
		return _walls.queryForAll();
	}
	
	/**
	 * Retrieve all Grounds from database
	 * @return A list of all project's grounds
	 * @throws SQLException
	 */
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
	
	@Override
	public void notifyObservers(){
		List<Change> changes = _changes;
		_changes = new LinkedList<Change>();
		super.notifyObservers(changes);
	}
	
	@Override
	public void notifyObservers(Object arg){
		notifyObservers();
	}
	
	/**
	 * Transform a Ground into a Mesh
	 * @param ground The ground to transform
	 * @return The mesh
	 * @throws SQLException
	 * @throws AssertionError If the number of points is less than 3
	 */
	public Mesh getGroundAsMesh(Ground ground) throws SQLException, AssertionError {
		List<Point> all_points = getPointsForShape(ground.getGroup());
		int shape_n_points = all_points.size();
		assert(shape_n_points > 2);
		
		/* 0) Closed polygon ? -> we don't need to store both first && last */
		Point firstPoint = all_points.get(0);
		Point lastPoint = all_points.get(shape_n_points - 1);
		if (firstPoint.equals(lastPoint))
			shape_n_points--;
		
		/* 1) Build an array of all points */
		Vector3f vertices[] = new Vector3f[shape_n_points];
		for (int i=0; i<shape_n_points; i++)
			vertices[i] = all_points.get(i).toVector3f();
		
		/* 2) Polygon triangulation to make a surface */
		int n_triangles = shape_n_points - 2;
		int edges[] = new int[3 * n_triangles];
		for (int i=0; i<n_triangles; i++){
			edges[3 * i] = 0;
			edges[3 * i + 1] = i+1;
			edges[3 * i + 2] = i+2;
		}
		
		Mesh mesh = new Mesh();
	  	mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
	  	mesh.setBuffer(Type.Index,    3, BufferUtils.createIntBuffer(edges));
	  	mesh.updateBound();
		return mesh;
	}
}
