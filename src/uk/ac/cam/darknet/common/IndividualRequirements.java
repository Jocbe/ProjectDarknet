package uk.ac.cam.darknet.common;

import java.util.Hashtable;
import uk.ac.cam.darknet.exceptions.InvalidReliabilityException;

/**
 * When an effect requests individuals, it may want to specify a set of strictly required attributes
 * that each individual has to have. These requirements can express something like
 * "individuals must have a photo" or
 * "individuals must have both Facebook and Twitter account information." The requirements specify
 * which individuals will be selected when querying the database manager.
 * 
 * @author Ibtehaj Nadeem
 */
public class IndividualRequirements {
	private final Show										show;
	private final Hashtable<AttributeCategories, Double>	requiredCategories;

	/**
	 * Create a new set of requirements for an effect. The individuals returned from the database
	 * manager with these requirements will all be attendees of the provided show.
	 * 
	 * @param show
	 *            The show that the individuals attend.
	 * @param globalAttributeTable
	 */
	public IndividualRequirements(Show show) {
		this.show = show;
		this.requiredCategories = new Hashtable<AttributeCategories, Double>();
	}

	/**
	 * Add a certain attribute category as a requirement. The minimum reliability must be between 0
	 * and 1.
	 * 
	 * @param category
	 *            The attribute category required.
	 * @param minimumReliability
	 *            The minimum reliability for the category.
	 * @throws InvalidReliabilityException
	 */
	public void addRequirement(AttributeCategories category, double minimumReliability) throws InvalidReliabilityException {
		if (minimumReliability < 0 || minimumReliability > 1)
			throw new InvalidReliabilityException(Strings.INVALID_RELIABILITY_EXN);
		requiredCategories.put(category, minimumReliability);
	}

	/**
	 * Returns the show associated with these requirements.
	 * 
	 * @return The show associated with these requirements.
	 */
	public Show getShow() {
		return show;
	}
	
	/**
	 * Returns the underlying hashtable of required categories.
	 * 
	 * @return The underlying hashtable of required categories
	 */
	public Hashtable<AttributeCategories, Double> getRequiredCategories() {
		return requiredCategories;
	}
}