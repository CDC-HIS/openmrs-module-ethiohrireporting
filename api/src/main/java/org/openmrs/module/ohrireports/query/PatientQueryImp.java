package org.openmrs.module.ohrireports.query;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.ART_START_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HTS_FOLLOW_UP_ENCOUNTER_TYPE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.FOLLOW_UP_STATUS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.ALIVE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.RESTART;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TRANSFERRED_IN;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TREATMENT_END_DATE;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientQueryImp extends BaseOpenmrsService implements PatientQuery {

	@Autowired
	DbSessionFactory sessionFactory;

	private DbSession getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public Cohort getOnArtCohorts() {

		StringBuilder sql = baseQuery(ART_START_DATE);
		sql.append(" and ob.value_datetime <= :startOnOrAfter");
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
			sql.append(" and ob.value_datetime <= :startOnOrAfter");
		if (cohort != null && cohort.size() == 0)
			sql.append("and p.person_id in (:personIds)");

		if (endOrBefore != null)
			sql.append("and ob.value_datetime>= :endOrBefore");

		Query q = getSession().createSQLQuery(sql.toString());

		if (startOnOrAfter != null)
			q.setTimestamp("startOnOrAfter", startOnOrAfter);

		if (endOrBefore != null)
			q.setTimestamp("endOrBefore", endOrBefore);

		if (cohort != null && cohort.size() == 0)
			q.setParameter("personIds", cohort.getMemberIds());

		return new Cohort(q.list());
	}

	private StringBuilder baseQuery(String conceptQuestionUUid) {
		StringBuilder sql = new StringBuilder();
		sql.append("select ob.person_id from obs as ob ");
		sql.append("inner join patient as pa on pa.patient_id = ob.person_id ");
		sql.append("inner join person as p on pa.patient_id = p.person_id ");
		sql.append("inner join concept as c on c.concept_id = ob.concept_id ");
		sql.append("and c.uuid= '" + conceptQuestionUUid + "' ");
		sql.append("inner join encounter as e on e.encounter_id = ob.encounter_id ");
		sql.append("inner join encounter_type as et on et.encounter_type_id = e.encounter_type ");
		sql.append("and et.uuid= '" + HTS_FOLLOW_UP_ENCOUNTER_TYPE + "'");
		sql.append("where pa.voided = false and ob.voided = false ");
		return sql;
	}

	@Override
	public Cohort getActiveOnCohort() {
		Cohort onArtCohort = getOnArtCohorts();
		StringBuilder sql = baseQuery(FOLLOW_UP_STATUS);
		sql.append("and ob.person_id in (:onArtPersonIds) ");
		sql.append("and ob.obs_datetime <= :startOnOrAfter ");
		sql.append("and obs.value_coded in (select concept_id from concepts where uuid = (:activeIndicator)) ");

		Query q = getSession().createSQLQuery(sql.toString());
		q.setParameter("onArtPersonIds", onArtCohort.getMemberIds());
		q.setParameter("startOnOrAfter", Calendar.getInstance().getTime());
		q.setParameter("activeIndicator", Arrays.asList(ALIVE, RESTART, TRANSFERRED_IN));

		return new Cohort(q.list());
	}

	@Override
	public Cohort getActiveOnCohort(String gender, Date startOnOrAfter, Date endOnOrBefore, Cohort cohort) {

		Cohort onTreatmentCohort = getCurrentOnTreatmentCohort(gender, endOnOrBefore, cohort);
		StringBuilder sql = baseQuery(FOLLOW_UP_STATUS);
		sql.append("and ob.person_id in (:onTreatmentPersonIds) ");
		sql.append("and obs.value_coded in (select concept_id from concepts where uuid = (:activeIndicator)) ");
		if (gender != null && !gender.trim().isEmpty())
			sql.append("and p.gender = '" + gender + "' ");
		if (startOnOrAfter != null)
			sql.append(" and ob.value_datetime <= :startOnOrAfter");
		if (cohort != null && cohort.size() == 0)
			sql.append("and p.person_id in (:personIds)");

		Query q = getSession().createSQLQuery(sql.toString());

		q.setParameter("onTreatmentPersonIds", onTreatmentCohort.getMemberIds());

		q.setParameter("activeIndicator", Arrays.asList(ALIVE, RESTART, TRANSFERRED_IN));
		if (startOnOrAfter != null)
			q.setTimestamp("startOnOrAfter", startOnOrAfter);
		if (cohort != null && cohort.size() == 0)
			q.setParameter("personIds", cohort.getMemberIds());

		return new Cohort(q.list());

	}

	@Override
	public Cohort getCurrentOnTreatmentCohort() {
		StringBuilder sql = baseQuery(TREATMENT_END_DATE);
		sql.append(" and ob.value_datetime >= :startOnOrAfter");
		Query q = getSession().createSQLQuery(sql.toString());
		q.setTimestamp("startOnOrAfter", Calendar.getInstance().getTime());
		return new Cohort(q.list());
	}

	@Override
	public Cohort getCurrentOnTreatmentCohort(String gender, Date endOnOrBefore, Cohort cohort) {
		Cohort onArtCohort = getOnArtCohorts();
		StringBuilder sql = baseQuery(ART_START_DATE);
		
		sql.append("and ob.person_id in (:onArtPersonIds) ");
		
		if (gender != null && !gender.trim().isEmpty())
			sql.append("and p.gender = '" + gender + "' ");
		if (endOnOrBefore != null)
			sql.append(" and ob.value_datetime >= :endOnOrBefore");
		if (cohort != null && cohort.size() == 0)
			sql.append("and p.person_id in (:personIds)");

		Query q = getSession().createSQLQuery(sql.toString());
		q.setParameter("onArtPersonIds", onArtCohort.getMemberIds());
	
		if (endOnOrBefore != null)
			q.setTimestamp("endOnOrBefore", endOnOrBefore);
		if (cohort != null && cohort.size() == 0)
			q.setParameter("personIds", cohort.getMemberIds());

		return new Cohort(q.list());
	}

	@Override
	public Set<Person> getPatients(Cohort cohort) {
		StringBuilder sql = new StringBuilder();

		sql.append("select p from patient as pa  ");
		sql.append("inner join person as p on pa.patient_id = p.person_id ");
		sql.append("where pa.voided = false and p.voided = false ");
		sql.append("and p.person_id in (: cohorts)");

		Query query = getSession().createQuery(sql.toString());
		query.setParameter("cohorts", cohort.getMemberIds());
		List<Object> result = query.list();
		Set<Person> persons = new TreeSet<>();
		for (Object object : result) {
			if (object instanceof Person) {
				persons.add((Person) object);
			}
		}
		return persons;
	}

}
