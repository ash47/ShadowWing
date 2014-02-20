package gameObjects;

import core.Game;
import network.Buffer;

/**
 * Used to create entities EASILY over the network
 * @author aschmid
 *
 */
public class EntityCreator {
	/**
	 * Create a new entity, based on the given buffer
	 * @param buff The buffer to read data from
	 * @return A new networked object if it was made successfully
	 */
	public static NetworkedObject createEntity(Buffer buff) {
		// Read Data
		Short entID = buff.readShort();
		String entType = buff.readString();
		String imageName = buff.readString();
		//Long serverTime = buff.readLong();
		
		// Attempt to create the entity
		NetworkedObject ent = createObject(entType);
		
		// Check if we failed
		if(ent == null) {
			System.out.println("Failed to create entity of type "+entType);
			return null;
		}
		
		// Store the image name
		ent.setImageName(imageName);
		
		// Queue it to load the image
		Game.getWorld().loadImage(ent);
		
		// Tell the world to process + render it
		Game.getWorld().addNetworkedObject(ent, entID);
		
		// Load all the network data
		ent.networkInit(buff);
		
		// Simulate some frames
		//ent.update(Game.getUpdateDelta(serverTime));
		
		// Return the ent
		return ent;
	}
	
	/**
	 * Attempts to create an object
	 * @param type The type of object to create (class name)
	 * @return A new object, if one was created
	 */
	private static NetworkedObject createObject(String type) {
		try {
			return (NetworkedObject)(Class.forName(type).newInstance());
		} catch (InstantiationException e) {
			System.out.println("Failed to create instance!");
		} catch (IllegalAccessException e) {
			System.out.println("Illegal access to object!");
		} catch (ClassNotFoundException e) {
			System.out.println("Failed to find the class!");
		}
		
		// Failed to create the object :(
		return null;
	}
}
