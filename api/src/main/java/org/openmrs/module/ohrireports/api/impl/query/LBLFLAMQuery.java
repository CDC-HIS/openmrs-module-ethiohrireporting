package org.openmrs.module.ohrireports.api.impl.query;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions.LF_LAM_RESULT;
import static org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions.TB_SCREENING_DATE;

@Component
public class LBLFLAMQuery extends PatientQueryImpDao {
	
	private DbSessionFactory sessionFactory;
	
	private Cohort cohort;
	
	public Cohort getCohort() {
		return cohort;
	}
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	private List<Integer> baseEncounter;
	
	public List<Integer> getBaseEncounter() {
		return baseEncounter;
	}
	
	@Autowired
	public LBLFLAMQuery(DbSessionFactory _SessionFactory) {
		sessionFactory = _SessionFactory;
		setSessionFactory(sessionFactory);
	}
	
	public void generateReport(Date start, Date end) {
		List<Integer> latestFollowUpEncounter = encounterQuery.getAliveFollowUpEncounters(null, end);
		baseEncounter = encounterQuery.getEncounters(Collections.singletonList(TB_SCREENING_DATE), start, end,
		    latestFollowUpEncounter);
		cohort = getActiveOnArtCohort("", null, end, null, baseEncounter);
		
	}
	
	public Cohort getByResult(String resultTypeConceptUUid) {
		StringBuilder sqlBuilder = new StringBuilder("select ob.person_id from obs as ob where ");
		sqlBuilder.append(" ob.concept_id =").append(conceptQuery(LF_LAM_RESULT));
		sqlBuilder.append(" and ob.value_coded =").append(conceptQuery(resultTypeConceptUUid));
		sqlBuilder.append(" and ob.person_id in (:personIDs) ");
		sqlBuilder.append(" and ob.encounter_id in (:encounters) ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
		
		query.setParameterList("personIDs", cohort.getMemberIds());
		query.setParameterList("encounters", baseEncounter);
		
		return new Cohort(query.list());
	}
	
}
