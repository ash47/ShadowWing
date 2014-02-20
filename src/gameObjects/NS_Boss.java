package gameObjects;

import core.World;

/**
 * Boss for the game
 * @author aschmid
 *
 */
public class NS_Boss extends NS_Base {
	/** Stores the drection the boss is moving */
	private boolean left = false;
	
	/** The farthest left this boss can get */
	private float leftMost = 1013;
	
	/** The farthest right this boss can get */
	private float rightMost = 1589;
	
	/** Create a new boss */
	public NS_Boss() {
		super();
		
		// Set stats
		this.setDamage(100);
		this.setMaxShield(240);
		this.setShield(240);
		this.setFirePower(3);
	}
	
	/**
	 * Gets the sprite needed for this unit
	 * @return The sprite needed for this unit
	 */
	protected String getSprite() {
		return "assets/units/boss.png";
	}
	
	/**
	 * Updates the boss
	 * @param delta amount of time since last update
	 */
	public void update(int delta) {
		// Ensure we're onscreen
		if(!World.onScreen(this)) return;
		
		if(left) {
			// Move left
			this.posX -= delta * this.moveSpeed;
			
			// Check if we've moved too far left
			if(this.posX <= leftMost) {
				this.posX = leftMost;
				left = false;
			}
		} else {
			// Move right
			this.posX += delta * this.moveSpeed;
			
			// Check if we've moved too far right
			if(this.posX >= rightMost) {
				this.posX = rightMost;
				left = true;
			}
		}
		
		// Make sure we're synced
		checkSync();
		
		// Fire!
		this.cooldownGun(delta);
		this.fire("assets/units/missile-enemy.png");
	}
}
