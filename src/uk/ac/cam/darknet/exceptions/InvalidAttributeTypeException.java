package uk.ac.cam.darknet.exceptions;

/**
 * This exception is thrown when the type of the object to be added to an
 * individual's <code>Properties</code> does not match the expected type for the
 * attribute as specified by <code>AttributeCategories</code>.
 * 
 * @author Ibtehaj Nadeem
 */
public class InvalidAttributeTypeException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new <code>InvalidAttributeTypeException</code> with the
	 * specified message.
	 * 
	 * @param message The error message.
	 */
	public InvalidAttributeTypeException(String message) {
		// TODO Auto-generated constructor stub
	}
}
