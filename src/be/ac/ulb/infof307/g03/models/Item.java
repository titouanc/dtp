/**
 * Group of entities to be set in the general canvas
 */
package be.ac.ulb.infof307.g03.models;

import com.j256.ormlite.field.DatabaseField;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * @author brochape
 *
 */
public class Item extends Meshable {
	@DatabaseField
	private double positionx = 0;
	@DatabaseField
	private double positiony = 0;
	@DatabaseField
	private double positionz = 0;
	@DatabaseField
	private double normalx = 0;
	@DatabaseField
	private double normaly = 0;
	@DatabaseField
	private double normalz = 0;
	@DatabaseField(foreign = true, canBeNull = false, foreignAutoRefresh = true)
	private Floor floor;
	@DatabaseField(foreign = true, canBeNull = false, foreignAutoRefresh = true)
	private Entity entity;
	

	public Item(){}
	
	public Item(Floor floorToSet, Entity entityToSet){
		setFloor(floorToSet);
		setEntity(entityToSet);
		
	}
	
	public void setPosition(Vector3f position){
		this.positionx = position.x;
		this.positiony = position.y;
		this.positionz = position.z;
	}
	
	public void setNormalVector(Vector3f normal){
		this.normalx = normal.x;
		this.normaly = normal.y;
		this.normalz = normal.z;
	}
	
	public Vector3f getNormalVector(){
		return new Vector3f((float)normalx,(float)normaly,(float)normalz);
	}
	
	public Vector3f getPositionVector(){
		return new Vector3f((float)positionx,(float)positiony,(float)positionz);
	}
	
	public Vector3f getAbsolutePositionVector(){
		return new Vector3f((float)positionx,(float)positiony,(float)positionz + (float)floor.getBaseHeight());
	}
	
	public void setEntity(Entity entityToSet){
		entity = entityToSet;
	}
	
	public Entity getEntity(){
		return entity;
	}
	
	public void setFloor(Floor floorToSet){
		floor = floorToSet;
	}
	
	public Floor getFloor(){
		return floor;
	}

	@Override
	public String getUIDPrefix() {
		return "item";
	}

	@Override
	protected String innerToString() {
		return entity.getName();
	}

	@Override
	public Spatial toSpatial(Material material) {
		Spatial res = entity.toSpatial(material);
		res.setLocalTranslation(getAbsolutePositionVector());
		return res;
	}
	
	@Override
	public Spatial toSpatial(AssetManager assetManager) {
		Spatial res = entity.toSpatial(assetManager);
		res.setLocalTranslation(getAbsolutePositionVector());
		return res;
	}
	

}
