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

public class TestOrdered {
	private ConnectionSource db;
	
	@Before
	public void setUp() throws Exception {
		db = new JdbcConnectionSource("jdbc:sqlite::memory:");
		TableUtils.createTableIfNotExists(db, ConcreteOrdered.class);
	}

	@After
	public void tearDown() throws Exception {
		db.close();
	}
	
	@Test
	public void test_is_geometric_dao() throws SQLException {
		Dao<ConcreteOrdered, Integer> dao = DaoManager.createDao(db, ConcreteOrdered.class);
		assertEquals(GeometricDAO.class, dao.getClass());
	}

	@Test
	public void test_order() throws SQLException {
		Dao<ConcreteOrdered, Integer> dao = DaoManager.createDao(db, ConcreteOrdered.class);
		ConcreteOrdered first = new ConcreteOrdered();
		dao.create(first);
		
		ConcreteOrdered second = new ConcreteOrdered();
		second.setIndex(2);
		dao.create(second);
		
		ConcreteOrdered found = dao.queryForFirst(first.getQueryForFollowing(dao));
		assertNotNull(found);
		assertEquals(second.getUID(), found.getUID());
		
		dao.delete(second);
		found = dao.queryForFirst(first.getQueryForFollowing(dao));
		assertNull(found);
	}

}
