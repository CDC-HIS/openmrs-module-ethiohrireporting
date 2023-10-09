package org.openmrs.module.ohrireports.api.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Person;

public interface PatientQueryDao {
	
	Cohort getOnArtCohorts();
	
	Cohort getNewOnArtCohort(String gender, Date startOnOrAfter, Date endOrBefore, Cohort cohort);
	
	Collection<Integer> getArtStartedCohort(String gender, Date startOnOrAfter, Date endOrBefore, Cohort cohort,
	        Cohort toBeExcludedCohort);
	
	Cohort getActiveOnArtCohort();
	
	Cohort getActiveOnArtCohort(String gender, Date startOnOrAfter, Date endOnOrBefore, Cohort cohort);
	
	Cohort getCurrentOnTreatmentCohort();
	
	Cohort getCurrentOnTreatmentCohort(String gender, Date startOnOrAfter, Date endOnOrBefore, Cohort cohort);
	
	List<Person> getPersons(Cohort cohort);
	
	Cohort getPatientByPregnantStatus(Cohort patient, String conceptUUID, Date startOnOrAfter, Date endOnOrBefore);
	
}
