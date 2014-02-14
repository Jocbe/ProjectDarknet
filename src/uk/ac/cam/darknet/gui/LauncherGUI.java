package uk.ac.cam.darknet.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;

/**
 * @author Augustin Zidek
 * 
 */
public class LauncherGUI {

	private JFrame frame;

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
					window.frame.setVisible(true);
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
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
