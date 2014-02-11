package uk.ac.cam.darknet.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;

public class PrimaryDataCollectorGUI {

	private JFrame frmPrimaryDataCollector;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PrimaryDataCollectorGUI window = new PrimaryDataCollectorGUI();
					window.frmPrimaryDataCollector.setVisible(true);
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
	public PrimaryDataCollectorGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmPrimaryDataCollector = new JFrame();
		frmPrimaryDataCollector.setTitle("Primary Data Collector");
		frmPrimaryDataCollector.setBounds(100, 100, 450, 300);
		frmPrimaryDataCollector.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
