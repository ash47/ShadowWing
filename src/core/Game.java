package core;
/* SWEN20003 Object Oriented Software Development
 * Space Game Engine
 * Author: Matt Giuca <mgiuca>
 */

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import network.Server;
import network.Client;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Input;

/** Main class for the 1945 Game engine.
 * Handles initialisation, input and rendering.
 */
public class Game extends BasicGame {
    /** Location of the "assets" directory. */
    public static final String ASSETS_PATH = "assets";
    
    /** Contains the first player's controller */
    private static GameController player1;
    
    /** Contains the second player's controller */
    private static GameController player2;
    
    /** Contains the main game container */
    private static Game game;
    
    /** The game state. */
    private static World world;
    
    /** The host to try and connect to */
    private static String host = "127.0.0.1";
    
    /** The difference in the client clock and the server clock */
    private static long clockDifference = 0;
    
    /** Weather or not this is the server */
    public static boolean isServer = true;
    
    /** Screen width, in pixels. */
    public static final int screenwidth = Settings.getInt("screenWidth", 800);
    
    /** Screen height, in pixels. */
    public static final int screenheight = 600;
    
    /** Weather or not the game has started */
    private boolean gameStarted = false;
    
    /** The space between the splitscreens */
    private static final int splitScreenSpace = 2;
    
    /** Used to split the screen smoothly */
    private int joinAnimation = screenwidth+1;
    
    /** Stores the server instance */
    private static Server server;
    
    /** Stores the client instance */
    private static Client client;
    
    /**
     * Returns weather or not the game has started
     * @return If the game has started or not
     */
    public boolean inGame() {
    	return this.gameStarted;
    }
    
    /**
     * Gets the width of the play area, in pixels.
     * @return The width of this player's view
     */
    public static int playwidth() {
        return screenwidth;
    }
    
    /**
     * Gets the hight of this player's view
     * @return The height of this player's view
     */
    public static int playheight() {
        return screenheight;
    }

    /**
     * Create a new Game object
     */
    public Game() {
    	super("Shadow Wing");
    }

    /** Initialise the game state.
     * @param gc The Slick game container object.
     */
    @Override
    public void init(GameContainer gc)
    throws SlickException {
    	// Create the players
    	player1 = new GameController((byte)1, true);
    	player2 = new GameController((byte)2, false);
    }

    /** Update the game state for a frame.
     * @param gc The Slick game container object.
     * @param delta Time passed since last frame (milliseconds).
     */
    @Override
    public void update(GameContainer gc, int delta)
    throws SlickException {
        // Get data about the current input (keyboard state).
        Input input = gc.getInput();
        
        if(joinAnimation <= screenwidth) {
        	// Decrease screen size
        	joinAnimation -= delta;
        	
        	// Make sure screen size doesnt get too small
        	if(joinAnimation < screenwidth/2-splitScreenSpace-1) {
        		joinAnimation = screenwidth/2-splitScreenSpace-1;
        	}
        	
        	// Adjust the player views
        	player1.setView_width(joinAnimation);
        	
        	player2.setView_xo(joinAnimation+splitScreenSpace);
        	player2.setView_width(screenwidth - joinAnimation - splitScreenSpace);
        	
        	// Check if we're done changing the screen size
        	if(joinAnimation == screenwidth/2) {
        		// Stop the screensize from changing anymore
        		joinAnimation = screenwidth+1;
        	}
        }
    	
        // Update the players
        player1.update(input, delta);
        player2.update(input, delta);
        
        // Check if we are ingame
        if(this.inGame()) {
        	// Let World.update decide what to do with this data.
            world.update(delta);
        }
    }

    /** Render the entire screen, so it reflects the current game state.
     * @param gc The Slick game container object.
     * @param g The Slick graphics object, used for drawing.
     */
    @Override
    public void render(GameContainer gc, Graphics g)
    throws SlickException {
    	// Render each player's view
    	player1.render(g);
  		player2.render(g);
    }

    /** Start-up method. Creates the game and runs it.
     * @param args Command-line arguments (ignored).
     */
    public static void main(String[] args)
    throws SlickException {
    	Game.game = new Game();
    	
        AppGameContainer app = new AppGameContainer(Game.game);
        app.setShowFPS(false);
        app.setDisplayMode(screenwidth, screenheight, false);
        
        // These two options stop networking functions from freezing if we minimse the window
        app.setUpdateOnlyWhenVisible(false);
        app.setAlwaysRender(true);
        
        app.start();
    }
    
    /**
     * Gets the game state
     * @return The current game state
     */
    public static Game getGame() {
    	return Game.game;
    }
    
    /**
     * Starts a new game, or does nothing if already in a game
     */
    public void startGame() {
    	if(gameStarted) return;
    	
    	try {
			// Check if we are the server
	        if(isServer) {
	        	// Create the world
				world = new World();
				
				// The game has started
				gameStarted = true;
	        	
	        	// Create the server
	        	Game.server = new Server();
	        	
	        	// Load layout info
	        	UnitLayoutReader.read("units");
	        	UnitLayoutReader.read("items");
	        } else {
	        	// Attempt to connect
	        	
		    	try {
		    		System.out.println("Connecting to: "+Game.getHost());
		    		
		    		// Create the world
					world = new World();
					
					// The game has started
					gameStarted = true;
		    		
		    		// Attempt to connect
					client = new Client(new Socket(Game.getHost(), 1337));
					
					System.out.println("Connected successfull");
				} catch (UnknownHostException e) {
					System.out.println("Unknown host");
					System.exit(-1);
				} catch (IOException e) {
					System.out.println("Failed to connect");
					System.exit(-1);
				}
	        }
			
	        // Connect player1
	        player1.connectClient();
	        
	        // Connect player2
	        if(player2.isActive()) {
	        	player2.connectClient();
	        }
		} catch (SlickException e) {
			System.out.println("Failed to create world");
			System.exit(-1);
		}
    }
    
    /**
     * Connects the second player
     */
    public void joinNewPlayer() {
    	// Play the join animation
    	joinAnimation = screenwidth;
    	
    	// If the game has already started
    	if(gameStarted) {
    		// Connect the second player
    		player2.connectClient();
    	}
    }
    
    /**
     * Change if this is the server or not
     * @param isServer Wether this is the server or not
     */
    public static void setServer(boolean isServer) {
    	Game.isServer = isServer;
    }
    
    /**
     * Gets the IP/Host to connect to
     * @return The IP/Host to connect to
     */
    public static String getHost() {
    	return Game.host;
    }
    
    /**
     * Gets the world
     * @return The current world
     */
    public static World getWorld() {
    	return world;
    }
    
    /**
     * Sets the ip/host to connect to
     * @param host The IP/Host to connect to
     */
    public static void setHost(String host) {
    	// Update the host
    	Game.host = host;
    }
    
    /**
     * Gets the server instance
     * @return The server instance
     */
    public static Server getServer() {
    	return Game.server;
    }
    
    /**
     * Gets the client instance
     * @return The client instance
     */
    public static Client getClient() {
    	return Game.client;
    }
    
    /**
     * Gets the first player's controller
     * @return The first player's controller
     */
    public static GameController getPlayer1() {
    	return Game.player1;
    }
    
    /**
     * Gets the second player's controller
     * @return The second player's controller
     */
    public static GameController getPlayer2() {
    	return Game.player2;
    }
    
    /**
     * Gets the time since the game started
     * @return The time since the game started
     */
    public static long getGameTime() {
    	return System.currentTimeMillis();
    }
    
    /**
     * Stores the difference in the client and server clocks
     * @param diff The difference in the two clocks
     */
    public static void setClockDifference(long diff) {
    	clockDifference = diff;
    }
    
    /**
     * Gets the difference between the client and server clocks
     * @return difference between server and client clocks
     */
    public static long getClockDifference() {
    	return Game.clockDifference;
    }
    
    /**
     * Gets the adjusted server time
     * @return the time as it would be on the server right now
     */
    public static long getAdjustedTime() {
    	return (Game.getGameTime() + Game.getClockDifference());
    }
    
    public static int getUpdateDelta(long serverTime) {
    	return (int)(getAdjustedTime() - serverTime);
    }
}
