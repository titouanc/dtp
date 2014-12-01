/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

/**
 * Represent an entity: an object composed of primitives shapes that can be
 * instanciated in the house
 * @author brochape, Titouan
 */
public class Entity extends Geometric {
	@ForeignCollectionField
	private ForeignCollection<Primitive> _primitives;
	@DatabaseField
	private String _name = "";
	
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
		_name = name;
	}

	public String getName(){
		return _name;
	}
	
	public ForeignCollection<Primitive> getPrimitives(){
		return _primitives;
	}
	
	@Override
	public String toString(){
		return _name;
	}
}
