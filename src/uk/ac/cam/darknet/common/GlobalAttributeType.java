package uk.ac.cam.darknet.common;

import java.util.UUID;

/**
 * The set of attribute types supported by the system. Each data collector
 * provides a list of attributes it supports, indicating for each one which
 * global attribute type it falls into. This enumeration includes some generic
 * types for uncommon attributes.
 * 
 * @author Ibtehaj Nadeem
 */
public enum GlobalAttributeType {
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

	GlobalAttributeType(Class<?> attributeType) {
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
}
