package uk.ac.cam.darknet.backend;

import java.util.Hashtable;

import uk.ac.cam.darknet.common.AttributeCategories;
import uk.ac.cam.darknet.database.DatabaseManager;

/**
 * Secondary data collectors harvest data from various sources (mostly the
 * Internet) such as Facebook, Twitter and LinkedIn.
 * 
 * @author Augustin Zidek
 * 
 */
public abstract class SecondaryDataCollector implements DataCollector {
	@SuppressWarnings("unused")
	private DatabaseManager databaseManager;
	private Hashtable<String, AttributeCategories> typeTable;

	@Override
	public Hashtable<String, AttributeCategories> getTypeTable() {
		return typeTable;
	}

	/**
	 * Create a new secondary data collector with the specified database
	 * manager.
	 * 
	 * @param databaseManager The database manager to use to write to the
	 *            database.
	 */
	public SecondaryDataCollector(DatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
	}
}
