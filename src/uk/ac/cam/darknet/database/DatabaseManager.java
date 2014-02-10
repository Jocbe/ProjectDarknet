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
 * This class serves as an abstraction layer sitting above the database. In the system, we allow
 * only this class to communicate with the database, hence it provides all the methods that are
 * needed.
 * 
 * @author Ibtehaj Nadeem
 */
public abstract class DatabaseManager {
	private static String									GET_BY_ID			= "SELECT * FROM individuals WHERE id = ?";
	private static String									GET_BY_EVENT_DATE	= "SELECT * FROM individuals WHERE event = ?";
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
