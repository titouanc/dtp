/**
 * General object class, represents the cubes and spheres used in items creations
 */
package be.ac.ulb.infof307.g03.models;

import com.j256.ormlite.field.DatabaseField;

/**
 * @author brochape
 *
 */
public class Entity extends Ordered {
	@DatabaseField
	private int _translationx = 0;
	@DatabaseField
	private int _translationy = 0;
	@DatabaseField
	private int _translationz = 0;
	@DatabaseField
	private int _rotationx = 0;
	@DatabaseField
	private int _rotationy = 0;
	@DatabaseField
	private int _rotationz = 0;
	@DatabaseField
	private String _type = "";
	
	Entity(){
		super();
	}

	@Override
	public String getUIDPrefix() {
		return "ent";
	}

}
