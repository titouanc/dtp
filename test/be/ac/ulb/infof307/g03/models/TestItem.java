package be.ac.ulb.infof307.g03.models;

import static org.junit.Assert.*;

import org.junit.Test;

import com.jme3.math.Vector3f;

public class TestItem extends DAOTest {

	@Test
	public void test_default() {
		Item item = new Item();
		assertEquals(new Vector3f(0,0,0),item.getNormalVector());
		assertEquals(new Vector3f(0,0,0),item.getPositionVector());
		assertNull(item.getFloor());
	}

}
