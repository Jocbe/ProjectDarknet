package uk.ac.cam.darknet.database;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import uk.ac.cam.darknet.common.AttributeCategories;
import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.exceptions.ConfigFileNotFoundException;

/**
 * Any implementation of this abstract class has methods to search for individuals. Both the data
 * collection manager and effects should use implementations of this class to search for
 * individuals.
 * 
 * @author Ibtehaj Nadeem
 */
public abstract class DatabaseManager {
	private static String									GET_BY_ID			= "SELECT * FROM individuals WHERE id = ?";
	private static String									GET_BY_EVENT_DATE	= "SELECT * FROM individuals WHERE event = ?";
	private static String									GET_BETWEEN_DATES	= "SELECT * FROM individuals WHERE event BETWEEN ? AND ?";
	private static String									GET_BY_SEAT			= "SELECT * FROM individuals WHERE seat = ?";
	private static String									GET_BY_EMAIL		= "SELECT * FROM individuals WHERE email = ?";
	private static String									GET_BY_FNAME		= "SELECT * FROM individuals WHERE fname = ?";
	private static String									GET_BY_LNAME		= "SELECT * FROM individuals WHERE lname = ?";
	protected final Connection								connection;
	protected final Hashtable<String, AttributeCategories>	globalAttributeTable;
	private long											id;
	private String											fname;
	private String											lname;
	private String											email;
	private Date											event;
	private String											seat;

	/**
	 * Creates a new <code>DatabaseManager</code> with the specified global attribute table and sets
	 * up the connection to the database.
	 * 
	 * @param globalAttributeTable
	 *            The global table of attributes currently supported by the system. These attributes
	 *            are used to generate the table names in the database and have to be consistent
	 *            between successive executions of the system.
	 * @throws ConfigFileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	protected DatabaseManager(Hashtable<String, AttributeCategories> globalAttributeTable, String pathToConfig) throws ConfigFileNotFoundException, IOException, ClassNotFoundException, SQLException {
		// Assign global attribute table to local field.
		this.globalAttributeTable = globalAttributeTable;

		// Create the connection to the database.
		connection = connectToDB(pathToConfig);
		connection.setAutoCommit(false);
	}

	private Connection connectToDB(String pathToConfig) throws SQLException, FileNotFoundException, IOException, ClassNotFoundException {
		String prefix = null;
		String host = null;
		String port = null;
		String alias = null;
		String username = null;
		String password = null;
		String connectionUrl;
		try (BufferedReader configBR = new BufferedReader(new FileReader(pathToConfig));) {
			String line;
			while ((line = configBR.readLine()) != null) {
				if (line.startsWith("#") || line.equals("")) {
					continue;
				} else {
					if (line.startsWith("prefix=")) {
						prefix = line.split("=", 2)[1];
					} else if (line.startsWith("host=")) {
						host = line.split("=", 2)[1];
					} else if (line.startsWith("port=")) {
						port = line.split("=", 2)[1];
					} else if (line.startsWith("alias=")) {
						alias = line.split("=", 2)[1];
					} else if (line.startsWith("username=")) {
						username = line.split("=", 2)[1];
					} else if (line.startsWith("password=")) {
						password = line.split("=", 2)[1];
					}
				}
			}
		}
		connectionUrl = prefix + "//" + host + ":" + port + "/" + alias;
		Class.forName("org.hsqldb.jdbc.JDBCDriver");
		return DriverManager.getConnection(connectionUrl, username, password);
	}

	/**
	 * Attempts to find and return an individual by their ID.
	 * 
	 * @param id
	 *            The unique ID of the individual to return.
	 * @return The individual with the given ID, or null if such an individual could not be found.
	 * @throws SQLException
	 */
	public synchronized Individual getById(long id) throws SQLException {
		Individual toReturn;
		try (PreparedStatement stmt = connection.prepareStatement(GET_BY_ID);) {
			stmt.setLong(1, id);
			try (ResultSet result = stmt.executeQuery();) {
				toReturn = createIndividual(result);
			}
		}
		return toReturn;
	}

	/**
	 * Return a list of individuals by the date of an event.
	 * 
	 * @param eventDate
	 *            The date of the event of which all individuals are to be fetched.
	 * @return A list of individuals, each of which has booked a ticket for the given date.
	 * @throws SQLException
	 */
	public synchronized List<Individual> getByEventDate(Date eventDate) throws SQLException {
		ArrayList<Individual> toReturn = new ArrayList<Individual>();
		Individual next;
		try (PreparedStatement stmt = connection.prepareStatement(GET_BY_EVENT_DATE);) {
			stmt.setTimestamp(1, dateToSQLTimestamp(eventDate));
			try (ResultSet result = stmt.executeQuery();) {
				next = createIndividual(result);
				while (next != null) {
					toReturn.add(next);
					next = createIndividual(result);
				}
			}
		}
		return toReturn;
	}

	/**
	 * Return a list of individuals who are sitting on the specified seat.
	 * 
	 * @param seat
	 *            The seat, expressed as a string.
	 * @return A list of individuals who have booked the specified seat.
	 * @throws SQLException
	 */
	public synchronized List<Individual> getBySeat(String seat) throws SQLException {
		ArrayList<Individual> toReturn = new ArrayList<Individual>();
		Individual next;
		try (PreparedStatement stmt = connection.prepareStatement(GET_BY_SEAT);) {
			stmt.setString(1, seat);
			try (ResultSet result = stmt.executeQuery();) {
				next = createIndividual(result);
				while (next != null) {
					toReturn.add(next);
					next = createIndividual(result);
				}
			}
		}
		return toReturn;
	}

	/**
	 * Return a list of individuals with the given first name.
	 * 
	 * @param fname
	 *            The first name of the individuals to be returned.
	 * @return A list of individuals with the specified first name.
	 * @throws SQLException
	 */
	public synchronized List<Individual> getByFirstName(String fname) throws SQLException {
		ArrayList<Individual> toReturn = new ArrayList<Individual>();
		Individual next;
		try (PreparedStatement stmt = connection.prepareStatement(GET_BY_FNAME);) {
			stmt.setString(1, fname);
			try (ResultSet result = stmt.executeQuery();) {
				next = createIndividual(result);
				while (next != null) {
					toReturn.add(next);
					next = createIndividual(result);
				}
			}
		}
		return toReturn;
	}

	/**
	 * Return a list of individuals with the given last name.
	 * 
	 * @param lname
	 *            The last name of the individuals to be returned.
	 * @return A list of individuals with the specified last name.
	 * @throws SQLException
	 */
	public synchronized List<Individual> getByLastName(String lname) throws SQLException {
		ArrayList<Individual> toReturn = new ArrayList<Individual>();
		Individual next;
		try (PreparedStatement stmt = connection.prepareStatement(GET_BY_LNAME);) {
			stmt.setString(1, lname);
			try (ResultSet result = stmt.executeQuery();) {
				next = createIndividual(result);
				while (next != null) {
					toReturn.add(next);
					next = createIndividual(result);
				}
			}
		}
		return toReturn;
	}

	/**
	 * Return a list of individuals that have made a booking within the interval specified by the
	 * two dates.
	 * 
	 * @param firstDate
	 *            The earlier date.
	 * @param secondDate
	 *            The later date.
	 * @return A list of individuals, each of which has booked a ticket in the period from
	 *         <code>firstDate</code> and <code>lastDate</code>.
	 * @throws SQLException
	 */
	public synchronized List<Individual> getBetweenDates(Date firstDate, Date secondDate) throws SQLException {
		ArrayList<Individual> toReturn = new ArrayList<Individual>();
		Individual next;
		try (PreparedStatement stmt = connection.prepareStatement(GET_BETWEEN_DATES);) {
			stmt.setTimestamp(1, dateToSQLTimestamp(firstDate));
			stmt.setTimestamp(2, dateToSQLTimestamp(secondDate));
			try (ResultSet result = stmt.executeQuery();) {
				next = createIndividual(result);
				while (next != null) {
					toReturn.add(next);
					next = createIndividual(result);
				}
			}
		}
		return toReturn;
	}

	private Individual createIndividual(ResultSet result) throws SQLException {
		if (result.next()) {
			id = result.getLong(1);
			fname = result.getString(2);
			lname = result.getString(3);
			email = result.getString(4);
			event = result.getDate(5);
			seat = result.getString(6);
			return new Individual(id, fname, lname, email, event, seat, globalAttributeTable);
		} else {
			return null;
		}
	}

	protected static String formatDate(Date date) {
		if (date != null) {
			return new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(date);
		} else {
			return null;
		}
	}

	protected static String formatDate(java.sql.Date date) {
		if (date != null) {
			return date.toString();
		} else {
			return null;
		}
	}

	protected static java.sql.Date parseDate(String date) {
		if (date != null) {
			return java.sql.Date.valueOf(date);
		} else {
			return null;
		}
	}

	protected static Timestamp dateToSQLTimestamp(Date date) {
		if (date != null) {
			return new Timestamp(date.getTime());
		} else {
			return null;
		}
	}
}
