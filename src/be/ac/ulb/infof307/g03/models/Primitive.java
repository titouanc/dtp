/**
 * General object class, represents the cubes and spheres used in items creations
 */
package be.ac.ulb.infof307.g03.models;

import java.nio.FloatBuffer;

import be.ac.ulb.infof307.g03.utils.Log;

import java.util.Vector;

import be.ac.ulb.infof307.g03.utils.Log;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.jme3.material.Material;
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.mesh.IndexBuffer;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Dome;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.BufferUtils;

/**
 * A primitive shape used to build objects
 * @author brochape, Titouan, jschembr
 */
@DatabaseTable(daoClass=GeometricDAO.class)
public class Primitive extends Meshable {
	/**
	 * A cube primitive.
	 */
	public static String CUBE = "Cube";
	
	/**
	 * A sphere primitive.
	 */
	public static String SPHERE = "Sphere";
	
	/**
	 * A pyramid primitive.
	 */
	public static String PYRAMID = "Pyramid";
	
	/**
	 * A cylinder primitive.
	 */
	public static String CYLINDER = "Cylinder";
	
	/**
	 * Tag to check if a primitive is imported
	 */
	public static String IMPORTED = "Imported";

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
	@ForeignCollectionField
	private ForeignCollection<Vertex> vertices;
	@ForeignCollectionField
	private ForeignCollection<Triangle> triangles;
	
	/**
	 * Primitive empty constructor
	 */
	public Primitive(){
		
	}
	
	/**
	 * @param entity The primitive's entity
	 * @param type The primitive type (sphere,cube,...)
	 */
	public Primitive(Entity entity, String type){
		super();
		setEntity(entity);
		setType(type);
		if (type.equals(PYRAMID)) {
			this.rotationx = 1.5707; this.rotationz = 0.7853;
		}
	}
	
	public Primitive clone(){
		Primitive copy = new Primitive(this.entity, this.type);
		copy.setRotation(this.getRotation());
		copy.setTranslation(this.getTranslation());
		copy.setScale(this.getScale());
		return copy;
	}
	
	/**
	 * @param type The primitive type (sphere,cube,...)
	 */
	public final void setType(String type){
		this.type = type;
	}
	
	/**
	 * @param ent The primitive's entity
	 */
	public final void setEntity(Entity ent){
		this.entity = ent;
	}
	
	/**
	 * Scale the primitive
	 * @param scale
	 */
	public final void setScale(Vector3f scale){
		this.scalex = scale.getX();
		this.scaley = scale.getY();
		this.scalez = scale.getZ();
	}
	
	/**
	 * Move the primitive
	 * @param translation vec3f
	 */
	public final void setTranslation(Vector3f translation) {
		this.translationx = translation.getX();
		this.translationy = translation.getY();
		this.translationz = translation.getZ();
	}
	
	/**
	 * Rotate the primitive
	 * @param rotation vec3f
	 */
	public final void setRotation(Vector3f rotation) {
		this.rotationx = rotation.getX();
		this.rotationy = rotation.getY();
		this.rotationz = rotation.getZ();
	}
	
	/**
	 * @return A vec3f of primitive scale
	 */
	public final Vector3f getScale(){
		return new Vector3f((float) this.scalex, (float) this.scaley, (float) this.scalez);
	}
		
	/**
	 * @return The primitive type (sphere,cube,...)
	 */
	public final String getType() {
		return this.type;
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
	
	/**
	 * Create a jme Geometry object for this primitive
	 * @return A jme Geometry object, with local scale, translation and rotation set
	 */
	private Geometry getGeometry(){
		Mesh mesh = null;
		if (this.type.equals(Primitive.CUBE)) {
			mesh = new Box(0.5f,0.5f,0.5f);
		} else if (this.type.equals(Primitive.SPHERE)) {
			mesh = new Sphere(32,32,1f);
		} else if (this.type.equals(Primitive.PYRAMID)) {
			mesh = new Dome(2, 4, 1);
		} else if (this.type.equals(Primitive.CYLINDER)) {
			mesh = new Cylinder(4, 20, 1f,0.9f, 1f, false, false);
		} else if (this.type.equals(Primitive.IMPORTED)) {
			int nVertices = this.vertices.size();
			Vector3f[] vertices = new Vector3f[nVertices];
			for (Vertex v : this.vertices){
				int i = v.getIndex();
				assert 0 <= i && i < nVertices;
				vertices[i] = v.asVector3f();
			}
			
			int nTriangles = this.triangles.size();
			int[] triangles = new int[3*nTriangles];
			for (Triangle t : this.triangles){
				int i = t.getIndex();
				assert 0 <= i && i < nTriangles;
				triangles[3*i] = t.getV1().getIndex();
				triangles[3*i+1] = t.getV2().getIndex();
				triangles[3*i+2] = t.getV3().getIndex();
				Log.debug("Meshing triangle %s %s %s", t.getV1().toString(), t.getV2().toString(), t.getV3().toString());
			}
			
			mesh = new Mesh();
		  	mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
		  	mesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(triangles));
		  	mesh.updateBound();
		}
		
		Geometry res = new Geometry(getUID(),mesh);
		res.scale((float) this.scalex, (float) this.scaley, (float) this.scalez);
		res.rotate((float) this.rotationx, (float) this.rotationy, (float) this.rotationz);
		res.setLocalTranslation((float) this.translationx, (float) this.translationy, (float) this.translationz);
		return res;
	}

	@Override
	public Spatial toSpatial(Material mat) {
		Geometry res = this.getGeometry();
		res.setMaterial(mat);
		return res;
	}
	
	/**
	 * @return the array of vertices for this Primitive
	 */
	public float[] getVertices() {
		Geometry geometry = this.getGeometry();
		Transform t = geometry.getWorldTransform();
	
		FloatBuffer buffer = geometry.getMesh().getFloatBuffer(Type.Position);
		float[] positions = new float[buffer.capacity()];
		buffer.clear();
		for (int i =0; i<positions.length; i+=3) {
			Vector3f vertex = new Vector3f(buffer.get(i),buffer.get(i+1),buffer.get(i+2));
			Vector3f res = new Vector3f();
			t.transformVector(vertex, res);
			positions[i] = res.getX();
			positions[i+1] = res.getY();
			positions[i+2] = res.getZ();
		}
		
		return positions;
 	}
	
	public FloatBuffer getRotMatrix() {
		Geometry geometry = this.getGeometry();
		Matrix4f m = geometry.getWorldMatrix();
		return m.toFloatBuffer();
	}
	
	/**
	 * @return the array of normals for this primitive
	 */
	public int[] getIndexes() {
		Geometry geometry = this.getGeometry();
		IndexBuffer ib = geometry.getMesh().getIndexBuffer();
		int[] res = new int[ib.size()];
		for (int i=0; i<res.length; ++i) {
			res[i] = ib.get(i);
		}
		return res;
	}

	@Override
	protected String innerToString() {
		return new String(type+" "+getId());
	}
}
