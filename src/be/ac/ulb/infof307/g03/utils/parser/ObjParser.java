/**
 * 
 */
package be.ac.ulb.infof307.g03.utils.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import be.ac.ulb.infof307.g03.models.Entity;
import be.ac.ulb.infof307.g03.models.GeometricDAO;
import be.ac.ulb.infof307.g03.models.MasterDAO;
import be.ac.ulb.infof307.g03.models.Primitive;
import be.ac.ulb.infof307.g03.models.Triangle;
import be.ac.ulb.infof307.g03.models.Vertex;


/**
 * @author Bruno
 *
 */
public class ObjParser extends Parser {
	private MasterDAO dao = null;
	private Entity buildingEntity = null;
	
	/**
	 * @param filename
	 */
	public ObjParser(String filename, Entity entity, MasterDAO dao){
		this.dao = dao;
		this.buildingEntity = entity;
		try {
			this.parse(filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static int extractVertexIndex(String text) {
		int index = text.indexOf("/");
		if (index > -1)
			text = text.substring(0, index);
		return Integer.parseInt(text);
	}
    
	public void parse(String filename) throws FileNotFoundException, SQLException{
		Scanner scan = new Scanner(new File(filename));
		GeometricDAO<Vertex> vertexDao = this.dao.getDao(Vertex.class);
		GeometricDAO<Triangle> triangleDao = this.dao.getDao(Triangle.class);
		List<Vertex> vertices = new ArrayList<Vertex>();
		
		Primitive prim = new Primitive(this.buildingEntity, Primitive.IMPORTED);
		this.dao.getDao(Primitive.class).create(prim);
		
		int verticeCount = 0;
		int triangleCount = 0;
		while (scan.hasNext()){
			String identifier = scan.next();
			if (identifier.equals("v")){
				float x = scan.nextFloat();
				float y = scan.nextFloat();
				float z = scan.nextFloat();
				Vertex newVert = new Vertex(prim, x, y, z);
				newVert.setIndex(verticeCount);
				vertexDao.create(newVert);
				vertices.add(newVert);
				verticeCount++;
				
				/* Skip weight attribute */
				while (scan.hasNextFloat()){
					scan.nextFloat();
				}
			} else if (identifier.equals("f")) {
				Vertex[] triangleVertices = new Vertex[3];
				for (int i=0; i<3; i++){
					String vertexIndexes = scan.next();
					int vertexIndex = extractVertexIndex(vertexIndexes);
					if (1 <= vertexIndex && vertexIndex <= verticeCount){
						triangleVertices[i] = vertices.get(vertexIndex - 1);
					}
				}
				Triangle tr = new Triangle(prim, triangleVertices[0], triangleVertices[1], triangleVertices[2]);
				tr.setIndex(triangleCount);
				triangleDao.create(tr);
				triangleCount++;
			}
		}
	}
}
