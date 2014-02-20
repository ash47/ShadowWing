package gameObjects;

import network.Buffer;
import core.Game;
import core.World;

public class NS_Drone extends NS_Base {
	/** The entity we need to move towards */
	NetworkedObject entToAttack;
	
	/**
	 * Create a new drone
	 */
	public NS_Drone() {
		this.setMaxShield(16);
		this.setShield(16);
		this.setDamage(8);
	}
	
	/**
	 * Gets the sprite needed for this unit
	 * @return The sprite needed for this unit
	 */
	protected String getSprite() {
		return "assets/units/drone.png";
	}
	
	/**
	 * Update the drone
	 * @param delta time since last update
	 */
	public void update(int delta) {
		// Ensure we're onscreen
		if(!World.onScreen(this)) return;
		
		if(entToAttack == null) {
			// If we're the server, find an ent
			if(Game.isServer) {
				// Find an ent to attack
				entToAttack = Game.getWorld().findRandomPlayer();
				
				// If we found an ent
				if(entToAttack != null) {
					// Tell clients
					this.syncVars();
				}
			}
			return;
		}
		
		// Grab the distance between us and the enemy
		float xDist = entToAttack.getPosX() - this.getPosX();
		float yDist = entToAttack.getPosY() - this.getPosY();
		
		// Workout total distance
		float totalDist = (float) Math.sqrt(xDist*xDist + yDist*yDist);
		
		// Move the drone
		this.posX += xDist/totalDist * this.moveSpeed * delta;
		this.posY += yDist/totalDist * this.moveSpeed * delta;
		
		// Check if we crashed into a wall
		if(this.solidAtPosPrecise(this.posX, this.posY)) {
			this.die();
		} else {
			// Make sure we're synced
			checkSync();
		}
	}
	
	/**
	 * Sync other vars
	 * @param buff buffer to write to
	 */
	public void syncVarsOther(Buffer buff) {
		// Write entID
		if(this.entToAttack == null) {
			buff.writeShort((short) -1);
		} else {
			buff.writeShort(this.entToAttack.getEntID());
		}
	}
	
	/**
	 * Read other data
	 * @param buff buffer to read from
	 */
	public void networkData(Buffer buff) {
		// Read the ID of the ent
		short eID = buff.readShort();
		
		// Read the entity we are meant to be attacking
		entToAttack = Game.getWorld().getEntByID(eID);
	}
}
