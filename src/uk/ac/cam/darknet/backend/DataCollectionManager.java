package uk.ac.cam.darknet.backend;

import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.Set;
import java.util.List;

import uk.ac.cam.darknet.common.AttributeCategories;
import uk.ac.cam.darknet.common.EffectsAndCollectorsLoader;
import uk.ac.cam.darknet.database.PrimaryDatabaseManager;
import uk.ac.cam.darknet.database.SecondaryDatabaseManager;
import uk.ac.cam.darknet.gui.DataCollectorGUI;

/**
 * Handles and coordinates the data collection. Responsible for creating the
 * global attribute table and appropriate database managers. Calls the primary
 * data collectors and then selected secondary data collectors.
 * 
 * @author Josh Treon
 */
public class DataCollectionManager {
	/**
	 * 
	 * @param args
	 */

	private DataCollectorGUI mpdc;
	private PrimaryDatabaseManager pdm;
	private SecondaryDatabaseManager sdm;
	private Hashtable<String, AttributeCategories> globalAttributeTable;

	public void DataCollectionManager() {
		globalAttributeTable = new Hashtable<String, AttributeCategories>();
		pdm = new PrimaryDatabaseManager(globalAttributeTable);
		sdm = new SecondaryDatabaseManager(globalAttributeTable);
	}

	// Update the globalAttributeTable with all attributes found in the
	// specified
	// SecondaryDataCollector.
	private void updateTable(SecondaryDataCollector sdc) {

		// String to prefix each attribute name with before adding to the
		// globalAttributeTable.
		String collectorId = sdc.getCollectorId() + "_";

		// Get the table of attributes specific to this data collector.
		Hashtable<String, AttributeCategories> tempAttributeTable = sdc
				.getAttributeTable();

		// Generate a set of all keys in tempAttributeTable.
		Set<String> keys = tempAttributeTable.keySet();

		// Prefix each key with collectorId before adding its Hashtable entry to
		// the globalAttributeTable.
		for (String key : keys) {
			globalAttributeTable.put(collectorId + key,
					tempAttributeTable.get(key));
		}
	}

	public static void main(String[] args) {

		// Create instance of DataCollectionManager from which to launch
		// everything else.
		final DataCollectionManager dcm = new DataCollectionManager();

    // Populate globalAttributeTable by retrieving attributes from each
    // SecondaryDataCollector.
    List<Class<?>> secondaryCollectors = EffectsAndCollectorsLoader
        .loadSecondaryCollectors();
    Constructor<SecondaryDataCollector> sdcCstr; 

		// Obtain the constructor object for the class object supplied.
		Class[] c = new Class[1];
		c[0] = dcm.sdm.getClass();
    for (Class<?> sClass : secondaryCollectors) {
      // Create an instance of the SecondaryDataCollector.
		  sdcCstr = sClass.getConstructor(c);
		  SecondaryDataCollector sdc = sdcCstr.newInstance(dcm.sdm);
      dcm.updateTable(sdc);
    }

		// Create the PrimaryDataCollector.
    dcm.mpdc = new DataCollectorGUI(dcm.pdm, dcm.sdm);
    dcm.mpdc.run();

	}
}
