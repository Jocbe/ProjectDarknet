package uk.ac.cam.darknet.backend;

import java.io.File;
import java.util.List;

import uk.ac.cam.darknet.database.DatabaseManager;
import uk.ac.cam.darknet.common.Individual;

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
	public ManualInputDataCollector(DatabaseManager databaseManager) {
		super(databaseManager);
		// TODO Auto-generated constructor stub
	}
	

	/**
	 * Loads Primary Data from CSV file into Database
	 * 
	 * @param pathname
	 * 			path to CSV file containing primary data
	 * @return
	 * 			List of Individuals stored in Database
	 */
	public List<Individual> loadfromCSV(String pathname){
		return null;
	}

	/**
	 * Loads an Individual from ManualInputGUI into Database
	 * 
	 * @param ind
	 * 			Individual to be stored
	 * @return
	 * 			Individual stored in Database
	 */
	public Individual loadIndividual(Individual ind){
		return null;
	}
		

	@Override
	public void run() {
		// TODO Auto-generated method stub
		// note will create Individuals and store them using database manager
	}
}
