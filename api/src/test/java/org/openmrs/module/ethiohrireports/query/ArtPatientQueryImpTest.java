package org.openmrs.module.ethiohrireports.query;

import java.util.Calendar;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.dao.PatientQueryDao;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.api.query.PatientQuery;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class ArtPatientQueryImpTest extends BaseModuleContextSensitiveTest {
	
	protected static final String XML_DATASET_PATH = "org/openmrs/module/reporting/include/ReportDataset.xml";
	
	private Calendar startDate, endDate;
	
	@Autowired
	private PatientQueryImpDao patientQueryDao;
	
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
		endDate.set(2023, Calendar.DECEMBER, 29);
		
		// cohort = new Cohort(1);
		// cohort.addMember(5);
		// cohort.addMember(3);
		// cohort.addMember(1058);
		
	}
	
	@Test
	public void QueryShouldFetchAllPatientOnArt() throws Exception {
		// Calendar startDate1 = Calendar.getInstance();
		// startDate1.set(2022, Calendar.DECEMBER, 01);
		// /patientQueryDao.goBackToMonths(endDate.getTime(), startDate, endDate);
		// Assert.assertEquals(startDate1.getTime(), startDate.getTime());
	}
	
	@Test
	public void QueryShouldFetchAllPatientBaseONCohort() throws Exception {
		//List<Person> result = Context.getService(PatientQuery.class).getPersons(cohort);
		//Assert.assertEquals(10, result.size());
		
	}
}
