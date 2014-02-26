package uk.ac.cam.darknet.tests;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import org.junit.Test;
import uk.ac.cam.darknet.common.AttributeCategories;
import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.common.IndividualRequirements;
import uk.ac.cam.darknet.common.Show;
import uk.ac.cam.darknet.database.DatabaseManager;
import uk.ac.cam.darknet.exceptions.ConfigFileNotFoundException;
import uk.ac.cam.darknet.exceptions.InvalidAttributeTypeException;
import uk.ac.cam.darknet.exceptions.InvalidReliabilityException;
import uk.ac.cam.darknet.exceptions.RequestNotSatisfiableException;
import uk.ac.cam.darknet.exceptions.UnknownAttributeException;

public class DatabaseManagerTests {

	@Test
	public void TestDBM() throws ClassNotFoundException, ConfigFileNotFoundException, IOException, SQLException, InvalidReliabilityException, RequestNotSatisfiableException, UnknownAttributeException, InvalidAttributeTypeException {
		Hashtable<String, AttributeCategories> globalAttributeTable = new Hashtable<String, AttributeCategories>();
		globalAttributeTable.put("fb_gender", AttributeCategories.GENDER);
		globalAttributeTable.put("fb_birthday", AttributeCategories.BIRTHDAY);
		DatabaseManager instance = new DatabaseManager(globalAttributeTable);
		ArrayList<Show> shows = (ArrayList<Show>) instance.getAllShows();
		Show show = shows.get(2);
		IndividualRequirements requirements = new IndividualRequirements(show);
		requirements.addRequirement(AttributeCategories.GENDER, 0.5);
		requirements.addRequirement(AttributeCategories.BIRTHDAY, 0.5);
		ArrayList<Individual> individuals = (ArrayList<Individual>) instance.getSuitableIndividuals(requirements);
		String gender;
		String dob;
		Enumeration<String> atts;
		for (Individual i : individuals) {
			atts = i.getProperties().keys();
			System.out.println("Individual " + i.getId() + ", " + i.getFirstName() + " " + i.getLastName() + ":");
			while (atts.hasMoreElements()) {
				System.out.println(" " + atts.nextElement());
			}
			gender = (String) i.getAttribute("fb_gender").get(0).getAttribute();
			dob = (String) i.getAttribute("fb_birthday").get(0).getAttribute();
			System.out.println("Individual " + i.getId() + ", called " + i.getFirstName() + " " + i.getLastName() + ", is " + gender + " and has birthday " + dob + ".");
		}
		instance.closeConnection();
	}

}