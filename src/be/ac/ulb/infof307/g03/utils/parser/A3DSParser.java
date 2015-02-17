/**
 * 
 */
package be.ac.ulb.infof307.g03.utils.parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

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

	private long currentVertexCount() throws SQLException{
		GeometricDAO<Vertex> dao = this.daoFactory.getDao(Vertex.class);
		return dao.countOf(dao.queryBuilder().setCountOf(true).where().eq("primitive_id", this.primitive.getId()).prepare());
	}

	private ArrayList<Vertex> getCurrentVertices() throws SQLException {
		GeometricDAO<Vertex> dao = this.daoFactory.getDao(Vertex.class);
		return new ArrayList<Vertex>(dao.queryForEq("primitive_id", this.primitive.getId()));
	}

	private void parseVerticesList() throws IOException, SQLException {
		int nVertices = (int) this.readInt(2);
		GeometricDAO<Vertex> vertexDao = this.daoFactory.getDao(Vertex.class);
		for (int i=0; i<nVertices; i++) {
			float x = readFloat();
			float y = readFloat();
			float z = readFloat();
			Vertex res = new Vertex(this.primitive, x, y, z);
			res.setIndex(i);
			vertexDao.create(res);
		}
		Log.log(Level.FINEST,"[DEBUG] Found " + nVertices + " vertices");
	}

	private void parseObjectChunk() throws IOException, SQLException {
		byte[] nameBytes = new byte[64];
		int c = 1;
		for (int i=0; i<64 && c != 0; i++){
			c = this.inFile.read();
			nameBytes[i] = (byte) c;
		}
		String name = new String(nameBytes);
		Log.debug(" ==> name: %s", name);
		if (this.currentVertexCount() > 0){
			this.primitive = new Primitive(this.primitive.getEntity(), Primitive.IMPORTED);
			this.daoFactory.getDao(Primitive.class).create(this.primitive);
		}
	}

	private void parseFacesDescription() throws IOException, SQLException {
		int numFaces = (int) this.readInt(2);
		GeometricDAO<Triangle> triangleDao = this.daoFactory.getDao(Triangle.class);
		ArrayList<Vertex> vertices = this.getCurrentVertices();

		for (int i=0; i<numFaces; i++) {
			Vertex[] uvw = new Vertex[3];
			for (int j=0; j<3; j++){
				int index = (int) this.readInt(2);
				Log.debug("Found index %d", index);
				assert 0 <= index && index < vertices.size();
				uvw[j] = vertices.get(index);
			}
			this.readInt(2); //Skip flags
			Triangle face = new Triangle(this.primitive, uvw);
			face.setIndex(i);
			try {
				triangleDao.create(face);
			} catch (SQLException err) {
				Log.warn("Unable to insert triangle with vertices %s %s %s", uvw[0].toString(), uvw[1].toString(), uvw[2].toString());
			}
		}
		Log.log(Level.FINEST,"[DEBUG] Found " + numFaces + " faces");
	}

	private float readFloat() throws IOException{
		return Float.intBitsToFloat((int) this.readInt(4));
	}

	public static long parseInt(byte[] bytes){
		long res = 0;
		for (int i=0; i<bytes.length; i++){
			// THX JAVA FOR 4LL THIS MARVELOUS ADVANCED FEATURES ON ELEMENTARY NUBERZZZZ !!!!
			res += (((int) bytes[i]&0xff) << (8*i));
		}
		return res;
	}

	private long readInt(int nBytes) throws IOException{
		byte[] data = new byte[nBytes];
		this.inFile.read(data);
		return parseInt(data);
	}

	private void parseChunk() throws IOException, SQLException{
		int identifier = (int) readInt(2);
		long len = readInt(4) - 6; // 6 bytes header

		Log.debug("Parse chunk id=%04x, len=%d (%08x)", identifier, len, len);

		assert len >= 0;

		switch (identifier) {
		/* Final sections (we should load their content) */
		case 0x4000:
			parseObjectChunk();
			break;
		case 0x4110:
			/* vertex = 3 floats, 4 bytes each => 12 bytes per vertex */
			parseVerticesList();
			Log.debug(" ==> Vertices");
			break;
		case 0x4120:
			/* face = 3 indexes, 2 bytes each => 6 bytes per face */
			parseFacesDescription();
			Log.debug(" ==> Faces");
			break;

			/* Interesting section (we should explore these) */
		case 0x4d4d:
		case 0x3d3d:
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
