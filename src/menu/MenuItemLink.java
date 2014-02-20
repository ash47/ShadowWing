package menu;

/**
 * A link between two menu screens
 * @author Ash
 *
 */
public class MenuItemLink extends MenuItem {
	/** The menu screen to link to */
	MenuScreen link;
	
	/**
	 * Create a new link
	 * @param controller The controller this item is linked to
	 * @param name THe text to display
	 * @param link The item to link to
	 */
	MenuItemLink(MenuController controller, String name, MenuScreen link) {
		super(controller, name);
		
		this.link = link;
	}
	
	/**
	 * When this item is selected, change to another screen
	 */
	public void select() {
		this.con.select(this.link);
	}
}
