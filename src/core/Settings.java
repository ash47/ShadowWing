package core;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.newdawn.slick.Input;

/**
 * A class for storing and retrieving settings easily.
 * @author aschmid
 *
 */
public class Settings {
	/** Where to store the settings */
	private static final String storeLocation = "aschmid/Shadow Wing";
	
	/** Stores the current pref lib */
	private static final Preferences prefs = Preferences.userRoot().node(storeLocation);
	
	/** Button to move menu up */
	public static int menu_up = Settings.getInt("menu_up", Input.KEY_UP);
	
	/** Button to move menu down */
	public static int menu_down = Settings.getInt("menu_down", Input.KEY_DOWN);
	
	/** Button to move menu left */
	public static int menu_left = Settings.getInt("menu_left", Input.KEY_LEFT);
	
	/** Button to move menu right */
	public static int menu_right = Settings.getInt("menu_right", Input.KEY_RIGHT);
	
	/** Button to select a menu item */
	public static int menu_select = Settings.getInt("menu_select", Input.KEY_ENTER);
	
	/** Player 1's left key */
	public static int p1_left = Settings.getInt("p1_left", Input.KEY_LEFT);
	
	/** Player 1's right key */
	public static int p1_right = Settings.getInt("p1_right", Input.KEY_RIGHT);
	
	/** Player 1's up key */
	public static int p1_up = Settings.getInt("p1_up", Input.KEY_UP);
	
	/** Player 1's down key */
	public static int p1_down = Settings.getInt("p1_down", Input.KEY_DOWN);
	
	/** Player 1's shoot key */
	public static int p1_pewpew = Settings.getInt("p1_pewpew", Input.KEY_SPACE);
	
	/** Player 2's left key */
	public static int p2_left = Settings.getInt("p2_left", Input.KEY_A);
	
	/** Player 2's right key */
	public static int p2_right = Settings.getInt("p2_right", Input.KEY_D);
	
	/** Player 2's up key */
	public static int p2_up = Settings.getInt("p2_up", Input.KEY_W);
	
	/** Player 2's down key */
	public static int p2_down = Settings.getInt("p2_down", Input.KEY_S);
	
	/** Player 2's shoot key */
	public static int p2_pewpew = Settings.getInt("p2_pewpew", Input.KEY_Q);
	
	/**
	 * Gets a setting
	 * @param name The name of the setting
	 * @param def The value if the setting wasn't found
	 * @return The value of a setting, or def if it doesn't exist
	 */
	public static int getInt(String name, int def) {
		return prefs.getInt(name, def);
	}
	
	/**
	 * Stores a setting
	 * @param name The name of the setting
	 * @param value The value to store for this setting
	 */
	public static void setInt(String name, int value) {
		try {
			prefs.putInt(name, value);
			prefs.flush();
			
			// Update values ingame
			switch(name) {
				case "p1_left":
					p1_left = value;
				break;
				
				case "p1_right":
					p1_right = value;
				break;
				
				case "p1_up":
					p1_up = value;
				break;
				
				case "p1_down":
					p1_down = value;
				break;
				
				case "p1_pewpew":
					p1_pewpew = value;
				break;
				
				case "p2_left":
					p2_left = value;
				break;
				
				case "p2_right":
					p2_right = value;
				break;
				
				case "p2_up":
					p2_up = value;
				break;
				
				case "p2_down":
					p2_down = value;
				break;
				
				case "p2_pewpew":
					p2_pewpew = value;
				break;
			}
		} catch (BackingStoreException e) {
			System.out.println("Failed to store setting "+name+" - "+value);
		}
	}
}
