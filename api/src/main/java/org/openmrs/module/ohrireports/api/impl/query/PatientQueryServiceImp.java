package org.openmrs.module.ohrireports.api.impl.query;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.ohrireports.api.dao.PatientQueryDao;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;

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
	public Cohort getActiveOnCohort() {
		return patientQueryDao.getActiveOnArtCohort();
	}
	
	@Override
	public Cohort getActiveOnArtCohort(String gender, Date startOnOrAfter, Date endOnOrBefore, Cohort cohort) {
		return patientQueryDao.getActiveOnArtCohort(gender, startOnOrAfter, endOnOrBefore, cohort);
		
	}
	
	@Override
	public Cohort getCurrentOnTreatmentCohort() {
		return patientQueryDao.getCurrentOnTreatmentCohort();
	}
	
	@Override
	public Cohort getCurrentOnTreatmentCohort(String gender, Date startOnOrAfter, Date endOnOrBefore, Cohort cohort) {
		return patientQueryDao.getCurrentOnTreatmentCohort(gender, startOnOrAfter, endOnOrBefore, cohort);
	}
	
	@Override
	public List<Person> getPersons(Cohort cohort) {
		return patientQueryDao.getPersons(cohort);
	}
	
	@Override
	public Cohort getPatientByPregnantStatus(Cohort patient, String conceptUUID, Date startOnOrAfter, Date endOnOrBefore) {
		return patientQueryDao.getPatientByPregnantStatus(patient, conceptUUID, startOnOrAfter, endOnOrBefore);
	}
	
	@Override
	public Cohort getNewOnArtCohort(String gender, Date startOnOrAfter, Date endOrBefore, Cohort cohort) {
		return patientQueryDao.getNewOnArtCohort(gender, startOnOrAfter, endOrBefore, cohort);
	}
	
	@Override
	public Cohort getArtStartedCohort(String gender, Date startOnOrAfter, Date endOrBefore, Cohort cohort,
	        Cohort toBeExcludedCohort) {
		return new Cohort(patientQueryDao.getArtStartedCohort(gender, startOnOrAfter, endOrBefore, cohort,
		    toBeExcludedCohort));
	}
	
	@Override
	public List<Integer> getBaseEncounters(Date start, Date end) {
		return patientQueryDao.getBaseEncounters(start, end);
	}
	
	@Override
	public List<Integer> getBaseEncountersByFollowUpDate(Date starDate, Date endDate) {
		return patientQueryDao.init(endDate);
	}
	
}
