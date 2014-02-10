package uk.ac.cam.darknet.tests;

import java.io.IOException;
import java.net.URL;
import uk.ac.cam.darknet.storage.ImageStorage;

/**
 * Tests the storage by saving three images and retrieving three images (each in different format).
 * 
 * @author Augustin Zidek
 * 
 */
public class StorageTests {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final ImageStorage imgStore = new ImageStorage();
		try {
			// URLs of the images
			final URL testImageURL1 = new URL("http://upload.wikimedia.org/wikipedia/sr/0/0c/Firefox-logo.png");
			final URL testImageURL2 = new URL("http://upload.wikimedia.org/wikipedia/commons/2/2b/Seven_segment_display-animated.gif");
			final URL testImageURL3 = new URL("http://upload.wikimedia.org/wikipedia/commons/c/c9/Moon.jpg");

			// Test saving
			final String img1 = imgStore.saveImage(testImageURL1);
			final String img2 = imgStore.saveImage(testImageURL2);
			final String img3 = imgStore.saveImage(testImageURL3);

			// Test retrieving
			imgStore.retreiveImage(img1);
			imgStore.retreiveImage(img2);
			imgStore.retreiveImage(img3);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
