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
	@Test
	public void runFacebookDataCollector() {
		FacebookDataCollector fbdc = new FacebookDataCollector(null);
		
		fbdc.run();
	}
	
	@Test
	public void initializeAndRunFacebookDataCollector() throws ClassNotFoundException, ConfigFileNotFoundException, IOException, SQLException, InvalidAttributeNameException {
		FacebookDataCollector fbdc = new FacebookDataCollector(null);
		
		Hashtable<String, AttributeCategories> atts = fbdc.getAttributeTable();
		
		SecondaryDatabaseManager sdbm = new SecondaryDatabaseManager(atts);
		
		fbdc = new FacebookDataCollector(sdbm);
		
		List<Individual> is = new LinkedList<Individual>();
		
		is.add(Individual.getNewIndividual("Fizz", "Sang", "", new Date(), 0, "", atts));
		is.add(Individual.getNewIndividual("Ellie", "Rbnsn", "", new Date(), 0, "", atts));
		is.add(Individual.getNewIndividual("Hayley", "Kasperczyk", "", new Date(), 0, "", atts));
		is.add(Individual.getNewIndividual("Rose", "Lewenstein", "", new Date(), 0, "", atts));
		is.add(Individual.getNewIndividual("William", "Drew", "", new Date(), 0, "", atts));
		is.add(Individual.getNewIndividual("Wendy", "Kibble", "", new Date(), 0, "", atts));
		is.add(Individual.getNewIndividual("Tassos", "Stevens", "", new Date(), 0, "", atts));
		is.add(Individual.getNewIndividual("Wolfram", "Kosch", "", new Date(), 0, "", atts));
		
		PrimaryDatabaseManager pdbm = new PrimaryDatabaseManager(atts);
		pdbm.storeIndividual(is);
		
		fbdc.setup(is);
		fbdc.run();
	}
}
