package uk.ac.cam.darknet.frontend;

import java.util.List;

import uk.ac.cam.darknet.common.Individual;

/**
 * This class queries the database via a database manager to find the most
 * suitable individuals for an effect. All the data requested is added to the
 * respective individual's <code>Properties</code> object.
 * 
 * This class contains overloaded methods suitable for working either with one
 * individual at a time, or with a list of individuals.
 * 
 * @author Augustin Zidek
 * 
 */
public class DataProvider {
	// TODO: Need to add methods for each global attribute type.
	/**
	 * Add all the data held in the database associated with the given
	 * individual. This includes data from each secondary data collector.
	 * 
	 * @param individual The individual to look up.
	 */
	public void getAllData(final Individual individual) {
		// TODO
	}

	/**
	 * Add all the data held in the database associated with each individual in
	 * the list. This includes data from each secondary data collector.
	 * 
	 * @param individuals The individuals to look up.
	 */
	public void getAllData(final List<Individual> individuals) {
		// TODO
	}

	/**
	 * Add one specific attribute to the individual.
	 * 
	 * @param individual The individual to look up.
	 * @param attributeName The name of the attribute to add to the properties.
	 */
	public void getSpecificAttribute(final Individual individual,
			String attributeName) {
		// TODO
	}

	/**
	 * Add one specific attribute to each individual.
	 * 
	 * @param individuals The individuals to look up.
	 * @param attributeName The name of the attribute to add to the properties.
	 */
	public void getSpecificAttribute(final List<Individual> individuals,
			String attributeName) {
		// TODO
	}
}
