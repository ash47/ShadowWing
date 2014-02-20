package gameObjects;

/* SWEN20003 Object Oriented Software Development
 * Space Game Engine - GameObject Class
 * Author: Ashley Schmid <aschmid>
 */

/** Represents any game related object.
 */
public class GameObject {
	/** The current x-coordinate of this game object. */
	protected float posX;
	
	/** The current y-coordinate of this game object. */
	protected float posY;
	
	/** If this object is valid anymore */
	private boolean valid = true;
	
	/** Create a new game object.
     * @param x The x coordinate to spawn at.
     * @param y The y coordinate to spawn at.
     */
	public GameObject(float x, float y) {
		// Store vars
		this.posX = x;
		this.posY = y;
	}
	
	public GameObject() {
		// Default to the top right
		this.posX = 0;
		this.posY = 0;
	}
	
	public void update(int delta) {
		// Nothing happens in the base game object
	}
	
	/** Change the position of this GameObject
	 * @param x The x-coordinate to set this GameObject to.
	 * @param y The y-coordinate to set this GameObject to.
	 * */
	public void setPos(float x, float y) {
		// Store starting position
		this.posX = x;
		this.posY = y;
	}
	
	/** Gets the x-coordinate.
	 * @return Returns the x-coordinate of this GameObject.
	 */
	public float getPosX() {
		return this.posX;
	}
	
	/** Gets the y-coordinate.
	 * @return Returns the y-coordinate of this GameObject.
	 */
	public float getPosY() {
		return this.posY;
	}
	
	/**
	 * Checks if this object is valid
	 * @return If this object is valid or not
	 */
	public boolean isValid() {
		return this.valid;
	}
	
	/**
	 * Sets if this object is valid or not
	 * @param valid IF this object is valid or not
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	/**
	 * Cleans up this object
	 */
	public void cleanup() {
		this.setValid(false);
	}
}
