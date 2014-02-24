package uk.ac.cam.darknet.backend;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import uk.ac.cam.darknet.common.AttributeCategories;
import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.common.LoggerFactory;
import uk.ac.cam.darknet.database.SecondaryDatabaseManager;
import uk.ac.cam.darknet.exceptions.InvalidAttributeTypeException;
import uk.ac.cam.darknet.exceptions.InvalidReliabilityException;
import uk.ac.cam.darknet.exceptions.UnknownAttributeException;
import uk.ac.cam.darknet.storage.ImageStorage;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.FacebookClient.AccessToken;
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
	
	private AccessToken accessToken;
	private String appId = "1472924302921478";
	private String appSecret = "aea3fa35cf8c02a310ed3f2145cd830e";
	
	
	public FacebookDataCollector(SecondaryDatabaseManager databaseManager) {
		super(databaseManager);
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		if(targets == null) {
			log.warning("Aborting Facebook data collection as collector was not intialized yet");
			return;
			
//			targets = new ArrayList<Individual>();
//			targets.add(Individual.getNewIndividual("John", "Smith", "john@smith.ex", new Date(), 0, "0", getAttributeTable()));
		}
		
		if(accessToken == null || accessToken.getExpires().before(new Date()) || accessToken.getAccessToken().length() < 1) {
			//TODO: get access token
			accessToken = new DefaultFacebookClient().obtainAppAccessToken(appId, appSecret);
			System.out.println("Token: " + accessToken);
			
		}
		
//		FacebookClient client = new DefaultFacebookClient("CAACEdEose0cBAGaO8Grihntt2uAgPzkll88dpVWT2kU7cr4ZA3zQ8u4sLwyN1ZCo7raIfBK8iw6vnKuwC43ZBut8swNISOhlgZCyqRTSoE7xLmZCluHbh0fZCKroK2s9ok5CMQNUAsW0lP3s5bnqxXbKMZCWeQ5WKLdGImAOJ7dLLeRAIsKR5S6tRBaTiKsKlD5eZAeVAUZBR1wZDZD");
		//FacebookClient client = new DefaultFacebookClient("CAACEdEose0cBADC6Q0PLbvXt3njijciVruFWZBpQDGm5F8FOjA2s5WEOg4pWyuQRjBcoHuiejlToLZApK3YMVVZC4axFgnoIXMJp80j0bVoWdMmArZBwZCEs5jTQAQ3OHsBsHbBdgUbuhLPxliMUCff3g2MI1ZB3CFOpGNbknU63Xy61kbvXPyl8CdzHsAf1YZD");
		//FacebookClient client = new DefaultFacebookClient(accessToken.getAccessToken(), appSecret);
		FacebookClient client = new AuthenticatedFacebookClient();
		
		Connection<User> c = client.fetchConnection("me/friends", User.class, Parameter.with("fields", "id,name,picture"));
		List<User> friends = c.getData();
		List<Photo> photos;
		List<String> photoIds;
		ImageStorage imageStorage = new ImageStorage();
		String birthday, hometown, relationshipStatus, gender, locale;
//		List<Education> education;
//		List<Work> work;
		User detailedFriend;
		boolean emailMatch;
		String email;
		
		for(Individual target: targets) {
			System.out.println("Looking for: " + target.getFirstName() + " " + target.getLastName());
			for(User f: friends) {
				detailedFriend = client.fetchObject(f.getId(), User.class);
				
				email = detailedFriend.getEmail();
				emailMatch = email == null ? false : email.equalsIgnoreCase(target.getEmail());
				
				if((detailedFriend.getFirstName().equalsIgnoreCase(target.getFirstName())
						&& detailedFriend.getLastName().equalsIgnoreCase(target.getLastName()))
						|| emailMatch
//						|| true
						) {
					
					photoIds = new LinkedList<String>();
					
					//try {
						//education = f.getEducation();
						//work = f.getWork();
						//hometown = f.getHometownName();
						//Intersting?: f.getLocation()
					//} 
					
					
					relationshipStatus = detailedFriend.getRelationshipStatus();
					birthday = detailedFriend.getBirthday();
					locale = detailedFriend.getLocale();
					gender = detailedFriend.getGender();
					//TODO: add try block around anything that is fetching data from FB
					photos = client.fetchConnection(f.getId() + "/photos", Photo.class).getData();
					for(Photo p: photos) {
						try {
							photoIds.add(imageStorage.saveImage(new URL(p.getSource())));
							System.out.println("adding photo: " + p.getSource());
						} catch (MalformedURLException e) {
							log.warning("MalformedURLException while trying to store image! URL: "
									+ p.getSource() + ". Message: " + e.getMessage());
						} catch (IOException e) {
							log.warning("IOException while trying to store image! URL: "
									+ p.getSource() + ". Message: " + e.getMessage());
						}
					}
					
					// Store photos in DB if any were found
					if(photoIds.size() > 0) {
						try {
							target.getProperties().put("fb_photos", photoIds, 0.8);
							if(relationshipStatus != null) target.getProperties().put("fb_relationshipStatus", relationshipStatus, 0.8);
							if(birthday != null) target.getProperties().put("fb_birthday", birthday, 0.8);
							if(gender != null) target.getProperties().put("fb_gender", gender, 0.8);
							if(locale != null) target.getProperties().put("fb_locale", locale, 1.0);
						} catch (UnknownAttributeException
								| InvalidAttributeTypeException
								| InvalidReliabilityException e) {
							
							log.severe("Exception (" + e.getClass() + ") while trying to store list of Facebook photos " +
									"in the database: " + e.getMessage());
						}
					}
					break;
				}
			}
		}
		
		try {
			databaseManager.storeAttributes(targets);
		} catch (SQLException e) {
			log.severe("SQLException while trying to store Facebook data on all given targets. Message:" +
					e.getMessage());
		}
		
	}

	@Override
	public void setup(List<Individual> individuals) {
		targets = individuals;
	}

	@Override
	public Hashtable<String, AttributeCategories> getAttributeTable() {
		Hashtable<String, AttributeCategories> atts = new Hashtable<String, AttributeCategories>();
		atts.put("fb_photos", AttributeCategories.PHOTOS);
		atts.put("fb_gender", AttributeCategories.GENDER);
		atts.put("fb_locale", AttributeCategories.LOCALE);
		atts.put("fb_birthday", AttributeCategories.BIRTHDAY);
		atts.put("fb_relationshipStatus", AttributeCategories.RELATIONSHIP_STATUS);
		return atts;
	}

	@Override
	public String getCollectorId() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private class AuthenticatedFacebookClient extends DefaultFacebookClient {
		public AuthenticatedFacebookClient() {
			super("CAACEdEose0cBAGaO8Grihntt2uAgPzkll88dpVWT2kU7cr4ZA3zQ8u4sLwyN1ZCo7raIfBK8iw6vnKuwC43ZBut8swNISOhlgZCyqRTSoE7xLmZCluHbh0fZCKroK2s9ok5CMQNUAsW0lP3s5bnqxXbKMZCWeQ5WKLdGImAOJ7dLLeRAIsKR5S6tRBaTiKsKlD5eZAeVAUZBR1wZDZD");
		}
	}
}
