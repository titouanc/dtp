/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.util.List;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author Titouan Christophe
 *
 */
@DatabaseTable
public abstract class Shape {
	@DatabaseField(generatedId = true)
	private int _id = 0;
	@DatabaseField(canBeNull = false, foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
	private ShapeRecord _shaperec;
	
	/**
	 * @return Shape unique identifier
	 */
	public int getShapeId(){
		return _shaperec.getId();
	}
	
	/**
	 * @return The shape identifier for its category
	 */
	public int getId(){
		return _id;
	}
	
	public Shape(){
		_shaperec = new ShapeRecord();
	}
	
	public ShapeRecord getRecord(){
		return _shaperec;
	}
	
	public final Boolean equals(Shape other){
		Boolean res = true;
		if (getShapeId() != 0)
			res = equalsById(other);
		return res && equalsByContent(other);
	}
	
	public abstract Boolean equalsByContent(Shape other);
	
	public final Boolean equalsById(Shape other){
		return getId() == other.getId() && getShapeId() == other.getShapeId();
	}
}
