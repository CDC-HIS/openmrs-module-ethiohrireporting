package org.openmrs.module.ohrireports.datasetevaluator.linelist.missedAppointments;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.api.impl.query.BaseLineListQuery;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.cohorts.util.EthiOhriUtil;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISUtilies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Component
public class MissedAppointmentQuery extends BaseLineListQuery {
	
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
	public MissedAppointmentQuery(DbSessionFactory _SessionFactory) {
		super(_SessionFactory);
		sessionFactory = _SessionFactory;
	}
	
	public void generateReport(Date start, Date end) {
		encounter = encounterQuery.getEncounters(Collections.singletonList(NEXT_VISIT_DATE), start, end,
		    HTS_FOLLOW_UP_ENCOUNTER_TYPE);
		List<Integer> visitEncounter = getEncounter(start, end);
		Cohort visitedCohort = getCohort(visitEncounter);
		baseCohort = getCohort(encounter);
		baseCohort = HMISUtilies.getOuterUnion(baseCohort, visitedCohort);
		
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
	
	public List<Integer> getEncounter(Date start, Date end) {
		List<Integer> allEncounters = encounterQuery.getEncounters(Collections.singletonList(FOLLOW_UP_DATE), start, end);
		
		String builder = "select ob.encounter_id from obs as ob" + " where ob.concept_id =" + conceptQuery(FOLLOW_UP_STATUS)
		        + " and ob.value_coded in " + conceptQuery(Arrays.asList(RESTART, ALIVE, STOP))
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
