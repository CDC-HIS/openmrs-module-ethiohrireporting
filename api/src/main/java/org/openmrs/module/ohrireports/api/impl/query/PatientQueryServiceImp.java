package org.openmrs.module.ohrireports.api.impl.query;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.ohrireports.api.dao.PatientQueryDao;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.springframework.stereotype.Component;

public class PatientQueryServiceImp extends BaseOpenmrsService implements PatientQueryService {
	
	private PatientQueryDao patientQueryDao;
	
	public PatientQueryServiceImp() {
	}
	
	public PatientQueryDao getPatientQueryDao() {
		return patientQueryDao;
	}
	
	public void setPatientQueryDao(PatientQueryDao patientQueryDao) {
		this.patientQueryDao = patientQueryDao;
	}
	
	@Override
	public Cohort getActiveOnArtCohort(String gender, Date startOnOrAfter, Date endOnOrBefore, Cohort cohort,
	        List<Integer> encounters) {
		return patientQueryDao.getActiveOnArtCohort(gender, startOnOrAfter, endOnOrBefore, cohort, encounters);
		
	}
	
	@Override
	public HashMap<Integer, Object> getObsValue(Cohort cohort, String conceptUUID, PatientQueryImpDao.ObsValueType type,
	        List<Integer> encounters) {
		return patientQueryDao.getObValue(conceptUUID, cohort, type, encounters);
	}
	
	@Override
	public List<Person> getPersons(Cohort cohort) {
		return patientQueryDao.getPersons(cohort);
	}
	
	@Override
	public Cohort getPatientByPregnantStatus(Cohort patient, String conceptUUID, List<Integer> encounters) {
		return patientQueryDao.getPatientByPregnantStatus(patient, conceptUUID, encounters);
	}
	
	@Override
	public Cohort getNewOnArtCohort(String gender, Date startOnOrAfter, Date endOrBefore, Cohort cohort,
	        List<Integer> encounters) {
		return patientQueryDao.getNewOnArtCohort(gender, startOnOrAfter, endOrBefore, cohort, encounters);
	}
	
	@Override
	public Cohort getArtStartedCohort(String gender, Date startOnOrAfter, Date endOrBefore, Cohort cohort,
	        Cohort toBeExcludedCohort, List<Integer> encounters) {
		return new Cohort(patientQueryDao.getArtStartedCohort(gender, startOnOrAfter, endOrBefore, cohort,
		    toBeExcludedCohort, encounters));
	}
	
}
