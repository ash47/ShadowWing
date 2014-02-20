package gameObjects;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

import core.Game;
import core.World;

/**
 * A class for pixel perfect collisions
 * @author aschmid
 *
 */
public class BitMask {
	/** Stores the collision data */
	private boolean[] mask;
	
	/** The width of the mask */
	private int width;
	
	/** The height of the mask */
	private int height;
	
	/**
	 * Creates a new bit mask based on the given image
	 * @param image The image to create based on
	 */
	public BitMask(Image image) {
		// Store width and height of the mask
		width = image.getWidth();
		height = image.getHeight();
		
		// Create byte array to store mask
		mask = new boolean[width*height];
		
		for(int y=0; y<height; y++) {
			for(int x=0; x<width; x++) {
				// Grab the color at this pixel
				Color color = image.getColor(x, y);
				
				// Check if this pixel should be visible
				mask[x + y*width] = (color.getAlpha() > 0);
			}
		}
	}
	
	/**
	 * Checks if this mask collides with a wall at the given position
	 * @param x x position to check
	 * @param y y position to check
	 * @return If a given position is solid or not
	 */
	public boolean solidAtPos(float x, float y) {
		// Grab a reference to the world
		World world = Game.getWorld();
		
		int left = (int)(x - width/2);
		int top = (int)(y - height/2);
		
		for(int yy=0; yy<height; yy++) {
			for(int xx=0; xx<width; xx++) {
				// Check if this pixel is solid, and if it collides
				if(mask[xx + yy*width] && world.solidAtPoint(left+xx, top+yy)) {
					return true;
				}
			}
		}
		
		// Not solid
		return false;
	}
}
