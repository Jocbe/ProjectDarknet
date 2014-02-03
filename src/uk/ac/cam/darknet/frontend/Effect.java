package uk.ac.cam.darknet.frontend;

/**
 * An interface for effects. An effect is something that processes the data in a
 * certain way and returns them.
 * 
 * @author Augustin Zidek
 * 
 */
public interface Effect {

	/**
	 * Executes the given effect, which involves fetching data, processing them
	 * and returning them in a certain format.
	 */
	public void execute();

}
