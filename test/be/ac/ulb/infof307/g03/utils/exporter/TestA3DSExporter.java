package be.ac.ulb.infof307.g03.utils.exporter;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.junit.Test;

import be.ac.ulb.infof307.g03.io.exporter.A3DSExporter;
import be.ac.ulb.infof307.g03.io.importer.A3DSParser;
import be.ac.ulb.infof307.g03.models.*;

public class TestA3DSExporter extends DAOTest {
	public static String filename = "TestA3DSExporter.3ds";
	
	public void test_string_bytes(){
		String s = "*";
		byte[] bytes = s.getBytes();
		assertEquals(42, bytes[0]);
		assertEquals(1, bytes.length);
	}
	
	@Test
	public void test_convert_int2(){
		int[] expected = {0x12, 0x34};
		int[] converted = A3DSExporter.convertInt(0x3412, 2);
		assertArrayEquals(expected, converted);
	}
	
	@Test
	public void test_convert_int_3d3d(){
		int[] expected = {0x3d, 0x3d};
		int[] converted = A3DSExporter.convertInt(0x3d3d, 2);
		assertArrayEquals(expected, converted);
	}
	
	@Test
	public void test_convert_int4(){
		int[] expected = {0x12, 0x34, 0x56, 0x78};
		int[] converted = A3DSExporter.convertInt(0x78563412, 4);
		assertArrayEquals(expected, converted);
	}
	
	@Test
	public void test_export() throws IOException, SQLException {
		Entity ent = new Entity("Un cube");
		this.master.getDao(Entity.class).create(ent);
		Primitive prim = new Primitive(ent, Primitive.CUBE);
		this.master.getDao(Primitive.class).create(prim);
		
		this.master.getDao(Entity.class).refresh(ent);
		new A3DSExporter().export(new File(filename), ent);
		
		new A3DSParser(filename, this.master).parse();
		assertEquals(prim.getVertices().length/3, this.master.getDao(Vertex.class).queryForAll().size());
		assertEquals(prim.getIndexes().length/3, this.master.getDao(Triangle.class).queryForAll().size());
	}

}
