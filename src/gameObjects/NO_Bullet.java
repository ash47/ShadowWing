package gameObjects;

import core.Game;
import core.World;
import network.Buffer;

/**
 * A networked bullet
 * @author aschmid
 *
 */
public class NO_Bullet extends NetworkedObject {
	/** The speed the missile moves */
	private float speed = 0.7f;
	
	/** The direction the missile moves */
	private int dir = -1;
	
	/** The amount of damage this bullet deals */
	private int damage;
	
	/**
	 * create a new networked bullet
	 */
	public NO_Bullet() {
		super();
		
		// Only do type stuff if server
		if(Game.isServer) {
			// Change the type
			this.setType("bullet");
		}
	}
	
	/**
	 * Sets the direction of the bullet
	 * @param dir Direction, -1 being up, 1 being down
	 */
	public void setDir(int dir) {
		this.dir = dir;
	}
	
	/**
	 * Updates the given bullet
	 * @param delta the time since the last update
	 */
	public void update(int delta) {
		// Check if we're onscreen
		if(!World.onScreen(this)) {
			// Nope, cleanup time
			this.cleanup();
			return;
		}
		
		// Move in the correct diration
		this.posY += delta * speed * dir;
		
		// Check if we hit a wall
		if(this.solidAtPos(this.posX, this.posY)) {
			// Cleanup
			this.cleanup();
		}
	}
	
	/**
	 * The amount of damage this bullet will deal
	 * @param damage
	 */
	public void setDamage(int damage) {
		this.damage = damage;
	}
	
	/**
	 * gets the amount of damage this bullet will deal
	 * @return The amount of damage this bullet will deal
	 */
	public int getDamage() {
		return this.damage;
	}
	
	// NETWORKING STUFF
	
	/**
	 * Write the direction of this bullet
	 */
	public void networkInitWriteOtherData(Buffer buff) {
		// Send the direction
		buff.writeInt(this.dir);
	}
	
	/**
	 * Read the direction of this bullet
	 */
	public void networkInitOtherData(Buffer buff) {
		// Read the direction
		this.setDir(buff.readInt());
	}
}
