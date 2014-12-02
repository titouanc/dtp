/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * A Floor is a set of groups that are all on the same floor in a house.
 * @author Titouan Christophe
 */
@DatabaseTable
public class Floor extends Ordered {
	@DatabaseField
	private double height = 1.0;
	@DatabaseField
	private double baseHeight = 0.0;
	@ForeignCollectionField
	private ForeignCollection<Room> rooms;
	@ForeignCollectionField
	private ForeignCollection<Item> items;
	
	/**
	 * Constructor of the class Floor.
	 * The constructor is empty
	 */
	public Floor(){
		super();
	}
	
	/**
	 * @param height of the floor
	 */
	public Floor(double height){
		super();
		setHeight(height);
	}
	
	/**
	 * @param height The height to be set.
	 */
	public final void setHeight(double height){
		this.height = height;
	}
	
	/**
	 * @return The height of the floor (height between roof and floor)
	 */
	public final double getHeight(){
		return this.height;
	}
	
	/**
	 * @return The base height of this floor (absolute elevation of the floor)
	 */
	public final double getBaseHeight() {
		return this.baseHeight;
	}
	
	/**
	 * @param baseHeight
	 */
	public final void setBaseHeight(double baseHeight){
		this.baseHeight = baseHeight;
	}
	
	public final String toString(){
		return String.format("Floor %d", getIndex());
	}

	@Override
	public final String getUIDPrefix() {
		return "flr";
	}
	
	/**
	 * @return All the rooms of the floor
	 */
	public final ForeignCollection<Room> getRooms(){
		return this.rooms;
	}
}
