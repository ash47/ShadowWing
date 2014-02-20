package network;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;

import core.Game;
import core.World;

/**
 * Handles connections from clients
 * @author aschmid
 *
 */
public class Server implements Runnable {
	/** Contains the socket */
	ServerSocket server;
	
	/** Holds all the active clients */
	ArrayList<ServerClient> clients;
	
	/** A buffer to write to */
	Buffer serverBuff;
	
	/**
	 * Create a new world
	 */
	public Server() {
		// Attempt to create a server
		try {
			server = new ServerSocket(1337);
			System.out.println("Opened port!");
		}
		catch(IOException e) {
			System.out.println("Failed to open port >_>");
			return;
		}
		
		try {
			String ip = Inet4Address.getLocalHost().getHostAddress();
			System.out.println(ip);
		} catch (UnknownHostException e) {
			System.out.println("Failed to find IP");
			e.printStackTrace();
		}
		
		// Prepare arraylist
		clients = new ArrayList<ServerClient>();
		
		// Allocate buffer for the server
		serverBuff = new Buffer(1024);
		
		// Start server thread
		Thread t = new Thread(this);
		t.start();
	}
	
	/**
	 * Broadcasts teh current buffer to all clients
	 */
	public synchronized void broadcast() {
		// Loop over all the clients
		Iterator<ServerClient> i = clients.iterator();
		while(i.hasNext()) {
			// Grab the next client
			ServerClient client = i.next();
			
			if(client.isValid()) {
				// Send them the buffer
				client.sendMessage(serverBuff);
			} else {
				i.remove();
			}
		}
	}
	
	/**
	 * Broadcasts the given buffer to all clients
	 * @param buff Buffer to broadcast
	 */
	public synchronized void broadcast(Buffer buff) {
		// Loop over all the clients
		Iterator<ServerClient> i = clients.iterator();
		while(i.hasNext()) {
			// Grab the next client
			ServerClient client = i.next();
			
			if(client.isValid()) {
				// Send them the buffer
				client.sendMessage(buff);
			} else {
				i.remove();
			}
		}
	}
	
	/**
	 * Makes new players for the clients
	 */
	public synchronized void remakePlayers() {
		// Loop over all the clients
		Iterator<ServerClient> i = clients.iterator();
		while(i.hasNext()) {
			// Grab the next client
			ServerClient client = i.next();
			
			if(client.isValid()) {
				if(client.player1 != null) {
					client.player1 = Game.getWorld().createPlayer();
					
					client.clearBuffer();
					client.writeByte(Msg.PLAY);
					client.writeByte((byte)1);
					client.writeShort(client.player1.getEntID());
					client.sendMessage();
				}
				
				if(client.player2 != null) {
					client.player2 = Game.getWorld().createPlayer();
					
					client.clearBuffer();
					client.writeByte(Msg.PLAY);
					client.writeByte((byte)2);
					client.writeShort(client.player2.getEntID());
					client.sendMessage();
				}
			}
		}
	}
	
	/**
	 * Runs the server
	 */
	public void run()  {
		while(true) {
			// Try and accept a new connection
			try {
				// Accept a client
				Socket newClient = server.accept();
				
				// Create a server client for this client
				ServerClient client = new ServerClient(newClient);
				
				// Store this client
				clients.add(client);
				
				client.clearBuffer();
				client.writeByte(Msg.SYNC_TIMER);
				client.writeLong(Game.getGameTime());
				client.writeFloat(World.getScreenY());
				client.sendMessage();
				
				// Find and send over ALL pre-existing entities
				Game.getWorld().sendAllEnts(client);
				
				// Log that someone connected
				System.out.println("Someone Connected!");
			} catch (IOException e) {
				System.out.println("Failed to do client stuff");
				System.exit(-1);
			}
		}
	}
}
