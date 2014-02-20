package network;

import gameObjects.EntityCreator;
import gameObjects.NetworkedObject;

import java.net.Socket;

import core.Game;
import core.World;

/**
 * handles data client side
 * @author aschmid
 *
 */
public class Client extends Sock {
	/** Stores the time on the server when we first connected */
	private static long serverTimeStore;
	
	/** Stores our time when we first connected */
	private static long clientTime;
	
	/** Stores the position of the screen when we first connected */
	private static float screenY;
	
	/**
	 * Create a new client
	 * @param socket the socket to liste on
	 */
	public Client(Socket socket) {
		super(socket);
		
		// Start the thread
		thread.start();
	}
	
	/**
	 * Processes message data
	 */
	protected void processMessage(Buffer buff) {
		// Read the message ID
		byte messageID = buff.readByte();
		
		// Define common vars here
		short entID;
		NetworkedObject ent;
		long serverTime;
		
		// Process the message based on the ID
		switch(messageID) {
			case Msg.PLAY:
				// Read the playerNum
				byte playerNum = buff.readByte();
				
				// Read our playerID
				short playerEntID = buff.readShort();
				
				// Check which player it was about
				if(playerNum == 1) {
					// Find it's player ent
					Game.getPlayer1().findPlayerEnt(playerEntID);
				} else {
					// Find it's player ent
					Game.getPlayer2().findPlayerEnt(playerEntID);
				}
			break;
			
			case Msg.NEW_ENT:
				// Parse the buffer to Entity, it will deal with this
				EntityCreator.createEntity(buff);
			break;
			
			case Msg.SYNC_ENT:
				// Grab the entID
				entID = buff.readShort();
				
				// Read in the time the server sent this update
				serverTime = buff.readLong();
				
				// Grab the entity assosiated with the entID we got
				ent = Game.getWorld().getEntByID(entID);
				
				// Make sure we got an entity
				if(ent != null) {
					// Give the entity the buffer, to sync etc
					ent.networkUpdate(buff);
					
					// Simulate some frames
					ent.update(Game.getUpdateDelta(serverTime));
				} else {
					// Failed to find the entity, print a warning
					System.out.println("WARNING: Failed to find ent with ID "+entID);
				}
			break;
			
			case Msg.REMOVE_ENT:
				// Grab the entID
				entID = buff.readShort();
				
				// Tell the world to remove it
				Game.getWorld().removeEntByID(entID);
			break;
			
			case Msg.ENT_DATA:
				// Grab the entID
				entID = buff.readShort();
				
				// Grab the entity assosiated with the entID we got
				ent = Game.getWorld().getEntByID(entID);
				
				// Make sure we got an entity
				if(ent != null) {
					// Give the entity the buffer, to sync etc
					ent.networkData(buff);
				}
			break;
			
			case Msg.SYNC_TIMER:
				// Read the time on the server
				serverTimeStore = buff.readLong();
				screenY = buff.readFloat();
				
				// Update the screenY
				World.setScreenY(screenY);
				
				// Store the current time
				clientTime = Game.getGameTime();
				
				// Send message back to server
				clearBuffer();
				writeByte(Msg.SYNC_TIMER);
				writeLong(serverTimeStore);
				sendMessage();
			break;
			
			case Msg.SYNC_OFFSET:
				// Calculate the clock difference
				long clockDifference = serverTimeStore - clientTime + buff.readLong();
				
				// Store the clock difference
				Game.setClockDifference(clockDifference);
				
				// Reset the screenY
				World.setScreenY(screenY);
				World.updateScreenY(Game.getUpdateDelta(serverTimeStore));
			break;
			
			case Msg.SCREEN_Y:
				// Read the time on the server
				serverTime = buff.readLong();
				
				// Set the screenY
				World.setScreenY(buff.readFloat());
				World.updateScreenY(Game.getUpdateDelta(serverTime));
			break;
			
			default:
				System.out.println("Unknown messageID "+messageID);
			break;
		}
	}
}
