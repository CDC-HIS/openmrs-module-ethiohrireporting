package org.openmrs.module.ethiohrireports.query;

import java.util.Calendar;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.query.PatientQuery;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class ArtPatientQueryImpTest extends BaseModuleContextSensitiveTest {
	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/ReportDataset.xml";
	
	private Calendar startDate, endDate;
	
	private Cohort cohort;
	
	// @Before
	// public void setupDataset() throws Exception {
	// executeDataSet(XML_DATASET_PATH);
	// }
	
	@Before
	public void Setup() {
		startDate = Calendar.getInstance();
		startDate.add(Calendar.MONTH, -12);
		//startDate.set(2023, 01, 01);
		endDate = Calendar.getInstance();
		// endDate.set(2023, 12, 30);
		
		cohort = new Cohort(1);
		cohort.addMember(5);
		cohort.addMember(3);
		cohort.addMember(1058);
		
	}
	
	@Test
	public void QueryShouldFetchAllPatientOnArt() throws Exception {
		Calendar startDate1 = Calendar.getInstance();
		startDate1.add(Calendar.MONTH, -12);
		
		Cohort result = Context.getService(PatientQuery.class).getActiveOnArtCohort("", startDate1.getTime(),
		    endDate.getTime(), null);
		Assert.assertEquals(cohort.getMemberIds().size(), result.getMemberIds().size());
		
	}
	
	@Test
	public void QueryShouldFetchAllPatientBaseONCohort() throws Exception {
		List<Person> result = Context.getService(PatientQuery.class).getPersons(cohort);
		Assert.assertEquals(10, result.size());
		
	}
}