package uk.ac.cam.darknet.frontend;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import uk.ac.cam.darknet.common.AttributeCategories;
import uk.ac.cam.darknet.common.AttributeReliabilityPair;
import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.common.IndividualRequirements;
import uk.ac.cam.darknet.common.Show;
import uk.ac.cam.darknet.database.DatabaseManager;
import uk.ac.cam.darknet.database.SecondaryDatabaseManager;
import uk.ac.cam.darknet.exceptions.ConfigFileNotFoundException;
import uk.ac.cam.darknet.exceptions.InvalidAttributeNameException;
import uk.ac.cam.darknet.exceptions.InvalidAttributeTypeException;
import uk.ac.cam.darknet.exceptions.InvalidReliabilityException;
import uk.ac.cam.darknet.exceptions.RequestNotSatisfiableException;
import uk.ac.cam.darknet.exceptions.UnknownAttributeException;
import uk.ac.cam.darknet.storage.ImageStorage;

/**
 * An effect for generating a wall of pictures of all the people in the theatre
 * (i.e. all individuals in the database for the given performance).
 * 
 * Setup method should take in a String array with 2 arguments: the "pathname"
 * of the directory the picture should be stored in and the "filename" (without
 * extension) the picture should be stores as
 * 
 * @author Farah Patel
 * 
 */
public class PictureWallEffect extends Effect {

	private String pathname;
	private String filename;
	private int colSize = 5;

	@Override
	public List<String> getSetupArgDescriptions() {
		final List<String> argDescr = new ArrayList<>();
		argDescr.add("path of the directory the picture should be stored in");
		argDescr.add("filename (without extension) the picture should be stored as");
		return argDescr;
	}

	/**
	 * Constructs PictureWallEffect
	 * 
	 * @param dm
	 *            appropriate DatabaseManager
	 */
	public PictureWallEffect(DatabaseManager dm) {
		super(dm);
	}

	@Override
	public void setup(String args[]) {
		this.pathname = args[0];
		this.filename = args[1];
	}

	@Override
	public void execute(Show show) {
		IndividualRequirements req = new IndividualRequirements(show);
		try {
			// create strict requirements for this effect
			req.addRequirement(AttributeCategories.PHOTO, 0.0);
			// get all individuals from the specified show with specified
			// requirements
			List<Individual> individuals = this.dm.getSuitableIndividuals(req);
			ImageStorage imgStore = new ImageStorage();

			List<String> photoids = new LinkedList<String>();
			// for each individual get all photo ids in the database
			for (int i = 0; i < individuals.size(); i++) {
				List<AttributeReliabilityPair> pairs = individuals.get(i)
						.getProperties().get("fb_photo");
				for (int j = 0; j < pairs.size(); j++) {
					photoids.add((String) pairs.get(j).getAttribute());
				}
			}

			// randomize photos
			Collections.shuffle(photoids);

			// get images from image storage
			BufferedImage[] photos = new BufferedImage[photoids.size()];
			for (int i = 0; i < photos.length; i++) {
				photos[i] = (BufferedImage) imgStore.retreiveImage(photoids
						.get(i));
			}

			BufferedImage image;
			if (photos.length > 5) {
				// assemble photos into columns of colSize
				int numcols = photos.length / this.colSize;
				BufferedImage[] colPhotos = new BufferedImage[numcols];
				for (int i = 0; i < numcols; i++) {
					colPhotos[i] = this.concatenateByHeight(Arrays.copyOfRange(
							photos, this.colSize * i, this.colSize * i + this.colSize));
				}
				// merge columns into a single photo
				image = this.concatenateByWidth(colPhotos);
			} else {
				image = this.concatenateByWidth(photos);
			}

			// save image
			ImageIO.write(image, "png", new File(this.pathname + this.filename + ".png"));
		} catch (InvalidReliabilityException e) {
			System.err.println("Invalid Reliability of Photo");
			return;
		} catch (SQLException e) {
			System.err.println("SQL Error");
			return;
		} catch (RequestNotSatisfiableException e) {
			System.err.println("DatabaseManager cannot satisfy Request");
			return;
		} catch (IOException e) {
			System.err.println("Cannot write image");
			return;
		} catch (UnknownAttributeException e) {
			e.printStackTrace();
			return;
		} catch (InvalidAttributeTypeException e) {
			e.printStackTrace();
			return;
		}

	}

	/**
	 * Method that takes in a list of images and returns an image of all the
	 * photos concatenated by width
	 */
	private BufferedImage concatenateByWidth(BufferedImage[] photos) {
		int widthTotal = 0;
		int height = 0;
		for (int i = 0; i < photos.length; i++) {
			widthTotal += photos[i].getWidth();
			int h = photos[i].getHeight();
			if (h > height) {
				height = h;
			}
		}
		int widthCurr = 0;
		BufferedImage image = new BufferedImage(widthTotal, height,
				BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < photos.length; i++) {
			Graphics2D g2d = image.createGraphics();
			g2d.drawImage(photos[i], widthCurr, 0, null);
			widthCurr += photos[i].getWidth();
			g2d.dispose();
		}
		return image;
	}

	/**
	 * Method that takes in a list of images and returns an image of all the
	 * photos concatenated by height
	 */
	private BufferedImage concatenateByHeight(BufferedImage[] photos) {
		int heightTotal = 0;
		int width = 0;
		for (int i = 0; i < photos.length; i++) {
			heightTotal += photos[i].getHeight();
			int w = photos[i].getWidth();
			if (w > width) {
				width = w;
			}
		}
		int heightCurr = 0;
		BufferedImage image = new BufferedImage(width, heightTotal,
				BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < photos.length; i++) {
			Graphics2D g2d = image.createGraphics();
			g2d.drawImage(photos[i], 0, heightCurr, null);
			heightCurr += photos[i].getHeight();
			g2d.dispose();
		}
		return image;
	}

	/**
	 * Method solely for testing the PicuteWallEffect
	 * 
	 * @param args
	 * @throws ClassNotFoundException
	 * @throws ConfigFileNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 * @throws InvalidAttributeNameException
	 */
	public static void main(String[] args) throws ClassNotFoundException,
			ConfigFileNotFoundException, IOException, SQLException,
			InvalidAttributeNameException {
		Hashtable<String, AttributeCategories> globalAttributeTable = new Hashtable<String, AttributeCategories>();
		globalAttributeTable.put("fb_photo", AttributeCategories.PHOTO);
		SecondaryDatabaseManager dm = new SecondaryDatabaseManager(
				globalAttributeTable);
		PictureWallEffect pwe = new PictureWallEffect(dm);
		String[] files = { "", "pictureTest" };
		pwe.setup(files);
		Show show = dm.getAllShows().get(2);
		pwe.execute(show);

	}

}
