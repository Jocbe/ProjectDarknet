package uk.ac.cam.darknet.backend;

/**
 * An interface for data collectors. Data collectors harvest data from the
 * Internet, parse/process it and save it in a database.
 * 
 * @author Augustin Zidek
 */
public interface DataCollector extends Runnable {
	/**
	 * Each collector must have a unique ID across the system. The IDs should
	 * only contain letters and numbers and are case-insensitive. If two
	 * collectors claim the same ID, the data collection manager may refuse to
	 * use them.
	 * 
	 * @return The unique ID of the collector.
	 */
	public String getCollectorId();
}
