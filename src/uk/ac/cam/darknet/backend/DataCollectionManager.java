package uk.ac.cam.darknet.backend;

import java.lang.Thread;
import java.util.Hashtable;
import java.util.Set;
import java.lang.Class;
import java.lang.reflect.Constructor;

import uk.ac.cam.darknet.common.AttributeCategories;
import uk.ac.cam.darknet.database.*;
import uk.ac.cam.darknet.gui.DataCollectorGUI;
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

  private int threadCount;
  private DataCollectorGUI mpdc;
  private PrimaryDatabaseManager pdm;
  private SecondaryDatabaseManager sdm;
  private Hashtable<String, AttributeCategories> globalAttributeTable;

  public void DataCollectionManager() {
    globalAttributeTable = new Hashtable<String, AttributeCategories>();
    pdm = new PrimaryDatabaseManager(globalAttributeTable);
    sdm = new SecondaryDatabaseManager(globalAttributeTable);
    threadCount = 0;
  }

  // Method to run secondary data collector in new thread.
  void startCollector(Class<SecondaryDataCollector> sdcObject) {
    // Obtain the constructor object for the class object supplied.
    Class[] args = new Class[1];
    args[0] = sdm.getClass();
    final Constructor<SecondaryDataCollector> sdcCstr = sdcObject.getConstructor(args);
    Thread t = new Thread() {
      @Override
      public void run() {
        // Create a new instance of the secondary data collector corresponding to sdcObject.
        final SecondaryDataCollector sdc = sdcCstr.newInstance(sdm);
        // TODO where to get the individuals for setting up??
        // TODO Setup the collector.
        // TODO Query - will this run() method interfere with "public void run()" above?
        sdc.run();
      }
    };
    t.start();
    // Update the thread counter.
    threadCount++;
  }
   
  // Update the globalAttributeTable with all attributes found in the specified
  // SecondaryDataCollector.
  private void updateTable(SecondaryDataCollector sdc) {

    // String to prefix each attribute name with before adding to the 
    // globalAttributeTable. 
    String collectorId = sdc.getCollectorId()+"_";

    // Get the table of attributes specific to this data collector.
    Hashtable<String, AttributeCategories> tempAttributeTable = sdc.getAttributeTable();

    // Generate a set of all keys in tempAttributeTable.
    Set<String> keys = tempAttributeTable.keySet();

    // Prefix each key with collectorId before adding its Hashtable entry to
    // the globalAttributeTable.
    for (String key : keys) {
      globalAttributeTable.put(collectorId+key, tempAttributeTable.get(key));
    }
  } 

	public static void main(String[] args) {

    // Create instance of DataCollectionManager from which to launch everything else.
    final DataCollectionManager dcm = new DataCollectionManager();

    // Populate globalAttributeTable.
    // More elegant way desired, but so far reflection seems like it will not work.
    FacebookDataCollector fdc = new FacebookDataCollector(dcm.sdm);
    dcm.updateTable(fdc);
    TwitterDataCollector tdc = new TwitterDataCollector(dcm.sdm);
    dcm.updateTable(tdc);

    // Create the ManualPrimaryDataCollector in a new thread..
    Thread guiThread = new Thread() {
      @Override
      public void run() {
        dcm.mpdc = new DataCollectorGUI(dcm.pdm);
        dcm.mpdc.run();
      }
    };
    guiThread.start();

    // TODO action after guiThread is finished.
	}
}
