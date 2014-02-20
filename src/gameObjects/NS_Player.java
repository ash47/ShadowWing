package gameObjects;

import Particles.Emitter;
import core.Game;
import core.Panel;
import core.World;
import network.Buffer;

/**
 * The player unit
 * @author aschmid
 *
 */
public class NS_Player extends NS_Base {
	/** Speed + direction to move vertically automatically (per millisecond). */
	private float vertSpeed = -0.25f;
	
	/** The speed in which the player can move the ship (per millisecond). */
	private float moveSpeed = 0.4f;
	
	/** Controls players x-movement */
	byte dir_x = 0;
	
	/** Controls players y-movement */
	byte dir_y = 0;
	
	/** Contains the previously known dir_x */
	int prev_dir_x = -1;
	
	/** Contains the previously known dir_y */
	int prev_dir_y = -1;
	
	/** Is this player shooting? */
	boolean isShooting = false;
	
	/** Contains a particle emmiter for it's boosters */
	private Emitter boosters;
	
	/**
	 * Gets the sprite needed for this unit
	 * @return The sprite needed for this unit
	 */
	protected String getSprite() {
		return "assets/units/player.png";
	}
	
	/**
	 * Create a new player
	 */
	public NS_Player() {
		super();
		
		// Set stats
		this.setMaxShield(100);
		this.setShield(100);
		this.setDamage(10);
		
		// Only do type stuff if server
		if(Game.isServer) {
			// Make it collide with stuff
			this.addCollision("enemy");
			this.addCollision("powerup");
			
			//System.out.println(this.collidesWith("enemy"));
			
			// Change the type
			this.setType("player");
		}
		
		// Create a partile emmiter for it's boosters
		boosters = new Emitter("boost", this.getPosX(), this.getPosY(), true);
	}
	
	/**
	 * The player collided with another ent
	 * @param ent2 the ent we collided with
	 */
	public void collide(NetworkedObject ent2) {
		// Grab the type of the colliding ent
		String cls = ent2.getType();
		
		// Check if it was a powerup
		if(cls == "powerup") {
			// Trigger the collect event
			((NI_Base)ent2).collect(this);
			
			// Remove the powerup
			ent2.cleanup();
		}
		// Check if it was an enemy
		if(cls == "enemy") {
			// We know it's a ship
			NS_Base enemy = (NS_Base)ent2;
			
			// While one is still alive, keep applying damage
			while(this.getShield() > 0 && enemy.getShield() > 0) {
				this.takeDamage(enemy.getDamage());
				enemy.takeDamage(this.getDamage());
			}
		}
		// Check if it's a bullet
		if(cls == "bullet") {
			// We know it's a bullet
			NO_Bullet bullet = (NO_Bullet)ent2;
			
			// Take damage
			this.takeDamage(bullet.getDamage());
			
			// Remove the bullet
			bullet.cleanup();
		}
		
		// Sync new data to other clients
		this.networkSync();
	}	
	
	/**
	 * Cleans the player up
	 */
	public void cleanup() {
		// If we're the server
		if(Game.isServer && this.isValid()) {
			// Run the normal cleanup
			super.cleanup();
			
			// Check if there are any players left
			if(!Game.getWorld().playersAlive()) {
				// Load the last checkpoint
				Game.getWorld().loadCheckpoint();
			}
		} else {
			super.cleanup();
		}
	}
	
	/**
	 * Sync shield to clients
	 * @param shield amount of shield
	 */
	public void setShield(int shield) {
		super.setShield(shield);
		super.syncVars();
	}
	
	/**
	 * Sync max shield to clients
	 * @param maxShield amount of shield
	 */
	public void setMaxShield(int maxShield) {
		super.setMaxShield(maxShield);
		super.syncVars();
	}
	
	/**
	 * Sync firepower to clients
	 * @param firePower amount of firepower
	 */
	public void setFirePower(int firePower) {
		super.setFirePower(firePower);
		super.syncVars();
	}
	
	/**
	 * Updates the player
	 * @param delta since last update
	 */
	public void update(int delta) {
		if(this.getShield() <= 0) return;
		
		// Work out where the ship should move to
		float newX = this.posX + (float)(this.moveSpeed * dir_x * delta);
		
		// Check if this position is solid
		if(this.solidAtPosPrecise(newX, this.posY)) {
			// Reset new x position to the old one
			newX = this.posX;
		}
		
		// Check if the screen has reached the top
		if(World.getScreenY() <= Game.playheight()/2) {
			this.vertSpeed = 0;
		}
		
		/* (1) I decided to do this in two steps as I was having jumping
		 * when I did it in one step.
		 * When a player would move into a wall with the up arrow
		 * then let it go, the player would jump forward by ~0.65 pixels */
		
		// Calculate the new y-coordinate after scrolling + movement
		float newY = this.posY + (float)((this.vertSpeed + this.moveSpeed * dir_y) * delta);
		
		
		float maxY = World.getScreenY() + Game.playheight()/2 - Panel.PANEL_HEIGHT - this.getHeight()/2;
		float minY = World.getScreenY() - Game.playheight()/2 + this.getHeight()/2;
		
		// They are trying to fly off the screen
		if(newY > maxY) {
			newY = maxY;
			
			if(Game.isServer) {
				// Check if the position is solid
				if(this.solidAtPosPrecise(this.posX, newY)) {
					// Die
					this.setShield(0);
					this.cleanup();
					return;
				}
			}
		}
		
		// Stop from moving off the top of the screen
		if(newY < minY) {
			newY = minY;
		}
		
		// Check if this position is solid
		//if(world.solidAtPos(this.posX, newY, this.getWidth(), this.getHeight())) {
		if(this.solidAtPosPrecise(this.posX, newY)) {
			/* Check which direction they are trying to move, if they
			 * are moving forward, we need to consider the above case (1),
			 * otherwise we can just reset the position */
			if(dir_y == -1) {
				// Calculate the new y-coordinate with only scrolling
				newY = this.posY + (float)(this.vertSpeed * delta);
				
				// Check if the position is still solid
				if(this.solidAtPosPrecise(this.posX, newY)) {
					// Reset new y position to the old one
					newY = this.posY;
				}
			} else {
				// They are trying to move backward, just don't let them
				newY = this.posY;
			}
			
		}
		
		// Check if our directions have changed
		if(dir_x != prev_dir_x || dir_y != prev_dir_y) {
			// Sync the new data to our clients
			this.networkSync();
			
			// Store the prev values
			prev_dir_x = dir_x;
			prev_dir_y = dir_y;
		}
		
		// Update position
		this.posX = newX;
		this.posY = newY;
		
		// Cooldown our fun
		this.cooldownGun(delta);
		
		// If we're holding down the shoot button
		if(this.isShooting) {
			this.fire("assets/units/missile-player.png");
		}
		
		// Update our boosters
		this.boosters.update(delta);
	}
	
	/**
	 * Updates our directional movements
	 * @param dir_x x movement
	 * @param dir_y y movement
	 */
	public void updateDirs(byte dir_x, byte dir_y) {
		// Store input
		this.dir_x = dir_x;
		this.dir_y = dir_y;
	}
	
	/**
	 * Updates wether we are shooting or not
	 * @param shooting Are we shooting?
	 */
	public void setIsShooting(boolean shooting) {
		this.isShooting = shooting;
	}
	
	// NETWORKING STUFF
	
	/**
	 * WRITE: When the object is first created
	 * @param buff buffer
	 */
	public void networkInitWriteOtherData(Buffer buff) {
		// Send data
		buff.writeInt(this.dir);
		buff.writeInt(this.getMaxShield());
		buff.writeInt(this.getShield());
		buff.writeInt(this.getFirePower());
		buff.writeByte(this.dir_x);
		buff.writeByte(this.dir_y);
	}
	
	/**
	 * READ: When the object is first created
	 * @param buff buffer to write to
	 */
	public void networkInitOtherData(Buffer buff) {
		// Read data
		this.setDir(buff.readInt());
		this.setMaxShield(buff.readInt());
		this.setShield(buff.readInt());
		this.setFirePower(buff.readInt());
		this.updateDirs(buff.readByte(), buff.readByte());
	}
	
	/**
	 * WRITE: Generic sync, write data that changes often
	 * @param buff buffer to write to
	 */
	public void networkSyncOtherData(Buffer buff) {
		buff.writeByte(this.dir_x);
		buff.writeByte(this.dir_y);
	}
	
	/**
	 * WRITE: Uncommon vars
	 * @param buff buffer to write to
	 */
	public void syncVarsOther(Buffer buff) {
		buff.writeInt(this.getMaxShield());
		buff.writeInt(this.getShield());
		buff.writeInt(this.getFirePower());
	}
	
	/**
	 * READ: Read uncommon synced vars
	 * @param buff buffer to read from
	 */
	public void networkData(Buffer buff) {
		this.setMaxShield(buff.readInt());
		this.setShield(buff.readInt());
		this.setFirePower(buff.readInt());
	}
	
	/**
	 * READ: Generic sync, read data that changes often
	 * @param buff to read from
	 */
	public void networkUpdateOtherData(Buffer buff) {
		// Update dirs
		this.updateDirs(buff.readByte(), buff.readByte());
	}
	
	/**
	 * Renders this player based on the given view
	 * @param cam Camera to render based on
	 */
	public void render(Camera cam) {
		if(this.getShield() <= 0) return;
		
		// Render boosters
		this.boosters.render(cam, this.getPosX()-9, this.getPosY()+20);
		this.boosters.render(cam, this.getPosX()+9, this.getPosY()+20);
		
		// Call the normal render method
		super.render(cam);
	}
}
