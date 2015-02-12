package be.ac.ulb.infof307.g03.models;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

public class TestGeometricDAO {
	private ConnectionSource db, db2;
	
	@Before
	public void setUp() throws Exception {
		db = new JdbcConnectionSource("jdbc:sqlite::memory:");
		db2 = new JdbcConnectionSource("jdbc:sqlite::memory:");
		MasterDAO.migrate(db);
		MasterDAO.migrate(db2);
	}

	@After
	public void tearDown() throws Exception {
		db.close();
		db2.close();
	}
	
	@Test
	public void test_insert_point() throws SQLException {
		MasterDAO master = new MasterDAO(db);
		GeometricDAO<Point> dao = master.getDao(Point.class);
		int res = dao.insert(new Point(1, 42, 3.14));
		assertEquals(res, 1);
		Point p = dao.queryForId(1);
		assertEquals(p.getX(), 1, 0);
		assertEquals(p.getY(), 42, 0);
		assertEquals(p.getZ(), 3.14, 0);
	}
	
	@Test
	public void test_get_point_by_uid() throws SQLException {
		MasterDAO master = new MasterDAO(db);
		GeometricDAO<Point> dao = master.getDao(Point.class);
		int res = dao.insert(new Point(1, 42, 3.14));
		assertEquals(res, 1);
		Point p = (Point) master.getByUID("pnt-1");
		assertEquals(p.getX(), 1, 0);
		assertEquals(p.getY(), 42, 0);
		assertEquals(p.getZ(), 3.14, 0);
	}
	
	@Test
	public void test_copy_models() throws SQLException {
		MasterDAO master = new MasterDAO(db);
		GeometricDAO<Point> dao = master.getDao(Point.class);
		int res = dao.insert(new Point(1, 42, 3.14));
		assertEquals(res, 1);
		
		MasterDAO copy = new MasterDAO(db2);
		copy.copyFrom(master);
		System.out.println("Copied all from master to copy");
		dao = copy.getDao(Point.class);
		Point p = dao.queryForId(1);
		assertNotNull(p);
		assertEquals(p.getX(), 1, 0);
		assertEquals(p.getY(), 42, 0);
		assertEquals(p.getZ(), 3.14, 0);
	}
	
	/**
	 * Test that we can automatically build all room areas
	 * @throws SQLException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@Test
	public void test_generic_room_build() throws SQLException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		MasterDAO master = new MasterDAO(db);
		Floor flr = new Floor(7);
		master.getDao(Floor.class).insert(flr);
		
		Room room = new Room("Supayr");
		room.setFloor(flr);
		GeometricDAO<Room> roomDao = master.getDao(Room.class);
		roomDao.create(room);
		
		for (Class<? extends Area> klass : master.areaClasses){
			Constructor<? extends Area> constr = klass.getConstructor(Room.class);
			Area newArea = constr.newInstance(room);
			master.getDao(klass).insert(newArea);
		}
		roomDao.modify(room);
		roomDao.refresh(room);
		
		assertNotNull(room.getRoof());
		assertNotNull(room.getGround());
		assertNotNull(room.getWall());
		
		List<Roof> roofs = master.getDao(Roof.class).queryForAll();
		assertEquals(1, roofs.size());
		assertEquals(room.getUID(), roofs.get(0).getRoom().getUID());
		
		List<Ground> grounds = master.getDao(Ground.class).queryForAll();
		assertEquals(1, grounds.size());
		assertEquals(room.getUID(), grounds.get(0).getRoom().getUID());
		
		List<Wall> walls = master.getDao(Wall.class).queryForAll();
		assertEquals(1, walls.size());
		assertEquals(room.getUID(), walls.get(0).getRoom().getUID());
	}
	
	@Test
	public void test_changes() throws SQLException{
		MasterDAO master = new MasterDAO(db);
		MockObserver<Change> observer = new MockObserver<Change>();
		master.addObserver(observer);
		
		Floor flr = new Floor();
		GeometricDAO<Floor> dao = master.getDao(Floor.class);
		dao.insert(flr);
		master.notifyObservers();
		assertTrue(observer.hasBeenCalled());
		assertEquals(1, observer.getCallNumber());
		assertNotEquals(0, flr.getId());
	}
}

