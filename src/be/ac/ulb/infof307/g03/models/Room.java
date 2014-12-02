/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

/**
 * @author Titouan Christophe
 * @brief A group is a shape constituted of multiple shapes
 */
public class Room extends Geometric {
	@DatabaseField(canBeNull = true, unique = true)
	private String name = null;
	@DatabaseField(foreign = true, canBeNull = false, foreignAutoRefresh = true)
	private Floor floor = null;
	@ForeignCollectionField(eager = false, orderColumnName = "index")
    private ForeignCollection<Binding> bindings;
	@DatabaseField(foreign = true, canBeNull = true, foreignAutoRefresh = true, foreignAutoCreate = true)
	private Ground ground;
	@DatabaseField(foreign = true, canBeNull = true, foreignAutoRefresh = true, foreignAutoCreate = true)
	private Roof roof;
	@DatabaseField(foreign = true, canBeNull = true, foreignAutoRefresh = true, foreignAutoCreate = true)
	private Wall wall;
	
	/**
	 * Create a new group project with an empty name;
	 */
	public Room(){

	}
	
	/**
	 * Create a new named group
	 * @param name
	 */
	public Room(String name){
		setName(name);
	}
	
	/**
	 * @param floor The floor containing the room
	 */
	public Room(Floor floor){
		setFloor(floor);
	}
	
	/**
	 * @param name The name of the floor
	 * @param floor The floor containing the room
	 */
	public Room(String name, Floor floor){
		setName(name);
		setFloor(floor);
	}
	
	/**
	 * Set the floor for this group
	 * @param floor The floor at which this group will be situated
	 */
	public final void setFloor(Floor floor){
		this.floor = floor;
	}
	
	/**
	 * @return The floor at which this group is situated
	 */
	public final Floor getFloor(){
		return this.floor;
	}

	/**
	 * @return The name of this group
	 */
	public final String getName(){
		return this.name;
	}
	
	/**
	 * Set the name for this group
	 * @param name The new name for this group
	 */
	public final void setName(String name){
		this.name = name;
	}
	
	public final Wall getWall(){
		return this.wall;
	}
	
	public final void setWall(Wall wall){
		this.wall = wall;
	}
	
	public final Ground getGround(){
		return this.ground;
	}
	
	public final void setGround(Ground gnd){
		this.ground = gnd;
	}
	
	public final Roof getRoof(){
		return this.roof;
	}
	
	public final void setRoof(Roof roof){
		this.roof = roof;
	}
		
	public final List<Point> getPoints(){
		List<Point> res = new LinkedList<Point>();
		for (Binding b : bindings)
			res.add(b.getPoint());
		return res;
	}
	
	public final ForeignCollection<Binding> getBindings(){
		return bindings;
	}
	
	@Override
	public String toString(){
		if (this.name == null)
			return "<>";
		return String.format("<%s>", getName());
	}
	
	@Override
	public String getUIDPrefix() {
		return "room";
	}
	
	public final void addPoints(Point...points){
		int i = bindings.size();
		for (Point p : points){
			bindings.add(new Binding(this, p, i));
			i++;
		}
	}
	
	public final List<Area> getMeshables(){
		List<Area> res = new ArrayList<Area>(3);
		if (this.wall != null)
			res.add(this.wall);
		if (this.ground != null)
			res.add(this.ground);
		if (this.roof != null)
			res.add(this.roof);
		return res;
	}
}
