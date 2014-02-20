package menu;

import core.Game;

/**
 * Hosts a game
 * @author aschmid
 *
 */
public class MenuItemHost extends MenuItem {
	/**
	 * Create a new host item
	 * @param controller the controller this is attached to
	 * @param name the name to display
	 */
	MenuItemHost(MenuController controller, String name) {
		super(controller, name);
	}
	
	/**
	 * called when this item is selected, hosts a game
	 */
	public void select() {
		// Hide the menu
		this.con.hideMenu();
		
		// We are the server
		Game.setServer(true);
		
		// Start the game
		Game.getGame().startGame();
	}
}
