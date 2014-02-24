package uk.ac.cam.darknet.exceptions;

/**
 * This exception is thrown when the database manager cannot satisfy a certain request (in the form
 * of <code>IndividualRequirements</code>) for individuals made by an effect. This may occur if
 * there are no attributes of a certain category, if the minimum reliabilities are too high or if no
 * individual has all the required attributes.
 * 
 * @author Ibtehaj Nadeem
 */
public class RequestNotSatisfiableException extends Exception {
	private static final long	serialVersionUID	= 1L;

	/**
	 * Creates a new <code>RequestNotSatisfyableException</code> with the specified message.
	 * 
	 * @param message
	 *            The error message.
	 */
	public RequestNotSatisfiableException(String message) {
		super(message);
	}
}
