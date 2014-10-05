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

import javax.swing.JComboBox;
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
	private int collectorsStillRunning;

	/**
	 * @param gui The gui that this listener should be listening to, so that the
	 *            listener can access it.
	 */
	public DataCollectorGUIListener(final DataCollectorGUI gui) {
		this.gui = gui;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void actionPerformed(final ActionEvent e) {
		// Get the source
		final Object source = e.getSource();
		// The browse button, open File Chooser dialog
		if (source == this.gui.btnBrowse) {
			this.handleBrowseButton();
		}
		// The load audience button, load the given CSV file
		else if (source == this.gui.btnLoadAudience) {
			this.handleLoadAudience();
		}
		// The add single person button, add them to the DB
		else if (source == this.gui.btnAddPerson) {
			this.handleAddPerson();
		}
		// Done button, close the window
		else if (source == this.gui.btnDone) {
			this.gui.frame.dispose();
		}
		// Refresh button, refresh the DB view
		else if (source == this.gui.btnRefresh) {
			this.handleRefresh();
		}
		// Delete individual button
		else if (source == this.gui.btnDelete) {
			this.handleIndividualDelete();
		}
		// New venue button
		else if (source == this.gui.btnNewVenue) {
			this.handleNewVenue();
		}
		// Shows combo box
		else if (source == this.gui.comboShowsFilter
				|| source == this.gui.comboShowsCollec) {
			this.handleShowsFilter((JComboBox<String>) source);
		}
		// Collect secondary data
		else if (source == this.gui.btnCollectData) {
			this.handleDataCollection();
		}
	}

	/**
	 * Open the file chooser when the browse button is clicked and handle the
	 * file that is returned.
	 */
	private void handleBrowseButton() {
		final JFileChooser fc = new JFileChooser();
		// Set up the filter for .csv and .txt files
		final FileFilter filter = new FileNameExtensionFilter("CSV file",
				"csv", "gui.txt");
		fc.setFileFilter(filter);

		// Return value from the file chooser (tells if a file was selected)
		int returnVal = fc.showOpenDialog(this.gui.panelMain);

		// If file selected, set the text field'c contents to the path of it
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			this.gui.txtFldCSVFilePath.setText(fc.getSelectedFile().toString());
		}
	}

	/**
	 * Load the given csv file.
	 */
	private void handleLoadAudience() {
		// Get the venue
		final Venue venue = this.gui.getSelectedVenue();
		if (venue == null) {
			JOptionPane.showMessageDialog(this.gui.frame, Strings.GUI_NO_VENUE_SEL,
					"No venue selected", JOptionPane.ERROR_MESSAGE);
			return;
		}
		// Get the path to the csv file
		final String csvFileURL = this.gui.txtFldCSVFilePath.getText();
		// Load the list of individuals from the csv file
		final List<Individual> csvIndividuals;
		final SpektrixCSVParser csvParser = new SpektrixCSVParser();
		try {
			csvIndividuals = csvParser.loadfromCSV(csvFileURL, venue.getId());
		}
		catch (IOException | SQLException | ParseException e) {
			JOptionPane.showMessageDialog(this.gui.frame, Strings.GUI_CSV_ADD_ERR,
					"CSV file import error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Add the individuals to the database
		final int audienceCount;
		try {
			audienceCount = this.gui.pdbm.storeIndividual(csvIndividuals);
		}
		catch (SQLException e) {
			return;
		}
		// Show confirmation dialog
		JOptionPane.showMessageDialog(this.gui.frame, audienceCount
				+ " audience members added to the database.");

		// Clear the gui.table and show all individuals. This is used because
		// user ID's can't be determined prior to adding them to the DB.
		this.gui.table.clearTable();
		try {
			this.gui.table.displayIndividuals(this.gui.pdbm.getAllIndividuals(),
					this.gui.venues);
		}
		catch (SQLException e) {
			JOptionPane.showMessageDialog(this.gui.frame, Strings.GUI_DB_READ_ERR,
					"Database error", JOptionPane.ERROR_MESSAGE);
		}

		// Jump to the last added individual
		final int lastIndividualRow = this.gui.table.getRowCount() - 1;
		this.gui.table.scrollRectToVisible(this.gui.table.getCellRect(lastIndividualRow,
				0, true));

		// Update shows list
		this.gui.updateShowsCBs();
	}

	/**
	 * Handle adding new person into the database, i.e. get the stuff from the
	 * text fields, validate it, process it, add it to the database and update
	 * the gui.table.
	 */
	private void handleAddPerson() {
		// Get values from the text fields
		final String firstName = this.gui.txtFldFirstName.getText();
		final String lastName = this.gui.txtFldSecondName.getText();
		final String email = this.gui.txtFldEmail.getText();
		final String seat = this.gui.txtFldSeat.getText();

		// Get the date
		final SimpleDateFormat dateFormatter = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm");
		final Date eventDate;
		try {
			eventDate = dateFormatter.parse(this.gui.txtFldVenueDate.getText());
		}
		catch (ParseException e1) {
			JOptionPane.showMessageDialog(this.gui.frame,
					Strings.GUI_DATE_FORMAT_ERR);
			return;
		}

		// Ensure that name and email are filled in, if not, shout
		if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
			JOptionPane.showMessageDialog(this.gui.frame, Strings.GUI_COMPUL_FLDS);
			return;
		}

		final Venue venue = this.gui.getSelectedVenue();
		if (venue == null) {
			JOptionPane.showMessageDialog(this.gui.frame, Strings.GUI_NO_VENUE_SEL,
					"No venue selected", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Save the individual
		final Individual newIndividual = Individual.getNewIndividual(firstName,
				lastName, email, eventDate, venue.getId(), seat, null);
		final long ID = this.gui.saveIndividual(newIndividual);

		// If there was an error
		if (ID == -1) {
			JOptionPane.showMessageDialog(this.gui.frame, Strings.GUI_DB_ADD_ERR,
					"Database error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Update the gui.table
		try {
			this.gui.table.displayIndividual(this.gui.pdbm.getById(ID), this.gui.venues);
		}
		catch (SQLException e) {
			JOptionPane.showMessageDialog(this.gui.frame, Strings.GUI_DB_CONN_ERR,
					"Database error", JOptionPane.ERROR_MESSAGE);
		}
		// Jump to the added line
		this.gui.table.scrollRectToVisible(this.gui.table.getCellRect(
				this.gui.table.getRowCount() - 1, 0, true));

		// Reset the text fields
		this.gui.txtFldFirstName.setText("");
		this.gui.txtFldSecondName.setText("");
		this.gui.txtFldEmail.setText("");
		this.gui.txtFldSeat.setText("");

		// Update shows comboboxes
		this.gui.updateShowsCBs();
	}

	/**
	 * Handle refresh of the gui.table - i.e. reload its contents from the DB.
	 */
	private void handleRefresh() {
		this.gui.table.clearTable();
		try {
			this.gui.table.displayIndividuals(this.gui.pdbm.getAllIndividuals(),
					this.gui.venues);
		}
		catch (SQLException e1) {
			JOptionPane.showMessageDialog(this.gui.frame, Strings.GUI_DB_READ_ERR,
					"Database error", JOptionPane.ERROR_MESSAGE);
		}
		// Update shows comboboxes
		this.gui.updateShowsCBs();
	}

	/**
	 * Handle collection of data - execute all collectors in separate threads.
	 */
	private void handleDataCollection() {
		// Get the selected show. If null, call collect on all data
		final Show show = this.gui.getSelectedShow();
		// Get the indexes of the checked collectors
		final List<Integer> checked = this.gui.tableColl.getCheckedRows();
		// Select only checked collectors.
		final List<Class<?>> checkedColl = new ArrayList<>();
		for (final int i : checked) {
			checkedColl.add(this.gui.dataCollectors.get(i));
		}

		// If no collector selected, complain
		if (checkedColl.size() == 0) {
			JOptionPane.showMessageDialog(this.gui.frame, Strings.GUI_SELECT_COLL,
					"No collectors selected", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		// Set the amount of running collectors to zero
		this.collectorsStillRunning = 0;

		// Notify user that collection has started
		this.gui.progressBar.setIndeterminate(true);
		this.gui.progressBar.setString("Collecting data");

		// Go through all collectors and invoke their run() methods
		for (final Class<?> collClass : checkedColl) {
			final SecondaryDataCollector collector;
			try {
				collector = (SecondaryDataCollector) collClass.getConstructor(
						SecondaryDatabaseManager.class).newInstance(this.gui.sdbm);
				// No show selected - collect data on everyone
				if (show == null) {
					collector.setup(this.gui.pdbm.getAllIndividuals());
				}
				// Collect data only for the given show
				else {
					collector.setup(this.gui.pdbm.getByShow(show));
				}
			}
			catch (InstantiationException | IllegalAccessException
					| SQLException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException
					| SecurityException e) {
				JOptionPane.showMessageDialog(this.gui.frame,
						Strings.GUI_COLL_ERROR, "Error loading collectors",
						JOptionPane.ERROR_MESSAGE);
				
				// Stop the progress bar
				this.gui.progressBar.setIndeterminate(false);
				return;
			}

			// Start a new thread executing the collector
			this.startCollectorThread(collector);
		}
	}

	/**
	 * Starts a new thread running the given collector and increases the count
	 * of running collectors.
	 */
	synchronized private void startCollectorThread(
			final SecondaryDataCollector collector) {
		// Increase the count of running collectors
		this.collectorsStillRunning++;
		// Run the collector
		final CollectorTask task = new CollectorTask(collector, this);
		task.execute();
	}

	/**
	 * This method is called by the done() method of the CollectorTasks to
	 * notify that they have finished their execution. This knows what is the
	 * amount of running collectors and if the count reaches zero, it updates
	 * the progress bar to 100%.
	 */
	synchronized void notifyCollectorDone() {
		// Decrease the amount of running collectors
		this.collectorsStillRunning--;
		if (this.collectorsStillRunning == 0) {
			// Everything done, stop the progress bar and set to 100%
			this.gui.progressBar.setIndeterminate(false);
			this.gui.progressBar.setValue(100);

			// Notify the user that the collection is done
			JOptionPane.showMessageDialog(this.gui.frame, Strings.GUI_COLL_DONE);
			this.gui.progressBar.setString("Collection done");
		}
	}

	/**
	 * Handle changes of the combobox that filters the database view by shows.
	 */
	private void handleShowsFilter(final JComboBox<String> comboBox) {
		final int selectedIndex = comboBox.getSelectedIndex();
		// Don't filter selected
		if (selectedIndex <= 0) {
			this.handleRefresh();
			return;
		}
		// Get the show that is selected
		final Show selectedShow = this.gui.shows.get(selectedIndex - 1);
		// Get all individuals that are attending this show
		final List<Individual> filteredIndividuals;
		try {
			filteredIndividuals = this.gui.pdbm.getByShow(selectedShow);
		}
		catch (final SQLException e) {
			JOptionPane.showMessageDialog(this.gui.frame, Strings.GUI_COLL_DONE);
			return;
		}
		// Refresh the gui.table view
		this.gui.table.clearTable();
		this.gui.table.displayIndividuals(filteredIndividuals, this.gui.venues);
	}

	/**
	 * Delete the individual on the selected row.
	 */
	private void handleIndividualDelete() {
		// Get ID of the selected individual
		final long selIndividualID = this.gui.table.getSelectedIndividualID();
		// No row selected
		if (selIndividualID == -1) {
			return;
		}
		// Delete the individual
		try {
			this.gui.pdbm.deleteIndividual(selIndividualID);
		}
		catch (SQLException e) {
			JOptionPane.showMessageDialog(this.gui.frame,
					Strings.GUI_DB_DEL_IND_ERR, "Database error",
					JOptionPane.ERROR_MESSAGE);
		}
		// Refresh the view
		this.handleRefresh();
	}

	/**
	 * Handle adding a new venue. Show dialog, update the combo box.
	 */
	private void handleNewVenue() {
		// Get the input from the user
		final String newVenue = JOptionPane.showInputDialog(this.gui.frame,
				Strings.GUI_SET_NEW_VENUE);
		// Cancel was pressed or empty string
		if (null == newVenue || newVenue.isEmpty()) {
			return;
		}
		// Create new venue in the database
		final int venueID;
		try {
			venueID = this.gui.pdbm.createVenue(newVenue);
			this.gui.updateVenuesList();
		}
		catch (SQLException e) {
			JOptionPane.showMessageDialog(this.gui.frame, Strings.GUI_VENUE_ADD_ERR,
					"Venue adding error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		// If addition OK - i.e. the venue wasn't in the db before, show it
		if (venueID != -1) {
			this.gui.comboVenues.addItem(newVenue);
			this.gui.comboVenues
					.setSelectedIndex(this.gui.comboVenues.getItemCount() - 1);
		}
	}

}
