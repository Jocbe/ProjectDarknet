package uk.ac.cam.darknet.common;

import java.util.Enumeration;
import java.util.Hashtable;

import uk.ac.cam.darknet.exceptions.InvalidAttributeTypeException;
import uk.ac.cam.darknet.exceptions.UnknownAttributeException;

/**
 * This class stores an individual's attributes as a set of key-value pairs.
 * Keys are always strings, but values must be of the type specified by the
 * attribute category that the attribute falls into.
 * 
 * @author Ibtehaj Nadeem
 * 
 */
public class Properties {
	private final Hashtable<String, AttributeCategories> globalAttributeTable;
	private Hashtable<String, Object> properties;

	/**
	 * Constructs a new <code>Properties</code> object with the given attribute
	 * table. This object will only allow those keys to be added which are also
	 * present in the provided global table. It will also enforce the
	 * corresponding value to be of the correct type, as determined by the
	 * category of each attribute.
	 * 
	 * @param globalAttributeTable The global table mapping all available
	 *            attribute names to their global attribute types.
	 */
	public Properties(
			final Hashtable<String, AttributeCategories> globalAttributeTable) {
		this.globalAttributeTable = globalAttributeTable;
		this.properties = new Hashtable<String, Object>();
	}

	/**
	 * This method adds an attribute to this <code>Properties</code> object. The
	 * key must be a valid attribute present in this object's global attribute
	 * table. If this is not the case, an <code>UnknownAttributeException</code>
	 * will be thrown. If the type of the object is not as specified by the
	 * attribute category, an <code>InvalidAttributeType</code> exception will
	 * be thrown.
	 * 
	 * @param key The name of the attribute to add.
	 * @param value The corresponding object.
	 * @throws UnknownAttributeException
	 * @throws InvalidAttributeTypeException
	 */
	public synchronized void put(String key, Object value)
			throws UnknownAttributeException, InvalidAttributeTypeException {
		AttributeCategories attributeCategory = globalAttributeTable.get(key);
		// Check if the key is a valid attribute.
		if (attributeCategory == null)
			throw new UnknownAttributeException(String.format(
					Strings.PROP_UNKNOWN_ATTR_EXN, key));
		// Check if the value is of the correct type.
		if (!attributeCategory.isObjectCompatible(value))
			throw new InvalidAttributeTypeException(String.format(
					Strings.PROP_INVALID_TYPE_EXN, key,
					attributeCategory.getClassName()));
		// If both checks are passed, add the new key-value pair.
		properties.put(key, attributeCategory.getAttributeType().cast(value));
	}

	/**
	 * The value to which the specified attribute name is mapped, or null if
	 * this map contains no mapping for the attribute.
	 * 
	 * @param key The attribute name whose associated value is to be returned.
	 * @return The value to which the specified attribute name is mapped, or
	 *         null if this map contains no mapping for the attribute.
	 */
	public synchronized Object get(String key) {
		return properties.get(key);
	}

	/**
	 * Removes the key-value pair specified by the given key from this
	 * <code>Properties</code> object. This method does nothing if the key is
	 * not in the object.
	 * 
	 * @param key The key of the key-value pair to remove.
	 */
	public synchronized void remove(String key) {
		properties.remove(key);
	}

	/**
	 * Clears this <code>Properties</code> object so that it contains no more
	 * key-value pairs.
	 */
	public synchronized void clear() {
		properties.clear();
	}

	/**
	 * Tests whether a given attribute is contained in this
	 * <code>Properties</code> object.
	 * 
	 * @param key The name of the attribute whose presence should be checked.
	 * @return True if the key is contained, false otherwise.
	 */
	public synchronized boolean containsAttribute(String key) {
		return properties.contains(key);
	}

	/**
	 * Returns an enumeration of the values in this <code>Properties</code>
	 * object.
	 * 
	 * @return an enumeration of the values in this <code>Properties</code>
	 *         object.
	 */
	public synchronized Enumeration<Object> elements() {
		return properties.elements();
	}

	/**
	 * Returns an enumeration of the keys in this <code>Properties</code>
	 * object.
	 * 
	 * @return an enumeration of the keys in this <code>Properties</code>
	 *         object.
	 */
	public synchronized Enumeration<String> keys() {
		return properties.keys();
	}
}