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
	private double _width = 0.2;
	
	/**
	 * Constructor of the class Wall.
	 */
	public Wall(){
		super();
	}
	
	/**
	 * Constructor of the class Wall.
	 * @param group The wall's group.
	 */
	public Wall(Group group){
		super(group);
	}
	
	/**
	 * @param width The new value for the width of the wall.
	 */
	public void setWidth(double width){
		// TODO possibly implement some better error management if the width is < 0
		if (width < 0){
			_width = 0;
			// add print otherwise error would be pass under silence
			System.out.println("[DEBUG] Wall received an incoherent value for width. Value is under 0");
		}else{
			_width = width;
		}
	}
	
	
	/**
	 * @return The width of the Wall.
	 */
	public double getWidth(){
		return _width;
	}
	
	protected String innerToString(){
		return "Wall" + getGroup().toString();
	}

	@Override
	public String getUID() {
		return String.format("wal-%d", getId());
	}
}
