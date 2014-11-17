/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * An abstract class for all elements that use an inner Group
 * (walls, grounds, ...)
 * @author Titouan Christophe
 */
@DatabaseTable
public abstract class Grouped implements Geometric {
	@DatabaseField(generatedId = true)
	private int _id = 0;
	
	@DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
	private Group _group = null;
	
	@DatabaseField
	private Boolean _visible = true;
	
	@DatabaseField
	private Boolean _selected = false;
	
	/**
	 * Constructor of the class Grouped
	 * It creates a new group
	 */
	public Grouped() {
		_group = new Group();
	}
	
	/**
	 * Constructor of the class Grouped
	 * @param group The group to be grouped
	 */
	public Grouped(Group group){
		setGroup(group);
	}

	/**
	 * @return The inner group of this item
	 */
	public Group getGroup(){
		return _group;
	}
	
	/**
	 * Set the inner group for this grouped item
	 * @param group
	 */
	public void setGroup(Group group){
		_group = group;
	}
	
	/**
	 * @return The identifier of this Grouped item
	 */
	public int getId(){
		return _id;
	}
	
	/**
	 * Set visibility to false;
	 */
	public void hide(){
		_visible = false;
	}
	
	/**
	 * Set visibility to true;
	 */
	public void show(){
		_visible = true;
	}
	
	/**
	 * Select object
	 */
	public void select(){
		_selected = true;
	}
	
	/**
	 * Deselect object
	 */
	public void deselect(){
		_selected = false;
	}
	
	/**
	 * Selects object if it is deselected
	 * Deselects object if it is selected
	 */
	public void toggleSelect(){
		_selected = !_selected;
	}
	
	/**
	 * Status of visibility
	 * @return True if the Shape is visible
	 */
	public Boolean isVisible(){
		return _visible;
	}
	
	/**
	 * Is the object selected?
	 * @return True if the Shape is selected
	 */
	public Boolean isSelected(){
		return _selected;
	}
	
	public final String toString(){
		String suffix = isSelected() ? " [S]" : "";
		return innerToString() + suffix;
	}
	
	protected abstract String innerToString();

	@Override
	public Boolean isLeaf(){
		return true;
	}
}
