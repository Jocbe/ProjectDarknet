package uk.ac.cam.darknet.backend;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Arrays;
import uk.ac.cam.darknet.common.AttributeCategories;
import uk.ac.cam.darknet.database.*;
/**
 * Handles and coordinates the data collection. Responsible for creating the global attribute table
 * and appropriate database managers. Calls the primary data collectors and then selected secondary
 * data collectors.
 * 
 * @author Josh Treon
 */
public class DataCollectionManager {
	/**
	 * 
	 * @param args
	 */

  private ManualPrimaryDataCollector mpdc;
  private SecondaryDataCollector tempsdc;
  private PrimaryDatabaseManager pdm;
  private SecondaryDatabaseManager sdm;
  private List<String> argList;
  private List<SecondaryDataCollector> sdcsToRun;
  private Hashtable<String, AttributeCategories> globalAttributeTable = new Hashtable<String, AttributeCategories>();
   
  // Update the globalAttributeTable with all attributes found in the specified
  // SecondaryDataCollector.
  private void updateTable(SecondaryDataCollector sdc) {

    // String to prefix each attribute name with before adding to the 
    // globalAttributeTable. 
    String collectorId = sdc.getCollectorId()+"_";

    // Get the table of attributes specific to this data collector.
    Hashtable<String, AttributeCategories> tempAttributeTable = sdc.getAttributeTable();

    // Generate a set of all keys in tempAttributeTable.
    Set<String> keys = sdc.keySet();

    // Prefix each key with collectorId before adding its Hashtable entry to
    // the globalAttributeTable.
    for (String key : keys) {
      globalAttributeTable.put(collectorId+key, tempAttributeTable.get(key));
    }
  } 

	public static void main(String[] args) {

    pdm = new PrimaryDatabaseManager(globalAttributeTable);
    sdm = new SecondaryDatabaseManager(globalAttributeTable);

    // Create the ManualPrimaryDataCollector.
    mpdc = new ManualPrimaryDataCollector(pdm);

    // Convert args to a list.
    argList = Arrays.asList(args);

    // Create secondary data collectors according to the arguments present.
    // Add an if statement for each new secondary data collector added to the system.

    // Facebook.
    if (argList.contains("-f")) {
      FacebookDataCollector fdc = new FacebookDataCollector(globalAttributeTable);
      sdcsToRun.add(fdc);
    }

    // Twitter.
    if (argList.contains("-t")) {
      TwitterDataCollector tdc = new TwitterDataCollector(globalAttributeTable);
      sdcsToRun.add(tdc);
    }

    // Update globalAttributeTable for each desired SecondaryDataCollector.
    for (SecondaryDataCollector sdc : sdcsToRun) {
      updateTable(sdc);
    }
    
    // Run the data collectors.
    mpdc.run();
    for (SecondaryDataCollector sdc : sdcsToRun) {
      sdc.run();
    }
	}
}
