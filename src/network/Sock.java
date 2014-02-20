package network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

/**
 * Handles socket connections
 * @author aschmid
 *
 */
public class Sock implements Runnable {
	/** This client's socket */
	private Socket socket;
	
	/** Used to send data to this client */
	private DataOutputStream out;
	
	/** Used to receive data from this client */
	private DataInputStream in;
	
	/** Stores this sockets current message */
	private Buffer sockBuff;
	
	/** The thread this socket is running in */
	protected Thread thread;
	
	/** Should this socket be running? */
	protected boolean running = true;
	
	/**
	 * Creates a new sock
	 * @param socket The socket to use with this sock
	 */
	public Sock(Socket socket) {
		// Store vars
		this.socket = socket;
		
		// Grab this client's IO handles
		try {
			out = new DataOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));
			in = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		// Allocate the buffer
		sockBuff = new Buffer(1024);
		
		// Start thread for this socket
		thread = new Thread(this);
	}
	
	/**
	 * Cleans up stuff, needed for sub classes
	 */
	public void cleanup() {
		// Nothing much happens
	}
	
	/**
	 * Runs the socket, making it read data, etc
	 */
	public void run() {
		while(running) {
			try {
				// Make sure we are still connected
				if(socket.isClosed()) {
					System.out.println("Socket was closed!");
					this.cleanup();
					running = false;
					return;
				}
				
				// Read header
				int size = -1;
				
				try {
					size = in.readUnsignedShort();
				} catch(EOFException e) {
					System.out.println("READ ERROR");
					this.cleanup();
					running = false;
					return;
				} catch(IOException e) {
					System.out.println("Connection was closed!");
					this.cleanup();
					running = false;
					return;
				}
				
				// Check if we got a message
				if(size == -1) {
					Thread.sleep(1);
				} else {
					// Make sure there is some data
					if(size >= 1) {
						// Prepare the message
						Buffer buff = new Buffer(size);
						in.read(buff.getBuffer(), 0, size);
						
						// Process the message
						processMessage(buff);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				running = false;
				return;
			} catch (InterruptedException e) {
				e.printStackTrace();
				running = false;
				return;
			}
		}
	}
	
	/**
	 * Checks if this socket is valid
	 * @return if this socket is valid
	 */
	public boolean isValid() {
		// Check if the thread for this is still running
		return running;
	}
	
	/**
	 * processess messages, used for sub classes
	 * @param buff the buffer to read from
	 */
	protected void processMessage(Buffer buff) {
		// Stub function, does nothing by default
	}
	
	/**
	 * sends a message
	 */
	public void sendMessage() {
		try {
			// Grab the size of the message
			int size = this.sockBuff.size();
			
			// Write the size
			out.writeShort(size);
			
			// Write the data
			out.write(this.sockBuff.getBuffer(), 0, size);
			
			// Send the message
			out.flush();
		} catch (IOException e) {
			System.out.println("Write Failed");
			
			// Stop running the thread
			running = false;
		}
	}
	
	/**
	 * sends a message based on the given buffer
	 * @param buff buffer to send
	 */
	public void sendMessage(Buffer buff) {
		try {
			// Grab the size of the message
			int size = buff.size();
			
			// Write the size
			out.writeShort(size);
			
			// Write the data
			out.write(buff.getBuffer(), 0, size);
			
			// Send the message
			out.flush();
		} catch (IOException e) {
			System.out.println("Failed to write stuff");
			
			// Stop running the thread
			running = false;
		}
	}
	
	/**
	 * clears the current buffer
	 */
	public void clearBuffer() {
		sockBuff.clearBuffer();
	}
	
	/**
	 * writes a byte of data to the current buffer
	 * @param data byte of data
	 */
	public void writeByte(byte data) {
		sockBuff.writeByte(data);
	}
	
	/**
	 * reads a byte of data from the current buffer
	 * @return a byte of data
	 */
	public byte readByte() {
		return sockBuff.readByte();
	}
	
	/**
	 * Write a boolean of data to the current buffer
	 * @param data boolean of data
	 */
	public void writeBoolean(boolean data) {
		sockBuff.writeBoolean(data);
	}
	
	/**
	 * Reads a boolean of data from the current buffer
	 * @return a boolean of data
	 */
	public boolean readBoolean() {
		return sockBuff.readBoolean();
	}
	
	/**
	 * Writes a short of data to the current buffer
	 * @param data a short of data
	 */
	public void writeShort(short data) {
		sockBuff.writeShort(data);
	}
	
	/**
	 * reads a short of data from teh current client
	 * @return a short of data
	 */
	public short readShort() {
		return sockBuff.readShort();
	}
	
	/**
	 * Writes an int of data to the current buffer
	 * @param data an int of data
	 */
	public void writeInt(int data) {
		sockBuff.writeInt(data);
	}
	
	/**
	 * reads an int of data from the current buffer
	 * @return an int of data
	 */
	public int readInt() {
		return sockBuff.readInt();
	}
	
	/**
	 * writes a long of data to the current buffer
	 * @param data a long of data
	 */
	public void writeLong(long data) {
		sockBuff.writeLong(data);
	}
	
	/**
	 * reads a long of data from the current buffer
	 * @return a long of data
	 */
	public long readLong() {
		return sockBuff.readLong();
	}
	
	/**
	 * writes a float of data to the current buffer
	 * @param data a float of data
	 */
	public void writeFloat(float data) {
		sockBuff.writeFloat(data);
	}
	
	/**
	 * reads a float of data from the current buffer
	 * @return a float of data
	 */
	public float readFloat() {
		return sockBuff.readFloat();
	}
	
	/**
	 * Writes a string to the current buffer
	 * @param data a string of data
	 */
	public void writeString(String data) {
		sockBuff.writeString(data);
	}
	
	/**
	 * Reads a string from teh current buffer
	 * @return A string of data
	 */
	public String readString() {
		return sockBuff.readString();
	}
}
