/**
 * 
 */
package be.ac.ulb.infof307.g03.utils.parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import be.ac.ulb.infof307.g03.models.Entity;
import be.ac.ulb.infof307.g03.models.MasterDAO;
import be.ac.ulb.infof307.g03.models.Primitive;
import be.ac.ulb.infof307.g03.models.Triangle;
import be.ac.ulb.infof307.g03.models.Vertex;
import be.ac.ulb.infof307.g03.utils.Log;

import com.jme3.math.Vector3f;
/*import com.owens.oobjloader.builder.Build;
import com.owens.oobjloader.builder.VertexGeometric;
import com.owens.oobjloader.parser.Parse;
*/
import com.owens.oobjloader.builder.Build;
import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.builder.FaceVertex;
import com.owens.oobjloader.builder.VertexGeometric;
import com.owens.oobjloader.parser.Parse;

/**
 * @author Bruno
 *
 */
public class ObjParser extends Parser {



	Build builder = new Build();
	Parse parser = null;
	
	/**
	 * @param filename
	 */
	public ObjParser(String filename, Entity entity, MasterDAO dao){
		Primitive primitive = new Primitive(entity, Primitive.IMPORTED);
		try {
			dao.getDao(Primitive.class).insert(primitive);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
        try {
			parser = new Parse(builder, filename);
		} catch (FileNotFoundException e) {
			Log.log(Level.FINEST, "[ERROR] File not found!");
		} catch (IOException e) {
			Log.log(Level.FINEST, "[ERROR] File not found!");
		}
        
        for (int i = 0; i< builder.verticesG.size(); ++i){
        	VertexGeometric vertice = builder.verticesG.get(i);
        	Vertex vertex = new Vertex(primitive,new Vector3f(vertice.x,vertice.y,vertice.z));
        	vertex.setIndex(i);
        	try {
				dao.getDao(Vertex.class).insert(vertex);
			} catch (SQLException e) {
				e.printStackTrace();
			}
        }
        ArrayList<Integer> indexes = new ArrayList<Integer>();
        ArrayList<Face> faces = builder.faces;
        for (int i=0;i<faces.size();++i){
			Primitive p = this.primitives.lastElement();
			
			ArrayList<FaceVertex> fv = faces.get(i).vertices;
			VertexGeometric gv1 = fv.get(0).v;
			Vertex v1 = new Vertex(p, new Vector3f(gv1.x,gv1.y,gv1.z));
			VertexGeometric gv2 = fv.get(1).v;
			Vertex v2 = new Vertex(p, new Vector3f(gv2.x,gv2.y,gv2.z));
			VertexGeometric gv3 = fv.get(2).v;
			Vertex v3 = new Vertex(p, new Vector3f(gv3.x,gv3.y,gv3.z));
			Triangle triangle = new Triangle(p,v1,v2,v3);
			triangle.setIndex(i);
			
			try {
				dao.getDao(Triangle.class).insert(triangle);
			} catch (SQLException e) {
				e.printStackTrace();
			}
        	
        }
        
        Log.log(Level.FINEST, "[DEBUG] Loaded vertices : " + builder.verticesG.toString());

	}
	
	public int getVertexIndex(VertexGeometric vertex, ArrayList<VertexGeometric> vertexList) {
		return vertexList.indexOf(vertex);
	}
		
    
    

}
