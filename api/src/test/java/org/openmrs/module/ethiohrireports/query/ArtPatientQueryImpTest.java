package org.openmrs.module.ethiohrireports.query;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.query.PatientQuery;

@RunWith(MockitoJUnitRunner.class)
public class ArtPatientQueryImpTest {
	
	
	@Test
	public void QueryShouldFetchAllPatientOnArt() {
		Cohort result = Context.getService(PatientQuery.class).getOnArtCohorts();
		Assert.assertEquals(new Cohort(5).getCohortId(), result.getCohortId());
		
	}
}
