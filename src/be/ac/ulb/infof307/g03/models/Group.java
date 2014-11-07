/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.sql.SQLException;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * @author Titouan Christophe
 * @brief A group is a shape constituted of multiple shapes
 */
public class Group extends Shape {
	@DatabaseField(canBeNull = true, unique = true)
	private String _name;
	
	public Group(){
		_name = "";
	}
	
	public Group(String name){
		_name = name;
	}

	public String getName(){
		return _name;
	}
	
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
		return String.format("<%s>", getName());
	}
}
