package uk.ac.cam.darknet.frontend;

import java.util.List;

import uk.ac.cam.darknet.common.Individual;

/**
 * Interface for all data providers, i.e. classes that get various data from
 * according tables in the database.
 * 
 * @author Augustin Zidek
 * 
 */
public interface DataProvider {

	/**
	 * Returns the first name of the given individual.
	 * 
	 * @param i The individual.
	 */
	public void getFirstName(final Individual i);

	/**
	 * Returns the date of birth of the given individual.
	 * 
	 * @param i The individual.
	 */
	public void getDoB(final Individual i);

	/**
	 * Returns the last name of the given individual.
	 * 
	 * @param i The individual.
	 */
	public void getLastName(final Individual i);

	/**
	 * Returns all data associated with the given individual.
	 * 
	 * @param i The individual.
	 * @param p The policy specifying what data should be get.
	 * 
	 */
	public void getAllData(final Individual i);

	/**
	 * Returns all data associated with each individual in the given list of
	 * individuals.
	 * 
	 * @param individualList The list of individuals.
	 * @param p The policy specifying what data should be get.
	 * 
	 */
	public void getAllData(final List<Individual> individualList);

	/**
	 * Returns the first name of each individual in the given list of
	 * individuals.
	 * 
	 * @param individualList The list of individuals.
	 */
	public void getFirstName(final List<Individual> individualList);

	/**
	 * Returns the last name of each individual in the given list of
	 * individuals.
	 * 
	 * @param individualList The list of individuals.
	 */
	public void getLastName(final List<Individual> individualList);

	/**
	 * Returns the date of birth of each individual in the given list of
	 * individuals.
	 * 
	 * @param individualList The list of individuals.
	 */
	public void getDoB(final List<Individual> individualList);

}
