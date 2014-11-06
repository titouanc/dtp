/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author Titouan Christophe
 *
 */
@DatabaseTable
public abstract class Shape implements Geometric {
	@DatabaseField(generatedId = true)
	private int _id = 0;
	
	@DatabaseField(canBeNull = true, foreign = true, foreignAutoCreate = false, foreignAutoRefresh = true)
	private Group _group;

	@DatabaseField
	private Boolean _visible = true;
	
	/**
	 * @return The shape identifier for its category
	 */
	public int getId(){
		return _id;
	}
	
	public final Boolean equals(Shape other){
		Boolean res = true;
		if (getId() != 0)
			res = equalsById(other);
		return res && equalsByContent(other);
	}
	
	public abstract Boolean equalsByContent(Shape other);
	
	public final Boolean equalsById(Shape other){
		return getId() == other.getId();
	}
	
	public void addToGroup(Group grp){
		_group = grp;
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
	 * Status of visibility
	 * @return True if the Shape is visible
	 */
	public Boolean isVisible(){
		return _visible;
	}
}
