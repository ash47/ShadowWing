package gameObjects;

import java.util.Hashtable;

import network.Buffer;
import network.Msg;

import core.Game;

/**
 * A class for easily networking game objects
 * @author aschmid
 *
 */
public class NetworkedObject extends VisibleGameObject {
	/** The entity ID of this object, used to identify stuff across the network */
	private short entID = -1;
	
	/** This is a unique ID that represents a networked object, each subclass should have its own */
	protected int entType;
	
	/** The type of this object, 'player', 'enemy' etc */
	protected String myType;
	
	/** The last time we sent a network update */
	private long lastUpdate = 0;
	
	/** The max time in ms between an update */
	private static final int maxUpdateDelay = 2500;
	
	/** A shared buffer used to send data */
	private static Buffer sharedBuffer = new Buffer(32);
	
	/** Stores the types this unit collides with */
	Hashtable<String, Boolean> myCollisions;
	
	/**
	 * Creates a new networked entity
	 */
	public NetworkedObject() {
		super();
		
		// Setup collisions
		setupCollisions();
	}
	
	/**
	 * Sets up collision for this entity
	 */
	public void setupCollisions() {
		// Only the server needs to know about collisions
		if(Game.isServer) {
			// Create hash table for collision mapping
			this.myCollisions = new Hashtable<String, Boolean>();
			
			// Default our type to networked object
			this.myType = "NetworkedObject";
		}
	}
	
	/**
	 * Makes this entity collide with another type of entity
	 * @param name The name of the entity to collide with
	 */
	public void addCollision(String name) {
		// Add the type
		this.myCollisions.put(name, true);
	}
	
	/**
	 * Checks if this entity should collide with the type given
	 * @param name The type of entity you to collide with
	 * @return If this entity should collide or not
	 */
	public boolean collidesWith(String name) {
		// Check if we there is a collision between this element and the other
		return myCollisions.containsKey(name);
	}
	
	/**
	 * Checks if this entity has any collisions
	 * @return If this entity has any collisions
	 */
	public boolean hasCollisions() {
		// Check if we have any mapped collisions
		return this.myCollisions.size() > 0;
	}
	
	/**
	 * Gets the type of this entity
	 * @return The type of this entity
	 */
	public String getType() {
		// Return the type
		return this.myType;
	}
	
	/**
	 * Sets the type of this entity
	 * @param type The type to set this entity to
	 */
	public void setType(String type) {
		// Store the type
		this.myType = type;
	}
	
	/**
	 * Sets up this entity, does networking handshale
	 * @param x X position to start at
	 * @param y Y position to start at
	 * @param image The name of the image to use
	 */
	public void setup(float x, float y, String image) {
		// Set position
		this.setPos(x, y);
		
		// Check if an image was parsed
		if(!image.equals("")) {
			// Set the name of the image
			this.setImageName(image);
		}
		
		// Queue it to load the image
		Game.getWorld().loadImage(this);
		
		// Store this entity
		Game.getWorld().storeEnt(this);
	}
	
	/**
	 * Removes this entity
	 */
	public void cleanup() {
		// If we aren't valid, do nothing
		if(!this.isValid()) return;
		
		// This object is no longer valid
		this.setValid(false);
		
		// Only the server will sync objects
		if(!Game.isServer) return;
		
		// Have the world remove us
		Game.getWorld().removeEntByID(this.getEntID());
		
		// Setup message
		sharedBuffer.clearBuffer();
		sharedBuffer.writeByte(Msg.REMOVE_ENT);
		sharedBuffer.writeShort(this.getEntID());
		
		// Broadcast the message
		Game.getServer().broadcast(sharedBuffer);
	}
	
	/**
	 * Gets this entities entity ID
	 * @return This entities entity ID
	 */
	public short getEntID() {
		return this.entID;
	}
	
	/**
	 * Handles collisions
	 * @param ent2 The ent that collided with us
	 */
	public void collide(NetworkedObject ent2) {
		// Handle collisions
	}
	
	/*
	 * WRITING DATA
	 */
	
	/**
	 * Checks if we should do a sync
	 */
	public void checkSync() {
		// Only server can do  this
		if(!Game.isServer) return;
		
		// Check if it's been a while since we updated
		if(System.currentTimeMillis() - this.lastUpdate > maxUpdateDelay) {
			// Update
			this.networkSync();
		}
	}
	
	/**
	 * Syncs other vars (stats, etc)
	 */
	public void syncVars() {
		if(!Game.isServer) return;
		
		// Write all the data to the buffer
		sharedBuffer.clearBuffer();
		sharedBuffer.writeByte(Msg.ENT_DATA);
		sharedBuffer.writeShort(this.getEntID());
		
		// Sync all the other variables
		syncVarsOther(sharedBuffer);
		
		// Broadcast the message
		Game.getServer().broadcast(sharedBuffer);
	}
	
	/**
	 * Adds entity specific vars
	 * @param buff The buffer to store to
	 */
	public void syncVarsOther(Buffer buff) {
		// Sync other data here
	}
	
	/**
	 * Sets this entities entity ID, broadcasts it to clients
	 * @param entID The entity ID of this ent
	 */
	public void setEntID(short entID) {
		// Only do this if our entID is unset
		if(this.entID != -1) return;
		
		// Store the entID
		this.entID = entID;
		
		// Tell the clients a new entity was created
		
		// Only the server will sync objects
		if(!Game.isServer) return;
		
		// Write all the data to the buffer
		networkInitWrite(sharedBuffer);
		
		// Broadcast the message
		Game.getServer().broadcast(sharedBuffer);
	}
	
	/**
	 * Initial write of networked data
	 * @param buff The buffer to write to
	 */
	public void networkInitWrite(Buffer buff) {
		// Only the server will sync objects
		if(!Game.isServer) return;
		
		// Setup message
		buff.clearBuffer();
		buff.writeByte(Msg.NEW_ENT);
		buff.writeShort(this.getEntID());
		buff.writeString(getClass().getName());
		buff.writeString(this.getImageName());
		//buff.writeLong(Game.getGameTime());
		buff.writeFloat(this.getPosX());
		buff.writeFloat(this.getPosY());
		
		// Write stuff specific to other entities
		networkInitWriteOtherData(buff);
	}
	
	/**
	 * Writes extra entity specific data
	 * @param buff The buffer to write to
	 */
	public void networkInitWriteOtherData(Buffer buff) {}
	
	/**
	 * Syncs an entity to clients
	 */
	public void networkSync() {
		// Only the server will sync objects
		if(!Game.isServer) return;
		
		// Store the current time
		lastUpdate = System.currentTimeMillis();
		
		// Setup message
		sharedBuffer.clearBuffer();
		sharedBuffer.writeByte(Msg.SYNC_ENT);
		sharedBuffer.writeShort(this.getEntID());
		sharedBuffer.writeLong(Game.getGameTime());
		sharedBuffer.writeFloat(this.getPosX());
		sharedBuffer.writeFloat(this.getPosY());
		
		// Write stuff specific to other entities
		networkSyncOtherData(sharedBuffer);
		
		// Broadcast the message
		Game.getServer().broadcast(sharedBuffer);
	}
	
	/**
	 * Writes other syncing data to a buffer
	 * @param buff The buffer to write to
	 */
	public void networkSyncOtherData(Buffer buff) {}
	
	/*
	 * READING DATA
	 * */
	
	/**
	 * Reads data from a network init write
	 * @param buff Buffer to read from
	 */
	public void networkInit(Buffer buff) {
		// Read in the position
		this.setPos(buff.readFloat(), buff.readFloat());
		
		// Read in the other entity specific data
		networkInitOtherData(buff);
	}
	
	/**
	 * Writes entity specific data that needs to be synced once
	 * @param buff The buffer to write to
	 */
	public void networkInitOtherData(Buffer buff) {}
	
	/**
	 * Reads a network update
	 * @param buff Buffer to read from
	 */
	public void networkUpdate(Buffer buff) {
		// Read in the position
		this.setPos(buff.readFloat(), buff.readFloat());
		
		// Read in the other entity specific data
		networkUpdateOtherData(buff);
	}
	
	/**
	 * Reads other data from a network update
	 * @param buff The buffer to read from
	 */
	public void networkUpdateOtherData(Buffer buff) {}
	
	/**
	 * Reads network data
	 * @param buff Buffer to read from
	 */
	public void networkData(Buffer buff) {
		// Sync netowrk data from syncVarsOther here
	}
}
