package uk.ac.cam.darknet.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Factory for a logger to ensure there is only one logger in the system.
 * 
 * @author Johann Beleites
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
			final ConsoleHandler ch;

			try {

				fh = new FileHandler(Strings.LOG_PATH, true);
				ch = new ConsoleHandler();

				class LogFormatter extends Formatter {

					@Override
					public String format(LogRecord r) {

						final StringBuilder sb = new StringBuilder();

						final String d = (new SimpleDateFormat(
								Strings.LOGGER_DATE_FORMAT)).format(new Date(r
								.getMillis()));

						sb.append(d).append(" [")
								.append(r.getLevel().getLocalizedName())
								.append("] - ").append(r.getSourceMethodName())
								.append(" (").append(r.getSourceClassName())
								.append("): ").append(formatMessage(r))
								.append("\n");

						if (r.getThrown() != null) {
							try {
								StringWriter sw = new StringWriter();
								try (PrintWriter pw = new PrintWriter(sw)) {
									r.getThrown().printStackTrace(pw);
								}
								sb.append(sw.toString());
							}
							catch (Exception e) {
								System.err.println(Strings.LOG_FORMAT_EXCEPTION
										+ e.getClass().toString()
										+ ". Message: " + e.getMessage());
							}
						}

						return sb.toString();

					}
				}

				final LogFormatter lf = new LogFormatter();

				fh.setFormatter(lf);
				ch.setFormatter(lf);

				logger.setUseParentHandlers(false);

				logger.addHandler(fh);
				logger.addHandler(ch);

			}
			catch (IOException e) {
				System.err.println(Strings.LOG_EXCEPTION + e.getMessage());
			}
		}
		return logger;
	}
}
