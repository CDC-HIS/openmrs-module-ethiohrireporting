package org.openmrs.module.ohrireports.api.impl;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.ART_START_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.FOLLOW_UP_DATE;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.PREGNANT_STATUS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.REASON_FOR_ART_ELIGIBILITY;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TRANSFERRED_IN;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TREATMENT_END_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.FOLLOW_UP_STATUS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.ALIVE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.RESTART;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
	
	private Date endDate;
	
	@Override
	public List<Integer> init(Date _endDate) {
		if (endDate == null || !endDate.equals(_endDate)) {
			endDate = _endDate;
			latestEncounterIds = getLatestDateByFollowUpDate(endDate);
		}
		return latestEncounterIds;
	}
	
	public DbSessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public void setSessionFactory(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	private List<Integer> latestEncounterIds;
	
	public List<Integer> getLatestEncounterIds() {
		return latestEncounterIds;
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
	
	// For new
	@Override
	public Cohort getNewOnArtCohort(String gender, Date startOnOrAfter, Date endOrBefore, Cohort cohort) {
		init(endOrBefore);
		Cohort transferInCohort = transferredInFacility(null, endOrBefore);
		
		Collection<?> list = getArtStartedCohort(gender, startOnOrAfter, endOrBefore, cohort, transferInCohort);
		
		return new Cohort(list);
	}
	
	/**
	 * @param gender for full range of gender "" or for specific gend pass F or M
	 * @param startOnOrAfter optional but at list one of date should be provided start or end date.
	 * @param endOrBefore optional but at list one of date should be provided start or end date.
	 * @param cohort Optional can be if there is no patients the ART should run on.
	 * @param toBeExcludedCohort Can be null if there is no patients to be excluded from the query.
	 * @return
	 */
	@Override
	public Collection<Integer> getArtStartedCohort(String gender, Date startOnOrAfter, Date endOrBefore, Cohort cohort,
			Cohort toBeExcludedCohort) {
		init(endOrBefore);

		if (latestEncounterIds.isEmpty())
			return new ArrayList<>();

		StringBuilder sql = baseQuery(ART_START_DATE);

		sql.append(" and " + OBS_ALIAS + "encounter_id in (:encounters)");

		if (!Objects.isNull(gender) && !gender.trim().isEmpty())
			sql.append("and p.gender = '" + gender + "' ");
		if (startOnOrAfter != null)
			sql.append(" and " + OBS_ALIAS + "value_datetime >= :start ");
		if (cohort != null && cohort.size() != 0)
			sql.append("and p.person_id in (:personIds) ");

		if (endOrBefore != null)
			sql.append("and " + OBS_ALIAS + "value_datetime <= :end ");
		if (toBeExcludedCohort != null && toBeExcludedCohort.size() != 0)
			sql.append("and p.person_id not in (:toBeExcludedCohort) ");

		Query q = getSession().createSQLQuery(sql.toString());
		q.setParameterList("encounters", latestEncounterIds);

		if (startOnOrAfter != null)
			q.setTimestamp("start", startOnOrAfter);

		if (endOrBefore != null)
			q.setTimestamp("end", endOrBefore);

		if (cohort != null && cohort.size() != 0)
			q.setParameter("personIds", cohort.getMemberIds());
		if (toBeExcludedCohort != null && toBeExcludedCohort.size() != 0)
			q.setParameterList("toBeExcludedCohort", toBeExcludedCohort.getMemberIds());

		List list = q.list();

		if (list != null) {
			return (List<Integer>) list;
		} else {
			return new ArrayList<Integer>();
		}

	}
	
	@Override
	public Cohort getActiveOnArtCohort() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -3);
		Cohort onTreatmentCohort = getCurrentOnTreatmentCohort("", calendar.getTime(), Calendar.getInstance().getTime(),
		    null);
		
		return onTreatmentCohort;
	}
	
	@Override
	public Cohort getActiveOnArtCohort(String gender, Date startOnOrAfter, Date endOnOrBefore, Cohort cohort) {
		
		Cohort onTreatmentCohort = getCurrentOnTreatmentCohort(gender, endOnOrBefore, null);
		
		return onTreatmentCohort;
		
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
		
		init(endOnOrBefore);
		
		if (latestEncounterIds == null || latestEncounterIds.isEmpty())
			return new Cohort();
		
		StringBuilder sql = baseQuery(TREATMENT_END_DATE);
		
		sql.append("and " + OBS_ALIAS + "encounter_id in (:encounterIds) ");
		
		if (!Objects.isNull(gender) && !gender.trim().isEmpty())
			sql.append("and p.gender = '" + gender + "' ");
		if (endOnOrBefore != null)
			sql.append(" and " + OBS_ALIAS + "value_datetime >= :endOnOrBefore ");
		if (cohort != null && cohort.size() != 0)
			sql.append("and p.person_id in (:personIds) ");
		
		Query q = getSession().createSQLQuery(sql.toString());
		q.setParameter("encounterIds", latestEncounterIds);
		
		if (endOnOrBefore != null)
			q.setTimestamp("endOnOrBefore", endOnOrBefore);
		if (cohort != null && cohort.size() != 0)
			q.setParameter("personIds", cohort.getMemberIds());
		
		return new Cohort(q.list());
	}
	
	public Cohort getCurrentOnTreatmentCohort(String gender, Date endOnOrBefore, Cohort cohort) {
		init(endOnOrBefore);
		if (latestEncounterIds.isEmpty())
			return new Cohort();
		
		StringBuilder sql = baseQuery(TREATMENT_END_DATE);
		
		String condition = LATEST_ENCOUNTER_BASE_ALIAS_OBS + "concept_id =" + conceptQuery(FOLLOW_UP_STATUS) + " and "
		        + LATEST_ENCOUNTER_BASE_ALIAS_OBS + "value_coded in " + conceptQuery(Arrays.asList(ALIVE, RESTART))
		        + " and " + LATEST_ENCOUNTER_BASE_ALIAS_OBS + "encounter_id in (:latestEncounterId)";
		
		sql.append(" and " + OBS_ALIAS + "encounter_id in ");
		sql.append(baseLatestEncounter(condition));
		
		if (!Objects.isNull(gender) && !gender.trim().isEmpty())
			sql.append("and p.gender = '" + gender + "' ");
		if (endOnOrBefore != null)
			sql.append(" and " + OBS_ALIAS + "value_datetime >= :endOnOrBefore ");
		if (cohort != null && cohort.size() != 0)
			sql.append("and p.person_id in (:personIds) ");
		
		Query q = getSession().createSQLQuery(sql.toString());
		
		q.setParameterList("latestEncounterId", latestEncounterIds);
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
	
	public List<Integer> getBaseEncounters(Date start, Date end) {
		StringBuilder builder = new StringBuilder("select Max(obs_enc.encounter_id) from obs as obs_enc");
		builder.append(" where obs_enc.concept_id =" + conceptQuery(ART_START_DATE));
		
		if (end != null)
			builder.append("and obs_enc.value_datetime <= :end ");
		builder.append(" GROUP BY obs_enc.person_id ");
		
		Query q = getSession().createSQLQuery(builder.toString());
		
		if (end != null)
			q.setDate("end", end);
		
		List list = q.list();
		
		if (list != null) {
			return (List<Integer>) list;
		} else {
			return new ArrayList<Integer>();
		}
	}
	
	private List<Integer> getLatestDateByFollowUpDate(Date end) {
		StringBuilder builder = new StringBuilder("select ob.encounter_id from obs as ob inner join ");
		builder.append("(select Max(obs_enc.value_datetime) as value_datetime, person_id as person_id from obs as obs_enc");
		
		builder.append(" where obs_enc.concept_id =" + conceptQuery(FOLLOW_UP_DATE));
		
		if (end != null)
			builder.append(" and obs_enc.value_datetime <= :end ");
		builder.append(" GROUP BY obs_enc.person_id ) as sub ");
		builder.append(" on ob.value_datetime = sub.value_datetime and ob.person_id = sub.person_id ");
		builder.append(" and ob.concept_id =" + conceptQuery(FOLLOW_UP_DATE));
		
		Query q = getSession().createSQLQuery(builder.toString());
		
		if (end != null)
			q.setDate("end", end);
		
		List list = q.list();
		
		if (list != null) {
			return latestEncounterIds = (List<Integer>) list;
		} else {
			return new ArrayList<Integer>();
		}
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
	
	private Cohort transferredInFacility(Date startOnOrAfter, Date endOnOrBefore) {
		
		StringBuilder sql = baseQuery(REASON_FOR_ART_ELIGIBILITY);
		
		sql.append(" and " + OBS_ALIAS + "value_coded = " + conceptQuery(TRANSFERRED_IN));
		
		if (startOnOrAfter != null)
			sql.append(" and " + OBS_ALIAS + "obs_datetime >= :startOnOrAfter ");
		if (endOnOrBefore != null)
			sql.append(" and " + OBS_ALIAS + "obs_datetime <= :endOnOrBefore ");
		
		Query q = getSession().createSQLQuery(sql.toString());
		
		if (startOnOrAfter != null)
			q.setTimestamp("startOnOrAfter", startOnOrAfter);
		if (endOnOrBefore != null)
			q.setTimestamp("endOnOrBefore", endOnOrBefore);
		
		return new Cohort(q.list());
	}
}
