package uk.ac.cam.darknet.backend;

import java.util.List;

import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.common.Properties;
import uk.ac.cam.darknet.database.DatabaseManager;

/**
 * Collects data inputed by the user of the system.
 * 
 * @author Augustin Zidek
 * 
 */
public class ManualInputDataCollector extends PrimaryDataCollector {
	public ManualInputDataCollector(DatabaseManager databaseManager) {
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
}
