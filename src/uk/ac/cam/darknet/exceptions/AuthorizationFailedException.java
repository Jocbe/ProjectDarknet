package uk.ac.cam.darknet.exceptions;


/**
 * Thrown when authorization to a service (ex. Facebook fails)
 * 
 * @author Johann Beleites
 *
 */
public class AuthorizationFailedException extends Exception {
	
	private static final long serialVersionUID = 3529430832714064305L;
	@SuppressWarnings("javadoc")
	public AuthorizationFailedException() {super();}
	@SuppressWarnings("javadoc")
	public AuthorizationFailedException(String message) {
		super(message);
	}
}
