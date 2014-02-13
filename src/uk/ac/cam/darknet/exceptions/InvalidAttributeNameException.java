package uk.ac.cam.darknet.exceptions;

/**
 * This exception is thrown if an invalid attribute name is encountered at some point during the
 * system execution execution.
 * 
 * @author Ibtehaj Nadeem
 */
public class InvalidAttributeNameException extends Exception {
	private static final long	serialVersionUID	= 1L;

	/**
	 * Creates a new <code>InvalidAttributeNameException</code> with the specified message.
	 * 
	 * @param message
	 *            The error message.
	 */
	public InvalidAttributeNameException(String message) {
		super(message);
	}
}
