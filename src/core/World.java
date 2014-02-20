package core;
/* SWEN20003 Object Oriented Software Development
 * Space Game Engine - Skeleton
 */

import gameObjects.Camera;
import gameObjects.NI_Repair;
import gameObjects.NI_Shield;
import gameObjects.NI_Firepower;
import gameObjects.NO_Bullet;
import gameObjects.NS_Asteroid;
import gameObjects.NS_Base;
import gameObjects.NS_Boss;
import gameObjects.NS_Drone;
import gameObjects.NS_Fighter;
import gameObjects.NS_Player;
import gameObjects.NetworkedObject;
import gameObjects.VisibleGameObject;

import java.util.ArrayList;
import java.util.Iterator;

import network.Buffer;
import network.Msg;
import network.ServerClient;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

import Particles.Emitter;

/** Represents the entire game world.
 * (Designed to be instantiated just once for the whole game).
 */
public class World {
	/** This world's map. */
	private TiledMap map;
	
	/** This world's active camera. */
	private Camera activeCamera;
	
	/** Stores bullets */
	private ArrayList<NetworkedObject> bullets;
	
	/** Stores all the objects that need images to be loaded */
	private ArrayList<VisibleGameObject> needImages;
	
	/** These are ents networked with the auto network library */
	private ArrayList<NetworkedObject> networkedEnts;
	
	/** Stores all the particle systems */
	private ArrayList<Emitter> emitters;
	
	/** Do we need to load a checkpoint? */
	private boolean loadCheck = false;
	
	/** All networked objects are stored in this array */
	private NetworkedObject entList[];
	
	/** The width of the view we are currently rendering */
	private int view_width;
	
	/** The height of the view we are currently rendering */
	private int view_height;
	
	/** The x-offset to render the map at. */
	private int worldOffsetX;
	
	/** The y-offset to render the map at. */
	private int worldOffsetY;
	
	/** Which tile to start rendering at in the x-direction. */
	private int tileNumberX;
	
	/** Which tile to start rendering at in the y-direction. */
	private int tileNumberY;
	
	/** How far bullets spawn from ships */
	private static final float BULLET_OFFSET = 50;
	
	/** The width of a tile. */
	static final private int TILE_WIDTH = 72;
	
	/** The height of a tile. */
	static final private int TILE_HEIGHT = 72;
	
	/** Starting x-coordinate for the camera. */
	static final private float CAMERA_START_X = 1296;
	
	/** Starting y-coordinate for the camera. */
	static final private float CAMERA_START_Y = 13716 - Game.playheight()/2;
	
	/** Starting x-coordinate for the player. */
	static final public float PLAYER_START_X = 1296;
	
	/** Starting y-coordinate for the player. */
	static final public float PLAYER_START_Y = 13716;
	
	/** Max number of ents we can store */
	static final private int MAX_ENTS = 2048;
	
	/** The y-position of the MIDDLE of the screen */
	static private float screenY;
	
	/** The speed the screen moves upward */
	public static final float screenMoveSpeed = 0.25f;
	
    /** Create a new World object.*/
    public World()
    throws SlickException {
    	// Create a map and a player
    	map = new TiledMap("assets/map.tmx", "assets");
    	
    	// Create a new camera, and make it our active camera
    	activeCamera = new Camera(CAMERA_START_X, CAMERA_START_Y);
    	
    	// Prepare lists for game objects
    	bullets = new ArrayList<NetworkedObject>();
    	needImages = new ArrayList<VisibleGameObject>();
    	networkedEnts = new ArrayList<NetworkedObject>();
    	
    	// Prepare list for particles
    	emitters = new ArrayList<Emitter>();
    	
    	// Allows us to store upto 2048 ents at any given time
    	entList = new NetworkedObject[MAX_ENTS];
    	
    	// Set the camera's Y position
    	setScreenY(CAMERA_START_Y);
    }
    
    /**
     * Updates a group of NetworkedObjects
     * @param delta The time since the last update
     * @param group The group of NetworkedObjects to update
     */
	public synchronized void updateGroup(int delta, ArrayList<NetworkedObject> group) {
		Iterator<NetworkedObject> i = group.iterator();
    	while(i.hasNext()) {
    		// Grab the game object
    		NetworkedObject obj = i.next();
    		
    		// Check if this object is valid
    		if(obj.isValid()) {
    			// Update this game object
    			obj.update(delta);
    		} else {
    			// No, remove it
    			i.remove();
    		}
    	}
	}
    
    /** Update the game state for a frame.
     * @param delta Time passed since last frame (milliseconds).
     */
    public synchronized void update(int delta)
    throws SlickException {
    	// Check if we need to load a checkpoint
    	if(this.loadCheck) {
    		// No need to load more than once
    		this.loadCheck = false;
    		
    		// Cleanup everything
        	cleanupArray(this.bullets);
        	cleanupArray(this.networkedEnts);
        	
        	float newScreenY = screenY;
        	
        	if(newScreenY < 2844) {
        		newScreenY = 2844;
        	}else if(newScreenY < 5796) {
        		newScreenY = 5796;
        	}else if(newScreenY < 7812) {
        		newScreenY = 7812;
        	}else if(newScreenY < 9756) {
        		newScreenY = 9756;
        	}else if(newScreenY < 13716) {
        		newScreenY = 13716;
        	}
        	
        	// Set the camera's Y position
        	setScreenY(newScreenY);
        	
        	// Tell the client about the new screenY
        	Buffer buff = new Buffer(32);
        	buff.clearBuffer();
        	buff.writeByte(Msg.SCREEN_Y);
        	buff.writeLong(Game.getGameTime());
        	buff.writeFloat(World.getScreenY());
        	Game.getServer().broadcast(buff);
        	
        	// Create new players
        	NS_Player ply = this.createPlayer();
        	Game.getPlayer1().findPlayerEnt(ply.getEntID());
        	ply.setPos(1296, newScreenY);
        	
        	// Check if player2 is active
        	if(Game.getPlayer2().isActive()) {
        		ply = this.createPlayer();
            	Game.getPlayer2().findPlayerEnt(ply.getEntID());
            	ply.setPos(1296, newScreenY);
        	}
        	
        	// Remake networked clients
        	Game.getServer().remakePlayers();
        	
        	// Load the units
        	UnitLayoutReader.read("units");
        	UnitLayoutReader.read("items");
    	}
    	
    	// Create images for all those objects that need em
    	Iterator<VisibleGameObject> l = needImages.iterator();
    	while(l.hasNext()) {
    		// Grab the game object
    		VisibleGameObject obj = l.next();
    		
    		// Tell it to load it's image
    		obj.loadImage();
    		
    		// Remove this object from the iterator
    		l.remove();
    	}
    	
    	// Update all game objects
    	updateGroup(delta, bullets);
    	updateGroup(delta, networkedEnts);
    	
    	// Only the server processes collisions between objects
    	if(Game.isServer) {
    		// Holders for two ents
    		NetworkedObject ent1;
        	NetworkedObject ent2;
        	
	    	Iterator<NetworkedObject> i = networkedEnts.iterator();
	    	while(i.hasNext()) {
	    		// Grab an object
	    		ent1 = i.next();
	    		
	    		// We can skip this entity if it has no collisions
	    		if(!ent1.isValid()) continue;
	    		if(!ent1.hasCollisions()) continue;
	    		
	    		// Make sure it's actually on the screen
	    		if(!onScreen(ent1)) continue;
	    		
	    		// Check if this class collides with bullets
	    		if(ent1.collidesWith("bullet")) {
	    			Iterator<NetworkedObject> j = bullets.iterator();
		    		
		    		// Process all ents after this ent
					while(j.hasNext()) {
						// Grab a bullet
						ent2 = j.next();
						
						// Validate the ent
						if(!ent2.isValid()) continue;
						
						// Make sure it's actually on the screen
			    		if(!onScreen(ent2)) continue;
						
						// Check collisions
						if(ent1.colliding(ent2)) {
							// Run the collision
							ent1.collide(ent2);
						}
					}
	    		}
	    		
	    		Iterator<NetworkedObject> j = networkedEnts.iterator();
	    		
	    		// Process all ents after this ent
				while(j.hasNext()) {
					ent2 = j.next();
					
					// Validate entity
					if(!ent2.isValid()) continue;
					
					// No need to collide with self
					if(ent1 == ent2) continue;
					
					// Check if ent1 should collide with ent2
					if(ent1.collidesWith(ent2.getType())) {
						// Check collisions
						if(ent1.colliding(ent2)) {
							// Run the collision
							ent1.collide(ent2);
						}
					}
				}
	    	}
    	}
    	
    	// Update all particle systems
    	Iterator<Emitter> e = emitters.iterator();
    	while(e.hasNext()) {
    		// Grab the game object
    		Emitter emitter = e.next();
    		
    		// Update it
    		emitter.update(delta);
    	}
    	
    	// Move screenY
    	World.updateScreenY(delta);
    }
    
    /**
     * Creates a particle emitter
     * @param file The file to load
     * @param posX The x position to draw it
     * @param posY The y position to draw it
     */
    public synchronized void createEmitter(String file, float posX, float posY) {
    	// Create emitter
    	Emitter emitter = new Emitter(file, posX, posY, false);
    	
    	// Add to the active emitters
    	emitters.add(emitter);
    }
    
    public synchronized void cleanupArray(ArrayList<NetworkedObject> group) {
		Iterator<NetworkedObject> i = group.iterator();
    	while(i.hasNext()) {
    		// Grab the game object
    		NetworkedObject obj = i.next();
    		
    		// Check if this object is valid
    		if(obj.isValid()) {
    			// Clean it up
    			obj.cleanup();
    		}
    	}
    }
    
    /**
     * Stores that we need to load a checkpoint
     */
    public synchronized void loadCheckpoint() {
    	loadCheck = true;
    }
    
    /**
     * Checks if any players are still alive
     * @return Any players still alive
     */
    public synchronized boolean playersAlive() {
    	Iterator<NetworkedObject> i = networkedEnts.iterator();
    	while(i.hasNext()) {
    		// Grab the game object
    		NetworkedObject obj = i.next();
    		
    		// Check if this object is valid
    		if(obj.isValid()) {
    			// Check if it's a player
    			if(obj.getType() == "player") {
    				return true;
    			}
    		}
    	}
    	
    	// Default to false
    	return false;
    }
    
    /**
     * Stores an ent into the world, and syncs it to the client if we're the server
     * @param ent The ent to store
     */
    public synchronized void storeEnt(NetworkedObject ent) {
    	// Check if it's already been stored
    	if(ent.getEntID() != -1) return;
    	
    	// Try to find a free position to store this ent
    	for(short i=0; i<MAX_ENTS; i++) {
    		if(entList[i] == null) {
    			// Found a free position, store the ent
    			entList[i] = ent;
    			
    			// Store the entID into the entity
    			ent.setEntID(i);
    			
    			// Done, we can exit
    			return;
    		}
    	}
    	
    	System.out.println("No free entities!");
    	System.exit(-1);
    }
    
    /**
     * Finds an entity by it's entity ID
     * @param entID The ID of the entity to find
     * @return An entity with entID if it exists
     */
    public synchronized NetworkedObject getEntByID(short entID) {
    	// Validate input
    	if(entID < 0 || entID >= World.MAX_ENTS) return null;
    	
    	// Grab the entity
    	return entList[entID];
    }
    
    /**
     * Finds and removes an entity by it's entity ID
     * @param entID The entity ID of the entity you want to remove
     */
    public synchronized void removeEntByID(short entID) {
    	NetworkedObject ent = entList[entID];
    	if(ent == null) return;
    	
    	// Remove from our ent list
    	entList[entID] = null;
    	
    	// Cleanup the ent
    	ent.cleanup();
    }
    
    /**
     * Gets the width of the current view
     * @return The width of the current view
     */
    public int getViewWidth() {
    	return this.view_width;
    }
    
    /**
     * Gets the height of the current view
     * @return The height of the current view
     */
    public int getViewHeight() {
    	return this.view_height;
    }
    
    /**
     * Renders a group of NetworkedObjects
     * @param group The group of NetworkedObjects to render
     */
    public void renderGroup(ArrayList<NetworkedObject> group) {
    	Iterator<NetworkedObject> i = group.iterator();
    	while(i.hasNext()) {
    		// Grab the game object
    		NetworkedObject gameObject = i.next();
    		
    		// Update this game object
    		gameObject.render(activeCamera);
    	}
    }
    
    /**
     * Render the entire screen, so it reflects the current game state.
     * @param view_width The width of the view to render
     * @param view_height The height of the view to render
     * @throws SlickException
     */
    public synchronized void render(int view_width, int view_height)
    throws SlickException {
    	// Store vars
    	this.view_width = view_width;
    	this.view_height = view_height;
    	
    	// Grab camera position, horizontally center over player
    	float cameraX = activeCamera.getCamX();
    	float cameraY = activeCamera.getCamY();
    	
    	// Calculate which tile to draw
    	tileNumberX = (int)cameraX / TILE_WIDTH;
    	tileNumberY = (int)cameraY / TILE_HEIGHT;
    	
    	// Calculate the offset to draw the map at
    	worldOffsetX = tileNumberX * TILE_WIDTH - (int)cameraX;
    	worldOffsetY = tileNumberY * TILE_HEIGHT - (int)cameraY;
    	
    	int tile_total_x = (int) Math.ceil((float)view_width / (float)TILE_WIDTH) + 1;
    	int tile_total_y = (int) Math.ceil((float)view_height / (float)TILE_HEIGHT) + 1;
    	
    	// Render map
    	map.render(worldOffsetX, worldOffsetY, tileNumberX, tileNumberY, tile_total_x, tile_total_y);
    	
    	// Render all particle systems
    	Iterator<Emitter> e = emitters.iterator();
    	while(e.hasNext()) {
    		// Grab the game object
    		Emitter emitter = e.next();
    		
    		// Check if the emitter is still valid
    		if(emitter.isValid()) {
    			// Update it
    			emitter.render(activeCamera);
    		} else {
    			// Nope, remove it
    			e.remove();
    		}
    	}
    	
    	// Render all the game objects
    	renderGroup(bullets);
    	renderGroup(networkedEnts);
    }
    
    /**
     * Gets the y position of the screen
     * @return The y position of the screen
     */
    public static float getScreenY() {
    	return World.screenY;
    }
    
    /**
     * Sets the y position of the screen
     * @param screenY The y position of the screen
     */
    public static void setScreenY(float screenY) {
    	World.screenY = screenY;
    }
    
    /**
     * Updates the y position of the screen, based on how long since the last update
     * @param delta How long since the last update
     */
    public static void updateScreenY(int delta) {
    	// Move the screen up
    	World.screenY -= screenMoveSpeed * delta;
    	
    	// Stop the screen from moving too far up
    	if(World.screenY < Game.playheight()/2) {
    		World.screenY = Game.playheight()/2;
    	}
    }
    
    /**
     * Creates a new player
     * @return A new player
     */
    public synchronized NS_Player createPlayer() {
    	float xSpawn = World.PLAYER_START_X;
    	float ySpawn = World.PLAYER_START_Y;
    	
    	for(NetworkedObject ent: networkedEnts) {
    		if(ent.getType() == "player") {
    			// Spawn ontop of this player
    			xSpawn = ent.getPosX();
    			ySpawn = ent.getPosY();
    		}
    	}
    	
    	// Create a player
    	NS_Player player = new NS_Player();
    	
    	this.storeShip(player, xSpawn, ySpawn, -1);
    	
    	// Give them their player
    	return player;
    }
    
    /**
     * Creates a new bullet
     * @param x The x position to spawn at
     * @param y The y position to spawn at
     * @param image The image to use
     * @param dir The direction to shoot
     * @param damage How much damage to deal
     */
    public synchronized void createBullet(float x, float y, String image, int dir, int damage) {
    	// Only server will shoot
    	if(!Game.isServer) return;
    	
		// Attempt to create a bullet
		NO_Bullet bullet = new NO_Bullet();
		
		// Set the direction of the bullet
		bullet.setDir(dir);
		
		// Set the damage on this bullest
		bullet.setDamage(damage);
		
		//Setup the bullet
		bullet.setup(x, y+BULLET_OFFSET*dir, image);
		
		// Store the bullet
		bullets.add(bullet);
    }
    
    /**
     * Creates a new unit
     * @param name The name of the unit to create
     * @param x The x position of the unit
     * @param y The y position of the unit
     */
    public synchronized void createUnit(String name, float x, float y) {
    	NetworkedObject unit;
    	
    	switch(name) {
    	case "Asteroid":
			unit = new NS_Asteroid();
		break;
		
    	case "Drone":
			unit = new NS_Drone();
		break;
		
    	case "Fighter":
			unit = new NS_Fighter();
		break;
		
    	case "Boss":
			unit = new NS_Boss();
		break;
		
    	case "Shield":
    		unit = new NI_Shield();
    	break;
    	
    	case "Repair":
    		unit = new NI_Repair();
    	break;
    	
    	case "Firepower":
    		unit = new NI_Firepower();
    	break;
    		
    		default:
    			System.out.println("Failed to find unit "+name);
    			return;
    	}
    	
    	// Network it
    	unit.setup(x, y, "");
    	
    	// Tell the game to update it
    	this.networkedEnts.add(unit);
    }
    
    /**
     * Finds and returns a random player
     * @return A random player if one exists
     */
    public synchronized NetworkedObject findRandomPlayer() {
    	// Find a random player
    	for(NetworkedObject ent: networkedEnts) {
    		if(ent.getType() == "player") {
    			return ent;
    		}
    	}
    	
    	// Failed to find one, return null
    	return null;
    }
    
    /**
     * Stores a ship
     * @param ship The ship to store
     * @param x The x position to start it
     * @param y The y position to start it
     * @param dir The direction it should move in
     */
    public synchronized void storeShip(NS_Base ship, float x, float y, int dir) {
    	// Make it float in the correct diration
    	ship.setDir(dir);
    	
    	// Network it
    	ship.setup(x, y, "");
    	
    	// Tell the game to update it
    	this.networkedEnts.add(ship);
    }
    
    /**
     * Checks if a position is solid, using image based collision, entire image is treated as a solid
     * @param x The x position to test at
     * @param y The y position to test at
     * @param width The Width of the sprite
     * @param height The height of the sprite
     * @return If a position is solid or not
     */
    public boolean solidAtPos(float x, float y, int width, int height) {
    	// Since (x, y) is the origin, change width to "radius"
    	width /= 2;
    	height /= 2;
    	
    	// Check the four corners
    	if(	solidAtPoint(x-width, y-height) ||
    		solidAtPoint(x+width, y-height) ||
    		solidAtPoint(x+width, y+height) ||
    		solidAtPoint(x-width, y+height)) {
    		// At least one intersects
    		return true;
    	} else {
    		// None intersect
    		return false;
    	}
    }
    
    /** Returns if there is a solid block at the given coordinate.
     * @param x The x-coordinate to check.
     * @param y The y-coordinate to check.
     * @return Weather or not there is a solid block at the given position.
     */
    public boolean solidAtPoint(float x, float y) {
    	try {
    		// Grab the tileID
	    	int tileID = map.getTileId((int)(x/TILE_WIDTH), (int)(y/TILE_HEIGHT), 0);
	    	
	    	// Grab weather this tile is solid, or not
	    	String res = map.getTileProperty(tileID, "block", "0");
	    	
	    	// Return true if it's solid
	    	return (res.equals("1"));
    	} catch(java.lang.IndexOutOfBoundsException e) {
    		// Make it solid
    		return false;
    	}
    }
    
    /**
     * Checks if a given entity can be seen on screen, only considers vertical position
     * @param ent The entity to check
     * @return If the ent is visible or not
     */
    public static boolean onScreen(VisibleGameObject ent) {
    	// Grab info on this entity
    	float posY = ent.getPosY();
    	float height = ent.getHeight();
    	
    	// Grab the distance between them
    	float dist = Math.abs(posY - screenY);
    	
    	// Check if the two overlap each other
    	return (dist < Game.playheight()/2 + height/2);
    }
    
    /**
     * Checks if a given ent should be drawn, considers x and y position
     * @param ent The ent to check
     * @return If the ent should be drawn
     */
    public static boolean onScreenVisible(VisibleGameObject ent) {
    	// Grab info on this entity
    	float posX = ent.getPosX();
    	float posY = ent.getPosY();
    	
    	float width = ent.getWidth();
    	float height = ent.getHeight();
    	
    	// Grab the distance between them
    	float xDist = Math.abs(posX - Game.getWorld().getActiveCamera().getPosX());
    	float yDist = Math.abs(posY - screenY);
    	
    	// Check if the two overlap each other
    	return (yDist < Game.playheight()/2 + height/2) && (xDist < Game.playwidth()/2 + width/2);
    }
    
    /** Get this world's active camera
     * @return This world's active Camera.
     * */
    public synchronized Camera getActiveCamera() {
    	return this.activeCamera;
    }
    
    /**
     * Changes the active camera
     * @param camera The camera that should be active
     */
    public synchronized void setActiveCamera(Camera camera) {
    	this.activeCamera = camera;
    }
    
    /**
     * Queues an image to be loaded
     * @param obj The object to load the image on
     */
    public synchronized void loadImage(VisibleGameObject obj) {
    	// Store this object as needing an image
    	needImages.add(obj);
    }
    
    /**
     * Stores a networked entity
     * @param obj The entity to store
     * @param entID The ID to store it under
     */
    public synchronized void addNetworkedObject(NetworkedObject obj, Short entID) {
    	// Valdiate entID
    	if(entID < 0 || entID >= World.MAX_ENTS) {
    		// Print Warning
    		System.out.println("Tried to store ent with invalid entID "+entID);
    		return;
    	}
    	
    	// Store the object
    	networkedEnts.add(obj);
    	
    	// Store into our entlist
    	entList[entID] = obj;
    	
    	// Store the ent ID into the object
    	obj.setEntID(entID);
    }
    
    /**
     * Sends every entity to the selected client
     * @param client The client to send to
     */
    public synchronized void sendAllEnts(ServerClient client) {
    	// Create buffer to write to
    	Buffer buff = new Buffer(32);
    	
    	// Cycle over every entity
    	for(int i=0; i<World.MAX_ENTS; i++) {
    		// Grab a networked object
    		NetworkedObject obj = this.entList[i]; 
    		
    		// Check if it exists
    		if(obj != null && obj.isValid()) {
    			// Write all the init data
    			obj.networkInitWrite(buff);
    			
    			// Send to client
    			client.sendMessage(buff);
    		}
    	}
    }
}
