package uk.ac.cam.darknet.tests;

import org.junit.Test;

import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import uk.ac.cam.darknet.backend.TwitterDataCollector;

public class TwitterTests {
	//@Test
	public void getFollowers() {
		try {
			Twitter twitter = new TwitterFactory().getInstance();
			long cursor = -1;
			IDs ids;
			System.out.println("Listing follower's ids");
			
			String name = "johnsmith";
			
			//ids = twitter.getFollowersIDs(names, cursor);
			
			do {
                ids = twitter.getFollowersIDs(name, cursor);
                
                for (long id : ids.getIDs()) {
                    System.out.println(id);
                }
            } while ((cursor = ids.getNextCursor()) != 0);
		} catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get followers' ids: " + te.getMessage());
        }
	}
	
	@Test
	public void status() {
		TwitterDataCollector dc = new TwitterDataCollector(null);
		dc.run();
	}
}