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
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author Titouan Christophe
 * @brief A group is a shape constituted of multiple shapes
 */
@DatabaseTable(daoClass=GeometricDAO.class)
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
	
	/**
	 * @return The room's wall
	 */
	public final Wall getWall(){
		return this.wall;
	}
	
	/**
	 * @param wall The room's wall
	 */
	public final void setWall(Wall wall){
		this.wall = wall;
	}
	
	/**
	 * @return The room's ground.
	 */
	public final Ground getGround(){
		return this.ground;
	}
	
	/**
	 * @param gnd The room's ground.
	 */
	public final void setGround(Ground gnd){
		this.ground = gnd;
	}
	
	/**
	 * @return The room's roof.
	 */
	public final Roof getRoof(){
		return this.roof;
	}
	
	/**
	 * @param roof The room's roof.
	 */
	public final void setRoof(Roof roof){
		this.roof = roof;
	}
	
	/**
	 * @return All of the room's points.
	 */
	public final List<Point> getPoints(){
		List<Point> res = new LinkedList<Point>();
		for (Binding b : bindings)
			res.add(b.getPoint());
		return res;
	}
	
	/**
	 * @return The room's binding
	 */
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
	
	/**
	 * Add multiple point to the room
	 * @param points The points to be added
	 */
	public final void addPoints(Point...points){
		int i = bindings.size();
		for (Point p : points){
			bindings.add(new Binding(this, p, i));
			i++;
		}
	}
	
	/**
	 * @return The room's area (wall,roof, ..)
	 */
	public final List<Area> getAreas(){
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
