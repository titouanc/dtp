/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.awt.List;
import java.io.IOException;
import java.util.logging.Level;

import be.ac.ulb.infof307.g03.utils.Log;

/**
 * @author Bruno
 *
 */
public class A3DSParser extends Parser {

	/* (non-Javadoc)
	 * @see be.ac.ulb.infof307.g03.models.Parser#getVertices()
	 */

	/**
	 * This parser understands the layout of the 3ds file and is
	 * able to construct a Model from a reader.
	 *
	 * @author Bruno
	 */

    private A3DSReader reader;
    private A3DSObject currentObject;

    public A3DSParser(A3DSReader reader) {
        this.reader = reader;
    }

    public void parseFile() throws IOException {
        int limit = readChunk();
        while (reader.position() < limit) {
            readChunk();
        }
    }

    private int readChunk() throws IOException {
        short type = reader.getShort();
        int size = reader.getInt();
        parseChunk(type, size);
        return size;
    }

    private void parseChunk(short type, int size) throws IOException {
        switch (type) {
        case 0x0002:
            parseVersionChunk();
            break;
        case 0x3d3d:
            break;
        case 0x4000:
            parseObjectChunk();
            break;
        case 0x4100:
            break;
        case 0x4110:
            parseVerticesList();
            break;
        case 0x4120:
            parseFacesDescription();
            break;
        case 0x4140:
            parseMappingCoordinates();
            break;
        case 0x4160:
            parseLocalCoordinateSystem();
            break;
        case 0x4d4d:
            break;
        case (short)0xafff: // Material block
            break;
        case (short)0xa000: // Material name
            parseMaterialName();
            break;
        case (short)0xa200: // Texture map 1
            break;
        case (short)0xa300: // Mapping filename
            parseMappingFilename();
            break;
        default:
            skipChunk(size);
        }
    }

    private void skipChunk(int size) throws IOException {
        move(size - 6); // size includes headers. header is 6 bytes
    }

    private void move(int i) throws IOException {
        reader.skip(i);
    }


    private void parseVersionChunk() throws IOException {
        int version = reader.getInt();
        Log.log(Level.FINEST,"[DEBUG]Using version " + version);
    }

    private void parseObjectChunk() throws IOException {
        String name = reader.readString();
        Log.log(Level.FINEST,"[DEBUG]Found object : " + name);
        currentObject = new A3DSObject(name);
    }

    private void parseVerticesList() throws IOException {
        short numVertices = reader.getShort();
        float[] vertices = new float[numVertices * 3];
        for (int i=0; i<vertices.length; i++) {
            vertices[i] = reader.getFloat();
        }

        currentObject.setVertices(vertices);
        Log.log(Level.FINEST,"[DEBUG]Found " + numVertices + " vertices");
    }

    private void parseFacesDescription() throws IOException {
        short numFaces = reader.getShort();
        short[] faces = new short[numFaces * 3];
        for (int i=0; i<numFaces; i++) {
            faces[i*3] = reader.getShort();
            faces[i*3 + 1] = reader.getShort();
            faces[i*3 + 2] = reader.getShort();
            reader.getShort(); // Discard face flag
        }
        Log.log(Level.FINEST,"[DEBUG]Found " + numFaces + " faces");
        currentObject.setPolygons(faces);
    }

    private void parseLocalCoordinateSystem() throws IOException {
        float[] x1 = new float[3];
        float[] x2 = new float[3];
        float[] x3 = new float[3];
        float[] origin = new float[3];
        readVector(x1);
        readVector(x2);
        readVector(x3);
        readVector(origin);
    }

    private void parseMappingCoordinates() throws IOException {
        short numVertices = reader.getShort();
        float[] uv = new float[numVertices * 2];
        for (int i=0; i<numVertices; i++) {
            uv[i*2] = reader.getFloat();
            uv[i*2+1] = reader.getFloat();
        }
        //currentObject.textureCoordinates = uv;
    }

    private void parseMaterialName() throws IOException {
        String materialName = reader.readString();
    }

    private void parseMappingFilename() throws IOException {
        String mappingFile = reader.readString();
    }

    private void readVector(float[] v) throws IOException {
        v[0] = reader.getFloat();
        v[1] = reader.getFloat();
        v[2] = reader.getFloat();
    }


}
