package uk.ac.cam.darknet.exceptions;

/**
 * This exception is thrown when an attempt to add an attribute to a
 * <code>Properties</code> object which is not in the global attribute table is
 * made.
 * 
 * @author Ibtehaj Nadeem
 */
public class UnknownAttributeException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new <code>UnknownAttributeException</code> with the specified
	 * message.
	 * 
	 * @param message The error message.
	 */
	public UnknownAttributeException(String message) {
		super(message);
	}
}
