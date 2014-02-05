package uk.ac.cam.darknet.common;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Factory for a logger to ensure there is only one logger in the system.
 */
public class LoggerFactory {
	private static Logger logger;

	/**
	 * @return New Logger into which messages can be logged.
	 */
	public static Logger getLogger() {
		if (logger == null) {
			logger = Logger.getLogger(Strings.LOGGER_NAME);
			logger.setLevel(Level.ALL);

			final FileHandler fh;

			try {
				fh = new FileHandler(Strings.LOG_PATH, true);
				fh.setFormatter(new SimpleFormatter());

				logger.addHandler(fh);
			}
			catch (IOException e) {
				System.err.println(Strings.LOG_EXCEPTION + e.getMessage());
			}

		}
		return logger;
	}
}
