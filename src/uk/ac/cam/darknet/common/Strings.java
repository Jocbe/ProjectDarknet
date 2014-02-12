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
	public static final String	LOG_FORMAT_EXCEPTION		= "Exception occurred in conditional block of log formatter while trying to format a log entry! Check. Exception type: ";

	// Storage
	public static final String	STORAGE_CLASS_REL_URL		= "bin/uk/ac/cam/darknet/storage/";
	public static final String	STORAGE_REL_URL				= "storage/";

	// Properties
	public static final String	PROP_UNKNOWN_ATTR_EXN		= "Cannot add the unknown attribute '%1$s' to the properties of an individual.";
	public static final String	PROP_INVALID_TYPE_EXN		= "Cannot add the specified object as a '%1$s' attribute. The required type is %2$s.";

	// AttributeReliabilityPair
	public static final String	ARP_INVALID_RELIABILITY_EXN	= "Cannot add an attribute with the given reliability. The reliability must be a double precision floating point value between 0.0 and 1.0 inclusive.";

	// Date formats - DatabaseManager, LoggerFactory
	public static final String	DB_DATE_FORMAT				= "yyyy-MM-dd HH-mm-ss";
	public static final String	LOGGER_DATE_FORMAT			= "yyyy-mm-dd_hh:mm:ss";
}
