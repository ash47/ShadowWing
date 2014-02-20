package Particles;

import gameObjects.Camera;
import gameObjects.VisibleGameObject;

import java.io.IOException;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ParticleIO;
import org.newdawn.slick.particles.ParticleSystem;

import core.Game;
import core.World;

/**
 * Creates a particle emitter
 * @author aschmid
 *
 */
public class Emitter {
	/** The main particle system container */
	private ParticleSystem particleSystem;
	
	/** The current x pos */
	private float posX;
	
	/** The current y pos */
	private float posY;
	
	/** If this particle system is valid, or not */
	private boolean valid;
	
	/**
	 * Creates a new emitter
	 * @param file The file and image name to use, in "assets/particles/file.<png/xml>"
	 * @param posX the x position to create it
	 * @param posY the y position to create it
	 * @param loop should we play the particle system once, or loop it
	 */
	public Emitter(String file, float posX, float posY, boolean loop) {
		// Store position
		this.posX = posX;
		this.posY = posY;
		
		// This system is valid
		this.valid = true;
		
		try {
			// Create the particle system\
			particleSystem = new ParticleSystem("assets/particles/"+file+".png");
			
			// Load particles from an XML file
			ConfigurableEmitter emitter = ParticleIO.loadEmitter("assets/particles/"+file+".xml");
			particleSystem.addEmitter(emitter);
			
			// Stop loops
			if(!loop) {
				particleSystem.setRemoveCompletedEmitters(true);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Updates the given particle system
	 * @param delta Time since last update
	 */
	public void update(int delta) {
		particleSystem.update(delta);
		
		// Check if the system is done
		if(particleSystem.getEmitterCount() <= 0) {
			this.valid = false;
		}
	}
	
	/**
	 * Checks if this particle system is valid
	 * @return if this particle system is valid
	 */
	public boolean isValid() {
		return this.valid;
	}
	
	/**
	 * Renders this particle system
	 * @param cam The camera to base the position off of
	 */
	public void render(Camera cam) {
		particleSystem.render(this.posX-cam.getCamX(), this.posY-cam.getCamY());
	}
	
	/**
	 * Renders this particle system at a given position
	 * @param cam The camera to base the position off of
	 * @param x The x coord to render at
	 * @param y The y coord to render at
	 */
	public void render(Camera cam, float x, float y) {
		particleSystem.render(x-cam.getCamX(), y-cam.getCamY());
	}
}
