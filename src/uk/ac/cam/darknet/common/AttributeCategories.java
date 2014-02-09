package uk.ac.cam.darknet.common;

import java.util.UUID;

/**
 * The set of attribute categories supported by the system. Each data collector
 * provides a list of attributes it supports, indicating for each one which
 * category it falls into.
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
	 * Photos of individuals stored in the photo store on disk. (
	 * <code>java.util.UUID</code>)
	 */
	PHOTO(UUID.class);

	private Class<?> attributeType;

	AttributeCategories(Class<?> attributeType) {
		this.attributeType = attributeType;
	}

	/**
	 * This method returns the class object of the type that a particular
	 * attribute type should be interpreted as.
	 * 
	 * @return The class object for the appropriate datatype of the attribute
	 *         type.
	 */
	public Class<?> getAttributeType() {
		return attributeType;
	}

	/**
	 * Checks whether a given object can be cast to an object suitable for
	 * representing this attribute type.
	 * 
	 * @param toCheck The object whose compatibility must be verified.
	 * @return True if the object is valid, false otherwise.
	 */
	public boolean isObjectCompatible(Object toCheck) {
		return attributeType.isInstance(toCheck);
	}

	/**
	 * Get the name of the class used to store attributes of this category.
	 * 
	 * @return Name of the class that should be used to represent this attribute
	 *         category.
	 */
	public String getClassName() {
		return attributeType.getSimpleName();
	}
}
