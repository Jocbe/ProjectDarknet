package uk.ac.cam.darknet.common;

import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import uk.ac.cam.darknet.exceptions.InvalidAttributeTypeException;
import uk.ac.cam.darknet.exceptions.InvalidReliabilityException;
import uk.ac.cam.darknet.exceptions.UnknownAttributeException;

/**
 * A class for storing a person with their data. The common attribute types that are provided by the
 * primary data collectors are available as getters. All the data coming from secondary collectors
 * is stored in a <code>Properties</code> object that belongs to the individual. Note that each
 * attribute type available is prefixed with the unique ID of the corresponding collector to avoid
 * name clashes.
 * 
 * @author Ibtehaj Nadeem
 */
public class Individual {
	private final Properties	properties;
	private final long			id;
	private final String		firstName;
	private final String		lastName;
	private final String		email;
	private final Date			date;
	private final int			venue;
	private final String		seat;

	/**
	 * Creates a new individual. The parameters should match the corresponding fields in the primary
	 * table of the database. This constructor should only be used by the database manager.
	 * 
	 * Only the first and last names are required. The remaining fields may be <code>null</code>.
	 * 
	 * @param id
	 *            The unique ID for the individual. This should be equal to the individual's primary
	 *            key in the database.
	 * @param firstName
	 *            The individual's first name.
	 * @param lastName
	 *            The individual's last name.
	 * @param email
	 *            The individual's email, if any.
	 * @param date
	 *            The date of the event the ticket was booked for, if any.
	 * @param venue
	 *            The ID of the venue this individual will be visiting.
	 * @param seat
	 *            The reserved seat of the booking, if any.
	 * @param globalAttributeTable
	 *            The global table mapping all available attribute names to their global attribute
	 *            types.
	 */
	public Individual(final long id, final String firstName, final String lastName, final String email, final Date date, final int venue, final String seat, Hashtable<String, AttributeCategories> globalAttributeTable) {
		// TODO: Add all the primary attributes.
		/*
		 * Event Name, Event Date/Time, Seat, Price, Ticket Type, Date Confirmed, Sales Channel
		 */
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.date = date;
		this.venue = venue;
		this.seat = seat;
		this.properties = new Properties(globalAttributeTable);
	}

	/**
	 * This method should be used by primary data collectors to obtain a new individual (with a
	 * default ID) that has to be added to the database. They should not use the constructor for
	 * this purpose. The database manager will automatically assign an ID to the new individual when
	 * storing it in the database.
	 * 
	 * Only the first and last names are required. The remaining fields may be <code>null</code>.
	 * 
	 * @param firstName
	 *            The first name of the new individual.
	 * @param lastName
	 *            The last name of the new individual.
	 * @param email
	 *            The email address of the new individual, expressed as a string.
	 * @param date
	 *            The date and time of the event the individual booked.
	 * @param venue
	 *            The ID of the venue this individual will be visiting.
	 * @param seat
	 *            The seat number of the individual booked, if any.
	 * @param globalAttributeTable
	 *            The global attribute table in the system.
	 * @return An individual suitable to be passed to <code>PrimaryDatabaseManager.store()</code>
	 *         for storage into the database.
	 */
	public static Individual getNewIndividual(String firstName, String lastName, String email, Date date, int venue, String seat, Hashtable<String, AttributeCategories> globalAttributeTable) {
		return new Individual(0, firstName, lastName, email, date, venue, seat, globalAttributeTable);
	}

	/**
	 * Get the unique ID of this individual. This should be equal to its primary key in the
	 * database.
	 * 
	 * @return Returns the unique user ID of the individual.
	 */
	public long getId() {
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
	 * @return The individual's email address (in the standard form, i.e. user@example.org)
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Get the date of the show the individual is attending.
	 * 
	 * @return The date of the event for which this individual is bought a ticket.
	 */
	public Date getEventDate() {
		return date;
	}

	/**
	 * Get the date of the show the individual is attending.
	 * 
	 * @return The date of the event for which this individual is bought a ticket.
	 */
	public int getEvenVenue() {
		return venue;
	}

	/**
	 * Get the reserved seat of the individual's booking, if any.
	 * 
	 * @return The booked seat, if any, as a string. Null otherwise.
	 */
	public String getSeat() {
		return seat;
	}

	/**
	 * This method returns the individual's <code>Properties</code> object. The exact attributes
	 * contained within depends on what the effect using this individual has requested.
	 * 
	 * @return The <code>Properties</code> object holding attributes about the individual.
	 */
	public Properties getProperties() {
		return properties;
	}

	@Override
	public boolean equals(Object otherIndividual) {
		if (otherIndividual != null && otherIndividual instanceof Individual) {
			if (((Individual) otherIndividual).getId() == this.id) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * This method adds an attribute to this <code>Properties</code> object. The key must be a valid
	 * attribute present in this object's global attribute table. If this is not the case, an
	 * <code>UnknownAttributeException</code> will be thrown. If the type of the object is not as
	 * specified by the attribute category, an <code>InvalidAttributeType</code> exception will be
	 * thrown. This method delegates the request to the underlying <code>Properties</code> object.
	 * 
	 * @param attributeName
	 *            The name of the attribute to add.
	 * @param attributeValue
	 *            The corresponding object.
	 * @param attributeReliability
	 *            The reliability of the attribute. This must be a number between 0.0 (no
	 *            confidence) and 1.0 (complete certainty).
	 * @throws UnknownAttributeException
	 * @throws InvalidAttributeTypeException
	 * @throws InvalidReliabilityException
	 */
	public void addAttribute(String attributeName, Object attributeValue, double attributeReliability) throws UnknownAttributeException, InvalidAttributeTypeException, InvalidReliabilityException {
		properties.put(attributeName, attributeValue, attributeReliability);
	}

	/**
	 * Returns a list of all the attributes stored in this object under the given name. This method
	 * delegates the request to the underlying <code>Properties</code> object.
	 * 
	 * @param attributeName
	 *            The attribute name whose associated list of values is to be returned.
	 * @return The list of values to which the specified attribute name is mapped, or null if this
	 *         map contains no mapping for the attribute.
	 */
	public List<AttributeReliabilityPair> getAttribute(String attributeName) {
		return properties.get(attributeName);
	}

	/**
	 * Removes all the attributes stored under the given key. If there are no such attributes,
	 * nothing is changed. This method delegates the request to the underlying
	 * <code>Properties</code> object.
	 * 
	 * @param attributeName
	 *            The key of the key-value pair to remove.
	 */
	public void removeAttribute(String attributeName) {
		properties.remove(attributeName);
	}

	/**
	 * Clears this individual's <code>Properties</code> object so that it contains no more
	 * attributes.
	 */
	public void clearAttributes() {
		properties.clear();
	}

	/**
	 * Tests whether a given attribute (i.e. at least one) is contained in this individual's
	 * <code>Properties</code> object.
	 * 
	 * @param attributeName
	 *            The name of the attribute whose presence should be checked.
	 * @return True if the key is contained, false otherwise.
	 */
	public boolean containsAttribute(String attributeName) {
		return properties.containsAttribute(attributeName);
	}
}