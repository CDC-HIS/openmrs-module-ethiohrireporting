package org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_pvls;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.HIV_VIRAL_LOAD_STATUS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HIV_VIRAL_LOAD_SUPPRESSED;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HIV_VIRAL_LOAD_COUNT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HIV_VIRAL_LOAD_LOW_LEVEL_VIREMIA;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.DATE_VIRAL_TEST_RESULT_RECEIVED;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.api.impl.query.VlQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*
* List of patient with viral load suppressed
* 
*/
@Component
public class HivPvlsQuery extends PatientQueryImpDao {
	
	private DbSessionFactory sessionFactory;
	
	private Date startDate, endDate = new Date();
	
	@Autowired
	private VlQuery vlQuery;
	
	/*
	 * -11 is because calendar library start count month from zero,
	 * the idea is to check all record from past twelve months
	 */
	private int STARTING_FROM_MONTHS = 12;
	
	@Autowired
	public HivPvlsQuery(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		super.setSessionFactory(sessionFactory);
	}
	
	private void setDate(Date end) {
		
		if (endDate != end) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(end);
			calendar.add(Calendar.MONTH, -STARTING_FROM_MONTHS);
			startDate = calendar.getTime();
			endDate = end;
			List<Integer> lastEncounterIds = getBaseEncounters(DATE_VIRAL_TEST_RESULT_RECEIVED, startDate, endDate);
			vlQuery.loadInitialCohort(startDate, endDate, lastEncounterIds);
		}
		
	}
	
	public Cohort getPatientsWithViralLoadSuppressed(String gender, Date endOnOrBefore) {
		setDate(endOnOrBefore);
		Cohort cohort = vlQuery.getViralLoadSuppressed();
		
		if (Objects.isNull(gender) || gender.isEmpty())
			return cohort;
		
		return filterByGender(cohort, gender);
	}
	
	private Cohort filterByGender(Cohort cohort, String gender) {
		StringBuilder sql = new StringBuilder("select person_id from person where person_id in (:cohorts) and gender = '"
		        + gender + "'");
		Query q = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		q.setParameterList("cohorts", cohort.getMemberIds());
		
		return new Cohort(q.list());
	}
	
	public Cohort getPatientWithViralLoadCount(String gender, Date endOnOrBefore) {
		setDate(endOnOrBefore);
		if (gender == null || gender.isEmpty())
			return vlQuery.cohort;
		
		StringBuilder sql = super.baseQuery(HIV_VIRAL_LOAD_COUNT);
		sql.append("and " + OBS_ALIAS + "value_numeric >= 0 ");
		sql.append(" and " + OBS_ALIAS + "encounter_id in (:encounters) ");
		
		if (!Objects.isNull(gender) && !gender.isEmpty())
			sql.append(" and p.gender ='" + gender + "' ");
		
		sql.append("and p.person_id in (:cohort) ");
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		query.setParameter("cohort", vlQuery.cohort.getMemberIds());
		query.setParameterList("encounters", vlQuery.getVlTakenEncounters());
		
		return new Cohort(query.list());
	}
	
	public Cohort getPatientWithViralLoadCountLowLevelViremia(String gender, Date endOnOrBefore) {
		setDate(endOnOrBefore);
		
		StringBuilder sql = super.baseQuery(HIV_VIRAL_LOAD_STATUS);
		sql.append("and " + OBS_ALIAS + "value_coded = " + conceptQuery(HIV_VIRAL_LOAD_LOW_LEVEL_VIREMIA));
		sql.append(" and " + OBS_ALIAS + "encounter_id in (:encounters) ");
		
		if (!Objects.isNull(gender) && !gender.isEmpty())
			sql.append(" and p.gender ='" + gender + "' ");
		
		sql.append("and p.person_id in (:cohort) ");
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		query.setParameterList("cohort", vlQuery.cohort.getMemberIds());
		query.setParameterList("encounters", vlQuery.getVlTakenEncounters());
		
		return new Cohort(query.list());
	}
}
