package gameObjects;

/**
 * A shield powerup
 * @author aschmid
 *
 */
public class NI_Shield extends NI_Base {
	/** The amount of bonus shield to award */
	private int bonusShield = 40;
	
	/**
	 * Gets the sprite needed for this unit
	 * @return The sprite needed for this unit
	 */
	protected String getSprite() {
		return "assets/items/shield.png";
	}
	
	/**
	 * Gives bonus shield to the ship that collects it
	 * @param ship the ship to give bonus shield to
	 */
	protected void collect(NS_Player ship) {
		// Give extra shield
		ship.setMaxShield(ship.getMaxShield() + bonusShield);
		ship.setShield(ship.getShield() + bonusShield);
	}
}
