package uk.ac.cam.darknet.backend;

import java.awt.BorderLayout;
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
import uk.ac.cam.darknet.common.Strings;
import uk.ac.cam.darknet.database.PrimaryDatabaseManager;
import uk.ac.cam.darknet.exceptions.ConfigFileNotFoundException;
import uk.ac.cam.darknet.gui.IndividualTable;

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
	private JTextField txtFldVenueName;
	private JTextField txtFldSeat;
	private JTextField txtFldCSVFilePath;
	private JFormattedTextField txtFldVenueDate;
	private JButton btnBrowse;
	private JPanel panel;
	private JButton btnLoadAudience;
	private JButton btnAddPerson;
	private JButton btnDone;
	private PrimaryDatabaseManager dbm;

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
		// Get all the individuals that are already in the DB
		final List<Individual> individualsInDB = getDBContent();
		// Display them in the table
		table.displayIndividuals(individualsInDB);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initializeGUI() {
		frame = new JFrame();
		frame.setTitle("Primary Data Collector");
		frame.setBounds(100, 100, 487, 624);
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

		final JLabel lblSetThe = new JLabel("1. Set the venue");

		final JLabel lblVenue = new JLabel("Venue name:");

		txtFldVenueName = new JTextField();
		txtFldVenueName.setColumns(10);

		final JLabel lblDate = new JLabel("Date:");

		// Date formatted text field
		final DateFormatter formatter = new DateFormatter(new SimpleDateFormat(
				"yyyy-MM-dd HH:mm"));
		txtFldVenueDate = new JFormattedTextField(formatter);
		txtFldVenueDate.setColumns(10);
		txtFldVenueDate.setValue(new Date());
		txtFldVenueDate
				.setToolTipText("Enter date in the format yyyy-MM-dd HH:mm.");

		final JLabel lblAddPerson = new JLabel("2. Add person");

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
		table.setEnabled(false);
		scrollPane.setViewportView(table);

		btnDone = new JButton("Done");
		btnDone.addActionListener(this);
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
																.addComponent(
																		lblLoadFromCsv,
																		GroupLayout.PREFERRED_SIZE,
																		118,
																		GroupLayout.PREFERRED_SIZE))
												.addGroup(
														gl_panel.createSequentialGroup()
																.addGap(12)
																.addComponent(
																		txtFldCSVFilePath,
																		GroupLayout.PREFERRED_SIZE,
																		234,
																		GroupLayout.PREFERRED_SIZE)
																.addGap(4)
																.addComponent(
																		btnBrowse,
																		GroupLayout.PREFERRED_SIZE,
																		79,
																		GroupLayout.PREFERRED_SIZE)
																.addGap(12)
																.addComponent(
																		btnLoadAudience,
																		GroupLayout.PREFERRED_SIZE,
																		124,
																		GroupLayout.PREFERRED_SIZE))
												.addGroup(
														gl_panel.createSequentialGroup()
																.addGap(12)
																.addComponent(
																		separator,
																		GroupLayout.PREFERRED_SIZE,
																		453,
																		GroupLayout.PREFERRED_SIZE))
												.addGroup(
														gl_panel.createSequentialGroup()
																.addGap(12)
																.addComponent(
																		lblInputManually,
																		GroupLayout.PREFERRED_SIZE,
																		118,
																		GroupLayout.PREFERRED_SIZE))
												.addGroup(
														gl_panel.createSequentialGroup()
																.addGap(12)
																.addComponent(
																		lblSetThe,
																		GroupLayout.PREFERRED_SIZE,
																		98,
																		GroupLayout.PREFERRED_SIZE))
												.addGroup(
														gl_panel.createSequentialGroup()
																.addGap(12)
																.addComponent(
																		lblVenue,
																		GroupLayout.PREFERRED_SIZE,
																		79,
																		GroupLayout.PREFERRED_SIZE)
																.addGap(8)
																.addComponent(
																		txtFldVenueName,
																		GroupLayout.PREFERRED_SIZE,
																		GroupLayout.DEFAULT_SIZE,
																		GroupLayout.PREFERRED_SIZE)
																.addGap(18)
																.addComponent(
																		lblDate,
																		GroupLayout.PREFERRED_SIZE,
																		55,
																		GroupLayout.PREFERRED_SIZE)
																.addGap(34)
																.addComponent(
																		txtFldVenueDate,
																		GroupLayout.PREFERRED_SIZE,
																		145,
																		GroupLayout.PREFERRED_SIZE))
												.addGroup(
														gl_panel.createSequentialGroup()
																.addGap(12)
																.addComponent(
																		lblAddPerson,
																		GroupLayout.PREFERRED_SIZE,
																		98,
																		GroupLayout.PREFERRED_SIZE))
												.addGroup(
														gl_panel.createSequentialGroup()
																.addGap(12)
																.addGroup(
																		gl_panel.createParallelGroup(
																				Alignment.LEADING)
																				.addGroup(
																						gl_panel.createSequentialGroup()
																								.addGap(87)
																								.addComponent(
																										txtFldFirstName,
																										GroupLayout.PREFERRED_SIZE,
																										GroupLayout.DEFAULT_SIZE,
																										GroupLayout.PREFERRED_SIZE))
																				.addComponent(
																						lblName,
																						GroupLayout.PREFERRED_SIZE,
																						98,
																						GroupLayout.PREFERRED_SIZE))
																.addGap(18)
																.addGroup(
																		gl_panel.createParallelGroup(
																				Alignment.LEADING)
																				.addComponent(
																						lblSecondName,
																						GroupLayout.PREFERRED_SIZE,
																						98,
																						GroupLayout.PREFERRED_SIZE)
																				.addGroup(
																						gl_panel.createSequentialGroup()
																								.addGap(89)
																								.addComponent(
																										txtFldSecondName,
																										GroupLayout.PREFERRED_SIZE,
																										145,
																										GroupLayout.PREFERRED_SIZE))))
												.addGroup(
														gl_panel.createSequentialGroup()
																.addGap(12)
																.addComponent(
																		lblEmail,
																		GroupLayout.PREFERRED_SIZE,
																		55,
																		GroupLayout.PREFERRED_SIZE)
																.addGap(32)
																.addComponent(
																		txtFldEmail,
																		GroupLayout.PREFERRED_SIZE,
																		GroupLayout.DEFAULT_SIZE,
																		GroupLayout.PREFERRED_SIZE)
																.addGap(18)
																.addComponent(
																		lblSeat,
																		GroupLayout.PREFERRED_SIZE,
																		55,
																		GroupLayout.PREFERRED_SIZE)
																.addGap(34)
																.addComponent(
																		txtFldSeat,
																		GroupLayout.PREFERRED_SIZE,
																		145,
																		GroupLayout.PREFERRED_SIZE))
												.addGroup(
														gl_panel.createSequentialGroup()
																.addGap(12)
																.addComponent(
																		lblFieldsMarked,
																		GroupLayout.PREFERRED_SIZE,
																		226,
																		GroupLayout.PREFERRED_SIZE)
																.addGap(122)
																.addComponent(
																		btnAddPerson,
																		GroupLayout.PREFERRED_SIZE,
																		105,
																		GroupLayout.PREFERRED_SIZE))
												.addGroup(
														gl_panel.createSequentialGroup()
																.addGap(12)
																.addComponent(
																		separator_1,
																		GroupLayout.PREFERRED_SIZE,
																		453,
																		GroupLayout.PREFERRED_SIZE))
												.addGroup(
														gl_panel.createSequentialGroup()
																.addGap(12)
																.addComponent(
																		lblPeopleInDB,
																		GroupLayout.PREFERRED_SIZE,
																		240,
																		GroupLayout.PREFERRED_SIZE))
												.addGroup(
														gl_panel.createSequentialGroup()
																.addGap(12)
																.addComponent(
																		scrollPane))
												.addGroup(
														Alignment.TRAILING,
														gl_panel.createSequentialGroup()
																.addGap(367)
																.addComponent(
																		btnDone,
																		GroupLayout.PREFERRED_SIZE,
																		98,
																		GroupLayout.PREFERRED_SIZE)))
								.addContainerGap()));
		gl_panel.setVerticalGroup(gl_panel
				.createParallelGroup(Alignment.LEADING)
				.addGroup(
						gl_panel.createSequentialGroup()
								.addGap(12)
								.addComponent(lblLoadFromCsv,
										GroupLayout.PREFERRED_SIZE, 16,
										GroupLayout.PREFERRED_SIZE)
								.addGap(11)
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.LEADING)
												.addGroup(
														gl_panel.createSequentialGroup()
																.addGap(1)
																.addComponent(
																		txtFldCSVFilePath,
																		GroupLayout.PREFERRED_SIZE,
																		GroupLayout.DEFAULT_SIZE,
																		GroupLayout.PREFERRED_SIZE))
												.addGroup(
														gl_panel.createParallelGroup(
																Alignment.BASELINE)
																.addComponent(
																		btnBrowse,
																		GroupLayout.PREFERRED_SIZE,
																		23,
																		GroupLayout.PREFERRED_SIZE)
																.addComponent(
																		btnLoadAudience,
																		GroupLayout.PREFERRED_SIZE,
																		23,
																		GroupLayout.PREFERRED_SIZE)))
								.addGap(16)
								.addComponent(separator,
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addGap(12)
								.addComponent(lblInputManually,
										GroupLayout.PREFERRED_SIZE, 16,
										GroupLayout.PREFERRED_SIZE)
								.addGap(12)
								.addComponent(lblSetThe)
								.addGap(10)
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.LEADING)
												.addGroup(
														gl_panel.createSequentialGroup()
																.addGap(2)
																.addComponent(
																		lblVenue))
												.addComponent(
														txtFldVenueName,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.PREFERRED_SIZE)
												.addGroup(
														gl_panel.createSequentialGroup()
																.addGap(2)
																.addComponent(
																		lblDate))
												.addComponent(
														txtFldVenueDate,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.PREFERRED_SIZE))
								.addGap(27)
								.addComponent(lblAddPerson)
								.addGap(12)
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.LEADING)
												.addComponent(
														txtFldFirstName,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.PREFERRED_SIZE)
												.addGroup(
														gl_panel.createSequentialGroup()
																.addGap(2)
																.addComponent(
																		lblName))
												.addGroup(
														gl_panel.createSequentialGroup()
																.addGap(2)
																.addComponent(
																		lblSecondName))
												.addComponent(
														txtFldSecondName,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.PREFERRED_SIZE))
								.addGap(8)
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.LEADING)
												.addGroup(
														gl_panel.createSequentialGroup()
																.addGap(2)
																.addComponent(
																		lblEmail))
												.addComponent(
														txtFldEmail,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.PREFERRED_SIZE)
												.addGroup(
														gl_panel.createSequentialGroup()
																.addGap(2)
																.addComponent(
																		lblSeat))
												.addComponent(
														txtFldSeat,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.PREFERRED_SIZE))
								.addGap(7)
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.LEADING)
												.addGroup(
														gl_panel.createSequentialGroup()
																.addGap(3)
																.addComponent(
																		lblFieldsMarked,
																		GroupLayout.PREFERRED_SIZE,
																		16,
																		GroupLayout.PREFERRED_SIZE))
												.addComponent(
														btnAddPerson,
														GroupLayout.PREFERRED_SIZE,
														23,
														GroupLayout.PREFERRED_SIZE))
								.addGap(8)
								.addComponent(separator_1,
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addGap(7)
								.addComponent(lblPeopleInDB,
										GroupLayout.PREFERRED_SIZE, 16,
										GroupLayout.PREFERRED_SIZE)
								.addGap(3)
								.addComponent(scrollPane,
										GroupLayout.DEFAULT_SIZE, 221,
										Short.MAX_VALUE).addGap(2)
								.addComponent(btnDone).addGap(8)));
		panel.setLayout(gl_panel);
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
		// Get the path to the csv file
		final String csvFileURL = txtFldCSVFilePath.getText();
		// Load the list of individuals from the csv file
		final List<Individual> csvIndividuals;
		final SpektrixCSVParser csvParser = new SpektrixCSVParser();
		try {
			csvIndividuals = csvParser.loadfromCSV(csvFileURL);
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

		// Save the individual
		final Individual newIndividual = Individual.getNewIndividual(firstName,
				lastName, email, eventDate, seat, null);
		final long ID = saveIndividual(newIndividual);

		// If there was an error
		if (ID == -1) {
			JOptionPane.showMessageDialog(frame, Strings.GUI_DB_ADD_ERR,
					"Database error", JOptionPane.ERROR_MESSAGE);
		}
		else {
			// Update the table
			try {
				table.displayIndividual(dbm.getById(ID));
			}
			catch (SQLException e) {
				JOptionPane.showMessageDialog(frame, Strings.GUI_DB_CONN_ERR,
						"Database error", JOptionPane.ERROR_MESSAGE);
			}
			// Jump to the added line
			table.scrollRectToVisible(table.getCellRect(
					table.getRowCount() - 1, 0, true));

			// Reset the text fields
			txtFldFirstName.setText("");
			txtFldSecondName.setText("");
			txtFldEmail.setText("");
			txtFldSeat.setText("");
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
	 * Action Listeners for all the buttons.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// The browse button, open File Chooser dialog
		if (e.getSource() == btnBrowse) {
			handleBrowseButton();
		}
		// The load audience button, load the given CSV file
		else if (e.getSource() == btnLoadAudience) {
			handleLoadAudience();
		}
		// The add single person button, add them to the DB
		else if (e.getSource() == btnAddPerson) {
			handleAddPerson();
		}
		// Done button, close the window
		else if (e.getSource() == btnDone) {
			frame.dispose();
		}
	}

	/**
	 * Launch the GUI, for testing purposes only.
	 * 
	 * @param args Ignored
	 */
	public static void main(String[] args) {
		try {
			// Start the database manager
			final Hashtable<String, AttributeCategories> emptyTable = new Hashtable<>();
			final PrimaryDatabaseManager dbm = new PrimaryDatabaseManager(
					emptyTable);
			final ManualPrimaryDataCollector pdcGUI = new ManualPrimaryDataCollector(
					dbm);
			pdcGUI.run();
		}
		catch (ClassNotFoundException | ConfigFileNotFoundException
				| IOException | SQLException e) {
			e.printStackTrace();
		}

	}
}
