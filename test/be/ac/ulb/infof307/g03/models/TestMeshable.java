package be.ac.ulb.infof307.g03.models;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestMeshable extends DAOTest {
	private Room room = null;
	
	@Before
	public void setUp() throws Exception{
		super.setUp();
		Floor f = new Floor();
		dao.create(f);
		room = new Room("room", f);
		dao.create(room);
	}
	
	@Test
	public void test_wall() throws SQLException{
		Wall wall = new Wall();
		
		assertEquals(1, dao.create(wall));
		assertTrue(wall.isVisible());
		assertEquals("wal-1", wall.getUID());
		
		room.setWall(wall);
		dao.update(room);
		wall.hide();
		dao.update(wall);
		wall = dao.getWall(1);
		assertFalse(wall.isVisible());
		
		wall.show();
		dao.update(wall);
		wall = dao.getWall(1);
		assertTrue(wall.isVisible());
		
		assertEquals(1, wall.getId());
		assertEquals(0, wall.getPoints().size());
		assertEquals(1, dao.getWalls().size());
		
		wall.select();
		assertTrue(wall.isSelected());
		wall.deselect();
		assertFalse(wall.isSelected());
	}
	
	@Test
	public void test_ground() throws SQLException{
		Ground ground = new Ground();
		assertEquals(1, dao.create(ground));
		
		room.setGround(ground);
		dao.update(room);
		dao.refresh(ground);
		assertEquals(1, ground.getId());
		assertEquals(0, ground.getPoints().size());
		assertEquals(1, dao.getGrounds().size());
		
		assertFalse(ground.isSelected());
		ground.toggleSelect();
		assertTrue(ground.isSelected());
		ground.toggleSelect();
		assertFalse(ground.isSelected());
	}
	
	@Test
	public void test_retrieve_selected() throws SQLException {
		Ground gnd = new Ground();
		dao.create(gnd);
		Wall wall = new Wall();
		dao.create(wall);
		Roof roof = new Roof();
		dao.create(roof);

		assertTrue(dao.getSelectedMeshables().isEmpty());
		
		wall.select();
		dao.update(wall);
		assertEquals(1, dao.getSelectedMeshables().size());
		
		roof.select();
		dao.update(roof);
		assertEquals(2, dao.getSelectedMeshables().size());
		
		wall.deselect();
		dao.update(wall);
		assertEquals(1, dao.getSelectedMeshables().size());
	}
}
