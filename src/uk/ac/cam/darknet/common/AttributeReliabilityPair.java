package uk.ac.cam.darknet.common;

import uk.ac.cam.darknet.exceptions.InvalidReliabilityException;

/**
 * This simple class is used to store an attribute with its reliability. This class is constructed
 * only by the <code>Properties</code> class. When retrieving attributes, client code should cast it
 * to an appropriate type (using the global attribute table), if needed.
 * 
 * @author Ibtehaj Nadeem
 */
public class AttributeReliabilityPair {
	private final Object	attribute;
	private final double	reliability;

	AttributeReliabilityPair(Object attribute, double reliability) throws InvalidReliabilityException {
		if (reliability < 0.0 || reliability > 1.0)
			throw new InvalidReliabilityException(Strings.INVALID_RELIABILITY_EXN);
		this.attribute = attribute;
		this.reliability = reliability;
	}

	/**
	 * Returns the reliability of the attribute stored.
	 * 
	 * @return The reliability of the attribute stored.
	 */
	public double getReliability() {
		return this.reliability;
	}

	/**
	 * Returns the attribute stored.
	 * 
	 * @return The attribute stored.
	 */
	public Object getAttribute() {
		return this.attribute;
	}
}