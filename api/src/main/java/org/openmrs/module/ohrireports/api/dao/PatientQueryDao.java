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
	
	/**
	 * @param questionConcept this should only be uuid of concept question
	 * @param answer uuid concept for questionConcept answer support only value_coded answer concept
	 * @param start start date of the report going to be considered
	 * @param end date of the report going to be considered
	 * @return collection latest encounter id base on the passed parameter of Question concepts and
	 *         answer
	 */
	public List<Integer> getBaseEncounters(String questionConcept, Date start, Date end);
	
	public List<Integer> init(Date endDate);
	
	Cohort getActiveOnArtCohort();
	
	Cohort getActiveOnArtCohort(String gender, Date startOnOrAfter, Date endOnOrBefore, Cohort cohort);
	
	Cohort getCurrentOnTreatmentCohort();
	
	Cohort getCurrentOnTreatmentCohort(String gender, Date startOnOrAfter, Date endOnOrBefore, Cohort cohort);
	
	List<Person> getPersons(Cohort cohort);
	
	Cohort getPatientByPregnantStatus(Cohort patient, String conceptUUID);
	
}
