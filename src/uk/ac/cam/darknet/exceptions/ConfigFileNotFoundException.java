package uk.ac.cam.darknet.exceptions;

/**
 * This exception is thrown when the configuration file containing the required access information
 * to establish a connection to the database cannot be found.
 * 
 * @author Ibtehaj Nadeem
 */
public class ConfigFileNotFoundException extends Exception {
	private static final long	serialVersionUID	= 1L;

	/**
	 * Creates a new <code>ConfigFileNotFoundException</code> with the specified message.
	 * 
	 * @param message
	 *            The error message.
	 */
	public ConfigFileNotFoundException(String message) {
		super(message);
	}
}
