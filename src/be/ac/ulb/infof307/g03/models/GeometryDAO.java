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
 * @author Titouan Christophe, Walter Moulart
 * The geometry class is a DAO for all geometry related models.
 * Handle CRUD operations, and the associations logic
 */
public class GeometryDAO extends Observable {
	private Dao<Binding, Integer> _bindings = null;
	private Dao<Room, Integer> _rooms = null;
	private Dao<Wall, Integer> _walls = null;
	private Dao<Ground, Integer> _grounds = null;
	private Dao<Point, Integer> _points = null;
	private Dao<Floor, Integer> _floors = null;
	private Dao<Roof,  Integer> _roofs = null;
	private List<Change> _changes = null;
	
	/**
	 * Migrate all needed tables to a database
	 * @param database The database connection
	 * @throws SQLException
	 */
	public static void migrate(ConnectionSource database) throws SQLException{
		TableUtils.createTableIfNotExists(database, Point.class);
		TableUtils.createTableIfNotExists(database, Binding.class);
		TableUtils.createTableIfNotExists(database, Room.class);
		TableUtils.createTableIfNotExists(database, Ground.class);
		TableUtils.createTableIfNotExists(database, Wall.class);
		TableUtils.createTableIfNotExists(database, Floor.class);
		TableUtils.createTableIfNotExists(database, Roof.class);
	}
	
	/**
	 * Create a new geometric data access object that relies on given database
	 * @param database A valid connection for ORMLite
	 * @throws SQLException
	 */
	public GeometryDAO(ConnectionSource database) throws SQLException{
		super();
		resetConnection(database);
	}
	
	/**
	 * Reset the inner database connection
	 * @param database The new database handler
	 * @throws SQLException
	 */
	public final void resetConnection(ConnectionSource database) throws SQLException {
		_bindings = DaoManager.createDao(database, Binding.class);
		_rooms = DaoManager.createDao(database, Room.class);
		_grounds = DaoManager.createDao(database, Ground.class);
		_walls = DaoManager.createDao(database, Wall.class);
		_points = DaoManager.createDao(database, Point.class);
		_floors = DaoManager.createDao(database, Floor.class);
		_roofs = DaoManager.createDao(database, Roof.class);
		_changes = new LinkedList<Change>();
	}
	
	/**
	 * Copy all content from another DAO
	 * @param other the original DAO
	 * @return The number of copied Geometric
	 * @throws SQLException
	 */
	public int copyFrom(GeometryDAO other) throws SQLException{
		List<Geometric> toCopy = new ArrayList<Geometric>();
		toCopy.addAll(other._floors.queryForAll());
		toCopy.addAll(other._points.queryForAll());
		toCopy.addAll(other._bindings.queryForAll());
		toCopy.addAll(other._rooms.queryForAll());
		toCopy.addAll(other._walls.queryForAll());
		toCopy.addAll(other._grounds.queryForAll());
		toCopy.addAll(other._roofs.queryForAll());
		int res = 0;
		for (Geometric g : toCopy)
			res += create(g);
		return res;
	}
	
	public int create(Point p) throws SQLException{
		int res = 0;
		try {res=_points.create(p);}
		catch (SQLException err){
			// Not unique: find existing point and copy its data
			p.copyFrom(getPoint(p.getX(), p.getY(), p.getZ()));
		}
		if (res != 0){
			setChanged();
			_changes.add(Change.create(p));
		}
		return res;
	}
	
	public int create(Binding bind) throws SQLException{
		if (bind.getPoint().getId() == 0)
			create(bind.getPoint());
		int res = _bindings.create(bind);
		if (res != 0){
			setChanged();
			_changes.add(Change.create(bind));
		}
		return res;
	}
	
	public int create(Floor floor) throws SQLException{
		double base = 0;
		for (Floor f : getFloorsBelow(floor))
			base += f.getHeight();
		floor.setBaseHeight(base);
		int res = _floors.create(floor);
		if (res != 0){
			setChanged();
			_changes.add(Change.create(floor));
		}
		return res;
	}
	
	/**
	 * Create a new item in the database
	 * @param object The object to create in database
	 * @return The number of modified rows
	 * @throws SQLException
	 */
	public int create(Geometric object) throws SQLException{
		int res = 0;
		if (object instanceof Point)
			create((Point) object);
		else if (object instanceof Binding)
			create((Binding) object);
		else if (object instanceof Room)
			res = _rooms.create((Room) object);
		else if (object instanceof Ground)
			res = _grounds.create((Ground) object);
		else if (object instanceof Wall)
			res = _walls.create((Wall) object);
		else if (object instanceof Floor)
			create((Floor) object);
		else if (object instanceof Roof){
			res = _roofs.create((Roof) object);
		}
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
		else if (object instanceof Binding)
			res = _bindings.refresh((Binding) object);
		else if (object instanceof Room)
			res = _rooms.refresh((Room) object);
		else if (object instanceof Ground)
			res = _grounds.refresh((Ground) object);
		else if (object instanceof Wall)
			res = _walls.refresh((Wall) object);
		else if (object instanceof Floor)
			res = _floors.refresh((Floor) object);
		else if (object instanceof Roof){
			res= _roofs.refresh((Roof)object);
		}
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
		else if (object instanceof Binding)
			res = _bindings.update((Binding) object);
		else if (object instanceof Room)
			res = _rooms.update((Room) object);
		else if (object instanceof Ground)
			res = _grounds.update((Ground) object);
		else if (object instanceof Wall)
			res = _walls.update((Wall) object);
		else if (object instanceof Floor)
			res = _floors.update((Floor) object);
		else if (object instanceof Roof){
			res= _roofs.update((Roof) object);
		}
		if (res != 0){
			setChanged();
			_changes.add(Change.update(object));
		}
		return res;
	}
	
	private int delete(Room room) throws SQLException{
		if (room.getGround() != null)
			delete(room.getGround());
		if (room.getWall() != null)
			delete(room.getWall());
		if (room.getRoof() != null)
			delete(room.getRoof());
		
		for (Binding b : room.getBindings())
			delete(b);
		
		return _rooms.delete(room);
	}
	
	private int delete(Floor floor) throws SQLException {
		for (Room grp : getRooms(floor))
			delete(grp);
		int res = _floors.delete(floor);
		for (Floor above : getFloorsAbove(floor)){
			above.setBaseHeight(above.getBaseHeight() - floor.getHeight());
			above.setIndex(above.getIndex() - 1);
			update(above);
		}
		return res;
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
		else if (object instanceof Binding)
			res = _bindings.delete((Binding) object);
		else if (object instanceof Room)
			res = delete((Room) object);
		else if (object instanceof Ground)
			res = _grounds.delete((Ground) object);
		else if (object instanceof Wall)
			res = _walls.delete((Wall) object);
		else if (object instanceof Floor)
			res = delete((Floor) object);
		else if (object instanceof Roof)
			res = _roofs.delete((Roof) object);
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
				String prefix = parts[0];
				Integer id = new Integer(parts[1]);
				if (prefix.equals(new Ground().getUIDPrefix()))
					res = getGround(id);
				else if (prefix.equals(new Room().getUIDPrefix()))
					res = getRoom(id);
				else if (prefix.equals(new Binding().getUIDPrefix()))
					res = getBinding(id);
				else if (prefix.equals(new Point().getUIDPrefix()))
					res = getPoint(id);
				else if (prefix.equals(new Wall().getUIDPrefix()))
					res = getWall(id);
				else if (prefix.equals(new Floor().getUIDPrefix()))
					res = getFloor(id);
				else if (prefix.equals(new Roof().getUIDPrefix()))
					res = getRoof(id);
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
	public List<Binding> getLinesForPoint(Point p) throws SQLException {
		int pid = p.getId();
		return _bindings.query(
			_bindings.queryBuilder().where().eq("_p1_id", pid).or().eq("_p2_id", pid).prepare()
		);
	}
	
	/**
	 * Get a line object from the database
	 * @param line_id The line identifier
	 * @return The line
	 * @throws SQLException
	 */
	public Binding getBinding(int bind_id) throws SQLException{
		return _bindings.queryForId(bind_id);
	}
	
	/**
	 * Get a group object from the database
	 * @param group_id The group identifier
	 * @return The group
	 * @throws SQLException
	 */
	public Room getRoom(int group_id) throws SQLException{
		return _rooms.queryForId(group_id);
	}
	
	/**
	 * Get a group object from the database
	 * @param name The group name
	 * @return The group
	 * @throws SQLException
	 */
	public Room getGroup(String name) throws SQLException {
		return _rooms.queryForFirst(
			_rooms.queryBuilder().where().eq("_name", name).prepare()
		);
	}
	
	/**
	 * Get all groups situated at a given floor
	 * @param floor The floor from which we want groups
	 * @return A (possibly empty) List of Groups
	 * @throws SQLException
	 */
	public List<Room> getRooms(Floor floor) throws SQLException {
		return _rooms.query(
			_rooms.queryBuilder().where().eq("_floor_id", floor.getId()).prepare()
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
	 * Get a Wall object from the database
	 * @param wall_id The Wall identifier
	 * @return a Wall object
	 * @throws SQLException
	 */
	public Wall getWall(int wall_id) throws SQLException{
		return _walls.queryForId(wall_id);
	}
	
	/**
	 * Get a floor given its floor id
	 * @param floor_id The floor identifier
	 * @return a Floor object, or null if not found
	 * @throws SQLException
	 */
	public Floor getFloor(int floor_id) throws SQLException{
		return _floors.queryForId(floor_id);
	}
	
	/**
	 * Get all floors above this one
	 * @param floor The reference floor
	 * @return 
	 * @throws SQLException
	 */
	public List<Floor> getFloorsAbove(Floor floor) throws SQLException{
		if (floor == null)
			return getFloors();
		return floor.getFollowing(_floors);
	}
	
	/**
	 * Get all floors below this one
	 * @param floor
	 * @return
	 * @throws SQLException
	 */
	public List<Floor> getFloorsBelow(Floor floor) throws SQLException{
		if (floor == null)
			return getFloors();
		return floor.getPreceding(_floors);
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
	 * Add a group to a floor in database
	 * @param floor The Floor to add
	 * @param grp The Room to put the Floor into
	 * @throws SQLException
	 */
	public void addRoomToFloor(Floor floor, Room grp) throws SQLException {
		if (floor.getId() == 0)
			_floors.create(floor);
		grp.setFloor(floor);
		if (grp.getId() != 0)
			update(grp);
		else
			create(grp);
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
	 * Get a Roof object from the database
	 * @param roof_id The Roof identifier
	 * @return a Roof object
	 * @throws SQLException
	 */
	public Roof getRoof(int roof_id) throws SQLException{
		return _roofs.queryForId(roof_id);
	}
	
	/**
	 * Retrieve all Roofs from database
	 * @return A list of all project's roofs
	 * @throws SQLException
	 */
	public List<Roof> getRoofs() throws SQLException{
		return _roofs.queryForAll();
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
	 * Retrieve the closest point given a point and a bounding radius,
	 * or null if no point in the bound
	 * @param p The point from which we search a neighbor
	 * @param bound The radius to search around p
	 * @return The closest Point, or null if not existant
	 * @throws SQLException
	 */
	public Point findClosePoint(Point p, double bound) throws SQLException{
		double xmin = p.getX() - bound,
		       xmax = p.getX() + bound,
    		   ymin = p.getY() - bound,
		       ymax = p.getY() + bound,
		       zmin = p.getZ() - bound,
		       zmax = p.getZ() + bound;
		
		return _points.queryForFirst(
			_points.queryBuilder().where().ge("_x", xmin).and().le("_x", xmax).
			and().ge("_y", ymin).and().le("_y", ymax).
			and().ge("_z", zmin).and().le("_z", zmax).prepare()
		);
	}
}
