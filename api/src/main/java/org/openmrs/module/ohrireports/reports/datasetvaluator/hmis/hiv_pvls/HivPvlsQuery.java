package org.openmrs.module.ohrireports.reports.datasetvaluator.hmis.hiv_pvls;

import java.util.Calendar;
import java.util.Date;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HIV_VIRAL_LOAD_STATUS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HIV_VIRAL_LOAD_SUPPRESSED;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HIV_VIRAL_LOAD_COUNT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HIV_VIRAL_LOAD_LOW_LEVEL_VIREMIA;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*
* List of patient with viral load suppressed
* 
*/
@Component
public class HivPvlsQuery extends PatientQueryImpDao {
	
	private DbSessionFactory sessionFactory;
	
	private Date startDate, endDate;
	
	// -11 is because calendar library start count month from zero or less should be
	// -12
	private int STARTING_FROM_MONTHS = -11;
	
	@Autowired
	public HivPvlsQuery(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		super.setSessionFactory(sessionFactory);
	}
	
	private void setDate(Date start, Date end) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(end);
		calendar.add(Calendar.MONTH, STARTING_FROM_MONTHS);
		startDate = calendar.getTime();
		endDate = end;
	}
	
	public Cohort getPatientsWithViralLoadSuppressed(String gender, Date startOnOrAfter, Date endOnOrBefore) {
		setDate(startOnOrAfter, endOnOrBefore);
		Cohort artCohort = getActiveOnArtCohort(gender, startDate, endOnOrBefore, null);
		if (artCohort == null || artCohort.size() <= 0)
			return new Cohort();
		
		StringBuilder sql = super.baseQuery(HIV_VIRAL_LOAD_STATUS);
		sql.append("and " + OBS_ALIAS + "value_coded =(select distinct concept_id from concept where uuid='"
		        + HIV_VIRAL_LOAD_SUPPRESSED + "'  limit 1 )  ");
		
		sql.append("and p.person_id in (:artCohorts) ");
		Query query = addDateRange(sql);
		query.setParameter("artCohorts", artCohort.getMemberIds());
		
		return new Cohort(query.list());
	}
	
	private Query addDateRange(StringBuilder sql) {
		sql.append(" and " + OBS_ALIAS + "obs_datetime >= :startOnOrAfter ");
		sql.append("and " + OBS_ALIAS + "obs_datetime  <= :endOnOrBefore ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setTimestamp("startOnOrAfter", startDate);
		query.setTimestamp("endOnOrBefore", endDate);
		
		return query;
	}
	
	public Cohort getPatientWithViralLoadCount(String gender, Date startOnOrAfter, Date endOnOrBefore) {
		setDate(startOnOrAfter, endOnOrBefore);
		Cohort artCohort = getActiveOnArtCohort(gender, startDate, endOnOrBefore, null);
		if (artCohort == null || artCohort.size() <= 0)
			return new Cohort();
		
		StringBuilder sql = super.baseQuery(HIV_VIRAL_LOAD_COUNT);
		sql.append("and " + OBS_ALIAS + "value_numeric > 0 ");
		
		sql.append("and p.person_id in (:artCohorts) ");
		Query query = addDateRange(sql);
		query.setParameter("artCohorts", artCohort.getMemberIds());
		
		return new Cohort(query.list());
	}
	
	public Cohort getPatientWithViralLoadCountLowLevelViremia(String gender, Date startOnOrAfter, Date endOnOrBefore) {
		setDate(startOnOrAfter, endOnOrBefore);
		Cohort artCohort = getActiveOnArtCohort(gender, startDate, endOnOrBefore, null);
		if (artCohort == null || artCohort.size() <= 0)
			return new Cohort();
		
		StringBuilder sql = super.baseQuery(HIV_VIRAL_LOAD_STATUS);
		sql.append("and " + OBS_ALIAS + "value_coded = (select distinct concept_id from concept where uuid='"
		        + HIV_VIRAL_LOAD_LOW_LEVEL_VIREMIA + "' limit 1) ");
		
		sql.append("and p.person_id in (:artCohorts) ");
		Query query = addDateRange(sql);
		query.setParameter("artCohorts", artCohort.getMemberIds());
		
		return new Cohort(query.list());
	}
}
