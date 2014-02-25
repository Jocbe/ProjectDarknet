package uk.ac.cam.darknet.frontend;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;
import java.util.List;

import javax.imageio.ImageIO;

import uk.ac.cam.darknet.common.AttributeCategories;
import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.common.Properties;
import uk.ac.cam.darknet.common.Show;
import uk.ac.cam.darknet.database.DatabaseManager;
import uk.ac.cam.darknet.database.SecondaryDatabaseManager;
import uk.ac.cam.darknet.storage.ImageStorage;

/**
 * An effect for generating a wall of pictures of all the people in the theatre
 * (i.e. all individuals in the database for the given performance).
 * 
 * @author Augustin Zidek
 * 
 */
public class PictureWallEffect implements Effect {

	@Override
	public void execute(Show show) {
		// DO I MAKE THIS? OR SHOULD IT BE PASSED? this goes for
		// globalAttributeTable and DatabaseManager
		Hashtable<String, AttributeCategories> globalAttributeTable = new Hashtable<String, AttributeCategories>();
		DatabaseManager manager = new SecondaryDatabaseManager(
				globalAttributeTable);
		// get rid of exceptions? if throw would need to change effect interface
		List<Individual> individuals = manager.getByShow(show);
		// path would need to be correct one? value should be passed?
		ImageStorage imgStore = new ImageStorage();

		// DO I create this here or should it be passed?? or is it not necessary
		// b/c gotten individuals
		DataProvider provider = new DataProvider();
		// should the second parameter be an attribute categories instead of a
		// String?? and is PHOTO correct?
		provider.getSpecificAttribute(individuals, "PHOTO");

		BufferedImage[] photos = new BufferedImage[individuals.size()];
		for (int i = 0; i < photos.length; i++) {
			Properties p = individuals.get(i).getProperties();
			photos[i] = (BufferedImage) imgStore.retreiveImage((String) p.get(
					"photo").getAttribute());
		}

		BufferedImage image;
		if (photos.length > 5) {
			// assemble into columns of 5
			int numcols = individuals.size() / 5;
			int[] colwidth = new int[numcols];
			BufferedImage[] colPhotos = new BufferedImage[numcols];
			for (int i = 0; i < numcols; i++) {
				int heightTotal = 0;
				for (int j = 0; j < 5; j++) {
					heightTotal += photos[i + j].getHeight();
					int width = photos[i + j].getWidth();
					if (width > colwidth[i]) {
						colwidth[i] = width;
					}
				}
				int heightCurr = 0;
				BufferedImage colImage = new BufferedImage(colwidth[i],
						heightTotal, BufferedImage.TYPE_INT_RGB);
				Graphics2D g2d = colImage.createGraphics();
				for (int j = 0; j < 5; j++) {
					g2d.drawImage(photos[i + j], 0, heightCurr, null);
					heightCurr += photos[i + j].getHeight();
				}
				colPhotos[i] = colImage;
				g2d.dispose();
			}

			// assemble columns into 1 image
			image = concatenateByWidth(colPhotos);
		} else {
			image = concatenateByWidth(photos);
		}

		//ummm pathname?? need input?
		ImageIO.write(image, "png", new File("pictureEffect.png"));

	}
	
	private BufferedImage concatenateByWidth(BufferedImage[] photos){
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
		Graphics2D g2d = image.createGraphics();
		for (int i = 0; i < photos.length; i++) {
			g2d.drawImage(photos[i], widthCurr, 0, null);
			widthCurr += photos[i].getWidth();
		}
		g2d.dispose();
		return image;
	}
}
