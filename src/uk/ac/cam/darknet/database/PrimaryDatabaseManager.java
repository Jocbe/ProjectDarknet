package uk.ac.cam.darknet.database;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import uk.ac.cam.darknet.common.AttributeCategories;
import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.exceptions.ConfigFileNotFoundException;

/**
 * A primary database manager is used to store individuals that are created by primary data
 * collectors.
 * 
 * @author Ibtehaj Nadeem
 */
public class PrimaryDatabaseManager extends DatabaseManager {
	private static final String	CREATE_PRIMARY_TABLE	= "CREATE CACHED TABLE individuals (id BIGINT GENERATED ALWAYS AS IDENTITY(START WITH 1000000) PRIMARY KEY, fname VARCHAR(25) NOT NULL, lname VARCHAR(25) NOT NULL, email VARCHAR(254), event TIMESTAMP(0) WITHOUT TIME ZONE, seat VARCHAR(10))";
	private static final String	INSERT_INDIVIDUAL		= "INSERT INTO individuals (id, fname, lname, email, event, seat) VALUES (DEFAULT, ?, ?, ?, ?, ?)";
	private static final String	DELETE_INDIVIDUAL		= "DELETE FROM individuals WHERE id = ?";
	private static final String	UPDATE_INDIVIDUAL		= "UPDATE individuals SET fname = ?, lname = ?, email = ?, event = ?, seat = ? WHERE id = ?";
	private static final String	EMAIL_PATTERN			= "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private String				fname;
	private String				lname;
	private String				email;
	private Timestamp			event;
	private String				seat;
	private Pattern				pattern					= Pattern.compile(EMAIL_PATTERN);

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

	/**
	 * Stores a list of individuals into the database. If the number of individuals is large, this
	 * is the preferred method. Inserting individuals one after the other is less efficient.
	 * 
	 * <b>Note:</b> This method will discard any individuals with invalid fields. This includes
	 * empty (or null) first and/or last names and malformed email addresses. A count of the number
	 * of individuals inserted will be given. In case of any other serious SQL error, all changes
	 * will be rolled back.
	 * 
	 * @param list
	 *            The list of individuals to store.
	 * @return The number of individuals successfully inserted.
	 * @throws SQLException
	 */
	public synchronized int storeIndividual(List<Individual> list) throws SQLException {
		Iterator<Individual> iterator = list.iterator();
		Individual current;
		int numOfIndividualsInserted = 0;
		try (PreparedStatement stmt = connection.prepareStatement(INSERT_INDIVIDUAL);) {
			while (iterator.hasNext()) {
				current = iterator.next();
				setupParameters(current);
				if (parametersValid())
					executeStatement(stmt);
				numOfIndividualsInserted++;
			}
		} catch (SQLException e) {
			connection.rollback();
			throw e;
		}
		connection.commit();
		return numOfIndividualsInserted;
	}

	/**
	 * Stores a single individual into the database.
	 * 
	 * <b>Note:</b> This method will discard the individual if it has invalid fields. This includes
	 * empty (or null) first and/or last name and malformed email address. A boolean value will be
	 * returned, indicating whether the insertion was successful. In case of any other serious SQL
	 * error, all changes will be rolled back.
	 * 
	 * @param individual
	 *            The individual to insert.
	 * @return True if the individual was inserted successfully, false otherwise.
	 * @throws SQLException
	 */
	public synchronized boolean storeIndividual(Individual individual) throws SQLException {
		boolean individualValid;
		try (PreparedStatement stmt = connection.prepareStatement(INSERT_INDIVIDUAL);) {
			setupParameters(individual);
			individualValid = parametersValid();
			if (individualValid)
				executeStatement(stmt);
		} catch (SQLException e) {
			connection.rollback();
			throw e;
		}
		connection.commit();
		return individualValid;
	}

	/**
	 * Deletes the individual specified by the given ID.
	 * 
	 * @param id
	 *            The ID of the individual to delete.
	 * @throws SQLException
	 */
	public synchronized void deleteIndividual(long id) throws SQLException {
		try (PreparedStatement stmt = connection.prepareStatement(DELETE_INDIVIDUAL);) {
			stmt.setLong(1, id);
			stmt.execute();
		} catch (SQLException e) {
			connection.rollback();
			throw e;
		}
		connection.commit();
	}

	/**
	 * Updates the data associated with an individual specified by their ID. The new data itself
	 * must be passed as an <code>Individual</code> object.
	 * 
	 * <b>Note:</b> This method will discard the new data if it has invalid fields. This includes
	 * empty (or null) first and/or last name and malformed email address. A boolean value will be
	 * returned, indicating whether the update was successful. In case of any other serious SQL
	 * error, all changes will be rolled back.
	 * 
	 * @param id
	 *            The ID of the individual to update.
	 * @param newData
	 *            The new data to be written to that individual.
	 * @return A boolean indicating whether the update was successful or not.
	 * @throws SQLException
	 */
	public synchronized boolean updateIndividual(long id, Individual newData) throws SQLException {
		boolean individualValid;
		try (PreparedStatement stmt = connection.prepareStatement(UPDATE_INDIVIDUAL);) {
			stmt.setLong(6, id);
			setupParameters(newData);
			individualValid = parametersValid();
			if (individualValid)
				executeStatement(stmt);
		} catch (SQLException e) {
			connection.rollback();
			throw e;
		}
		connection.commit();
		return individualValid;
	}

	private void executeStatement(PreparedStatement stmt) throws SQLException {
		stmt.setString(1, fname);
		stmt.setString(2, lname);
		stmt.setString(3, email);
		stmt.setTimestamp(4, event);
		stmt.setString(5, seat);
		stmt.executeUpdate();
	}

	private void setupParameters(Individual toStore) {
		if (toStore.getFirstName() == null) {
			fname = null;
		} else {
			fname = toStore.getFirstName().trim().equals("") ? null : toStore.getFirstName().trim();
		}
		if (toStore.getLastName() == null) {
			lname = null;
		} else {
			lname = toStore.getLastName().trim().equals("") ? null : toStore.getLastName().trim();
		}
		if (toStore.getEmail() == null) {
			email = null;
		} else {
			email = toStore.getEmail().trim().equals("") ? null : toStore.getEmail().trim().toLowerCase();
		}
		event = dateToSQLTimestamp(toStore.getEventDate());
		if (toStore.getSeat() == null) {
			seat = null;
		} else {
			seat = toStore.getSeat().trim().equals("") ? null : toStore.getSeat().trim();
		}
	}

	private boolean parametersValid() {
		if (fname == null || lname == null)
			return false;
		if (fname.length() > 25 || lname.length() > 25)
			return false;
		if (email != null && email.length() > 254)
			return false;
		if (email != null && (!pattern.matcher(email).matches()))
			return false;
		if (seat != null && seat.length() > 10)
			return false;
		return true;
	}

	@SuppressWarnings({"javadoc"})
	public static void main(String args[]) throws ClassNotFoundException, ConfigFileNotFoundException, IOException, SQLException {
		PrimaryDatabaseManager instance = new PrimaryDatabaseManager(null, args[0]);
		ArrayList<Individual> individuals = new ArrayList<Individual>();
		String[] fnames = {"Claire", "Denise", "Richard", "Travis", "Sheila"};
		String[] lnames = {"Manzella", "Salazar", "Connally", "Briggs", "Brewer"};
		String[] emails = {"c.manzella241@gmail.com", "", "", "", "sheilambrewer@teleworm.us"};
		String[] seats = {"A01", "B52", null, "C04", "D14"};
		long b = System.currentTimeMillis();
		for (int i = 0; i < 500; i++) {
			individuals.add(Individual.getNewIndividual(fnames[i % 5], lnames[i % 5], emails[i % 5], new java.util.Date(), seats[i % 5], null));
		}
		instance.storeIndividual(individuals);
		long a = System.currentTimeMillis();
		individuals = (ArrayList<Individual>) instance.getAllIndividuals();
		for (Individual i : individuals) {
			System.out.println("Individual " + i.getId() + ": " + i.getFirstName() + " " + i.getLastName());
		}
		System.out.println("Done in " + (a - b) + "ms");
	}
}
