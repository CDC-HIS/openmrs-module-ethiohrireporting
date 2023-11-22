package org.openmrs.module.ohrireports.api.dao;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;

public interface PatientQueryDao {
	
	Cohort getNewOnArtCohort(String gender, Date startOnOrAfter, Date endOrBefore, Cohort cohort, List<Integer> encounters);
	
	Collection<Integer> getArtStartedCohort(String gender, Date startOnOrAfter, Date endOrBefore, Cohort cohort,
	        Cohort toBeExcludedCohort, List<Integer> encounters);
	
	Cohort getActiveOnArtCohort(String gender, Date startOnOrAfter, Date endOnOrBefore, Cohort cohort,
	        List<Integer> encounters);
	
	HashMap<Integer, Object> getObValue(String conceptUUId, Cohort cohort, @NotNull PatientQueryImpDao.ObsValueType type,
	        List<Integer> encounter);
	
	List<Person> getPersons(Cohort cohort);
	
	Cohort getPatientByPregnantStatus(Cohort patient, String conceptUUID, List<Integer> encounters);
	
}
