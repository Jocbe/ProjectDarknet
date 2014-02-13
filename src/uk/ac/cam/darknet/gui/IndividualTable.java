package uk.ac.cam.darknet.gui;

import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import uk.ac.cam.darknet.common.Individual;

/**
 * A Swing table extending JTable to store individuals. Provides methods such as
 * addIndividual(), which add the given individual to the table and display it.
 * 
 * @author Augustin Zidek
 * 
 */
public class IndividualTable extends JTable {
	private static final long serialVersionUID = 1L;
	private static final String[] columns = { "ID", "First name", "Last name",
			"Email", "Event date", "Seat" };
	final static DefaultTableModel model = new DefaultTableModel();

	/**
	 * Creates a new empty table with the column names set to the primary fields
	 * of the Individual, i.e. id, firstName, lastName, email, eventDate, seat.
	 * 
	 */
	public IndividualTable() {
		super(model);
		// Set the columns
		model.setColumnIdentifiers(columns);
		// Create auto sorted for each column
		this.setAutoCreateRowSorter(true);
		// Set the column widths
		this.getColumnModel().getColumn(0).setPreferredWidth(50);
		this.getColumnModel().getColumn(4).setPreferredWidth(60);
		this.getColumnModel().getColumn(5).setPreferredWidth(25);
	}

	/**
	 * Displays the primary data of the given individuals in the table, one
	 * individual per row.
	 * 
	 * @param individuals The list of individuals.
	 */
	public void displayIndividuals(final List<Individual> individuals) {
		// Go through all individuals, get their fields and display
		for (final Individual i : individuals) {
			displayIndividual(i);
		}
	}

	/**
	 * Displays the primary data of the given individual in the table. If there
	 * are any data in the table this row is appended.
	 * 
	 * @param i The individual to be included in the table.
	 */
	public void displayIndividual(final Individual i) {
		final long ID = i.getId();
		final String firstName = i.getFirstName();
		final String lastName = i.getLastName();
		final String email = i.getEmail();
		final String eventDate = i.getEventDate().toString();
		final String seat = i.getSeat();

		// Add row to the table
		model.addRow(new Object[] { ID, firstName, lastName, email, eventDate,
				seat });
	}

}
