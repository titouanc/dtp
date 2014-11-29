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

public class TestPoint extends DAOTest {
	/**
	 * @brief Basic test: create a point, save it in database.
	 *        Assert it has an id, then fetch a point with the same
	 *        id from database, and compare them.
	 * @throws SQLException
	 */
	@Test
	public void test_create() throws SQLException {
		Point p = new Point(42, 3.14, 2.21);
		dao.create(p);
		
		int pointId = p.getId();
		assertNotEquals(0, pointId);
		
		Point q = dao.getPoint(pointId);
		assertEquals(p.getX(), q.getX(), 0);
		assertEquals(p.getY(), q.getY(), 0);
		assertEquals(p.getZ(), q.getZ(), 0);
	}
	
	@Test
	public void test_uniqueness() throws SQLException {
		Point p1 = new Point(1, 2, 3);
		Point p2 = new Point(1, 2, 3);
		
		/* When creating two points with same coordinates,
		 * the DAO should reference the same */
		dao.create(p1);
		dao.create(p2);
		assertEquals(p1.getId(), p2.getId());
	}
	
	@Test
	public void test_equals(){
		Point p1 = new Point(0, 0, 0);
		Point p2 = new Point(0, 0, 0);
		assertEquals(0, p1.getId());
		assertEquals(0, p2.getId());
		assertTrue(p1.equals(p2));
	}
	
	@Test
	public void test_retrieve_selected_points() throws SQLException{
		Point p1 = new Point(1, 2, 3);
		Point p2 = new Point(4, 5, 6);
		dao.create(p1);
		dao.create(p2);
		
		assertTrue(dao.getSelectedPoints().isEmpty());
		p1.select();
		dao.update(p1);
		
		List<Point> selected = dao.getSelectedPoints();
		assertEquals(1, selected.size());
		assertTrue(p1.equals(selected.get(0)));
		assertFalse(p2.equals(selected.get(0)));
	}
}
