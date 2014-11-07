package be.ac.ulb.infof307.g03.models;

import static org.junit.Assert.*;
import java.sql.SQLException;
import java.util.List;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class TestPoint {
	private ConnectionSource _db;
	private Dao<Point, Integer> _dao;

	@Before
	public void setUp() throws Exception {
		_db = new JdbcConnectionSource("jdbc:sqlite::memory:");
		TableUtils.createTableIfNotExists(_db, Point.class);
		_dao = DaoManager.createDao(_db, Point.class);
	}

	@After
	public void tearDown() throws Exception {
		_db.close();
	}
	
	/**
	 * @brief Basic test: create a point, save it in database.
	 *        Assert it has an id, then fetch a point with the same
	 *        id from database, and compare them.
	 * @throws SQLException
	 */
	@Test
	public void test_create() throws SQLException {
		Point p = new Point(42, 3.14, 2.21);
		_dao.create(p);
		
		int pointId = p.getId();
		assertNotEquals(0, pointId);
		
		Point q = _dao.queryForId(pointId);
		assertEquals(p.getX(), q.getX(), 0);
		assertEquals(p.getY(), q.getY(), 0);
		assertEquals(p.getZ(), q.getZ(), 0);
	}
	
	/**
	 * @brief Insert 2 points in database then fetch them and assert
	 *        on their values
	 * @throws SQLException
	 */
	@Test
	public void test_multiple_points() throws SQLException {
		_dao.create(new Point(1, 2, 3));
		_dao.create(new Point(4, 5, 6));
		assertEquals(2, _dao.countOf());
		
		List<Point> points = _dao.queryForAll();
		assertEquals(2, points.size());
		assert(points.get(0).equals(new Point(1, 2, 3)));
		assert(points.get(1).equals(new Point(4, 5, 6)));
	}
	
	/**
	 * @brief Ensure that 2 points with the same coordinates could not
	 *        be inserted in the database
	 * @throws SQLException
	 */
	@Test(expected=SQLException.class)
	public void test_uniqueness() throws SQLException {
		_dao.create(new Point(1, 2, 3));
		_dao.create(new Point(1, 2, 3));
	}
	
	@Test
	public void test_equals(){
		Point p1 = new Point(0, 0, 0);
		Point p2 = new Point(0, 0, 0);
		assertEquals(0, p1.getId());
		assertEquals(0, p2.getId());
		assertTrue(p1.equals(p2));
	}
}
