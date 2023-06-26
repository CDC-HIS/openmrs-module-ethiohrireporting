package org.openmrs.module.ohrireports.api.impl;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.ALIVE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.ART_START_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.FOLLOW_UP_STATUS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.PREGNANT_STATUS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.RESTART;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TRANSFERRED_IN;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TREATMENT_END_DATE;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.dao.PatientQueryDao;
import org.springframework.stereotype.Component;

@Component
public class PatientQueryImpDao extends BaseEthiOhriQuery implements PatientQueryDao {
	
	// Calendar month is zero base so total month in a year is 11 which is counting
	// from zero
	private DbSessionFactory sessionFactory;
	
	public PatientQueryImpDao() {
	}
	
	public DbSessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public void setSessionFactory(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	private DbSession getSession() {
		return getSessionFactory().getCurrentSession();
	}
	
	@Override
	public Cohort getOnArtCohorts() {
		
		StringBuilder sql = baseQuery(ART_START_DATE);
		sql.append(" and " + OBS_ALIAS + "value_datetime >= :startOnOrAfter ");
		Query q = getSession().createSQLQuery(sql.toString());
		q.setTimestamp("startOnOrAfter", Calendar.getInstance().getTime());
		return new Cohort(q.list());
	}
	
	@Override
	public Cohort getOnArtCohorts(String gender, Date startOnOrAfter, Date endOrBefore, Cohort cohort) {
		
		StringBuilder sql = baseQuery(ART_START_DATE);
		if (gender != null && !gender.trim().isEmpty())
			sql.append("and p.gender = '" + gender + "' ");
		if (startOnOrAfter != null)
			sql.append(" and " + OBS_ALIAS + "value_datetime >= :start ");
		if (cohort != null && cohort.size() != 0)
			sql.append("and p.person_id in (:personIds) ");
		
		if (endOrBefore != null)
			sql.append("and " + OBS_ALIAS + "value_datetime <= :end ");
		
		Query q = getSession().createSQLQuery(sql.toString());
		
		if (startOnOrAfter != null)
			q.setTimestamp("start", startOnOrAfter);
		
		if (endOrBefore != null)
			q.setTimestamp("end", endOrBefore);
		
		if (cohort != null && cohort.size() != 0)
			q.setParameter("personIds", cohort.getMemberIds());
		
		return new Cohort(q.list());
	}
	
	@Override
	public Cohort getActiveOnArtCohort() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -3);
		Cohort onTreatmentCohort = getCurrentOnTreatmentCohort("", calendar.getTime(), Calendar.getInstance().getTime(),
		    null);
		
		if (onTreatmentCohort == null || onTreatmentCohort.size() == 0)
			return new Cohort();
		
		StringBuilder sql = baseQuery(FOLLOW_UP_STATUS);
		sql.append("and " + OBS_ALIAS + "person_id in (:onTreatmentCohortIds) ");
		sql.append("and " + OBS_ALIAS + "obs_datetime >= :startOnOrAfter ");
		sql.append("and " + OBS_ALIAS + "value_coded in (select concept_id from concept where uuid in (:activeIndicator)) ");
		
		Query q = getSession().createSQLQuery(sql.toString());
		q.setParameter("onTreatmentCohortIds", onTreatmentCohort.getMemberIds());
		q.setParameter("startOnOrAfter", Calendar.getInstance().getTime());
		q.setParameter("activeIndicator", Arrays.asList(ALIVE, RESTART, TRANSFERRED_IN));
		
		return new Cohort(q.list());
	}
	
	@Override
	public Cohort getActiveOnArtCohort(String gender, Date startOnOrAfter, Date endOnOrBefore, Cohort cohort) {
		
		Cohort onTreatmentCohort = getCurrentOnTreatmentCohort(gender, startOnOrAfter, endOnOrBefore, cohort);
		
		if (onTreatmentCohort == null || onTreatmentCohort.size() == 0)
			return new Cohort();
		
		StringBuilder sql = baseQuery(FOLLOW_UP_STATUS);
		
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
		
		Query q = getSession().createSQLQuery(sql.toString());
		
		q.setParameter("onTreatmentPersonIds", onTreatmentCohort.getMemberIds());
		
		q.setParameter("activeIndicator", Arrays.asList(ALIVE, RESTART, TRANSFERRED_IN));
		if (startOnOrAfter != null)
			q.setTimestamp("startOnOrAfter", startOnOrAfter);
		if (endOnOrBefore != null)
			q.setTimestamp("endOnOrBefore", endOnOrBefore);
		if (cohort != null && cohort.size() != 0)
			q.setParameter("personIds", cohort.getMemberIds());
		
		return new Cohort(q.list());
		
	}
	
	@Override
	public Cohort getCurrentOnTreatmentCohort() {
		StringBuilder sql = baseQuery(TREATMENT_END_DATE);
		sql.append(" and " + OBS_ALIAS + "value_datetime >= :startOnOrAfter ");
		Query q = getSession().createSQLQuery(sql.toString());
		q.setTimestamp("startOnOrAfter", Calendar.getInstance().getTime());
		return new Cohort(q.list());
	}
	
	@Override
	public Cohort getCurrentOnTreatmentCohort(String gender, Date startOnOrAfter, Date endOnOrBefore, Cohort cohort) {
		Cohort onArtCohort = getOnArtCohorts(gender, startOnOrAfter, endOnOrBefore, cohort);
		
		if (onArtCohort == null || onArtCohort.size() == 0)
			return new Cohort();
		
		StringBuilder sql = baseQuery(TREATMENT_END_DATE);
		
		sql.append("and " + OBS_ALIAS + "person_id in (:onArtPersonIds) ");
		
		if (gender != null && !gender.trim().isEmpty())
			sql.append("and p.gender = '" + gender + "' ");
		if (endOnOrBefore != null)
			sql.append(" and " + OBS_ALIAS + "value_datetime >= :endOnOrBefore ");
		if (cohort != null && cohort.size() != 0)
			sql.append("and p.person_id in (:personIds) ");
		
		Query q = getSession().createSQLQuery(sql.toString());
		q.setParameter("onArtPersonIds", onArtCohort.getMemberIds());
		
		if (endOnOrBefore != null)
			q.setTimestamp("endOnOrBefore", endOnOrBefore);
		if (cohort != null && cohort.size() != 0)
			q.setParameter("personIds", cohort.getMemberIds());
		
		return new Cohort(q.list());
	}
	
	public Cohort getCurrentOnTreatmentCohort(String gender, Date endOnOrBefore, Cohort cohort) {
		
		if (cohort == null || cohort.size() == 0)
			return new Cohort();
		
		StringBuilder sql = baseQuery(TREATMENT_END_DATE);
		
		if (gender != null && !gender.trim().isEmpty())
			sql.append("and p.gender = '" + gender + "' ");
		if (endOnOrBefore != null)
			sql.append(" and " + OBS_ALIAS + "value_datetime >= :endOnOrBefore ");
		if (cohort != null && cohort.size() != 0)
			sql.append("and p.person_id in (:personIds) ");
		
		Query q = getSession().createQuery(sql.toString());
		
		if (endOnOrBefore != null)
			q.setTimestamp("endOnOrBefore", endOnOrBefore);
		if (cohort != null && cohort.size() != 0)
			q.setParameter("personIds", cohort.getMemberIds());
		
		return new Cohort(q.list());
	}
	
	@Override
	public List<Person> getPersons(Cohort cohort) {
		Set<Integer> pIntegers = cohort.getMemberIds();
		Criteria criteria = getSession().createCriteria(Person.class);
		
		criteria.setCacheable(false);
		criteria.add(Restrictions.eq("voided", false));
		criteria.add(Restrictions.in("personId", pIntegers));
		
		return criteria.list();
		
	}
	
	@Override
	public Cohort getPatientByPregnantStatus(Cohort patient, String conceptUUID, Date startOnOrAfter, Date endOnOrBefore) {
		StringBuilder sql = baseQuery(PREGNANT_STATUS);
		sql.append(" and " + OBS_ALIAS + "value_coded = (select c.concept_id from concept where uuid='" + conceptUUID + "')");
		
		if (startOnOrAfter != null)
			sql.append(" and " + OBS_ALIAS + "obs_datetime >= :startOnOrAfter ");
		if (endOnOrBefore != null)
			sql.append(" and " + OBS_ALIAS + "obs_datetime <= :endOnOrBefore ");
		if (patient != null && patient.size() != 0)
			sql.append("and p.person_id in (:personIds) ");
		
		Query q = getSession().createSQLQuery(sql.toString());
		
		if (startOnOrAfter != null)
			q.setTimestamp("startOnOrAfter", startOnOrAfter);
		if (endOnOrBefore != null)
			q.setTimestamp("endOnOrBefore", endOnOrBefore);
		if (patient != null && patient.size() != 0)
			q.setParameter("personIds", patient.getMemberIds());
		
		return new Cohort(q.list());
		
	}
	
}
