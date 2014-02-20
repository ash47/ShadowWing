package core;

import gameObjects.CenterCamera;
import gameObjects.NS_Player;
import network.Client;
import network.Msg;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import menu.MenuController;

public class GameController {
	/** Contains a menu for this player */
    private MenuController menu;
    
    /** The x offset to draw this player's view at */
    private int view_xo = 0;
    
    /** The y offset to draw this player's view at */
    private int view_yo = 0;
    
    /** The width of this player's view */
    private int view_width = Game.playwidth();
    
    /** The height of this player's view */
    private int view_height = Game.playheight();
    
    /** The previous x direction our player was moving in */
    private double prev_dir_x = 2;
    
    /** The previous y direction our player was moving in */
    private double prev_dir_y = 2;
    
    /** Weather our player was previously shooting or not */
    private boolean prev_isShooting;
    
    /** Our player number */
    private byte playerNum;
    
    /** Wether or not this client is active */
    private boolean active;
    
    /** The camera to render our view from */
    private CenterCamera myCamera;
    
    /** Our player Entity */
    private NS_Player myPlayer;
    
    /** Stores this player's panel */
    private Panel panel;
    
    /**
     * Creates a new Game Controller
     * @param playerNum The player number, 1 for player one, 2 for player two
     * @param active Should we start this controller has active?
     */
    GameController(byte playerNum, boolean active) {
    	// Store vars
    	this.playerNum = playerNum;
    	this.active = active;
    	
    	// Never got around to making a menu for player2
    	if(playerNum == 1) {
    		// Create a menu controller
    		menu = new MenuController();
    	}
    	
    	// Create a panel
    	try {
			panel = new Panel();
		} catch (SlickException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Adds this controller's player to the game
     */
    public void connectClient() {
    	// Create a camera for our view
    	myCamera = new CenterCamera(null);
    	
    	// Check if they are the server
    	if(Game.isServer) {
    		// They are the server, just spawn a new player
    		
    		// Create a player
    		myPlayer = Game.getWorld().createPlayer();
			
			// Make the camera follow our player
			myCamera.setFollowObject(myPlayer);
    	} else {
    		// Not the server, tell the server we are ready to play
    		
    		// Grab the client
    		Client client = Game.getClient();
    		
    		// Send a message with our player num
    		client.clearBuffer();
    		client.writeByte(Msg.PLAY);
    		client.writeByte((byte)this.playerNum);
    		client.sendMessage();
    	}
    }
    /**
     * Sets the view x offset for this player
     * @param xo The x offset of this players view
     */
    public void setView_xo(int xo) {
    	this.view_xo = xo;
    }
    
    /**
     * Sets the width of this player's view
     * @param width The width of this player's view
     */
    public void setView_width(int width) {
    	this.view_width = width;
    }
    
    /**
     * Uses an entID to find this controller's player
     * @param playerEntID The entID of this controller's player
     */
    public void findPlayerEnt(short playerEntID) {
    	// Find our player
    	myPlayer = (NS_Player) Game.getWorld().getEntByID(playerEntID);
    	
    	// Create a camera to follow our player
    	this.myCamera.setFollowObject(myPlayer);
    }
    
    /**
     * Checks if the controller is active
     * @return Wether this game controller is active or not
     */
    public boolean isActive() {
    	return this.active;
    }
    
    /**
     * Updates this game controller
     * @param input The input controller
     * @param delta The time in ms since the last update
     */
    public void update(Input input, int delta) {
    	boolean inGame = Game.getGame().inGame();
    	
    	if(!this.active && inGame) {
    		if(input.isKeyPressed(Settings.p2_pewpew)) {
    			// Connect the 2nd player
    			Game.getGame().joinNewPlayer();
    			
    			this.active = true;
    		}
    		
    		return;
    	}
    	
    	if(this.playerNum == 1) {
    		// Update the menu
    		menu.update(input, delta);
    	}
    	
        // Check if we are ingame
        if(inGame) {
        	// Update the player's movement direction based on keyboard presses.
            byte dir_x = 0;
            byte dir_y = 0;
            
            boolean isShooting = false;
            
            if(playerNum == 1) {
	            if (input.isKeyDown(Settings.p1_down))
	                dir_y += 1;
	            if (input.isKeyDown(Settings.p1_up))
	                dir_y -= 1;
	            if (input.isKeyDown(Settings.p1_left))
	                dir_x -= 1;
	            if (input.isKeyDown(Settings.p1_right))
	                dir_x += 1;
	            if (input.isKeyDown(Settings.p1_pewpew))
	                isShooting = true;
            } else {
            	if (input.isKeyDown(Settings.p2_down))
	                dir_y += 1;
	            if (input.isKeyDown(Settings.p2_up))
	                dir_y -= 1;
	            if (input.isKeyDown(Settings.p2_left))
	                dir_x -= 1;
	            if (input.isKeyDown(Settings.p2_right))
	                dir_x += 1;
	            if (input.isKeyDown(Settings.p2_pewpew))
	                isShooting = true;
            }
            
            // Push the input onto our player
            if(myPlayer != null) {
            	// Always push movement
            	myPlayer.updateDirs(dir_x, dir_y);
            	
            	// Only push shooting if we're teh server
            	if(Game.isServer) {
            		myPlayer.setIsShooting(isShooting);
            	} else {
            		// Grab the client
            		Client client = Game.getClient();
            		
		            if(prev_dir_x != dir_x || prev_dir_y != dir_y) {
		    	        // Send update to the server
		            	client.clearBuffer();
		            	client.writeByte(Msg.MOVE);
		            	client.writeByte(this.playerNum);
		            	client.writeByte((byte)dir_x);
		            	client.writeByte((byte)dir_y);
		            	client.sendMessage();
		    			
		    			// Store directions
		    			prev_dir_x = dir_x;
		    			prev_dir_y = dir_y;
		            }
		            
		            if(prev_isShooting != isShooting) {
		            	// Send update to the server
		            	client.clearBuffer();
		            	client.writeByte(Msg.SHOOT);
		            	client.writeByte(this.playerNum);
		            	client.writeBoolean(isShooting);
		    			client.sendMessage();
		    			
		    			// Store the change
		    			prev_isShooting = isShooting;
		            }
            	}
            }
            
            // Update your camera
            if(myCamera != null) {
            	myCamera.update(delta);
            }
        }
    }
    
    /**
     * Renders this player's view of the world
     * @param g The main graphics render for this frame
     * @throws SlickException
     */
    public void render(Graphics g) throws SlickException {
    	boolean inGame = Game.getGame().inGame();
    	
    	if(!this.active && inGame) {
    		g.setColor(Color.white);
    		g.drawString("Press "+Input.getKeyName(Settings.p2_pewpew)+" to join.", 4, 4);
    		
    		return;
    	}
    	
    	// Setup the view
    	g.translate(this.view_xo, this.view_yo);
    	g.setClip(this.view_xo, this.view_yo, this.view_width, this.view_height);
    	
    	// Check if we are in a game
    	if(inGame) {
    		// Make sure we have a camera
    		if(myCamera != null) {
	    		// Render
	    		World world = Game.getWorld();
	    		
	    		world.setActiveCamera(myCamera);
	    		world.render(this.view_width, this.view_height);
	    		
	    		if(myPlayer != null) {
	    			panel.render(g, (this.view_width-800)/2, this.view_width, myPlayer.getShield(), myPlayer.getMaxShield(), myPlayer.getFirePower());
	    		}
    		}
    	} else {
    		// Never got around to making a menu for player2
    		if(this.playerNum == 1) {
    			// Render the menu
    			menu.render();
    		}
    	}
    	
    	// Reset translation
    	g.resetTransform();
    	g.clearClip();
    }
}
