package gameObjects;

import core.World;

/**
 * Fighter ai unit
 * @author aschmid
 *
 */
public class NS_Fighter extends NS_Base {
	/** The max shield this unit can have */
	protected int maxShield = 24;
	
	/** The amount of damage this unit deals */
	protected int damage = 9;
	
	/**
	 * Create a new fighter
	 */
	public NS_Fighter() {
		// Set stats
		this.setMaxShield(24);
		this.setShield(24);
		this.setDamage(9);
	}
	
	/**
	 * Gets the sprite needed for this unit
	 * @return The sprite needed for this unit
	 */
	protected String getSprite() {
		return "assets/units/fighter.png";
	}
	
	/**
	 * Updates this fighter
	 * @param delta time since last update
	 */
	public void update(int delta) {
		// Ensure we're onscreen
		if(!World.onScreen(this)) return;
		
		// Just do default movement
		this.defaultMovement(delta);
		
		// Fire!
		this.cooldownGun(delta);
		this.fire("assets/units/missile-enemy.png");
	}
}
