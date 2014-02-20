package menu;

import java.awt.Font;
import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

import core.Settings;

/**
 * Contains / controls a menu
 * @author aschmid
 *
 */
public class MenuController {
	/** The font we use to draw menu items */
	private UnicodeFont font;
	
	/** The font we use to draw menu the hint */
	private UnicodeFont fontHint;
	
	/** Stores the different menu screens */
	private ArrayList<MenuScreen> screens;
	
	/** The screen that is currently active */
	private MenuScreen activeScreen;
	
	/** Is this menu visible? */
	private boolean visible = true;
	
	/** The hint to draw in the top left corner */
	private String hint;
	
	/** If we are allowing normal input */
	private boolean allowInput = true;
	
	/** Are we looking for any key */
	private MenuItemControlBase anyKey;
	
	/*
	 * Creates a new menu contoller
	 */
	@SuppressWarnings("unchecked")	// Error in slick with fonts, nothing I can do
	public MenuController() {
		try {
			// Create font
			font = new UnicodeFont(new Font("Times New Roman", Font.BOLD, 24));
			font.getEffects().add(new ColorEffect(java.awt.Color.white));
			font.addNeheGlyphs();
			font.loadGlyphs();
			
			// Create font
			fontHint = new UnicodeFont(new Font("Times New Roman", Font.BOLD, 18));
			fontHint.getEffects().add(new ColorEffect(java.awt.Color.white));
			fontHint.addNeheGlyphs();
			fontHint.loadGlyphs();
		} catch (SlickException e) {
			e.printStackTrace();
		}
		
		// Create array list for menu screens
		screens = new ArrayList<MenuScreen>();
		
		// Create screens
		MenuScreen screen_main = createScreen();
		MenuScreen screen_options = createScreen();
		MenuScreen screen_connect = createScreen();
		
		// Select the menu screen
		select(screen_main);
		
		// Set the navigation hint
		this.navHint();
		
		// Main Menu
		screen_main.addMenuItem(new MenuItemHost(this, "Host"));
		screen_main.addMenuItem(new MenuItemLink(this, "IP Connect", screen_connect));
		screen_main.addMenuItem(new MenuItemLink(this, "Options", screen_options));
		screen_main.addMenuItem(new MenuItemExit(this, "Exit"));
		
		// Connect menu
		screen_connect.addMenuItem(new MenuItemLink(this, "Back", screen_main));
		screen_connect.addMenuItem(new MenuItemFindGame(this, "127.0.0.1"));
		screen_connect.addMenuItem(new MenuItemFindGame(this, "10.9.196.118"));
		screen_connect.addMenuItem(new MenuItemScanner(this, "Enter into console"));
		
		// Options Menu
		screen_options.addMenuItem(new MenuItemLink(this, "Back", screen_main));
		screen_options.addMenuItem(new MenuItemControlBase(this, "p1_right", "Player 1 Right", Settings.p1_right));
		screen_options.addMenuItem(new MenuItemControlBase(this, "p1_up", "Player 1 Up", Settings.p1_up));
		screen_options.addMenuItem(new MenuItemControlBase(this, "p1_left", "Player 1 Left", Settings.p1_left));
		screen_options.addMenuItem(new MenuItemControlBase(this, "p1_down", "Player 1 Down", Settings.p1_down));
		screen_options.addMenuItem(new MenuItemControlBase(this, "p1_pewpew", "Player 1 Shoot", Settings.p1_pewpew));
		
		screen_options.addMenuItem(new MenuItemControlBase(this, "p2_right", "Player 2 Right", Settings.p2_right));
		screen_options.addMenuItem(new MenuItemControlBase(this, "p2_up", "Player 2 Up", Settings.p2_up));
		screen_options.addMenuItem(new MenuItemControlBase(this, "p2_left", "Player 2 Left", Settings.p2_left));
		screen_options.addMenuItem(new MenuItemControlBase(this, "p2_down", "Player 2 Down", Settings.p2_down));
		screen_options.addMenuItem(new MenuItemControlBase(this, "p2_pewpew", "Player 2 Shoot", Settings.p2_pewpew));
		
		screen_options.addMenuItem(new MenuItemResWidth(this));
	}
	
	/**
	 * Changes the hint to display on the menu
	 * @param hint The hint to display
	 */
	public void setHint(String hint) {
		this.hint = hint;
	}
	
	/**
	 * Create a new menu screen, and store it
	 * @return A new menu screen
	 */
	public MenuScreen createScreen() {
		// Create a new menu screen
		MenuScreen s = new MenuScreen(this);
		
		// Store it
		this.screens.add(s);
		
		// Return it
		return s;
	}
	
	/**
	 * Selects a given menu screen
	 * @param s The screen to select
	 */
	public void select(MenuScreen s)  {
		this.activeScreen = s;
	}
	
	/**
	 * Makes the menu wait for anykey to be pressed, and then parses it to the given menu item
	 * @param mi The menu item to parse any key to
	 */
	public void getAnyKey(MenuItemControlBase mi) {
		this.allowInput = false;
		this.anyKey = mi;
		
		this.setHint("Press any key to change the control.");
	}
	
	/**
	 * A navigation hint
	 */
	public void navHint() {
		setHint("Use "+
			Input.getKeyName(Settings.menu_up)+", "+
			Input.getKeyName(Settings.menu_down)+", and "+
			Input.getKeyName(Settings.menu_select)+" to navigate");
	}
	
	/**
	 * Updates this menu controller
	 * @param input An input from slick
	 * @param delta The time since the last update
	 */
	public void update(Input input, int delta) {
		if(this.visible) {
			// Check if we are allowing input
			if(this.allowInput) {
				// Update active screen
				activeScreen.update(input, delta);
			} else {
				if(anyKey != null) {
					// We are doing anykey
					
					for(int i=0; i<256; i++) {
						if(input.isKeyPressed(i)) {
							// Tell the menu item
							this.anyKey.anyKeyPressed(i);
							
							// Allow input again
							this.allowInput = true;
							
							// Stop anykey
							this.anyKey = null;
							
							// Reset the nav hint
							navHint();
						}
					}
				}
			}
		}
	}
	
	/**
	 * Renders the menu
	 */
	public void render() {
		if(this.visible) {
			// Draw help text
			fontHint.drawString(4, 4, this.hint);
			
			// Render the active screen
			activeScreen.render();
		}
	}
	
	/**
	 * Draws text
	 * @param x x position to draw
	 * @param y y position to draw
	 * @param txt text to draw
	 */
	public void drawText(float x, float y, String txt) {
		font.drawString(x, y, txt, Color.white);
	}
	
	/**
	 * Hides this menu
	 */
	public void hideMenu() {
		this.visible = false;
	}
	
	/**
	 * Shows this menu
	 */
	public void showMenu() {
		this.visible = true;
	}
}
