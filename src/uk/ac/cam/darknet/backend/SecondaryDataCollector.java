package uk.ac.cam.darknet.backend;

import java.util.Hashtable;
import java.util.List;

import uk.ac.cam.darknet.common.AttributeCategories;
import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.database.SecondaryDatabaseManager;

/**
 * Secondary data collectors harvest data from various sources (mostly the
 * Internet) such as Facebook, Twitter and LinkedIn.
 * 
 * @author Augustin Zidek
 * 
 */
public abstract class SecondaryDataCollector implements DataCollector {
	protected SecondaryDatabaseManager databaseManager;
	protected Hashtable<String, AttributeCategories> attributeTable;

	/**
	 * This method returns a table of the attributes that this collector
	 * supports. The table is a set of key-value pairs, with each key being a
	 * string equal to the name of the attribute, and each value being an
	 * <code>AttributeCategory</code>. The returned <code>Hashtable</code>
	 * object may for example contain the following key-value pairs:
	 * 
	 * <table>
	 * <col width="50%"/> <col width="50%"/> <thead>
	 * <tr>
	 * <th>Attribute Name</th>
	 * <th>Global Attribute Type</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>twitter.username</td>
	 * <td><code>GlobalAttributeType.USER_NAME</code></td>
	 * </tr>
	 * <tr>
	 * <td>twitter.displaypic</td>
	 * <td><code>GlobalAttributeType.PHOTO</code></td>
	 * </tr>
	 * <tr>
	 * <td>...</td>
	 * <td>...</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * The data collection manager will build the global attribute table by
	 * combining the attribute tables returned by each collector.
	 * 
	 * @return A <code>Hashtable</code> containing all the attributes supported
	 *         with their global types.
	 */
	public Hashtable<String, AttributeCategories> getAttributeTable() {
		// NOTE: It is impossible to make this method static and abstract at the
		// same time. That is why we have to have an implementation that is not
		// static. This means that the DataCollectionManager will operate as
		// follows:
		// 0) Create the DatabaseManager(s) with an empty global attribute
		// table.
		// 1) Create all the secondary data collectors.
		// 2) By calling the getAttributeTable() method on each one, add all the
		// attributes to the global attribute table.
		return this.attributeTable;
	}

	/**
	 * This method is used to setup the collector before it is run in its own
	 * thread. Primary data collectors do not need to implement this and should
	 * return immediately.
	 * 
	 * @param individuals The list of individuals, containing only basic
	 *            information, whose data should be looked up.
	 */
	public abstract void setup(List<Individual> individuals);

	@Override
	public String getCollectorId() {
		return this.getClass().getSimpleName();
	}

	/**
	 * Create a new secondary data collector with the specified database
	 * manager.
	 * 
	 * @param databaseManager The database manager to use to write to the
	 *            database.
	 */
	public SecondaryDataCollector(SecondaryDatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
	}
}
