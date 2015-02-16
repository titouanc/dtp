/**
 * 
 */
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
import com.j256.ormlite.logger.LocalLog;
import com.j256.ormlite.support.ConnectionSource;

/**
 * @author titou
 *
 */
public class TestObjParser {
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
	public void test_import_face_vertex_only() throws SQLException, IOException {
		MasterDAO factory = new MasterDAO(this.db);
		new ObjParser("test/be/ac/ulb/infof307/g03/assets/cube.obj", factory).parse();
		
		List<Vertex> vertices = factory.getDao(Vertex.class).queryForAll();
		assertEquals(8, vertices.size());
		
		List<Triangle> triangles = factory.getDao(Triangle.class).queryForAll();
		assertEquals(12, triangles.size());
	}
	
	@Test
	public void test_import_face_vertex_and_normal() throws SQLException, IOException {
		MasterDAO factory = new MasterDAO(this.db);
		new ObjParser("test/be/ac/ulb/infof307/g03/assets/vache.obj", factory).parse();
		
		List<Vertex> vertices = factory.getDao(Vertex.class).queryForAll();
		assertEquals(1907, vertices.size());
		
		List<Triangle> triangles = factory.getDao(Triangle.class).queryForAll();
		assertEquals(2613, triangles.size());
	}
	
	@Test
	public void test_import_icosphere() throws SQLException, IOException {
		MasterDAO factory = new MasterDAO(this.db);
		new ObjParser("test/be/ac/ulb/infof307/g03/assets/icoSphere.obj", factory).parse();
		
		List<Vertex> vertices = factory.getDao(Vertex.class).queryForAll();
		assertEquals(42, vertices.size());
		
		List<Triangle> triangles = factory.getDao(Triangle.class).queryForAll();
		assertEquals(80, triangles.size());
	}
	
	@Test
	public void test_extract_integer(){
		assertEquals(322, ObjParser.extractVertexIndex("322//322"));
		assertEquals(322, ObjParser.extractVertexIndex("322/322/322"));
		assertEquals(322, ObjParser.extractVertexIndex("322"));
	}
}
