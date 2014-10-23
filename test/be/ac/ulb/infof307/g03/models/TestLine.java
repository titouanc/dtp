/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

/**
 * @author Titouan Christophe
 *
 */

public class TestLine {
	private ConnectionSource _db;
	private Dao<Line, String> _dao;

	@Before
	public void setUp() throws Exception {
		_db = new JdbcConnectionSource("jdbc:sqlite::memory:");
		Line.migrate(_db);
		_dao = DaoManager.createDao(_db, Line.class);
	}

	@After
	public void tearDown() throws Exception {
		_db.close();
	}
	
	/**
	 * @brief Create a new line in memory, ensure its length is ok
	 * @throws SQLException
	 */
	@Test
	public void test_line_length() throws SQLException {
		Point p1 = new Point(0, 0, 0);
		Point p2 = new Point(3, 4, 0);
		Line l = new Line(p1, p2);
		
		assertEquals(l.length(), 5, 0);
	}
	
	/**
	 * @brief Insert a line in the database,
	 *        then fetch a copy and ensure its length is ok
	 * @throws SQLException
	 */
	@Test
	public void test_line_insert() throws SQLException {
		Point p1 = new Point(0, 0, 0);
		Point p2 = new Point(3, 4, 0);
		Line l = new Line(p1, p2);
		
		_dao.create(l);
		Line copy = _dao.queryForSameId(l);
		assertEquals(5, copy.length(), 0);
	}
}
