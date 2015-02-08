/**
 * 
 */
package be.ac.ulb.infof307.g03.models;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @author Bruno
 *
 */
public class A3DSReader {

    private BufferedInputStream stream;
    private int position = 0;

    /**
     * @param stream
     */
    public A3DSReader(BufferedInputStream stream) {
        this.stream = stream;
    }

    /**
     * @param stream
     */
    public A3DSReader(InputStream stream) {
        this.stream = new BufferedInputStream(stream);
    }

    /**
     * @return the next 2 bytes as short
     * @throws IOException
     */
    public short getShort() throws IOException {
        byte b0 = getByte();
        byte b1 = getByte();
        //short is 2 bytes
        return makeShort(b1, b0);
    }

    /**
     * @return the next 4 bytes as integer
     * @throws IOException
     */
    public int getInt() throws IOException {
        byte b0 = getByte();
        byte b1 = getByte();
        byte b2 = getByte();
        byte b3 = getByte();
        //int is 4 bytes
        return makeInt(b3, b2, b1, b0);
    }

    /**
     * @return the next 4 bytes as float
     * @throws IOException
     */
    public float getFloat() throws IOException {
        return Float.intBitsToFloat(getInt());
    }

    private byte getByte() throws IOException {
        int read = stream.read();
        if (read == -1) {//End of file
            throw new EOFException();
        }
        position++;
        return (byte) read;
    }

    /**
     * Skips the next i*4 bytes
     * @param i
     * @throws IOException
     */
    public void skip(int i) throws IOException {
        int skipped = 0;
        do {
            skipped += stream.skip(i - skipped);
        } while (skipped < i);

        position += i;
    }

    /**
     * @return
     * @throws IOException
     */
    public String readString() throws IOException {
        StringBuilder sb = new StringBuilder(64);
        byte ch = getByte();
        while (ch != 0) {
            sb.append((char)ch);
            ch = getByte();
        }
        return sb.toString();
    }

    /**
     * @return
     */
    public int position() {
        return position;
    }

    static private int makeInt(byte b3, byte b2, byte b1, byte b0) {
    	//source : http://stackoverflow.com/questions/14583442/java-bytebuffer-convert-three-bytes-to-int
        return (((b3 & 255) << 24) |
                ((b2 & 255) << 16) |
                ((b1 & 255) <<  8) |
                ((b0 & 255)      ));
    }

    static private short makeShort(byte b1, byte b0) {
        return (short)(((b1 & 255)<< 8) |
        			    (b0 & 255     ));
    }
}
