package uk.ac.cam.darknet.gui;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.common.Strings;

/**
 * A Swing table extending JTable to store individuals. Provides methods such as
 * addIndividual(), which add the given individual to the table and display it.
 * 
 * @author Augustin Zidek
 * 
 */
public class IndividualTable extends JTable {
	private static final long serialVersionUID = 1L;
	// Names of the columns in the table
	private static final String[] columns = { "ID", "First name", "Last name",
			"Email", "Venue ID", "Show date", "Seat" };
	final static DefaultTableModel model = new DefaultTableModel();

	/**
	 * Make all cells un-editable.
	 */
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

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
		// Set the column widths: ID
		this.getColumnModel().getColumn(0).setMaxWidth(60);
		// Set the column widths: Seat
		this.getColumnModel().getColumn(6).setMaxWidth(40);
		// Set the selection mode
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
		final String eventDate;
		final String venueID = String.valueOf(i.getEventVenue());
		// Handle cases with no date specified
		if (i.getEventDate() != null) {
			final SimpleDateFormat sdf = new SimpleDateFormat(
					Strings.GUI_DATE_FORMAT);
			eventDate = sdf.format(i.getEventDate());
		}
		else {
			eventDate = "";
		}
		final String seat = i.getSeat();

		// Add row to the table
		model.addRow(new Object[] { ID, firstName, lastName, email, venueID,
				eventDate, seat });
	}

	/**
	 * Clears all the rows in the table.
	 */
	public void clearTable() {
		model.setRowCount(0);
	}

	/**
	 * Returns ID of individual that is on the selected row.
	 * 
	 * @return The ID of the individual on the currently selected row. If no row
	 *         selected, returns -1.
	 */
	public long getSelectedIndividualID() {
		final int selectedRow = this.getSelectedRow();
		// No row selected
		if (selectedRow == -1) {
			return -1;
		}
		return (Long) this.getValueAt(this.getSelectedRow(), 0);
	}
}
