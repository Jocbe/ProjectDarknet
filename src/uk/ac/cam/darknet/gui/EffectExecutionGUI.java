package uk.ac.cam.darknet.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;

public class EffectExecutionGUI {

	private JFrame frmEffectsDj;

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
		frmEffectsDj.setBounds(100, 100, 450, 300);
		frmEffectsDj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
