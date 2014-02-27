package uk.ac.cam.darknet.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.ac.cam.darknet.backend.SecondaryDataCollector;
import uk.ac.cam.darknet.frontend.Effect;

/**
 * Loads, using Reflection, effect or collector classes.
 * 
 * @author Augustin Zidek
 * 
 */
public class EffectsAndCollectorsLoader {

	/**
	 * Loads all secondary collector classes that are present in the backend
	 * package (i.e. Facebook or Twitter collectors).
	 * 
	 * @return A list of classes that extend SecondaryDataCollector (not
	 *         including SDC itself).
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public List<Class<?>> loadSecondaryCollectors()
			throws ClassNotFoundException, IOException {
		// Read all classes that are in the backend package
		final Class<?>[] backendClasses = AdvancedReflection
				.getClasses("uk.ac.cam.darknet.backend");

		// Get only the data collectors
		final List<Class<?>> dataCollectorClasses = new ArrayList<>();
		for (final Class<?> c : backendClasses) {
			// If class extends DataCollector and is not a SDC itself
			if (SecondaryDataCollector.class.isAssignableFrom(c)
					&& !c.equals(SecondaryDataCollector.class)) {
				dataCollectorClasses.add(c);
			}
		}
		return dataCollectorClasses;
	}

	/**
	 * Loads all effect classes that are present in the frontend package (i.e.
	 * PictureWall or Report effects).
	 * 
	 * @return A list of classes that extend SecondaryDataCollector (not
	 *         including SDC itself).
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public List<Class<?>> loadEffects() throws ClassNotFoundException,
			IOException {
		// Read all classes that are in the frontend package
		final Class<?>[] frontendClasses = AdvancedReflection
				.getClasses("uk.ac.cam.darknet.frontend");

		// Get only the effects
		final List<Class<?>> effectClasses = new ArrayList<>();
		for (final Class<?> c : frontendClasses) {
			// If class ectends the Effect and is not Effect itself
			if (Effect.class.isAssignableFrom(c) && !c.equals(Effect.class)) {
				effectClasses.add(c);
			}
		}
		return effectClasses;
	}

}
