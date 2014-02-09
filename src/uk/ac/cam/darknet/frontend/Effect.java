package uk.ac.cam.darknet.frontend;

/**
 * An interface for effects. An effect is something that queries and makes use
 * of the data in the system.
 * 
 * @author Augustin Zidek
 * 
 */
public interface Effect {

	/**
	 * Executes the given effect, which involves fetching data, processing it
	 * and displaying it in some format.
	 */
	public void execute();

}
