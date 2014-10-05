package uk.ac.cam.darknet.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * A Swing table extending JTable to store collectors.
 * 
 * @author Augustin Zidek
 * 
 */
public class EffectsTable extends JTable {
	private static final long serialVersionUID = 1L;
	// Names of the columns in the table
	private static final String[] columns = { "Use?", "Effect name" };
	final static DefaultTableModel model = new DefaultTableModel();

	/**
	 * Make all cells un-editable.
	 */
	@Override
	public boolean isCellEditable(int row, int column) {
		return (column == 0);
	}

	/**
	 * Creates a new empty table with the column names set to the primary fields
	 * of the Individual, i.e. id, firstName, lastName, email, eventDate, seat.
	 * 
	 */
	public EffectsTable() {
		super(model);
		// Set the columns
		model.setColumnIdentifiers(columns);
		// Create auto sorted for each column
		this.setAutoCreateRowSorter(true);
		// Set the column width
		this.getColumnModel().getColumn(0).setMaxWidth(40);
	}

	/**
	 * Adds the given collector to the table.
	 * 
	 * @param effectName The name of the collector.
	 */
	public void addEffect(final String effectName) {
		// Add row to the table
		model.addRow(new Object[] { new Boolean(false), effectName });
	}

	// Make checkboxes appear
	@Override
	public Class<?> getColumnClass(final int column) {
		return (this.getValueAt(0, column).getClass());
	}

	/**
	 * Clears all the rows in the table.
	 */
	public void clearTable() {
		model.setRowCount(0);
	}

	/**
	 * @return Return indexes of rows with selected checkboxes.
	 */
	public List<Integer> getCheckedRows() {
		final List<Integer> checked = new ArrayList<>();
		// Go through all rows and check first column
		for (int row = 0; row < model.getRowCount(); row++) {
			if ((Boolean) model.getValueAt(row, 0)) {
				checked.add(row);
			}
		}
		return checked;
	}

}
