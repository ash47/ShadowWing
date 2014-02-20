package gameObjects;

import core.Game;

/**
 * Networked Item base
 * @author aschmid
 *
 */
public class NI_Base extends NetworkedObject {
	/**
	 * Gets the sprite needed for this unit
	 * @return The sprite needed for this unit
	 */
	protected String getSprite() {
		return "assets/units/asteroid.png";
	}
	
	/**
	 * Create a new Networked Item base
	 */
	NI_Base() {
		super();
		
		// Store the image name
		this.setImageName(getSprite());
		
		// Only do type stuff if server
		if(Game.isServer) {
			// Change the type
			this.setType("powerup");
		}
	}
	
	/**
	 * Runs when a ship collects this item
	 * @param ship the ship that collected the item
	 */
	protected void collect(NS_Player ship) {
		// Base item does nothing
	}
}
