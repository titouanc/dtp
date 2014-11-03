package be.ac.ulb.infof307.g03.models;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.LinkedList;
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
		assertNull(geo.getLine(1));
	}
	
	@Test
	public void test_geometry_line() throws SQLException{
		Geometry geo = new Geometry(_db);
		Line l = new Line(new Point(0, 0, 0), new Point(3, 4, 0));
		geo.create(l);
		assertTrue(l.getId() != 0);
		assertTrue(l.equals(geo.getLine(l.getId())));
		
		List<Point> points = geo.getPointsForShape(l);
		assertEquals(2, points.size());
	}
	
	@Test
	public void test_get_points_from_line() throws SQLException {
		Geometry geo = new Geometry(_db);
		Point x = new Point(1, 0, 0), y = new Point(0, 1, 0);
		
		Line l = new Line(x, y);
		geo.create(l);
		
		List<Point> points = geo.getPointsForShape(l);
		assertEquals(2, points.size());
		assertTrue(x.equals(points.get(0)));
		assertTrue(y.equals(points.get(1)));
	}
	
	@Test
	public void test_get_points_from_group() throws SQLException {
		Geometry geo = new Geometry(_db);
		Point x = new Point(1, 0, 0), y = new Point(0, 1, 0), z = new Point(0, 0, 1);
		
		Group grp = new Group();
		geo.create(grp);
		
		Line l1 = new Line(x, y);
		l1.addToGroup(grp);
		geo.create(l1);
		
		Line l2 = new Line(y, z);
		l2.addToGroup(grp);
		geo.create(l2);
		
		List<Point> points = geo.getPointsForShape(grp);
		assertEquals(3, points.size());
		assertTrue(x.equals(points.get(0)));
		assertTrue(y.equals(points.get(1)));
		assertTrue(z.equals(points.get(2)));
	}
}
