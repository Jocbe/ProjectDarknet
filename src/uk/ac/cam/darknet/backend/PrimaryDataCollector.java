package uk.ac.cam.darknet.backend;

import uk.ac.cam.darknet.database.DatabaseManager;

/**
 * Primary data collectors collect reliable data from ticket booking systems (such as Spektrix) or
 * manually inserted data. They are used to enter the basic details of individuals. The data entered
 * serves as search parameters for secondary collectors.
 * 
 * @author Augustin Zidek
 * 
 */
public abstract class PrimaryDataCollector implements DataCollector {
	@SuppressWarnings("unused")
	private DatabaseManager	databaseManager;

	@Override
	public String getCollectorId() {
		return this.getClass().getSimpleName();
	}

	/**
	 * Create a new primary data collector with the specified database manager.
	 * 
	 * @param databaseManager
	 *            The database manager to use to write to the database.
	 */
	public PrimaryDataCollector(DatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
	}
}
