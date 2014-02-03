package uk.ac.cam.darknet.frontend;

import java.util.List;

import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.common.Policy;

/**
 * Primary data provider provides information obtained by primary data
 * collectors. This data is 100% reliable, i.e. it comes either from the booking
 * systems or has been manually inserted into the system.
 * 
 * @author Augustin Zidek
 * 
 */
public abstract class PrimaryDataProvider implements DataProvider {

	@Override
	public void getFirstName(Individual i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getDoB(Individual i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getLastName(Individual i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getAllData(Individual i, Policy p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getAllData(List<Individual> individualList, Policy p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getFirstName(List<Individual> individualList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getLastName(List<Individual> individualList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getDoB(List<Individual> individualList) {
		// TODO Auto-generated method stub

	}

}
