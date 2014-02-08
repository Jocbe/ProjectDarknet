package uk.ac.cam.darknet.backend;

import java.util.List;

import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.common.Properties;

/**
 * An interface for data collectors. Data collectors harvest data from the
 * Internet, parse/process it and save it in a database.
 * 
 * @author Augustin Zidek
 * 
 */
public interface DataCollector extends Runnable {
	public void setup(List<Individual> individuals);
	public Properties getTypeTable();
}
