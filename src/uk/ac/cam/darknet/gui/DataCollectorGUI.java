package uk.ac.cam.darknet.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.DateFormatter;

import uk.ac.cam.darknet.backend.PrimaryDataCollector;
import uk.ac.cam.darknet.common.AttributeCategories;
import uk.ac.cam.darknet.common.EffectsAndCollectorsLoader;
import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.common.Show;
import uk.ac.cam.darknet.common.Strings;
import uk.ac.cam.darknet.common.Venue;
import uk.ac.cam.darknet.database.PrimaryDatabaseManager;
import uk.ac.cam.darknet.database.SecondaryDatabaseManager;
import uk.ac.cam.darknet.exceptions.ConfigFileNotFoundException;
import uk.ac.cam.darknet.exceptions.InvalidAttributeNameException;
import java.awt.Toolkit;

/**
 * GUI for the primary data collector. Displays the current contents of the
 * database, gives option to load new people from a CSV file or input them
 * manually.
 * 
 * @author Augustin Zidek
 * 
 */
public class DataCollectorGUI extends PrimaryDataCollector {
	// Fields are package-protected so they can be accessed from the listener
	JFrame frame;
	JTextField txtFldFirstName;
	JTextField txtFldSecondName;
	JTextField txtFldEmail;
	IndividualTable table;
	JTextField txtFldSeat;
	JTextField txtFldCSVFilePath;
	JFormattedTextField txtFldVenueDate;
	JButton btnBrowse;
	JPanel panelMain;
	JButton btnLoadAudience;
	JButton btnAddPerson;
	JButton btnDone;
	JButton btnRefresh;
	JButton btnNewVenue;
	JButton btnDelete;
	JButton btnCollectData;
	JComboBox<String> comboShowsFilter;
	JComboBox<String> comboShowsColl;
	JComboBox<String> comboVenues;
	CollectorsTable tableColl;
	JScrollPane scrollPane_1;

	List<Show> shows;
	List<Venue> venues;
	List<Class<?>> dataCollectors;

	final PrimaryDatabaseManager pdbm;
	final SecondaryDatabaseManager sdbm;
	final DataCollectorGUIListener listener;
	JPanel panelPrimary;
	JPanel panelDatabase;
	JPanel panelShow;
	JPanel panelCSV;
	JPanel panel;
	JLabel label;

	/**
	 * Creates new primary data collector. The GUI and everything else is
	 * started using the run method.
	 * 
	 * @param pdbm The primary database manager.
	 * @param sdbm The secondary database manager.
	 */
	public DataCollectorGUI(final PrimaryDatabaseManager pdbm,
			final SecondaryDatabaseManager sdbm) {
		super(pdbm);
		this.pdbm = pdbm;
		this.sdbm = sdbm;
		this.listener = new DataCollectorGUIListener(this);
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
		if (this.pdbm == null) {
			JOptionPane.showMessageDialog(frame, Strings.GUI_DB_CONN_ERR,
					"Database error", JOptionPane.ERROR_MESSAGE);
		}
		else {
			// Get all the individuals that are already in the DB
			final List<Individual> individualsInDB = getDBContent();
			// Populate the comboboxes
			populateComboBoxes();
			// Display them in the table
			table.displayIndividuals(individualsInDB, venues);
		}
		populateCollectorsTable();
	}

	/**
	 * Updates the field shows with data from the database.
	 */
	void updateShowsList() {
		try {
			shows = pdbm.getAllShows();
		}
		catch (SQLException e) {
			JOptionPane.showMessageDialog(frame, Strings.GUI_DB_VEN_ERR,
					"Database error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Updates the field venues with data from the database.
	 */
	void updateVenuesList() {
		try {
			venues = pdbm.getAllVenues();
		}
		catch (SQLException e) {
			JOptionPane.showMessageDialog(frame, Strings.GUI_DB_VEN_ERR,
					"Database error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void populateVenueCB() {
		updateVenuesList();
		// Clear first
		comboVenues.removeAllItems();
		// Add blank no venue selected item
		comboVenues.addItem(Strings.GUI_NO_VENUE_SEL);
		for (final Venue v : venues) {
			comboVenues.addItem(v.getName());
		}
	}

	void populateShowsCBs() {
		updateShowsList();
		comboShowsColl.removeAllItems();
		comboShowsFilter.removeAllItems();

		final SimpleDateFormat sdf = new SimpleDateFormat(
				Strings.GUI_DATE_FORMAT);
		// Add don't filter item
		comboShowsFilter.addItem(Strings.GUI_DONT_FILTER);
		comboShowsColl.addItem(Strings.GUI_ALL_SHOWS);
		for (final Show s : shows) {
			final String show = s.getVenue().getName() + " at "
					+ sdf.format(s.getDate());
			comboShowsFilter.addItem(show);
			comboShowsColl.addItem(show);
		}
	}

	/**
	 * Fill the collectors table with data
	 */
	private void populateCollectorsTable() {
		// Read all classes that are in the backend package
		try {
			final EffectsAndCollectorsLoader loader = new EffectsAndCollectorsLoader();
			dataCollectors = loader.loadSecondaryCollectors();
		}
		catch (ClassNotFoundException | IOException a) {
			JOptionPane.showMessageDialog(frame, Strings.GUI_NO_COLLECTORS,
					"No collectors found", JOptionPane.ERROR_MESSAGE);
			return;
		}
		// Add the collectors into the table
		for (final Class<?> c : dataCollectors) {
			tableColl.addCollector(c.getSimpleName());
		}
	}

	/**
	 * Populates the comboboxes with data from the database.
	 */
	private void populateComboBoxes() {
		// Venues
		populateVenueCB();
		// Shows
		populateShowsCBs();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initializeGUI() {
		// Date formatted text field
		final DateFormatter formatter = new DateFormatter(new SimpleDateFormat(
				"yyyy-MM-dd HH:mm"));

		frame = new JFrame();
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(
				DataCollectorGUI.class
						.getResource("/uk/ac/cam/darknet/gui/icon.png")));
		frame.setTitle("Primary Data Collector");
		frame.setBounds(100, 100, 1020, 624);
		frame.setMinimumSize(new Dimension(1020, 624));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		panelMain = new JPanel();
		frame.getContentPane().add(panelMain, BorderLayout.CENTER);

		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 510, 510 };
		gbl_panel.rowHeights = new int[] { 300, 300 };
		gbl_panel.columnWeights = new double[] { 0.0, 0.0 };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0 };
		panelMain.setLayout(gbl_panel);

		panelPrimary = new JPanel();
		panelPrimary.setBorder(new TitledBorder(null,
				"Load the audience members", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panelPrimary = new GridBagConstraints();
		gbc_panelPrimary.weightx = 0.5;
		gbc_panelPrimary.fill = GridBagConstraints.BOTH;
		gbc_panelPrimary.insets = new Insets(5, 5, 5, 5);
		gbc_panelPrimary.gridx = 0;
		gbc_panelPrimary.gridy = 0;
		panelMain.add(panelPrimary, gbc_panelPrimary);

		panelShow = new JPanel();
		panelShow.setBorder(new TitledBorder(null, "Set the show",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));

		panelCSV = new JPanel();
		panelCSV.setBorder(new TitledBorder(null, "Import from CSV file",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));

		panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Manually add audience members",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));

		label = new JLabel("* Fields marked with asterisk are compulsory");
		label.setFont(new Font("Tahoma", Font.PLAIN, 10));
		GroupLayout gl_panelPrimary = new GroupLayout(panelPrimary);
		gl_panelPrimary
				.setHorizontalGroup(gl_panelPrimary
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_panelPrimary
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												gl_panelPrimary
														.createParallelGroup(
																Alignment.LEADING)
														.addGroup(
																gl_panelPrimary
																		.createParallelGroup(
																				Alignment.LEADING)
																		.addComponent(
																				panelShow,
																				GroupLayout.PREFERRED_SIZE,
																				0,
																				Short.MAX_VALUE)
																		.addComponent(
																				panelCSV,
																				0,
																				0,
																				Short.MAX_VALUE)
																		.addComponent(
																				panel,
																				GroupLayout.DEFAULT_SIZE,
																				460,
																				Short.MAX_VALUE))
														.addComponent(
																label,
																GroupLayout.PREFERRED_SIZE,
																202,
																GroupLayout.PREFERRED_SIZE))
										.addGap(13)));
		gl_panelPrimary.setVerticalGroup(gl_panelPrimary.createParallelGroup(
				Alignment.LEADING).addGroup(
				gl_panelPrimary
						.createSequentialGroup()
						.addComponent(panelShow, GroupLayout.PREFERRED_SIZE,
								86, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(panelCSV, GroupLayout.PREFERRED_SIZE, 54,
								GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(panel, GroupLayout.PREFERRED_SIZE, 91,
								GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(label, GroupLayout.PREFERRED_SIZE, 13,
								GroupLayout.PREFERRED_SIZE)
						.addContainerGap(GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)));
		GridBagLayout gbl_panel2 = new GridBagLayout();
		gbl_panel2.columnWidths = new int[] { 50, 160, 40, 60, 80 };
		gbl_panel2.rowHeights = new int[] { 20, 26, 0 };
		gbl_panel2.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0 };
		gbl_panel2.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel2);

		final JLabel lblName = new JLabel("Name*:");
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.anchor = GridBagConstraints.WEST;
		gbc_lblName.insets = new Insets(0, 0, 5, 5);
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 0;
		panel.add(lblName, gbc_lblName);

		txtFldFirstName = new JTextField();
		GridBagConstraints gbc_txtFldFirstName = new GridBagConstraints();
		gbc_txtFldFirstName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtFldFirstName.anchor = GridBagConstraints.NORTH;
		gbc_txtFldFirstName.insets = new Insets(0, 0, 5, 5);
		gbc_txtFldFirstName.gridx = 1;
		gbc_txtFldFirstName.gridy = 0;
		panel.add(txtFldFirstName, gbc_txtFldFirstName);
		txtFldFirstName.setColumns(10);

		final JLabel lblSecondName = new JLabel("Surname*:");
		GridBagConstraints gbc_lblSecondName = new GridBagConstraints();
		gbc_lblSecondName.anchor = GridBagConstraints.EAST;
		gbc_lblSecondName.insets = new Insets(0, 0, 5, 5);
		gbc_lblSecondName.gridx = 2;
		gbc_lblSecondName.gridy = 0;
		panel.add(lblSecondName, gbc_lblSecondName);

		txtFldSecondName = new JTextField();
		GridBagConstraints gbc_txtFldSecondName = new GridBagConstraints();
		gbc_txtFldSecondName.gridwidth = 2;
		gbc_txtFldSecondName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtFldSecondName.anchor = GridBagConstraints.NORTH;
		gbc_txtFldSecondName.gridx = 3;
		gbc_txtFldSecondName.gridy = 0;
		panel.add(txtFldSecondName, gbc_txtFldSecondName);
		txtFldSecondName.setColumns(10);

		final JLabel lblEmail = new JLabel("Email*:");
		GridBagConstraints gbc_lblEmail = new GridBagConstraints();
		gbc_lblEmail.anchor = GridBagConstraints.WEST;
		gbc_lblEmail.insets = new Insets(0, 0, 0, 5);
		gbc_lblEmail.gridx = 0;
		gbc_lblEmail.gridy = 1;
		panel.add(lblEmail, gbc_lblEmail);

		txtFldEmail = new JTextField();
		GridBagConstraints gbc_txtFldEmail = new GridBagConstraints();
		gbc_txtFldEmail.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtFldEmail.insets = new Insets(0, 0, 0, 5);
		gbc_txtFldEmail.gridx = 1;
		gbc_txtFldEmail.gridy = 1;
		panel.add(txtFldEmail, gbc_txtFldEmail);
		txtFldEmail.setColumns(10);

		final JLabel lblSeat = new JLabel("Seat:");
		GridBagConstraints gbc_lblSeat = new GridBagConstraints();
		gbc_lblSeat.anchor = GridBagConstraints.WEST;
		gbc_lblSeat.insets = new Insets(0, 0, 0, 5);
		gbc_lblSeat.gridx = 2;
		gbc_lblSeat.gridy = 1;
		panel.add(lblSeat, gbc_lblSeat);

		txtFldSeat = new JTextField();
		GridBagConstraints gbc_txtFldSeat = new GridBagConstraints();
		gbc_txtFldSeat.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtFldSeat.insets = new Insets(0, 0, 0, 5);
		gbc_txtFldSeat.gridx = 3;
		gbc_txtFldSeat.gridy = 1;
		panel.add(txtFldSeat, gbc_txtFldSeat);
		txtFldSeat.setColumns(10);

		btnAddPerson = new JButton("Add person");
		GridBagConstraints gbc_btnAddPerson = new GridBagConstraints();
		gbc_btnAddPerson.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnAddPerson.gridx = 4;
		gbc_btnAddPerson.gridy = 1;
		panel.add(btnAddPerson, gbc_btnAddPerson);
		btnAddPerson.addActionListener(listener);

		txtFldCSVFilePath = new JTextField();
		txtFldCSVFilePath.setColumns(10);

		btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(listener);

		btnLoadAudience = new JButton("Load audience");
		GroupLayout gl_panelCSV = new GroupLayout(panelCSV);
		gl_panelCSV.setHorizontalGroup(gl_panelCSV.createParallelGroup(
				Alignment.LEADING).addGroup(
				Alignment.TRAILING,
				gl_panelCSV
						.createSequentialGroup()
						.addContainerGap()
						.addComponent(txtFldCSVFilePath,
								GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnBrowse)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnLoadAudience)));
		gl_panelCSV
				.setVerticalGroup(gl_panelCSV
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_panelCSV
										.createSequentialGroup()
										.addGroup(
												gl_panelCSV
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																btnLoadAudience)
														.addComponent(btnBrowse)
														.addComponent(
																txtFldCSVFilePath,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addContainerGap(
												GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));
		panelCSV.setLayout(gl_panelCSV);
		btnLoadAudience.addActionListener(listener);

		final JLabel lblVenue = new JLabel("Venue*:");

		comboVenues = new JComboBox<>();

		final JLabel lblDate = new JLabel("Show date*:");
		txtFldVenueDate = new JFormattedTextField(formatter);
		txtFldVenueDate.setColumns(10);
		txtFldVenueDate.setValue(new Date());
		txtFldVenueDate
				.setToolTipText("Enter date in the format yyyy-MM-dd HH:mm.");

		btnNewVenue = new JButton("Add new venue");
		btnNewVenue.addActionListener(listener);
		GroupLayout gl_panelShow = new GroupLayout(panelShow);
		gl_panelShow
				.setHorizontalGroup(gl_panelShow
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_panelShow
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												gl_panelShow
														.createParallelGroup(
																Alignment.LEADING)
														.addComponent(lblVenue)
														.addComponent(lblDate))
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addGroup(
												gl_panelShow
														.createParallelGroup(
																Alignment.LEADING)
														.addGroup(
																gl_panelShow
																		.createSequentialGroup()
																		.addComponent(
																				comboVenues,
																				GroupLayout.PREFERRED_SIZE,
																				194,
																				GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addComponent(
																				btnNewVenue))
														.addComponent(
																txtFldVenueDate,
																GroupLayout.PREFERRED_SIZE,
																105,
																GroupLayout.PREFERRED_SIZE))
										.addGap(67)));
		gl_panelShow
				.setVerticalGroup(gl_panelShow
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_panelShow
										.createSequentialGroup()
										.addGroup(
												gl_panelShow
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(lblVenue)
														.addComponent(
																comboVenues,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(
																btnNewVenue))
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addGroup(
												gl_panelShow
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																txtFldVenueDate,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(lblDate))
										.addContainerGap(
												GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));
		panelShow.setLayout(gl_panelShow);
		panelPrimary.setLayout(gl_panelPrimary);

		final JPanel panelSecondary = new JPanel();
		panelSecondary.setBorder(new TitledBorder(null,
				"Collect data for show", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));

		comboShowsColl = new JComboBox<>();
		comboShowsColl.setMaximumRowCount(20);

		final JPanel panelCollectors = new JPanel();
		panelCollectors.setBorder(new TitledBorder(new LineBorder(new Color(
				184, 207, 229)), "Available collectors", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));

		btnCollectData = new JButton("Collect selected data");
		btnCollectData.addActionListener(listener);

		btnDone = new JButton("Exit");
		btnDone.addActionListener(listener);
		panelCollectors.setLayout(new BoxLayout(panelCollectors,
				BoxLayout.X_AXIS));

		scrollPane_1 = new JScrollPane();
		panelCollectors.add(scrollPane_1);

		tableColl = new CollectorsTable();
		scrollPane_1.setViewportView(tableColl);

		GridBagConstraints gbc_panelSecondary = new GridBagConstraints();
		gbc_panelSecondary.weightx = 0.5;
		gbc_panelSecondary.insets = new Insets(5, 5, 5, 5);
		gbc_panelSecondary.gridx = 1;
		gbc_panelSecondary.gridy = 0;
		gbc_panelSecondary.fill = GridBagConstraints.BOTH;
		panelMain.add(panelSecondary, gbc_panelSecondary);
		GroupLayout gl_panelSecondary = new GroupLayout(panelSecondary);
		gl_panelSecondary
				.setHorizontalGroup(gl_panelSecondary
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_panelSecondary
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												gl_panelSecondary
														.createParallelGroup(
																Alignment.LEADING)
														.addGroup(
																Alignment.TRAILING,
																gl_panelSecondary
																		.createSequentialGroup()
																		.addComponent(
																				btnCollectData)
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addComponent(
																				btnDone,
																				GroupLayout.PREFERRED_SIZE,
																				74,
																				GroupLayout.PREFERRED_SIZE)
																		.addContainerGap())
														.addGroup(
																Alignment.TRAILING,
																gl_panelSecondary
																		.createSequentialGroup()
																		.addComponent(
																				panelCollectors,
																				GroupLayout.DEFAULT_SIZE,
																				456,
																				Short.MAX_VALUE)
																		.addContainerGap())
														.addGroup(
																gl_panelSecondary
																		.createSequentialGroup()
																		.addComponent(
																				comboShowsColl,
																				GroupLayout.PREFERRED_SIZE,
																				262,
																				GroupLayout.PREFERRED_SIZE)
																		.addContainerGap(
																				211,
																				Short.MAX_VALUE)))));
		gl_panelSecondary
				.setVerticalGroup(gl_panelSecondary.createParallelGroup(
						Alignment.LEADING).addGroup(
						gl_panelSecondary
								.createSequentialGroup()
								.addComponent(comboShowsColl,
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(panelCollectors,
										GroupLayout.PREFERRED_SIZE, 204,
										GroupLayout.PREFERRED_SIZE)
								.addGap(7)
								.addGroup(
										gl_panelSecondary
												.createParallelGroup(
														Alignment.BASELINE)
												.addComponent(btnDone)
												.addComponent(btnCollectData))));
		panelSecondary.setLayout(gl_panelSecondary);

		panelDatabase = new JPanel();
		panelDatabase.setBorder(new TitledBorder(null,
				"People currently in the database", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));

		final JScrollPane scrollPane = new JScrollPane();

		// The table of individuals
		table = new IndividualTable();
		scrollPane.setViewportView(table);

		comboShowsFilter = new JComboBox<String>();
		comboShowsFilter.addActionListener(listener);
		comboShowsFilter.setMaximumRowCount(17);

		final JLabel lblFilterByShow = new JLabel("Filter by show");

		btnRefresh = new JButton("Refresh");

		btnDelete = new JButton("Delete individual");
		btnDelete.addActionListener(listener);
		btnRefresh.addActionListener(listener);

		GridBagConstraints gbc_panelDatabase = new GridBagConstraints();
		gbc_panelDatabase.weighty = 1.0;
		gbc_panelDatabase.weightx = 1.0;
		gbc_panelDatabase.insets = new Insets(5, 5, 5, 5);
		gbc_panelDatabase.gridwidth = 2;
		gbc_panelDatabase.fill = GridBagConstraints.BOTH;
		gbc_panelDatabase.gridx = 0;
		gbc_panelDatabase.gridy = 1;
		panelMain.add(panelDatabase, gbc_panelDatabase);
		GroupLayout gl_panelDatabase = new GroupLayout(panelDatabase);
		gl_panelDatabase
				.setHorizontalGroup(gl_panelDatabase
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_panelDatabase
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												gl_panelDatabase
														.createParallelGroup(
																Alignment.LEADING)
														.addComponent(
																scrollPane,
																GroupLayout.DEFAULT_SIZE,
																966,
																Short.MAX_VALUE)
														.addGroup(
																gl_panelDatabase
																		.createSequentialGroup()
																		.addComponent(
																				lblFilterByShow)
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addComponent(
																				comboShowsFilter,
																				GroupLayout.PREFERRED_SIZE,
																				281,
																				GroupLayout.PREFERRED_SIZE)
																		.addGap(35)
																		.addComponent(
																				btnDelete)
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addComponent(
																				btnRefresh)))
										.addContainerGap()));
		gl_panelDatabase
				.setVerticalGroup(gl_panelDatabase
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_panelDatabase
										.createSequentialGroup()
										.addGroup(
												gl_panelDatabase
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																lblFilterByShow)
														.addComponent(
																btnRefresh)
														.addComponent(btnDelete)
														.addComponent(
																comboShowsFilter,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addComponent(scrollPane,
												GroupLayout.DEFAULT_SIZE, 215,
												Short.MAX_VALUE)
										.addContainerGap()));
		panelDatabase.setLayout(gl_panelDatabase);
	}

	Venue getSelectedVenue() {
		// No venue selected
		if (comboVenues.getSelectedIndex() == 0) {
			return null;
		}
		return venues.get(comboVenues.getSelectedIndex() - 1);
	}

	Show getSelectedShow() {
		// All shows selected
		if (comboShowsColl.getSelectedIndex() == 0) {
			return null;
		}
		return shows.get(comboShowsColl.getSelectedIndex() - 1);
	}

	/**
	 * Get the list of all individuals that are already in the database.
	 * 
	 * @return The list of all individuals already in the database.
	 */
	private List<Individual> getDBContent() {
		try {
			return pdbm.getAllIndividuals();
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
	long saveIndividual(final Individual i) {
		final long status;
		try {
			status = pdbm.storeIndividual(i);
		}
		catch (SQLException e1) {
			JOptionPane.showMessageDialog(frame, Strings.GUI_DB_ADD_ERR,
					"Database error", JOptionPane.ERROR_MESSAGE);
			return -1;
		}
		return status;
	}

	/**
	 * Launch the GUI, for testing purposes only.
	 * 
	 * @param args Ignored
	 */
	public static void main(String[] args) {
		// Start the database manager
		final Hashtable<String, AttributeCategories> emptyTable = new Hashtable<>();
		PrimaryDatabaseManager pdbm;
		SecondaryDatabaseManager sdbm;
		try {
			pdbm = new PrimaryDatabaseManager(emptyTable);
			sdbm = new SecondaryDatabaseManager(emptyTable);
		}
		catch (ClassNotFoundException | ConfigFileNotFoundException
				| IOException | SQLException | InvalidAttributeNameException e) {
			System.out.println("MPDataCollector: Exception in main().");
			e.printStackTrace();
			pdbm = null;
			sdbm = null;
		}

		final DataCollectorGUI pdcGUI = new DataCollectorGUI(pdbm, sdbm);
		pdcGUI.run();

	}
}
