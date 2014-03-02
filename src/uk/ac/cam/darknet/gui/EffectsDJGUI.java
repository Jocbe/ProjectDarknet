package uk.ac.cam.darknet.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
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
import javax.swing.border.TitledBorder;

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
import javax.swing.JProgressBar;

/**
 * The GUI for effect execution. It allows user to select a show and displays
 * available effects in the system. The user can select effects that want to use
 * and then execute them.
 * 
 * @author Augustin Zidek
 * 
 */
public class EffectsDJGUI {
	private JFrame frmEffectsDj;
	private EffectsTable table;
	private JButton btnRunEffects;
	private JScrollPane scrollPane;
	private JComboBox<String> comboShows;

	private List<Class<?>> effectClasses;
	private final PrimaryDatabaseManager pdbm;
	private final SecondaryDatabaseManager sdbm;
	private List<Show> shows;
	JProgressBar progressBar;

	/**
	 * Initializes a new Effects DJ.
	 * 
	 * @param pdbm The primary database manager.
	 * @param sdbm The secondary database manager.
	 */
	public EffectsDJGUI(final PrimaryDatabaseManager pdbm,
			final SecondaryDatabaseManager sdbm) {
		this.pdbm = pdbm;
		this.sdbm = sdbm;
	}

	/**
	 * Starts the GUI, populates it with content and sets the frame visible.
	 */
	public void run() {
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
				EffectsDJGUI.class
						.getResource("/uk/ac/cam/darknet/gui/icon.png")));
		frmEffectsDj.setMinimumSize(new Dimension(486, 524));
		frmEffectsDj.setTitle("Effects DJ");
		frmEffectsDj.setBounds(100, 100, 486, 524);
		frmEffectsDj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final JPanel panelMain = new JPanel();
		frmEffectsDj.getContentPane().add(panelMain, BorderLayout.CENTER);
		GridBagLayout gbl_panelMain = new GridBagLayout();
		gbl_panelMain.columnWidths = new int[] { 243, 243 };
		gbl_panelMain.rowHeights = new int[] { 57, 400, 26 };
		gbl_panelMain.columnWeights = new double[] { 0.0, 0.0, 0.0 };
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
		gbc_panelSelShow.gridwidth = 3;
		gbc_panelSelShow.insets = new Insets(0, 5, 5, 0);
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
		gbc_panelEffects.gridwidth = 3;
		gbc_panelEffects.weightx = 1.0;
		gbc_panelEffects.fill = GridBagConstraints.BOTH;
		gbc_panelEffects.insets = new Insets(0, 5, 5, 0);
		gbc_panelEffects.gridx = 0;
		gbc_panelEffects.gridy = 1;
		panelMain.add(panelEffects, gbc_panelEffects);

		progressBar = new JProgressBar();
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setToolTipText("Effect execution progress");
		GridBagConstraints gbc_progressBar = new GridBagConstraints();
		gbc_progressBar.fill = GridBagConstraints.BOTH;
		gbc_progressBar.insets = new Insets(0, 10, 5, 5);
		gbc_progressBar.gridx = 0;
		gbc_progressBar.gridy = 2;
		panelMain.add(progressBar, gbc_progressBar);

		btnRunEffects = new JButton("Run effects");
		GridBagConstraints gbc_btnRunEffects = new GridBagConstraints();
		gbc_btnRunEffects.fill = GridBagConstraints.VERTICAL;
		gbc_btnRunEffects.insets = new Insets(0, 5, 0, 10);
		gbc_btnRunEffects.anchor = GridBagConstraints.EAST;
		gbc_btnRunEffects.gridx = 1;
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
	 * Launches the Effects DJ. This involves: <br>
	 * 1. Getting all the collectors in the system using reflection.<br>
	 * 2. Starting the primary and secondary database managers and passing the
	 * obtained collector attributes to them<br>
	 * 3. Starting the GUI
	 * 
	 * @param args The arguments are ignored.
	 */
	public static void main(String[] args) {
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

		// Get the database managers
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

		// Run the Effects DJ
		EffectsDJGUI effectsDJ = new EffectsDJGUI(pdbm, sdbm);
		effectsDJ.run();
	}

	class RunEffectsListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Get rows that have selected checkboxes
			final List<Integer> checkedRows = table.getCheckedRows();

			// Nothing checked
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

			// Get the amount of effects that are going to be executed
			final int effectCount = selectedEffects.size();
			progressBar.setMaximum(effectCount);
			progressBar.setValue(0);
			progressBar.setStringPainted(true);

			int progress = 0;
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
								frmEffectsDj, Strings.GUI_EFFECT_ARG_PREFIX
										+ argDes);
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
				progressBar.setValue(++progress);
			}

			JOptionPane.showMessageDialog(frmEffectsDj,
					Strings.GUI_EFFECTS_DONE);
		}
	}
}
