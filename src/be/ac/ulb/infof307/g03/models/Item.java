/**
 * Group of entities to be set in the general canvas
 */
package be.ac.ulb.infof307.g03.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * An Item is an instance of an object in the 3D world.
 * For example, the Entity "Lamp" could be instanciated multiple times
 * as different items
 * @author brochape
 */
@DatabaseTable(daoClass=GeometricDAO.class)
public class Item extends Meshable implements Selectionable {
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
	@DatabaseField
	private Boolean selected = false;
	
	/**
	 * Empty constructor of the Item class
	 */
	public Item(){}
	
	/**
	 * Constructor of the Item class
	 * @param floorToSet The item's floor (when added to the world)
	 * @param entityToSet The item's entity
	 */
	public Item(Floor floorToSet, Entity entityToSet){
		setFloor(floorToSet);
		setEntity(entityToSet);	
	}
	
	/**
	 * Set the position of an item on its floor
	 * @param position The relative position of an item on its floor
	 */
	public void setPosition(Vector3f position){
		this.positionx = position.x;
		this.positiony = position.y;
		this.positionz = position.z;
	}
	
	/**
	 * Set the item facing direction
	 * @param normal The normal vector
	 */
	public void setNormalVector(Vector3f normal){
		this.normalx = normal.x;
		this.normaly = normal.y;
		this.normalz = normal.z;
	}
	
	/**
	 * @return The object facing direction
	 */
	public Vector3f getNormalVector(){
		return new Vector3f((float)normalx,(float)normaly,(float)normalz);
	}
	
	/**
	 * Return the item position on its floor
	 * @return Vec3f of the item position on its floor
	 */
	public Vector3f getPositionVector(){
		return new Vector3f((float)positionx,(float)positiony,(float)positionz);
	}
	
	/**
	 * The absolute position is the item position on its floor plus its floor base height
	 * @return The item's absolute position
	 */
	public Vector3f getAbsolutePositionVector(){
		return new Vector3f((float)positionx,(float)positiony,(float)positionz + (float)floor.getBaseHeight());
	}
	
	/**
	 * Set The item position from an absolute position.
	 * The actual position is the absolute position minus its floor base height
	 * @param pos An absolute (x,y,z) position
	 */
	public void setAbsolutePosition(Vector3f pos){
		this.positionx = pos.x;
		this.positiony = pos.y;
		this.positionz = pos.z - this.floor.getBaseHeight();
	}
	
	/**
	 * Change the type of object
	 * @param entityToSet The new entity (3D model)
	 */
	public void setEntity(Entity entityToSet){
		entity = entityToSet;
	}
	
	/**
	 * @return The entity (3D model) of this object
	 */
	public Entity getEntity(){
		return entity;
	}
	
	/**
	 * Set the floor of this object
	 * @param floorToSet 
	 */
	public void setFloor(Floor floorToSet){
		floor = floorToSet;
	}
	
	/**
	 * Get the floor of this object
	 * @return the floor
	 */
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
		res.setName(getUID());
		return res;
	}
	
	@Override
	public Spatial toSpatial(AssetManager assetManager) {
		Spatial res = entity.toSpatial(assetManager);
		res.setLocalTranslation(getAbsolutePositionVector());
		res.setName(getUID());
		return res;
	}
	
	@Override
	public Boolean isSelected() {return this.selected;}
	@Override
	public void select() {this.selected = true;}
	@Override
	public void unselect() {this.selected = false;}
	@Override
	public void toggleSelect(){this.selected = ! this.selected;}

	@Override
	protected Boolean drawAsSelected() {
		return this.isSelected();
	}
}
