package uk.ac.cam.darknet.common;

/**
 * The set of attribute categories supported by the system. Each data collector provides a list of
 * attributes it supports, indicating for each one which category it falls into.
 * 
 * @author Ibtehaj Nadeem
 */
public enum AttributeCategories {
	/**
	 * First names of individuals (<code>String</code>)
	 */
	FISRT_NAME(String.class),
	/**
	 * Last names of individuals (<code>String</code>)
	 */
	LAST_NAME(String.class),
	/**
	 * User names that individuals use on various websites (<code>String</code>)
	 */
	USER_NAME(String.class),
	/**
	 * Email addresses of individuals (<code>String</code>)
	 */
	EMAIL(String.class),
	/**
	 * Paths to photos of individuals stored in the photo store on disk. (<code>String</code>)
	 */
	PHOTO(String.class),
	/**
	 * The age of individuals
	 */
	AGE(byte.class),
	/**
	 * A comma-separated list of strings which are related to the individual (<code>String</code>)
	 */
	CS_STRING_LIST(String.class),
	/**
	 * A string attribute of an individual (<code>String</code>)
	 */
	STRING(String.class);

	private final Class<?>	attributeType;
	private final String	sqlType;

	AttributeCategories(Class<?> attributeType) {
		this.attributeType = attributeType;
		if (attributeType == byte.class) {
			this.sqlType = "TINYINT";
		} else if (attributeType == short.class) {
			this.sqlType = "SMALLINT";
		} else if (attributeType == int.class) {
			this.sqlType = "INTEGER";
		} else if (attributeType == long.class) {
			this.sqlType = "BIGINT";
		} else if (attributeType == boolean.class) {
			this.sqlType = "BOOLEAN";
		} else {
			this.sqlType = "OTHER";
		}
	}

	/**
	 * This method returns the class object of the type that a particular attribute type should be
	 * interpreted as.
	 * 
	 * @return The class object for the appropriate datatype of the attribute type.
	 */
	public Class<?> getAttributeType() {
		return attributeType;
	}

	/**
	 * Returns the string representation of the appropriate SQL type to use for this attribute
	 * category.
	 * 
	 * @return A string literal that can be embedded in SQL statements to specify an SQL type.
	 */
	public String getSQLTypeString() {
		return sqlType;
	}

	/**
	 * Checks whether a given object can be cast to an object suitable for representing this
	 * attribute type.
	 * 
	 * @param toCheck
	 *            The object whose compatibility must be verified.
	 * @return True if the object is valid, false otherwise.
	 */
	public boolean isObjectCompatible(Object toCheck) {
		return attributeType.isInstance(toCheck);
	}

	/**
	 * Get the name of the class used to store attributes of this category.
	 * 
	 * @return Name of the class that should be used to represent this attribute category.
	 */
	public String getClassName() {
		return attributeType.getSimpleName();
	}
}
