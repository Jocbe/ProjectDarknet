package uk.ac.cam.darknet.common;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

import uk.ac.cam.darknet.backend.SecondaryDataCollector;
import uk.ac.cam.darknet.database.SecondaryDatabaseManager;
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
		
		final Reflections reflections = new Reflections("uk.ac.cam.darknet.backend");

		final Set<Class<? extends SecondaryDataCollector>> backendClasses = reflections
				.getSubTypesOf(SecondaryDataCollector.class);

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
		final Reflections reflections = new Reflections("uk.ac.cam.darknet.frontend");

		final Set<Class<? extends Effect>> frontendClasses = reflections
				.getSubTypesOf(Effect.class);

		// Get only the effects
		final List<Class<?>> effectClasses = new ArrayList<>();
		for (final Class<?> c : frontendClasses) {
			// If class extends the Effect and is not Effect itself
			if (Effect.class.isAssignableFrom(c) && !c.equals(Effect.class)) {
				effectClasses.add(c);
			}
		}
		return effectClasses;
	}

	/**
	 * Finds all collectors present in the backend package and returns their
	 * database attributes.
	 * 
	 * @return The database attributes each collector requires.
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public Hashtable<String, AttributeCategories> getDatabaseCollectorAttributes()
			throws ClassNotFoundException, IOException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		final Hashtable<String, AttributeCategories> attributes = new Hashtable<>();

		// Get all the collectors in the system via reflection
		final List<Class<?>> collectors = loadSecondaryCollectors();

		// Go through all collector
		for (final Class<?> collClass : collectors) {
			// Initialize collector with null hashtable
			final SecondaryDataCollector collector = (SecondaryDataCollector) collClass
					.getConstructor(SecondaryDatabaseManager.class)
					.newInstance((Object) null);
			attributes.putAll(collector.getAttributeTable());
		}

		return attributes;
	}
}
