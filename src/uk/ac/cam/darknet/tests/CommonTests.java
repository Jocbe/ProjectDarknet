package uk.ac.cam.darknet.tests;

import java.util.logging.Logger;

import org.junit.Test;

import uk.ac.cam.darknet.common.LoggerFactory;

/**
 * @author Johann Beleites
 *
 */
public class CommonTests {
	@SuppressWarnings("javadoc")
	@Test
	public void LoggerTest() {
		final Logger l = LoggerFactory.getLogger();
		final Logger l2 = LoggerFactory.getLogger();
		l.info("Info message");
		l.warning("Warning message");
		l.severe("Severe message");

		l2.info("Testing infor for l2");
		l.fine("Test");

	}

	@SuppressWarnings("javadoc")
	@Test
	public void LoggerTest2() {
		final Logger l = LoggerFactory.getLogger();
		l.warning("WARNING");
	}
}
