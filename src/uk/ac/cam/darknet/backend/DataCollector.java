package uk.ac.cam.darknet.backend;

/**
 * An interface for data collectors. Data collectors harvest data from the
 * Internet, parse/process it and save it in a database.
 * 
 * @author Augustin Zidek
 * 
 */
public interface DataCollector {

	/**
	 * Collects appropriate data, processes them and saves them into the
	 * database.
	 */
	public void collectData();

}
