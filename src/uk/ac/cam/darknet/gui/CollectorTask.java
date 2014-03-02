package uk.ac.cam.darknet.gui;

import javax.swing.SwingWorker;

import uk.ac.cam.darknet.backend.SecondaryDataCollector;

/**
 * A SwingWorker task that runs a Secondary Data Collector. It is used to notify
 * the GUI that the given collector has finished executing its task so that the
 * GUI can display nice progress bar but still be responsive.
 * 
 * @author Augustin Zidek
 * 
 */
public class CollectorTask extends SwingWorker<Void, Void> {
	private final SecondaryDataCollector collector;
	private final DataCollectorGUIListener guiListener;

	/**
	 * Initialize the task with the collector to be run and the GUI Listener
	 * that takes care of the progress.
	 * 
	 * @param collector The collector to be run in this worker.
	 * @param guiListener The GUI listener that is notified when the collector
	 *            is done with its work.
	 */
	public CollectorTask(final SecondaryDataCollector collector,
			final DataCollectorGUIListener guiListener) {
		this.collector = collector;
		this.guiListener = guiListener;
	}

	@Override
	protected Void doInBackground() throws Exception {
		// Start the collector in a new thread
		final Thread collectorThread = new Thread(collector);
		collectorThread.run();

		return null;
	}

	protected void done() {
		guiListener.notifyCollectorDone();
	}
}
