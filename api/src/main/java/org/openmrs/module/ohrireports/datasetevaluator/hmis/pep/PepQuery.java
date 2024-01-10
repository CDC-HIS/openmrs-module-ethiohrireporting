package org.openmrs.module.ohrireports.datasetevaluator.hmis.pep;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Component
public class PepQuery extends PatientQueryImpDao {
	
	private DbSessionFactory sessionFactory;
	
	public List<Integer> getBaseEncounter() {
		return baseEncounter;
	}
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	public Cohort getBaseCohort() {
		return baseCohort;
	}
	
	private List<Integer> baseEncounter;
	
	private Cohort baseCohort;
	
	@Autowired
	public PepQuery(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		setSessionFactory(sessionFactory);
	}
	
	public void generateReport(Date start, Date end) {
		baseEncounter = encounterQuery.getEncounters(Collections.singletonList(PEP_REGISTRATION_DATE), start, end,
		    PEP_ENCOUNTER_TYPE);
		baseCohort = getCohort(baseEncounter);
	}
	
	public Integer getCountByExposureType(String uuid) {
		StringBuilder sqlBuilder = new StringBuilder("select person_id from obs as ob where ");
		sqlBuilder.append(" ob.concept_id = ").append(conceptQuery(EXPOSURE_TYPE));
		sqlBuilder.append(" and ob.value_coded = ").append(conceptQuery(uuid));
		sqlBuilder.append(" and ob.person_id in (:personIds) ");
		sqlBuilder.append(" and ob.encounter_id in (:encounterIds) ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
		
		query.setParameterList("personIds", baseCohort.getMemberIds());
		query.setParameterList("encounterIds", baseEncounter);
		return query.list().size();
		
	}
}
