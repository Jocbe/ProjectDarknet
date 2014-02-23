package uk.ac.cam.darknet.backend;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import uk.ac.cam.darknet.common.AttributeCategories;
import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.common.LoggerFactory;
import uk.ac.cam.darknet.database.DatabaseManager;
import uk.ac.cam.darknet.storage.ImageStorage;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.Photo;
import com.restfb.types.User;
import com.restfb.types.User.Education;
import com.restfb.types.User.Work;

/**
 * Secondary data collector which gets data from Facebook's public API.
 * 
 * @author Augustin Zidek
 * 
 */
public class FacebookDataCollector extends SecondaryDataCollector {
	/**
	 * Create a new Facebook data collector with the specified database manager.
	 * 
	 * @param databaseManager
	 *            The database manager to use to write to the database.
	 */
	private static final Logger log = LoggerFactory.getLogger();
	
	private List<Individual> targets;
	
	private String accessToken;
	private String appId = "145634995501895"; //TODO: Register app on FB & change this ID (currently using graph explorer ID)
	
	
	public FacebookDataCollector(DatabaseManager databaseManager) {
		super(databaseManager);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		if(targets == null) {
			log.warning("Aborting Facebook data collection as collector was not intialized yet");
			//return;
			//TODO: remove default target
			targets = new ArrayList<Individual>();
			targets.add(new Individual(0L, "John", "Smith", "john@smith.ex", new Date(), "0", null));
		}
		
		if(accessToken == null || accessToken.length() < 1) {
			//TODO: get access token
		}
		
		FacebookClient client = new DefaultFacebookClient("CAACEdEose0cBAItJ4MhB7YJmZArUtQgWsOZCXCgFE0wNRpImMNILbspcPk9JtoGxejQ4ZBjy2Xdnli7Fqjx49GQkai7N1Wau0wjw0savoZBrrZBSCflestDFPNX1Pnh0KHzFyGLZC0v9ExXbJRJGZBC7ZCyEAPyeTioi76CwFbxzAigCYW06juMtNxNlybRberwZD");
		Connection<User> c = client.fetchConnection("me/friends", User.class, Parameter.with("fields", "id,name,picture"));
		List<User> friends = c.getData();
		List<Photo> photos;
		List<String> photoIds = new LinkedList<String>();
		ImageStorage imageStorage = new ImageStorage();
		String birthday, hometown, relationshipStatus;
		List<Education> education;
		List<Work> work;
		User completeFriend;
		
		for(Individual target: targets) {
			for(User f: friends) {
				completeFriend = client.fetchObject(f.getId(), User.class);
				if((completeFriend.getFirstName().equalsIgnoreCase(target.getFirstName())
						&& completeFriend.getLastName().equalsIgnoreCase(target.getLastName()))
						//|| completeFriend.getEmail().equalsIgnoreCase(target.getEmail())
						|| true
						) {
					
					//TODO: try {
						photos = client.fetchConnection(f.getId() + "/photos", Photo.class).getData();
						//birthday = f.getBirthday();
						//education = f.getEducation();
						//work = f.getWork();
						//hometown = f.getHometownName();
						//relationshipStatus = f.getRelationshipStatus();
						//Intersting?: f.getLocation()
					//} 
					
					for(Photo p: photos) {
						try {
							photoIds.add(imageStorage.saveImage(new URL(p.getSource())));
						} catch (MalformedURLException e) {
							log.warning("MalformedURLException while trying to store image! URL: "
									+ p.getSource() + ". Message: " + e.getMessage());
						} catch (IOException e) {
							log.warning("IOException while trying to store image! URL: "
									+ p.getSource() + ". Message: " + e.getMessage());
						}
					}
					
					// TODO: Store images photoIds in database for User target 
					
					break;
				}
			}
		}
		
		
		
		String id;
		
		System.out.println(friends.size());
		/*for(User u: friends) {
			System.out.print(u.getName());
			id = u.getId();
			
			List<Photo> p = client.fetchConnection(id + "/photos", Photo.class).getData();
			
			//User f = client.fetchObject(id, User.class);
			if(p.size() > 0) System.out.print("  " + p.size() + ": " + p.get(0).getSource());
			
			System.out.println();
		}*/
	}

	@Override
	public void setup(List<Individual> individuals) {
		targets = individuals;
	}

	@Override
	public Hashtable<String, AttributeCategories> getAttributeTable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCollectorId() {
		// TODO Auto-generated method stub
		return null;
	}
}
