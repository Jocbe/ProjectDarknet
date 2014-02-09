package uk.ac.cam.darknet.backend;

import java.util.Hashtable;
import java.util.List;

import uk.ac.cam.darknet.common.AttributeCategories;
import uk.ac.cam.darknet.common.Individual;

/**
 * An interface for data collectors. Data collectors harvest data from the
 * Internet, parse/process it and save it in a database.
 * 
 * @author Augustin Zidek
 */
public interface DataCollector extends Runnable {
	/**
	 * This method is used to setup the collector before it is run in its own
	 * thread. Primary data collectors do not need to implement this and should
	 * return immediately.
	 * 
	 * @param individuals The list of individuals, containing only basic
	 *            information, whose data should be looked up.
	 */
	public void setup(List<Individual> individuals);

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
	 * @return A <code>Properties</code> object containing all the attributes
	 *         supported with their global types.
	 */
	public Hashtable<String, AttributeCategories> getTypeTable();

	/**
	 * Each collector must have a unique ID across the system. The IDs should
	 * only contain letters and numbers and are case-insensitive. If two
	 * collectors claim the same ID, the data collection manager may refuse to
	 * use them.
	 * 
	 * @return The unique ID of the collector.
	 */
	public String getCollectorId();
}
