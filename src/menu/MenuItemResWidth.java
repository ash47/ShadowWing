package menu;

import java.awt.Toolkit;

import core.Settings;

/**
 * A menu item for changing the screen's width
 * @author aschmid
 *
 */
public class MenuItemResWidth extends MenuItem {
	/** The current res setting */
	int res;
	
	/** The last time we got an update */
	long lastChange;
	
	/** Stores if we should do fast movement or not */
	int fastMovement;
	
	/**
	 * Creates a new res width menu item
	 * @param controller The contoller to attach this to
	 */
	MenuItemResWidth(MenuController controller) {
		super(controller, "Res Width");
		
		// Grab the current width
		this.res = Settings.getInt("screenWidth", 800);
		
		// Change  the name
		changeName();
		
		this.lastChange = System.currentTimeMillis();
		this.fastMovement = 0;
	}
	
	/**
	 * Changes the name of this item
	 */
	private void changeName() {
		this.name = "Game Width: "+this.res;
	}
	
	/**
	 * Works out if the res number should change or not
	 * @return If the res number should change
	 */
	private boolean shouldChange() {
		if(System.currentTimeMillis() - this.lastChange > 50) {
			this.fastMovement = 0;
			this.lastChange = System.currentTimeMillis();
			
			return true;
		} else {
			this.fastMovement += (System.currentTimeMillis() - this.lastChange);
			this.lastChange = System.currentTimeMillis();
		}
		
		return (this.fastMovement > 500);
	}
	
	/**
	 * Lowers the resolution
	 */
	public void left() {
		if(shouldChange()) {
			this.res -= 1;
			
			// Stop res from getting too low
			if(this.res < 800) {
				this.res = 800;
			}
		}
		
		// Change  the name
		changeName();
	}
	
	/**
	 * Increases the resolution
	 */
	public void right() {
		if(shouldChange()) {
			this.res += 1;
			
			// Grab the max size it can be
			int maxWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
			
			// Limit max
			if(this.res > maxWidth) {
				this.res = maxWidth;
			}
		}
		
		// Change  the name
		changeName();
	}
	
	/**
	 * Selects the given resolution
	 */
	public void select() {
		// Lock the setting in
		Settings.setInt("screenWidth", this.res);
	}
}
