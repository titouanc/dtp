package be.ac.ulb.infof307.g03.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Interface for all objects managed by GeometryDAO
 * @author Titouan Christophe
 */
@DatabaseTable
public abstract class Geometric {
	@DatabaseField(generatedId = true)
	private int _id = 0;
	
	public abstract String getUIDPrefix();
	
	public final int getId(){
		return _id;
	}
	
	public final void setId(int id){
		_id = id;
	}
	
	/**
	 * Every Geometric object has a unique identifier.
	 * @return The Unique identifier (UID) of the Geometric
	 */
	public final String getUID(){
		return String.format("%s-%d", getUIDPrefix(), getId());
	}
}
