package uk.ac.cam.darknet.common;

import java.util.UUID;

/**
 * The set of attribute classes supported by the system. Each data collector should provide a list
 * 
 * @author Ibtehaj Nadeem
 *
 */
public enum GlobalAttributeType {
	FISRT_NAME(String.class),
	LAST_NAME(String.class),
	EMAIL(String.class),
	PHOTO(UUID.class);

	private Class<?> attributeType;

	GlobalAttributeType(Class<?> attributeType) {
		this.attributeType = attributeType;
	}

	public Class<?> getAttributeType() {
		return attributeType;
	}
}
