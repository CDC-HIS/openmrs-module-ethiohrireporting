package org.openmrs.module.ohrireports.datasetevaluator.linelist.PreExposureProphylaxis;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.query.ObsElement;
import org.openmrs.module.ohrireports.constants.EncounterType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class PreExposureProphylaxisLineListQuery extends ObsElement {
	
	private final DbSessionFactory sessionFactory;
	
	public PreExposureProphylaxisLineListQuery(DbSessionFactory _SessionFactory, DbSessionFactory sessionFactory) {
		super(_SessionFactory);
		this.sessionFactory = sessionFactory;
	}
	
	public HashMap<Integer, Object> getObsValueDate(List<Integer> baseEncounters, String concept, Cohort cohort,
	        String encounterTypeUUid) {
		return getDictionary(getObsValueDateQuery(baseEncounters, concept, cohort, encounterTypeUUid));
	}
	
	public HashMap<Integer, Object> getScreeningObsValueDate(String concept, Cohort cohort, List<Integer> encounters) {
		StringBuilder stringQuery = baseValueDateQuery(concept, EncounterType.PREP_SCREENING_ENCOUNTER_TYPE);
		stringQuery.append(" and  ").append(VALUE_DATE_BASE_ALIAS_OBS).append("person_id in (:cohorts) ");
		stringQuery.append(" and  ").append(VALUE_DATE_BASE_ALIAS_OBS).append("encounter_id in (:encounters) ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringQuery.toString());
		
		query.setParameterList("cohorts", cohort.getMemberIds());
		query.setParameterList("encounters", encounters);
		
		return getDictionary(query);
	}
	
	protected Query getObsValueDateQuery(List<Integer> baseEncounters, String concept, Cohort cohort,
	        String encounterTypeUUid) {
		StringBuilder sql = baseValueDateQuery(concept, encounterTypeUUid);
		
		sql.append(" and  " + VALUE_DATE_BASE_ALIAS_OBS + "encounter_id in (:encounters) ");
		sql.append(" and  " + VALUE_DATE_BASE_ALIAS_OBS + "person_id in (:cohorts) ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("encounters", baseEncounters);
		query.setParameterList("cohorts", cohort.getMemberIds());
		
		return query;
	}
	
	protected StringBuilder baseValueDateQuery(String conceptQuestionUUid, String encounterTypeUUid) {
		
		StringBuilder sql = new StringBuilder();
		sql.append("select " + VALUE_DATE_BASE_ALIAS_OBS + "person_id," + VALUE_DATE_BASE_ALIAS_OBS
		        + "value_datetime from obs as  obvd ");
		sql.append(" inner join patient as pa on pa.patient_id = " + VALUE_DATE_BASE_ALIAS_OBS + "person_id ");
		sql.append(" inner join person as p on pa.patient_id = p.person_id ");
		sql.append(" inner join concept as c on c.concept_id = " + VALUE_DATE_BASE_ALIAS_OBS
		        + " concept_id and c.retired = false ");
		sql.append(" and c.uuid= '" + conceptQuestionUUid + "' ");
		sql.append(" inner join encounter as e on e.encounter_id = " + VALUE_DATE_BASE_ALIAS_OBS + "encounter_id ");
		sql.append(" inner join encounter_type as et on et.encounter_type_id = e.encounter_type ");
		sql.append(" and et.uuid= '" + encounterTypeUUid + "' ");
		sql.append(" where pa.voided = false and " + VALUE_DATE_BASE_ALIAS_OBS + "voided = false ");
		return sql;
	}
	
	public HashMap<Integer, Object> getConceptValue(String concept, Cohort cohort, String encounterTypeUUid,
	        List<Integer> encounters) {
		return getDictionary(getObsValue(concept, cohort, encounterTypeUUid, encounters));
	}
	
	protected Query getObsValue(String concept, Cohort cohort, String encounterTypeUUid, List<Integer> encounters) {
		StringBuilder sql = baseConceptValueQuery(concept, encounterTypeUUid);
		
		sql.append(" and  ").append(CONCEPT_BASE_ALIAS_OBS).append("person_id in (:cohorts) ");
		sql.append(" and  ").append(CONCEPT_BASE_ALIAS_OBS).append("encounter_id in (:encounter) ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("cohorts", cohort.getMemberIds());
		query.setParameterList("encounter", encounters);
		
		return query;
	}
	
	protected StringBuilder baseConceptValueQuery(String conceptQuestionUUid, String encounterTypeUUid) {
		
		StringBuilder sql = new StringBuilder();
		sql.append("select " + CONCEPT_BASE_ALIAS_OBS + "person_id,obc.value_text from obs as  obc");
		sql.append(" inner join patient as pa on pa.patient_id = " + CONCEPT_BASE_ALIAS_OBS + "person_id ");
		sql.append(" inner join person as p on pa.patient_id = p.person_id ");
		sql.append(" inner join concept as c on c.concept_id = " + CONCEPT_BASE_ALIAS_OBS + " concept_id  ");
		sql.append(" inner join encounter as e on e.encounter_id = " + CONCEPT_BASE_ALIAS_OBS + "encounter_id ");
		sql.append(" inner join encounter_type as et on et.encounter_type_id = e.encounter_type ");
		sql.append(" and et.uuid= '" + encounterTypeUUid + "' ");
		sql.append(" where obc.concept_id =" + conceptQuery(conceptQuestionUUid));
		return sql;
	}
	
	public HashMap<Integer, Object> getConceptName(String concept, Cohort cohort, String encounterTypeUUid) {
		return getDictionary(getObs(concept, cohort, encounterTypeUUid));
	}
	
	protected Query getObs(String concept, Cohort cohort, String encounterTypeUUid) {
		StringBuilder sql = baseConceptQuery(concept, encounterTypeUUid);
		
		sql.append(" and  " + CONCEPT_BASE_ALIAS_OBS + "person_id in (:cohorts) ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("cohorts", cohort.getMemberIds());
		
		return query;
	}
	
	public HashMap<Integer, Object> getConceptName(List<Integer> baseEncounters, String concept, Cohort cohort,
	        String encounterTypeUUid) {
		return getDictionary(getObs(baseEncounters, concept, cohort, encounterTypeUUid));
	}
	
	public HashMap<Integer, Object> getConceptLabel(List<Integer> baseEncounters, String concept, Cohort cohort,
	        String encounterTypeUUid) {
		return getPrepLabelDictionary(getObsWithUuid(baseEncounters, concept, cohort, encounterTypeUUid));
	}
	
	public Query getObsWithUuid(List<Integer> baseEncounters, String concept, Cohort cohort, String encounterTypeUUid) {
		StringBuilder stringQuery = baseConceptUUIDQuery(concept, encounterTypeUUid);
		stringQuery.append(" and ");
		stringQuery.append(CONCEPT_BASE_ALIAS_OBS).append("encounter_id in (:encounter) ");
		stringQuery.append(" and ");
		stringQuery.append(CONCEPT_BASE_ALIAS_OBS).append("person_id in (:personIds) ");
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringQuery.toString());
		query.setParameterList("encounter", baseEncounters);
		query.setParameterList("personIds", cohort.getMemberIds());
		return query;
	}
	
	public Query getObs(List<Integer> baseEncounters, String concept, Cohort cohort, String encounterTypeUUid) {
		StringBuilder stringQuery = baseConceptQuery(concept, encounterTypeUUid);
		stringQuery.append(" and ");
		stringQuery.append(CONCEPT_BASE_ALIAS_OBS).append("encounter_id in (:encounter) ");
		stringQuery.append(" and ");
		stringQuery.append(CONCEPT_BASE_ALIAS_OBS).append("person_id in (:personIds) ");
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringQuery.toString());
		query.setParameterList("encounter", baseEncounters);
		query.setParameterList("personIds", cohort.getMemberIds());
		return query;
	}
	
	protected StringBuilder baseConceptQuery(String conceptQuestionUUid, String encounterTypeUUid) {
		
		StringBuilder sql = new StringBuilder();
		sql.append("select ").append(CONCEPT_BASE_ALIAS_OBS).append("person_id,cn.name from obs as  obc");
		sql.append(" inner join patient as pa on pa.patient_id = ").append(CONCEPT_BASE_ALIAS_OBS).append("person_id ");
		sql.append(" inner join person as p on pa.patient_id = p.person_id ");
		sql.append(" inner join concept as c on c.concept_id = ").append(CONCEPT_BASE_ALIAS_OBS).append(" concept_id  ");
		sql.append(" inner join concept_name as cn on cn.concept_id = ").append(CONCEPT_BASE_ALIAS_OBS)
		        .append(" value_coded and cn.locale_preferred =1 and cn.locale='en' ");
		sql.append(" inner join encounter as e on e.encounter_id = ").append(CONCEPT_BASE_ALIAS_OBS).append("encounter_id ");
		sql.append(" inner join encounter_type as et on et.encounter_type_id = e.encounter_type ");
		sql.append(" and et.uuid= '").append(encounterTypeUUid).append("' ");
		sql.append(" where obc.concept_id =").append(conceptQuery(conceptQuestionUUid)).append(" and obc.voided = false ");
		return sql;
	}
	
	protected StringBuilder baseConceptUUIDQuery(String conceptQuestionUUid, String encounterTypeUUid) {
		
		StringBuilder sql = new StringBuilder();
		sql.append("select ").append(CONCEPT_BASE_ALIAS_OBS).append("person_id,c.uuid from obs as  obc");
		sql.append(" inner join patient as pa on pa.patient_id = ").append(CONCEPT_BASE_ALIAS_OBS).append("person_id ");
		sql.append(" inner join person as p on pa.patient_id = p.person_id ");
		sql.append(" inner join concept as c on c.concept_id = ").append(CONCEPT_BASE_ALIAS_OBS).append(" value_coded  ");
		sql.append(" inner join encounter as e on e.encounter_id = ").append(CONCEPT_BASE_ALIAS_OBS).append("encounter_id ");
		sql.append(" inner join encounter_type as et on et.encounter_type_id = e.encounter_type ");
		sql.append(" and et.uuid= '").append(encounterTypeUUid).append("' ");
		sql.append(" where obc.concept_id =").append(conceptQuery(conceptQuestionUUid)).append(" and obc.voided = false ");
		return sql;
	}
	
}
