package org.openmrs.module.ohrireports.api.impl;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.HTS_FOLLOW_UP_ENCOUNTER_TYPE;

public abstract class BaseEthiOhriQuery {
	
	protected String OBS_ALIAS = "ob.";
	
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
	
	protected StringBuilder baseConceptQuery(String conceptQuestionUUid) {
		
		StringBuilder sql = new StringBuilder();
		sql.append("select ob.person_id,cn.name from obs as ob ");
		sql.append("inner join patient as pa on pa.patient_id = " + OBS_ALIAS + "person_id ");
		sql.append("inner join person as p on pa.patient_id = p.person_id ");
		sql.append("inner join concept as c on c.concept_id = " + OBS_ALIAS + "concept_id and c.retired = false ");
		sql.append("inner join concept_name as cn on cn.concept_id = " + OBS_ALIAS
		        + "value_coded and cn.locale_preferred =1 and cn.locale='en' ");
		sql.append("and c.uuid= '" + conceptQuestionUUid + "' ");
		sql.append("inner join encounter as e on e.encounter_id = " + OBS_ALIAS + "encounter_id ");
		sql.append("inner join encounter_type as et on et.encounter_type_id = e.encounter_type ");
		sql.append("and et.uuid= '" + HTS_FOLLOW_UP_ENCOUNTER_TYPE + "' ");
		sql.append("where pa.voided = false and " + OBS_ALIAS + "voided = false ");
		return sql;
	}
	
	protected StringBuilder baseValueDateQuery(String conceptQuestionUUid) {
		
		StringBuilder sql = new StringBuilder();
		sql.append("select ob.person_id,ob.value_datetime from obs as ob ");
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
	
	protected StringBuilder personIdQuery(String conditions, String outerQuery) {
		
		StringBuilder sql = new StringBuilder();
		
		sql.append("select distinct person_id from obs as ob where encounter_id in ");
		sql.append("(select Max(encounter_id) from obs where ");
		sql.append(conditions);
		sql.append(" GROUP BY person_id)");
		
		if (!outerQuery.isEmpty()) {
			sql.append(outerQuery);
		}
		return sql;
		
	}
	
	protected String conceptQuery(String uuid) {
		return "(select concept_id from concept where uuid ='" + uuid + "' limit 1 )";
	}
	
	protected StringBuilder baseSubQuery(String query) {
		StringBuilder sql = new StringBuilder();
		if (query == null || query.isEmpty())
			return sql.append("(select MAX(ob.obs_id) from obs as ob where ob.voided=false;   group by person_id )");
		
		return sql.append("(select MAX(ob.obs_id) from obs as ob where " + query + "  group by person_id)");
		
	}
	
	protected StringBuilder baseSubQueryJoin(String query, String joinQuery) {
		StringBuilder sql = new StringBuilder();
		if (query == null || query.isEmpty())
			return sql.append("(select MAX(obs_id) from obs as ob " + joinQuery
			        + " where ob.voided=false  group by person_id )");
		
		return sql.append("(select MAX(obs_id) from obs as ob " + joinQuery + " where ob.voided=false and " + query
		        + "  group by person_id)");
		
	}
}
