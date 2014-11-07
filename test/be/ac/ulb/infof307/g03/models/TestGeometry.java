package be.ac.ulb.infof307.g03.models;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;

public class TestGeometry {
	private ConnectionSource _db;
	
	@Before
	public void setUp() throws Exception {
		_db = new JdbcConnectionSource("jdbc:sqlite::memory:");
		GeometryDAO.migrate(_db);
	}

	@After
	public void tearDown() throws Exception {
		_db.close();
	}

	/**
	 * GeometryDAO on empty database: could not get any shape
	 * @throws SQLException
	 */
	@Test
	public void test_geometry_empty() throws SQLException {
		GeometryDAO geo = new GeometryDAO(_db);
		List<Shape> shapes = geo.getRootNodes();
		assertTrue(shapes.isEmpty());
	}
	
	@Test
	public void test_geometry_line() throws SQLException{
		GeometryDAO geo = new GeometryDAO(_db);
		Line l = new Line(new Point(0, 0, 0), new Point(3, 4, 0));
		geo.create(l);
		assertTrue(l.getId() != 0);
		assertTrue(l.equals(geo.getLine(l.getId())));
		
		List<Point> points = geo.getPointsForShape(l);
		assertEquals(2, points.size());
		
		assertEquals(1, geo.getRootNodes().size());
		geo.delete(l);
		assertEquals(0, geo.getRootNodes().size());
	}
	
	@Test
	public void test_get_points_from_line() throws SQLException {
		GeometryDAO geo = new GeometryDAO(_db);
		Point x = new Point(1, 0, 0), y = new Point(0, 1, 0);
		
		Line l = new Line(x, y);
		geo.create(l);
		
		List<Point> points = geo.getPointsForShape(l);
		assertEquals(2, points.size());
		assertTrue(x.equals(points.get(0)));
		assertTrue(y.equals(points.get(1)));
	}
	
	@Test
	public void test_group_name() throws SQLException{
		GeometryDAO geo = new GeometryDAO(_db);
		Group grp = new Group();
		geo.create(grp);
		
		assertTrue(grp.getId() != 0);
		Group copy = geo.getGroup(grp.getId());
		copy.setName("PaKeBot");
		geo.update(copy);
		
		grp = geo.getGroup(copy.getName());
		assertEquals("PaKeBot", grp.getName());
		
		assertEquals(1, geo.getRootNodes().size());
		geo.delete(grp);
		assertEquals(0, geo.getRootNodes().size());
	}
	
	@Test
	public void test_get_points_from_group() throws SQLException {
		GeometryDAO geo = new GeometryDAO(_db);
		Point x = new Point(1, 0, 0), y = new Point(0, 1, 0), z = new Point(0, 0, 1);
		
		Group grp = new Group();
		geo.create(grp);
		assertTrue(grp.equals(geo.getGroup(grp.getId())));
		
		Line l = new Line(x, y);
		l.addToGroup(grp);
		geo.create(l);
		geo.addShapeToGroup(grp, new Line(y, z));
		
		assertEquals(1, geo.getRootNodes().size());
		
		List<Point> points = geo.getPointsForShape(grp);
		assertEquals(3, points.size());
		assertTrue(x.equals(points.get(0)));
		assertTrue(y.equals(points.get(1)));
		assertTrue(z.equals(points.get(2)));
	}
	
	@Test
	public void test_get_roots() throws SQLException {
		GeometryDAO geo = new GeometryDAO(_db);
		Line l = new Line(new Point(0, 0, 0), new Point(1, 2, 3));
		geo.create(l);
		
		List<Shape> roots = geo.getRootNodes();
		assertEquals(1, roots.size());
		assertTrue(l.equals(roots.get(0)));
	}
		
	@Test
	public void test_wall() throws SQLException{
		GeometryDAO geo = new GeometryDAO(_db);
		Wall wall = new Wall(1);
		
		assertEquals(1, geo.create(wall));
		assertTrue(wall.isVisible());
		assertEquals(1.0, wall.getHeight(), 0);
		
		wall.setHeight(42.27);
		wall.hide();
		geo.update(wall);
		wall = geo.getWall(1);
		assertEquals(42.27, geo.getWall(wall.getId()).getHeight(), 0);
		assertFalse(wall.isVisible());
		
		wall.show();
		geo.update(wall);
		wall = geo.getWall(1);
		assertTrue(wall.isVisible());
		
		assertEquals(1, wall.getId());
		assertEquals(1, wall.getGroup().getId());
		assertEquals(1, geo.getWalls().size());
	}
	
	@Test
	public void test_ground() throws SQLException{
		GeometryDAO geo = new GeometryDAO(_db);
		Ground ground = new Ground();
		assertEquals(1, geo.create(ground));
		
		assertEquals(1, ground.getId());
		assertEquals(1, ground.getGroup().getId());
		assertEquals(1, geo.getGrounds().size());
	}
	
	/**
	 * Create a new Group named "room" maid of 4 lines with 4 points 
	 * (in this order): 00 10 11 01.
	 * Create a Wall and a Ground object using the group.
	 * Insert everything in database
	 * @param geo The data access object
	 * @return The newly created Group
	 * @throws SQLException
	 */
	private Group create_a_room(GeometryDAO geo) throws SQLException{
		Group room = new Group("room");
		geo.create(room);
		geo.create(new Wall(room, 2.35));
		geo.create(new Ground(room));
		
		Point o = new Point(0, 0, 0),
			  x = new Point(1, 0, 0),
			  y = new Point(0, 1, 0),
			  xy = new Point(1, 1, 0);
		geo.addShapeToGroup(room, new Line(o, x));
		geo.addShapeToGroup(room, new Line(x, xy));
		geo.addShapeToGroup(room, new Line(xy, y));
		geo.addShapeToGroup(room, new Line(y, o));
		return room;
	}

	@Test
	public void test_room() throws SQLException{
		GeometryDAO geo = new GeometryDAO(_db);
		Group room = create_a_room(geo);
		assertEquals(1, room.getId());
		
		Wall wall = geo.getWall(1);
		assertEquals(room.getId(), wall.getGroup().getId());
		assertEquals("Wall<room>", wall.toString());
		
		Ground gnd = geo.getGround(1);
		assertEquals(room.getId(), gnd.getGroup().getId());
		assertEquals("Ground<room>", gnd.toString());
	}
	
	@Test
	public void test_wall_as_mesh() throws SQLException {
		GeometryDAO geo = new GeometryDAO(_db);
		create_a_room(geo);
		Wall wall = geo.getWall(1);
		Mesh mesh = geo.getWallAsMesh(wall);
		assertEquals(8, mesh.getVertexCount());
		assertEquals(8, mesh.getTriangleCount());
	}
	
	@Test
	public void test_ground_as_mesh() throws SQLException {
		GeometryDAO geo = new GeometryDAO(_db);
		create_a_room(geo);
		Ground gnd = geo.getGround(1);
		Mesh mesh = geo.getGroundAsMesh(gnd);
		assertEquals(4, mesh.getVertexCount());
		assertEquals(2, mesh.getTriangleCount());
	}
	
	@Test
	public void test_line_with_new_same_points() throws SQLException {
		GeometryDAO geo = new GeometryDAO(_db);
		Line l1 = new Line(new Point(0, 0, 0), new Point(1, 1, 1));
		Line l2 = new Line(new Point(new Vector3f(0, 0, 0)), new Point(-1, 1, 1));
		geo.create(l1);
		geo.create(l2);
		
		List<Point> p1 = l1.getPoints();
		List<Point> p2 = l2.getPoints();
		
		assertEquals(p1.get(0).getId(), p2.get(0).getId());
		assertTrue(p1.get(0).equals(p2.get(0)));
		
		assertNotEquals(p1.get(1).getId(), p2.get(1).getId());
		assertFalse(p1.get(1).equals(p2.get(1)));
	}
}

