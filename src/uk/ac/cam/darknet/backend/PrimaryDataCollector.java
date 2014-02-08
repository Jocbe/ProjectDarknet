package uk.ac.cam.darknet.backend;

import java.util.List;

import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.common.Properties;
import uk.ac.cam.darknet.database.DatabaseManager;

/**
 * Primary data collectors collect 100% reliable data from ticket systems (such
 * as Spektrix) or manually inserted data.
 * 
 * @author Augustin Zidek
 * 
 */
public abstract class PrimaryDataCollector implements DataCollector {
	private Properties typeTable;
	private DatabaseManager databaseManager;

	@Override
	public abstract void run();

	@Override
	public abstract void setup(List<Individual> individuals);

	@Override
	public abstract Properties getTypeTable();

	public PrimaryDataCollector(DatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
	}
}
