package uk.ac.cam.darknet.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.hsqldb.DatabaseManager;

import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.database.PrimaryDatabaseManager;

/**
 * GUI for the primary data collector. Displays the current contents of the
 * database, gives option to load new people from a CSV file or input them
 * manually.
 * 
 * @author Augustin Zidek
 * 
 */
public class PrimaryDataCollectorGUI implements ActionListener {

	private JFrame frmPrimaryDataCollector;
	private JTextField txtFldName;
	private JTextField txtFldSecondName;
	private JTextField txtFldEmail;
	private JTable table;
	private JTextField txtFldVenueName;
	private JTextField txtFldSeat;
	private JTextField txtFldCSVFilePath;
	private JButton btnBrowse;
	private JPanel panel;
	private JButton btnLoadAudience;
	private JButton btnAddPerson;

	/**
	 * Initialize the GUI.
	 */
	public PrimaryDataCollectorGUI() {
		initialize();
		final List<Individual> individualsInDB = getDBContent();
		displayDBContent(individualsInDB);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmPrimaryDataCollector = new JFrame();
		frmPrimaryDataCollector.setResizable(false);
		frmPrimaryDataCollector.setTitle("Primary Data Collector");
		frmPrimaryDataCollector.setBounds(100, 100, 487, 600);
		frmPrimaryDataCollector.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		panel = new JPanel();
		frmPrimaryDataCollector.getContentPane()
				.add(panel, BorderLayout.CENTER);
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
		panel.add(btnLoadAudience);

		txtFldName = new JTextField();
		txtFldName.setBounds(99, 221, 114, 20);
		panel.add(txtFldName);
		txtFldName.setColumns(10);

		txtFldSecondName = new JTextField();
		txtFldSecondName.setBounds(320, 221, 145, 20);
		panel.add(txtFldSecondName);
		txtFldSecondName.setColumns(10);

		txtFldEmail = new JTextField();
		txtFldEmail.setBounds(99, 249, 114, 20);
		panel.add(txtFldEmail);
		txtFldEmail.setColumns(10);

		final JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(12, 307, 453, 2);
		panel.add(separator_1);

		final JLabel lblPeopleCurrentlyIn = new JLabel(
				"People currently in the database");
		lblPeopleCurrentlyIn.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblPeopleCurrentlyIn.setBounds(12, 316, 240, 16);
		panel.add(lblPeopleCurrentlyIn);

		table = new JTable();
		table.setBounds(12, 335, 453, 221);
		panel.add(table);

		final JLabel lblSetThe = new JLabel("1. Set the venue");
		lblSetThe.setBounds(12, 120, 98, 16);
		panel.add(lblSetThe);

		final JLabel lblVenue = new JLabel("Name:");
		lblVenue.setBounds(12, 148, 55, 16);
		panel.add(lblVenue);

		txtFldVenueName = new JTextField();
		txtFldVenueName.setBounds(99, 146, 114, 20);
		panel.add(txtFldVenueName);
		txtFldVenueName.setColumns(10);

		final JLabel lblDate = new JLabel("Date:");
		lblDate.setBounds(231, 148, 55, 16);
		panel.add(lblDate);

		final JFormattedTextField txtFldVenueDate = new JFormattedTextField();
		txtFldVenueDate.setColumns(10);
		txtFldVenueDate.setBounds(320, 146, 145, 20);
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
		panel.add(btnAddPerson);

		txtFldCSVFilePath = new JTextField();
		txtFldCSVFilePath.setBounds(12, 40, 234, 20);
		panel.add(txtFldCSVFilePath);
		txtFldCSVFilePath.setColumns(10);
	}

	/**
	 * Get the list of all individuals that are already in the database.
	 * 
	 * @return The list of all individuals already in the database.
	 */
	private List<Individual> getDBContent() {
		// TODO: Get the contents of the Database
		return null;
	}

	/**
	 * Display all given individuals in a table in the GUI. Only primary
	 * information will be displayed.
	 * 
	 * @param individuals The list of individuals to be displayed.
	 */
	private void displayDBContent(final List<Individual> individuals) {
		// TODO: Display the list of all individuals with their primary data in
		// the table
	}

	/**
	 * Action Listener for all the buttons.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// The browse button, open File Chooser dialog
		if (e.getSource() == btnBrowse) {
			final JFileChooser fc = new JFileChooser();

			// Return value from the file chooser (tells if a file was selected)
			int returnVal = fc.showOpenDialog(panel);

			// If file selected, set the text field'c contents to the path of it
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				txtFldCSVFilePath.setText(fc.getSelectedFile().toString());
			}
		}

		// The load audience button, load the given CSV file
		if (e.getSource() == btnLoadAudience) {
			// TODO: Call appropriate method in the PrimaryDataCollector
		}

		// The add single person button, add them to the DB
		if (e.getSource() == btnAddPerson) {
			// TODO: Call appropriate method in the DatabaseManager
		}

	}

	/**
	 * Launch the GUI, for testing purposes only.
	 * 
	 * @param args Ignored
	 */
	public static void main(String[] args) {
		final PrimaryDataCollectorGUI pdcGUI = new PrimaryDataCollectorGUI();
		pdcGUI.frmPrimaryDataCollector.setVisible(true);
	}
}
