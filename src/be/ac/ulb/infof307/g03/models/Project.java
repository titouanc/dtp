/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;

import be.ac.ulb.infof307.g03.utils.Log;

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
	private ConnectionSource db = null; 
	private Dao<Config, String> config = null;
	private MasterDAO geo = null;
	private String filename = null;

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
		TableUtils.createTableIfNotExists(this.db, Config.class);
		MasterDAO.migrate(this.db);
		
		Floor initialFloor = new Floor(7);
		getGeometryDAO().getDao(Floor.class).create(initialFloor);
		
		this.filename = filename;
		config("floor.current", initialFloor.getUID());
		config("edition.mode", "world");
		config("camera.mode", "2D");
		config("mouse.mode", "dragSelect");
		config("creation.time", (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()));
		config("version.current", "0.3.0");
	}

	/**
	 * Open an existing project.
	 * @param filename The location of the project's database
	 * @throws SQLException
	 */
	public void load(String filename) throws SQLException {
		this.db = new JdbcConnectionSource("jdbc:sqlite:" + filename);
		this.config = DaoManager.createDao(this.db, Config.class);
		this.filename = filename;
	}
	
	/**
	 * @return True if the project is not an in-memory database
	 */
	public Boolean isOnDisk(){
		return ! this.filename.equals(":memory:");
	}
	
	/**
	 * @return Return the filename for this project
	 */
	public String getFilename(){
		return this.filename;
	}
	
	/**
	 * Copy current project to a new database,
	 * then set this new database as the current database
	 * @param filename The new database filename
	 * @throws SQLException
	 */
	public void saveAs(String filename) throws SQLException {
		/* Open new DB handler */
		ConnectionSource newDB = new JdbcConnectionSource("jdbc:sqlite:" + filename);
		Dao<Config, String> newDAO = DaoManager.createDao(newDB, Config.class);
		
		/* Migrate schemas */
		TableUtils.createTableIfNotExists(newDB, Config.class);
		MasterDAO.migrate(newDB);
		
		/* Copy data */
		new MasterDAO(newDB).copyFrom(getGeometryDAO());
		for (Config c : this.config.queryForAll())
			newDAO.create(c);
		
		/* Replace current DB handler with new one */
		getGeometryDAO().resetConnection(newDB);
		this.db.close();
		this.db = newDB;
		this.config = newDAO;
		
		this.filename = filename;
	}

	/**
	 * Retrieve a configuration value from the project.
	 * If the configuration key is not found, create an empty one.
	 * @param name The configuration key
	 * @return The configuration value
	 */
	public String config(String name) {
		try {
			Config entry = this.config.queryForId(name);
			if (entry == null){
				entry = new Config(name, "");
				this.config.create(entry);
			}
			return entry.getValue();
		} catch (SQLException err){
			Log.warn("SQLException in Project.config");
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
			Config entry = this.config.queryForId(name);
			if (entry != null){
				entry.setValue(value);
				this.config.update(entry);
			} else {
				entry = new Config(name, value);
				this.config.create(entry);
			}
			setChanged();
			notifyObservers(entry);
			return entry.getValue();
		} catch (SQLException err){
			Log.warn("SQLException in Project.config");
			return "";
		}
	}
	
	/**
	 * @note The object returned by this method is a singleton.
	 * @return A Data Access Object on all geometric models.
	 * @throws SQLException
	 */
	public MasterDAO getGeometryDAO() throws SQLException {
		if (this.geo == null)
			this.geo = new MasterDAO(this.db);
		return this.geo;
	}
}
