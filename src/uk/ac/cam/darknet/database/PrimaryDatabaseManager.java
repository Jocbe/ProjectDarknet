package uk.ac.cam.darknet.database;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import uk.ac.cam.darknet.common.AttributeCategories;
import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.exceptions.ConfigFileNotFoundException;

/**
 * A primary database manager is used to provide database access to the primary data collectors.
 * 
 * @author Ibtehaj Nadeem
 */
public class PrimaryDatabaseManager extends DatabaseManager {
	private static final String	CREATE_PRIMARY_TABLE	= "CREATE CACHED TABLE individuals (id BIGINT GENERATED ALWAYS AS IDENTITY(START WITH 1000000) PRIMARY KEY, fname VARCHAR(25) NOT NULL, lname VARCHAR(25) NOT NULL, email VARCHAR(254), event TIMESTAMP WITHOUT TIME ZONE, seat VARCHAR(10))";
	private static final String	INSERT_INDIVIDUAL		= "INSERT INTO individuals (id, fname, lname, email, event, seat) VALUES (DEFAULT, ?, ?, ?, ?, ?)";
	private String				fname;
	private String				lname;
	private String				email;
	private String				event;
	private String				seat;

	/**
	 * Creates a new <code>PrimaryDatabaseManager</code> with the specified global attribute table
	 * and sets up the connection to the database.
	 * 
	 * @param globalAttributeTable
	 *            The global table of attributes currently supported by the system. These attributes
	 *            are used to generate the table names in the database and have to be consistent
	 *            between successive executions of the system.
	 * @param pathToConfig
	 *            The path to the file containing information about how to connect to the database
	 *            server.
	 * @throws ConfigFileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public PrimaryDatabaseManager(Hashtable<String, AttributeCategories> globalAttributeTable, String pathToConfig) throws ConfigFileNotFoundException, IOException, ClassNotFoundException, SQLException {
		super(globalAttributeTable, pathToConfig);
		createTable();
	}

	private void createTable() throws SQLException {
		try (Statement stmt = connection.createStatement();) {
			stmt.execute(CREATE_PRIMARY_TABLE);
			connection.commit();
		} catch (SQLException e) {
			// Table already exists.
		}
	}

	private void setupStrings(Individual toStore) {
		fname = toStore.getFirstName() == null ? "NULL" : toStore.getFirstName();
		lname = toStore.getLastName() == null ? "NULL" : toStore.getLastName();
		email = toStore.getEmail() == null ? "NULL" : toStore.getEmail();
		event = toStore.getEventDate() == null ? "NULL" : new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(toStore.getEventDate());
		seat = toStore.getSeat() == null ? "NULL" : toStore.getSeat();
	}

	/**
	 * Stores a list of individuals into the database. If the number of individuals is large, this
	 * is the preferred method. Inserting individuals one after the other is less efficient.
	 * 
	 * @param list
	 *            The list of individuals to store.
	 * @return The number of individuals successfully inserted.
	 * @throws SQLException
	 */
	public synchronized int store(List<Individual> list) throws SQLException {
		Iterator<Individual> iterator = list.iterator();
		Individual current;
		int numOfIndividualsInserted = 0;
		try (PreparedStatement stmt = connection.prepareStatement(INSERT_INDIVIDUAL);) {
			while (iterator.hasNext()) {
				current = iterator.next();
				setupStrings(current);
				if (fname.equals("NULL") || lname.equals("NULL") || fname.length() > 25 || lname.length() > 25 || email.length() > 254 || seat.length() > 10)
					continue;
				stmt.setString(1, fname);
				stmt.setString(2, lname);
				stmt.setString(3, email);
				stmt.setString(4, event);
				stmt.setString(5, seat);
				stmt.executeUpdate();
				numOfIndividualsInserted++;
			}
		}
		connection.commit();
		return numOfIndividualsInserted;
	}

	/**
	 * Stores a single individual into the database.
	 * 
	 * @param individual
	 *            The individual to insert.
	 * @return True if the individual was inserted successfully, false otherwise.
	 * @throws SQLException
	 */
	public synchronized boolean store(Individual individual) throws SQLException {
		try (PreparedStatement stmt = connection.prepareStatement(INSERT_INDIVIDUAL);) {
			setupStrings(individual);
			if (fname.equals("NULL") || lname.equals("NULL") || fname.length() > 25 || lname.length() > 25 || email.length() > 254 || seat.length() > 10)
				return false;
			stmt.setString(1, fname);
			stmt.setString(2, lname);
			stmt.setString(3, email);
			stmt.setString(4, event);
			stmt.setString(5, seat);
			stmt.executeUpdate();
		}
		connection.commit();
		return true;
	}

	/**
	 * Attempts to find and return an individual by their ID.
	 * 
	 * @param id
	 *            The unique ID of the individual to return.
	 * @return The individual with the given ID, or null if such an individual could not be found.
	 */
	public synchronized Individual getById(long id) {
		// TODO
		return null;
	}

	private Individual createIndividual() {
		// TODO
		return null;
	}

	// public static void main(String args[]) throws ClassNotFoundException,
	// ConfigFileNotFoundException, IOException, SQLException {
	// PrimaryDatabaseManager instance = new PrimaryDatabaseManager(null,
	// args[0]);
	// }
}
