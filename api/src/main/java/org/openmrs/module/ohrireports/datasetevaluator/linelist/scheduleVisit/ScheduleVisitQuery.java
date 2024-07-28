package org.openmrs.module.ohrireports.datasetevaluator.linelist.scheduleVisit;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.api.impl.query.ObsElement;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ScheduleVisitQuery extends ObsElement {
	
	private final DbSessionFactory sessionFactory;
	
	@Autowired
	PatientQueryImpDao patientQueryImpDao;
	
	@Autowired
	EncounterQuery encounterQuery;
	
	public List<Integer> getEncounter() {
		return encounter;
	}
	
	private List<Integer> encounter;
	
	public Cohort getBaseCohort() {
		return baseCohort;
	}
	
	private Cohort baseCohort;
	
	@Autowired
	public ScheduleVisitQuery(DbSessionFactory _SessionFactory) {
		super(_SessionFactory);
		sessionFactory = _SessionFactory;
	}
	
	public void generateReport(Date start, Date end) {
		encounter = encounterQuery.getAliveFollowUpEncounters(null, end);
		List<Integer> visitEncounter = encounterQuery.getEncounters(
		    Collections.singletonList(FollowUpConceptQuestions.NEXT_VISIT_DATE), start, end, encounter);
		baseCohort = getCohort(visitEncounter);
	}
	
	public Cohort getCohort(List<Integer> encounterIds) {
		StringBuilder sqlBuilder = new StringBuilder(
		        "select distinct (person_id) from obs where encounter_id in (:encounterIds) ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
		query.setParameterList("encounterIds", encounterIds);
		
		return new Cohort(query.list());
		
	}
	
	public HashMap<Integer, Object> getObNumericValue(String conceptUUId) {
		return getDictionary(getObsNumber(encounter, conceptUUId, baseCohort));
	}
	
	public List<Person> getPersons(Cohort baseCohort) {
		return patientQueryImpDao.getPersons(baseCohort);
	}
	
}
