package uk.ac.cam.darknet.frontend;

import uk.ac.cam.darknet.common.Show;

/**
 * An interface for effects. An effect is something that queries and makes use of the data in the
 * system.
 * 
 * @author Augustin Zidek
 * 
 */
public interface Effect {

	/**
	 * Executes the given effect on the specified individuals, which involves fetching data,
	 * processing it and displaying it in some format.
	 * 
	 * @param individuals
	 *            List of individuals the effect should be performed upon, provided EffectGUI.
	 */
	public void execute(Show show);

}
