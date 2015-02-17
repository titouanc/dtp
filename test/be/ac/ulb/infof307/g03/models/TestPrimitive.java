package be.ac.ulb.infof307.g03.models;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TestPrimitive {
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test_cube_primitive() {
		Primitive primitive = new Primitive(new Entity(), Primitive.CUBE);
		assertEquals(72, primitive.getVertices().length);
		assertEquals(36, primitive.getIndexes().length);
	}

}
