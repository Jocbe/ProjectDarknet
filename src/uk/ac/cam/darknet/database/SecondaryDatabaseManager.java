package uk.ac.cam.darknet.database;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Pattern;
import uk.ac.cam.darknet.common.AttributeCategories;
import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.common.Strings;
import uk.ac.cam.darknet.exceptions.ConfigFileNotFoundException;
import uk.ac.cam.darknet.exceptions.InvalidAttributeNameException;
import com.sun.xml.internal.ws.org.objectweb.asm.Type;

/**
 * A secondary database manager is used to provide database access to the secondary data collectors.
 * 
 * @author Ibtehaj Nadeem
 */
public class SecondaryDatabaseManager extends DatabaseManager {
	private static final String	CREATE_SECONDARY_TABLE	= "CREATE CACHED TABLE %1$s (id BIGINT, attribute %2$s, reliability DOUBLE PRECISION, FOREIGN KEY (id) REFERENCES individuals(id), CHECK (reliability >= 0 AND reliability <= 1), UNIQUE(id, attribute))";
	private static final String	INSERT_ATTRIBUTE		= "INSERT INTO %1$s (id, attribute, reliability) VALUES (?, ?, ?)";
	private static final String	ATTRIBUTE_PATTERN		= "[a-zA-Z0-9_]+";
	private Pattern				pattern					= Pattern.compile(ATTRIBUTE_PATTERN);

	/**
	 * Creates a new <code>SecondaryDatabaseManager</code> with the specified global attribute table
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
	 * @throws InvalidAttributeNameException
	 */
	public SecondaryDatabaseManager(Hashtable<String, AttributeCategories> globalAttributeTable, String pathToConfig) throws ConfigFileNotFoundException, IOException, ClassNotFoundException, SQLException, InvalidAttributeNameException {
		super(globalAttributeTable, pathToConfig);
		createTables();
	}

	private void createTables() throws SQLException, InvalidAttributeNameException {
		Enumeration<String> attributeNames = globalAttributeTable.keys();
		String currentAttributeName;
		AttributeCategories currentCategory;
		while (attributeNames.hasMoreElements()) {
			currentAttributeName = attributeNames.nextElement();
			currentCategory = globalAttributeTable.get(currentAttributeName);
			if (isAttributeNameValid(currentAttributeName)) {
				try (Statement stmt = connection.createStatement();) {
					stmt.execute(String.format(CREATE_SECONDARY_TABLE, currentAttributeName, getSQLTypeString(getSQLType(currentCategory))));
				} catch (SQLException e) {
					// Table already exists.
				}
			} else {
				connection.rollback();
				throw new InvalidAttributeNameException(String.format(Strings.INVALID_ATTRIBUTE_NAME_EXN, currentAttributeName));
			}
		}
		connection.commit();
	}

	/**
	 * Stores the attributes of a list of individuals in the database.
	 * 
	 * @param individuals
	 *            The list of individuals with <code>Properties</code> objects containing the
	 *            attributes to be stored.
	 */
	public synchronized void storeAttributes(List<Individual> individuals) {
		Enumeration<String> attributeNames = globalAttributeTable.keys();
		// Iterator<Individual> iterator;
		String currentAttributeName;
		while (attributeNames.hasMoreElements()) {
			currentAttributeName = attributeNames.nextElement();
			try (PreparedStatement stmt = connection.prepareStatement(String.format(INSERT_ATTRIBUTE, currentAttributeName));) {
				// TODO
			} catch (SQLException e) {
				// TODO Auto-generated catch block THIS IS TEMPORARY!!!
				e.printStackTrace();
			}
		}
	}

	private boolean isAttributeNameValid(String attributeName) {
		if (attributeName == null || attributeName.length() == 0 || (!pattern.matcher(attributeName).matches())) {
			return false;
		} else {
			return true;
		}
	}

	private int getSQLType(AttributeCategories category) {
		if (category.getAttributeType() == byte.class) {
			return Types.TINYINT;
		} else if (category.getAttributeType() == short.class) {
			return Types.SMALLINT;
		} else if (category.getAttributeType() == int.class) {
			return Types.INTEGER;
		} else if (category.getAttributeType() == long.class) {
			return Types.BIGINT;
		} else if (category.getAttributeType() == boolean.class) {
			return Types.BOOLEAN;
		} else {
			return Types.OTHER;
		}
	}

	private String getSQLTypeString(int SQLType) {
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

	@SuppressWarnings({"javadoc", "unused"})
	public static void main(String args[]) throws ClassNotFoundException, ConfigFileNotFoundException, IOException, SQLException, InvalidAttributeNameException {
		Hashtable<String, AttributeCategories> myTable = new Hashtable<String, AttributeCategories>();
		myTable.put("test", AttributeCategories.USER_NAME);
		SecondaryDatabaseManager instance = new SecondaryDatabaseManager(myTable, args[0]);
	}
}