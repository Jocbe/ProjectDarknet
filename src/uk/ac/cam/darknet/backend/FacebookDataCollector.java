package uk.ac.cam.darknet.backend;

import java.util.List;

import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.common.Properties;
import uk.ac.cam.darknet.database.DatabaseManager;

/**
 * Secondary data collector which gets data from Facebook's public API.
 * 
 * @author Augustin Zidek
 * 
 */
public class FacebookDataCollector extends SecondaryDataCollector {
	/**
	 * Create a new Facebook data collector with the specified database manager.
	 * 
	 * @param databaseManager The database manager to use to write to the
	 *            database.
	 */
	public FacebookDataCollector(DatabaseManager databaseManager) {
		super(databaseManager);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
	}

	@Override
	public void setup(List<Individual> individuals) {
		// TODO Auto-generated method stub
	}

	@Override
	public Properties getTypeTable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCollectorId() {
		// TODO Auto-generated method stub
		return null;
	}
}
