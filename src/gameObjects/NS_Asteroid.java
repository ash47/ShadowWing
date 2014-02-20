package gameObjects;

import core.World;

/**
 * A networked astroid
 * @author aschmid
 *
 */
public class NS_Asteroid extends NS_Base {
	/**
	 * Create a new networked astroid
	 */
	public NS_Asteroid() {
		super();
		
		// Set stats
		this.setMaxShield(24);
		this.setShield(24);
		this.setDamage(12);
	}
	
	/**
	 * Gets the sprite needed for this unit
	 * @return The sprite needed for this unit
	 */
	protected String getSprite() {
		return "assets/units/asteroid.png";
	}
	
	/**
	 * Updates this astroid
	 * @param delta the time since the last update
	 */
	public void update(int delta) {
		// Ensure we're onscreen
		if(!World.onScreen(this)) return;
		
		// Just do default movement
		this.defaultMovement(delta);
	}
}
