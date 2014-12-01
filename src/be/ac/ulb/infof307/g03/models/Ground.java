/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.util.List;

import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

/**
 * A ground is a surface delimited by a group of shapes
 * @author Titouan Christophe
 */
public class Ground extends Meshable {
	/**
	 * Create a new empty ground object, and create a new group for it
	 */
	public Ground() {
		super();
	}

	@Override
	protected final String innerToString() {
		return "Ground";
	}

	@Override
	public String getUIDPrefix() {
		return "gnd";
	}

	@Override
	public final Spatial toSpatial(Material material) {
		List<Point> all_points = getPoints();
		int shape_n_points = all_points.size();
		float baseHeight = (float) getRoom().getFloor().getBaseHeight();
		
		if (shape_n_points == 0)
			return null;
		
		/* 0) Closed polygon ? -> we don't need to store both first && last */
		Point firstPoint = all_points.get(0);
		Point lastPoint = all_points.get(shape_n_points - 1);
		if (firstPoint.equals(lastPoint))
			shape_n_points--;
		
		if(shape_n_points < 3){
			throw new IllegalArgumentException();
		}
		
		/* 1) Build an array of all points */
		Vector3f vertices[] = new Vector3f[shape_n_points];
		for (int i=0; i<shape_n_points; i++){
			vertices[i] = all_points.get(i).toVector3f();
			vertices[i].setZ(baseHeight);
		}
		
		/* 2) Polygon triangulation to make a surface */
		int n_triangles = shape_n_points - 2;
		int edges[] = new int[3 * n_triangles];
		for (int i=0; i<n_triangles; i++){
			edges[3 * i] = 0;
			edges[3 * i + 1] = i+2;
			edges[3 * i + 2] = i+1;
		}
		
		Mesh mesh = new Mesh();
	  	mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
	  	mesh.setBuffer(Type.Index,    3, BufferUtils.createIntBuffer(edges));
	  	Vector2f[] texCoord = new Vector2f[7];
	  	texCoord[0] = new Vector2f(0.5f, 0.5f);
	  	texCoord[1] = new Vector2f(0, 0.5f);
	  	texCoord[2] = new Vector2f(0.25f, 0);
	  	texCoord[3] = new Vector2f(0.75f, 0);
	  	texCoord[4] = new Vector2f(1, 0.5f);
	  	texCoord[5] = new Vector2f(0.75f, 1);
	  	texCoord[6] = new Vector2f(0.25f, 1);
	  	mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
	  	mesh.updateBound();
		Geometry res = new Geometry(getUID(), mesh);
		res.setMaterial(material);
		return res;
	}
}
