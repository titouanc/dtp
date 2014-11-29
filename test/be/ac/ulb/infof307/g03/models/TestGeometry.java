package be.ac.ulb.infof307.g03.models;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;

public class TestGeometry {
	private ConnectionSource _db;
	
	@Before
	public void setUp() throws Exception {
		_db = new JdbcConnectionSource("jdbc:sqlite::memory:");
		GeometryDAO.migrate(_db);
	}

	@After
	public void tearDown() throws Exception {
		_db.close();
	}
	
	/**
	 * Create a new Room named "room" maid of 4 lines with 4 points 
	 * (in this order): 00 10 11 01.
	 * Create a Wall and a Ground object using the group.
	 * Insert everything in database
	 * @param geo The data access object
	 * @return The newly created Room
	 * @throws SQLException
	 */
	private Room create_a_room(GeometryDAO geo) throws SQLException{
		Floor floor = new Floor();
		Room room = new Room("room", floor);
		room.setWall(new Wall());
		room.setGround(new Ground());
		room.setRoof(new Roof());
		geo.create(room);
		geo.refresh(room);
		
		room.addPoints(
			new Point(0, 0, 0),
			new Point(0, 1, 0),
			new Point(1, 1, 0),
			new Point(1, 0, 0)
		);
		return room;
	}
	
	@Test
	public void test_dao_no_changes() throws SQLException {
		GeometryDAO geo = new GeometryDAO(_db);
		MockObserver<List<Change>> mock = new MockObserver<List<Change>>();
		create_a_room(geo);
		
		geo.addObserver(mock);
		geo.notifyObservers();
		assertNotNull(mock.changes);
		assertFalse(mock.changes.isEmpty());
	}
	
	@Test
	public void test_dao_changes() throws SQLException {
		GeometryDAO geo = new GeometryDAO(_db);
		MockObserver<List<Change>> mock = new MockObserver<List<Change>>();
		Room room = create_a_room(geo);
		geo.notifyObservers();
		
		geo.addObserver(mock);
		assertFalse(mock.hasBeenCalled());
		
		geo.delete(room);
		geo.notifyObservers();
		assertNotNull(mock.changes);
		
		Change firstChange = mock.changes.get(0);
		assertFalse(firstChange.isCreation());
		assertFalse(firstChange.isUpdate());
		assertTrue(firstChange.isDeletion());
		
		mock.reset();
		
		geo.create(new Room("Hello", new Floor()));
		geo.notifyObservers();
		assertNotNull(mock.changes);
		assertEquals(1, mock.changes.size());
		assertTrue(mock.changes.get(0).isCreation());
		assertEquals("Hello", ((Room) mock.changes.get(0).getItem()).getName());
	}
	
	@Test
	public void test_dao_no_changes_before_register() throws SQLException {
		GeometryDAO geo = new GeometryDAO(_db);
		MockObserver<List<Change>> mock = new MockObserver<List<Change>>();
		create_a_room(geo);
		
		geo.notifyObservers();
		assertNull(mock.changes);
		geo.addObserver(mock);
		geo.notifyObservers();
		assertNull(mock.changes);
	}
	
	@Test
	public void test_close_point() throws SQLException{
		GeometryDAO geo = new GeometryDAO(_db);
		Point origin = new Point(0, 0, 0);
		geo.create(origin);
		Point p = new Point(0.3, 0, 0);
		
		Point res = geo.findClosePoint(p, 0.5);
		assertNotNull(res);
		assertTrue(res.equals(origin));
	}
	
	@Test
	public void test_close_point_too_high() throws SQLException{
		GeometryDAO geo = new GeometryDAO(_db);
		Point origin = new Point(0, 0, 0);
		geo.create(origin);
		Point p = new Point(0.3, 0, 1);
		
		Point res = geo.findClosePoint(p, 0.5);
		assertNull(res);
	}
}

