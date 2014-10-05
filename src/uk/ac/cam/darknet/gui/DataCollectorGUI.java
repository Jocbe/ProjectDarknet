package uk.ac.cam.darknet.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
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
	JComboBox<String> comboShowsCollec;
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
	JProgressBar progressBar;

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
	 * Initializes the GUI.
	 */
	@Override
	public void run() {
		// Show the GUI
		this.initializeGUI();
		this.frame.setVisible(true);
		// If the connection to the database was successful, show all
		// individuals in the table
		if (this.pdbm == null || this.sdbm == null) {
			JOptionPane.showMessageDialog(this.frame, Strings.GUI_DB_CONN_ERR,
					"Database error", JOptionPane.ERROR_MESSAGE);
		}
		else {
			// Get all the individuals that are already in the DB
			final List<Individual> individualsInDB = this.getDBContent();
			// Populate the comboboxes
			this.updateComboBoxes();
			// Display them in the table
			this.table.displayIndividuals(individualsInDB, this.venues);
		}
		// Populate the table with collectors that have been found in the system
		this.populateCollectorsTable();
	}

	/**
	 * Updates the field shows with data from the database.
	 */
	void updateShowsList() {
		if (this.shows != null) {
			this.shows.clear();
		}
		try {
			this.shows = this.pdbm.getAllShows();
		}
		catch (SQLException e) {
			JOptionPane.showMessageDialog(this.frame, Strings.GUI_DB_VEN_ERR,
					"Database error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Updates the venues list with data from the database.
	 */
	void updateVenuesList() {
		if (this.venues != null) {
			this.venues.clear();
		}
		try {
			this.venues = this.pdbm.getAllVenues();
		}
		catch (SQLException e) {
			JOptionPane.showMessageDialog(this.frame, Strings.GUI_DB_VEN_ERR,
					"Database error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Updates the combobox containing the venues.
	 */
	private void updateVenueCB() {
		this.updateVenuesList();
		// Clear first
		this.comboVenues.removeAllItems();
		// Add blank no venue selected item
		this.comboVenues.addItem(Strings.GUI_NO_VENUE_SEL);
		for (final Venue v : this.venues) {
			this.comboVenues.addItem(v.getName());
		}
	}

	/**
	 * Populates the comboboxes with shows.
	 */
	void updateShowsCBs() {
		// Update the shows list from the DB
		this.updateShowsList();
		// Clear the combo boxes
		this.comboShowsCollec.removeAllItems();
		this.comboShowsFilter.removeAllItems();

		final SimpleDateFormat sdf = new SimpleDateFormat(
				Strings.GUI_DATE_FORMAT);

		// Create default models to enable addition and deletion
		final DefaultComboBoxModel<String> showFilterCBM = new DefaultComboBoxModel<>();
		final DefaultComboBoxModel<String> showCollecCBM = new DefaultComboBoxModel<>();
		// Add don't filter / collect for all item
		showFilterCBM.addElement(Strings.GUI_DONT_FILTER);
		showCollecCBM.addElement(Strings.GUI_ALL_SHOWS);
		// Add all the shows
		for (final Show s : this.shows) {
			final String show = s.getVenue().getName() + " at "
					+ sdf.format(s.getDate());
			showFilterCBM.addElement(show);
			showCollecCBM.addElement(show);
		}

		// Set the model to the comboboxes
		this.comboShowsCollec.setModel(showCollecCBM);
		this.comboShowsFilter.setModel(showFilterCBM);
	}

	/**
	 * Populates the comboboxes with data from the database.
	 */
	private void updateComboBoxes() {
		// Populate venues
		this.updateVenueCB();
		// Populate shows
		this.updateShowsCBs();
	}

	/**
	 * Fill the collectors table with data
	 */
	private void populateCollectorsTable() {
		// Read all classes that are in the backend package
		try {
			final EffectsAndCollectorsLoader loader = new EffectsAndCollectorsLoader();
			this.dataCollectors = loader.loadSecondaryCollectors();
		}
		catch (ClassNotFoundException | IOException a) {
			JOptionPane.showMessageDialog(this.frame, Strings.GUI_NO_COLLECTORS,
					"No collectors found", JOptionPane.ERROR_MESSAGE);
			return;
		}
		// Add the collectors into the table
		for (final Class<?> c : this.dataCollectors) {
			this.tableColl.addCollector(c.getSimpleName());
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initializeGUI() {
		// Date formatted text field
		final DateFormatter formatter = new DateFormatter(new SimpleDateFormat(
				"yyyy-MM-dd HH:mm"));

		this.frame = new JFrame();
		this.frame.setIconImage(Toolkit.getDefaultToolkit().getImage(
				DataCollectorGUI.class
						.getResource("/uk/ac/cam/darknet/gui/icon.png")));
		this.frame.setTitle("Primary Data Collector");
		this.frame.setBounds(100, 100, 1020, 624);
		this.frame.setMinimumSize(new Dimension(1020, 624));
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.panelMain = new JPanel();
		this.frame.getContentPane().add(this.panelMain, BorderLayout.CENTER);

		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 510, 510 };
		gbl_panel.rowHeights = new int[] { 300, 300 };
		gbl_panel.columnWeights = new double[] { 0.0, 0.0 };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0 };
		this.panelMain.setLayout(gbl_panel);

		this.panelPrimary = new JPanel();
		this.panelPrimary.setBorder(new TitledBorder(null,
				"Load the audience members", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panelPrimary = new GridBagConstraints();
		gbc_panelPrimary.weightx = 0.5;
		gbc_panelPrimary.fill = GridBagConstraints.BOTH;
		gbc_panelPrimary.insets = new Insets(5, 5, 5, 5);
		gbc_panelPrimary.gridx = 0;
		gbc_panelPrimary.gridy = 0;
		this.panelMain.add(this.panelPrimary, gbc_panelPrimary);

		this.panelShow = new JPanel();
		this.panelShow.setBorder(new TitledBorder(null, "Set the show",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));

		this.panelCSV = new JPanel();
		this.panelCSV.setBorder(new TitledBorder(null, "Import from CSV file",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));

		this.panel = new JPanel();
		this.panel.setBorder(new TitledBorder(null, "Manually add audience members",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));

		this.label = new JLabel("* Fields marked with asterisk are compulsory");
		this.label.setFont(new Font("Tahoma", Font.PLAIN, 10));
		GroupLayout gl_panelPrimary = new GroupLayout(this.panelPrimary);
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
																				this.panelShow,
																				GroupLayout.PREFERRED_SIZE,
																				0,
																				Short.MAX_VALUE)
																		.addComponent(
																				this.panelCSV,
																				0,
																				0,
																				Short.MAX_VALUE)
																		.addComponent(
																				this.panel,
																				GroupLayout.DEFAULT_SIZE,
																				460,
																				Short.MAX_VALUE))
														.addComponent(
																this.label,
																GroupLayout.PREFERRED_SIZE,
																202,
																GroupLayout.PREFERRED_SIZE))
										.addGap(13)));
		gl_panelPrimary.setVerticalGroup(gl_panelPrimary.createParallelGroup(
				Alignment.LEADING).addGroup(
				gl_panelPrimary
						.createSequentialGroup()
						.addComponent(this.panelShow, GroupLayout.PREFERRED_SIZE,
								86, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(this.panelCSV, GroupLayout.PREFERRED_SIZE, 54,
								GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(this.panel, GroupLayout.PREFERRED_SIZE, 91,
								GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(this.label, GroupLayout.PREFERRED_SIZE, 13,
								GroupLayout.PREFERRED_SIZE)
						.addContainerGap(GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)));
		GridBagLayout gbl_panel2 = new GridBagLayout();
		gbl_panel2.columnWidths = new int[] { 50, 160, 40, 60, 80 };
		gbl_panel2.rowHeights = new int[] { 20, 26, 0 };
		gbl_panel2.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0 };
		gbl_panel2.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		this.panel.setLayout(gbl_panel2);

		final JLabel lblName = new JLabel("Name*:");
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.anchor = GridBagConstraints.WEST;
		gbc_lblName.insets = new Insets(0, 0, 5, 5);
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 0;
		this.panel.add(lblName, gbc_lblName);

		this.txtFldFirstName = new JTextField();
		GridBagConstraints gbc_txtFldFirstName = new GridBagConstraints();
		gbc_txtFldFirstName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtFldFirstName.anchor = GridBagConstraints.NORTH;
		gbc_txtFldFirstName.insets = new Insets(0, 0, 5, 5);
		gbc_txtFldFirstName.gridx = 1;
		gbc_txtFldFirstName.gridy = 0;
		this.panel.add(this.txtFldFirstName, gbc_txtFldFirstName);
		this.txtFldFirstName.setColumns(10);

		final JLabel lblSecondName = new JLabel("Surname*:");
		GridBagConstraints gbc_lblSecondName = new GridBagConstraints();
		gbc_lblSecondName.anchor = GridBagConstraints.EAST;
		gbc_lblSecondName.insets = new Insets(0, 0, 5, 5);
		gbc_lblSecondName.gridx = 2;
		gbc_lblSecondName.gridy = 0;
		this.panel.add(lblSecondName, gbc_lblSecondName);

		this.txtFldSecondName = new JTextField();
		GridBagConstraints gbc_txtFldSecondName = new GridBagConstraints();
		gbc_txtFldSecondName.gridwidth = 2;
		gbc_txtFldSecondName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtFldSecondName.anchor = GridBagConstraints.NORTH;
		gbc_txtFldSecondName.gridx = 3;
		gbc_txtFldSecondName.gridy = 0;
		this.panel.add(this.txtFldSecondName, gbc_txtFldSecondName);
		this.txtFldSecondName.setColumns(10);

		final JLabel lblEmail = new JLabel("Email*:");
		GridBagConstraints gbc_lblEmail = new GridBagConstraints();
		gbc_lblEmail.anchor = GridBagConstraints.WEST;
		gbc_lblEmail.insets = new Insets(0, 0, 0, 5);
		gbc_lblEmail.gridx = 0;
		gbc_lblEmail.gridy = 1;
		this.panel.add(lblEmail, gbc_lblEmail);

		this.txtFldEmail = new JTextField();
		GridBagConstraints gbc_txtFldEmail = new GridBagConstraints();
		gbc_txtFldEmail.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtFldEmail.insets = new Insets(0, 0, 0, 5);
		gbc_txtFldEmail.gridx = 1;
		gbc_txtFldEmail.gridy = 1;
		this.panel.add(this.txtFldEmail, gbc_txtFldEmail);
		this.txtFldEmail.setColumns(10);

		final JLabel lblSeat = new JLabel("Seat:");
		GridBagConstraints gbc_lblSeat = new GridBagConstraints();
		gbc_lblSeat.anchor = GridBagConstraints.WEST;
		gbc_lblSeat.insets = new Insets(0, 0, 0, 5);
		gbc_lblSeat.gridx = 2;
		gbc_lblSeat.gridy = 1;
		this.panel.add(lblSeat, gbc_lblSeat);

		this.txtFldSeat = new JTextField();
		GridBagConstraints gbc_txtFldSeat = new GridBagConstraints();
		gbc_txtFldSeat.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtFldSeat.insets = new Insets(0, 0, 0, 5);
		gbc_txtFldSeat.gridx = 3;
		gbc_txtFldSeat.gridy = 1;
		this.panel.add(this.txtFldSeat, gbc_txtFldSeat);
		this.txtFldSeat.setColumns(10);

		this.btnAddPerson = new JButton("Add person");
		GridBagConstraints gbc_btnAddPerson = new GridBagConstraints();
		gbc_btnAddPerson.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnAddPerson.gridx = 4;
		gbc_btnAddPerson.gridy = 1;
		this.panel.add(this.btnAddPerson, gbc_btnAddPerson);
		this.btnAddPerson.addActionListener(this.listener);

		this.txtFldCSVFilePath = new JTextField();
		this.txtFldCSVFilePath.setColumns(10);

		this.btnBrowse = new JButton("Browse");
		this.btnBrowse.addActionListener(this.listener);

		this.btnLoadAudience = new JButton("Load audience");
		GroupLayout gl_panelCSV = new GroupLayout(this.panelCSV);
		gl_panelCSV.setHorizontalGroup(gl_panelCSV.createParallelGroup(
				Alignment.LEADING).addGroup(
				Alignment.TRAILING,
				gl_panelCSV
						.createSequentialGroup()
						.addContainerGap()
						.addComponent(this.txtFldCSVFilePath,
								GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(this.btnBrowse)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(this.btnLoadAudience)));
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
																this.btnLoadAudience)
														.addComponent(this.btnBrowse)
														.addComponent(
																this.txtFldCSVFilePath,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addContainerGap(
												GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));
		this.panelCSV.setLayout(gl_panelCSV);
		this.btnLoadAudience.addActionListener(this.listener);

		final JLabel lblVenue = new JLabel("Venue*:");

		this.comboVenues = new JComboBox<>();

		final JLabel lblDate = new JLabel("Show date*:");
		this.txtFldVenueDate = new JFormattedTextField(formatter);
		this.txtFldVenueDate.setColumns(10);
		this.txtFldVenueDate.setValue(new Date());
		this.txtFldVenueDate
				.setToolTipText("Enter date in the format yyyy-MM-dd HH:mm.");

		this.btnNewVenue = new JButton("Add new venue");
		this.btnNewVenue.addActionListener(this.listener);
		GroupLayout gl_panelShow = new GroupLayout(this.panelShow);
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
																				this.comboVenues,
																				GroupLayout.PREFERRED_SIZE,
																				194,
																				GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addComponent(
																				this.btnNewVenue))
														.addComponent(
																this.txtFldVenueDate,
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
																this.comboVenues,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(
																this.btnNewVenue))
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addGroup(
												gl_panelShow
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																this.txtFldVenueDate,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(lblDate))
										.addContainerGap(
												GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));
		this.panelShow.setLayout(gl_panelShow);
		this.panelPrimary.setLayout(gl_panelPrimary);

		final JPanel panelSecondary = new JPanel();
		panelSecondary.setBorder(new TitledBorder(null,
				"Collect data for show", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));

		this.comboShowsCollec = new JComboBox<>();
		this.comboShowsCollec.setMaximumRowCount(20);
		this.comboShowsCollec.addActionListener(this.listener);

		final JPanel panelCollectors = new JPanel();
		panelCollectors.setBorder(new TitledBorder(new LineBorder(new Color(
				184, 207, 229)), "Available collectors", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));

		this.btnCollectData = new JButton("Collect selected data");
		this.btnCollectData.addActionListener(this.listener);

		this.btnDone = new JButton("Exit");
		this.btnDone.addActionListener(this.listener);
		panelCollectors.setLayout(new BoxLayout(panelCollectors,
				BoxLayout.X_AXIS));

		this.scrollPane_1 = new JScrollPane();
		panelCollectors.add(this.scrollPane_1);

		this.tableColl = new CollectorsTable();
		this.scrollPane_1.setViewportView(this.tableColl);

		GridBagConstraints gbc_panelSecondary = new GridBagConstraints();
		gbc_panelSecondary.weightx = 0.5;
		gbc_panelSecondary.insets = new Insets(5, 5, 5, 5);
		gbc_panelSecondary.gridx = 1;
		gbc_panelSecondary.gridy = 0;
		gbc_panelSecondary.fill = GridBagConstraints.BOTH;
		this.panelMain.add(panelSecondary, gbc_panelSecondary);

		this.progressBar = new JProgressBar();
		this.progressBar.setStringPainted(true);
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
																Alignment.TRAILING)
														.addGroup(
																gl_panelSecondary
																		.createSequentialGroup()
																		.addComponent(
																				this.progressBar,
																				GroupLayout.DEFAULT_SIZE,
																				222,
																				Short.MAX_VALUE)
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addComponent(
																				this.btnCollectData)
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addComponent(
																				this.btnDone,
																				GroupLayout.PREFERRED_SIZE,
																				74,
																				GroupLayout.PREFERRED_SIZE))
														.addComponent(
																panelCollectors,
																GroupLayout.DEFAULT_SIZE,
																461,
																Short.MAX_VALUE)
														.addComponent(
																this.comboShowsCollec,
																Alignment.LEADING,
																GroupLayout.PREFERRED_SIZE,
																262,
																GroupLayout.PREFERRED_SIZE))
										.addContainerGap()));
		gl_panelSecondary
				.setVerticalGroup(gl_panelSecondary
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_panelSecondary
										.createSequentialGroup()
										.addComponent(this.comboShowsCollec,
												GroupLayout.PREFERRED_SIZE,
												GroupLayout.DEFAULT_SIZE,
												GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addComponent(panelCollectors,
												GroupLayout.PREFERRED_SIZE,
												204, GroupLayout.PREFERRED_SIZE)
										.addGroup(
												gl_panelSecondary
														.createParallelGroup(
																Alignment.LEADING)
														.addGroup(
																gl_panelSecondary
																		.createSequentialGroup()
																		.addGap(7)
																		.addGroup(
																				gl_panelSecondary
																						.createParallelGroup(
																								Alignment.BASELINE)
																						.addComponent(
																								this.btnDone)
																						.addComponent(
																								this.btnCollectData)))
														.addGroup(
																gl_panelSecondary
																		.createSequentialGroup()
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addComponent(
																				this.progressBar,
																				GroupLayout.DEFAULT_SIZE,
																				GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE)))
										.addGap(6)));
		panelSecondary.setLayout(gl_panelSecondary);

		this.panelDatabase = new JPanel();
		this.panelDatabase.setBorder(new TitledBorder(null,
				"People currently in the database", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));

		final JScrollPane scrollPane = new JScrollPane();

		// The table of individuals
		this.table = new IndividualTable();
		scrollPane.setViewportView(this.table);

		this.comboShowsFilter = new JComboBox<String>();
		this.comboShowsFilter.setModel(new DefaultComboBoxModel<String>());
		this.comboShowsFilter.addActionListener(this.listener);
		this.comboShowsFilter.setMaximumRowCount(17);

		final JLabel lblFilterByShow = new JLabel("Filter by show");

		this.btnRefresh = new JButton("Refresh");

		this.btnDelete = new JButton("Delete individual");
		this.btnDelete.addActionListener(this.listener);
		this.btnRefresh.addActionListener(this.listener);

		GridBagConstraints gbc_panelDatabase = new GridBagConstraints();
		gbc_panelDatabase.weighty = 1.0;
		gbc_panelDatabase.weightx = 1.0;
		gbc_panelDatabase.insets = new Insets(5, 5, 5, 5);
		gbc_panelDatabase.gridwidth = 2;
		gbc_panelDatabase.fill = GridBagConstraints.BOTH;
		gbc_panelDatabase.gridx = 0;
		gbc_panelDatabase.gridy = 1;
		this.panelMain.add(this.panelDatabase, gbc_panelDatabase);
		GroupLayout gl_panelDatabase = new GroupLayout(this.panelDatabase);
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
																				this.comboShowsFilter,
																				GroupLayout.PREFERRED_SIZE,
																				281,
																				GroupLayout.PREFERRED_SIZE)
																		.addGap(35)
																		.addComponent(
																				this.btnDelete)
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addComponent(
																				this.btnRefresh)))
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
																this.btnRefresh)
														.addComponent(this.btnDelete)
														.addComponent(
																this.comboShowsFilter,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addComponent(scrollPane,
												GroupLayout.DEFAULT_SIZE, 215,
												Short.MAX_VALUE)
										.addContainerGap()));
		this.panelDatabase.setLayout(gl_panelDatabase);
	}

	/**
	 * @return The venue that is currently selected in the combobox. Returns
	 *         null if the "no venue" item is selected.
	 */
	Venue getSelectedVenue() {
		// No venue selected
		if (this.comboVenues.getSelectedIndex() == 0) {
			return null;
		}
		return this.venues.get(this.comboVenues.getSelectedIndex() - 1);
	}

	/**
	 * @return The show that is currently selected in the shows filter combobox.
	 *         Returns null if the "all shows" item is selected.
	 */
	Show getSelectedShow() {
		// All shows selected
		if (this.comboShowsCollec.getSelectedIndex() == 0) {
			return null;
		}
		return this.shows.get(this.comboShowsCollec.getSelectedIndex() - 1);
	}

	/**
	 * Get the list of all individuals that are already in the database.
	 * 
	 * @return The list of all individuals already in the database.
	 */
	private List<Individual> getDBContent() {
		try {
			return this.pdbm.getAllIndividuals();
		}
		catch (SQLException e) {
			JOptionPane.showMessageDialog(this.frame, Strings.GUI_DB_READ_ERR,
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
			status = this.pdbm.storeIndividual(i);
		}
		catch (SQLException e1) {
			JOptionPane.showMessageDialog(this.frame, Strings.GUI_DB_ADD_ERR,
					"Database error", JOptionPane.ERROR_MESSAGE);
			return -1;
		}
		return status;
	}

	/**
	 * This is the main entry of the package DataCollector. It launches the Data
	 * Collector. This involves: <br>
	 * 1. Getting all the collectors in the system using reflection.<br>
	 * 2. Starting the primary and secondary database managers and passing the
	 * obtained collector attributes to them<br>
	 * 3. Starting the GUI
	 * 
	 * @param args The arguments are ignored
	 */
	public static void main(final String[] args) {
		// Get the effects & collectors loader that uses reflection to load them
		final EffectsAndCollectorsLoader collLoader = new EffectsAndCollectorsLoader();

		// Get the attributes the collector will need in the database
		Hashtable<String, AttributeCategories> attributes;
		try {
			attributes = collLoader.getDatabaseCollectorAttributes();
		}
		catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| SecurityException | IOException e1) {
			JOptionPane.showMessageDialog(null, Strings.GUI_COLL_ERROR,
					"Collectors error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Get the database managers using the attributes
		PrimaryDatabaseManager pdbm;
		SecondaryDatabaseManager sdbm;
		try {
			pdbm = new PrimaryDatabaseManager(attributes);
			sdbm = new SecondaryDatabaseManager(attributes);
		}
		catch (ClassNotFoundException | ConfigFileNotFoundException
				| IOException | SQLException | InvalidAttributeNameException e) {
			JOptionPane.showMessageDialog(null, Strings.GUI_DB_CONN_ERR,
					"Database error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Start the GUI
		final DataCollectorGUI pdcGUI = new DataCollectorGUI(pdbm, sdbm);
		pdcGUI.run();

	}
}
