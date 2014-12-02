/**
 * General object class, represents the cubes and spheres used in items creations
 */
package be.ac.ulb.infof307.g03.models;

import com.j256.ormlite.field.DatabaseField;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;

/**
 * A primitive shape used to build objects
 * @author brochape, Titouan, jschembr
 */
public class Primitive extends Meshable {
	public static String CUBE = "cube";
	public static String SPHERE = "sphere";
	
	@DatabaseField(foreign = true, canBeNull = false)
	private Entity entity;
	@DatabaseField
	private double translationx = 0;
	@DatabaseField
	private double translationy = 0;
	@DatabaseField
	private double translationz = 0;
	@DatabaseField
	private double rotationx = 0;
	@DatabaseField
	private double rotationy = 0;
	@DatabaseField
	private double rotationz = 0;
	@DatabaseField
	private double scalex = 1;
	@DatabaseField
	private double scaley = 1;
	@DatabaseField
	private double scalez = 1;
	@DatabaseField
	private String type = "";
	
	public Primitive(){
		
	}
	
	public Primitive(Entity entity, String type){
		super();
		setEntity(entity);
		setType(type);
	}
	
	public final void setType(String type){
		this.type = type;
	}
	
	public final void setEntity(Entity ent){
		this.entity = ent;
	}
	
	public final void setScale(Vector3f scale){
		this.scalex = scale.getX();
		this.scaley = scale.getY();
		this.scalez = scale.getZ();
	}
	
	public final void setTranslation(Vector3f translation) {
		this.translationx = translation.getX();
		this.translationy = translation.getY();
		this.translationz = translation.getZ();
	}
	
	public final Vector3f getScale(){
		return new Vector3f((float) this.scalex, (float) this.scaley, (float) this.scalez);
	}
	
	@Override
	public String getUIDPrefix() {
		return "prim";
	}

	/**
	 * @return The XYZ components of the translation applied to this primitive object
	 */
	public Vector3f getTranslation(){
		return new Vector3f((float) this.translationx, (float) this.translationy, (float) this.translationz);
	}
	
	/**
	 * @return The rotation applied to this primitive object, around X, Y and Z axis
	 */
	public Vector3f getRotation(){
		return new Vector3f((float) this.rotationx, (float) this.rotationy, (float) this.rotationz);
	}

	public Spatial toSpatial(Material mat) {
		Mesh mesh = null;
		if (this.type.equals(Primitive.CUBE)) {
			mesh = new Box(0.5f,0.5f,0.5f);
		} else if (this.type.equals(Primitive.SPHERE)) {
			mesh = new Sphere(32,32,1f);
		}
		
		Geometry res = new Geometry(getUID(),mesh);
		res.setMaterial(mat);
		res.scale((float) this.scalex, (float) this.scaley, (float) this.scalez);
		res.rotate((float) this.rotationx, (float) this.rotationy, (float) this.rotationz);
		res.setLocalTranslation((float) this.translationx, (float) this.translationy, (float) this.translationz);
		return res;
	}

	@Override
	protected String innerToString() {
		// TODO Auto-generated method stub
		return null;
	}
}