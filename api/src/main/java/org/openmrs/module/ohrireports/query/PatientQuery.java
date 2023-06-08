package org.openmrs.module.ohrireports.query;

import java.util.Date;
import java.util.Set;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.api.OpenmrsService;

public interface PatientQuery extends OpenmrsService {
	
	Cohort getOnArtCohorts();
	
	Cohort getOnArtCohorts(String gender, Date startOnOrAfter,Date endOnOrBefore,  Cohort cohort);
	
	Cohort getActiveOnCohort();
	
	Cohort getActiveOnCohort(String gender, Date startOnOrAfter,Date endOnOrBefore, Cohort cohort);
	
	Cohort getCurrentOnTreatmentCohort();
	
	Cohort getCurrentOnTreatmentCohort(String gender, Date endOnOrBefore, Cohort cohort);
	
	Set<Person> getPatients(Cohort cohort);
}
