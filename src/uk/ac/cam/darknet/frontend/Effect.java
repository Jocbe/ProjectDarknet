package uk.ac.cam.darknet.frontend;

import java.util.List;

import uk.ac.cam.darknet.common.Show;
import uk.ac.cam.darknet.database.DatabaseManager;

/**
 * An interface for effects. An effect is something that queries and makes use
 * of the data in the system.
 * 
 * Abstract class constuctors
 * 
 * @author Augustin Zidek
 * 
 */
public abstract class Effect {

	DatabaseManager dm;

	/**
	 * @param dm
	 */
	public Effect(DatabaseManager dm) {
		this.dm = dm;
	}

	/**
	 * Executes the given effect on the specified individuals, which involves
	 * fetching data, processing it and displaying it in some format.
	 * 
	 * NOTE: If an effect has a setup method it must be called before execution.
	 * 
	 * @param show The show (a particular set of individual) the effect should
	 *            be performed upon, provided EffectGUI.
	 */
	public abstract void execute(Show show);

	/**
	 * Setup method to give Effect Specific arguments
	 * 
	 * @param args look at class documentation to determine appropriate String
	 *            arguments
	 */
	public abstract void setup(String[] args);

	/**
	 * Returns human understandable descriptions of the parameters that it is
	 * expecting in the setup() method. I.e. say setup() expects arguments path
	 * and token, then getSetupArgNames should return list with
	 * "path to the configuration folder", "Facebook user token".
	 * 
	 * The text should make sense when appended to "Please type in the " ...
	 * 
	 * @return Descriptions for the setup arguments
	 */
	public abstract List<String> getSetupArgDescriptions();

}
