package be.ac.ulb.infof307.g03.models;

import static org.junit.Assert.*;
import java.sql.SQLException;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

public class TestPoint {
	private ConnectionSource _db;
	private Dao<Point, Integer> _dao;

	@Before
	public void setUp() throws Exception {
		_db = new JdbcConnectionSource("jdbc:sqlite::memory:");
		Point.migrate(_db);
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

}
