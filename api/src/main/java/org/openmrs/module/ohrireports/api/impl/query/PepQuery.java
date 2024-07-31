package org.openmrs.module.ohrireports.api.impl.query;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.constants.EncounterType;
import org.openmrs.module.ohrireports.constants.PostExposureConceptQuestions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

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
	
	public List<Integer> getPepFollowUpEncounter() {
		return pepFollowUpEncounter;
	}
	
	private List<Integer> pepFollowUpEncounter;
	
	private Cohort baseCohort;
	
	@Autowired
	public PepQuery(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		setSessionFactory(sessionFactory);
	}
	
	public void generateReport(Date start, Date end) {
		baseEncounter = encounterQuery.getEncounters(
		    Collections.singletonList(PostExposureConceptQuestions.POST_REPORTING_DATE), start, end,
		    EncounterType.PEP_REGISTRATION_ENCOUNTER_TYPE);
		pepFollowUpEncounter = getPepFollowupEncounters();
		baseCohort = getCohort(baseEncounter);
	}
	
	private List<Integer> getPepFollowupEncounters(){
		StringBuilder sqlBuilder = new StringBuilder("select ob.encounter_id from obs as ob inner join encounter as e on e.encounter_id = ob.encounter_id");
		 sqlBuilder.append(" inner join encounter_type as et on et.encounter_type_id = e.encounter_type and et.uuid='")
				 .append(EncounterType.PEP_FOLLOWUP_ENCOUNTER).append("' ");
		sqlBuilder.append(" and ob.concept_id =").append(conceptQuery(PostExposureConceptQuestions.POST_REPORTING_DATE));
		sqlBuilder.append("  where ob.value_datetime = (select r.value_datetime from obs as r where r.person_id = ob.person_id and r.concept_id= ")
				.append(conceptQuery(PostExposureConceptQuestions.POST_REPORTING_DATE));
		sqlBuilder.append(" and r.encounter_id in (:encounter) and r.encounter_id = ob.encounter_id limit 1)");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
		query.setParameterList("encounter",baseEncounter);
		List list = query.list();
		
		if(Objects.nonNull(list))
		{
			return (List<Integer>) list;
		}
		return new ArrayList<>();
	}
	
	public Integer getCountByExposureType(String uuid) {
		StringBuilder sqlBuilder = new StringBuilder("select person_id from obs as ob where ");
		sqlBuilder.append(" ob.concept_id = ").append(conceptQuery(PostExposureConceptQuestions.PEP_EXPOSURE_TYPE));
		sqlBuilder.append(" and ob.value_coded = ").append(conceptQuery(uuid));
		sqlBuilder.append(" and ob.person_id in (:personIds) ");
		sqlBuilder.append(" and ob.encounter_id in (:encounterIds) ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
		
		query.setParameterList("personIds", baseCohort.getMemberIds());
		query.setParameterList("encounterIds", baseEncounter);
		return query.list().size();
		
	}
}
