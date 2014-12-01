/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

/**
 * @author brochape
 *
 */
public class Entity extends Geometric {
	@ForeignCollectionField
	private ForeignCollection<Primitive> _primitives;
	@DatabaseField
	private String _name = "";
	
	public Entity(){
		super();
	}
	
	@Override
	public String getUIDPrefix() {
		return "ent";
	}

	public String getName(){
		return _name;
	}
	
	public ForeignCollection<Primitive> getPrimitives(){
		return _primitives;
	}
}
