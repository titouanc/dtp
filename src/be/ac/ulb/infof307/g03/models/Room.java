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
	private String _name = null;
	@DatabaseField(foreign = true, canBeNull = false, foreignAutoRefresh = true)
	private Floor _floor = null;
	@ForeignCollectionField(eager = false, orderColumnName = "_index")
    private ForeignCollection<Binding> _bindings;
	@DatabaseField(foreign = true, canBeNull = true, foreignAutoRefresh = true, foreignAutoCreate = true)
	private Ground _ground;
	@DatabaseField(foreign = true, canBeNull = true, foreignAutoRefresh = true, foreignAutoCreate = true)
	private Roof _roof;
	@DatabaseField(foreign = true, canBeNull = true, foreignAutoRefresh = true, foreignAutoCreate = true)
	private Wall _wall;
	
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
	
	public Room(Floor floor){
		setFloor(floor);
	}
	
	public Room(String name, Floor floor){
		setName(name);
		setFloor(floor);
	}
	
	/**
	 * Set the floor for this group
	 * @param floor The floor at which this group will be situated
	 */
	public final void setFloor(Floor floor){
		_floor = floor;
	}
	
	/**
	 * @return The floor at which this group is situated
	 */
	public final Floor getFloor(){
		return _floor;
	}

	/**
	 * @return The name of this group
	 */
	public final String getName(){
		return _name;
	}
	
	/**
	 * Set the name for this group
	 * @param name The new name for this group
	 */
	public final void setName(String name){
		_name = name;
	}
	
	public final Wall getWall(){
		return _wall;
	}
	
	public final void setWall(Wall wall){
		_wall = wall;
	}
	
	public final Ground getGround(){
		return _ground;
	}
	
	public final void setGround(Ground gnd){
		_ground = gnd;
	}
	
	public final Roof getRoof(){
		return _roof;
	}
	
	public final void setRoof(Roof roof){
		_roof = roof;
	}
		
	public final List<Point> getPoints(){
		List<Point> res = new LinkedList<Point>();
		for (Binding b : _bindings)
			res.add(b.getPoint());
		return res;
	}
	
	public final ForeignCollection<Binding> getBindings(){
		return _bindings;
	}
	
	@Override
	public String toString(){
		if (_name == null)
			return "<>";
		return String.format("<%s>", getName());
	}
	
	@Override
	public String getUIDPrefix() {
		return "room";
	}
	
	public final void addPoints(Point...points){
		int i = _bindings.size();
		for (Point p : points){
			_bindings.add(new Binding(this, p, i));
			i++;
		}
	}
	
	public final List<Meshable> getMeshables(){
		List<Meshable> res = new ArrayList<Meshable>(3);
		if (_wall != null)
			res.add(_wall);
		if (_ground != null)
			res.add(_ground);
		if (_roof != null)
			res.add(_roof);
		return res;
	}
}
