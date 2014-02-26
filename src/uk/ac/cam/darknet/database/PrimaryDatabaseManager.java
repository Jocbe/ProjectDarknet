package uk.ac.cam.darknet.database;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import uk.ac.cam.darknet.common.AttributeCategories;
import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.common.LoggerFactory;
import uk.ac.cam.darknet.exceptions.ConfigFileNotFoundException;

/**
 * A primary database manager is used to store individuals that are created by primary data
 * collectors.
 * 
 * @author Ibtehaj Nadeem
 */
public class PrimaryDatabaseManager extends DatabaseManager {
	private static final String	CREATE_VENUES_TABLE			= "CREATE TABLE venues (id INTEGER GENERATED ALWAYS AS IDENTITY(START WITH 1) PRIMARY KEY, name VARCHAR(50) NOT NULL)";
	private static final String	CREATE_SHOWS_TABLE			= "CREATE TABLE shows (date TIMESTAMP(0) WITHOUT TIME ZONE, venue INTEGER, FOREIGN KEY (venue) REFERENCES venues(id), PRIMARY KEY (date, venue))";
	private static final String	CREATE_INDIVIDUALS_TABLE	= "CREATE CACHED TABLE individuals (id BIGINT GENERATED ALWAYS AS IDENTITY(START WITH 1000000) PRIMARY KEY, fname VARCHAR(50) NOT NULL, lname VARCHAR(50) NOT NULL, email VARCHAR(254), date TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL, venue INTEGER NOT NULL, seat VARCHAR(10), UNIQUE (fname, lname, email, date, venue, seat), FOREIGN KEY (date, venue) REFERENCES shows(date, venue))";
	private static final String	INSERT_INDIVIDUAL			= "INSERT INTO individuals (id, fname, lname, email, date, venue, seat) VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)";
	private static final String	INSERT_VENUE				= "INSERT INTO venues (id, name) VALUES (DEFAULT, ?)";
	private static final String	INSERT_SHOW					= "INSERT INTO shows (date, venue) VALUES (?, ?)";
	private static final String	GET_NEW_INDIVIDUAL_ID		= "SELECT MAX(id) FROM individuals";
	private static final String	GET_NEW_VENUE_ID			= "SELECT MAX(id) FROM venues";
	private static final String	DELETE_INDIVIDUAL			= "DELETE FROM individuals WHERE id = ?";
	private static final String	UPDATE_INDIVIDUAL			= "UPDATE individuals SET fname = ?, lname = ?, email = ?, date = ?, venue = ?, seat = ? WHERE id = ?";
	private static final String	CHECK_SHOW_EXISTS			= "SELECT COUNT(1) FROM shows WHERE date = ? AND venue = ?";
	private String				fname;
	private String				lname;
	private String				email;
	private Timestamp			date;
	private int					venue;
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
	public PrimaryDatabaseManager(Hashtable<String, AttributeCategories> globalAttributeTable) throws ConfigFileNotFoundException, IOException, ClassNotFoundException, SQLException {
		super(globalAttributeTable);
		createTable();
	}

	private void createTable() throws SQLException {
		try (Statement stmt = connection.createStatement();) {
			try {
				stmt.execute(CREATE_VENUES_TABLE);
			} catch (SQLException e) {
				// Table already exists.
				LoggerFactory.getLogger().info(e.getMessage());
			}
			try {
				stmt.execute(CREATE_SHOWS_TABLE);
			} catch (SQLException e) {
				// Table already exists.
				LoggerFactory.getLogger().info(e.getMessage());
			}
			try {
				stmt.execute(CREATE_INDIVIDUALS_TABLE);
			} catch (SQLException e) {
				// Table already exists.
				LoggerFactory.getLogger().info(e.getMessage());
			}
		} catch (SQLException e) {
			connection.rollback();
			throw e;
		}
		connection.commit();
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
				setupLocalFieldParameters(current);
				try {
					createShowIfNotExists(current.getEventDate(), current.getEvenVenue());
					executeIndividualUpdateStatement(stmt);
					numOfIndividualsInserted++;
				} catch (SQLException e) {
					// Do not increment numOfIndividualsInserted.
					LoggerFactory.getLogger().info(e.getMessage());
				}
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
	 * empty (or null) first and/or last name and malformed email address. In case of any other
	 * serious SQL error, all changes will be rolled back.
	 * 
	 * @param individual
	 *            The individual to insert.
	 * @return The ID of the new individual, or -1 if insertion was unsuccessful. This may be the
	 *         case if an individual with exactly the same data already existed.
	 * @throws SQLException
	 */
	public synchronized long storeIndividual(Individual individual) throws SQLException {
		long individualId = -1;
		try (PreparedStatement stmt = connection.prepareStatement(INSERT_INDIVIDUAL);) {
			setupLocalFieldParameters(individual);
			try {
				createShowIfNotExists(individual.getEventDate(), individual.getEvenVenue());
				executeIndividualUpdateStatement(stmt);
				try (Statement getId = connection.createStatement()) {
					try (ResultSet result = getId.executeQuery(GET_NEW_INDIVIDUAL_ID);) {
						if (result.next())
							individualId = result.getLong(1);
					}
				}
			} catch (SQLException e) {
				// Leave individualId as -1.
				LoggerFactory.getLogger().info(e.getMessage());
			}
		} catch (SQLException e) {
			connection.rollback();
			throw e;
		}
		connection.commit();
		return individualId;
	}

	/**
	 * Deletes the individual specified by the given ID.
	 * 
	 * @param id
	 *            The ID of the individual to delete.
	 * @return A boolean indicating whether the individual was successfully removed.
	 * @throws SQLException
	 */
	public synchronized boolean deleteIndividual(long id) throws SQLException {
		boolean individualDeleted = true;
		try (PreparedStatement stmt = connection.prepareStatement(DELETE_INDIVIDUAL);) {
			stmt.setLong(1, id);
			try {
				stmt.execute();
			} catch (SQLException e) {
				individualDeleted = false;
				LoggerFactory.getLogger().info(e.getMessage());
			}
		} catch (SQLException e) {
			connection.rollback();
			throw e;
		}
		connection.commit();
		return individualDeleted;
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
		boolean individualValid = true;
		try (PreparedStatement stmt = connection.prepareStatement(UPDATE_INDIVIDUAL);) {
			stmt.setLong(7, id);
			setupLocalFieldParameters(newData);
			try {
				executeIndividualUpdateStatement(stmt);
			} catch (SQLException e) {
				individualValid = false;
				LoggerFactory.getLogger().info(e.getMessage());
			}
		} catch (SQLException e) {
			connection.rollback();
			throw e;
		}
		connection.commit();
		return individualValid;
	}

	/**
	 * Create a new venue.
	 * 
	 * @param name
	 *            name of the venue to create.
	 * @return The ID of the newly created venue, or -1 on error.
	 * @throws SQLException
	 */
	public synchronized int createVenue(String name) throws SQLException {
		int venueId = -1;
		try (PreparedStatement stmt = connection.prepareStatement(INSERT_VENUE);) {
			stmt.setString(1, name);
			try {
				stmt.execute();
				try (Statement getId = connection.createStatement()) {
					try (ResultSet result = getId.executeQuery(GET_NEW_VENUE_ID);) {
						if (result.next())
							venueId = result.getInt(1);
					}
				}
			} catch (SQLException e) {
				LoggerFactory.getLogger().info(e.getMessage());
			}
		} catch (SQLException e) {
			connection.rollback();
			throw e;
		}
		connection.commit();
		return venueId;
	}

	private void createShowIfNotExists(Date date, int venue) throws SQLException {
		try (PreparedStatement stmt = connection.prepareStatement(CHECK_SHOW_EXISTS)) {
			stmt.setTimestamp(1, dateToSQLTimestamp(date));
			stmt.setInt(2, venue);
			try (ResultSet resultSet = stmt.executeQuery();) {
				if (resultSet.next()) {
					if (resultSet.getInt(1) == 1) {
						return;
					} else {
						createShow(date, venue);
					}
				}
			}
		}
	}

	private void createShow(Date date, int venue) throws SQLException {
		try (PreparedStatement stmt = connection.prepareStatement(INSERT_SHOW);) {
			stmt.setTimestamp(1, dateToSQLTimestamp(date));
			stmt.setInt(2, venue);
			stmt.execute();
		}
	}

	private void setupPreparedStatementParameters(PreparedStatement stmt) throws SQLException {
		stmt.setString(1, fname);
		stmt.setString(2, lname);
		stmt.setString(3, email);
		stmt.setTimestamp(4, date);
		stmt.setInt(5, venue);
		stmt.setString(6, seat);
	}

	// This method does not catch SQL exceptions. Thus, if it is called from within a loop then all
	// changes will be rolled back if only one statement execution fails.
	private void executeIndividualUpdateStatement(PreparedStatement stmt) throws SQLException {
		setupPreparedStatementParameters(stmt);
		stmt.executeUpdate();
	}

	private void setupLocalFieldParameters(Individual toStore) {
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
		if (toStore.getSeat() == null) {
			seat = null;
		} else {
			seat = toStore.getSeat().trim().equals("") ? null : toStore.getSeat().trim();
		}
		date = dateToSQLTimestamp(toStore.getEventDate());
		venue = toStore.getEvenVenue();
	}
}