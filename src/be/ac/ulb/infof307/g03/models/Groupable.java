/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * An abstract class for all elements that use an inner Group
 * @author Titouan Christophe
 */
@DatabaseTable
public abstract class Groupable implements Geometric {
	@DatabaseField(generatedId = true)
	private int _id = 0;
	
	@DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true, canBeNull = false)
	private Group _group = null;
	
	public Groupable() {
		_group = new Group();
	}
	
	public Groupable(Group group){
		setGroup(group);
	}

	public Group getGroup(){
		return _group;
	}
	
	public void setGroup(Group group){
		_group = group;
	}
	
	public int getId(){
		return _id;
	}
	
	public abstract String toString();
}
