package org.openmrs.module.ohrireports.api.query;

import java.util.Date;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.api.OpenmrsService;

public interface PatientQueryService extends OpenmrsService {
	
	Cohort getOnArtCohorts();
	
	Cohort getOnArtCohorts(String gender, Date startOnOrAfter, Date endOnOrBefore, Cohort cohort);
	
	Cohort getActiveOnCohort();
	
	Cohort getActiveOnArtCohort(String gender, Date startOnOrAfter, Date endOnOrBefore, Cohort cohort);
	
	Cohort getCurrentOnTreatmentCohort();
	
	Cohort getCurrentOnTreatmentCohort(String gender, Date startOnOrAfter, Date endOnOrBefore, Cohort cohort);
	
	List<Person> getPersons(Cohort cohort);
	
	Cohort getPatientByPregnantStatus(Cohort patient, String ConceptUUID, Date startOnOrAfter, Date endOnOrBefore);
}
