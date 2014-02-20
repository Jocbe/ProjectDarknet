package uk.ac.cam.darknet.tests;

import org.junit.Test;

import uk.ac.cam.darknet.backend.FacebookDataCollector;

public class FacebookTests {
	@Test
	public void runFacebookDataCollector() {
		FacebookDataCollector fbdc = new FacebookDataCollector(null);
		
		fbdc.run();
	}
}
