package org.openmrs.module.ohrireports.datasetevaluator.linelist.hivPositiveTracking;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.ObsElement;
import org.openmrs.module.ohrireports.constants.EncounterType;
import org.openmrs.module.ohrireports.constants.PositiveCaseTrackingConceptQuestions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class HIVPositiveTrackingLineListQuery extends ObsElement {
	
	private final DbSessionFactory sessionFactory;
	
	private List<Integer> baseEncounter;
	
	@Autowired
	EncounterQuery encounterQuery;
	
	public List<Integer> getBaseEncounter() {
		return baseEncounter;
	}
	
	@Autowired
	PatientQueryImpDao patientQueryImpDao;
	
	private Cohort baseCohort;
	
	public Cohort getBaseCohort() {
		return baseCohort;
	}
	
	public HIVPositiveTrackingLineListQuery(DbSessionFactory sessionFactory) {
		super(sessionFactory);
		this.sessionFactory = sessionFactory;
	}
	
	public void generateReport(Date start, Date end) {
		baseEncounter = encounterQuery.getAllEncounters(
		    Collections.singletonList(PositiveCaseTrackingConceptQuestions.POSITIVE_TRACKING_REGISTRATION_DATE), start, end,
		    EncounterType.POSITIVE_TRACKING_ENCOUNTER_TYPE);
		baseCohort = getCohort(baseEncounter);
		
	}
	
	public Cohort getCohort(List<Integer> encounterIds) {
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(
		    "select distinct (person_id) from obs where encounter_id in (:encounterIds) ");
		query.setParameterList("encounterIds", encounterIds);
		
		return new Cohort(query.list());
		
	}
	
	public List<Person> getPersons(Cohort baseCohort) {
		return patientQueryImpDao.getPersons(baseCohort);
	}
}
