package be.ac.ulb.infof307.g03.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Interface for all objects managed by GeometryDAO
 * @author Titouan Christophe
 */
@DatabaseTable(daoClass=GeometricDAO.class)
public abstract class Geometric {
	@DatabaseField(generatedId = true)
	private int id = 0;
	
	/**
	 * @return The UID of the Geometric
	 */
	public abstract String getUIDPrefix();
	
	/**
	 * @return The geometric ID
	 */
	public final int getId(){
		return this.id;
	}
	
	/**
	 * @param id The new if of the geometric
	 */
	public final void setId(int id){
		this.id = id;
	}
	
	/**
	 * Every Geometric object has a unique identifier.
	 * @return The Unique identifier (UID) of the Geometric
	 */
	public final String getUID(){
		return String.format("%s-%d", getUIDPrefix(), getId());
	}
}
