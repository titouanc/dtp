/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.sql.SQLException;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.table.DatabaseTable;
import com.jme3.math.Vector3f;

/**
 * @author Titouan Christophe
 * Points constituting imported objects
 */
@DatabaseTable(daoClass=GeometricDAO.class)
public class Vertex extends Ordered {
	@DatabaseField(foreign = true, uniqueCombo = true, canBeNull = false)
	private Primitive primitive = null;
	@DatabaseField(uniqueCombo = true)
	private float x = 0;
	@DatabaseField(uniqueCombo = true)
	private float y = 0;
	@DatabaseField(uniqueCombo = true)
	private float z = 0;
	@DatabaseField
	private float normalX = 0;
	@DatabaseField
	private float normalY = 0;
	@DatabaseField
	private float normalZ = 0;
	/**
	 * Default constructor (needed by ormlite)
	 */
	public Vertex() {super();}
	
	/**
	 * @param primitive The primitive to which the vertex shall belong
	 * @param coords The coordinates of the new vertex
	 */
	public Vertex(Primitive primitive, Vector3f coords, Vector3f normal) {
		super();
		this.primitive = primitive;
		this.x = coords.x;
		this.y = coords.y;
		this.z = coords.z;
		this.normalX = normal.x;
		this.normalY = normal.y;
		this.normalZ = normal.z;
	}
	
	/**
	 * @return The primitive this vertex belongs to
	 */
	public Primitive getPrimitive(){
		return this.primitive;
	}
	
	/**
	 * @return A Vector3f for this vertex
	 */
	public Vector3f asVector3f(){
		return new Vector3f(this.x, this.y, this.z);
	}
	
	/**
	 * @return The normal vector of this vertex
	 */
	public Vector3f getNormal(){
		return new Vector3f(this.normalX, this.normalY, this.normalZ);
	}

	@Override
	public String getUIDPrefix() {
		return "vert";
	}

	@Override
	public String toString(){
		return String.format("(%f %f %f)", x, y, z);
	}
	
	@Override
	protected <Subtype> Where<Subtype, Integer> getWhereForUniqueness(Where<Subtype, Integer> initialClause) throws SQLException {
		return initialClause.and().eq("primitive_id", this.primitive.getId());
	}
}
