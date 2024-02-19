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
	
	public void generateReport(Date reportEnd) {
		Cohort hasFollowupAfterEndCohort = getCohortHasFollowUpAfterDate(reportEnd);
		encounter = encounterQuery.getAliveFollowUpEncounters(null, reportEnd);
		Cohort visitedCohort = getCohort(encounter);
		baseCohort = HMISUtilies.getLeftOuterUnion(visitedCohort, hasFollowupAfterEndCohort);
		baseCohort = getMissideCohort(reportEnd);
	}
	
	private Cohort getMissideCohort(Date reportEnd) {
		StringBuilder sqlBuilder = new StringBuilder("select ob.person_id from obs as ob ");
		sqlBuilder.append(" where ob.encounter_id in (:encounter) ");
		sqlBuilder.append(" and ob.concept_id= ").append(conceptQuery(NEXT_VISIT_DATE));
		sqlBuilder.append(" and ob.person_id in (:personId) ");
		sqlBuilder.append(" and DATEDIFF(:endDate, ob.value_datetime)>0");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
		query.setParameterList("personId", baseCohort.getMemberIds());
		query.setParameterList("encounter", encounter);
		query.setDate("endDate", reportEnd);
		
		return new Cohort(query.list());
	}
	
	private Cohort getCohortHasFollowUpAfterDate(Date reportEnd) {
		List<Integer> afterDateEncounter = encounterQuery.getEncounters(Collections.singletonList(FOLLOW_UP_DATE),
		    reportEnd, null, HTS_FOLLOW_UP_ENCOUNTER_TYPE);
		return getCohort(afterDateEncounter);
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
