package uk.ac.cam.darknet.database;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Hashtable;
import uk.ac.cam.darknet.common.AttributeCategories;
import uk.ac.cam.darknet.exceptions.ConfigFileNotFoundException;

/**
 * This class serves as an abstraction layer sitting above the database. In the system, we allow
 * only this class to communicate with the database, hence it provides all the methods that are
 * needed.
 * 
 * @author Ibtehaj Nadeem
 */
public abstract class DatabaseManager {
	protected final Connection								connection;
	protected final Hashtable<String, AttributeCategories>	globalAttributeTable;

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
}
