package uk.ac.cam.darknet.tests;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import uk.ac.cam.darknet.backend.FacebookDataCollector;
import uk.ac.cam.darknet.common.AttributeCategories;
import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.database.PrimaryDatabaseManager;
import uk.ac.cam.darknet.database.SecondaryDatabaseManager;
import uk.ac.cam.darknet.exceptions.ConfigFileNotFoundException;
import uk.ac.cam.darknet.exceptions.InvalidAttributeNameException;

/**
 * 
 * Test whether the FacebookDataCollector works as desired
 * 
 * @author The Doctor
 *
 */
public class FacebookTests {
	
	/**
	 * 
	 * Main test for the FacebookDataCollector
	 * 
	 * @throws ClassNotFoundException
	 * @throws ConfigFileNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 * @throws InvalidAttributeNameException
	 */
	@Test
	public void initializeAndRunFacebookDataCollector() throws ClassNotFoundException, ConfigFileNotFoundException, IOException, SQLException, InvalidAttributeNameException {
		FacebookDataCollector fbdc = new FacebookDataCollector(null);
		
		Hashtable<String, AttributeCategories> atts = fbdc.getAttributeTable();
		
		System.out.println("Trying to create sdbm...");
		SecondaryDatabaseManager sdbm = new SecondaryDatabaseManager(atts);
		
		System.out.println("Trying to create fbdc...");
		fbdc = new FacebookDataCollector(sdbm);
		
		List<Individual> is = new LinkedList<Individual>();
		
		Date eventDate = new Date(100000L);
		System.out.println("Populating sample target list");
		is.add(Individual.getNewIndividual("Fizz", "Sang", "", eventDate, 1, "fb_test", atts));
		is.add(Individual.getNewIndividual("Ellie", "Rbnsn", "", eventDate, 1, "fb_test", atts));
		is.add(Individual.getNewIndividual("Hayley", "Kasperczyk", "", eventDate, 1, "fb_test", atts));
		is.add(Individual.getNewIndividual("Rose", "Lewenstein", "", eventDate, 1, "fb_test", atts));
		is.add(Individual.getNewIndividual("William", "Drew", "", eventDate, 1, "fb_test", atts));
		is.add(Individual.getNewIndividual("Wendy", "Kibble", "", eventDate, 1, "fb_test", atts));
		is.add(Individual.getNewIndividual("Tassos", "Stevens", "", eventDate, 1, "fb_test", atts));
		is.add(Individual.getNewIndividual("Wolfram", "Kosch", "", eventDate, 1, "fb_test", atts));
		
		System.out.println("Trying to create pdbm");
		PrimaryDatabaseManager pdbm = new PrimaryDatabaseManager(atts);
		System.out.println("Trying to store individuals...");
		pdbm.storeIndividual(is);
		
		is = pdbm.getBySeat("fb_test");
		
		System.out.println("Now setting up fbdc...");
		fbdc.setup(is);
		
		System.out.println("...and running it.");
		try{
			fbdc.run();
		} catch(Exception e) {
			System.err.println(e.getClass().getSimpleName() + " while trying to run FacebookDataCollector! Message: "
					+ e.getMessage());
			Assert.fail();
		} finally {
		
			System.out.println("Cleaning up...");
			pdbm.closeConnection();
			sdbm.closeConnection();
			
			System.out.println("Done.");
		}
	}
}
