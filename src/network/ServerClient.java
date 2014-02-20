package network;

import gameObjects.NS_Player;
import java.net.Socket;
import core.Game;

/**
 * Handles a given clients connection
 * @author aschmid
 *
 */
public class ServerClient extends Sock {
	/** Player 1s entity */
	NS_Player player1;
	
	/** Player 2s entity */
	NS_Player player2;
	
	/**
	 * Creates a new server client
	 * @param socket The socket to watch
	 */
	ServerClient(Socket socket) {
		super(socket);
		
		// Start the thread
		thread.start();
	}
	
	/**
	 * Cleans up this client
	 */
	public void cleanup() {
		// Check if we have a players
		if(this.player1 != null) {
			this.player1.cleanup();
		}
		
		if(this.player2 != null) {
			this.player2.cleanup();
		}
	}
	
	/**
	 * Processes a message
	 * @param buff The buffer to read from
	 */
	protected void processMessage(Buffer buff) {
		// Read the message ID
		byte messageID = buff.readByte();
		
		byte playerNum;
		
		// Process the message based on the ID
		switch(messageID) {
			case Msg.PLAY:
				// Grab which player it was
				playerNum = buff.readByte();
				
				short entID = -1;
				
				if(playerNum == 1) {
					if(player1 == null) {
						// Create a player
						player1 = Game.getWorld().createPlayer();
						
						// Grab entID
						entID = player1.getEntID();
					}
				} else {
					if(player2 == null) {
						// Create a player
						player2 = Game.getWorld().createPlayer();
						
						// Grab entID
						entID = player2.getEntID();
					}
				}
				
				// If a player was created
				if(entID != -1) {
					// Tell the client
					clearBuffer();
					writeByte(Msg.PLAY);
					writeByte(playerNum);
					writeShort(entID);
					sendMessage();
				}
			break;
			
			case Msg.MOVE:
				// Read which player this is about
				playerNum = buff.readByte();
				
				// Read directional data
				byte dir_x = buff.readByte();
				byte dir_y = buff.readByte();
				
				// Check which player this is about, update their dirs
				if(playerNum == 1) {
					// Validate player first
					if(player1 != null)
						player1.updateDirs(dir_x, dir_y);
				} else {
					// Validate player first
					if(player2 != null)
						player2.updateDirs(dir_x, dir_y);
				}
			break;
			
			case Msg.SHOOT:
				// Read which player this is about
				playerNum = buff.readByte();
				
				// Check which player it's about
				if(playerNum == 1) {
					// Validate player first
					if(player1 != null)
						player1.setIsShooting(buff.readBoolean());
				} else {
					// Validate player first
					if(player2 != null)
						player2.setIsShooting(buff.readBoolean());
				}
			break;
			
			case Msg.SYNC_TIMER:
				// Read client time
				long clientTime = buff.readLong();
				
				// Send them their sync offset
				clearBuffer();
				writeByte(Msg.SYNC_OFFSET);
				writeLong((Game.getGameTime()-clientTime)/2);
				sendMessage();
			break;
			
			default:
				System.out.println("Unknown messageID "+messageID);
			break;
		}
	}
}
