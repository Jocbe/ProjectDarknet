package uk.ac.cam.darknet.common;

/**
 * Externalised string resources used across the system. Each string should be public final.
 * 
 * @author Augustin Zidek
 */
@SuppressWarnings("javadoc")
public class Strings {
	// LoggerFactory
	public static final String	LOGGER_NAME					= "DarkLogger";
	public static final String	LOG_PATH					= "log/main.log";
	public static final String	LOG_EXCEPTION				= "IOException opening log file! ";

	// Storage
	public static final String	STORAGE_CLASS_REL_URL		= "bin/uk/ac/cam/darknet/storage/";
	public static final String	STORAGE_REL_URL				= "storage/";

	// Exceptions
	public static final String	UNKNOWN_ATTR_EXN			= "Cannot add the unknown attribute '%1$s' to the properties of an individual.";
	public static final String	INVALID_TYPE_EXN			= "Cannot add the specified object as a '%1$s' attribute. The required type is %2$s.";
	public static final String	INVALID_RELIABILITY_EXN		= "Cannot add an attribute with the given reliability. The reliability must be a double precision floating point value between 0.0 and 1.0 inclusive.";
	public static final String	INVALID_ATTRIBUTE_NAME_EXN	= "The attribute name %1$s is invalid.";
	public static final String	LOG_FORMAT_EXCEPTION		= "Exception occurred in conditional block of log formatter while trying to format a log entry! Check. Exception type: ";
	public static final String	NULL_GLOBAL_TABLE_EXN		= "The global attribute table cannot be null.";
	public static final String	REQUEST_NOT_SATISFIABLE		= "The request could not be satisfied.";

	// Date formats - DatabaseManager, LoggerFactory
	public static final String	DB_DATE_FORMAT				= "yyyy-MM-dd HH:mm:ss";
	public static final String	LOGGER_DATE_FORMAT			= "yyyy-mm-dd_hh:mm:ss";

	// GUI
	public static final String	GUI_DATE_FORMAT				= "yyyy-MM-dd HH:mm";
	public static final String	GUI_COMPUL_FLDS				= "Fields marked with an asterisk are compulsory.";
	public static final String	GUI_DATE_FORMAT_ERR			= "The date format is invalid. Use yyyy-MM-dd HH:mm.";
	public static final String	GUI_DB_ADD_ERR				= "It wasn't possible to add the individual to the database. Have you set all the fields correctly?";
	public static final String	GUI_DB_READ_ERR				= "It wasn't possible to load individuals from the database.";
	public static final String	GUI_DB_VEN_ERR				= "It wasn't possible to read the list of venues from the database.";
	public static final String	GUI_DB_CONN_ERR				= "It wasn't possible to connect to the database.";
	public static final String	GUI_CSV_ADD_ERR				= "It wasn't possible to open or parse the given CSV file. Have you selected the right one :-)?";
	public static final String	GUI_DB_CSV_ADD_ERR			= "It wasn't possible to load the individuals from the CSV file into the database. Check your CSV file.";
	public static final String GUI_SET_NEW_VENUE 			= "Please set a name for the new venue.";
	public static final String GUI_VENUE_ADD_ERR 			= "It wasn't possible to add venue to the database. Check your connection and spelling.";
	public static final String GUI_DB_DEL_IND_ERR 			= "It wasn't possible to delete this individual. Check your connection.";
	public static final String GUI_DONT_FILTER 				= "Don't filter";
	public static final String GUI_NO_VENUE_SEL 			= "No venue selected";
	public static final String GUI_ALL_SHOWS 				= "Collect for all shows";
	public static final String GUI_NO_COLLECTORS 			= "No collectors have been found on your system. That IMHO sucks.";
	public static final String GUI_COLLECTORS_ERR 			= "There has been error executing the collectors. Have you added new (buggy) collectors into your system?";
	public static final String GUI_SELECT_COLL 				= "Select collectors that shall be used.";
	public static final String GUI_SELECT_EFFECTS 			= "Select effects that shall be used.";
	public static final String GUI_EFFECTS_ERR 				= "There has been error execting the effects.";
	public static final String GUI_COLL_ERROR 				= "There was an error loading the collectors.";
	public static final String GUI_COLLECTION_STARTED 		= "Data collection started. Please be patient, this might take a while.";

	/**
	 * @return The URL of the project folder.
	 */
	public static String getBaseDir() {
		// Get URL of this class and get rid of the file:/ at the beginning
		final String classURL = Strings.class.getResource("").toString().replaceAll("file:/", "");
		// Get out of the bin/uk/ac/cam/darknet/...
		return classURL.replaceAll("bin/uk/ac/cam/darknet/[a-z]*/", "");
	}

}
