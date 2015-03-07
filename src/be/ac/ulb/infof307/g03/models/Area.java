/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.ForeignCollectionField;

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
	
	/**
	 * @param forRoom ??
	 */
	public Area(Room forRoom){
		// TODO forRoom not used ?
	}
	
	/**
	 * @return The area's room based on UID
	 */
	public Room getRoom(){
		List<Room> room = new ArrayList<Room>(this.room);
		if (room.size() != 1)
			throw new AssertionError("No room contains " + getUID());
		return room.get(0);
	}
	
	/**
	 * @return The area's room points
	 */
	public final List<Point> getPoints(){
		return getRoom().getPoints();
	}
	
	/**
	 * @return The area surface
	 */
	public double getSurface(){
		double surface = 0;
		List<Point> points =  getPoints();
		for (int i = 0; i< points.size(); i+=2){
			surface += points.get(i+1).getX()*(points.get(i+2).getY()-points.get(i).getY()) + points.get(i+1).getY()*(points.get(i).getX()-points.get(i+2).getX());
		}
		surface = surface/2;
		return surface;
	}
	
}
