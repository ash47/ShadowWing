package gameObjects;

import core.World;

/* SWEN20003 Object Oriented Software Development
 * Space Game Engine - FollowCamera Class
 * Author: Ashley Schmid <aschmid>
 */

/** A camera that centers horizontally and vertically onto a game object
 */
public class CenterCamera extends Camera {
	/** Which object this camera should center horizontally on. */
	private GameObject followObject;
	
	/** Create a new Camera that centers on a specific game object.
	 * @param followObject Which object to center on horizontally.
	 * */
	public CenterCamera(GameObject followObject) {
		// Store standard camera stuff
		super(World.PLAYER_START_X, World.PLAYER_START_Y);
		
		// Store which object to follow
		this.followObject = followObject;
		
		// Center onto the selected object
		update(0);
	}
	
	/**
	 * Follow a different object
	 * @param followObject The object to follow
	 */
	public void setFollowObject(GameObject followObject) {
		// Store which object to follow
		this.followObject = followObject;
	}
	
	/** Centers the camera on the game object it is following.
	 *  @param delta Time passed since last frame (milliseconds).
	 *  */
	public void update(int delta) {
		// Make sure we have an object to follow
		if(followObject != null) {
			// Center camera on the player
			this.posX = followObject.getPosX();
			//this.posY = followObject.getPosY();
			
			// Set the camera's y-position to the corrent position
			this.posY = World.getScreenY();
		}
	}
}
