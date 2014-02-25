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
 * Secondary data collector which gets data from Twitter's API
 * @author Johann Beleites
 */
public class TwitterDataCollector extends SecondaryDataCollector {
	/**
	 * Create a new Twitter data collector with the specified database manager.
	 * 
	 * @param databaseManager
	 *            The database manager to use to write to the database.
	 */
	
	private List<Individual> individuals;
	
	public TwitterDataCollector(SecondaryDatabaseManager databaseManager) {
		super(databaseManager);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		
		List<String> demoUsers = new ArrayList<String>();
		demoUsers.add("treozs");
		demoUsers.add("johnsmith");
		
		Twitter twitter = new TwitterFactory().getInstance();
		ResponseList<User> users;
		
		int i = 0;
		
		for(String demoName: demoUsers) {
			System.out.println("#### Searching for users with '" + demoUsers.get(i) + "' ####");
			
			try {
				users = twitter.searchUsers(demoUsers.get(i), 1);
				
				for(User u: users) {
					if (u.getStatus() != null) {
						System.out.println("@" + u.getScreenName() + " - " + u.getStatus().getText());
	                } else {
	                    // the user is protected
	                    System.out.println("@" + u.getScreenName());
	                }
				}
				
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println();
			i++;
		}
		
	}

	@Override
	public void setup(List<Individual> individuals) {
		this.individuals = individuals;
	}

	@Override
	public Hashtable<String, AttributeCategories> getAttributeTable() {
		
		if(attributeTable == null) {
			attributeTable = new Hashtable<String, AttributeCategories>();
			attributeTable.put("tweet", AttributeCategories.STATUS);
//			attributeTable.put("twitter_display_name", AttributeCategories.NAME);
		}
		
		return attributeTable;
	}

	@Override
	public String getCollectorId() {
		return("twitter_default");
	}
}
