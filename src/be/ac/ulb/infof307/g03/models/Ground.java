/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.util.ArrayList;
import java.util.List;

import org.jdelaunay.delaunay.ConstrainedMesh;
import org.jdelaunay.delaunay.error.DelaunayError;
import org.jdelaunay.delaunay.geometries.DEdge;
import org.jdelaunay.delaunay.geometries.DPoint;
import org.jdelaunay.delaunay.geometries.DTriangle;

import be.ac.ulb.infof307.g03.utils.Log;

import com.j256.ormlite.table.DatabaseTable;
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
 * @author Titouan Christophe, Bruno Rocha Pereira
 */
@DatabaseTable(daoClass=GeometricDAO.class)
public class Ground extends Area {
	List<DTriangle> triangleList = null;
	
	/**
	 * Create a new empty ground object, and create a new group for it
	 */
	public Ground() {
		super();
	}
	
	/**
	 * Constructor of the class Ground.
	 * @param forRoom The Ground's Room
	 */
	public Ground(Room forRoom){
		super();
		forRoom.setGround(this);
	}

	@Override
	protected final String innerToString() {
		return "Ground";
	}

	@Override
	public String getUIDPrefix() {
		return "gnd";
	}
	
	/**
	 * @param edgeToRemove 
	 * @param triangleList 
	 * @param edge_to_remove
	 * @return
	 */
	public List<DTriangle> removeTriangle(DEdge edgeToRemove, List<DTriangle> triangleList){
		
		for(DTriangle triangle: triangleList){
			if(triangle.getEdge(0).equals(edgeToRemove) &&
			   triangle.getEdge(1).equals(edgeToRemove) &&
			   triangle.getEdge(2).equals(edgeToRemove)){
				triangleList.remove(triangleList.indexOf(edgeToRemove));
			}
		}
		return triangleList;
		
	}
	
	@Override
	public double getSurface(){
		double surface = 0;
		if(triangleList != null){
			double triangleSurface = 0;
			System.out.println(triangleList);
			for (DTriangle triangle : triangleList){
				System.out.println("Coucou");
				triangleSurface = 0;
				for (DEdge edge : triangle.getEdges()){
					triangleSurface += edge.get2DLength();
				}
				triangleSurface /= 2;
				triangleSurface = Math.sqrt(triangleSurface*
						(triangleSurface - triangle.getEdge(0).get2DLength())*
						(triangleSurface - triangle.getEdge(1).get2DLength())*
						(triangleSurface - triangle.getEdge(2).get2DLength()));
				surface += triangleSurface;
				System.out.println(surface);
				
			}
		}
		
		
		return surface;
	}
	
	

	@Override
	public final Spatial toSpatial(Material material) {

		List<Point> all_points = getPoints();
		int shape_n_points = all_points.size();
		if (shape_n_points == 0)
			return null;
		float baseHeight = (float) getRoom().getFloor().getBaseHeight();
		
		ConstrainedMesh delaunay = new ConstrainedMesh();
		
		/*0) Closed polygon ? -> we don't need to store both first && last */
		Point firstPoint = all_points.get(0);
		Point lastPoint = all_points.get(shape_n_points - 1);
		if (firstPoint.equals(lastPoint))
			shape_n_points--;
		
		if(shape_n_points < 3){
			throw new IllegalArgumentException();
		}
		
		/* 1) Add the constraints edges for the dalaunay algorithm */
		for(int i=0;i<all_points.size()-1;++i){
			Point orig_point = null;
			Point dest_point = null;
			DPoint orig = null;
			DPoint dest = null;
			try{
				orig_point = all_points.get(i);
				dest_point = all_points.get(i+1);
				orig = new DPoint((float)orig_point.getX(),(float)orig_point.getY(),baseHeight);
				dest = new DPoint((float)dest_point.getX(),(float)dest_point.getY(),baseHeight);
			} catch (DelaunayError e) {
				e.printStackTrace();
			}
			try {
				DEdge edge = new DEdge(orig,dest);
				delaunay.addConstraintEdge(edge);
			} catch (DelaunayError e) {
				e.printStackTrace();
			} 
		}


		/* 2) Polygon triangulation to make a surface using Delaunay's algorithm*/
		try {
			delaunay.forceConstraintIntegrity();
			delaunay.processDelaunay();//Error here for concave polygons : too many triangles computed despite the constraint edges
			System.out.println(delaunay.getTriangleList());
		} catch (DelaunayError e) {
			Log.error("Could not process Delaunay's algorithm");
			e.printStackTrace();
		}
		triangleList = delaunay.getTriangleList();
		
		List<DEdge> constraintEdgesList = delaunay.getConstraintEdges();
		List<DEdge> dedges = delaunay.getEdges();
		for (DEdge edge : dedges){
			if(! constraintEdgesList.contains(edge)){
				//ANGLE CONSTRAINT : Check if edge is inside the room
				// If edge is outside, remove the triangles it forms
				//triangleList = removeTriangle(edge, triangleList);
			}
		}
		
		/*3) Set up the computed data for jmonkey*/
		List<DPoint> pointsList= delaunay.getPoints();
		Vector3f vertices[] = new Vector3f[shape_n_points];
		for(int index = 0; index<pointsList.size(); ++index){
			DPoint currentPoint = pointsList.get(index);
			vertices[index] = new Vector3f((float)currentPoint.getX(),(float)currentPoint.getY(),(float)currentPoint.getZ());
		}
		
		int n_triangles = triangleList.size();
		int edges[] = new int[3 * n_triangles];
		
		for(int i=0;i<triangleList.size();++i){
			edges[3 * i] = pointsList.indexOf(triangleList.get(i).getPoint(0));
			edges[3 * i + 1] = pointsList.indexOf(triangleList.get(i).getPoint(1));
			edges[3 * i + 2] = pointsList.indexOf(triangleList.get(i).getPoint(2));
		}
		
		
		System.out.println("Surface : " + getSurface());
		
		
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

