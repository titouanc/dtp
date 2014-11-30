/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.util.List;
import be.ac.ulb.infof307.g03.utils.Log;

import com.j256.ormlite.field.DatabaseField;
import com.jme3.material.Material;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 * @author Titouan Christophe
 */
public class Wall extends Meshable {
	@DatabaseField
	private double _width = 0.2;
	
	/**
	 * Constructor of the class Wall.
	 */
	public Wall(){
		super();
	}
	
	/**
	 * @param width The new value for the width of the wall.
	 */
	public void setWidth(double width){
		// TODO possibly implement some better error management if the width is < 0
		if (width < 0){
			_width = 0;
			// add print otherwise error would be pass under silence
			Log.warn("Wall received an incoherent value for width. Value is under 0");
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
		return "Wall";
	}

	@Override
	public String getUIDPrefix() {
		return "wal";
	}

	@Override
	public Spatial toSpatial(Material material) {
		Node res = new Node(getUID());
		List<Point> allPoints = getPoints();
		Floor currentFloor = getRoom().getFloor();
		
		float height = (float) currentFloor.getHeight();
		float elevation = (float) currentFloor.getBaseHeight();
		float width = (float) getWidth();
		
		for (int i=0; i<allPoints.size()-1; i++){
			// 1) Build a box the right length, width and height
			Vector3f a = allPoints.get(i).toVector3f();
			Vector3f b = allPoints.get(i+1).toVector3f();
			Vector2f segment = new Vector2f(b.x-a.x, b.y-a.y);
			Vector3f vec1 = new Vector3f(-width/2, -width/2, elevation);
			Vector3f vec2 = new Vector3f(segment.length()+width/2, width/2, elevation+height-0.001f);
			Box box = new Box(vec1, vec2);
			
			// 2) Place the wall at the right place
			Geometry wallGeometry = new Geometry(getUID(), box);
			wallGeometry.setMaterial(material);
			wallGeometry.setLocalTranslation(a);
			 
			// 3) Rotate the wall at the right orientation
			Quaternion rot = new Quaternion();
			rot.fromAngleAxis(-segment.angleBetween(new Vector2f(1,0)), new Vector3f(0,0,1));
			wallGeometry.setLocalRotation(rot);
			
			// 4) Attach it to the node
			res.attachChild(wallGeometry);
		}
		return res;
	}
}
