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
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Pattern;
import uk.ac.cam.darknet.common.AttributeCategories;
import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.common.IndividualRequirements;
import uk.ac.cam.darknet.common.Show;
import uk.ac.cam.darknet.common.Strings;
import uk.ac.cam.darknet.common.Venue;
import uk.ac.cam.darknet.exceptions.ConfigFileNotFoundException;
import uk.ac.cam.darknet.exceptions.InvalidAttributeTypeException;
import uk.ac.cam.darknet.exceptions.InvalidReliabilityException;
import uk.ac.cam.darknet.exceptions.RequestNotSatisfiableException;
import uk.ac.cam.darknet.exceptions.UnknownAttributeException;

/**
 * Any implementation of this abstract class has methods to search for individuals. Both the data
 * collection manager and effects should use implementations of this class to search for
 * individuals.
 * 
 * @author Ibtehaj Nadeem
 */
public class DatabaseManager {
	private static final String								GET_BY_ID			= "SELECT * FROM individuals WHERE id = ?";
	private static final String								GET_BY_SHOW			= "SELECT * FROM individuals WHERE date = ? AND venue = ?";
	private static final String								GET_BY_SEAT			= "SELECT * FROM individuals WHERE seat = ?";
	private static final String								GET_BY_EMAIL		= "SELECT * FROM individuals WHERE email = ?";
	private static final String								GET_BY_FNAME		= "SELECT * FROM individuals WHERE fname = ?";
	private static final String								GET_BY_LNAME		= "SELECT * FROM individuals WHERE lname = ?";
	private static final String								GET_ALL_INDIVIDUALS	= "SELECT * FROM individuals";
	private static final String								GET_ALL_SHOWS		= "SELECT shows.date, venues.id, venues.name FROM shows JOIN venues ON venues.id = shows.venue ORDER BY venues.id, shows.date";
	private static final String								GET_ALL_VENUES		= "SELECT * FROM venues";
	private static final String								CREATE_FILTER_TABLE	= "DECLARE LOCAL TEMPORARY TABLE filter AS (SELECT * FROM INDIVIDUALS WHERE date = '%1$s' AND venue = %2$d) WITH DATA";
	private static final String								DROP_FILTER_TABLE	= "DROP TABLE IF EXISTS session.filter";
	private static final String								CREATE_TEMP_TABLE	= "DECLARE LOCAL TEMPORARY TABLE temp%1$d AS (%2$s) WITH DATA";
	private static final String								DROP_TEMP_TABLE		= "DROP TABLE IF EXISTS session.temp%1$d";
	private static final String								SELECT_FILTERED		= "SELECT DISTINCT filter.* FROM filter";
	private static final String								FILTER_JOIN			= " JOIN temp%1$d ON temp%1$d.id = filter.id";
	private static final String								GET_ATTRIBUTE		= "SELECT attribute, reliability FROM %1$s WHERE id = ?";
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
	public DatabaseManager(Hashtable<String, AttributeCategories> globalAttributeTable) throws ConfigFileNotFoundException, IOException, ClassNotFoundException, SQLException {
		if (globalAttributeTable == null) {
			throw new IllegalArgumentException(Strings.NULL_GLOBAL_TABLE_EXN);
		} else {
			this.globalAttributeTable = globalAttributeTable;
		}
		this.connection = this.connectToDB("res/dbconfig.txt");
		// Use this line instead for absolute paths.
		// connection = connectToDB(Strings.getBaseDir() + "/res/dbconfig.txt");
		this.connection.setAutoCommit(false);
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
	 * Closes the underlying database connection. This object should not be reused afterwards.
	 * 
	 * @throws SQLException
	 */
	public synchronized void closeConnection() throws SQLException {
		this.connection.close();
	}

	/**
	 * Return a list of all individuals in the system.
	 * 
	 * @return A list containing all the individuals in the system.
	 * @throws SQLException
	 */
	public synchronized List<Individual> getAllIndividuals() throws SQLException {
		ArrayList<Individual> toReturn = new ArrayList<Individual>();
		try (PreparedStatement stmt = this.connection.prepareStatement(GET_ALL_INDIVIDUALS);) {
			toReturn = this.getIndividualQueryResults(stmt);
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
		try (PreparedStatement stmt = this.connection.prepareStatement(GET_BY_ID);) {
			stmt.setLong(1, id);
			try (ResultSet result = stmt.executeQuery();) {
				toReturn = this.createIndividual(result);
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
		try (PreparedStatement stmt = this.connection.prepareStatement(GET_BY_FNAME);) {
			stmt.setString(1, fname);
			toReturn = this.getIndividualQueryResults(stmt);
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
		try (PreparedStatement stmt = this.connection.prepareStatement(GET_BY_LNAME);) {
			stmt.setString(1, lname);
			toReturn = this.getIndividualQueryResults(stmt);
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
		try (PreparedStatement stmt = this.connection.prepareStatement(GET_BY_EMAIL);) {
			stmt.setString(1, email);
			toReturn = this.getIndividualQueryResults(stmt);
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
		try (PreparedStatement stmt = this.connection.prepareStatement(GET_BY_SEAT);) {
			stmt.setString(1, seat);
			toReturn = this.getIndividualQueryResults(stmt);
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
		try (PreparedStatement stmt = this.connection.prepareStatement(GET_BY_SHOW);) {
			stmt.setTimestamp(1, dateToSQLTimestamp(eventDate));
			stmt.setInt(2, eventVenue);
			toReturn = this.getIndividualQueryResults(stmt);
		}
		return toReturn;
	}

	/**
	 * Return a list of individuals that attend a particular show (combination of date and venue).
	 * 
	 * @param show
	 *            The show for which to find infividuals.
	 * @return A list of individuals, each of which has booked a ticket for the given show.
	 * @throws SQLException
	 */
	public synchronized List<Individual> getByShow(Show show) throws SQLException {
		return this.getByShow(show.getDate(), show.getVenue().getId());
	}

	/**
	 * Get all the shows in the system.
	 * 
	 * @return A list of all the shows in the system.
	 * @throws SQLException
	 */
	public synchronized List<Show> getAllShows() throws SQLException {
		ArrayList<Show> toReturn = new ArrayList<Show>();
		Show next;
		try (PreparedStatement stmt = this.connection.prepareStatement(GET_ALL_SHOWS);) {
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
	public synchronized List<Venue> getAllVenues() throws SQLException {
		ArrayList<Venue> toReturn = new ArrayList<Venue>();
		Venue next;
		try (PreparedStatement stmt = this.connection.prepareStatement(GET_ALL_VENUES);) {
			try (ResultSet resultSet = stmt.executeQuery();) {
				while (resultSet.next()) {
					next = new Venue(resultSet.getInt(1), resultSet.getString(2));
					toReturn.add(next);
				}
			}
		}
		return toReturn;
	}

	/**
	 * This method adds all the attributes stored about each individual to their respective
	 * <code>Properties</code> objects.
	 * 
	 * @param individuals
	 *            The list of individuals for which to get attributes.
	 * @throws SQLException
	 * @throws InvalidReliabilityException
	 * @throws InvalidAttributeTypeException
	 * @throws UnknownAttributeException
	 */
	public void getAllAttributes(List<Individual> individuals) throws SQLException, UnknownAttributeException, InvalidAttributeTypeException, InvalidReliabilityException {
		Enumeration<String> attributes = this.globalAttributeTable.keys();
		String currentAttributeName;
		double currentReliability;
		Object currentAttribute;
		// First clear the attributes of the individuals to avoid duplicate entries.
		for (Individual currentIndividual : individuals) {
			currentIndividual.clearAttributes();
		}
		// Now go through the global attribute table attribute-by-attribute, and, for each
		// individual, add the information in the database to the individual's properties.
		while (attributes.hasMoreElements()) {
			currentAttributeName = attributes.nextElement();
			try (PreparedStatement stmt = this.connection.prepareStatement(String.format(GET_ATTRIBUTE, currentAttributeName));) {
				for (Individual currentIndividual : individuals) {
					stmt.setLong(1, currentIndividual.getId());
					try (ResultSet resultSet = stmt.executeQuery();) {
						while (resultSet.next()) {
							currentAttribute = resultSet.getObject(1, this.globalAttributeTable.get(currentAttributeName).getAttributeType());
							currentReliability = resultSet.getDouble(2);
							currentIndividual.addAttribute(currentAttributeName, this.globalAttributeTable.get(currentAttributeName).getAttributeType().cast(currentAttribute), currentReliability);
						}
					}
				}
			}
		}
	}

	/**
	 * Returns a list of suitable individuals for an effect. The list will be sorted roughly by the
	 * overall suitability, from most suitable to least suitable.
	 * 
	 * @param requirements
	 *            The requirements that the effect places on the individuals.
	 * @return A list of individuals that satisfy the given requirements, arranged in no particular
	 *         order.
	 * @throws SQLException
	 * @throws RequestNotSatisfiableException
	 * @throws InvalidReliabilityException
	 * @throws InvalidAttributeTypeException
	 * @throws UnknownAttributeException
	 */
	public synchronized List<Individual> getSuitableIndividuals(IndividualRequirements requirements) throws SQLException, RequestNotSatisfiableException, UnknownAttributeException, InvalidAttributeTypeException, InvalidReliabilityException {
		ArrayList<String> attributes = new ArrayList<String>();
		ArrayList<Individual> toReturn;
		Enumeration<AttributeCategories> categories = requirements.getRequiredCategories().keys();
		AttributeCategories currentCategory;
		double currentMinReliability;
		int tableCounter = 0;
		String statement = SELECT_FILTERED;
		try (Statement stmt = this.connection.createStatement();) {
			stmt.execute(String.format(CREATE_FILTER_TABLE, formatDate(requirements.getShow().getDate()), requirements.getShow().getVenue().getId()));
			while (categories.hasMoreElements()) {
				currentCategory = categories.nextElement();
				currentMinReliability = requirements.getRequiredCategories().get(currentCategory);
				this.filterAttributes(currentCategory, attributes);
				if (attributes.isEmpty())
					throw new RequestNotSatisfiableException(Strings.REQUEST_NOT_SATISFIABLE);
				this.createTemporaryTable(attributes, currentMinReliability, tableCounter++);
			}
			for (int i = 0; i < tableCounter; i++) {
				statement += String.format(FILTER_JOIN, i);
			}
			toReturn = this.getIndividualQueryResults(stmt, statement);
		} finally {
			try (Statement stmt = this.connection.createStatement();) {
				stmt.execute(DROP_FILTER_TABLE);
				for (int i = 0; i < tableCounter; i++) {
					stmt.execute(String.format(DROP_TEMP_TABLE, i));
				}
			}
		}
		if (toReturn.size() == 0)
			throw new RequestNotSatisfiableException(Strings.REQUEST_NOT_SATISFIABLE);
		this.getAllAttributes(toReturn);
		return toReturn;
	}

	private void createTemporaryTable(ArrayList<String> attributes, double minReliability, int tableCounter) throws SQLException {
		ArrayList<String> subQueries = new ArrayList<String>();
		String finalSubQuery;
		String statement;
		for (String a : attributes) {
			subQueries.add("(SELECT id FROM " + a + " WHERE reliability >= " + minReliability + ")");
		}
		finalSubQuery = subQueries.get(0);
		for (int i = 1; i < subQueries.size(); i++) {
			finalSubQuery += " UNION " + subQueries.get(i);
		}
		statement = String.format(CREATE_TEMP_TABLE, tableCounter, finalSubQuery);
		try (Statement stmt = this.connection.createStatement();) {
			stmt.execute(statement);
		}
	}

	private void filterAttributes(AttributeCategories filter, ArrayList<String> attributes) {
		attributes.clear();
		Enumeration<String> allAttributes = this.globalAttributeTable.keys();
		String currentAttribute;
		while (allAttributes.hasMoreElements()) {
			currentAttribute = allAttributes.nextElement();
			if (this.globalAttributeTable.get(currentAttribute) == filter)
				attributes.add(currentAttribute);
		}
	}

	private ArrayList<Individual> getIndividualQueryResults(PreparedStatement stmt) throws SQLException {
		ArrayList<Individual> toReturn = new ArrayList<Individual>();
		Individual next;
		try (ResultSet resultSet = stmt.executeQuery();) {
			next = this.createIndividual(resultSet);
			while (next != null) {
				toReturn.add(next);
				next = this.createIndividual(resultSet);
			}
		}
		return toReturn;
	}

	private ArrayList<Individual> getIndividualQueryResults(Statement stmt, String query) throws SQLException {
		ArrayList<Individual> toReturn = new ArrayList<Individual>();
		Individual next;
		try (ResultSet resultSet = stmt.executeQuery(query);) {
			next = this.createIndividual(resultSet);
			while (next != null) {
				toReturn.add(next);
				next = this.createIndividual(resultSet);
			}
		}
		return toReturn;
	}

	private Individual createIndividual(ResultSet result) throws SQLException {
		if (result.next()) {
			this.id = result.getLong(1);
			this.fname = result.getString(2);
			this.lname = result.getString(3);
			this.email = result.getString(4);
			this.date = result.getTimestamp(5);
			this.venue = result.getInt(6);
			this.seat = result.getString(7);
			return new Individual(this.id, this.fname, this.lname, this.email, this.date, this.venue, this.seat, this.globalAttributeTable);
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
		if (attributeName == null || attributeName.length() == 0 || (!this.pattern.matcher(attributeName).matches())) {
			return false;
		} else {
			return true;
		}
	}
}