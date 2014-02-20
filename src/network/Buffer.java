package network;
import java.nio.ByteBuffer;

/**
 * A class for reading / writing data EASILY
 * @author Ash
 *
 */
public class Buffer {
	/** The place we store the data */
	ByteBuffer buff;
	
	/**
	 * Create a new buffer
	 * @param size the size of the buffer to start with
	 */
	public Buffer(int size) {
		buff = ByteBuffer.allocate(size);
	}
	
	/**
	 * Empties the buffer
	 */
	public void clearBuffer() {
		buff.clear();
	}
	
	/**
	 * Ensures there is at least `size` more space left, if not, it allocates more space
	 * @param size the amount of space to allocate
	 */
	private void allocateSpace(int size) {
		// Work out how much space will be needed
		int spaceNeeded = buff.position() + size;
		
		// Check if we have enough space
		if(spaceNeeded > buff.capacity()) {
			ByteBuffer newBuff = ByteBuffer.allocate(spaceNeeded);
			
			// Copy the old buffer in
			newBuff.put(buff.array(), 0, size());
			
			// Replace our old buffer
			buff = newBuff;
		}
	}
	
	/**
	 * gets the byte buffer
	 * @return the byte buffer
	 */
	protected byte[] getBuffer() {
		return buff.array();
	}
	
	/**
	 * gets how much data is written to this buffer
	 * @return how much data is written to this buffer
	 */
	public int size() {
		return buff.position();
	}
	
	/**
	 * writes a byte to teh buffer
	 * @param data a byte of data
	 */
	public void writeByte(byte data) {
		// Stop overflow
		allocateSpace(1);
		
		// Write data
		buff.put(data);
	}
	
	/**
	 * reads a byte of data
	 * @return a byte of data
	 */
	public byte readByte() {
		return buff.get();
	}
	
	/**
	 * writes a boolean of data
	 * @param data boolean of data
	 */
	public void writeBoolean(boolean data) {
		// Stop overflow
		allocateSpace(1);
		
		// Write data
		if(data) {
			buff.put((byte)1);
		} else {
			buff.put((byte)0);
		}
	}
	
	/**
	 * reads a boolean of data
	 * @return a boolean of data
	 */
	public boolean readBoolean() {
		return (buff.get() != 0);
	}
	
	/**
	 * writes a short of data
	 * @param data short of data
	 */
	public void writeShort(short data) {
		// Stop overflow
		allocateSpace(2);
		
		buff.putShort(data);
	}
	
	/**
	 * reads a short of data
	 * @return a short of data
	 */
	public short readShort() {
		return buff.getShort();
	}
	
	/**
	 * writes an int of data
	 * @param data an int of data
	 */
	public void writeInt(int data) {
		// Stop overflow
		allocateSpace(4);
		
		// Write data
		buff.putInt(data);
	}
	
	/**
	 * reads an int of data
	 * @return an int of data
	 */
	public int readInt() {
		return buff.getInt();
	}
	
	/**
	 * writes a long of data
	 * @param data a long of data
	 */
	public void writeLong(long data) {
		// Stop overflow
		allocateSpace(8);
		
		// Write data
		buff.putLong(data);
	}
	
	/**
	 * reads a long of data
	 * @return a long of data
	 */
	public long readLong() {
		return buff.getLong();
	}
	
	/**
	 * writes a float of data
	 * @param data float of data
	 */
	public void writeFloat(float data) {
		// Allocate space
		allocateSpace(4);
		
		// Write data
		buff.putFloat(data);
	}
	
	/**
	 * reads a float of data
	 * @return a float of data
	 */
	public float readFloat() {
		return buff.getFloat();
	}
	
	/**
	 * writes a string of data
	 * @param data string of data
	 */
	public void writeString(String data) {
		// Stop overflow
		allocateSpace(data.length()+1);
		
		// Write the string
		buff.put(data.getBytes());
		
		// Null terminate
		buff.put((byte)0);
	}
	
	/**
	 * reads a string of data
	 * @return a string of data
	 */
	public String readString() {
		// Create a string buffer
		StringBuffer str = new StringBuffer();
		
		// Build the string
		byte a;
		while((a=buff.get()) != 0) {
			str.append((char)a);
		}
		
		// Return the string
		return str.toString();
	}
	
	/**
	 * sets the position in the buffer to read / write from
	 * @param pos
	 */
	public void setPos(int pos) {
		// Change the buffers position
		buff.position(pos);
	}
}
