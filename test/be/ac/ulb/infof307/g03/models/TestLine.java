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
import com.j256.ormlite.table.TableUtils;

/**
 * @author Titouan Christophe
 *
 */
public class TestLine {
	private ConnectionSource _db;
	private Dao<Line, Integer> _dao;

	@Before
	public void setUp() throws Exception {
		_db = new JdbcConnectionSource("jdbc:sqlite::memory:");
		TableUtils.createTableIfNotExists(_db, Point.class);
		TableUtils.createTableIfNotExists(_db, Line.class);
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
	public void test_line_length() {
		Point p1 = new Point(0, 0, 0);
		Point p2 = new Point(3, 4, 0);
		Line l = new Line(p1, p2);
		
		assertEquals(l.length(), 5, 0);
	}
	
	/**
	 * @brief Insert a line in the database,
	 *        then fetch a copy and ensure its length and shapeID are ok
	 * @throws SQLException
	 */
	@Test
	public void test_line_insert() throws SQLException {
		Point p1 = new Point(0, 0, 0);
		Point p2 = new Point(3, 4, 0);
		Line l = new Line(p1, p2);
		
		_dao.create(l);
		int lineId = l.getId();
		assertEquals(1, lineId);
		
		Line copy = _dao.queryForId(lineId);
		assertEquals(5, copy.length(), 0);
		
		assertTrue(l.equalsById(copy));
		assertTrue(l.equalsByContent(copy));
		assertTrue(l.equals(copy));
	}
}
