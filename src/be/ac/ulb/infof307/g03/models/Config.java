/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author Titouan Christophe
 * @brief Key:value store for configuration options
 */
@DatabaseTable
public class Config {
	@DatabaseField(id = true)
	private String key = "";
	@DatabaseField
	private String value = "";
	
	/**
	 * Create a new empty key:value pair
	 */
	public Config(){
		
	}
	
	/**
	 * Create a new Config value object
	 * @param key The configuration key
	 * @param value The configuration value for this key
	 */
	public Config(String key, String value){
		this.key = key;
		this.value = value;
	}
	
	/**
	 * @return The name of the configuration object
	 */
	public String getName(){
		return this.key;
	}
	
	/**
	 * @return The value of the configuration object
	 */
	public String getValue(){
		return this.value;
	}

	/**
	 * Set a new value for the configuration object
	 * @param value The new value
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
