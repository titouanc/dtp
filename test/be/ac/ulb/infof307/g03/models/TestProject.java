package be.ac.ulb.infof307.g03.models;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestProject {
	/* If a file is created with this name, it will be deleted by tearDown() */
	private final String FILENAME = "Project.testdb";
	private Project proj;
	
	@Before
	public void setUp() throws Exception {
		proj = new Project();
		proj.create(":memory:");
	}

	@After
	public void tearDown() throws Exception {
		File testFile = new File(FILENAME);
		if (testFile.exists())
			testFile.delete();
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
	
	/**
	 * Test the Observable behavior
	 */
	@Test
	public void test_project_observable(){
		MockObserver<Config> mock = new MockObserver<Config>();
		proj.addObserver(mock);
		proj.config("Hello", "World");
		assertEquals(1, mock.getCallNumber());
		assertEquals("Hello", mock.changes.getName());
		assertEquals("World", mock.changes.getValue());
	}
	
	/**
	 * Test that Project.getGeometryDAO() is always the same object
	 * @throws SQLException
	 */
	@Test
	public void test_geoDAO_is_singleton() throws SQLException {
		GeometryDAO geo = proj.getGeometryDAO();
		GeometryDAO geo2 = proj.getGeometryDAO();
		assertEquals(geo, geo2);
	}
	
	/**
	 * Test that saveAs creates a new file
	 * @throws SQLException
	 */
	@Test
	public void test_saveAs() throws SQLException{
		proj.getGeometryDAO().create(new Point(0, 0, 0));
		proj.saveAs(FILENAME);
		assertTrue(new File(FILENAME).exists());
	}
	
	/**
	 * Test that config values are copied
	 * @throws SQLException
	 */
	@Test
	public void test_saveAs_config() throws SQLException {
		proj.config("Hello", "World");
		proj.saveAs(FILENAME);
		
		Project copy = new Project();
		copy.load(FILENAME);
		assertEquals("World", copy.config("Hello"));
	}
	
	/**
	 * Test that the database handler of a Project has been
	 * replaced by a new one
	 * @throws SQLException
	 */
	@Test
	public void test_saveAs_changedHandler() throws SQLException {
		GeometryDAO dao = proj.getGeometryDAO();
		/* Pre condition: the project is empty */
		assertEquals(1, dao.getFloors().size());
		/* Save copy to file */
		proj.saveAs(FILENAME);
		/* THEN create a floor */
		Floor newFloor = new Floor();
		newFloor.setIndex(2);
		dao.create(newFloor);
		/* Open the newly created file as a new projet */
		Project copy = new Project();
		copy.load(FILENAME);
		/* It should contain the created Floor */
		assertEquals(2, copy.getGeometryDAO().getFloors().size());
	}
}
