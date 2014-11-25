/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.sql.SQLException;
import java.util.List;
import java.util.Observable;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * @author Titouan Christophe
 * @brief Homeplan Project data structure
 */

public class Project extends Observable {
	private ConnectionSource _db = null;
	private Dao<Config, String> _config = null;
	private GeometryDAO _geo = null;
	private String _filename = null;

	/**
	 * Create a new Project object (needs to be initialized with load() or create())
	 */
	public Project() {

	}

	/**
	 * Create a new project
	 * @param filename The destination filename of the new project
	 * @throws SQLException
	 */
	public void create(String filename) throws SQLException {
		load(filename);
		TableUtils.createTableIfNotExists(_db, Config.class);
		GeometryDAO.migrate(_db);
		_filename = filename;
	}

	/**
	 * Open an existing project.
	 * @param filename The location of the project's database
	 * @throws SQLException
	 */
	public void load(String filename) throws SQLException {
		_db = new JdbcConnectionSource("jdbc:sqlite:" + filename);
		_config = DaoManager.createDao(_db, Config.class);
		_filename = filename;
	}
	
	/**
	 * @return True if the project is not an in-memory database
	 */
	public Boolean isOnDisk(){
		return ! _filename.equals(":memory:");
	}
	
	/**
	 * @return Return the filename for this project
	 */
	public String getFilename(){
		return _filename;
	}
	
	/**
	 * Copy current project to a new database,
	 * then set this new database as the current database
	 * @param filename The new database filename
	 * @throws SQLException
	 */
	public int saveAs(String filename) throws SQLException {
		/* Open new DB handler */
		ConnectionSource newDB = new JdbcConnectionSource("jdbc:sqlite:" + filename);
		Dao<Config, String> newDAO = DaoManager.createDao(newDB, Config.class);
		
		/* Migrate schemas */
		TableUtils.createTableIfNotExists(newDB, Config.class);
		GeometryDAO.migrate(newDB);
		
		/* Copy data */
		int res = 0;
		res += new GeometryDAO(newDB).copyFrom(getGeometryDAO());
		for (Config c : _config.queryForAll())
			newDAO.create(c);
		
		/* Replace current DB handler with new one */
		getGeometryDAO().resetConnection(newDB);
		_db.close();
		_db = newDB;
		_config = newDAO;
		
		_filename = filename;
		
		return res;
	}

	/**
	 * Retrieve a configuration value from the project.
	 * If the configuration key is not found, create an empty one.
	 * @param name The configuration key
	 * @return The configuration value
	 */
	public String config(String name) {
		try {
			Config entry = _config.queryForId(name);
			if (entry == null){
				entry = new Config(name, "");
				_config.create(entry);
			}
			return entry.getValue();
		} catch (SQLException err){
			return "";
		}
	}
	
	/**
	 * Create a new configuration key,value pair in the project.
	 * If the configuration key already exists, only update its value.
	 * @param name The configuration key
	 * @param value The configuration value
	 * @return The new value for this configuration key
	 */
	public String config(String name, String value) {
		try {
			Config entry = _config.queryForId(name);
			if (entry != null){
				entry.setValue(value);
				_config.update(entry);
			} else {
				entry = new Config(name, value);
				_config.create(entry);
			}
			setChanged();
			notifyObservers(entry);
			return entry.getValue();
		} catch (SQLException err){
			return "";
		}
	}
	
	/**
	 * @note The object returned by this method is a singleton.
	 * @return A Data Access Object on all geometric models.
	 * @throws SQLException
	 */
	public GeometryDAO getGeometryDAO() throws SQLException {
		if (_geo == null)
			_geo = new GeometryDAO(_db);
		return _geo;
	}
}
