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
		assertEquals(80, triangles.size());
	}

}
