package be.ac.ulb.infof307.g03.models;

import org.junit.After;
import org.junit.Before;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

/**
 * Base class for all tests that need a GeometryDAO
 * @author Titouan Christophe
 */
public abstract class DAOTest {

	protected GeometryDAO dao = null;

	@Before
	public void setUp() throws Exception {
		ConnectionSource db = new JdbcConnectionSource("jdbc:sqlite::memory:");
		GeometryDAO.migrate(db);
		dao = new GeometryDAO(db);
	}

	@After
	public void tearDown() throws Exception {
	}
}