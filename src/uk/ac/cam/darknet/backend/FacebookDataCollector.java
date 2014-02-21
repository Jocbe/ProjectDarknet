package uk.ac.cam.darknet.backend;

import java.util.Hashtable;
import java.util.List;

import uk.ac.cam.darknet.common.AttributeCategories;
import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.database.DatabaseManager;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.Facebook;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.Album;
import com.restfb.types.CategorizedFacebookType;
import com.restfb.types.NamedFacebookType;
import com.restfb.types.Photo;
import com.restfb.types.User;

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
	public FacebookDataCollector(DatabaseManager databaseManager) {
		super(databaseManager);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		FacebookClient client = new DefaultFacebookClient("CAACEdEose0cBAEogvuL9ynu08Umd5JFCpjSlylropybW0LO4vKX89ZArJjTfSzxzIF1JVvuk2Su3MSp3iKn9EqlC4EdoSEVPEdPZBYAyto5Jgb9bBtAwQjnNJF8n5vKgDMbGmkiX2U4RMzmkNiKdT01HvyqOlrWAUGarMAlOh4YC0z4KcvTjx0fos5UgMIekgFeDQ3xQZDZD");
		
		Connection<User> c = client.fetchConnection("me/friends", User.class, Parameter.with("fields", "id,name,picture"));
		
		List<User> friends = c.getData();
		String id;
		
		for(User u: friends) {
			System.out.print(u.getName());
			id = u.getId();
			
			List<Photo> p = client.fetchConnection(id + "/photos", Photo.class).getData();
			
			//User f = client.fetchObject(id, User.class);
			if(p.size() > 0) System.out.print("  " + p.size() + ": " + p.get(0).getSource());
			
			System.out.println();
		}
	}

	@Override
	public void setup(List<Individual> individuals) {
		// TODO Auto-generated method stub
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
