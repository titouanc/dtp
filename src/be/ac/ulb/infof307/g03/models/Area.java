/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * An abstract class for all elements that use an inner Room
 * (walls, grounds, ...)
 * @author Titouan Christophe
 */
public abstract class Area extends Meshable {
	@ForeignCollectionField
	private ForeignCollection<Room> room;
	
	/**
	 * Constructor of the class Meshable
	 * It creates a new group
	 */
	public Area() {}
	
	public Area(Room forRoom){}
	
	public Room getRoom(){
		List<Room> room = new ArrayList<Room>(this.room);
		if (room.size() != 1)
			throw new AssertionError("No room contains " + getUID());
		return room.get(0);
	}
	
	public final List<Point> getPoints(){
		return getRoom().getPoints();
	}
}
