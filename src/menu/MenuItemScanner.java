package menu;

import java.util.Scanner;

import core.Game;

/*
 * Reads input from a scanner >_>
 */
public class MenuItemScanner extends MenuItem {
	
	/**
	 * Create a new menu item 
	 * @param controller the menu controller
	 */
	MenuItemScanner(MenuController controller, String name) {
		super(controller, name);
	}
	
	/**
	 * Called when this item is selected, connect to the ip
	 */
	public void select() {
		// Hide the menu
		this.con.hideMenu();
		
		// Create scanner
		Scanner sc = new Scanner(System.in);
		
		// Set the ip
		Game.setHost(sc.nextLine());
		
		// Close scanner
		sc.close();
		
		// We are NOT the server
		Game.setServer(false);
		
		// Start the game
		Game.getGame().startGame();
	}
}
