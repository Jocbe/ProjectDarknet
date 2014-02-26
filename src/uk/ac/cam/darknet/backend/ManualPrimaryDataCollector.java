package uk.ac.cam.darknet.backend;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DateFormatter;

import uk.ac.cam.darknet.common.AttributeCategories;
import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.common.Show;
import uk.ac.cam.darknet.common.Strings;
import uk.ac.cam.darknet.common.Venue;
import uk.ac.cam.darknet.database.PrimaryDatabaseManager;
import uk.ac.cam.darknet.exceptions.ConfigFileNotFoundException;
import uk.ac.cam.darknet.gui.IndividualTable;

import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JComboBox;

/**
 * GUI for the primary data collector. Displays the current contents of the
 * database, gives option to load new people from a CSV file or input them
 * manually.
 * 
 * @author Augustin Zidek
 * 
 */
public class ManualPrimaryDataCollector extends PrimaryDataCollector implements
		ActionListener {
	private JFrame frame;
	private JTextField txtFldFirstName;
	private JTextField txtFldSecondName;
	private JTextField txtFldEmail;
	private IndividualTable table;
	private JTextField txtFldSeat;
	private JTextField txtFldCSVFilePath;
	private JFormattedTextField txtFldVenueDate;
	private JButton btnBrowse;
	private JPanel panel;
	private JButton btnLoadAudience;
	private JButton btnAddPerson;
	private JButton btnDone;
	private JButton btnRefresh;
	private JButton btnNewVenue;
	private JButton btnDelete;
	private JComboBox<String> comboShowsFilter;
	private JComboBox<String> comboVenues;
	private PrimaryDatabaseManager dbm;
	private List<Show> shows;
	private List<Venue> venues;

	/**
	 * Creates new primary data collector. The GUI and everything else is
	 * started using the run method.
	 * 
	 * @param databaseManager The primary database manager.
	 */
	public ManualPrimaryDataCollector(
			final PrimaryDatabaseManager databaseManager) {
		super(databaseManager);
		this.dbm = databaseManager;
	}

	/**
	 * Initialize the GUI.
	 */
	public void run() {
		// Show the GUI
		initializeGUI();
		frame.setVisible(true);
		// If the connection to the database was successful, show all
		// individuals in the table
		if (this.dbm == null) {
			JOptionPane.showMessageDialog(frame, Strings.GUI_DB_CONN_ERR,
					"Database error", JOptionPane.ERROR_MESSAGE);
		}
		else {
			// Get all the individuals that are already in the DB
			final List<Individual> individualsInDB = getDBContent();
			// Display them in the table
			table.displayIndividuals(individualsInDB);
			// Populate the comboboxes
			populateComboBoxes();
		}
	}

	// TODO: split populate combo boxes and add clear

	/**
	 * Updates the field shows with data from the database.
	 */
	private void updateShowsList() {
		try {
			shows = dbm.getAllShows();
		}
		catch (SQLException e) {
			JOptionPane.showMessageDialog(frame, Strings.GUI_DB_VEN_ERR,
					"Database error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Updates the field venues with data from the database.
	 */
	private void updateVenuesList() {
		try {
			venues = dbm.getAllVenues();
		}
		catch (SQLException e) {
			JOptionPane.showMessageDialog(frame, Strings.GUI_DB_VEN_ERR,
					"Database error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Populates the comboboxes with data from the database.
	 */
	private void populateComboBoxes() {
		// Venues
		updateVenuesList();
		// Add blank no venue selected item
		comboVenues.addItem(Strings.GUI_NO_VENUE_SEL);
		for (final Venue v : venues) {
			comboVenues.addItem(v.getName());
		}
		// Shows
		updateShowsList();
		final SimpleDateFormat sdf = new SimpleDateFormat(
				Strings.GUI_DATE_FORMAT);
		// Add don't filter item
		comboShowsFilter.addItem(Strings.GUI_DONT_FILTER);
		for (final Show s : shows) {
			comboShowsFilter.addItem(s.getVenue().getName() + " at "
					+ sdf.format(s.getDate()));
		}

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initializeGUI() {
		frame = new JFrame();
		frame.setTitle("Primary Data Collector");
		frame.setBounds(100, 100, 601, 624);
		frame.setMinimumSize(new Dimension(601, 624));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);

		final JLabel lblLoadFromCsv = new JLabel("Load from CSV file");
		lblLoadFromCsv.setFont(new Font("Tahoma", Font.BOLD, 11));

		final JSeparator separator = new JSeparator();

		final JLabel lblInputManually = new JLabel("Manual input");
		lblInputManually.setFont(new Font("Tahoma", Font.BOLD, 11));

		btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(this);

		btnLoadAudience = new JButton("Load audience");
		btnLoadAudience.addActionListener(this);

		txtFldFirstName = new JTextField();
		txtFldFirstName.setColumns(10);

		txtFldSecondName = new JTextField();
		txtFldSecondName.setColumns(10);

		txtFldEmail = new JTextField();
		txtFldEmail.setColumns(10);

		final JLabel lblSetThe = new JLabel("Set the show");
		lblSetThe.setFont(new Font("Tahoma", Font.BOLD, 11));

		final JLabel lblVenue = new JLabel("Venue name*:");

		final JLabel lblDate = new JLabel("Show date*:");

		// Date formatted text field
		final DateFormatter formatter = new DateFormatter(new SimpleDateFormat(
				"yyyy-MM-dd HH:mm"));
		txtFldVenueDate = new JFormattedTextField(formatter);
		txtFldVenueDate.setColumns(10);
		txtFldVenueDate.setValue(new Date());
		txtFldVenueDate
				.setToolTipText("Enter date in the format yyyy-MM-dd HH:mm.");

		final JLabel lblName = new JLabel("First name*:");

		final JLabel lblSecondName = new JLabel("Second name*:");

		final JLabel lblEmail = new JLabel("Email*:");

		final JLabel lblSeat = new JLabel("Seat:");

		txtFldSeat = new JTextField();
		txtFldSeat.setColumns(10);

		final JLabel lblFieldsMarked = new JLabel(
				"* Fields marked with asterisk are compulsory");
		lblFieldsMarked.setFont(new Font("Tahoma", Font.PLAIN, 10));

		btnAddPerson = new JButton("Add person");
		btnAddPerson.addActionListener(this);

		txtFldCSVFilePath = new JTextField();
		txtFldCSVFilePath.setColumns(10);

		// People in the database
		final JSeparator separator_1 = new JSeparator();

		final JLabel lblPeopleInDB = new JLabel(
				"People currently in the database");
		lblPeopleInDB.setFont(new Font("Tahoma", Font.BOLD, 11));

		final JScrollPane scrollPane = new JScrollPane();

		// The table of individuals
		table = new IndividualTable();
		scrollPane.setViewportView(table);

		btnDone = new JButton("Done");
		btnDone.addActionListener(this);

		btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(this);

		comboVenues = new JComboBox<>();

		final JSeparator separator_2 = new JSeparator();

		comboShowsFilter = new JComboBox<String>();
		comboShowsFilter.addActionListener(this);
		comboShowsFilter.setMaximumRowCount(20);

		final JLabel lblFilterByShow = new JLabel("Filter by show");

		btnNewVenue = new JButton("New venue");
		btnNewVenue.addActionListener(this);

		btnDelete = new JButton("Delete individual");
		btnDelete.addActionListener(this);

		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel
				.createParallelGroup(Alignment.LEADING)
				.addGroup(
						gl_panel.createSequentialGroup()
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.LEADING)
												.addGroup(
														gl_panel.createSequentialGroup()
																.addGap(12)
																.addGroup(
																		gl_panel.createParallelGroup(
																				Alignment.LEADING)
																				.addGroup(
																						gl_panel.createSequentialGroup()
																								.addGap(87)
																								.addGroup(
																										gl_panel.createParallelGroup(
																												Alignment.TRAILING,
																												false)
																												.addComponent(
																														txtFldEmail,
																														Alignment.LEADING)
																												.addComponent(
																														txtFldFirstName,
																														Alignment.LEADING,
																														GroupLayout.DEFAULT_SIZE,
																														167,
																														Short.MAX_VALUE))
																								.addPreferredGap(
																										ComponentPlacement.RELATED)
																								.addGroup(
																										gl_panel.createParallelGroup(
																												Alignment.LEADING)
																												.addComponent(
																														lblSeat,
																														GroupLayout.PREFERRED_SIZE,
																														55,
																														GroupLayout.PREFERRED_SIZE)
																												.addComponent(
																														lblSecondName,
																														GroupLayout.PREFERRED_SIZE,
																														98,
																														GroupLayout.PREFERRED_SIZE))
																								.addPreferredGap(
																										ComponentPlacement.RELATED)
																								.addGroup(
																										gl_panel.createParallelGroup(
																												Alignment.LEADING)
																												.addComponent(
																														txtFldSecondName,
																														GroupLayout.PREFERRED_SIZE,
																														191,
																														GroupLayout.PREFERRED_SIZE)
																												.addGroup(
																														gl_panel.createSequentialGroup()
																																.addComponent(
																																		txtFldSeat,
																																		GroupLayout.PREFERRED_SIZE,
																																		73,
																																		GroupLayout.PREFERRED_SIZE)
																																.addGap(13)
																																.addComponent(
																																		btnAddPerson,
																																		GroupLayout.PREFERRED_SIZE,
																																		105,
																																		GroupLayout.PREFERRED_SIZE)
																																.addGap(0,
																																		0,
																																		Short.MAX_VALUE))))
																				.addGroup(
																						gl_panel.createSequentialGroup()
																								.addPreferredGap(
																										ComponentPlacement.RELATED)
																								.addComponent(
																										txtFldCSVFilePath,
																										GroupLayout.PREFERRED_SIZE,
																										326,
																										GroupLayout.PREFERRED_SIZE)
																								.addPreferredGap(
																										ComponentPlacement.RELATED)
																								.addComponent(
																										btnBrowse,
																										GroupLayout.PREFERRED_SIZE,
																										79,
																										GroupLayout.PREFERRED_SIZE)
																								.addPreferredGap(
																										ComponentPlacement.RELATED)
																								.addComponent(
																										btnLoadAudience,
																										GroupLayout.PREFERRED_SIZE,
																										150,
																										GroupLayout.PREFERRED_SIZE))
																				.addGroup(
																						gl_panel.createSequentialGroup()
																								.addPreferredGap(
																										ComponentPlacement.RELATED)
																								.addGroup(
																										gl_panel.createParallelGroup(
																												Alignment.LEADING)
																												.addGroup(
																														gl_panel.createSequentialGroup()
																																.addComponent(
																																		btnRefresh)
																																.addPreferredGap(
																																		ComponentPlacement.RELATED)
																																.addComponent(
																																		btnDelete)
																																.addPreferredGap(
																																		ComponentPlacement.RELATED,
																																		258,
																																		Short.MAX_VALUE)
																																.addComponent(
																																		btnDone,
																																		GroupLayout.PREFERRED_SIZE,
																																		98,
																																		GroupLayout.PREFERRED_SIZE))
																												.addGroup(
																														gl_panel.createSequentialGroup()
																																.addComponent(
																																		lblFilterByShow)
																																.addPreferredGap(
																																		ComponentPlacement.RELATED)
																																.addComponent(
																																		comboShowsFilter,
																																		GroupLayout.PREFERRED_SIZE,
																																		286,
																																		GroupLayout.PREFERRED_SIZE)))))
																.addPreferredGap(
																		ComponentPlacement.RELATED))
												.addGroup(
														gl_panel.createSequentialGroup()
																.addContainerGap()
																.addComponent(
																		lblSetThe,
																		GroupLayout.PREFERRED_SIZE,
																		98,
																		GroupLayout.PREFERRED_SIZE))
												.addGroup(
														gl_panel.createSequentialGroup()
																.addContainerGap()
																.addComponent(
																		lblVenue,
																		GroupLayout.PREFERRED_SIZE,
																		79,
																		GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		ComponentPlacement.RELATED)
																.addComponent(
																		comboVenues,
																		GroupLayout.PREFERRED_SIZE,
																		134,
																		GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		ComponentPlacement.RELATED)
																.addComponent(
																		btnNewVenue)
																.addPreferredGap(
																		ComponentPlacement.RELATED)
																.addComponent(
																		lblDate,
																		GroupLayout.PREFERRED_SIZE,
																		77,
																		GroupLayout.PREFERRED_SIZE)
																.addGap(4)
																.addComponent(
																		txtFldVenueDate,
																		GroupLayout.PREFERRED_SIZE,
																		147,
																		GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		ComponentPlacement.RELATED,
																		GroupLayout.DEFAULT_SIZE,
																		Short.MAX_VALUE))
												.addGroup(
														gl_panel.createSequentialGroup()
																.addContainerGap()
																.addComponent(
																		lblLoadFromCsv,
																		GroupLayout.PREFERRED_SIZE,
																		118,
																		GroupLayout.PREFERRED_SIZE)))
								.addGap(12))
				.addGroup(
						gl_panel.createSequentialGroup()
								.addContainerGap()
								.addComponent(separator_2,
										GroupLayout.PREFERRED_SIZE, 566,
										GroupLayout.PREFERRED_SIZE)
								.addContainerGap(13, Short.MAX_VALUE))
				.addGroup(
						gl_panel.createSequentialGroup()
								.addContainerGap()
								.addComponent(lblInputManually,
										GroupLayout.PREFERRED_SIZE, 118,
										GroupLayout.PREFERRED_SIZE)
								.addContainerGap(461, Short.MAX_VALUE))
				.addGroup(
						gl_panel.createSequentialGroup()
								.addContainerGap()
								.addComponent(lblName,
										GroupLayout.PREFERRED_SIZE, 98,
										GroupLayout.PREFERRED_SIZE)
								.addContainerGap(481, Short.MAX_VALUE))
				.addGroup(
						gl_panel.createSequentialGroup()
								.addContainerGap()
								.addComponent(lblEmail,
										GroupLayout.PREFERRED_SIZE, 55,
										GroupLayout.PREFERRED_SIZE)
								.addContainerGap(524, Short.MAX_VALUE))
				.addGroup(
						gl_panel.createSequentialGroup()
								.addContainerGap()
								.addComponent(lblFieldsMarked,
										GroupLayout.DEFAULT_SIZE, 364,
										Short.MAX_VALUE).addGap(215))
				.addGroup(
						gl_panel.createSequentialGroup()
								.addContainerGap()
								.addComponent(separator_1,
										GroupLayout.PREFERRED_SIZE, 567,
										GroupLayout.PREFERRED_SIZE)
								.addContainerGap(GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE))
				.addGroup(
						gl_panel.createSequentialGroup()
								.addContainerGap()
								.addComponent(lblPeopleInDB,
										GroupLayout.PREFERRED_SIZE, 240,
										GroupLayout.PREFERRED_SIZE)
								.addContainerGap(339, Short.MAX_VALUE))
				.addGroup(
						gl_panel.createSequentialGroup()
								.addContainerGap()
								.addComponent(scrollPane,
										GroupLayout.DEFAULT_SIZE, 567,
										Short.MAX_VALUE).addContainerGap())
				.addGroup(
						gl_panel.createSequentialGroup()
								.addContainerGap()
								.addComponent(separator,
										GroupLayout.PREFERRED_SIZE, 567,
										GroupLayout.PREFERRED_SIZE)
								.addContainerGap(GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)));
		gl_panel.setVerticalGroup(gl_panel
				.createParallelGroup(Alignment.LEADING)
				.addGroup(
						gl_panel.createSequentialGroup()
								.addContainerGap()
								.addComponent(lblSetThe)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.BASELINE)
												.addComponent(lblVenue)
												.addComponent(
														comboVenues,
														GroupLayout.PREFERRED_SIZE,
														20,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(
														txtFldVenueDate,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(btnNewVenue)
												.addComponent(lblDate))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(separator,
										GroupLayout.PREFERRED_SIZE, 2,
										GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(lblLoadFromCsv,
										GroupLayout.PREFERRED_SIZE, 16,
										GroupLayout.PREFERRED_SIZE)
								.addGap(4)
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.BASELINE)
												.addComponent(
														txtFldCSVFilePath,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(
														btnLoadAudience,
														GroupLayout.PREFERRED_SIZE,
														23,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(
														btnBrowse,
														GroupLayout.PREFERRED_SIZE,
														23,
														GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(separator_2,
										GroupLayout.PREFERRED_SIZE, 1,
										GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(lblInputManually,
										GroupLayout.PREFERRED_SIZE, 16,
										GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.BASELINE)
												.addComponent(lblName)
												.addComponent(
														txtFldFirstName,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(lblSecondName)
												.addComponent(
														txtFldSecondName,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.BASELINE)
												.addComponent(lblEmail)
												.addComponent(
														txtFldEmail,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(lblSeat)
												.addComponent(
														txtFldSeat,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(
														btnAddPerson,
														GroupLayout.PREFERRED_SIZE,
														23,
														GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(lblFieldsMarked,
										GroupLayout.PREFERRED_SIZE, 16,
										GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(separator_1,
										GroupLayout.PREFERRED_SIZE, 2,
										GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(lblPeopleInDB,
										GroupLayout.PREFERRED_SIZE, 16,
										GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.BASELINE)
												.addComponent(lblFilterByShow)
												.addComponent(
														comboShowsFilter,
														GroupLayout.PREFERRED_SIZE,
														20,
														GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(scrollPane,
										GroupLayout.DEFAULT_SIZE, 273,
										Short.MAX_VALUE)
								.addGap(2)
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.BASELINE)
												.addComponent(btnRefresh)
												.addComponent(btnDone)
												.addComponent(btnDelete))
								.addGap(8)));
		panel.setLayout(gl_panel);
	}

	private Venue getSelectedVenue() {
		// No venue selected
		if (comboVenues.getSelectedIndex() == 0) {
			return null;
		}
		return venues.get(comboVenues.getSelectedIndex() - 1);
	}

	/**
	 * Get the list of all individuals that are already in the database.
	 * 
	 * @return The list of all individuals already in the database.
	 */
	private List<Individual> getDBContent() {
		try {
			return dbm.getAllIndividuals();
		}
		catch (SQLException e) {
			JOptionPane.showMessageDialog(frame, Strings.GUI_DB_READ_ERR,
					"Database error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}

	/**
	 * Save the given individual in the db. Show error dialog if error.
	 * 
	 * @param i The individual to be saved.
	 * @return The ID of the individual, or -1 in case of error.
	 */
	private long saveIndividual(final Individual i) {
		final long status;
		try {
			status = dbm.storeIndividual(i);
		}
		catch (SQLException e1) {
			JOptionPane.showMessageDialog(frame, Strings.GUI_DB_ADD_ERR,
					"Database error", JOptionPane.ERROR_MESSAGE);
			return -1;
		}
		return status;
	}

	/**
	 * Open the file chooser when the browse button is clicked and handle the
	 * file that is returned.
	 */
	private void handleBrowseButton() {
		final JFileChooser fc = new JFileChooser();
		// Set up the filter for .csv and.txt files
		final FileFilter filter = new FileNameExtensionFilter("CSV file",
				"csv", "txt");
		fc.setFileFilter(filter);

		// Return value from the file chooser (tells if a file was selected)
		int returnVal = fc.showOpenDialog(panel);

		// If file selected, set the text field'c contents to the path of it
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			txtFldCSVFilePath.setText(fc.getSelectedFile().toString());
		}
	}

	/**
	 * Load the given csv file.
	 */
	private void handleLoadAudience() {
		// Get the venue
		final Venue venue = getSelectedVenue();
		if (venue == null) {
			JOptionPane.showMessageDialog(frame, Strings.GUI_NO_VENUE_SEL,
					"No venue selected", JOptionPane.ERROR_MESSAGE);
			return;
		}
		// Get the path to the csv file
		final String csvFileURL = txtFldCSVFilePath.getText();
		// Load the list of individuals from the csv file
		final List<Individual> csvIndividuals;
		final SpektrixCSVParser csvParser = new SpektrixCSVParser();
		try {
			csvIndividuals = csvParser.loadfromCSV(csvFileURL, venue.getId());
		}
		catch (IOException | SQLException | ParseException e) {
			JOptionPane.showMessageDialog(frame, Strings.GUI_CSV_ADD_ERR,
					"CSV file import error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Add the individuals to the database
		final int audienceCount;
		try {
			audienceCount = dbm.storeIndividual(csvIndividuals);
		}
		catch (SQLException e) {
			return;
		}
		// Show confirmation dialog
		JOptionPane.showMessageDialog(frame, audienceCount
				+ " audience members added to the database.");

		// Clear the table and show all individuals. This is used because user
		// ID's can't be determined prior to adding them to the DB.
		table.clearTable();
		try {
			table.displayIndividuals(dbm.getAllIndividuals());
		}
		catch (SQLException e) {
			JOptionPane.showMessageDialog(frame, Strings.GUI_DB_READ_ERR,
					"Database error", JOptionPane.ERROR_MESSAGE);
		}

		// Jump to the last added individual
		final int lastIndividualRow = table.getRowCount() - 1;
		table.scrollRectToVisible(table.getCellRect(lastIndividualRow, 0, true));

		// Update shows list
		updateShowsList();
	}

	/**
	 * Handle adding new person into the database, i.e. get the stuff from the
	 * text fields, validate it, process it, add it to the database and update
	 * the table.
	 */
	private void handleAddPerson() {
		// Get values from the text fields
		final String firstName = txtFldFirstName.getText();
		final String lastName = txtFldSecondName.getText();
		final String email = txtFldEmail.getText();
		final String seat = txtFldSeat.getText();

		// Get the date
		final SimpleDateFormat dateFormatter = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm");
		final Date eventDate;
		try {
			eventDate = dateFormatter.parse(txtFldVenueDate.getText());
		}
		catch (ParseException e1) {
			JOptionPane.showMessageDialog(frame, Strings.GUI_DATE_FORMAT_ERR);
			return;
		}

		// Ensure that name and email are filled in, if not, shout
		if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
			JOptionPane.showMessageDialog(frame, Strings.GUI_COMPUL_FLDS);
			return;
		}

		final Venue venue = getSelectedVenue();
		if (venue == null) {
			JOptionPane.showMessageDialog(frame, Strings.GUI_NO_VENUE_SEL,
					"No venue selected", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Save the individual
		final Individual newIndividual = Individual.getNewIndividual(firstName,
				lastName, email, eventDate, venue.getId(), seat, null);
		final long ID = saveIndividual(newIndividual);

		// If there was an error
		if (ID == -1) {
			JOptionPane.showMessageDialog(frame, Strings.GUI_DB_ADD_ERR,
					"Database error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Update the table
		try {
			table.displayIndividual(dbm.getById(ID));
		}
		catch (SQLException e) {
			JOptionPane.showMessageDialog(frame, Strings.GUI_DB_CONN_ERR,
					"Database error", JOptionPane.ERROR_MESSAGE);
		}
		// Jump to the added line
		table.scrollRectToVisible(table.getCellRect(table.getRowCount() - 1, 0,
				true));

		// Reset the text fields
		txtFldFirstName.setText("");
		txtFldSecondName.setText("");
		txtFldEmail.setText("");
		txtFldSeat.setText("");

		// Update shows
		updateShowsList();
	}

	/**
	 * Handle refresh of the table - i.e. reload its contents from the DB.
	 */
	private void handleRefresh() {
		table.clearTable();
		try {
			table.displayIndividuals(dbm.getAllIndividuals());
		}
		catch (SQLException e1) {
			JOptionPane.showMessageDialog(frame, Strings.GUI_DB_READ_ERR,
					"Database error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Action Listeners for all the buttons.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// Get the source
		final Object source = e.getSource();
		// The browse button, open File Chooser dialog
		if (source == btnBrowse) {
			handleBrowseButton();
		}
		// The load audience button, load the given CSV file
		else if (source == btnLoadAudience) {
			handleLoadAudience();
		}
		// The add single person button, add them to the DB
		else if (source == btnAddPerson) {
			handleAddPerson();
		}
		// Done button, close the window
		else if (source == btnDone) {
			frame.dispose();
		}
		// Refresh button, refresh the DB view
		else if (source == btnRefresh) {
			handleRefresh();
		}
		// Delete individual button
		else if (source == btnDelete) {
			handleIndividualDelete();
		}
		// New venue button
		else if (source == btnNewVenue) {
			handleNewVenue();
		}
		// Shows combo box
		else if (source == comboShowsFilter) {
			handleShowsFilter();
		}
	}

	/**
	 * Handle changes of the combobox that filters the database view by shows.
	 */
	private void handleShowsFilter() {
		final int selectedIndex = comboShowsFilter.getSelectedIndex();
		// Don't filter selected
		if (selectedIndex == 0) {
			handleRefresh();
			return;
		}
		// Get the show that is selected
		final Show selectedShow = shows.get(selectedIndex - 1);
		// Get all individuals that are attending this show
		final List<Individual> filteredIndividuals;
		try {
			filteredIndividuals = dbm.getByShow(selectedShow);
		}
		catch (final SQLException e) {
			JOptionPane.showMessageDialog(frame, Strings.GUI_DB_READ_ERR,
					"Database error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		// Refresh the table view
		table.clearTable();
		table.displayIndividuals(filteredIndividuals);
	}

	/**
	 * Delete the individual on the selected row.
	 */
	private void handleIndividualDelete() {
		// Get ID of the selected individual
		final long selIndividualID = table.getSelectedIndividualID();
		// No row selected
		if (selIndividualID == -1) {
			return;
		}
		// Delete the individual
		try {
			dbm.deleteIndividual(selIndividualID);
		}
		catch (SQLException e) {
			JOptionPane.showMessageDialog(frame, Strings.GUI_DB_DEL_IND_ERR,
					"Database error", JOptionPane.ERROR_MESSAGE);
		}
		// Refresh the view
		handleRefresh();
	}

	/**
	 * Handle adding a new venue. Show dialog, update the combo box.
	 */
	private void handleNewVenue() {
		// Get the input from the user
		final String newVenue = JOptionPane.showInputDialog(frame,
				Strings.GUI_SET_NEW_VENUE);
		// Cancel was pressed or empty string
		if (null == newVenue || newVenue.isEmpty()) {
			return;
		}
		// Create new venue in the database
		final int venueID;
		try {
			venueID = dbm.createVenue(newVenue);
			updateVenuesList();
		}
		catch (SQLException e) {
			JOptionPane.showMessageDialog(frame, Strings.GUI_VENUE_ADD_ERR,
					"CSV file import error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		// If addition OK - i.e. the venue wasn't in the db before, show it
		if (venueID != -1) {
			comboVenues.addItem(newVenue);
		}
	}

	/**
	 * Launch the GUI, for testing purposes only.
	 * 
	 * @param args Ignored
	 */
	public static void main(String[] args) {
		// Start the database manager
		final Hashtable<String, AttributeCategories> emptyTable = new Hashtable<>();
		PrimaryDatabaseManager dbm;
		try {
			dbm = new PrimaryDatabaseManager(emptyTable);
		}
		catch (ClassNotFoundException | ConfigFileNotFoundException
				| IOException | SQLException e) {
			System.out.println("MPDataCollector: Exception in main().");
			e.printStackTrace();
			dbm = null;
		}

		final ManualPrimaryDataCollector pdcGUI = new ManualPrimaryDataCollector(
				dbm);
		pdcGUI.run();

	}
}
