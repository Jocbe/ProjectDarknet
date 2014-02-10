package uk.ac.cam.darknet.common;

import java.util.Hashtable;

/**
 * A class for storing a person with their data. The common attribute types that
 * are provided by the primary data collectors are available as getters. All the
 * data coming from secondary collectors is stored in a <code>Properties</code>
 * object that belongs to the individual. Note that each attribute type
 * available is prefixed with the unique ID of the corresponding collector to
 * avoid name clashes.
 * 
 */
public class Individual {
	private final Properties properties;
	private final int id;
	private final String firstName;
	private final String lastName;
	private final String email;

	/**
	 * Creates a new individual. The parameters should match the corresponding
	 * fields in the primary table of the database.
	 * 
	 * @param id The unique ID for the individual. This should be equal to the
	 *            individual's primary key in the database.
	 * @param firstName The individual's first name.
	 * @param lastName The individual's last name.
	 * @param email The individual's email.
	 * @param globalAttributeTable The global table mapping all available
	 *            attribute names to their global attribute types.
	 */
	public Individual(final int id, final String firstName,
			final String lastName, final String email,
			Hashtable<String, AttributeCategories> globalAttributeTable) {
		// TODO: Add all the primary attributes.
		/*
		 * Event Name, Event Date/Time, Seat, Price, Ticket Type, Date
		 * Confirmed, Sales Channel
		 */
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.properties = new Properties(globalAttributeTable);
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
	 * Get the individual's email address.
	 * 
	 * @return The individual's email address (in the standard form, i.e.
	 *         user@example.org)
	 */
	public String getEmail() {
		return email;
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