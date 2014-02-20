package menu;

import core.Game;

/**
 * Finds a game
 * @author aschmid
 *
 */
public class MenuItemFindGame extends MenuItem {
	/** The ip to connect to */
	private String ip;
	
	/**
	 * Create a new menu item 
	 * @param controller the menu controller
	 * @param ip
	 */
	MenuItemFindGame(MenuController controller, String ip) {
		super(controller, ip);
		
		// Store ip
		this.ip = ip;
	}
	
	/**
	 * Called when this item is selected, connect to the ip
	 */
	public void select() {
		// Hide the menu
		this.con.hideMenu();
		
		// Set the ip
		Game.setHost(ip);
		
		// We are NOT the server
		Game.setServer(false);
		
		// Start the game
		Game.getGame().startGame();
	}
}
