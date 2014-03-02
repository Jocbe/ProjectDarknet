package uk.ac.cam.darknet.common;

/**
 * The set of attribute categories supported by the system. Each data collector provides a list of
 * attributes it supports, indicating for each one which category it falls into.
 * 
 * @author Ibtehaj Nadeem
 */
public enum AttributeCategories {
	/**
	 * User/nick names that individuals use on various websites (<code>String</code>)
	 */
	ALIAS(String.class),
	/**
	 * Paths to photos of individuals stored in the photo store on disk. (<code>String</code>)
	 */
	PHOTO(String.class),
	/**
	 * The age of individuals (<code>Byte</code>)
	 */
	AGE(Byte.class),
	/**
	 * Gender of user (<code>String</code>)
	 */
	GENDER(String.class),
	/**
	 * A phone number of the individual (<code>String</code>)
	 */
	PHONE_NUMBER(String.class),
	/**
	 * Some status (e.g. Facebook status, tweet) (<code>String</code>)
	 */
	STATUS(String.class),
	/**
	 * Some locale (language setting) (e.g. Facebook locale) (<code>String</code>)
	 */
	LOCALE(String.class),
	/**
	 * Birthday of individual (<code>String</code>)
	 */
	BIRTHDAY(String.class),
	/**
	 * Individuals's relationship status (<code>String</code>)
	 */
	RELATIONSHIP_STATUS(String.class);

	private final Class<?>	attributeType;

	AttributeCategories(Class<?> attributeType) {
		this.attributeType = attributeType;
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