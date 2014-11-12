/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import com.j256.ormlite.field.DatabaseField;

/**
 * @author Titouan Christophe
 */
public class Wall extends Grouped {
	@DatabaseField
	private double _height = 0;
	private double _width = 0.1;
	
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
	
	public void setWidth(double width){
		// TODO possibly implement some better error management if the width is < 0
		if (width < 0)
			_width = 0;
		else
			_width = width;
	}
	
	public double getWidth(){
		return _width;
	}
	
	public String toString(){
		return "Wall" + getGroup().toString();
	}

	@Override
	public String getUID() {
		return String.format("wal-%d", getId());
	}
}
