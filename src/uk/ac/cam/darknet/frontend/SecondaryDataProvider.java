package uk.ac.cam.darknet.frontend;

import java.util.List;

import uk.ac.cam.darknet.common.Individual;
import uk.ac.cam.darknet.common.Policy;

/**
 * Secondary data provider provides information obtained by secondary data
 * collectors. That is data from various web services like Facebook, Twitter,
 * LinkedIN, etc.
 * 
 * @author Augustin Zidek
 * 
 */
public abstract class SecondaryDataProvider implements DataProvider {

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
