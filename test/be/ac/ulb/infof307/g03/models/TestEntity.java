package be.ac.ulb.infof307.g03.models;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.jme3.math.Vector3f;

public class TestEntity extends DAOTest {

	@Test
	public void test_empty_entity() throws SQLException {
		Entity ent = new Entity();
		master.getDao(Entity.class).create(ent);
		assertNotEquals(0, ent.getId());
		master.getDao(Entity.class).refresh(ent);
		assertEquals(0, ent.getPrimitives().size());
		assertEquals("", ent.getName());
	}

	@Test
	public void test_entity_with_primitive() throws SQLException{
		Entity ent = new Entity();
		master.getDao(Entity.class).create(ent);
		Primitive prim = new Primitive(ent, Primitive.CUBE);
		master.getDao(Primitive.class).create(prim);
		master.getDao(Entity.class).refresh(ent);
		
		List<Primitive> prims = new ArrayList<Primitive>(ent.getPrimitives());
		assertEquals(1, prims.size());
		assertEquals(prim.getId(), prims.get(0).getId());
	}
	
	@Test
	public void test_primitives_defaults() {
		Entity ent = new Entity();
		Primitive prim = new Primitive(ent, Primitive.SPHERE);
		Vector3f zero = new Vector3f(0f, 0f, 0f);
		
		assertEquals(zero, prim.getTranslation());
		assertEquals(zero, prim.getRotation());
	}
}
