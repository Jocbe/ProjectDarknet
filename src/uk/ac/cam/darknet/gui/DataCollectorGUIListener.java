package uk.ac.cam.darknet.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import uk.ac.cam.darknet.backend.SecondaryDataCollector;
import uk.ac.cam.darknet.backend.SpektrixCSVParser;
import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.common.Show;
import uk.ac.cam.darknet.common.Strings;
import uk.ac.cam.darknet.common.Venue;
import uk.ac.cam.darknet.database.SecondaryDatabaseManager;

/**
 * Listener for the Data Collector GUI.
 * 
 * @author Augustin Zidek
 * 
 */
public class DataCollectorGUIListener implements ActionListener {
	private final DataCollectorGUI gui;

	/**
	 * @param gui The gui that this listener should be listening to.
	 */
	public DataCollectorGUIListener(final DataCollectorGUI gui) {
		this.gui = gui;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Get the source
		final Object source = e.getSource();
		// The browse button, open File Chooser dialog
		if (source == gui.btnBrowse) {
			handleBrowseButton();
		}
		// The load audience button, load the given CSV file
		else if (source == gui.btnLoadAudience) {
			handleLoadAudience();
		}
		// The add single person button, add them to the DB
		else if (source == gui.btnAddPerson) {
			handleAddPerson();
		}
		// Done button, close the window
		else if (source == gui.btnDone) {
			gui.frame.dispose();
		}
		// Refresh button, refresh the DB view
		else if (source == gui.btnRefresh) {
			handleRefresh();
		}
		// Delete individual button
		else if (source == gui.btnDelete) {
			handleIndividualDelete();
		}
		// New venue button
		else if (source == gui.btnNewVenue) {
			handleNewVenue();
		}
		// Shows combo box
		else if (source == gui.comboShowsFilter) {
			handleShowsFilter();
		}
		// Collect secondary data
		else if (source == gui.btnCollectData) {
			handleDataCollection();
		}
	}

	/**
	 * Open the file chooser when the browse button is clicked and handle the
	 * file that is returned.
	 */
	private void handleBrowseButton() {
		final JFileChooser fc = new JFileChooser();
		// Set up the filter for .csv and.gui.txt files
		final FileFilter filter = new FileNameExtensionFilter("CSV file",
				"csv", "gui.txt");
		fc.setFileFilter(filter);

		// Return value from the file chooser (tells if a file was selected)
		int returnVal = fc.showOpenDialog(gui.panelMain);

		// If file selected, set the text field'c contents to the path of it
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			gui.txtFldCSVFilePath.setText(fc.getSelectedFile().toString());
		}
	}

	/**
	 * Load the given csv file.
	 */
	private void handleLoadAudience() {
		// Get the venue
		final Venue venue = gui.getSelectedVenue();
		if (venue == null) {
			JOptionPane.showMessageDialog(gui.frame, Strings.GUI_NO_VENUE_SEL,
					"No venue selected", JOptionPane.ERROR_MESSAGE);
			return;
		}
		// Get the path to the csv file
		final String csvFileURL = gui.txtFldCSVFilePath.getText();
		// Load the list of individuals from the csv file
		final List<Individual> csvIndividuals;
		final SpektrixCSVParser csvParser = new SpektrixCSVParser();
		try {
			csvIndividuals = csvParser.loadfromCSV(csvFileURL, venue.getId());
		}
		catch (IOException | SQLException | ParseException e) {
			JOptionPane.showMessageDialog(gui.frame, Strings.GUI_CSV_ADD_ERR,
					"CSV file import error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Add the individuals to the database
		final int audienceCount;
		try {
			audienceCount = gui.pdbm.storeIndividual(csvIndividuals);
		}
		catch (SQLException e) {
			return;
		}
		// Show confirmation dialog
		JOptionPane.showMessageDialog(gui.frame, audienceCount
				+ " audience members added to the database.");

		// Clear the gui.table and show all individuals. This is used because
		// user
		// ID's can't be determined prior to adding them to the DB.
		gui.table.clearTable();
		try {
			gui.table.displayIndividuals(gui.pdbm.getAllIndividuals(),
					gui.venues);
		}
		catch (SQLException e) {
			JOptionPane.showMessageDialog(gui.frame, Strings.GUI_DB_READ_ERR,
					"Database error", JOptionPane.ERROR_MESSAGE);
		}

		// Jump to the last added individual
		final int lastIndividualRow = gui.table.getRowCount() - 1;
		gui.table.scrollRectToVisible(gui.table.getCellRect(lastIndividualRow,
				0, true));

		// Update shows list
		gui.updateShowsList();
	}

	/**
	 * Handle adding new person into the database, i.e. get the stuff from the
	 * text fields, validate it, process it, add it to the database and update
	 * the gui.table.
	 */
	private void handleAddPerson() {
		// Get values from the text fields
		final String firstName = gui.txtFldFirstName.getText();
		final String lastName = gui.txtFldSecondName.getText();
		final String email = gui.txtFldEmail.getText();
		final String seat = gui.txtFldSeat.getText();

		// Get the date
		final SimpleDateFormat dateFormatter = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm");
		final Date eventDate;
		try {
			eventDate = dateFormatter.parse(gui.txtFldVenueDate.getText());
		}
		catch (ParseException e1) {
			JOptionPane.showMessageDialog(gui.frame,
					Strings.GUI_DATE_FORMAT_ERR);
			return;
		}

		// Ensure that name and email are filled in, if not, shout
		if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
			JOptionPane.showMessageDialog(gui.frame, Strings.GUI_COMPUL_FLDS);
			return;
		}

		final Venue venue = gui.getSelectedVenue();
		if (venue == null) {
			JOptionPane.showMessageDialog(gui.frame, Strings.GUI_NO_VENUE_SEL,
					"No venue selected", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Save the individual
		final Individual newIndividual = Individual.getNewIndividual(firstName,
				lastName, email, eventDate, venue.getId(), seat, null);
		final long ID = gui.saveIndividual(newIndividual);

		// If there was an error
		if (ID == -1) {
			JOptionPane.showMessageDialog(gui.frame, Strings.GUI_DB_ADD_ERR,
					"Database error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Update the gui.table
		try {
			gui.table.displayIndividual(gui.pdbm.getById(ID), gui.venues);
		}
		catch (SQLException e) {
			JOptionPane.showMessageDialog(gui.frame, Strings.GUI_DB_CONN_ERR,
					"Database error", JOptionPane.ERROR_MESSAGE);
		}
		// Jump to the added line
		gui.table.scrollRectToVisible(gui.table.getCellRect(
				gui.table.getRowCount() - 1, 0, true));

		// Reset the text fields
		gui.txtFldFirstName.setText("");
		gui.txtFldSecondName.setText("");
		gui.txtFldEmail.setText("");
		gui.txtFldSeat.setText("");

		// Update shows
		gui.updateShowsList();
	}

	/**
	 * Handle refresh of the gui.table - i.e. reload its contents from the DB.
	 */
	private void handleRefresh() {
		gui.table.clearTable();
		try {
			gui.table.displayIndividuals(gui.pdbm.getAllIndividuals(),
					gui.venues);
		}
		catch (SQLException e1) {
			JOptionPane.showMessageDialog(gui.frame, Strings.GUI_DB_READ_ERR,
					"Database error", JOptionPane.ERROR_MESSAGE);
		}
	}


	/**
	 * Handle collection of data - execute all collectors in separate threads.
	 */
	private void handleDataCollection() {
		
		// TODO: This should be done in threads.
		
		// Get the selected show. If null, call collect on all data
		final Show show = gui.getSelectedShow();
		// Get the indexes of the checked collectors
		final List<Integer> checked = gui.tableColl.getCheckedRows();
		// Select only checked collectors.
		final List<Class<?>> checkedColl = new ArrayList<>();
		for (final int i : checked) {
			checkedColl.add(gui.dataCollectors.get(i));
		}

		if (checkedColl.size() == 0) {
			JOptionPane.showMessageDialog(gui.frame, Strings.GUI_SELECT_COLL,
					"No collectors selected", JOptionPane.INFORMATION_MESSAGE);
		}

		// Go through all collectors and invoke their run() methods
		for (final Class<?> collClass : checkedColl) {
			try {
				final SecondaryDataCollector collector = (SecondaryDataCollector) collClass
						.getConstructor(SecondaryDatabaseManager.class)
						.newInstance(gui.sdbm);
				if (show == null) {
					collector.setup(gui.pdbm.getAllIndividuals());
				}
				else {
					collector.setup(gui.pdbm.getByShow(show));
				}

				// Start the collector in a new thread
				Thread collectorThread = new Thread(collector); 
				collectorThread.run();
			}
			catch (InstantiationException | IllegalAccessException
					| SQLException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException
					| SecurityException e) {
				JOptionPane.showMessageDialog(gui.frame,
						Strings.GUI_COLLECTORS_ERR, "Error loading collectors",
						JOptionPane.ERROR_MESSAGE);
			}

		}
	}

	/**
	 * Handle changes of the combobox that filters the database view by shows.
	 */
	private void handleShowsFilter() {
		final int selectedIndex = gui.comboShowsFilter.getSelectedIndex();
		// Don't filter selected
		if (selectedIndex == 0) {
			handleRefresh();
			return;
		}
		// Get the show that is selected
		final Show selectedShow = gui.shows.get(selectedIndex - 1);
		// Get all individuals that are attending this show
		final List<Individual> filteredIndividuals;
		try {
			filteredIndividuals = gui.pdbm.getByShow(selectedShow);
		}
		catch (final SQLException e) {
			JOptionPane.showMessageDialog(gui.frame, Strings.GUI_DB_READ_ERR,
					"Database error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		// Refresh the gui.table view
		gui.table.clearTable();
		gui.table.displayIndividuals(filteredIndividuals, gui.venues);
	}

	/**
	 * Delete the individual on the selected row.
	 */
	private void handleIndividualDelete() {
		// Get ID of the selected individual
		final long selIndividualID = gui.table.getSelectedIndividualID();
		// No row selected
		if (selIndividualID == -1) {
			return;
		}
		// Delete the individual
		try {
			gui.pdbm.deleteIndividual(selIndividualID);
		}
		catch (SQLException e) {
			JOptionPane.showMessageDialog(gui.frame,
					Strings.GUI_DB_DEL_IND_ERR, "Database error",
					JOptionPane.ERROR_MESSAGE);
		}
		// Refresh the view
		handleRefresh();
	}

	/**
	 * Handle adding a new venue. Show dialog, update the combo box.
	 */
	private void handleNewVenue() {
		// Get the input from the user
		final String newVenue = JOptionPane.showInputDialog(gui.frame,
				Strings.GUI_SET_NEW_VENUE);
		// Cancel was pressed or empty string
		if (null == newVenue || newVenue.isEmpty()) {
			return;
		}
		// Create new venue in the database
		final int venueID;
		try {
			venueID = gui.pdbm.createVenue(newVenue);
			gui.updateVenuesList();
		}
		catch (SQLException e) {
			JOptionPane.showMessageDialog(gui.frame, Strings.GUI_VENUE_ADD_ERR,
					"CSV file import error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		// If addition OK - i.e. the venue wasn't in the db before, show it
		if (venueID != -1) {
			gui.comboVenues.addItem(newVenue);
		}
	}

}
