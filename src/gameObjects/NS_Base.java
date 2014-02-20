package gameObjects;

import core.Game;
import core.World;
import network.Buffer;

/**
 * The base for all networked ships
 * @author aschmid
 *
 */
public class NS_Base extends NetworkedObject {
	/** The direction this ship is moving */
	protected int dir = 1;
	
	/** The max shield this unit can have */
	private int maxShield = 100;
	
	/** The amount of shield this unit has */
	private int shield;
	
	/** This determines if a player can shoot, <=0 means it can shoot */
	private int gunCooldown = 0;
	
	/** This affects how fast a ship can shoot */
	private int firePower = 0;
	
	/** The speed units float at */
	protected float moveSpeed = 0.2f;
	
	/** The amount of damage this unit deals */
	private int damage = 1;
	
	/**
	 * Gets the sprite needed for this unit
	 * @return The sprite needed for this unit
	 */
	protected String getSprite() {
		return "assets/units/asteroid.png";
	}
	
	/**
	 * Create a new networked ship base
	 */
	public NS_Base() {
		super();
		
		// Store the image name
		this.setImageName(getSprite());
		
		// Give them full shield
		this.shield = this.maxShield;
		
		// Only do type stuff if server
		if(Game.isServer) {
			// Change the type
			this.setType("enemy");
			
			// Make it collide with bullets
			this.addCollision("bullet");
			
			// Make it collide with enemies
			this.addCollision("enemy");
		}
	}
	
	/**
	 * Another networked object has collided with us
	 * @param ent2 the entity that collided with us
	 */
	public void collide(NetworkedObject ent2) {
		// Grab the type of the other ent
		String cls = ent2.getType();
		
		
		if(cls == "bullet") {
			// Take damage
			this.takeDamage(((NO_Bullet)ent2).getDamage());
			
			// Kill the bullet
			ent2.cleanup();
		}
		
		if(cls == "enemy") {
			// We know it's a ship
			NS_Base enemy = (NS_Base)ent2;
			
			// While one is still alive, keep applying damage
			while(this.getShield() > 0 && enemy.getShield() > 0) {
				this.takeDamage(enemy.getDamage());
				enemy.takeDamage(this.getDamage());
			}
		}
	}
	
	/**
	 * Cools down our gun
	 * @param delta the time since the last cooldown check / update
	 */
	public void cooldownGun(int delta) {
		// Cooldown gun
		this.gunCooldown -= delta;
		
		// Stop it from going below 0
		if(this.gunCooldown <= 0) {
			this.gunCooldown = 0;
		}
	}
	
	/**
	 * Fires our gun if it can fire
	 * @param spr The sprite to fire
	 */
	public void fire(String spr) {
		// Check if we can FIRE
		if(this.gunCooldown <= 0) {
			// Create a bullet
			Game.getWorld().createBullet(this.posX, this.posY, spr, this.dir, this.damage);
			
			// Apply the cooldown
			gunCooldown = 300 - (80 * this.firePower);
		}
	}
	
	/**
	 * Gets the amount of damage we deal
	 * @return the amount of damage we deal
	 */
	public int getDamage() {
		return this.damage;
	}
	
	/**
	 * Gets the amount of shield we have
	 * @return the amount of shield we have
	 */
	public int getShield() {
		return this.shield;
	}
	
	/**
	 * Takes damage
	 * @param amount amount of damage to take
	 */
	public void takeDamage(int amount) {
		// Take the damage
		this.shield -= amount;
		
		// Check if we ran out of shield
		if(this.shield <= 0) {
			this.shield = 0;
			this.die();
		}
	}
	
	/**
	 * Cleans up this ship
	 */
	public void cleanup() {
		// Explode
		this.explode();
		
		// Run the normal cleanup
		super.cleanup();
	}
	
	/**
	 * Kills this ship
	 */
	public void die() {
		// Make sure we are valid
		if(!this.isValid()) return;
		
		// If we're the server
		if(Game.isServer) {
			// Clean this object up
			this.cleanup();
		}
	}
	
	/**
	 * Default flying forward movement
	 * @param delta time since last update
	 */
	public void defaultMovement(int delta) {
		// Calculate a new position
		float newY = this.posY + moveSpeed * delta * dir;
		
		// Check if the position is free
		if(!this.solidAtPosPrecise(this.posX, newY)) {
			// Move there
			this.setPos(this.posX, newY);
			
			// Make sure we're synced
			checkSync();
		} else {
			// We crashed, die
			this.die();
		}
	}
	
	/**
	 * Changes the direction we are flying
	 * @param dir direction to fly
	 */
	public void setDir(int dir) {
		// Store our dir
		this.dir = dir;
	}
	
	/**
	 * Sets the damage we deal
	 * @param damage amount of damage we deal
	 */
	public void setDamage(int damage) {
		this.damage = damage;
	}
	
	/**
	 * Gets the max shield we can have
	 * @return max shield we can have
	 */
	public int getMaxShield() {
		return this.maxShield;
	}
	
	/**
	 * Sets teh max shield we can have
	 * @param maxShield the max shield we can have
	 */
	public void setMaxShield(int maxShield) {
		// Store max shield
		this.maxShield = maxShield;
	}
	
	/**
	 * sets teh actual shield we have
	 * @param shield The amount of shield we have
	 */
	public void setShield(int shield) {
		// Store our shield
		this.shield = shield;
	}
	
	/**
	 * Gets the amount of firepower we have
	 * @return teh amount of firepower we have
	 */
	public int getFirePower() {
		return this.firePower;
	}
	
	/**
	 * Sets the amount of firepower we have
	 * @param firePower The amount of firepower we have
	 */
	public void setFirePower(int firePower) {
		// Store the firePower
		this.firePower = firePower;
		
		// Limit firepower
		if(this.firePower > 3) {
			this.firePower = 3;
		}
	}
	
	/**
	 * Adds to our firepower
	 * @param amount amount of firepower to add
	 */
	public void addFirePower(int amount) {
		// Add to our firepower
		this.firePower += amount;
		
		// Limit firepower
		if(this.firePower > 3) {
			this.firePower = 3;
		}
	}
	
	/**
	 * Creates a cool explosion
	 */
	public void explode() {
		// Check if we're onscreen
		if(World.onScreenVisible(this)){
			// Create an emitter
			Game.getWorld().createEmitter("explosion", this.getPosX(), this.getPosY());
		}
	}
	
	// NETWORKING STUFF
	
	/**
	 * WRITE: When the object is first created 
	 * @param buff buffer to write to
	 */
	public void networkInitWriteOtherData(Buffer buff) {
		// Send data
		buff.writeInt(this.dir);
		buff.writeInt(this.maxShield);
		buff.writeInt(this.shield);
		buff.writeInt(this.firePower);
	}
	
	/**
	 * READ: When the object is first created
	 * @param buff buffer to read from
	 */
	public void networkInitOtherData(Buffer buff) {
		// Read data
		this.setDir(buff.readInt());
		this.setMaxShield(buff.readInt());
		this.setShield(buff.readInt());
		this.setFirePower(buff.readInt());
	}
	
	/**
	 * WRITE: Generic sync, write data that changes often
	 * @param buff Buffer to write to
	 */
	public void networkSyncOtherData(Buffer buff) {
		
	}
	
	/**
	 * READ: Generic sync, read data that changes often
	 * @param buff buffer to read from
	 */
	public void networkUpdateOtherData(Buffer buff) {
		
	}
}
