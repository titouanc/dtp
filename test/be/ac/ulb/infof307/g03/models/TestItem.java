package be.ac.ulb.infof307.g03.models;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import com.jme3.math.Vector3f;

public class TestItem extends DAOTest {
	private Floor floor;
	private Entity ent;
	
	@Override
	@Before
	public void setUp() throws Exception{
		super.setUp();
		floor = new Floor(42);
		dao.create(floor);
		ent = new Entity();
		dao.create(ent);
	}
	
	@Test
	public void test_default() {
		Item item = new Item();
		assertEquals(new Vector3f(0,0,0), item.getNormalVector());
		assertEquals(new Vector3f(0,0,0), item.getPositionVector());
		assertNull(item.getFloor());
	}

	@Test(expected=SQLException.class)
	public void test_notnull_constraint() throws SQLException{
		Item item = new Item();
		dao.create(item);
	}
	
	@Test
	public void test_item_assoc() throws SQLException{
		Item item = new Item(floor, ent);
		dao.create(item);
		dao.refresh(floor);
		assertEquals(1, floor.getItems().size());
	
		Item copy = dao.getItem(item.getId());
		assertNotNull(copy);
	}
	
	@Test
	public void test_item_position_in_floor() throws SQLException{
		Floor atop = dao.createFloorOnTop(10);
		Item item = new Item(atop, ent);
		item.setPosition(new Vector3f(1, 2, 3));
		assertEquals(new Vector3f(1, 2, 45), item.getAbsolutePositionVector());
	}
}
