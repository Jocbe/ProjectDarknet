package uk.ac.cam.darknet.tests;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Hashtable;

import junit.framework.Assert;

import org.junit.Test;

import uk.ac.cam.darknet.common.AttributeCategories;
import uk.ac.cam.darknet.common.Show;
import uk.ac.cam.darknet.database.SecondaryDatabaseManager;
import uk.ac.cam.darknet.exceptions.ConfigFileNotFoundException;
import uk.ac.cam.darknet.exceptions.InvalidAttributeNameException;
import uk.ac.cam.darknet.frontend.PictureWallEffect;

public class PictureWallEffectTests {

	@Test
	public void test() throws ClassNotFoundException, ConfigFileNotFoundException, IOException, SQLException, InvalidAttributeNameException {
		Hashtable<String, AttributeCategories> globalAttributeTable = new Hashtable<String, AttributeCategories>();
		globalAttributeTable.put("fb_photo", AttributeCategories.PHOTO);
		SecondaryDatabaseManager dm = new SecondaryDatabaseManager(
				globalAttributeTable);
		PictureWallEffect pwe = new PictureWallEffect(dm);
		String[] files = {"", "pictureTest"};
		pwe.setup(files);
		Show show = dm.getAllShows().get(2);
		pwe.execute(show);
		Assert.assertTrue(true);
	}

}
