package org.openmrs.module.ohrireports.api.impl;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.HTS_FOLLOW_UP_ENCOUNTER_TYPE;

public abstract class BaseEthiOhriQuery {
	
	protected String OBS_ALIAS = "ob.";
	
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
}
