package uk.ac.cam.darknet.tests;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class DateParserTests {
	/**
	 * @param args
	 * @throws ParseException
	 */
	public static void main(String[] args) throws ParseException {
		final Date eventDate = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
				.parse("03/07/2011 19:30:00");

		System.out.println(eventDate.toString());
	}

}
