package be.ac.ulb.infof307.g03.models;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.Test;


public class TestFloor extends DAOTest {
	/**
	 * Helper function that create 3 consecutive floors of height 1.0
	 * @throws SQLException
	 */
	private void create3Floors() throws SQLException{
		for (int i=0; i<3; i++){
			Floor floor = new Floor();
			floor.setIndex(i);
			floor.setHeight(1);
			dao.getDao(Floor.class).create(floor);
		}
	}
	
	@Test
	public void test_floor_defaults() {
		Floor floor = new Floor();
		assertEquals(1, floor.getHeight(), 0);
		assertEquals(0, floor.getBaseHeight(), 0);
		assertEquals(0, floor.getIndex());
	}
	
	@Test(expected=SQLException.class)
	public void test_floor_order_unique() throws SQLException{
		Floor f1 = new Floor(),
		      f2 = new Floor();
		assertEquals(f1.getIndex(), f2.getIndex());
		dao.getDao(Floor.class).create(f1);
		dao.getDao(Floor.class).create(f2);
	}
	
	@Test
	public void test_floor_recompute() throws SQLException {
		create3Floors();
		List<Floor> floors = dao.getDao(Floor.class).getFloors();
		assertEquals(0, floors.get(0).getBaseHeight(), 0);
		assertEquals(1, floors.get(1).getBaseHeight(), 0);
		assertEquals(2, floors.get(2).getBaseHeight(), 0);
	}
	
	@Test
	public void test_above_below() throws SQLException {
		create3Floors();
		Floor first = dao.getFloor(1);
		assertEquals(2, dao.getFloorsAbove(first).size());
		assertEquals(0, dao.getFloorsBelow(first).size());
		
		Floor middle = dao.getFloor(2);
		assertEquals(1, dao.getFloorsAbove(middle).size());
		assertEquals(1, dao.getFloorsBelow(middle).size());
		
		Floor last = dao.getFloor(3);
		assertEquals(0, dao.getFloorsAbove(last).size());
		assertEquals(2, dao.getFloorsBelow(last).size());
	}
	
	@Test
	public void test_above_below_null() throws SQLException {
		create3Floors();
		assertEquals(3, dao.getFloorsAbove(null).size());
		assertEquals(3, dao.getFloorsBelow(null).size());
	}
	
	@Test
	public void test_floor_delete() throws SQLException {
		create3Floors();
		List<Floor> floors = dao.getFloors();
		dao.delete(floors.get(0));
		
		floors = dao.getFloors();
		assertEquals(2, floors.size());
		
		Floor bottom = floors.get(0);
		assertEquals(2, bottom.getId());
		assertEquals(0, bottom.getIndex());
	}
}
