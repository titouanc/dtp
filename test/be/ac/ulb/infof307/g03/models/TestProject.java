package be.ac.ulb.infof307.g03.models;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestProject {
	private Project proj;
	
	@Before
	public void setUp() throws Exception {
		proj = new Project();
		proj.create(":memory:");
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Create a new config value, assert its value is good.
	 * Then change the value, assert the value has changed too
	 * @throws SQLException
	 */
	@Test
	public void test_config() throws SQLException {
		assertEquals("NAME", proj.config("name", "NAME"));
		assertEquals("NAME", proj.config("name"));
		assertEquals("NONAME", proj.config("name", "NONAME"));
		assertEquals("NONAME", proj.config("name"));
	}
	
	/**
	 * Attempt to retrieve an inexistant config value, assert it is an empty string
	 * @throws SQLException
	 */
	@Test
	public void test_auto_create() throws SQLException {
		assertEquals("", proj.config("inexistant_key"));
	}
	
	@Test
	public void test_project_observable(){
		MockObserver<Config> mock = new MockObserver<Config>();
		proj.addObserver(mock);
		proj.config("Hello", "World");
		assertEquals(1, mock.getCallNumber());
		assertEquals("Hello", mock.changes.getName());
		assertEquals("World", mock.changes.getValue());
	}
	
	@Test
	public void test_geoDAO_is_singleton() throws SQLException {
		GeometryDAO geo = proj.getGeometryDAO();
		GeometryDAO geo2 = proj.getGeometryDAO();
		assertEquals(geo, geo2);
	}
}
