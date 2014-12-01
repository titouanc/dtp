/**
 * General object class, represents the cubes and spheres used in items creations
 */
package be.ac.ulb.infof307.g03.models;

import com.j256.ormlite.field.DatabaseField;

/**
 * @author brochape
 *
 */
public class Primitive extends Ordered {
	@DatabaseField(foreign = true)
	private Entity _entity;
	@DatabaseField
	private double _translationx = 0;
	@DatabaseField
	private double _translationy = 0;
	@DatabaseField
	private double _translationz = 0;
	@DatabaseField
	private double _rotationx = 0;
	@DatabaseField
	private double _rotationy = 0;
	@DatabaseField
	private double _rotationz = 0;
	@DatabaseField
	private String _type = "";
	
	Primitive(){
		super();
	}

	@Override
	public String getUIDPrefix() {
		return "prim";
	}

}
