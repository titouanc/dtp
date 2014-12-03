/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import be.ac.ulb.infof307.g03.utils.Log;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * Represent an entity: an object composed of primitives shapes that can be
 * instanciated in the house
 * @author brochape, Titouan
 */
public class Entity extends Meshable {
	@ForeignCollectionField
	private ForeignCollection<Primitive> primitives;
	@DatabaseField
	private String name = "";
	
	public Entity(){
		super();
	}
	
	public Entity(String name){
		super();
		setName(name);
	}
	
	@Override
	public String getUIDPrefix() {
		return "ent";
	}
	
	public final void setName(String name){
		this.name = name;
	}

	public String getName(){
		return this.name;
	}
	
	public ForeignCollection<Primitive> getPrimitives(){
		return this.primitives;
	}

	@Override
	protected String innerToString() {
		return getName();
	}

	@Override
	public Spatial toSpatial(Material material) {
		Node res = new Node(getUID());
		for (Primitive primitive : getPrimitives()) {
			if (isSelected()) primitive.select(); else primitive.deselect();
			res.attachChild(primitive.toSpatial(material));
		}
		return res;
	}

	@Override
	public Spatial toSpatial(AssetManager assetManager) {
		Node res = new Node(getUID());
		for (Primitive primitive : getPrimitives()) {
			if (isSelected()) primitive.select(); else primitive.deselect();
			res.attachChild(primitive.toSpatial(assetManager));
		}
		return res;
	}
}
