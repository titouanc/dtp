/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.field.DatabaseField;

/**
 * @author Titouan Christophe
 * @brief A point bound to a Room
 */
public class Binding extends Ordered {
	/**
	 * First point of the line
	 */
	@DatabaseField(canBeNull = false, foreign = true, uniqueCombo = true)
	private Room _room = null;
	/**
	 * Second point of the line
	 */
	@DatabaseField(canBeNull = false, foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
	private Point _point = null;
	
	public Binding(){
		super();
	}
	
	public Binding(Room room, Point point, int index) {
		super();
		setRoom(room);
		setPoint(point);
		setIndex(index);
	}

	public void setPoint(Point point) {
		_point = point;
	}
	
	public Point getPoint(){
		return _point;
	}

	public void setRoom(Room room) {
		_room = room;
	}
	
	public Room getRoom(){
		return _room;
	}
	
	@Override
	public String toString(){
		return String.format("Binding %d", getId());
	}

	@Override
	public String getUIDPrefix() {
		return "bind";
	}
}