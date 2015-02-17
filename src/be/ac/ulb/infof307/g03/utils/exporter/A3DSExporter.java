package be.ac.ulb.infof307.g03.utils.exporter;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import be.ac.ulb.infof307.g03.models.*;


/**
 * @author pierre
 *
 */
public class A3DSExporter {
	DataOutputStream outFile;
	
	/**
	 *  Constructor of A3DSExporter
	 */
	public A3DSExporter(){
		
	}
	
	/**
	 * Convert an integer to a writable representation
	 * @param n The number
	 * @param nBytes The number of bytes to encode this number
	 * @return An integer array of length nBytes
	 */
	public static int[] convertInt(long n, int nBytes){
		int[] res = new int[nBytes];
		for (int i=0; i<nBytes; i++)
			res[i] = (int) ((n>>(8*i)) & 0xff);
		return res;
	}
	
	private void writeInt(long n, int nBytes) throws IOException{
		for (int k : convertInt(n, nBytes)){
			this.outFile.write(k);
		}
	}
	
	private void writeHeader(int identifier, long size) throws IOException{
		this.writeInt(identifier, 2);
		this.writeInt(size, 4);
	}
	
	/**
	 * Export to file
	 * @param fileToExport The file in which the object will be write
	 * @param entity The entity to be exported
	 * @throws IOException 
	 */
	public void export(File fileToExport, Entity entity) throws IOException{
		List<Primitive> primitives = new ArrayList<Primitive>(entity.getPrimitives());
		int nChunks = primitives.size();
		int[] chunkSizes = new int[nChunks];
		int allChunkSizes = 0;
		
		for (int i=0; i<nChunks; i++){
			Primitive prim = primitives.get(i);
			chunkSizes[i] = 29 + prim.toString().length() + 4*prim.getVertices().length + 2*4*prim.getIndexes().length/3;
			allChunkSizes += chunkSizes[i];
		}
		
		this.outFile = new DataOutputStream(new FileOutputStream(fileToExport));
		/* Common header */
		this.writeHeader(0x4d4d, allChunkSizes+12);
		this.writeHeader(0x3d3d, allChunkSizes+6);
		
		for (int i=0; i<nChunks; i++){
			Primitive prim = primitives.get(i);
			String name = prim.toString();
			
			/* Object section (name) */
			this.writeHeader(0x4000, chunkSizes[i]);
			this.outFile.write(name.getBytes());
			this.outFile.write('\0');
			
			/* Geometry section */
			this.writeHeader(0x4100, chunkSizes[i] - (7 + name.length()));
			float[] vertices = prim.getVertices();
			int[] indexes = prim.getIndexes();
			
			/* Vertices (floats) */
			this.writeHeader(0x4110, 4*vertices.length + 6);
			this.writeInt(vertices.length/3, 2);
			for (int j=0; j<vertices.length; j++){
				this.writeInt(Float.floatToIntBits(vertices[j]), 4);
			}
			
			/* Indexes (integers) */
			this.writeHeader(0x4120, 2*4*indexes.length/3 + 6);
			this.writeInt(indexes.length/3, 2);
			for (int j=0; j<indexes.length; j++){
				this.writeInt(indexes[j], 2);
				if (j%3 == 2){
					this.writeInt(0, 2);
				}
			}
		}
		
		this.outFile.close();
	}
}