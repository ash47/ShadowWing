package gameObjects;

/**
 * A repair item
 * @author aschmid
 *
 */
public class NI_Repair extends NI_Base {
	/**
	 * Gets the sprite needed for this unit
	 * @return The sprite needed for this unit
	 */
	protected String getSprite() {
		return "assets/items/repair.png";
	}
	
	/**
	 * Repairs the given ship
	 * @param The ship to repair
	 */
	protected void collect(NS_Player ship) {
		// Fill up their shield
		ship.setShield(ship.getMaxShield());
	}
}
