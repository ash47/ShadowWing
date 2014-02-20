package gameObjects;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

import core.Game;

/**
 * A bounding box based collision system
 * @author aschmid
 *
 */
public class BoundBox {
	/** The x offset for the bounding box */
	private int xo;
	
	/** The y offset for the bounding box */
	private int yo;
	
	/** The width of the bounding box */
	private int width;
	
	/** The height of the bounding box */
	private int height;
	
	/** If this bounding box is solid or not */
	private boolean solid;
	
	/**
	 * Creates a new bounding box based on the given image
	 * @param image The image to base the bounding box on
	 */
	public BoundBox(Image image) {
		// Store width and height of the mask
		width = image.getWidth();
		height = image.getHeight();
		
		// Find the highest point in the image with solid data
		int high = -1;
		highLoop:
			for(int y=0; y<height; y++) {
				for(int x=0; x<width; x++) {
					// Grab the color at this pixel
					Color color = image.getColor(x, y);
					
					// A solid pixel
					if((color.getAlpha() > 0)) {
						high = y;
						break highLoop;
					}
				}
			}
		
		// If we failed to find a point
		if(high == -1) {
			// The image isn't solid / has no collision data
			this.solid = false;
			return;
		}
		
		// Find the lowesst point on the image with data
		int low = -1;
		lowLoop:
			for(int y=height-1; y>=0; y--) {
				for(int x=0; x<width; x++) {
					// Grab the color at this pixel
					Color color = image.getColor(x, y);
					
					// A solid pixel
					if((color.getAlpha() > 0)) {
						low = y;
						break lowLoop;
					}
				}
			}
		
		
		int left = 0;
		leftLoop:
			for(int x=0; x<width; x++) {
				for(int y=0; y<height; y++) {
					// Grab the color at this pixel
					Color color = image.getColor(x, y);
					
					// A solid pixel
					if((color.getAlpha() > 0)) {
						left = x;
						break leftLoop;
					}
				}
			}
		
		int right = width;
		rightLoop:
			for(int x=width-1; x>=0; x--) {
				for(int y=0; y<height; y++) {
					// Grab the color at this pixel
					Color color = image.getColor(x, y);
					
					// A solid pixel
					if((color.getAlpha() > 0)) {
						right = x;
						break rightLoop;
					}
				}
			}
		
		// Calculate the offset to get from the middle of the image, to the top left most point
		xo = left - width/2;
		yo = high - height/2;
		
		// Use the data we just found to build a bounding box
		width = right - left+1;
		height = low - high+1;
		
		// Make sure we have some solid data
		if(width <= 0 || height <= 0) {
			this.solid = false;
			return;
		}
		
		// Move the offset to the middle of the bounding box
		xo += width/2;
		yo += height/2;
		
		// We are solid
		this.solid = true;
	}
	
	/**
	 * Checks if this bounding box collides with a wall at the given position
	 * @param x x position to check
	 * @param y y position to check
	 * @return If this bounding box hits a wall or not
	 */
	public boolean solidAtPos(float x, float y) {
		// Check if we're solid
		if(!solid) return false;
		
		// Check the 4 corners
		return Game.getWorld().solidAtPos(x+xo, y+yo, width, height);
	}
	
	/**
	 * Checks if this bounding box intersects another entities bounding box
	 * @param x x position to check
	 * @param y y position to check
	 * @param ent2 The ent to check against
	 * @return If this bounding box intersects the other players or not
	 */
	public boolean intersecting(float x, float y, VisibleGameObject ent2) {
		// Grab the other ent's bounding box
		BoundBox b = ent2.getBoundBox();
		
		// Workout the distance between the two entities
		float xDist = Math.abs((x+this.xo) - (ent2.getPosX() + b.xo));
		float yDist = Math.abs((y+this.xo) - (ent2.getPosY() + b.yo));
		
		// Check if the two entities intersect
		if(	xDist < (this.width/2 + b.width/2) &&
			yDist < (this.height/2 + b.height/2)) {
			// They do
			return true;
		}
		
		// They don't intersect
		return false;
	}
}
