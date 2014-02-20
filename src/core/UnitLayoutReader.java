package core;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * A Class for reading unit/item locations
 * @author aschmid
 *
 */
public class UnitLayoutReader {
	/**
	 * Reads and loads the given file
	 * @param file The name of the file to load, with no extension, location is data/file.txt
	 */
	public static void read(String file) {
		BufferedReader f;
		
		try {
			// Declare string
			String line;
			
			// Load up the file
			f = new BufferedReader(new FileReader("data/"+file+".txt"));
			
			// Process input
			while ((line = f.readLine()) != null) {
				String[] secs = line.split("\t");
				
				// We need at least two sections
				if(secs.length < 2) continue;
				
				// Grab the name of the unit
				String name = secs[0];
				
				// Grab the data
				String data = secs[secs.length-1];
				
				// Grab the two coords
				String[] coords = data.split(", ");
				
				// Make sure we got two coords
				if(coords.length != 2) continue;
				
				// Grab coordinates
				float posX = Float.parseFloat(coords[0]);
				float posY = Float.parseFloat(coords[1]);
				
				// Create the unit
				Game.getWorld().createUnit(name, posX, posY);
			}
			
			// Close input
			f.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
