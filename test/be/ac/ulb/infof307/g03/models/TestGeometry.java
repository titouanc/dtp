package be.ac.ulb.infof307.g03.models;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

public class TestGeometry {
	private ConnectionSource _db;
	
	@Before
	public void setUp() throws Exception {
		_db = new JdbcConnectionSource("jdbc:sqlite::memory:");
		Geometry.migrate(_db);
	}

	@After
	public void tearDown() throws Exception {
		_db.close();
	}

	/**
	 * Geometry on empty database: could not get any shape
	 * @throws SQLException
	 */
	@Test
	public void test_geometry_empty() throws SQLException {
		Geometry geo = new Geometry(_db);
		assertNull(geo.getShape(1));
	}
	
	/**
	 * Insert a line in the database, 
	 * assert that getting a Shape with the line unique ID is equals to 
	 * the initial object, either by ID or content.
	 * @throws SQLException
	 */
	@Test
	public void test_geometry_one_line() throws SQLException{
		Geometry geo = new Geometry(_db);
		Point p1 = new Point(0, 0, 0);
		Point p2 = new Point(3, 4, 0);
		Line l = new Line(p1, p2);
		geo.addLine(l);
		
		assertFalse(l.getId() == 0);
		assertFalse(l.getShapeId() == 0);
		assertTrue(l.equals(geo.getShape(l.getShapeId())));
	}

	/**
	 * Insert a line in the database, 
	 * assert that getting points associated to its unique ID are the original ones
	 * the assert that the type of Shape associated with this unique ID is a Line
	 * @throws SQLException
	 */
	@Test
	public void test_geometry_points() throws SQLException{
		Geometry geo = new Geometry(_db);
		Point p1 = new Point(0, 0, 0);
		Point p2 = new Point(3, 4, 0);
		Line l = new Line(p1, p2);
		geo.addLine(l);
		
		List<Point> points = geo.getPointsForShape(l.getShapeId());
		assertTrue(p1.equals(points.get(0)));
		assertTrue(p2.equals(points.get(1)));
		
		Shape copy = geo.getShape(l.getShapeId());
		assertEquals(Line.class, copy.getClass());
	}
	
	/**
	 * Insert 2 lines in database, with a common point.
	 * Assert both could be retrieved and points are correctly ordered.
	 * @throws SQLException
	 */
	@Test
	public void test_multiple_lines() throws SQLException {
		Geometry geo = new Geometry(_db);
		Point p1 = new Point(0, 0, 0);
		Point p2 = new Point(3, 4, 0);
		Point p3 = new Point(0, -4, -3);
		Line l1 = new Line(p1, p2);
		Line l2 = new Line(p2, p3);
		
		geo.addLine(l1);
		geo.addLine(l2);
		
		List<Point> points = geo.getPointsForShape(l1.getShapeId());
		assertTrue(p1.equals(points.get(0)));
		assertTrue(p2.equals(points.get(1)));
		
		points = geo.getPointsForShape(l2.getShapeId());
		assertTrue(p2.equals(points.get(0)));
		assertTrue(p3.equals(points.get(1)));
	}
	
	@Test
	public void test_group_points() throws SQLException {
		Geometry geo = new Geometry(_db);
		Point p1 = new Point(0, 0, 0);
		Point p2 = new Point(3, 4, 0);
		Point p3 = new Point(0, -4, -3);
		Line l1 = new Line(p1, p2);
		Line l2 = new Line(p2, p3);
		
		Group g = geo.createGroup();
		g.addShape(l1);
		g.addShape(l2);
		geo.addLine(l1);
		geo.addLine(l2);
		geo.update(g);
		
		List<Point> points = geo.getPointsForShape(g.getShapeId());
		assertEquals(3, points.size());
		assertTrue(p1.equals(points.get(0)));
		assertTrue(p2.equals(points.get(1)));
		assertTrue(p3.equals(points.get(2)));
	}
}
