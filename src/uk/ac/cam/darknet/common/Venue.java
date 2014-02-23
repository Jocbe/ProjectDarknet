package uk.ac.cam.darknet.common;

/**
 * This class represents a venue.
 * 
 * @author Ibtehaj Nadeem
 */
public class Venue {
	private final int		id;
	private final String	name;

	/**
	 * Create a new <code>Venue</code> object with the specified ID and name.
	 * 
	 * @param id
	 *            The ID of the venue.
	 * @param name
	 *            The name of the venue.
	 */
	public Venue(int id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * Returns the ID of the venue.
	 * 
	 * @return The venue ID.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns the name of the venue.
	 * 
	 * @return The venue name.
	 */
	public String getName() {
		return name;
	}
}