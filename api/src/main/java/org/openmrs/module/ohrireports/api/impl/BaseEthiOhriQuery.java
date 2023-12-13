package org.openmrs.module.ohrireports.api.impl;

import org.openmrs.Cohort;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.HTS_FOLLOW_UP_ENCOUNTER_TYPE;

import java.util.HashMap;
import java.util.List;

public abstract class BaseEthiOhriQuery {
	
	protected String OBS_ALIAS = "ob.";
	
	protected String PERSON_BASE_ALIAS_OBS = "obp.";
	
	protected String CONCEPT_BASE_ALIAS_OBS = "obc.";
	
	protected String VALUE_DATE_BASE_ALIAS_OBS = "obvd.";
	
	protected String VALUE_NUMERIC_BASE_ALIAS_OBS = "obvnd.";
	
	protected String LATEST_ENCOUNTER_BASE_ALIAS_OBS = "obenc.";
	
	protected String SUB_QUERY_BASE_ALIAS_OBS = "obsq.";
	
	protected String SUB_QUERY_JOIN_BASE_ALIAS_OBS = "obsqj.";
	
	protected String PERSON_ID_ALIAS_OBS = "obpid.";
	
	protected String PERSON_ID_SUB_ALIAS_OBS = "obpidsub.";
	
	protected String CONCEPT_ALIAS = "c.";
	
	protected StringBuilder baseQuery(String conceptQuestionUUid) {
		
		StringBuilder sql = new StringBuilder();
		sql.append("select distinct " + OBS_ALIAS + "person_id from obs as ob ");
		sql.append("inner join patient as pa on pa.patient_id = " + OBS_ALIAS + "person_id ");
		sql.append("inner join person as p on pa.patient_id = p.person_id ");
		sql.append("inner join concept as c on c.concept_id = " + OBS_ALIAS + "concept_id and c.retired = false ");
		sql.append("and c.uuid= '" + conceptQuestionUUid + "' ");
		sql.append("inner join encounter as e on e.encounter_id = " + OBS_ALIAS + "encounter_id ");
		sql.append("inner join encounter_type as et on et.encounter_type_id = e.encounter_type ");
		sql.append("and et.uuid= '" + HTS_FOLLOW_UP_ENCOUNTER_TYPE + "' ");
		sql.append("where pa.voided = false and " + OBS_ALIAS + "voided = false ");
		return sql;
	}
	
	protected StringBuilder baseObsQuery(String conceptQuestionUUid) {
		
		StringBuilder sql = new StringBuilder();
		sql.append("select ob.* from obs as ob ");
		sql.append("inner join patient as pa on pa.patient_id = " + OBS_ALIAS + "person_id ");
		sql.append("inner join person as p on pa.patient_id = p.person_id ");
		sql.append("inner join concept as c on c.concept_id = " + OBS_ALIAS + "concept_id and c.retired = false ");
		sql.append("and c.uuid= '" + conceptQuestionUUid + "' ");
		sql.append("inner join encounter as e on e.encounter_id = " + OBS_ALIAS + "encounter_id ");
		sql.append("inner join encounter_type as et on et.encounter_type_id = e.encounter_type ");
		sql.append("and et.uuid= '" + HTS_FOLLOW_UP_ENCOUNTER_TYPE + "' ");
		sql.append("where pa.voided = false and " + OBS_ALIAS + "voided = false ");
		return sql;
	}
	
	protected StringBuilder basePersonIdQuery(String conceptQuestionUUid, String valueCoded) {
		
		StringBuilder sql = new StringBuilder();
		sql.append("select " + PERSON_BASE_ALIAS_OBS + "person_id from obs as  obp");
		sql.append(" inner join patient as pa on pa.patient_id = " + PERSON_BASE_ALIAS_OBS + "person_id");
		sql.append(" inner join person as p on pa.patient_id = p.person_id ");
		sql.append(" inner join concept as c on c.concept_id = " + PERSON_BASE_ALIAS_OBS
		        + "concept_id and c.retired = false ");
		sql.append(" and c.uuid= '" + conceptQuestionUUid + "' ");
		sql.append(" and " + PERSON_BASE_ALIAS_OBS + "value_coded =" + conceptQuery(valueCoded) + " ");
		sql.append(" inner join encounter as e on e.encounter_id = " + PERSON_BASE_ALIAS_OBS + "encounter_id ");
		sql.append(" inner join encounter_type as et on et.encounter_type_id = e.encounter_type ");
		sql.append(" and et.uuid= '" + HTS_FOLLOW_UP_ENCOUNTER_TYPE + "' ");
		sql.append(" where pa.voided = false and " + PERSON_BASE_ALIAS_OBS + "voided = false ");
		return sql;
	}
	
	protected StringBuilder basePersonIdQuery(String conceptQuestionUUid, List<String> answers) {
		
		StringBuilder sql = new StringBuilder();
		sql.append("select " + PERSON_BASE_ALIAS_OBS + "person_id from obs as  obp");
		sql.append(" inner join patient as pa on pa.patient_id = " + PERSON_BASE_ALIAS_OBS + "person_id");
		sql.append(" inner join person as p on pa.patient_id = p.person_id ");
		sql.append(" inner join concept as c on c.concept_id = " + PERSON_BASE_ALIAS_OBS
		        + "concept_id and c.retired = false ");
		sql.append(" and c.uuid= '" + conceptQuestionUUid + "' ");
		sql.append(" and " + PERSON_BASE_ALIAS_OBS + "value_coded in (" + conceptQuery(answers) + ")");
		sql.append(" inner join encounter as e on e.encounter_id = " + PERSON_BASE_ALIAS_OBS + "encounter_id ");
		sql.append(" inner join encounter_type as et on et.encounter_type_id = e.encounter_type ");
		sql.append(" and et.uuid= '" + HTS_FOLLOW_UP_ENCOUNTER_TYPE + "' ");
		sql.append(" where pa.voided = false and " + PERSON_BASE_ALIAS_OBS + "voided = false ");
		return sql;
	}
	
	protected StringBuilder baseConceptQuery(String conceptQuestionUUid) {
		
		StringBuilder sql = new StringBuilder();
		sql.append("select " + CONCEPT_BASE_ALIAS_OBS + "person_id,cn.name from obs as  obc");
		sql.append(" inner join patient as pa on pa.patient_id = " + CONCEPT_BASE_ALIAS_OBS + "person_id ");
		sql.append(" inner join person as p on pa.patient_id = p.person_id ");
		sql.append(" inner join concept as c on c.concept_id = " + CONCEPT_BASE_ALIAS_OBS + " concept_id  ");
		sql.append(" inner join concept_name as cn on cn.concept_id = " + CONCEPT_BASE_ALIAS_OBS
		        + " value_coded and cn.locale_preferred =1 and cn.locale='en' ");
		sql.append(" inner join encounter as e on e.encounter_id = " + CONCEPT_BASE_ALIAS_OBS + "encounter_id ");
		sql.append(" inner join encounter_type as et on et.encounter_type_id = e.encounter_type ");
		sql.append(" and et.uuid= '" + HTS_FOLLOW_UP_ENCOUNTER_TYPE + "' ");
		sql.append(" where obc.concept_id =" + conceptQuery(conceptQuestionUUid));
		return sql;
	}
	
	protected StringBuilder baseConceptCountQuery(String conceptQuestionUUid) {
		
		StringBuilder sql = new StringBuilder();
		sql.append("select COUNT( distinct(" + CONCEPT_BASE_ALIAS_OBS + "person_id))," + CONCEPT_BASE_ALIAS_OBS
		        + "value_coded from obs as  obc");
		sql.append(" inner join patient as pa on pa.patient_id = ").append(CONCEPT_BASE_ALIAS_OBS).append("person_id ");
		sql.append(" inner join person as p on pa.patient_id = p.person_id ");
		sql.append(" inner join concept as c on c.concept_id = ").append(CONCEPT_BASE_ALIAS_OBS)
		        .append(" concept_id and c.retired = false ");
		sql.append(" and c.uuid= '" + conceptQuestionUUid + "' ");
		sql.append(" inner join encounter as e on e.encounter_id = " + CONCEPT_BASE_ALIAS_OBS + "encounter_id ");
		sql.append(" inner join encounter_type as et on et.encounter_type_id = e.encounter_type ");
		sql.append(" and et.uuid= '" + HTS_FOLLOW_UP_ENCOUNTER_TYPE + "' ");
		sql.append(" where pa.voided = false and " + CONCEPT_BASE_ALIAS_OBS + "voided = false ");
		return sql;
	}
	
	protected StringBuilder baseValueDateQuery(String conceptQuestionUUid) {
		
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
		sql.append(" and et.uuid= '" + HTS_FOLLOW_UP_ENCOUNTER_TYPE + "' ");
		sql.append(" where pa.voided = false and " + VALUE_DATE_BASE_ALIAS_OBS + "voided = false ");
		return sql;
	}
	
	protected StringBuilder baseValueNumberQuery(String conceptQuestionUUid) {
		
		StringBuilder sql = new StringBuilder();
		sql.append("select " + VALUE_NUMERIC_BASE_ALIAS_OBS + "person_id," + VALUE_NUMERIC_BASE_ALIAS_OBS
		        + "value_numeric from obs as  obvnd ");
		sql.append(" inner join patient as pa on pa.patient_id = " + VALUE_NUMERIC_BASE_ALIAS_OBS + "person_id ");
		sql.append(" inner join person as p on pa.patient_id = p.person_id ");
		sql.append(" inner join concept as c on c.concept_id = " + VALUE_NUMERIC_BASE_ALIAS_OBS
		        + " concept_id and c.retired = false ");
		sql.append(" and c.uuid= '" + conceptQuestionUUid + "' ");
		sql.append(" inner join encounter as e on e.encounter_id = " + VALUE_NUMERIC_BASE_ALIAS_OBS + "encounter_id ");
		sql.append(" inner join encounter_type as et on et.encounter_type_id = e.encounter_type ");
		sql.append(" and et.uuid= '" + HTS_FOLLOW_UP_ENCOUNTER_TYPE + "' ");
		sql.append(" where pa.voided = false and " + VALUE_NUMERIC_BASE_ALIAS_OBS + "voided = false ");
		return sql;
	}
	
	protected StringBuilder personIdQuery(String conditions, String outerQuery) {
		StringBuilder sql = new StringBuilder();
		sql.append("select distinct " + PERSON_ID_ALIAS_OBS + " person_id from obs as obpid" + " where encounter_id in ");
		sql.append("(select Max(" + PERSON_ID_SUB_ALIAS_OBS + "encounter_id) from obs as obpidsub where ");
		sql.append(conditions);
		sql.append(" GROUP BY " + PERSON_ID_SUB_ALIAS_OBS + " person_id)");
		
		if (!outerQuery.isEmpty()) {
			sql.append(outerQuery);
		}
		return sql;
		
	}
	
	protected String conceptQuery(String uuid) {
		return "(select concept_id from concept where uuid ='" + uuid + "' limit 1 )";
	}
	
	protected String conceptQuery(List<String> uuids) {
		return "(select concept_id from concept where uuid in ('" + String.join("','", uuids) + "'))";
	}
	
	protected StringBuilder baseSubQuery(String condition) {
		StringBuilder sql = new StringBuilder("(select MAX(" + SUB_QUERY_BASE_ALIAS_OBS + "obs_id) from obs as obsq where "
		        + SUB_QUERY_BASE_ALIAS_OBS + "voided=false ");
		if (condition == null || condition.isEmpty()) {
			return sql.append("  group by " + SUB_QUERY_BASE_ALIAS_OBS + " person_id )");
			
		}
		
		return sql.append(" and " + condition + "  group by " + SUB_QUERY_BASE_ALIAS_OBS + " person_id) ");
		
	}
	
	protected StringBuilder baseLatestEncounter(String condition) {
		StringBuilder sql = new StringBuilder("(select MAX(" + LATEST_ENCOUNTER_BASE_ALIAS_OBS
		        + "encounter_id) from obs as obenc  where " + LATEST_ENCOUNTER_BASE_ALIAS_OBS + "voided=false ");
		if (condition == null || condition.isEmpty()) {
			return sql.append("  group by " + LATEST_ENCOUNTER_BASE_ALIAS_OBS + "person_id )");
			
		}
		
		return sql.append(" and " + condition + "  group by " + LATEST_ENCOUNTER_BASE_ALIAS_OBS + "person_id) ");
		
	}
	
	protected StringBuilder baseSubQueryJoin(String query, String joinQuery) {
		StringBuilder sql = new StringBuilder();
		if (query == null || query.isEmpty())
			return sql.append("(select MAX(" + SUB_QUERY_JOIN_BASE_ALIAS_OBS + "obs_id) from obs as obsqj " + joinQuery
			        + " where " + SUB_QUERY_JOIN_BASE_ALIAS_OBS + "voided=false  group by " + SUB_QUERY_JOIN_BASE_ALIAS_OBS
			        + " person_id )");
		
		return sql.append("(select MAX(" + SUB_QUERY_JOIN_BASE_ALIAS_OBS + "obs_id) from obs as obsqj " + joinQuery
		        + " where " + SUB_QUERY_JOIN_BASE_ALIAS_OBS + "voided=false and " + query + "  group by "
		        + SUB_QUERY_JOIN_BASE_ALIAS_OBS + " person_id)");
		
	}
	
}
