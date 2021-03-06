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
public class Vertex extends Indexed {
	@DatabaseField(foreign = true, uniqueCombo = true, canBeNull = false)
	private Primitive primitive = null;
	@DatabaseField(uniqueCombo = true)
	private float x = 0;
	@DatabaseField(uniqueCombo = true)
	private float y = 0;
	@DatabaseField(uniqueCombo = true)
	private float z = 0;
	/**
	 * Default constructor (needed by ormlite)
	 */
	public Vertex() {super();}
	
	/**
	 * @param primitive The primitive to which the vertex shall belong
	 * @param coords The coordinates of the new vertex
	 */
	public Vertex(Primitive primitive, Vector3f coords) {
		super();
		this.primitive = primitive;
		this.x = coords.x;
		this.y = coords.y;
		this.z = coords.z;
	}
	
	/**
	 * @param primitive
	 * @param x The first coordinate
	 * @param y The second coordinate
	 * @param z The third coordinate
	 */
	public Vertex(Primitive primitive, float x, float y, float z){
		super();
		this.primitive = primitive;
		this.x = x;
		this.y = y;
		this.z = z;
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
