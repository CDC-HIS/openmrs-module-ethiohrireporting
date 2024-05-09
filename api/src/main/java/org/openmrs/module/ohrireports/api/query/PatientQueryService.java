package org.openmrs.module.ohrireports.api.query;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.id.IncrementGenerator;
import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;

public interface PatientQueryService extends OpenmrsService {
	
	Cohort getNewOnArtCohort(String gender, Date startOnOrAfter, Date endOrBefore, Cohort cohort, List<Integer> encounters);
	
	Cohort getArtStartedCohort(String gender, Date startOnOrAfter, Date endOrBefore, Cohort cohort,
	        Cohort toBeExcludedCohort, List<Integer> encounters);
	
	Cohort getActiveOnArtCohort(String gender, Date startOnOrAfter, Date endOnOrBefore, Cohort cohort,
	        List<Integer> encounters);
	
	Cohort getCD4ByCohort(Cohort cohort, Boolean isCD4GreaterThan200, List<Integer> encounters);
	
	HashMap<Integer, Object> getObsValue(Cohort cohort, String conceptUUID, PatientQueryImpDao.ObsValueType type,
	        List<Integer> encounters);
	
	List<Person> getPersons(Cohort cohort);
	
	Cohort getPatientByPregnantStatus(Cohort patient, String ConceptUUID, List<Integer> encounters);
}
