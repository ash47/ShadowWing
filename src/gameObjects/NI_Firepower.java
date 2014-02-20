package gameObjects;

/**
 * A firepower item
 * @author aschmid
 *
 */
public class NI_Firepower extends NI_Base {
	/**
	 * Gets the sprite needed for this unit
	 * @return The sprite needed for this unit
	 */
	protected String getSprite() {
		return "assets/items/firepower.png";
	}
	
	/**
	 * Add firepower the the given ship
	 * @param ship The ship to give firepower to
	 */
	protected void collect(NS_Player ship) {
		// Add firepower
		ship.addFirePower(1);
	}
}
