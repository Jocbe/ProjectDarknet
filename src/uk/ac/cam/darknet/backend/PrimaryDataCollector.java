package uk.ac.cam.darknet.backend;

import java.util.Hashtable;
import java.util.List;

import uk.ac.cam.darknet.common.AttributeCategories;
import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.database.DatabaseManager;

/**
 * Primary data collectors collect reliable data from ticket booking systems
 * (such as Spektrix) or manually inserted data. They are used to enter the
 * basic details of individuals. The data entered serves as search parameters
 * for secondary collectors.
 * 
 * @author Augustin Zidek
 * 
 */
public abstract class PrimaryDataCollector implements DataCollector {
	@SuppressWarnings("unused")
	private DatabaseManager databaseManager;
	private Hashtable<String, AttributeCategories> typeTable;

	@Override
	public void setup(List<Individual> individuals) {
		return;
	}

	@Override
	public Hashtable<String, AttributeCategories> getTypeTable() {
		return typeTable;
	}

	/**
	 * Create a new primary data collector with the specified database manager.
	 * 
	 * @param databaseManager The database manager to use to write to the
	 *            database.
	 */
	public PrimaryDataCollector(DatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
	}
}
