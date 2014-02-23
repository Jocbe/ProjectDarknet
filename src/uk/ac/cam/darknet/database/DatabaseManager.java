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
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Pattern;
import uk.ac.cam.darknet.common.AttributeCategories;
import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.common.Show;
import uk.ac.cam.darknet.common.Strings;
import uk.ac.cam.darknet.common.Venue;
import uk.ac.cam.darknet.exceptions.ConfigFileNotFoundException;
import com.sun.xml.internal.ws.org.objectweb.asm.Type;

/**
 * Any implementation of this abstract class has methods to search for individuals. Both the data
 * collection manager and effects should use implementations of this class to search for
 * individuals.
 * 
 * @author Ibtehaj Nadeem
 */
public abstract class DatabaseManager {
	private static String									GET_BY_ID			= "SELECT * FROM individuals WHERE id = ?";
	private static String									GET_BY_SHOW			= "SELECT * FROM individuals WHERE date = ? and venue = ?";
	private static String									GET_BY_SEAT			= "SELECT * FROM individuals WHERE seat = ?";
	private static String									GET_BY_EMAIL		= "SELECT * FROM individuals WHERE email = ?";
	private static String									GET_BY_FNAME		= "SELECT * FROM individuals WHERE fname = ?";
	private static String									GET_BY_LNAME		= "SELECT * FROM individuals WHERE lname = ?";
	private static String									GET_ALL_INDIVIDUALS	= "SELECT * FROM individuals";
	private static String									GET_ALL_SHOWS		= "SELECT shows.date, venues.id, venues.name FROM shows NATURAL JOIN venues";
	private static String									GET_ALL_VENUES		= "SELECT * FROM venues";
	private static final String								ATTRIBUTE_PATTERN	= "[a-zA-Z0-9_]+";
	protected final Connection								connection;
	protected final Hashtable<String, AttributeCategories>	globalAttributeTable;
	private long											id;
	private String											fname;
	private String											lname;
	private String											email;
	private Date											date;
	private int												venue;
	private String											seat;
	private Pattern											pattern				= Pattern.compile(ATTRIBUTE_PATTERN);

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
	protected DatabaseManager(Hashtable<String, AttributeCategories> globalAttributeTable) throws ConfigFileNotFoundException, IOException, ClassNotFoundException, SQLException {
		// if (globalAttributeTable == null) {
		// throw new IllegalArgumentException(Strings.NULL_GLOBAL_TABLE_EXN);
		// } else {
		this.globalAttributeTable = globalAttributeTable;
		// }
		connection = connectToDB(Strings.getBaseDir() + "/res/dbconfig.txt");
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
	 * Return a list of all individuals in the system.
	 * 
	 * @return A list containing all the individuals in the system.
	 * @throws SQLException
	 */
	public synchronized List<Individual> getAllIndividuals() throws SQLException {
		ArrayList<Individual> toReturn = new ArrayList<Individual>();
		try (PreparedStatement stmt = connection.prepareStatement(GET_ALL_INDIVIDUALS);) {
			toReturn = getIndividualQueryResults(stmt);
		}
		return toReturn;
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
	 * Return a list of individuals with the given first name.
	 * 
	 * @param fname
	 *            The first name of the individuals to be returned.
	 * @return A list of individuals with the specified first name.
	 * @throws SQLException
	 */
	public synchronized List<Individual> getByFirstName(String fname) throws SQLException {
		ArrayList<Individual> toReturn = new ArrayList<Individual>();
		try (PreparedStatement stmt = connection.prepareStatement(GET_BY_FNAME);) {
			stmt.setString(1, fname);
			toReturn = getIndividualQueryResults(stmt);
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
		try (PreparedStatement stmt = connection.prepareStatement(GET_BY_LNAME);) {
			stmt.setString(1, lname);
			toReturn = getIndividualQueryResults(stmt);
		}
		return toReturn;
	}

	/**
	 * Return a list of individuals with the specified email address.
	 * 
	 * @param email
	 *            The email address of the individual.
	 * @return A list of individuals who have the specified email address.
	 * @throws SQLException
	 */
	public synchronized List<Individual> getByEmail(String email) throws SQLException {
		ArrayList<Individual> toReturn = new ArrayList<Individual>();
		try (PreparedStatement stmt = connection.prepareStatement(GET_BY_EMAIL);) {
			stmt.setString(1, email);
			toReturn = getIndividualQueryResults(stmt);
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
		try (PreparedStatement stmt = connection.prepareStatement(GET_BY_SEAT);) {
			stmt.setString(1, seat);
			toReturn = getIndividualQueryResults(stmt);
		}
		return toReturn;
	}

	/**
	 * Return a list of individuals that attend a particular show (combination of date and venue).
	 * 
	 * @param eventDate
	 *            The date and time of the show of which all individuals are to be fetched.
	 * @param eventVenue
	 *            The venue ID of the show.
	 * @return A list of individuals, each of which has booked a ticket for the given show.
	 * @throws SQLException
	 */
	public synchronized List<Individual> getByShow(Date eventDate, int eventVenue) throws SQLException {
		ArrayList<Individual> toReturn = new ArrayList<Individual>();
		try (PreparedStatement stmt = connection.prepareStatement(GET_BY_SHOW);) {
			stmt.setTimestamp(1, dateToSQLTimestamp(eventDate));
			stmt.setInt(2, eventVenue);
			toReturn = getIndividualQueryResults(stmt);
		}
		return toReturn;
	}

	/**
	 * Return a list of individuals that attend a particular show (combination of date and venue).
	 * 
	 * @param show The show for which to find infividuals.
	 * @return A list of individuals, each of which has booked a ticket for the given show.
	 * @throws SQLException
	 */
	public synchronized List<Individual> getByShow(Show show) throws SQLException {
		return getByShow(show.getDate(), show.getVenue().getId());
	}

	/**
	 * Get all the shows in the system.
	 * 
	 * @return A list of all the shows in the system.
	 * @throws SQLException
	 */
	public ArrayList<Show> getAllShows() throws SQLException {
		ArrayList<Show> toReturn = new ArrayList<Show>();
		Show next;
		try (PreparedStatement stmt = connection.prepareStatement(GET_ALL_SHOWS);) {
			try (ResultSet resultSet = stmt.executeQuery();) {
				while (resultSet.next()) {
					next = new Show(resultSet.getTimestamp(1), new Venue(resultSet.getInt(2), resultSet.getString(3)));
					toReturn.add(next);
				}
			}
		}
		return toReturn;
	}

	/**
	 * Returns a list of all venues in the system.
	 * 
	 * @return A list of venues in the system.
	 * @throws SQLException
	 */
	public ArrayList<Venue> getAllVenues() throws SQLException {
		ArrayList<Venue> toReturn = new ArrayList<Venue>();
		Venue next;
		try (PreparedStatement stmt = connection.prepareStatement(GET_ALL_VENUES);) {
			try (ResultSet resultSet = stmt.executeQuery();) {
				while (resultSet.next()) {
					next = new Venue(resultSet.getInt(1), resultSet.getString(2));
					toReturn.add(next);
				}
			}
		}
		return toReturn;
	}

	private ArrayList<Individual> getIndividualQueryResults(PreparedStatement stmt) throws SQLException {
		ArrayList<Individual> toReturn = new ArrayList<Individual>();
		Individual next;
		try (ResultSet resultSet = stmt.executeQuery();) {
			next = createIndividual(resultSet);
			while (next != null) {
				toReturn.add(next);
				next = createIndividual(resultSet);
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
			date = result.getTimestamp(5);
			venue = result.getInt(6);
			seat = result.getString(7);
			return new Individual(id, fname, lname, email, date, venue, seat, globalAttributeTable);
		} else {
			return null;
		}
	}

	protected static String formatDate(Date date) {
		if (date != null) {
			return new SimpleDateFormat(Strings.DB_DATE_FORMAT).format(date);
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

	protected boolean isAttributeNameValid(String attributeName) {
		if (attributeName == null || attributeName.length() == 0 || (!pattern.matcher(attributeName).matches())) {
			return false;
		} else {
			return true;
		}
	}

	protected int getSQLType(AttributeCategories category) {
		if (category.getAttributeType() == Byte.class) {
			return Types.TINYINT;
		} else if (category.getAttributeType() == Short.class) {
			return Types.SMALLINT;
		} else if (category.getAttributeType() == Integer.class) {
			return Types.INTEGER;
		} else if (category.getAttributeType() == Long.class) {
			return Types.BIGINT;
		} else if (category.getAttributeType() == Boolean.class) {
			return Types.BOOLEAN;
		} else {
			return Types.OTHER;
		}
	}

	protected String getSQLTypeString(int SQLType) {
		switch (SQLType) {
			case Types.TINYINT :
				return "TINYINT";
			case Types.SMALLINT :
				return "SMALLINT";
			case Types.INTEGER :
				return "INTEGER";
			case Types.BIGINT :
				return "BIGINT";
			case Type.BOOLEAN :
				return "BOOLEAN";
			default :
				return "OTHER";
		}
	}
}