/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.sql.SQLException;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

/**
 * @author Titouan Christophe
 * @brief Key:value store for configuration options
 */
@DatabaseTable
public class Config {
	@DatabaseField(id = true)
	private String _key;
	@DatabaseField
	private String _value;

	public static void migrate(ConnectionSource database) throws SQLException {
		TableUtils.createTableIfNotExists(database, Config.class);
	}
	
	public Config(){
		
	}
	
	/**
	 * Create a new Config value object
	 * @param key The configuration key
	 * @param value The configuration value for this key
	 */
	public Config(String key, String value){
		_key = key;
		_value = value;
	}
	
	/**
	 * @return The value of the configuration object
	 */
	public String getValue(){
		return _value;
	}

	/**
	 * Set a new value for the configuration object
	 * @param value The new value
	 */
	public void setValue(String value) {
		_value = value;
	}
}
