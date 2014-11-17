/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import com.j256.ormlite.field.DatabaseField;

/**
 * @author Titouan Christophe
 * @brief A group is a shape constituted of multiple shapes
 */
public class Group extends Shape {
	@DatabaseField(canBeNull = true, unique = true)
	private String _name = null;
	@DatabaseField(foreign = true, canBeNull = true, foreignAutoRefresh = true)
	private Floor _floor = null;
	
	/**
	 * Create a new group project with an empty name;
	 */
	public Group(){

	}
	
	/**
	 * Create a new named group
	 * @param name
	 */
	public Group(String name){
		_name = name;
	}
	
	/**
	 * Set the floor for this group
	 * @param floor The floor at which this group will be situated
	 */
	public void setFloor(Floor floor){
		_floor = floor;
	}
	
	/**
	 * @return The floor at which this group is situated
	 */
	public Floor getFloor(){
		return _floor;
	}

	/**
	 * @return The name of this group
	 */
	public String getName(){
		return _name;
	}
	
	/**
	 * Set the name for this group
	 * @param name The new name for this group
	 */
	public void setName(String name){
		_name = name;
	}
	
	@Override
	public Boolean equalsByContent(Shape other) {
		if (other.getClass() != Group.class)
			return false;
		return true;
	}
	
	@Override
	public String toString(){
		if (_name == null)
			return "<>";
		return String.format("<%s>", getName());
	}
	
	@Override
	public Boolean isLeaf(){
		return false;
	}
	
	@Override
	public String getUID() {
		return String.format("grp-%d", getId());
	}
}
