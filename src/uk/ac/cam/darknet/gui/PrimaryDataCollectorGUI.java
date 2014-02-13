package uk.ac.cam.darknet.gui;

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
import javax.swing.text.DateFormatter;

import uk.ac.cam.darknet.common.AttributeCategories;
import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.common.Strings;
import uk.ac.cam.darknet.database.PrimaryDatabaseManager;
import uk.ac.cam.darknet.exceptions.ConfigFileNotFoundException;

/**
 * GUI for the primary data collector. Displays the current contents of the
 * database, gives option to load new people from a CSV file or input them
 * manually.
 * 
 * @author Augustin Zidek
 * 
 */
public class PrimaryDataCollectorGUI implements ActionListener {
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
	 * Initialize the GUI.
	 */
	public PrimaryDataCollectorGUI() {
		// Show the GUI
		initializeGUI();
		// Connect to the database
		startDBManager();
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
		frame.setResizable(false);
		frame.setTitle("Primary Data Collector");
		frame.setBounds(100, 100, 487, 624);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);

		final JLabel lblLoadFromCsv = new JLabel("Load from CSV file");
		lblLoadFromCsv.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblLoadFromCsv.setBounds(12, 12, 118, 16);
		panel.add(lblLoadFromCsv);

		final JSeparator separator = new JSeparator();
		separator.setBounds(12, 78, 453, 2);
		panel.add(separator);

		final JLabel lblInputManually = new JLabel("Manual input");
		lblInputManually.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblInputManually.setBounds(12, 92, 118, 16);
		panel.add(lblInputManually);

		btnBrowse = new JButton("Browse");
		btnBrowse.setBounds(250, 39, 79, 23);
		btnBrowse.addActionListener(this);
		panel.add(btnBrowse);

		btnLoadAudience = new JButton("Load audience");
		btnLoadAudience.setBounds(341, 39, 124, 23);
		btnLoadAudience.addActionListener(this);
		panel.add(btnLoadAudience);

		txtFldFirstName = new JTextField();
		txtFldFirstName.setBounds(99, 221, 114, 20);
		panel.add(txtFldFirstName);
		txtFldFirstName.setColumns(10);

		txtFldSecondName = new JTextField();
		txtFldSecondName.setBounds(320, 221, 145, 20);
		panel.add(txtFldSecondName);
		txtFldSecondName.setColumns(10);

		txtFldEmail = new JTextField();
		txtFldEmail.setBounds(99, 249, 114, 20);
		panel.add(txtFldEmail);
		txtFldEmail.setColumns(10);

		final JLabel lblSetThe = new JLabel("1. Set the venue");
		lblSetThe.setBounds(12, 120, 98, 16);
		panel.add(lblSetThe);

		final JLabel lblVenue = new JLabel("Venue name:");
		lblVenue.setBounds(12, 148, 79, 16);
		panel.add(lblVenue);

		txtFldVenueName = new JTextField();
		txtFldVenueName.setBounds(99, 146, 114, 20);
		panel.add(txtFldVenueName);
		txtFldVenueName.setColumns(10);

		final JLabel lblDate = new JLabel("Date:");
		lblDate.setBounds(231, 148, 55, 16);
		panel.add(lblDate);

		// Date formatted text field
		final DateFormatter formatter = new DateFormatter(new SimpleDateFormat(
				"yyyy-MM-dd HH:mm"));
		txtFldVenueDate = new JFormattedTextField(formatter);
		txtFldVenueDate.setColumns(10);
		txtFldVenueDate.setBounds(320, 146, 145, 20);
		txtFldVenueDate.setValue(new Date());
		txtFldVenueDate
				.setToolTipText("Enter date in the format yyyy-MM-dd HH:mm.");
		panel.add(txtFldVenueDate);

		final JLabel lblAddPerson = new JLabel("2. Add person");
		lblAddPerson.setBounds(12, 193, 98, 16);
		panel.add(lblAddPerson);

		final JLabel lblName = new JLabel("First name*:");
		lblName.setBounds(12, 223, 98, 16);
		panel.add(lblName);

		final JLabel lblSecondName = new JLabel("Second name*:");
		lblSecondName.setBounds(231, 223, 98, 16);
		panel.add(lblSecondName);

		final JLabel lblEmail = new JLabel("Email*:");
		lblEmail.setBounds(12, 251, 55, 16);
		panel.add(lblEmail);

		final JLabel lblSeat = new JLabel("Seat:");
		lblSeat.setBounds(231, 251, 55, 16);
		panel.add(lblSeat);

		txtFldSeat = new JTextField();
		txtFldSeat.setBounds(320, 249, 145, 20);
		panel.add(txtFldSeat);
		txtFldSeat.setColumns(10);

		final JLabel lblFieldsMarked = new JLabel(
				"* Fields marked with asterisk are compulsory");
		lblFieldsMarked.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblFieldsMarked.setBounds(12, 279, 226, 16);
		panel.add(lblFieldsMarked);

		btnAddPerson = new JButton("Add person");
		btnAddPerson.setBounds(360, 276, 105, 23);
		btnAddPerson.addActionListener(this);
		panel.add(btnAddPerson);

		txtFldCSVFilePath = new JTextField();
		txtFldCSVFilePath.setBounds(12, 40, 234, 20);
		panel.add(txtFldCSVFilePath);
		txtFldCSVFilePath.setColumns(10);

		// People in the database
		final JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(12, 307, 453, 2);
		panel.add(separator_1);

		final JLabel lblPeopleInDB = new JLabel(
				"People currently in the database");
		lblPeopleInDB.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblPeopleInDB.setBounds(12, 316, 240, 16);
		panel.add(lblPeopleInDB);

		final JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 335, 453, 221);
		panel.add(scrollPane);

		// The table of individuals
		table = new IndividualTable();
		table.setEnabled(false);
		scrollPane.setViewportView(table);

		btnDone = new JButton("Done");
		btnDone.setBounds(367, 558, 98, 26);
		btnDone.addActionListener(this);
		panel.add(btnDone);
	}

	/**
	 * Starts the database manager.
	 */
	private void startDBManager() {
		try {
			// Start the database manager
			final Strings strings = new Strings();
			final Hashtable<String, AttributeCategories> emptyTable = new Hashtable<>();
			dbm = new PrimaryDatabaseManager(emptyTable, strings.getBaseDir()
					+ "/res/dbconfix.txt");
		}
		catch (ClassNotFoundException | ConfigFileNotFoundException
				| IOException | SQLException e) {
			JOptionPane.showMessageDialog(frame, Strings.GUI_DB_CONN_ERR,
					"Database error", JOptionPane.ERROR_MESSAGE);
		}
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
		// TODO
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
			JOptionPane.showMessageDialog(frame, Strings.GUI_DATE_FORMAT);
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
		saveIndividual(newIndividual);

		// Update the table
		table.displayIndividual(newIndividual);

		// Reset the text fields
		txtFldFirstName.setText("");
		txtFldSecondName.setText("");
		txtFldEmail.setText("");
		txtFldSeat.setText("");
	}

	/**
	 * Save the given individual in the db. Show error dialog if error.
	 * 
	 * @param i The individual to be saved.
	 */
	private void saveIndividual(final Individual i) {
		try {
			dbm.storeIndividual(i);
		}
		catch (SQLException e1) {
			JOptionPane.showMessageDialog(frame, Strings.GUI_DB_ADD_ERR,
					"Database error", JOptionPane.ERROR_MESSAGE);
			return;
		}
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
		final PrimaryDataCollectorGUI pdcGUI = new PrimaryDataCollectorGUI();
		pdcGUI.frame.setVisible(true);
	}
}
