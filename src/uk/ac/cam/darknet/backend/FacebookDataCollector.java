package uk.ac.cam.darknet.backend;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import uk.ac.cam.darknet.common.AttributeCategories;
import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.common.LoggerFactory;
import uk.ac.cam.darknet.database.SecondaryDatabaseManager;
import uk.ac.cam.darknet.exceptions.AuthorizationFailedException;
import uk.ac.cam.darknet.exceptions.InvalidAttributeTypeException;
import uk.ac.cam.darknet.exceptions.InvalidReliabilityException;
import uk.ac.cam.darknet.exceptions.UnknownAttributeException;
import uk.ac.cam.darknet.storage.ImageStorage;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.Parameter;
import com.restfb.types.Photo;
import com.restfb.types.User;

/**
 * Secondary data collector which gets data from Facebook's public API.
 *  
 */
public class FacebookDataCollector extends SecondaryDataCollector {
	
	private static final Logger log = LoggerFactory.getLogger();
	
	private List<Individual> targets;
	
	//private AccessToken accessToken;
	
	private DefaultFacebookClient client;
	
	// appId and appSecret will probably be necessary if a better authentication model is found - not at the moment
	//private final String appId = "1472924302921478";
	//private final String appSecret = "";
	private String token;
	
	
	/**
	 * Create a new Facebook data collector with the specified database manager.
	 * 
	 * @param databaseManager
	 *            The database manager to use to write to the database.
	 */
	public FacebookDataCollector(SecondaryDatabaseManager databaseManager) {
		super(databaseManager);
	}

	@Override
	public void run() {
		if(targets == null || token == null) {
			log.warning("Aborting Facebook data collection as collector was not intialized yet");
			return;
		}
		
		// TODO: find better authentication method
		client = new DefaultFacebookClient(token);//,appSecret);
		Connection<User> c = null;
		int error;
		
		error = 0;
		do{
			try{
				c = client.fetchConnection("me/friends", User.class, Parameter.with("fields", "id,name"));
				error = 0;
			} catch(Exception e) {
				error++;
				log.warning("Exception (" + error + ") (" + e.getClass() + ") while trying to fetch friends. Message: "
						+ e.getMessage());
				JOptionPane.showMessageDialog(null, "Invalid user token (try " + error + "/3).", "Invalid Token", JOptionPane.ERROR_MESSAGE);
				if(error >= 3) return;
				try{
					reauthenticate();
					client = new DefaultFacebookClient(token);//,appSecret);
				}catch(Exception e2){
					log.warning("Reauthentication failed due to invalid input");
				}
			}	
		} while(error > 0);

		List<User> friends = c.getData();
		List<Photo> photos = null;
		ImageStorage imageStorage = new ImageStorage();
		String birthday, relationshipStatus, gender, locale;
		User detailedFriend;
		List<User> friendsDetailed = new LinkedList<User>();
		boolean emailMatch;
		String email;
		
		// Get more details on all friends
		for(User f: friends) {
			// Try to get friends' details; need to take into consideration an invalid
			// user token (ex. when stale). Reauthentication needed in that case.
			error = 0;
			do{
				try{
					detailedFriend = client.fetchObject(f.getId(), User.class);						
					error = 0;
					friendsDetailed.add(detailedFriend);
				} catch(Exception e) {
					error++;
					log.warning("Exception (" + error + ") (" + e.getClass() + ") while trying to fetch a friend. Message: "
							+ e.getMessage());
					JOptionPane.showMessageDialog(null, "Invalid user token (try " + error + "/3).", "Invalid Token", JOptionPane.ERROR_MESSAGE);
					if(error >= 3) return;
					try{
						reauthenticate();
						client = new DefaultFacebookClient(token);//,appSecret);
					}catch(Exception e2){
						log.warning("Reauthentication failed due to invalid input");
					}
				}	
			} while(error > 0);
		}
		
		// Now iterate through targets and check if they are present in friend list of facebook
		for(Individual target: targets) {
			log.info("Looking for: " + target.getFirstName() + " " + target.getLastName());
			for(User f: friendsDetailed) {

				// Try to get email address from facebook
				email = f.getEmail();
				emailMatch = email == null ? false : email.equalsIgnoreCase(target.getEmail());
				
				// See if the current target matches the current friend (by full name or email) 
				if((f.getFirstName().equalsIgnoreCase(target.getFirstName())
						&& f.getLastName().equalsIgnoreCase(target.getLastName()))
						|| emailMatch) {
					
					// Get some potentially interesting data about target
					relationshipStatus = f.getRelationshipStatus();
					birthday = f.getBirthday();
					locale = f.getLocale();
					gender = f.getGender();
					
					// Now fetch 25 (max) photos of the target, if possible. Again an invalid token
					// must be considered.
					error = 0;
					do{
						try{
							photos = client.fetchConnection(f.getId() + "/photos", Photo.class).getData();
							error = 0;
						} catch(Exception e) {
							error++;
							log.warning("Exception (" + error + ") (" + e.getClass() + ") while trying to fetch photos. Message: "
									+ e.getMessage());
							JOptionPane.showMessageDialog(null, "Invalid user token (try " + error + "/3).", "Invalid Token", JOptionPane.ERROR_MESSAGE);
							if(error >= 3) return;
							try{
								reauthenticate();
								client = new DefaultFacebookClient(token);//,appSecret);
							}catch(Exception e2){
								log.warning("Reauthentication failed due to invalid input");
							}
						}	
					} while(error > 0);
					
					// If photos were found, store them on the hard drive
					log.info("Trying to add " + photos.size() + " photo(s) to target...");
					for(Photo p: photos) {
						try {
							//photoIds.add(imageStorage.saveImage(new URL(p.getSource())));
							target.addAttribute("fb_photo", imageStorage.saveImage(new URL(p.getSource())), 0.8);
						} catch (MalformedURLException e) {
							log.warning("MalformedURLException while trying to store image! URL: "
									+ p.getSource() + ". Message: " + e.getMessage());
						} catch (IOException e) {
							log.warning("IOException while trying to store image! URL: "
									+ p.getSource() + ". Message: " + e.getMessage());
						} catch (UnknownAttributeException
								| InvalidAttributeTypeException
								| InvalidReliabilityException e) {

							log.severe(e.getClass() + " while trying to add photo to individual. Message: " + e.getMessage());
						}
					}
					
					// Store all other data besides photos in the individual's properties before moving on to next target
					try {
						if(relationshipStatus != null) target.getProperties().put("fb_relationshipStatus", relationshipStatus, 0.8);
						if(birthday != null) target.getProperties().put("fb_birthday", birthday, 0.8);
						if(gender != null) target.getProperties().put("fb_gender", gender, 0.8);
						if(locale != null) target.getProperties().put("fb_locale", locale, 1.0);
					} catch (UnknownAttributeException
							| InvalidAttributeTypeException
							| InvalidReliabilityException e) {
						
						log.severe("Exception (" + e.getClass() + ") while trying to store the individual's attributes: " 
								+ e.getMessage());
					}
					break;
				}
			}
		}
		
		// Finally, store all the data collected in the database
		try {
			databaseManager.storeAttributes(targets);
		} catch (SQLException e) {
			log.severe("SQLException while trying to store Facebook data on all given targets. Message:" +
					e.getMessage());
		}
		
	}

	@Override
	public void setup(List<Individual> individuals) {
		// This does 2 things: authenticates and sets up the list of individuals to be targeted
		try {
			reauthenticate();
			
		} catch (AuthorizationFailedException e) {
			JOptionPane.showMessageDialog(null, "Invalid user token! Exiting.", "Invalid Token", JOptionPane.ERROR_MESSAGE);
			return;
		}
		targets = individuals;
	}

	@Override
	public Hashtable<String, AttributeCategories> getAttributeTable() {
		Hashtable<String, AttributeCategories> atts = new Hashtable<String, AttributeCategories>();
		atts.put("fb_photo", AttributeCategories.PHOTO);
		atts.put("fb_gender", AttributeCategories.GENDER);
		atts.put("fb_locale", AttributeCategories.LOCALE);
		atts.put("fb_birthday", AttributeCategories.BIRTHDAY);
		atts.put("fb_relationshipStatus", AttributeCategories.RELATIONSHIP_STATUS);
		return atts;
	}
	
	
	/**
	 * Will prompt the user to enter a user token at this stage. This token will be used to 
	 * authenticate all request to facebook. It WILL also accept invalid / stale tokens without
	 * throwing an exception; those will only be caught later. AuthorizationFailedException will
	 * only be thrown when the input is less than 1 character long (or null) at this point.
	 * 
	 * @throws AuthorizationFailedException
	 */
	public void reauthenticate() throws AuthorizationFailedException {
		
		// Takes care of authentication. At the moment user needs to input a valid user
		// token manually, due to issues with automatically obtaining such a token from
		// within a desktop application.
		
		String manualToken = JOptionPane.showInputDialog(null, "Please enter Facebook user token:");
		
		if(manualToken == null || manualToken.length() < 1) {
			throw new AuthorizationFailedException();
		}
		
		token = manualToken;
	}
	
}
