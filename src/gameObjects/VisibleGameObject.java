package gameObjects;
/* SWEN20003 Object Oriented Software Development
 * Space Game Engine - VisibleGameObject Class
 * Author: Ashley Schmid <aschmid>
 */

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import core.World;

/** Represents any visible game related object.
 */
public class VisibleGameObject extends GameObject {
	/** The image to draw for this game object, */
	private Image image;
	
	/** The name of our image */
	private String imageName;
	
	/** A bitmask used for precise collisions */
	private BitMask mask;
	
	/** Used for less precise, fast collisions */
	private BoundBox boundBox;
	
	/** Create a new visible game object.
     * @param x The x coordinate to spawn at.
     * @param y The y coordinate to spawn at.
     * @param image The image to use for this game object
	 * @throws SlickException 
     */
	public VisibleGameObject(float x, float y, String image) throws SlickException {
		// Store standard GameObject stuff
		super(x, y);
		
		// Store the name of our image
		imageName = image;
		
		// Create the image for this object
		this.image = new Image(imageName);
	}
	
	/**
	 * Creates a new visible game object
	 */
	public VisibleGameObject() {
		super();
		
		// Store empty image name
		imageName = "";
	}
	
	/**
	 * Gets the width of the image, if we have one
	 * @return The width of our image
	 */
	public int getWidth() {
		if(this.image != null) {
			return this.image.getWidth();
		} else {
			return 0;
		}
	}
	
	/**
	 * Gets the Height of our image if we have one
	 * @return height of our image
	 */
	public int getHeight() {
		if(this.image != null) {
			return this.image.getHeight();
		} else {
			return 0;
		}
	}
	
	/**
	 * Ensures we have a mask
	 */
	public void hasMask() {
		// Ensure we have a mask
		if(this.mask == null) {
			this.mask = new BitMask(this.image);
		}
	}
	
	/**
	 * Checks if a given position is solid, based on pixel perfect data
	 * @param x x to check
	 * @param y y to check
	 * @return if a given position is solid
	 */
	public boolean solidAtPosPrecise(float x, float y) {
		// Ensure they have an image
		if(this.image == null) return true;
		
		// Do a bounding box check first
		if(!this.solidAtPos(x, y)) return false;
		
		// Check if it's solid
		return this.mask.solidAtPos(x, y);
	}
	
	/**
	 * ensures this unit has a bounding box
	 */
	public void hasBoundingBox() {
		// Check if we have a bounding box
		if(this.boundBox == null) {
			// Nope, create one
			this.boundBox = new BoundBox(this.image);
		}
	}
	
	/**
	 * Checks if a given position is solid using a bounding box
	 * @param x x to check
	 * @param y y to check
	 * @return if a given position is solid
	 */
	public boolean solidAtPos(float x, float y) {
		// Ensure they have an image
		if(this.image == null) return true;
		
		// Do the collisions
		return this.boundBox.solidAtPos(x, y);
	}
	
	/**
	 * Sets the name of the image to load
	 * @param img name of the image to load
	 */
	public void setImageName(String img) {
		this.imageName = img;
	}
	
	/**
	 * Gets the name of the image to load
	 * @return the name of the image to load
	 */
	public String getImageName() {
		return this.imageName;
	}
	
	/**
	 * Checks if we are colliding with another ent
	 * @param ent2 The ent to check against
	 * @return if we a re colliding with said ent
	 */
	public boolean colliding(VisibleGameObject ent2) {
		// Make sure both have an image
		if(this.image == null || ent2.image == null) return false;
		
		// Make sure it has a bounding box
		hasBoundingBox();
		
		// Check if their bounding boxes intersect
		if(this.boundBox.intersecting(this.posX, this.posY, ent2)) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Loads our image
	 */
	public void loadImage() {
		// Create the image for this object
		try {
			// Load the image
			this.image = new Image(this.imageName);
			
			// Create a bounding box, and mask for this entity
			this.hasBoundingBox();
			this.hasMask();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * gets our bounding box
	 * @return our bounding box
	 */
	public BoundBox getBoundBox() {
		// Make sure we have a bounding box
		this.hasBoundingBox();
		
		// Return it
		return this.boundBox;
	}
	
	/**
	 * Renders this game object
	 * @param cam the camera to base the render on
	 */
	public void render(Camera cam) {
		if(!World.onScreenVisible(this)) return;
		
		// Make sure we have an image to draw
		if(image != null) {
			// Render at our position, adjusted for the camera
			image.drawCentered(this.posX-cam.getCamX(), this.posY-cam.getCamY());
		}
	}
}
