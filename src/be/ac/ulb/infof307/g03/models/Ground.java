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
	 * @param triangleToRemove 
	 * @param trianglesList 
	 * @return the triangle list without the deleted triangle
	 */
	public List<DTriangle> removeTriangle(DTriangle triangleToRemove, List<DTriangle> trianglesList){
		
		for(DTriangle triangle: trianglesList){
			if(triangle == triangleToRemove){
				trianglesList.remove(trianglesList.indexOf(triangleToRemove));
			}
		}
		return trianglesList;
		
	}
	
	@Override
	public double getSurface(){
		double surface = 0;
		if(triangleList != null){
			double triangleSurface = 0;
			for (DTriangle triangle : triangleList){
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
				
			}
		}
		
		
		return surface;
	}
	
	/**
	 * @param triangle
	 * @return
	 */
	public DPoint getTriangleCenter(DTriangle triangle){
		DPoint returnPoint = null;
		double x = 0;
		double y = 0;
		for (DPoint point : triangle.getPoints()){
			x += point.getX();
			y += point.getY();
		}
		x/=3;
		y/=3;
		try {
			returnPoint = new DPoint(x,y,(double)getRoom().getFloor().getBaseHeight());
		} catch (DelaunayError e) {
			e.printStackTrace();
		}
		return returnPoint;
		
	}
	
	/**
	 * @param polygon
	 * @param point 
	 * @return
	 */
	public boolean isInsidePolygon(List<Point> polygon, DPoint point )
	{
		  int i, j;
		  boolean res = false;
		  int nvert = polygon.size();
		  for (i = 0, j = nvert-1; i < nvert; j = i++) {
		    if ( ((polygon.get(i).getY()>point.getY()) != (polygon.get(j).getY()>point.getY())) &&
		     (point.getX() < (polygon.get(j).getX()-polygon.get(i).getX()) * (point.getY()-polygon.get(i).getY()) / (polygon.get(j).getY()-polygon.get(i).getY()) + polygon.get(i).getX()) )
		       res = !res;
		  }
		  return res;
	}
	
	/**
	 * @param x1 
	 * @param y1 
	 * @param x2 
	 * @param y2 
	 * @return the angle between the edges
	 */
	public double angle2D(double x1, double y1, double x2, double y2){
		
		double dtheta,theta1,theta2;
		
		
		theta1 = Math.atan2(y1,x1);
		theta2 = Math.atan2(y2,x2);
		dtheta = theta2 - theta1;
		while (dtheta > Math.PI)
			dtheta -= 2*Math.PI;
		while (dtheta < -Math.PI)
			dtheta += 2*Math.PI;

		return dtheta ;
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
		} catch (DelaunayError e) {
			Log.error("Could not process Delaunay's algorithm");
			e.printStackTrace();
		}
		triangleList = delaunay.getTriangleList();
		List<DTriangle> finalTriangleList = new ArrayList<DTriangle>();
		for (DTriangle triangle : triangleList){
			if(isInsidePolygon(all_points,getTriangleCenter(triangle))){
				finalTriangleList.add(triangle);
			}
		}
		/*3) Set up the computed data for jmonkey*/
		List<DPoint> pointsList= delaunay.getPoints();
		Vector3f vertices[] = new Vector3f[shape_n_points];
		for(int index = 0; index<pointsList.size(); ++index){
			DPoint currentPoint = pointsList.get(index);
			vertices[index] = new Vector3f((float)currentPoint.getX(),(float)currentPoint.getY(),(float)currentPoint.getZ());
		}
		
		int n_triangles = finalTriangleList.size();
		int edges[] = new int[3 * n_triangles];
		
		for(int i=0;i<finalTriangleList.size();++i){
			edges[3 * i] = pointsList.indexOf(finalTriangleList.get(i).getPoint(0));
			edges[3 * i + 1] = pointsList.indexOf(finalTriangleList.get(i).getPoint(1));
			edges[3 * i + 2] = pointsList.indexOf(finalTriangleList.get(i).getPoint(2));
		}
		
		
		
		Mesh mesh = new Mesh();
	  	mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
	  	mesh.setBuffer(Type.Index,    3, BufferUtils.createIntBuffer(edges));
	  	
	  	Vector2f[] texCoord = new Vector2f[4];
	  	texCoord[0] = new Vector2f(0,0);
	  	texCoord[1] = new Vector2f(1,0);
	  	texCoord[2] = new Vector2f(0,1);
	  	texCoord[3] = new Vector2f(1,1);
	  	mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
	  	mesh.updateBound();
		Geometry res = new Geometry(getUID(), mesh);
		res.setMaterial(material);
		return res;
	}
}

