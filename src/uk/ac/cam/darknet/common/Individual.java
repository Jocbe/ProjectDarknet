package uk.ac.cam.darknet.common;

/**
 * A class for storing a person with their data. The common attribute types that
 * are provided by the primary data collectors are available as getters. All the
 * data coming from secondary collectors is stored in a <code>Properties</code>
 * object that belongs to the individual. Note that each attribute type
 * available is prefixed with the unique ID of the corresponding collector to
 * avoid name clashes.
 * 
 * @author Augustin Zidek
 * 
 */
public class Individual {
	private final Properties properties;
	private final int id;
	private final String firstName;
	private final String lastName;

	/**
	 * Creates a new individual. The attributes of the individual should match
	 * their corresponding fields in the primary table in the database.
	 * 
	 * @param id The unique ID for the individual. This should be equal to the
	 *            individual's primary key in the database.
	 * @param firstName The individual's first name.
	 * @param lastName The individual's last name.
	 */
	public Individual(int id, String firstName, String lastName) {
		// TODO: Add all the primary attributes.
		// Possible list (to be confirmed):
		// Email
		// Date booked
		// Tickets booked
		// Phone number
		// Etc.
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.properties = new Properties();
	}

	/**
	 * Get the unique ID of this individual. This should be equal to its primary
	 * key in the database.
	 * 
	 * @return Returns the unique user ID of the individual.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Get the individual's first name.
	 * 
	 * @return The individual's first name.
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Get the individual's last name.
	 * 
	 * @return The individual's last name.
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * This method returns the individual's <code>Properties</code> object. The
	 * exact attributes contained within depends on what the effect using this
	 * individual has requested.
	 * 
	 * @return The <code>Properties</code> object holding attributes about the
	 *         individual.
	 */
	public Properties getProperties() {
		return properties;
	}
}