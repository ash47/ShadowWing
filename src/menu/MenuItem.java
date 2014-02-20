package menu;

/**
 * An item in a menu
 * @author aschmid
 *
 */
public class MenuItem {
	/** The text to draw */
	protected String name;
	
	/** The controller this item is attached to */
	protected MenuController con;
	
	/**
	 * Create a new menu item
	 * @param controller the controller this item is attached to
	 * @param name the text to render
	 */
	public MenuItem(MenuController controller, String name) {
		// Store vars
		this.name = name;
		this.con = controller;
	}
	
	/**
	 * Updates this menu item
	 */
	public void update() {
		
	}
	
	/**
	 * Runs when this menu item is selected
	 */
	public void select() {
		
	}
	
	/**
	 * runs when this item is selected, and the left arrow is down
	 */
	public void left() {
		
	}
	
	/**
	 * runs when this item is selected, and the right arrow is down
	 */
	public void right() {
		
	}
	
	/**
	 * Renders this menu item
	 * @param x x position to render
	 * @param y y position to render
	 */
	public void render(float x, float y) {
		con.drawText(x, y, name);
	}
}
