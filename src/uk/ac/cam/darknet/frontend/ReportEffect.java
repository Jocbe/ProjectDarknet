package uk.ac.cam.darknet.frontend;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import uk.ac.cam.darknet.common.AttributeReliabilityPair;
import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.common.IndividualRequirements;
import uk.ac.cam.darknet.common.Show;
import uk.ac.cam.darknet.common.Venue;
import uk.ac.cam.darknet.database.DatabaseManager;
import uk.ac.cam.darknet.exceptions.InvalidAttributeTypeException;
import uk.ac.cam.darknet.exceptions.InvalidReliabilityException;
import uk.ac.cam.darknet.exceptions.RequestNotSatisfiableException;
import uk.ac.cam.darknet.exceptions.UnknownAttributeException;
import uk.ac.cam.darknet.storage.ImageStorage;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * An effect for generating a report for each individual.
 * 
 * @author Farah Patel
 * 
 */
public class ReportEffect extends Effect {
	private String pathname;
	private String filename;

	/**
	 * Constructs a new ReportEffect.
	 * @param dm An appropriate database manager.
	 */
	public ReportEffect(DatabaseManager dm) {
		super(dm);
	}

	@Override
	public List<String> getSetupArgDescriptions() {
		final List<String> argDescr = new ArrayList<>();
		argDescr.add("path of the directory the report should be stored in");
		argDescr.add("filename (without extension) the report should be stored as");
		return argDescr;
	}

	@Override
	public void execute(Show show) {
		ArrayList<Individual> individuals;
		IndividualRequirements requirements = new IndividualRequirements(show);
		Enumeration<String> attributes;
		ImageStorage imgStore = new ImageStorage();
		String currentAttribute;
		Document doc = new Document();
		String data;
		try {
			individuals = (ArrayList<Individual>) dm
					.getSuitableIndividuals(requirements);
			PdfWriter.getInstance(doc, new FileOutputStream(pathname + filename
					+ ".pdf"));
			doc.open();
			DateFormat df = new SimpleDateFormat("dd/mm/yyyy 'at' HH:mm:ss");
			Date showDate = show.getDate();
			Venue showVenue = show.getVenue();
			doc.add(new Paragraph("This is the report for the audience at "
					+ showVenue.getName() + " that takes place on "
					+ df.format(showDate), FontFactory.getFont(
					FontFactory.COURIER, 25)));
			doc.newPage();
			for (Individual i : individuals) {
				// display primary information
				PdfPTable table = new PdfPTable(3);
				table.setWidths(new int[] { 1, 1, 1 });
				PdfPCell cell;
				cell = new PdfPCell(new Phrase(i.getFirstName() + " "
						+ i.getLastName(), FontFactory.getFont(
						FontFactory.COURIER, 18)));
				cell.setColspan(3);
				cell.setRowspan(2);
				table.addCell(cell);
				table.addCell("Email");
				data = i.getEmail();
				if (data == null || data == "") {
					data = "No Current Data";
				}
				cell = new PdfPCell(new Phrase(data));
				cell.setColspan(2);
				table.addCell(cell);
				table.addCell("Seat");
				data = i.getSeat();
				if (data == null || data == "") {
					data = "No Current Data";
				}
				cell = new PdfPCell(new Phrase(data));
				cell.setColspan(2);
				table.addCell(cell);
				attributes = i.getProperties().keys();
				cell = new PdfPCell(new Phrase("Secondary Data"));
				cell.setColspan(3);
				table.addCell(cell);
				// add Secondary information
				table.addCell("Attribute");
				table.addCell("Value");
				table.addCell("Reliability");
				while (attributes.hasMoreElements()) {
					currentAttribute = attributes.nextElement();
					table.addCell(currentAttribute);
					List<AttributeReliabilityPair> arp = i
							.getAttribute(currentAttribute);
					AttributeReliabilityPair a = arp.get(0);
					if (arp.size() > 1 && currentAttribute.contains("photo")) {
						data = "We have " + arp.size()
								+ " photos stored currently";
						if (arp.isEmpty()) {
							data = "No Current Data";
						}
						table.addCell(data);
						table.addCell("N/A");
						try {
							table.addCell("Most Recent Photo");
							Image img;
							img = Image.getInstance(imgStore
									.retreiveImage((String) a.getAttribute()),
									null);
							img.scaleToFit(250, 250);
							table.addCell(img);
							table.addCell(Double.toString(a.getReliability()));
						}
						catch (IOException e) {
							System.err.println("Failed to retrieve Image");
							e.printStackTrace();
						}
					}
					else {
						data = a.getAttribute().toString();
						if (data == null || data == "") {
							data = "No Current Data";
						}
						table.addCell(data);
						data = Double.toString(a.getReliability());
						if (data == null || data == "") {
							data = "N/A";
						}
						table.addCell(data);
					}
				}

				doc.add(table);
				doc.newPage();
			}
		}
		catch (FileNotFoundException e) {
			System.err
					.println("Error in pathname or filename. Check setup method args");
			return;
		}
		catch (SQLException e) {
			System.err.println("SQL Error");
			return;
		}
		catch (RequestNotSatisfiableException e) {
			System.err.println("DatabaseManager could not satisfy Request");
			e.printStackTrace();
			return;
		}
		catch (UnknownAttributeException e) {
			System.err.println("Unknown Attribute");
			return;
		}
		catch (InvalidAttributeTypeException e) {
			System.err.println("Invalid Attribute Type");
			return;
		}
		catch (InvalidReliabilityException e) {
			System.err.println("Invalid Reliability");
			return;
		}
		catch (DocumentException e) {
			System.err.println("Error creating document to make PDF");
			return;
		}
		finally {
			doc.close();
		}
	}

	@Override
	public void setup(String[] args) {
		pathname = args[0];
		filename = args[1];
	}
}
