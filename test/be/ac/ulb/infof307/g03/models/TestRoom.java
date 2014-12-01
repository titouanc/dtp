/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Titouan Christophe
 *
 */
public class TestRoom extends DAOTest {
	private Floor floor = null;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		floor = new Floor();
		dao.create(floor);
	}
	
	@Test
	public void test_multiple_unnamed_rooms () throws SQLException{
		Room r1 = new Room(floor);
		Room r2 = new Room(floor);
		dao.create(r1);
		dao.create(r2);
		
		assertEquals(1, r1.getId());
		assertEquals(2, r2.getId());
		assertEquals("<>", r1.toString());
	}
	
	@Test
	public void test_room_name() throws SQLException{
		Room room = new Room(floor);
		dao.create(room);
		
		assertTrue(room.getId() != 0);
		Room copy = dao.getRoom(room.getId());
		copy.setName("PaKeBot");
		dao.update(copy);
		
		room = dao.getGroup(copy.getName());
		assertEquals("PaKeBot", room.getName());
	}
}
