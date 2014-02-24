package uk.ac.cam.darknet.backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import uk.ac.cam.darknet.common.AttributeCategories;
import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.exceptions.ConfigFileNotFoundException;
import au.com.bytecode.opencsv.CSVReader;

/**
 * This class is a primary data collector with a graphical user interface
 * allowing manual data input. It represents the primary way of entering data
 * into the system. This collector supports reading comma-separated values (CSV)
 * files generated by booking systems.
 * 
 * @author Farah Patel
 */
public class SpektrixCSVParser {

	/**
	 * Parses CSV file from Spektrix and produces a list of Individuals
	 * 
	 * @param pathname
	 *            path to Spektrix CSV file, CSV file expected to have the
	 *            following format: Customer Id, First Name, Last Name, Email
	 *            Address, Event Name, Event Date/Time, Seat, Price, Ticket
	 *            Type, Date Confirmed, Sales Channel. Note ignores first line
	 *            of CSV file on the assumption that this lays out columns and
	 *            it not actual data
	 * @return List of Individuals stored in Database
	 * @throws IOException
	 *             If CSV file not found or if read is unsuccessful
	 * @throws SQLException
	 * @throws ParseException
	 */
	public List<Individual> loadfromCSV(String pathname, int venue) throws IOException,
			SQLException, ParseException {
		List<Individual> audience = new ArrayList<Individual>();
		InputStream csvStream = new FileInputStream(new File(pathname));
		try (CSVReader reader = new CSVReader(new InputStreamReader(csvStream));) {
			String[] nextLine;
			DateFormat df = new SimpleDateFormat("dd/mm/yyyy HH:mm:ss");
			Hashtable<String, AttributeCategories> table = new Hashtable<String, AttributeCategories>();
			reader.readNext(); // get rid of column titles (first line ignored.
								// assumed to be field names)
			while ((nextLine = reader.readNext()) != null) {
				if (nextLine != null) {
					Date eventDate = df.parse(nextLine[5]);
					Individual ind = Individual.getNewIndividual(nextLine[1],
							nextLine[2], nextLine[3], eventDate, venue, nextLine[6],
							table);
					audience.add(ind);
				}
			}
			return audience;
		}
	}

	/**
	 * this method is for testing purposes only
	 * 
	 * @param args
	 * @throws IOException
	 * @throws SQLException
	 * @throws ParseException
	 * @throws ConfigFileNotFoundException
	 * @throws ClassNotFoundException
	 */
	public static void main(String args[]) throws IOException, SQLException,
			ParseException, ClassNotFoundException, ConfigFileNotFoundException {
		SpektrixCSVParser parser = new SpektrixCSVParser();
		List<Individual> audience = parser.loadfromCSV(args[0]);
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.UK);
		Individual i = audience.get(0);
		System.out.println(i.getFirstName());
		System.out.println(i.getFirstName() + "  " + i.getLastName() + "  "
				+ i.getEmail() + "  " + df.format(i.getEventDate()) + "  "
				+ i.getSeat());
		i = audience.get(1);
		System.out.println(i.getFirstName() + "  " + i.getLastName() + "  "
				+ i.getEmail() + "  " + df.format(i.getEventDate()) + "  "
				+ i.getSeat());
		i = audience.get(520);
		System.out.println(i.getFirstName() + "  " + i.getLastName() + "  "
				+ i.getEmail() + "  " + df.format(i.getEventDate()) + "  "
				+ i.getSeat());
		i = audience.get(525);
		System.out.println(i.getFirstName() + "  " + i.getLastName() + "  "
				+ i.getEmail() + "  " + df.format(i.getEventDate()) + "  "
				+ i.getSeat());
	}
}
