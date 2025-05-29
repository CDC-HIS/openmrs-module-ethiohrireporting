package org.openmrs.module.ohrireports.datasetevaluator.linelist.monthlyVisit;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.api.impl.query.ObsElement;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.constants.ConceptAnswer;
import org.openmrs.module.ohrireports.constants.EncounterType;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MonthlyVisitQuery extends ObsElement {
	
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
	public MonthlyVisitQuery(DbSessionFactory _SessionFactory) {
		super(_SessionFactory);
		sessionFactory = _SessionFactory;
	}
	
	public void generateReport(Date start, Date end) {
		encounter = getMonthlyVisitEncounter(start, end);
		baseCohort = getCohort(encounter);
		
	}
	
	public Cohort getCohort(List<Integer> encounterIds) {
		StringBuilder sqlBuilder = new StringBuilder(
		        "select distinct (person_id) from obs where encounter_id in (:encounterIds) ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
		query.setParameterList("encounterIds", encounterIds);
		
		return new Cohort(query.list());
		
	}
	
	public List<Person> getPersons(Cohort baseCohort) {
		return patientQueryImpDao.getPersons(baseCohort);
	}
	
	public List<Integer> getMonthlyVisitEncounter(Date start, Date end) {
		List<Integer> allEncounters = encounterQuery.getAllEncounters(
		    Collections.singletonList(FollowUpConceptQuestions.FOLLOW_UP_DATE), start, end,
		    EncounterType.HTS_FOLLOW_UP_ENCOUNTER_TYPE);
		
		String builder = "select ob.encounter_id from obs as ob" + " where ob.concept_id ="
		        + conceptQuery(FollowUpConceptQuestions.FOLLOW_UP_STATUS) + " and ob.value_coded in "
		        + conceptQuery(Arrays.asList(ConceptAnswer.RESTART, ConceptAnswer.ALIVE))
		        + " and ob.encounter_id in (:encounters)";
		
		Query q = sessionFactory.getCurrentSession().createSQLQuery(builder);
		q.setParameterList("encounters", allEncounters);
		
		List list = q.list();
		
		if (list != null) {
			return (List<Integer>) list;
		} else {
			return new ArrayList<Integer>();
		}
		
	}
}
