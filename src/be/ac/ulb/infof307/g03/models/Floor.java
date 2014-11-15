/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * A Floor is a set of groups that are all on the same floor in a house.
 * @author Titouan Christophe
 */
@DatabaseTable
public class Floor implements Geometric {
	@DatabaseField(generatedId = true)
	private int _id = 0;
	@DatabaseField(foreign = true, canBeNull = true, foreignAutoRefresh = true)
	private Floor _previous = null;
	
	/**
	 * @return The floor id
	 */
	public int getId(){
		return _id;
	}
	
	/**
	 * Set a given floor as previous (below) this one
	 * @param prev
	 */
	public void setPrevious(Floor prev){
		_previous = prev;
	}
	
	/**
	 * @return The previous (below) floor
	 */
	public Floor getPrevious(){
		return _previous;
	}
	
	/**
	 * @return True if this floor has no floor below
	 */
	public Boolean isFirstFloor(){
		return _previous == null;
	}
	
	@Override
	public Group getGroup() {
		return null;
	}

	@Override
	public Boolean isLeaf() {
		return false;
	}

	@Override
	public String getUID() {
		return String.format("flr-%d", getId());
	}
	
	public String toString(){
		return isFirstFloor() ? "First floor" : "Floor";
	}
}
