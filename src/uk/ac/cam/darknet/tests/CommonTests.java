package uk.ac.cam.darknet.tests;

import java.util.logging.Logger;

import org.junit.Test;

import uk.ac.cam.darknet.common.LoggerFactory;

@SuppressWarnings("javadoc")
public class CommonTests {
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

	@Test
	public void LoggerTest2() {
		final Logger l = LoggerFactory.getLogger();
		l.warning("WARNING");
	}
}
