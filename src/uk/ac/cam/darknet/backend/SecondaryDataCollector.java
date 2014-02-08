package uk.ac.cam.darknet.backend;

import java.util.List;

import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.common.Properties;
import uk.ac.cam.darknet.database.DatabaseManager;

/**
 * Secondary data collectors harvest data from various sources (mostly
 * Internet), such as Facebook, Twitter, LinkedIn.
 * 
 * @author Augustin Zidek
 * 
 */
public abstract class SecondaryDataCollector implements DataCollector {
	private Properties typeTable;
	private DatabaseManager databaseManager;

	@Override
	public abstract void run();

	@Override
	public abstract void setup(List<Individual> individuals);

	@Override
	public abstract Properties getTypeTable();

	public SecondaryDataCollector(DatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
	}
}
