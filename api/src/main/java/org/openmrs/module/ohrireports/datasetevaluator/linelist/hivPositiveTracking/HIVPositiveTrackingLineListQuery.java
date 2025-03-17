package org.openmrs.module.ohrireports.datasetevaluator.linelist.hivPositiveTracking;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.ObsElement;
import org.openmrs.module.ohrireports.constants.EncounterType;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.openmrs.module.ohrireports.constants.PositiveCaseTrackingConceptQuestions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class HIVPositiveTrackingLineListQuery extends ObsElement {
	
	private final DbSessionFactory sessionFactory;
	
	private List<Integer> baseEncounter;
	
	private List<Integer> followUpEncounter;
	
	@Autowired
	EncounterQuery encounterQuery;
	
	public List<Integer> getBaseEncounter() {
		return baseEncounter;
	}
	
	public List<Integer> getFollowUpEncounter() {
		return followUpEncounter;
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
		followUpEncounter = getFollowUpEncounter(baseCohort, end);
	}
	
	private List<Integer> getFollowUpEncounter(Cohort cohort, Date endDate) {
		return encounterQuery.getEncounters(Collections.singletonList(FollowUpConceptQuestions.FOLLOW_UP_DATE), null,
		    endDate, cohort);
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
