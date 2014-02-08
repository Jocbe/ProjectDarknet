package uk.ac.cam.darknet.common;

import java.util.Properties;

/**
 * A class for storing a person with its data, such as name, date of birth,
 * email address, etc.
 * 
 * @author Augustin Zidek
 * 
 */
public class Individual {
	private Properties properties;
	private final int id;

	/**
	 * Creates a new individual. The attributes of the individual should match their corresponding
	 * fields in the primary table in the database.
	 * 
	 * @param id The unique ID for the individual. This should be equal to the individual's primary key in the database.
	 */
	public Individual(int id) {
		this.id = id;
	}
	
	/**
	 * Get the unique ID of this individual. This should be equal to its primary key in the database.
	 * 
	 * @return Returns the unique user ID of the individual.
	 */
	public int getId() {
		return id;
	}
}