package gameObjects;
import core.Game;

/* SWEN20003 Object Oriented Software Development
 * Space Game Engine - Camera Class
 * Author: Ashley Schmid <aschmid>
 */

/** Represents different views inside the game world.
 */
public class Camera extends GameObject {
	/** Create a new Camera.
	 * @param x The x-coordinate to spawn this camera.
	 * @param y The y-coordinate to spawn this camera.
	 * */
	public Camera(float x, float y) {
		// Store standard GameObject stuff
		super(x, y);
	}
	
	/** This is a stub function, it is used in OTHER camera types.
	 *  Since this is a generic camera, it does nothing.
	 *  @param delta Time passed since last frame (milliseconds).
	 *  */
	public void update(int delta) {
		// Nothing much happens
	}
	
	/**
	 * Gets a view adjusted x position of the camera
	 * @return The view adjust x position of the camera
	 */
	public float getCamX() {
		return this.posX - Game.getWorld().getViewWidth()/2;
	}
	
	/**
	 * Gets a view adjusted y position of the camera
	 * @return The view adjusted y position of the camera
	 */
	public float getCamY() {
		return this.posY - Game.getWorld().getViewHeight()/2;
	}
}
