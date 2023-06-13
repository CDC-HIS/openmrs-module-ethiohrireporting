package org.openmrs.module.ohrireports.api.impl.query;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.ohrireports.api.dao.PatientQueryDao;
import org.openmrs.module.ohrireports.api.query.PatientQuery;

public class PatientQueryImp extends BaseOpenmrsService implements PatientQuery {
	
	private PatientQueryDao patientQueryDao;
	
	public PatientQueryImp() {
	}
	
	public PatientQueryDao getPatientQueryDao() {
		return patientQueryDao;
	}
	
	public void setPatientQueryDao(PatientQueryDao patientQueryDao) {
		this.patientQueryDao = patientQueryDao;
	}
	
	@Override
	public Cohort getOnArtCohorts() {
		
		return patientQueryDao.getOnArtCohorts();
	}
	
	@Override
	public Cohort getOnArtCohorts(String gender, Date startOnOrAfter, Date endOnOrBefore, Cohort cohort) {
		
		return patientQueryDao.getOnArtCohorts(gender, startOnOrAfter, endOnOrBefore, cohort);
	}
	
	@Override
	public Cohort getActiveOnCohort() {
		return patientQueryDao.getActiveOnCohort();
	}
	
	@Override
	public Cohort getActiveOnCohort(String gender, Date startOnOrAfter, Date endOnOrBefore, Cohort cohort) {
		return patientQueryDao.getActiveOnCohort(gender, startOnOrAfter, endOnOrBefore, cohort);
		
	}
	
	@Override
	public Cohort getCurrentOnTreatmentCohort() {
		return patientQueryDao.getCurrentOnTreatmentCohort();
	}
	
	@Override
	public Cohort getCurrentOnTreatmentCohort(String gender, Date endOnOrBefore, Cohort cohort) {
		return patientQueryDao.getCurrentOnTreatmentCohort(gender, endOnOrBefore, cohort);
	}
	
	@Override
	public List<Person> getPersons(Cohort cohort) {
		return patientQueryDao.getPersons(cohort);
	}
	
	@Override
	public Cohort getPatientByPregnantStatus(Cohort patient, String conceptUUID, Date startOnOrAfter, Date endOnOrBefore) {
		return patientQueryDao.getPatientByPregnantStatus(patient, conceptUUID, startOnOrAfter, endOnOrBefore);
	}
	
}
