package org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_art_intr;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.TREATMENT_END_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.FOLLOW_UP_STATUS;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.BaseEthiOhriQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HivArtIntrQuery extends BaseEthiOhriQuery {
	
	private DbSessionFactory sessionFactory;
	
	private Date startDate, endDate;
	
	public void initialize(Date _startDate, Date _endDate) {
		startDate = _startDate;
		endDate = _endDate;
	}
	
	@Autowired
	public HivArtIntrQuery(DbSessionFactory _dbSessionFactory) {
		super();
		sessionFactory = _dbSessionFactory;
	}
	
	/*
	 * Get all patient whose lost to follow up treatment end date is less than
	 * reporting end date
	 */
	public Set<Integer> getAllPatientExceedsTreatmentEndDate(Range range, String status) {
		Set<Integer> patientId = new HashSet<>();
		Query lostQuery = lostToFollowUpWithNoFollowUp(range);
		Query lostStatusQuery = lostToFollowUpWithStatus(range, status);

		patientId.addAll(lostQuery.list());
		patientId.addAll(lostStatusQuery.list());
		return patientId;
	}
	
	private Query lostToFollowUpWithStatus(Range range, String status) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(endDate);
		// Note: calendar library month start from zero that's why we use -4
		calendar.add(Calendar.MONTH, -4);
		
		StringBuilder sql = new StringBuilder();
		sql.append("select " + OBS_ALIAS + "person_id from obs as ob where obs_id in ");
		String sqlJoin = "inner join concept as c on c.concept_id = " + OBS_ALIAS + "concept_id and c.uuid ='"
		        + FOLLOW_UP_STATUS + "'";
		// e.g if less than three month
		if (range == Range.LESS_THAN_THREE_MONTH) {
			sql.append(baseSubQueryJoin(" obs_datetime >=:startOnOrBefore and obs_datetime <:endOnBefore  and " + OBS_ALIAS
			        + "value_coded =(select concept_id from concept where uuid ='" + status + "' limit 1) ", sqlJoin));
			
			// above three month
		} else {
			sql.append(baseSubQueryJoin("  obs_datetime < :startOnOrBefore   and " + OBS_ALIAS
			        + "value_coded =(select concept_id from concept where uuid ='" + status + "' limit 1)", sqlJoin));
		}
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		query.setTimestamp("startOnOrBefore", calendar.getTime());
		if (range == Range.LESS_THAN_THREE_MONTH)
			query.setTimestamp("endOnBefore", endDate);
		return query;
	}
	
	private Query lostToFollowUpWithNoFollowUp(Range range) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(endDate);
		// Note: calendar library month start from zero that's why we use -4
		calendar.add(Calendar.MONTH, -4);
		
		StringBuilder sql = new StringBuilder();
		sql.append("select " + OBS_ALIAS + "person_id from obs as ob where obs_id in ");
		String sqlJoin = "inner join concept as c on c.concept_id = " + OBS_ALIAS + "concept_id and c.uuid ='"
		        + TREATMENT_END_DATE + "'";
		// e.g if less than three month
		if (range == Range.LESS_THAN_THREE_MONTH) {
			sql.append(baseSubQueryJoin(" value_datetime >=:startOnOrBefore and value_datetime<:endOnBefore ", sqlJoin));
			// above three month
		} else {
			sql.append(baseSubQueryJoin("  value_datetime < :startOnOrBefore", sqlJoin));
		}
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		query.setTimestamp("startOnOrBefore", calendar.getTime());
		if (range == Range.LESS_THAN_THREE_MONTH)
			query.setTimestamp("endOnBefore", endDate);
		return query;
	}
	
	public Set<Integer> getPatientByFollowUpStatus(String status) {
		StringBuilder sql = baseQuery(FOLLOW_UP_STATUS);
		sql.append(
				" and " + OBS_ALIAS + "value_coded = (select concept_id from concept where uuid ='" + status
						+ "' limit 1) ");
		sql.append(" and ob.obs_datetime>=:startOnOrAfter and ob.obs_datetime<=:endOnOrBefore ");
		sql.append("and " + OBS_ALIAS + "obs_id in ");

		String sqlJoin = "inner join concept as c on c.concept_id = " + OBS_ALIAS + "concept_id and c.uuid ='"
				+ FOLLOW_UP_STATUS + "' ";

		String sqlQuery = OBS_ALIAS + "value_coded = (select concept_id from concept where uuid ='" + status
				+ "' limit 1) and ob.obs_datetime>=:start and ob.obs_datetime<=:end ";
		sql.append(baseSubQueryJoin(sqlQuery, sqlJoin));

		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());

		query.setDate("start", startDate);
		query.setDate("startOnOrAfter", startDate);
		query.setDate("end", endDate);
		query.setDate("endOnOrBefore", endDate);

		return new HashSet<>(query.list());

	}
	
	enum Range {
		NONE, ABOVE_THREE_MONTH, LESS_THAN_THREE_MONTH, ABOVE_OR_EQUAL_TO_FIFTY, LESS_THAN_FIFTY
	}
}
