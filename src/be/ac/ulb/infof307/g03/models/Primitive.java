/**
 * General object class, represents the cubes and spheres used in items creations
 */
package be.ac.ulb.infof307.g03.models;

import com.j256.ormlite.field.DatabaseField;
import com.jme3.math.Vector3f;

/**
 * @author brochape
 *
 */
public class Primitive extends Geometric {
	public static String CUBE = "cube";
	public static String SPHERE = "sphere";
	
	@DatabaseField(foreign = true, canBeNull = false)
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
	
	public Primitive(){
		
	}
	
	public Primitive(Entity entity, String type){
		super();
		setEntity(entity);
		setType(type);
	}
	
	public final void setType(String type){
		_type = type;
	}
	
	public final void setEntity(Entity ent){
		_entity = ent;
	}

	@Override
	public String getUIDPrefix() {
		return "prim";
	}

	/**
	 * @return The XYZ components of the translation applied to this primitive object
	 */
	public Vector3f getTranslation(){
		return new Vector3f((float) _translationx, (float) _translationy, (float) _translationz);
	}
	
	/**
	 * @return The rotation applied to this primitive object, around X, Y and Z axis
	 */
	public Vector3f getRotation(){
		return new Vector3f((float) _rotationx, (float) _rotationy, (float) _rotationz);
	}
}
