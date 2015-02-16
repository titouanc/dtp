/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.sql.SQLException;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author Titouan Christophe
 * Triangles constituting imported objects
 */
@DatabaseTable(daoClass=GeometricDAO.class)
public class Triangle extends Ordered {
	@DatabaseField(foreign = true, uniqueCombo = true, foreignAutoRefresh = true)
	private Primitive primitive;
	@DatabaseField(foreign = true, uniqueCombo = true, foreignAutoRefresh = true)
	private Vertex u;
	@DatabaseField(foreign = true, uniqueCombo = true, foreignAutoRefresh = true)
	private Vertex v;
	@DatabaseField(foreign = true, uniqueCombo = true, foreignAutoRefresh = true)
	private Vertex w;
	
	/**
	 * Default constructor (needed by ormlite)
	 */
	public Triangle() {super();}

	/**
	 * Create a new Triangle
	 * @param primitive The primitive to which this Triangle shall belong
	 * @param v1 Its first vertex
	 * @param v2 Its second vertex
	 * @param v3 Its third vertex
	 */
	public Triangle(Primitive primitive, Vertex ... vertices){
		super();
		assert vertices.length == 3;
		this.primitive = primitive;
		this.u = vertices[0];
		this.v = vertices[1];
		this.w = vertices[2];
	}
	
	/**
	 * @return The first vertex
	 */
	public Vertex getV1(){
		return this.u;
	}
	
	/**
	 * @return The second vertex
	 */
	public Vertex getV2(){
		return this.v;
	}
	
	/**
	 * @return The third vertex
	 */
	public Vertex getV3(){
		return this.w;
	}
	
	@Override
	public String getUIDPrefix() {
		return "trg";
	}
	
	@Override
	protected <Subtype> Where<Subtype, Integer> getWhereForUniqueness(Where<Subtype, Integer> initialClause) throws SQLException {
		return initialClause.and().eq("primitive_id", this.primitive.getId());
	}
}
