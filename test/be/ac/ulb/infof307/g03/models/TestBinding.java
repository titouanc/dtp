/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import com.j256.ormlite.dao.ForeignCollection;

/**
 * @author Titouan Christophe
 *
 */
public class TestBinding extends DAOTest {
	private Room room;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		Floor floor = new Floor();
		dao.create(floor);
		room = new Room("room", floor);
		dao.create(room);
	}
	
	@Test
	public void test_room_bindings() throws SQLException {
		dao.create(new Binding(room, new Point(), 0));
		dao.refresh(room);
		assertEquals(1, room.getBindings().size());
	}
	
	@Test
	public void test_room_points() throws SQLException {
		dao.create(new Binding(room, new Point(), 0));
		dao.refresh(room);
		assertEquals(1, room.getPoints().size());
	}
	
	@Test
	public void test_point_rooms() throws SQLException {
		Point p = new Point(3, 4, 5);
		dao.create(p);
		dao.create(new Binding(room, p, 0));
		dao.refresh(p);
		assertEquals(1, p.getBoundRooms().size());
	}
} 