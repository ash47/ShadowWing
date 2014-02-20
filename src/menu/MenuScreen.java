package menu;

import java.util.ArrayList;

import org.newdawn.slick.Input;

import core.Settings;

/**
 * A screen for the menu
 * @author aschmid
 *
 */
public class MenuScreen {
	/** The list of items in the menu */
	ArrayList<MenuItem> items;
	
	/** The controller of this screen */
	MenuController con;
	
	/** The number of the currently selected item */
	int selected;
	
	/**
	 * Creates a new menu screen
	 * @param controller The controller that owns this menu
	 */
	public MenuScreen(MenuController controller) {
		// Store vars
		this.con = controller;
		
		// Create items array
		items = new ArrayList<MenuItem>();
		
		// Default to the top menu item
		this.selected = 0;
	}
	
	/**
	 * Updates this menu screen
	 * @param input The input from slick
	 * @param delta The time since the last update
	 */
	public void update(Input input, int delta) {
		// Move down
		if(input.isKeyPressed(Settings.menu_down)) {
			this.selected += 1;
			
			if(this.selected >= this.items.size()) {
				this.selected = 0;
			}
		}
		
		// Move up
		if(input.isKeyPressed(Settings.menu_up)) {
			this.selected -= 1;
			
			if(this.selected < 0) {
				this.selected += this.items.size();
			}
		}
		
		// Left
		if(input.isKeyDown(Settings.menu_left)) {
			// Grab the selected item
			MenuItem item = items.get(this.selected);
			
			// Select it
			item.left();
		}
		
		// Right
		if(input.isKeyDown(Settings.menu_right)) {
			// Grab the selected item
			MenuItem item = items.get(this.selected);
			
			// Select it
			item.right();
		}
		
		// Select
		if(input.isKeyPressed(Settings.menu_select)) {
			// Grab the selected item
			MenuItem item = items.get(this.selected);
			
			// Select it
			item.select();
		}
	}
	
	/**
	 * Renders the menu
	 */
	public void render() {
		float x = 120;
		float y = 120;
		
		float xx = x;
		
		for(int i=0; i<items.size(); i++) {
			// Grab menu item
			MenuItem item = items.get(i);
			
			if(this.selected == i) {
				xx = x + 20;
			} else {
				xx = x;
			}
			
			// Render the item
			item.render(xx, y);
			
			y += 30;
		}
	}
	
	/**
	 * Adds an item to this menu
	 * @param item The item to add
	 */
	public void addMenuItem(MenuItem item) {
		items.add(item);
	}
}
