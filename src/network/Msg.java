package network;

/**
 * Contains a bunch of IDs for messages, used in various places
 * @author aschmid
 *
 */
public class Msg {
	/** A player is ready to play / Server is sending entID to a client */
	public static final byte PLAY = 1;
	
	/** Client is sending key press data to server */
	public static final byte MOVE = 3;
	
	/** The client is telling the server weather it is shooting or not */
	public static final byte SHOOT = 4;
	
	/** Server is syncing an ent to the client */
	public static final byte SYNC_ENT = 5;
	
	/** The server created a new ent, and is telling the client */
	public static final byte NEW_ENT = 6;
	
	/** The server removed an ent */
	public static final byte REMOVE_ENT = 7;
	
	/** The server is sending data on an ent */
	public static final byte ENT_DATA = 8;
	
	/** The server is sending the screen Y */
	public static final byte SCREEN_Y = 9;
	
	/** Used to sync the time between client and server properly */
	public static final byte SYNC_TIMER = 10;
	
	/** Server is sending sync offset */
	public static final byte SYNC_OFFSET = 11;
}
