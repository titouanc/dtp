package be.ac.ulb.infof307.g03.models;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.j256.ormlite.dao.DaoManager;
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
	 * @brief Geometry on empty database: could not get any shape
	 * @throws SQLException
	 */
	@Test
	public void test_geometry_empty() throws SQLException {
		Geometry geo = new Geometry(_db);
		assertNull(geo.getShape(1));
	}
	
	/**
	 * @brief Insert a line in the database, 
	 *        assert that getting a Shape with the line unique ID is equals to 
	 *        the initial object, either by ID or content.
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

}
