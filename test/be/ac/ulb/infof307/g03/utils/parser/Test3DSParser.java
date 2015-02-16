package be.ac.ulb.infof307.g03.utils.parser;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import be.ac.ulb.infof307.g03.models.Entity;
import be.ac.ulb.infof307.g03.models.MasterDAO;
import be.ac.ulb.infof307.g03.models.Triangle;
import be.ac.ulb.infof307.g03.models.Vertex;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

public class Test3DSParser {
	private ConnectionSource db;
	
	@Before
	public void setUp() throws Exception {
		db = new JdbcConnectionSource("jdbc:sqlite::memory:");
		MasterDAO.migrate(db);
	}

	@After
	public void tearDown() throws Exception {
		db.close();
	}

	@Test
	public void test_parseicosphere() throws SQLException, IOException {
		MasterDAO factory = new MasterDAO(this.db);
		new A3DSParser("test/be/ac/ulb/infof307/g03/assets/icoSphere.3ds", factory).parse();
		
		List<Vertex> vertices = factory.getDao(Vertex.class).queryForAll();
		assertEquals(42, vertices.size());
		
		List<Triangle> triangles = factory.getDao(Triangle.class).queryForAll();
		assertEquals(160, triangles.size()); //This version has 2 faces (two sides per triangle)
	}
	
	@Test
	public void test_parsewatertower() throws SQLException, IOException {
		MasterDAO factory = new MasterDAO(this.db);
		new A3DSParser("test/be/ac/ulb/infof307/g03/assets/watertower.3ds", factory).parse();
		
		List<Vertex> vertices = factory.getDao(Vertex.class).queryForAll();
		assertEquals(1304, vertices.size());
		
		List<Triangle> triangles = factory.getDao(Triangle.class).queryForAll();
		assertEquals(1868, triangles.size());
	}
	
	@Test
	public void test_parseInt() {
		byte[] u8 = {0x01};
		assertEquals(1, A3DSParser.parseInt(u8));
		byte[] u16 = {0x01, 0x00};
		assertEquals(1, A3DSParser.parseInt(u16));
		byte[] u32 = {0x01, 0x00, 0x00, 0x00};
		assertEquals(1, A3DSParser.parseInt(u32));
		byte[] u32_2 = {0x00, 0x01, 0x00, 0x00};
		assertEquals(256, A3DSParser.parseInt(u32_2));
		byte[] u32_3 = {(byte) 0xca, (byte) 0xde, (byte) 0xfe, (byte) 0xca};
		assertEquals((long) 0xcafedeca, (long) A3DSParser.parseInt(u32_3));
	}
}
