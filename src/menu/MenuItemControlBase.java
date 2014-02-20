package menu;

import org.newdawn.slick.Input;

import core.Settings;

/**
 * A menu item used for changing controls
 * @author aschmid
 *
 */
public class MenuItemControlBase extends MenuItem {
	/** Friendly name of button */
	String friendlyName;
	
	/** Internal name of the setting */
	String settingName;
	
	/**
	 * Create a new menu item control
	 * @param controller The menu controller to attach to
	 * @param settingName The name of the setting
	 * @param friendlyName the name of the setting to show
	 * @param def the default value
	 */
	MenuItemControlBase(MenuController controller, String settingName, String friendlyName, int def) {
		super(controller, friendlyName+" - "+Input.getKeyName(def));
		
		// Store stuff
		this.settingName = settingName;
		this.friendlyName = friendlyName;
	}
	
	/**
	 * Item is selected, ask for anykey
	 */
	public void select() {
		this.con.getAnyKey(this);
	}
	
	/**
	 * anykey is pressed, change setting
	 * @param key the key that was pressed
	 */
	public void anyKeyPressed(int key) {
		this.name = friendlyName+" - "+Input.getKeyName(key);
		Settings.setInt(settingName, key);
	}
}
