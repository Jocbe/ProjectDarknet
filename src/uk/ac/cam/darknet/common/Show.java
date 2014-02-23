package uk.ac.cam.darknet.common;

import java.util.Date;

/**
 * This class represents a show.
 * 
 * @author Ibtehaj Nadeem
 */
public class Show {
	private final Date	date;
	private final Venue	venue;

	/**
	 * Create a new <code>Show</code> object with the specified date and venue.
	 * 
	 * @param date
	 *            The date and time of the show.
	 * @param venue
	 *            The venue of the show.
	 */
	public Show(Date date, Venue venue) {
		this.date = date;
		this.venue = venue;
	}

	/**
	 * Returns the date of the show.
	 * 
	 * @return The show date.
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Returns the venue of the show.
	 * 
	 * @return The show venue.
	 */
	public Venue getVenue() {
		return venue;
	}
}
