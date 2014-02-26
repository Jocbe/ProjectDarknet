package uk.ac.cam.darknet.frontend;

import java.util.ArrayList;
import java.util.Enumeration;
import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.common.IndividualRequirements;
import uk.ac.cam.darknet.common.Show;
import uk.ac.cam.darknet.database.DatabaseManager;

/**
 * An effect for generating a report for each individual.
 * 
 * @author Augustin Zidek
 * 
 */
public class ReportEffect extends Effect {
	private String	outputPath;

	public ReportEffect(DatabaseManager dm) {
		super(dm);
	}

	@Override
	public void execute(Show show) {
		ArrayList<Individual> individuals;
		IndividualRequirements requirements = new IndividualRequirements(show);
		Enumeration<String> attributes;
		String currentAttribute;
		try {
			individuals = (ArrayList<Individual>) dm.getSuitableIndividuals(requirements);
			for (Individual i : individuals) {
				attributes = i.getProperties().keys();
				while (attributes.hasMoreElements()) {
					currentAttribute = attributes.nextElement();
					i.getAttribute(currentAttribute);
				}
			}
		} catch (Exception e) {
			return;
		}
	}

	@Override
	public void setup(String[] args) {
		outputPath = args[0];
	}
}