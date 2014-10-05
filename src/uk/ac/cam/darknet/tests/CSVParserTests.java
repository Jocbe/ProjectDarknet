package uk.ac.cam.darknet.tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import org.junit.Test;

import uk.ac.cam.darknet.backend.SpektrixCSVParser;
import uk.ac.cam.darknet.common.Individual;

public class CSVParserTests {

	@Test
	public void testmember0() throws IOException, SQLException, ParseException {
		SpektrixCSVParser parser = new SpektrixCSVParser();
		List<Individual> audience = parser.loadfromCSV("src/TestSpektrixData.csv", 0);
		DateFormat df = new SimpleDateFormat("dd/mm/yyyy HH:mm:ss");
		Individual i = audience.get(0);
		assertEquals(i.getFirstName(), "");
		assertEquals(i.getLastName(), "");
		assertEquals(i.getEmail(), "");
		assertEquals(i.getEventDate(), df.parse("3/7/2011  19:30:00"));
		assertEquals(i.getSeat(), "C26");
	}
	
	@Test
	public void testmember522() throws IOException, SQLException, ParseException {
		SpektrixCSVParser parser = new SpektrixCSVParser();
		List<Individual> audience = parser.loadfromCSV("src/TestSpektrixData.csv", 0);
		DateFormat df = new SimpleDateFormat("dd/mm/yyyy HH:mm:ss");
		Individual i = audience.get(522);
		assertEquals(i.getFirstName(), "Kimberly");
		assertEquals(i.getLastName(), "Dietrich");
		assertEquals(i.getEmail(), "Kimberly.Dietrich.0@test.com");
		assertEquals(i.getEventDate(), df.parse("3/6/2013  20:00:00"));
		assertEquals(i.getSeat(), "A4");
	}

}
