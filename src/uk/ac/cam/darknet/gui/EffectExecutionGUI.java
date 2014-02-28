package uk.ac.cam.darknet.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import uk.ac.cam.darknet.backend.SecondaryDataCollector;
import uk.ac.cam.darknet.common.AttributeCategories;
import uk.ac.cam.darknet.common.EffectsAndCollectorsLoader;
import uk.ac.cam.darknet.common.Show;
import uk.ac.cam.darknet.common.Strings;
import uk.ac.cam.darknet.database.DatabaseManager;
import uk.ac.cam.darknet.database.PrimaryDatabaseManager;
import uk.ac.cam.darknet.database.SecondaryDatabaseManager;
import uk.ac.cam.darknet.exceptions.ConfigFileNotFoundException;
import uk.ac.cam.darknet.exceptions.InvalidAttributeNameException;
import uk.ac.cam.darknet.frontend.Effect;
import javax.swing.ImageIcon;
import java.awt.Toolkit;

/**
 * The GUI for effect execution. It allows user to select a show and displays
 * available effects in the system. The user can select effects thet want to use
 * and then execute them.
 * 
 * @author Augustin Zidek
 * 
 */
public class EffectExecutionGUI {

	private JFrame frmEffectsDj;
	private EffectsTable table;
	private JButton btnRunEffects;
	private JScrollPane scrollPane;
	private JComboBox<String> comboShows;

	private List<Class<?>> effectClasses;
	private final PrimaryDatabaseManager pdbm;
	private final SecondaryDatabaseManager sdbm;
	private List<Show> shows;

	/**
	 * Starts the effect execution GUI.
	 * 
	 * @param pdbm The primary database manager.
	 * @param sdbm The secondary database manager.
	 */
	public EffectExecutionGUI(final PrimaryDatabaseManager pdbm,
			final SecondaryDatabaseManager sdbm) {
		this.pdbm = pdbm;
		this.sdbm = sdbm;
		initializeGUI();
		populateEffectsTable();
		populateShows();
		frmEffectsDj.setVisible(true);
	}

	/**
	 * Updates the field shows with data from the database.
	 */
	void updateShowsList() {
		try {
			shows = pdbm.getAllShows();
		}
		catch (SQLException e) {
			JOptionPane.showMessageDialog(frmEffectsDj, Strings.GUI_DB_VEN_ERR,
					"Database error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Populate the shows combo box.
	 */
	private void populateShows() {
		updateShowsList();
		comboShows.removeAllItems();

		final SimpleDateFormat sdf = new SimpleDateFormat(
				Strings.GUI_DATE_FORMAT);
		// Add don't filter item
		comboShows.addItem(Strings.GUI_ALL_SHOWS);
		for (final Show s : shows) {
			final String show = s.getVenue().getName() + " at "
					+ sdf.format(s.getDate());
			comboShows.addItem(show);
		}
	}

	/**
	 * Fill the collectors table with data
	 */
	private void populateEffectsTable() {
		// Read all classes that are in the backend package
		try {
			final EffectsAndCollectorsLoader loader = new EffectsAndCollectorsLoader();
			effectClasses = loader.loadEffects();
		}
		catch (ClassNotFoundException | IOException a) {
			JOptionPane.showMessageDialog(frmEffectsDj,
					Strings.GUI_NO_COLLECTORS, "No collectors found",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		// Add the collectors into the table
		for (final Class<?> c : effectClasses) {
			table.addEffect(c.getSimpleName());
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initializeGUI() {
		frmEffectsDj = new JFrame();
		frmEffectsDj.setIconImage(Toolkit.getDefaultToolkit().getImage(
				EffectExecutionGUI.class
						.getResource("/uk/ac/cam/darknet/gui/icon.png")));
		frmEffectsDj.setMinimumSize(new Dimension(486, 524));
		frmEffectsDj.setTitle("Effects DJ");
		frmEffectsDj.setBounds(100, 100, 486, 524);
		frmEffectsDj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final JPanel panelMain = new JPanel();
		frmEffectsDj.getContentPane().add(panelMain, BorderLayout.CENTER);
		GridBagLayout gbl_panelMain = new GridBagLayout();
		gbl_panelMain.columnWidths = new int[] { 636 };
		gbl_panelMain.rowHeights = new int[] { 57, 400, 26 };
		gbl_panelMain.columnWeights = new double[] { 0.0 };
		gbl_panelMain.rowWeights = new double[] { 0.0, 0.0, 0.0 };
		panelMain.setLayout(gbl_panelMain);

		final JPanel panelSelShow = new JPanel();
		panelSelShow.setPreferredSize(new Dimension(636, 57));
		panelSelShow.setMinimumSize(new Dimension(636, 57));
		panelSelShow.setAlignmentY(Component.TOP_ALIGNMENT);
		panelSelShow.setBorder(new TitledBorder(null, "Select show",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelSelShow.setLayout(null);

		comboShows = new JComboBox<>();
		comboShows.setBounds(12, 19, 279, 25);
		panelSelShow.add(comboShows);
		GridBagConstraints gbc_panelSelShow = new GridBagConstraints();
		gbc_panelSelShow.insets = new Insets(0, 5, 0, 5);
		gbc_panelSelShow.weightx = 1.0;
		gbc_panelSelShow.fill = GridBagConstraints.BOTH;
		gbc_panelSelShow.gridx = 0;
		gbc_panelSelShow.gridy = 0;
		panelMain.add(panelSelShow, gbc_panelSelShow);

		final JPanel panelEffects = new JPanel();
		panelEffects.setAlignmentY(Component.TOP_ALIGNMENT);
		panelEffects.setBorder(new TitledBorder(null,
				"Select from the list of available effects",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));

		scrollPane = new JScrollPane();

		table = new EffectsTable();
		scrollPane.setViewportView(table);
		panelEffects.setLayout(new BoxLayout(panelEffects, BoxLayout.X_AXIS));
		panelEffects.add(scrollPane);
		GridBagConstraints gbc_panelEffects = new GridBagConstraints();
		gbc_panelEffects.weightx = 1.0;
		gbc_panelEffects.fill = GridBagConstraints.BOTH;
		gbc_panelEffects.insets = new Insets(0, 5, 0, 5);
		gbc_panelEffects.gridx = 0;
		gbc_panelEffects.gridy = 1;
		panelMain.add(panelEffects, gbc_panelEffects);

		btnRunEffects = new JButton("Run effects");
		GridBagConstraints gbc_btnRunEffects = new GridBagConstraints();
		gbc_btnRunEffects.fill = GridBagConstraints.VERTICAL;
		gbc_btnRunEffects.insets = new Insets(0, 5, 0, 5);
		gbc_btnRunEffects.anchor = GridBagConstraints.EAST;
		gbc_btnRunEffects.gridx = 0;
		gbc_btnRunEffects.gridy = 2;
		panelMain.add(btnRunEffects, gbc_btnRunEffects);

		btnRunEffects.addActionListener(new RunEffectsListener());
	}

	private Show getSelectedShow() {
		// All shows selected
		if (comboShows.getSelectedIndex() == 0) {
			return null;
		}
		return shows.get(comboShows.getSelectedIndex() - 1);
	}

	/**
	 * Launch the application, testing only
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

		EffectExecutionGUI window = new EffectExecutionGUI(pdbm, sdbm);

	}

	class RunEffectsListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			final List<Integer> checkedRows = table.getCheckedRows();

			if (checkedRows.size() == 0) {
				JOptionPane.showMessageDialog(frmEffectsDj,
						Strings.GUI_SELECT_EFFECTS, "No effects selected",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			// Get the selected show. If null, call collect on all data
			final Show show = getSelectedShow();
			// Select only checked effects
			final List<Class<?>> selectedEffects = new ArrayList<>();
			for (final int row : checkedRows) {
				selectedEffects.add(effectClasses.get(row));
			}

			// Go through all collectors and invoke their run() methods
			for (final Class<?> effectClass : selectedEffects) {
				try {
					// Get the effect
					final Effect effect = (Effect) effectClass.getConstructor(
							DatabaseManager.class).newInstance(sdbm);

					// Get the argument descriptions
					final List<String> argDescriptions = effect
							.getSetupArgDescriptions();
					final String[] arguments = new String[argDescriptions
							.size()];

					// Ask user for all parameters and use the anwers
					int cnt = 0;
					for (final String argDes : argDescriptions) {
						final String arg = JOptionPane.showInputDialog(
								frmEffectsDj, "Please type in the " + argDes);
						arguments[cnt++] = arg;
					}

					// Set up the effect
					effect.setup(arguments);

					// Handle all shows
					if (show == null) {
						for (final Show s : pdbm.getAllShows()) {
							effect.execute(s);
						}
					}
					// Handle single show only
					else {
						effect.execute(show);
					}
				}
				catch (InstantiationException | IllegalAccessException
						| SQLException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException
						| SecurityException exn) {
					JOptionPane.showMessageDialog(frmEffectsDj,
							Strings.GUI_EFFECTS_ERR, "Error loading effects",
							JOptionPane.ERROR_MESSAGE);
				}

			}
		}

	}

}
