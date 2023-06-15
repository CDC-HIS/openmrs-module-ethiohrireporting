package org.openmrs.module.ohrireports.api.impl;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.ALIVE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.ART_START_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.FOLLOW_UP_STATUS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HTS_FOLLOW_UP_ENCOUNTER_TYPE;
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
import org.hibernate.engine.profile.Fetch;
import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.dao.PatientQueryDao;

public class PatientQueryImpDao implements PatientQueryDao {
	
	private DbSessionFactory sessionFactory;
	
	public PatientQueryImpDao() {
	}
	
	public DbSessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public void setSessionFactory(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	private String OBS_ALIAS = "ob.";
	
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
	
	private StringBuilder baseQuery(String conceptQuestionUUid) {
		StringBuilder sql = new StringBuilder();
		sql.append("select " + OBS_ALIAS + "person_id from obs as ob ");
		sql.append("inner join patient as pa on pa.patient_id = " + OBS_ALIAS + "person_id ");
		sql.append("inner join person as p on pa.patient_id = p.person_id ");
		sql.append("inner join concept as c on c.concept_id = " + OBS_ALIAS + "concept_id ");
		sql.append("and c.uuid= '" + conceptQuestionUUid + "' ");
		sql.append("inner join encounter as e on e.encounter_id = " + OBS_ALIAS + "encounter_id ");
		sql.append("inner join encounter_type as et on et.encounter_type_id = e.encounter_type ");
		sql.append("and et.uuid= '" + HTS_FOLLOW_UP_ENCOUNTER_TYPE + "' ");
		sql.append("where pa.voided = false and " + OBS_ALIAS + "voided = false ");
		return sql;
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
