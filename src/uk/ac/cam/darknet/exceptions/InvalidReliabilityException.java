package uk.ac.cam.darknet.exceptions;

/**
 * This exception is thrown when an attempt to add an attribute to a <code>Properties</code> object
 * with reliability less than 0.0 or greater than 1.0 is made.
 * 
 * @author Ibtehaj Nadeem
 */
public class InvalidReliabilityException extends Exception {
	private static final long	serialVersionUID	= 1L;

	/**
	 * Creates a new <code>InvalidReliabilityException</code> with the specified message.
	 * 
	 * @param message
	 *            The error message.
	 */
	public InvalidReliabilityException(String message) {
		super(message);
	}
}
