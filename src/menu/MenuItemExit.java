package menu;

/**
 * Exit menu item
 * @author aschmid
 *
 */
public class MenuItemExit extends MenuItem {
	/**
	 * Create a new menu exit item
	 * @param controller the controller this menu item is attached to
	 * @param name The name to display
	 */
	MenuItemExit(MenuController controller, String name) {
		super(controller, name);
	}
	
	/**
	 * Called when this item is selected, exit the game
	 */
	public void select() {
		// Exit the game
		System.exit(0);
	}
}
