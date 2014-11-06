/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import com.j256.ormlite.field.DatabaseField;

/**
 * @author Titouan Christophe
 */
public class Wall extends Groupable {
	@DatabaseField
	private double _height = 0;
	
	public Wall(){
		super();
	}
	
	public Wall(Group group, double height){
		super(group);
		setHeight(height);
	}
	
	public Wall(double height){
		super();
		setHeight(height);
	}
	
	public void setHeight(double h){
		_height = h;
	}
	
	public double getHeight(){
		return _height;
	}
	
	public String toString(){
		return "Wall" + getGroup().toString();
	}
}
