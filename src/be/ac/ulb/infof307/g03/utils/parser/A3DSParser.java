/**
 * 
 */
package be.ac.ulb.infof307.g03.utils.parser;

import java.awt.List;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;

import com.jme3.math.Vector3f;

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
public class A3DSParser extends Parser {
	private FileInputStream inFile;
	private static int[] interestingSections = {0x4d4d, 0x3d3d, 0x4000, 0x4100};

	/**
	 * This parser understands the layout of the 3ds file and is
	 * able to construct a Model from a reader.
	 *
	 * @author Bruno
	 * @throws SQLException 
	 */
    public A3DSParser(String filename, MasterDAO daoFactory) throws IOException, SQLException  {
        super(filename, daoFactory);
        this.inFile = new FileInputStream(filename);
    }

    private void parseVerticesList(int nVertices) throws IOException, SQLException {
    	GeometricDAO<Vertex> vertexDao = this.daoFactory.getDao(Vertex.class);
        for (int i=0; i<nVertices; i++) {
        	float x = readFloat();
        	float y = readFloat();
        	float z = readFloat();
        	Vertex res = new Vertex(this.primitive, x, y, z);
        	vertexDao.create(res);
        }
        Log.log(Level.FINEST,"[DEBUG]Found " + nVertices + " vertices");
    }

    private void parseFacesDescription(int numFaces) throws IOException, SQLException {
        GeometricDAO<Triangle> triangleDao = this.daoFactory.getDao(Triangle.class);
    	ArrayList<Vertex> vertices = new ArrayList<Vertex>(this.daoFactory.getDao(Vertex.class).queryForAll());
    	
        for (int i=0; i<numFaces; i++) {
        	Vertex[] uvw = new Vertex[3];
        	for (int j=0; j<3; j++){
        		int index = (int) this.readLong(2);
        		assert 1 <= index && index <= vertices.size();
        		uvw[j] = vertices.get(index-1);
        	}
        	Triangle face = new Triangle(this.primitive, uvw);
        	triangleDao.create(face);
        }
        Log.log(Level.FINEST,"[DEBUG]Found " + numFaces + " faces");
    }
    
    private float readFloat() throws IOException{
    	return Float.intBitsToFloat((int) this.readLong(4));
    }
    
    private long readLong(int nBytes) throws IOException{
    	byte[] data = new byte[nBytes];
    	this.inFile.read(data);
    	long res = 0;
    	for (int i=0; i<nBytes; i++){
    		res += (data[i] << (8*i));
    	}
    	return res;
    }
    
    private void parseChunk() throws IOException, SQLException{
    	int identifier = (int) readLong(2);
		long len = readLong(4) - 6; // 6 bytes header
		
		Log.debug("Parse chunk %04x (len=%d %08x)", identifier, len, len);
	
		assert len >= 0;
		
		switch (identifier) {
	    	/* Final sections (we should load their content) */
	        case 0x4110:
	        	/* vertex = 3 floats, 4 bytes each => 12 bytes per vertex */
	            parseVerticesList((int) len / 12);
	            Log.debug(" ==> Vertices");
	            break;
	        case 0x4120:
	        	/* face = 3 indexes, 2 bytes each => 6 bytes per face */
	            parseFacesDescription((int) len / 6);
	            Log.debug(" ==> Faces");
	            break;
	            
	        /* Interesting section (we should explore these) */
	        case 0x4d4d:
	        case 0x3d3d:
	        case 0x4000:
	        case 0x4100:
	        	Log.debug(" ==> Dive");
	        	parseChunk();
	        	break;
	        	
	        /* Otherwise just skip */
	        default:
	            this.inFile.skip(len);
	            Log.debug(" ==> Skip");
        }
    }
    
    @Override
	public void parse() throws SQLException, IOException {
		while (inFile.available() > 0){
			parseChunk();
		}
	}
}
