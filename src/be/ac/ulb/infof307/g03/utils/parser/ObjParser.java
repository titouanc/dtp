/**
 * 
 */
package be.ac.ulb.infof307.g03.utils.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;



import be.ac.ulb.infof307.g03.models.Entity;
import be.ac.ulb.infof307.g03.models.GeometricDAO;
import be.ac.ulb.infof307.g03.models.MasterDAO;
import be.ac.ulb.infof307.g03.models.Primitive;
import be.ac.ulb.infof307.g03.models.Triangle;
import be.ac.ulb.infof307.g03.models.Vertex;
import be.ac.ulb.infof307.g03.utils.Log;


/**
 * @author Bruno
 *
 */
public class ObjParser extends Parser {
	private Scanner scan;
	
	/**
	 * @param filename
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public ObjParser(String filename, MasterDAO dao) throws IOException, SQLException{
		super(filename, dao);
		this.scan = new Scanner(new File(filename));
		scan.useLocale(Locale.US);
	}
	
	public static int extractVertexIndex(String text) {
		int index = text.indexOf("/");
		if (index > -1)
			text = text.substring(0, index);
		return Integer.parseInt(text);
	}
    
	@Override
	public void parse() throws SQLException, IOException {
		GeometricDAO<Vertex> vertexDao = this.daoFactory.getDao(Vertex.class);
		GeometricDAO<Triangle> triangleDao = this.daoFactory.getDao(Triangle.class);
		List<Vertex> vertices = new ArrayList<Vertex>();
		
		int verticeCount = 0;
		int triangleCount = 0;
		while (scan.hasNext()){
			String identifier = scan.next();
			if (identifier.equals("v")){
				float x = scan.nextFloat();
				float y = scan.nextFloat();
				float z = scan.nextFloat();
				Vertex newVert = new Vertex(this.primitive, x, y, z);
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
				Triangle tr = new Triangle(this.primitive, triangleVertices);
				tr.setIndex(triangleCount);
				triangleDao.create(tr);
				triangleCount++;
			}
		}
	}
}
