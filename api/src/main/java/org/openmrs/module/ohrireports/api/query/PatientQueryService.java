package org.openmrs.module.ohrireports.api.query;

import java.util.Date;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.api.OpenmrsService;

public interface PatientQueryService extends OpenmrsService {
	
	Cohort getNewOnArtCohort(String gender, Date startOnOrAfter, Date endOrBefore, Cohort cohort);
	
	Cohort getArtStartedCohort(String gender, Date startOnOrAfter, Date endOrBefore, Cohort cohort, Cohort toBeExcludedCohort);
	
	Cohort getActiveOnCohort();
	
	Cohort getActiveOnArtCohort(String gender, Date startOnOrAfter, Date endOnOrBefore, Cohort cohort);
	
	Cohort getCurrentOnTreatmentCohort();
	
	public List<Integer> getBaseEncounters(Date start, Date end);
	
	public List<Integer> getBaseEncountersByFollowUpDate(Date starDate, Date endDate);
	
	Cohort getCurrentOnTreatmentCohort(String gender, Date startOnOrAfter, Date endOnOrBefore, Cohort cohort);
	
	List<Person> getPersons(Cohort cohort);
	
	Cohort getPatientByPregnantStatus(Cohort patient, String ConceptUUID, Date startOnOrAfter, Date endOnOrBefore);
}
