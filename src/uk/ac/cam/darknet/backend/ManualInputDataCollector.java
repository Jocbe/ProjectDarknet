package uk.ac.cam.darknet.backend;

import java.io.File;
import uk.ac.cam.darknet.database.DatabaseManager;

/**
 * This class is a primary data collector with a graphical user interface allowing manual data
 * input. It represents the primary way of entering data into the system. This collector supports
 * reading comma-separated values (CSV) files generated by booking systems.
 * 
 * @author Augustin Zidek
 */
public class ManualInputDataCollector extends PrimaryDataCollector {
	/**
	 * Create a new manual data collector with the specified database manager.
	 * 
	 * @param databaseManager
	 *            The database manager to use to write to the database.
	 * @param dataInput
	 *            The file which has the input data.
	 */
	public ManualInputDataCollector(DatabaseManager databaseManager, File dataInput) {
		super(databaseManager);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		// note will create Individuals and store them using database manager
	}
}
