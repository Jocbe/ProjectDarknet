package uk.ac.cam.darknet.tests;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import uk.ac.cam.darknet.backend.FacebookDataCollector;
import uk.ac.cam.darknet.common.AttributeCategories;
import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.database.PrimaryDatabaseManager;
import uk.ac.cam.darknet.database.SecondaryDatabaseManager;
import uk.ac.cam.darknet.exceptions.ConfigFileNotFoundException;
import uk.ac.cam.darknet.exceptions.InvalidAttributeNameException;

public class FacebookTests {
	//@Test
	public void runFacebookDataCollector() {
		FacebookDataCollector fbdc = new FacebookDataCollector(null);
		
		fbdc.run();
	}
	
	@Test
	public void initializeAndRunFacebookDataCollector() throws ClassNotFoundException, ConfigFileNotFoundException, IOException, SQLException, InvalidAttributeNameException {
		FacebookDataCollector fbdc = new FacebookDataCollector(null);
		
		Hashtable<String, AttributeCategories> atts = fbdc.getAttributeTable();
		
		System.out.println("Trying to create sdbm...");
		SecondaryDatabaseManager sdbm = new SecondaryDatabaseManager(atts);
		
		System.out.println("Trying to create fbdc...");
		fbdc = new FacebookDataCollector(sdbm);
		
		List<Individual> is = new LinkedList<Individual>();
		
		System.out.println("Populating sample target list");
		is.add(Individual.getNewIndividual("Fizz", "Sang", "", new Date(), 1, "", atts));
		is.add(Individual.getNewIndividual("Ellie", "Rbnsn", "", new Date(), 1, "", atts));
		is.add(Individual.getNewIndividual("Hayley", "Kasperczyk", "", new Date(), 1, "", atts));
		is.add(Individual.getNewIndividual("Rose", "Lewenstein", "", new Date(), 1, "", atts));
		is.add(Individual.getNewIndividual("William", "Drew", "", new Date(), 1, "", atts));
		is.add(Individual.getNewIndividual("Wendy", "Kibble", "", new Date(), 1, "", atts));
		is.add(Individual.getNewIndividual("Tassos", "Stevens", "", new Date(), 1, "", atts));
		is.add(Individual.getNewIndividual("Wolfram", "Kosch", "", new Date(), 1, "", atts));
		
		System.out.println("Trying to create pdbm");
		PrimaryDatabaseManager pdbm = new PrimaryDatabaseManager(atts);
		System.out.println("Trying to store individuals...");
		pdbm.storeIndividual(is);
		
		System.out.println("Now setting up fbdc...");
		fbdc.setup(is);
		
		System.out.println("...and running it.");
		fbdc.run();
	}
}
