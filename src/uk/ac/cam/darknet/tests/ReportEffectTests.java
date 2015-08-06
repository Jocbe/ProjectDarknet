package uk.ac.cam.darknet.tests;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;

import org.junit.Test;

import uk.ac.cam.darknet.common.AttributeCategories;
import uk.ac.cam.darknet.common.Show;
import uk.ac.cam.darknet.database.SecondaryDatabaseManager;
import uk.ac.cam.darknet.exceptions.ConfigFileNotFoundException;
import uk.ac.cam.darknet.exceptions.InvalidAttributeNameException;
import uk.ac.cam.darknet.frontend.ReportEffect;

/**
 *
 */
public class ReportEffectTests {
	@SuppressWarnings("javadoc")
	@Test
	public void testReportEffect() throws ClassNotFoundException, ConfigFileNotFoundException, IOException, SQLException, InvalidAttributeNameException {
		Hashtable<String, AttributeCategories> globalAttributeTable = new Hashtable<String, AttributeCategories>();
		globalAttributeTable.put("fb_photo", AttributeCategories.PHOTO);
		globalAttributeTable.put("fb_gender", AttributeCategories.GENDER);
		globalAttributeTable.put("fb_locale", AttributeCategories.LOCALE);
		globalAttributeTable.put("fb_birthday", AttributeCategories.BIRTHDAY);
		globalAttributeTable.put("fb_relationshipStatus", AttributeCategories.RELATIONSHIP_STATUS);
		SecondaryDatabaseManager dm = new SecondaryDatabaseManager(
				globalAttributeTable);
		ReportEffect effect = new ReportEffect(dm);
		String[] files = {"", "reportTest1"};
		effect.setup(files);
		Show show = dm.getAllShows().get(2);
		effect.execute(show);
		
	}
	@SuppressWarnings("javadoc")
	@Test
	public void testReportEffect2() throws ClassNotFoundException, ConfigFileNotFoundException, IOException, SQLException, InvalidAttributeNameException {
		Hashtable<String, AttributeCategories> globalAttributeTable = new Hashtable<String, AttributeCategories>();
		globalAttributeTable.put("fb_photo", AttributeCategories.PHOTO);
		globalAttributeTable.put("fb_gender", AttributeCategories.GENDER);
		globalAttributeTable.put("fb_locale", AttributeCategories.LOCALE);
		globalAttributeTable.put("fb_birthday", AttributeCategories.BIRTHDAY);
		globalAttributeTable.put("fb_relationshipStatus", AttributeCategories.RELATIONSHIP_STATUS);
		SecondaryDatabaseManager dm = new SecondaryDatabaseManager(
				globalAttributeTable);
		ReportEffect effect = new ReportEffect(dm);
		String[] files = {"", "reportTest2"};
		effect.setup(files);
		Show show = dm.getAllShows().get(6);
		effect.execute(show);
		
	}
	@SuppressWarnings("javadoc")
	@Test
	public void testReportEffect3() throws ClassNotFoundException, ConfigFileNotFoundException, IOException, SQLException, InvalidAttributeNameException {
		Hashtable<String, AttributeCategories> globalAttributeTable = new Hashtable<String, AttributeCategories>();
		globalAttributeTable.put("fb_photo", AttributeCategories.PHOTO);
		globalAttributeTable.put("fb_gender", AttributeCategories.GENDER);
		globalAttributeTable.put("fb_locale", AttributeCategories.LOCALE);
		globalAttributeTable.put("fb_birthday", AttributeCategories.BIRTHDAY);
		globalAttributeTable.put("fb_relationshipStatus", AttributeCategories.RELATIONSHIP_STATUS);
		SecondaryDatabaseManager dm = new SecondaryDatabaseManager(
				globalAttributeTable);
		ReportEffect effect = new ReportEffect(dm);
		String[] files = {"", "reportTest3"};
		effect.setup(files);
		Show show = dm.getAllShows().get(48);
		effect.execute(show);
		
	}
}
