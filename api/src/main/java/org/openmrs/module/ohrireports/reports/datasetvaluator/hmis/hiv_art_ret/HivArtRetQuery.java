package org.openmrs.module.ohrireports.reports.datasetvaluator.hmis.hiv_art_ret;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.ALIVE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.DIED;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.FOLLOW_UP_STATUS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.RESTART;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TRANSFERRED_IN;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.LOST_TO_FOLLOW_UP;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HivArtRetQuery extends PatientQueryImpDao {
	
	private static final int MONTHS_IN_YEAR = 12;
	
	@Autowired
	private DbSessionFactory sessionFactory;
	
	public Cohort getPatientRetentionCohort(String gender, Date startOnOrAfter, Date endOnOrBefore, Cohort cohort) {
		if (getSessionFactory() == null)
			this.setSessionFactory(sessionFactory);
		
		Calendar startDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		
		goBackToMonths(endOnOrBefore, startDate, endDate);
		Cohort artCohort = getOnArtCohorts(gender, startDate.getTime(), endDate.getTime(), cohort);
		if (artCohort == null || artCohort.size() == 0)
			return new Cohort();
		
		Cohort onTreatmentCohort = getCurrentOnTreatmentCohort(gender, endOnOrBefore, cohort);
		
		if (onTreatmentCohort == null || onTreatmentCohort.size() == 0)
			return new Cohort();
		
		StringBuilder sql = baseQuery(FOLLOW_UP_STATUS);
		
		sql.append("and p.person_id in (:artCohort) ");
		sql.append("and " + OBS_ALIAS + "person_id in (:onTreatmentPersonIds) ");
		sql.append("and " + OBS_ALIAS + "value_coded in (select concept_id from concept where uuid in (:activeIndicator)) ");
		if (gender != null && !gender.trim().isEmpty())
			sql.append("and p.gender = '" + gender + "' ");
		if (startOnOrAfter != null)
			sql.append(" and " + OBS_ALIAS + "obs_datetime >= :startOnOrAfter ");
		if (endOnOrBefore != null)
			sql.append(" and " + OBS_ALIAS + "obs_datetime <= :endOnOrBefore ");
		if (cohort != null && cohort.size() != 0)
			sql.append("and p.person_id in (:personIds) ");
		
		Query q = super.getSessionFactory().getCurrentSession().createSQLQuery(sql.toString());
		
		q.setParameter("onTreatmentPersonIds", onTreatmentCohort.getMemberIds());
		
		q.setParameter("activeIndicator", Arrays.asList(ALIVE, RESTART, TRANSFERRED_IN));
		q.setParameter("artCohort", artCohort.getMemberIds());
		if (startOnOrAfter != null)
			q.setTimestamp("startOnOrAfter", startDate.getTime());
		if (endOnOrBefore != null)
			q.setTimestamp("endOnOrBefore", endOnOrBefore);
		if (cohort != null && cohort.size() != 0)
			q.setParameter("personIds", cohort.getMemberIds());
		
		return new Cohort(q.list());
	}
	
	public Cohort getPatientRetentionCohortNet(String gender, Date startOnOrAfter, Date endOnOrBefore, Cohort cohort) {
		if (getSessionFactory() == null)
			this.setSessionFactory(sessionFactory);
		
		Calendar startDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		
		goBackToMonths(endOnOrBefore, startDate, endDate);
		Cohort artCohort = getOnArtCohorts(gender, startDate.getTime(), endDate.getTime(), cohort);
		if (artCohort == null || artCohort.size() == 0)
			return new Cohort();
		
		StringBuilder sql = baseQuery(FOLLOW_UP_STATUS);
		
		sql.append("and p.person_id in (:artCohort) ");
		sql.append("and " + OBS_ALIAS + "value_coded in (select concept_id from concept where uuid in (:activeIndicator)) ");
		if (gender != null && !gender.trim().isEmpty())
			sql.append("and p.gender = '" + gender + "' ");
		if (startOnOrAfter != null)
			sql.append(" and " + OBS_ALIAS + "obs_datetime >= :startOnOrAfter ");
		if (endOnOrBefore != null)
			sql.append(" and " + OBS_ALIAS + "obs_datetime <= :endOnOrBefore ");
		if (cohort != null && cohort.size() != 0)
			sql.append("and p.person_id in (:personIds) ");
		
		Query q = getSessionFactory().getCurrentSession().createSQLQuery(sql.toString());
		
		q.setParameter("activeIndicator", Arrays.asList(ALIVE, RESTART, LOST_TO_FOLLOW_UP, TRANSFERRED_IN, DIED));
		q.setParameter("artCohort", artCohort.getMemberIds());
		
		if (startOnOrAfter != null)
			q.setTimestamp("startOnOrAfter", startDate.getTime());
		if (endOnOrBefore != null)
			q.setTimestamp("endOnOrBefore", endOnOrBefore);
		if (cohort != null && cohort.size() != 0)
			q.setParameter("personIds", cohort.getMemberIds());
		
		return new Cohort(q.list());
	}
	
	public void goBackToMonths(Date endOnOrBefore, Calendar startDate, Calendar endDate) {
		startDate.setTime(endOnOrBefore);
		startDate.add(Calendar.MONTH, -MONTHS_IN_YEAR);
		int actualMaximum = startDate.getActualMaximum(Calendar.DATE);
		int month = startDate.get(Calendar.MONTH);
		endDate.set(startDate.get(Calendar.YEAR), month, actualMaximum);
		startDate
		        .set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.getActualMinimum(Calendar.DATE));
	}
}
