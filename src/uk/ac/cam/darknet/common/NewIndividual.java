package uk.ac.cam.darknet.common;

import java.util.Hashtable;

/**
 * This class is used by primary data collectors to add new individuals to the
 * database. It is necessary to use this subclass rather than the
 * <code>Individual</code> class directly, because the individual's ID is not
 * known before they are added to the database for the first time.
 * 
 * @author Ibtehaj Nadeem
 */
public class NewIndividual extends Individual {

	/**
	 * @param firstName
	 * @param lastName
	 * @param globalAttributeTable
	 */
	public NewIndividual(String firstName, String lastName,
			Hashtable<String, AttributeCategories> globalAttributeTable) {
		super(0, firstName, lastName, globalAttributeTable);
		// TODO Auto-generated constructor stub
	}

}
