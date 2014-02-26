package uk.ac.cam.darknet.backend;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import uk.ac.cam.darknet.common.AttributeCategories;
import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.database.SecondaryDatabaseManager;

/**
 * [DEMO CLASS: This class demonstrates some of the elements that need to be
 * implemented in a secondary data collector. Since it is not feasible to
 * obtain a user's Twitter handle from Spektrix it does not actually get any
 * useful information about guests.]
 *  
 * Secondary data collector which gets data from Twitter's API
 *  
 * @author Johann Beleites
 */
public class TwitterDataCollector extends SecondaryDataCollector {
	
	// This might be needed if this is to be used at some point in the future
	//private List<Individual> individuals;
	
	/**
	 * NOTE: This class is for demo-purposes only.
	 * Create a new Twitter data collector with the specified database manager.
	 * 
	 * @param databaseManager
	 *            The database manager to use to write to the database.
	 */
	public TwitterDataCollector(SecondaryDatabaseManager databaseManager) {
		super(databaseManager);
	}

	@Override
	public void run() {
		
		List<String> demoUsers = new ArrayList<String>();
		demoUsers.add("treozs");
		demoUsers.add("johnsmith");
		
		Twitter twitter = new TwitterFactory().getInstance();
		ResponseList<User> users;
		
		for(String demoName: demoUsers) {
			System.out.println("#### Searching for users with '" + demoName + "' ####");
			
			try {
				users = twitter.searchUsers(demoName, 1);
				
				for(User u: users) {
					if (u.getStatus() != null) {
						System.out.println("@" + u.getScreenName() + " - " + u.getStatus().getText());
	                } else {
	                    // the user is protected
	                    System.out.println("@" + u.getScreenName());
	                }
				}
				
			} catch (TwitterException e) {
				e.printStackTrace();
			}
			
			System.out.println();
		}
		
	}

	@Override
	public void setup(List<Individual> individuals) {
		// If one were able to provide individuals with twitter display names,
		// those individuals would probably be put into the individuals field
		// of the data collector. 
		//this.individuals = individuals;
		throw new UnsupportedOperationException("This operation is not supported in the demo of the TwitterDataCollector");
	}

	/**
	 * This method is an example of what data collectors return for the database manager and effects.
	 * The attribute name stored with every user is a "tweet" but it is of type "STATUS", which is 
	 * a global type that effects will know about. "tweet", on the other hand, will probably not be 
	 * known to effects (or the database manager) without this translation.
	 */
	@Override
	public Hashtable<String, AttributeCategories> getAttributeTable() {
		
		if(attributeTable == null) {
			attributeTable = new Hashtable<String, AttributeCategories>();
			attributeTable.put("tweet", AttributeCategories.STATUS);
		}
		
		return attributeTable;
	}
}
