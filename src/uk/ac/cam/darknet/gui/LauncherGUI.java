package uk.ac.cam.darknet.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Augustin Zidek
 * 
 */
public class LauncherGUI {

	private JFrame frmProjectDarknet;

	/**
	 * Launch the application.
	 * 
	 * @param args Ignored
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LauncherGUI window = new LauncherGUI();
					window.frmProjectDarknet.setVisible(true);
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
	public LauncherGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmProjectDarknet = new JFrame();
		frmProjectDarknet.setResizable(false);
		frmProjectDarknet.setTitle("Project Darknet - Launcher");
		frmProjectDarknet.setBounds(100, 100, 356, 141);
		frmProjectDarknet.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final JPanel panel = new JPanel();
		frmProjectDarknet.getContentPane().add(panel, BorderLayout.CENTER);

		final JButton btnPrimary = new JButton(
				"<html><center>Primary <br> data <br> collection</center></html>");
		btnPrimary.setBounds(12, 12, 100, 67);
		btnPrimary.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		panel.setLayout(null);
		panel.add(btnPrimary);

		final JButton btnsecondaryDataCollection = new JButton(
				"<html><center>Secondary <br> data <br> collection</center></html>");
		btnsecondaryDataCollection.setBounds(124, 12, 100, 67);
		panel.add(btnsecondaryDataCollection);

		final JButton btneffectsExecution = new JButton(
				"<html><center>Effects <br> execution</center></html>");
		btneffectsExecution.setBounds(236, 12, 100, 67);
		panel.add(btneffectsExecution);

		final JLabel lblcopy = new JLabel(
				"<html>&copy; 2014, Project Darknet</html>");
		lblcopy.setBounds(12, 91, 324, 16);
		panel.add(lblcopy);
	}
}
