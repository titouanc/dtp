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

	/**
	 * @return The shape identifier for its category
	 */
	public int getId(){
		return _id;
	}
	
	/**
	 * Check if two shapes are the same.
	 * @param other The shape to be compared.
	 * @return True if equals, False otherwise.
	 */
	public final Boolean equals(Shape other){
		Boolean res = true;
		if (getId() != 0)
			res = equalsById(other);
		return res && equalsByContent(other);
	}
	
	/**
	 * Abstract method to check if two shapes have the same content.
	 * @param other The shape to be compared.
	 * @return True if equals, False otherwise.
	 */
	public abstract Boolean equalsByContent(Shape other);
	
	/**
	 * Check if two shapes have the same Id
	 * @param other The shape to be compared.
	 * @return True if equals, False otherwise.
	 */
	public final Boolean equalsById(Shape other){
		return getId() == other.getId();
	}
	
	/**
	 * This method is a setter.
	 * @param grp The new group of the shape.
	 */
	public void addToGroup(Group grp){
		_group = grp;
	}
	
	public Group getGroup(){
		return _group;
	}
}
