package be.ac.ulb.infof307.g03.models;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

public class TestGroup {
	private ConnectionSource _db;
	private Dao<Group, Integer> _dao;

	@Before
	public void setUp() throws Exception {
		_db = new JdbcConnectionSource("jdbc:sqlite::memory:");
		Group.migrate(_db);
		_dao = DaoManager.createDao(_db, Group.class);
	}

	@After
	public void tearDown() throws Exception {
		_db.close();
	}

	@Test
	public void test_create_group() throws SQLException {
		Line.migrate(_db);
		Group g = new Group();
		_dao.create(g);
		g = _dao.queryForId(g.getId());
		g.addShape(new Line(new Point(3, 4, 5), new Point(-1, -2, -3)));
		g.addShape(new Line(new Point(1, 2, 3), new Point(-3, -4, -5)));
		_dao.update(g);
		
		List<ShapeRecord> shapes = new ArrayList<ShapeRecord>(g.getShapes());
		assertEquals(2, shapes.size());
	}

}
