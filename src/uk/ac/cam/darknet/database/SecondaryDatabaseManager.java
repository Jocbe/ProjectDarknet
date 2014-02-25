package uk.ac.cam.darknet.database;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import uk.ac.cam.darknet.common.AttributeCategories;
import uk.ac.cam.darknet.common.AttributeReliabilityPair;
import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.common.IndividualRequirements;
import uk.ac.cam.darknet.common.LoggerFactory;
import uk.ac.cam.darknet.common.Show;
import uk.ac.cam.darknet.common.Strings;
import uk.ac.cam.darknet.exceptions.ConfigFileNotFoundException;
import uk.ac.cam.darknet.exceptions.InvalidAttributeNameException;
import uk.ac.cam.darknet.exceptions.InvalidAttributeTypeException;
import uk.ac.cam.darknet.exceptions.InvalidReliabilityException;
import uk.ac.cam.darknet.exceptions.RequestNotSatisfiableException;
import uk.ac.cam.darknet.exceptions.UnknownAttributeException;

/**
 * A secondary database manager is used to provide database access to the secondary data collectors.
 * 
 * @author Ibtehaj Nadeem
 */
public class SecondaryDatabaseManager extends DatabaseManager {
	private static final String	CREATE_SECONDARY_TABLE	= "CREATE CACHED TABLE %1$s (id BIGINT NOT NULL, attribute OTHER NOT NULL, reliability DOUBLE PRECISION NOT NULL, FOREIGN KEY (id) REFERENCES individuals(id), CHECK (reliability >= 0 AND reliability <= 1))";
	private static final String	INSERT_ATTRIBUTE		= "INSERT INTO %1$s (id, attribute, reliability) VALUES (?, ?, ?)";

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
	public SecondaryDatabaseManager(Hashtable<String, AttributeCategories> globalAttributeTable) throws ConfigFileNotFoundException, IOException, ClassNotFoundException, SQLException, InvalidAttributeNameException {
		super(globalAttributeTable);
		createTables();
	}

	private void createTables() throws SQLException, InvalidAttributeNameException {
		Enumeration<String> attributeNames = globalAttributeTable.keys();
		String currentAttributeName;
		while (attributeNames.hasMoreElements()) {
			currentAttributeName = attributeNames.nextElement();
			if (isAttributeNameValid(currentAttributeName)) {
				try (Statement stmt = connection.createStatement();) {
					stmt.execute(String.format(CREATE_SECONDARY_TABLE, currentAttributeName));
				} catch (SQLException e) {
					// Table already exists.
					LoggerFactory.getLogger().info(e.getMessage());
				}
			} else {
				connection.rollback();
				throw new InvalidAttributeNameException(String.format(Strings.INVALID_ATTRIBUTE_NAME_EXN, currentAttributeName));
			}
		}
		connection.commit();
	}

	/**
	 * Stores the attributes of a list of individuals in the database. The changes are committed
	 * atomically. If there is an error, the changes are rolled back.
	 * 
	 * @param individuals
	 *            The list of individuals with <code>Properties</code> objects containing the
	 *            attributes to be stored.
	 * @throws SQLException
	 */
	public synchronized void storeAttributes(List<Individual> individuals) throws SQLException {
		Enumeration<String> attributeNames = globalAttributeTable.keys();
		Iterator<Individual> iterator;
		String currentAttributeName;
		Individual currentIndividual;
		List<AttributeReliabilityPair> currentAttrList;
		// Note that the time spent executing this method is dominated by SQL. Iterate
		// over attribute names first rather than individuals to exploit prepared statements.
		while (attributeNames.hasMoreElements()) {
			currentAttributeName = attributeNames.nextElement();
			try (PreparedStatement stmt = connection.prepareStatement(String.format(INSERT_ATTRIBUTE, currentAttributeName));) {
				iterator = individuals.iterator();
				while (iterator.hasNext()) {
					currentIndividual = iterator.next();
					if (currentIndividual.containsAttribute(currentAttributeName)) {
						stmt.setLong(1, currentIndividual.getId());
						currentAttrList = currentIndividual.getAttribute(currentAttributeName);
						for (AttributeReliabilityPair currentAttrRel : currentAttrList) {
							stmt.setObject(2, currentAttrRel.getAttribute());
							stmt.setDouble(3, currentAttrRel.getReliability());
							try {
								stmt.execute();
							} catch (SQLException e) {
								LoggerFactory.getLogger().info(e.getMessage());
							}
						}
					}
				}
			} catch (SQLException e) {
				connection.rollback();
				throw e;
			}
		}
		connection.commit();
	}

	@SuppressWarnings({"javadoc"})
	public static void main(String[] args) throws ClassNotFoundException, ConfigFileNotFoundException, IOException, SQLException, InvalidAttributeNameException, UnknownAttributeException, InvalidAttributeTypeException, InvalidReliabilityException, RequestNotSatisfiableException {
		Hashtable<String, AttributeCategories> globalAttributeTable = new Hashtable<String, AttributeCategories>();
		globalAttributeTable.put("test1", AttributeCategories.ALIAS);
		globalAttributeTable.put("test2", AttributeCategories.AGE);
		SecondaryDatabaseManager instance = new SecondaryDatabaseManager(globalAttributeTable);
		Show show = instance.getAllShows().get(2);
		ArrayList<Individual> individuals = (ArrayList<Individual>) instance.getByShow(show);
		Random random = new Random();
		for (Individual i : individuals) {
			if (random.nextDouble() < 1)
				i.addAttribute("test1", "aliasA" + (i.getId() - 1000000), random.nextDouble());
			if (random.nextDouble() < 1)
				i.addAttribute("test1", "aliasB" + (i.getId() - 1000000), random.nextDouble());
			if (random.nextDouble() < 1)
				i.addAttribute("test2", (byte) (i.getId() - 1000000), random.nextDouble());
			if (random.nextDouble() < 1)
				i.addAttribute("test2", (byte) (-(i.getId() - 1000000)), random.nextDouble());
		}
		// instance.storeAttributes(individuals);
		IndividualRequirements requirements = new IndividualRequirements(show);
		requirements.addRequirement(AttributeCategories.ALIAS, 0.5);
		requirements.addRequirement(AttributeCategories.AGE, 0.5);
		individuals = (ArrayList<Individual>) instance.getSuitableIndividuals(requirements);
		for (Individual i : individuals) {
			if (i.containsAttribute("test1")) {
				System.out.print("Individual " + i.getId() + ": ALIAS ");
				for (AttributeReliabilityPair a : i.getAttribute("test1")) {
					System.out.print(a.getAttribute() + " ");
				}
				System.out.print("\n");
			}
			if (i.containsAttribute("test2")) {
				System.out.print("Individual " + i.getId() + ": AGE ");
				for (AttributeReliabilityPair a : i.getAttribute("test2")) {
					System.out.print(a.getAttribute() + " ");
				}
				System.out.print("\n");
			}
		}
		instance.closeConnection();
	}
}