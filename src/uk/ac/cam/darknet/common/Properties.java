package uk.ac.cam.darknet.common;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import uk.ac.cam.darknet.exceptions.InvalidAttributeTypeException;
import uk.ac.cam.darknet.exceptions.InvalidReliabilityException;
import uk.ac.cam.darknet.exceptions.UnknownAttributeException;

/**
 * This class stores an individual's attributes as a set of key-value pairs. Keys are always
 * strings, but values must be of the type specified by the attribute category that the attribute
 * falls into. This class also takes the attribute's reliability as a parameter when one is added.
 * It always returns an <code>AttributeReliabilityPair</code>, which can be used to retrieve both
 * the attribute and its reliability. Note that attributes are type checked using the global
 * attribute table when being added. However, client code should cast retrieved attributes as they
 * have to be returned as <code>Object</code>.
 * 
 * @author Ibtehaj Nadeem
 */
public class Properties {
	private final Hashtable<String, AttributeCategories>		globalAttributeTable;
	private Hashtable<String, List<AttributeReliabilityPair>>	hashtable;

	/**
	 * Constructs a new <code>Properties</code> object with the given attribute table. This object
	 * will only allow those keys to be added which are also present in the provided global table.
	 * It will also enforce the corresponding value to be of the correct type, as determined by the
	 * category of each attribute.
	 * 
	 * @param globalAttributeTable
	 *            The global table mapping all available attribute names to their global attribute
	 *            types.
	 */
	public Properties(final Hashtable<String, AttributeCategories> globalAttributeTable) {
		this.globalAttributeTable = globalAttributeTable;
		this.hashtable = new Hashtable<String, List<AttributeReliabilityPair>>();
	}

	/**
	 * This method adds an attribute to this <code>Properties</code> object. The key must be a valid
	 * attribute present in this object's global attribute table. If this is not the case, an
	 * <code>UnknownAttributeException</code> will be thrown. If the type of the object is not as
	 * specified by the attribute category, an <code>InvalidAttributeType</code> exception will be
	 * thrown.
	 * 
	 * @param key
	 *            The name of the attribute to add.
	 * @param value
	 *            The corresponding object.
	 * @param reliability
	 *            The reliability of the attribute. This must be a number between 0.0 (no
	 *            confidence) and 1.0 (complete certainty).
	 * @throws UnknownAttributeException
	 * @throws InvalidAttributeTypeException
	 * @throws InvalidReliabilityException
	 */
	public synchronized void put(String key, Object value, double reliability) throws UnknownAttributeException, InvalidAttributeTypeException, InvalidReliabilityException {
		AttributeCategories attributeCategory = globalAttributeTable.get(key);
		// Check if the key is a valid attribute.
		if (attributeCategory == null)
			throw new UnknownAttributeException(String.format(Strings.UNKNOWN_ATTR_EXN, key));
		// Check if the value is of the correct type.
		if (!attributeCategory.isObjectCompatible(value))
			throw new InvalidAttributeTypeException(String.format(Strings.INVALID_TYPE_EXN, key, attributeCategory.getClassName()));
		// If both checks are passed, add the new key-value pair.
		AttributeReliabilityPair newAttrRel = new AttributeReliabilityPair(value, reliability);
		if (hashtable.containsKey(key)) {
			hashtable.get(key).add(newAttrRel);
		} else {
			ArrayList<AttributeReliabilityPair> newList = new ArrayList<AttributeReliabilityPair>();
			newList.add(newAttrRel);
			hashtable.put(key, newList);
		}
	}

	/**
	 * Returns a list of all the attributes stored in this object under the given name.
	 * 
	 * @param key
	 *            The attribute name whose associated list of values is to be returned.
	 * @return The list of values to which the specified attribute name is mapped, or null if this
	 *         map contains no mapping for the attribute.
	 */
	public synchronized List<AttributeReliabilityPair> get(String key) {
		return hashtable.get(key);
	}

	/**
	 * Removes all the attributes stored under the given key. If there are no such attributes,
	 * nothing is changed.
	 * 
	 * @param key
	 *            The key of the key-value pair to remove.
	 */
	public synchronized void remove(String key) {
		hashtable.remove(key);
	}

	/**
	 * Clears this <code>Properties</code> object so that it contains no more attributes.
	 */
	public synchronized void clear() {
		hashtable.clear();
	}

	/**
	 * Tests whether a given attribute (i.e. at least one) is contained in this
	 * <code>Properties</code> object.
	 * 
	 * @param key
	 *            The name of the attribute whose presence should be checked.
	 * @return True if the key is contained, false otherwise.
	 */
	public synchronized boolean containsAttribute(String key) {
		return hashtable.containsKey(key);
	}

	/**
	 * Returns an enumeration of the values in this <code>Properties</code> object.
	 * 
	 * @return an enumeration of the values in this <code>Properties</code> object.
	 */
	public synchronized Enumeration<List<AttributeReliabilityPair>> elements() {
		return hashtable.elements();
	}

	/**
	 * Returns an enumeration of the keys in this <code>Properties</code> object.
	 * 
	 * @return an enumeration of the keys in this <code>Properties</code> object.
	 */
	public synchronized Enumeration<String> keys() {
		return hashtable.keys();
	}
}