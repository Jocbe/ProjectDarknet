package uk.ac.cam.darknet.storage;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import javax.imageio.ImageIO;

import uk.ac.cam.darknet.common.Strings;

/**
 * Stores a image given as an URL, file or a awt.Image and returns its
 * universally unique ID (UUID) which can be later used to obtain the photo from
 * the storage. The images are stored in one folder with the unique ID being
 * their name.
 * 
 * The UUID is generated using java.util.UUID. For convenience, all images are
 * stored as jpg files.
 * 
 * The class is thread-safe.
 * 
 * @author Augustin Zidek
 * 
 */
public class ImageStorage {
	// The path to the folder which stores all the images
	private final String storagePath;

	/**
	 * Creates a new image storage located at the given path.
	 * 
	 * @param storagePath The path of the image storage folder.
	 */
	public ImageStorage(final String storagePath) {
		this.storagePath = storagePath;
	}

	/**
	 * Creates a new image storage with default path of the storage being the
	 * folder <code>storage/</code> on the same level as the folder
	 * <code>bin/</code> is.
	 */
	public ImageStorage() {
		// Get URL of this class and get rid of the file:/ at the beginning
		final String classURL = this.getClass().getResource("").toString()
				.replaceAll("file:/", "");
		// Get out of the project and bin class and go into storage/
		this.storagePath = classURL.replaceAll(Strings.STORAGE_CLASS_REL_URL,
				Strings.STORAGE_REL_URL);
	}

	/**
	 * Parse the URL and determine the format of the image.
	 */
	private String getFormat(final URL imageURL) {
		final String url = imageURL.toString();
		if (url.endsWith(".jpg")) {
			return "jpg";
		}
		else if (url.endsWith(".png")) {
			return "png";
		}
		else if (url.endsWith(".gif")) {
			return "gif";
		}
		// Default: jpg
		else {
			return "jpg";
		}
	}

	/**
	 * Decode the format from the UUID
	 */
	private String getFormatFromUUID(final String UUID) {
		return UUID.substring(UUID.length() - 3, UUID.length());
	}

	/**
	 * Retrieves image from the given URL, generates a unique name for it,
	 * converts it into a jpg and saves it in the storage. The image can be
	 * retrieved from the storage using the <code>retrieveImage()</code> method.
	 * 
	 * @param imageURL The URL where the image is.
	 * @return The unique identifier of the image
	 * @throws IOException If the image can't be read from the URL or there was
	 *             error saving it into the storage.
	 */
	public String saveImage(final URL imageURL) throws IOException {
		// Read the image from the given URL, throws IOException if error
		final BufferedImage image = ImageIO.read(imageURL);
		// Get the format of the image (jpg/png/gif otherwise jpg)
		final String format = getFormat(imageURL);
		// Generate unique name for the image and append also the format
		final String imageUUID = UUID.randomUUID().toString() + "-" + format;
		// Create the new file that will contain the image
		final File imageFile = new File(storagePath + imageUUID + "." + format);
		// Save the image into the storage
		ImageIO.write(image, format, imageFile);

		return imageUUID;
	}

	/**
	 * Retrieves an image with the given UUID from the storage
	 * 
	 * @param UUID The UUID of the image
	 * @return The Image with such UUID, throws exception otherwise.
	 * @throws IOException If the file with such UUID doesn't exist or there has
	 *             been some other error while reading from the storage (i.e.
	 *             the path of the storage might be corrupted).
	 */
	public Image retreiveImage(final String UUID) throws IOException {
		// Determine the format
		final String format = getFormatFromUUID(UUID);
		// Get the image file
		System.out.println(storagePath + UUID + "." + format);
		final File imageFile = new File(storagePath + UUID + "." + format);
		// Load the image from the file
		final BufferedImage image = ImageIO.read(imageFile);

		return image;
	}
}
