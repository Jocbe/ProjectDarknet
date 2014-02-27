package uk.ac.cam.darknet.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.TitledBorder;

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
	private JTable table;
	private JButton btnRunEffects;
	private JScrollPane scrollPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EffectExecutionGUI window = new EffectExecutionGUI();
					window.frmEffectsDj.setVisible(true);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public EffectExecutionGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmEffectsDj = new JFrame();
		frmEffectsDj.setTitle("Effects DJ");
		frmEffectsDj.setBounds(100, 100, 646, 499);
		frmEffectsDj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final JPanel panel = new JPanel();
		frmEffectsDj.getContentPane().add(panel, BorderLayout.CENTER);

		final JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Select show",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));

		final JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null,
				"Select from the list of available effects",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));

		btnRunEffects = new JButton("Run effects");
		panel_1.setLayout(null);

		final JComboBox<String> comboBox = new JComboBox<>();
		comboBox.setBounds(12, 19, 279, 25);
		panel_1.add(comboBox);

		scrollPane = new JScrollPane();

		table = new JTable();
		scrollPane.setViewportView(table);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel
				.createParallelGroup(Alignment.TRAILING)
				.addGroup(
						gl_panel.createSequentialGroup()
								.addContainerGap(525, Short.MAX_VALUE)
								.addComponent(btnRunEffects).addContainerGap())
				.addGroup(
						gl_panel.createSequentialGroup()
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.TRAILING)
												.addGroup(
														Alignment.LEADING,
														gl_panel.createSequentialGroup()
																.addContainerGap()
																.addComponent(
																		panel_2,
																		GroupLayout.DEFAULT_SIZE,
																		612,
																		Short.MAX_VALUE))
												.addGroup(
														gl_panel.createSequentialGroup()
																.addGap(12)
																.addComponent(
																		panel_1,
																		GroupLayout.DEFAULT_SIZE,
																		612,
																		Short.MAX_VALUE)))
								.addGap(12)));
		gl_panel.setVerticalGroup(gl_panel.createParallelGroup(
				Alignment.LEADING).addGroup(
				gl_panel.createSequentialGroup()
						.addGap(12)
						.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 56,
								GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(panel_2, GroupLayout.DEFAULT_SIZE, 349,
								Short.MAX_VALUE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnRunEffects).addContainerGap()));
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(gl_panel_2.createParallelGroup(
				Alignment.LEADING).addGroup(
				gl_panel_2
						.createSequentialGroup()
						.addGap(7)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE,
								588, Short.MAX_VALUE).addGap(7)));
		gl_panel_2.setVerticalGroup(gl_panel_2.createParallelGroup(
				Alignment.LEADING).addGroup(
				gl_panel_2
						.createSequentialGroup()
						.addGap(8)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE,
								297, Short.MAX_VALUE).addGap(7)));
		panel_2.setLayout(gl_panel_2);
		panel.setLayout(gl_panel);
	}

}
