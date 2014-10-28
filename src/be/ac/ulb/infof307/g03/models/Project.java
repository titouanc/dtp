/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

/**
 * @author Titouan Christophe
 * @brief Homeplan Project data structure
 */

public class Project {
	private ConnectionSource _db = null;
	private Dao<Config, String> _config = null;

	public Project() {

	}

	public void create(String filename) throws SQLException {
		load(filename);
		Config.migrate(_db);
		Geometry.migrate(_db);
	}

	public void load(String filename) throws SQLException {
		_db = new JdbcConnectionSource("jdbc:sqlite:" + filename);
		_config = DaoManager.createDao(_db, Config.class);
	}

	public String getName() throws SQLException {
		return _config.queryForId("name").getValue();
	}

	public Geometry getGeometry() throws SQLException {
		return new Geometry(_db);
	}
}
