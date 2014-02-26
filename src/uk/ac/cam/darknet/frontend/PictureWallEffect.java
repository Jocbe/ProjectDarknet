package uk.ac.cam.darknet.frontend;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import uk.ac.cam.darknet.common.AttributeCategories;
import uk.ac.cam.darknet.common.AttributeReliabilityPair;
import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.common.IndividualRequirements;
import uk.ac.cam.darknet.common.Properties;
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
 * @author Augustin Zidek
 * 
 */
public class PictureWallEffect extends Effect {
	
	private String pathname;
	
	//Setup method, DatabaseManager
	/**
	 * @param dm
	 */
	public PictureWallEffect(DatabaseManager dm){
		super(dm);
	}
	
	public void setup(String pathname){
		this.pathname = pathname;
	}

	@Override
	public void execute(Show show) {
		IndividualRequirements req = new IndividualRequirements(show);
		try {
			req.addRequirement(AttributeCategories.PHOTO, 0.0);
			List<Individual> individuals = this.dm.getSuitableIndividuals(req);
			ImageStorage imgStore = new ImageStorage();

			List<String> photoids = new LinkedList<String>();
			for (int i=0; i<individuals.size(); i++){
				 List<AttributeReliabilityPair> pairs = individuals.get(i).getProperties().get("fb_photo");
				 for (int j=0; j<pairs.size(); j++){
					 photoids.add((String) pairs.get(i).getAttribute());
				 }
			}
			
			BufferedImage[] photos = new BufferedImage[photoids.size()];
			for (int i = 0; i < photos.length; i++) {
				photos[i] = (BufferedImage) imgStore.retreiveImage(photoids.get(i));
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
		} catch (InvalidReliabilityException e) {
			//TODO 
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RequestNotSatisfiableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownAttributeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAttributeTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


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
	public static void main(String[] args) throws ClassNotFoundException, ConfigFileNotFoundException, IOException, SQLException, InvalidAttributeNameException{
		Hashtable<String,AttributeCategories> globalAttributeTable = new Hashtable<String,AttributeCategories>();
		globalAttributeTable.put("fb_photo", AttributeCategories.PHOTO);
		SecondaryDatabaseManager dm = new SecondaryDatabaseManager(globalAttributeTable);
		PictureWallEffect pwe = new PictureWallEffect(dm);
		Show show = dm.getAllShows().get(2);
		pwe.execute(show);
	}
	
}
