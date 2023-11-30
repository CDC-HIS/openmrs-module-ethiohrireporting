package org.openmrs.module.ohrireports.api.impl;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.ART_START_DATE;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.PREGNANT_STATUS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.REASON_FOR_ART_ELIGIBILITY;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TRANSFERRED_IN;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TREATMENT_END_DATE;

import java.util.*;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.dao.PatientQueryDao;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISUtilies;
import org.springframework.stereotype.Component;

@Component
public class PatientQueryImpDao extends BaseEthiOhriQuery implements PatientQueryDao {
	
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
	public Cohort getNewOnArtCohort(String gender, Date startOnOrAfter, Date endOrBefore, Cohort cohort,
	        List<Integer> encounters) {
		
		Cohort transferInCohort = transferredInFacility(endOrBefore);
		
		Collection<?> list = getArtStartedCohort(gender, startOnOrAfter, endOrBefore, cohort, transferInCohort, encounters);
		
		return new Cohort(list);
	}
	
	@Override
    public Collection<Integer> getArtStartedCohort(String gender, Date startOnOrAfter, Date endOrBefore, Cohort cohort,
                                                   Cohort toBeExcludedCohort, List<Integer> encounters) {

        if (encounters.isEmpty() || (cohort != null && cohort.isEmpty()))
            return new ArrayList<>();

        StringBuilder sql = baseQuery(ART_START_DATE);

        sql.append(" and ").append(OBS_ALIAS).append("encounter_id in (:encounters)");

        if (!Objects.isNull(gender) && !gender.trim().isEmpty())
            sql.append("and p.gender = '").append(gender).append("' ");
        if (startOnOrAfter != null)
            sql.append(" and ").append(OBS_ALIAS).append("value_datetime >= :start ");
        if (cohort != null && !cohort.isEmpty())
            sql.append("and p.person_id in (:personIds) ");

        if (endOrBefore != null)
            sql.append("and ").append(OBS_ALIAS).append("value_datetime <= :end ");
        if (toBeExcludedCohort != null && !toBeExcludedCohort.isEmpty())
            sql.append("and p.person_id not in (:toBeExcludedCohort) ");

        Query q = getSession().createSQLQuery(sql.toString());
        q.setParameterList("encounters", encounters);

        if (startOnOrAfter != null)
            q.setTimestamp("start", startOnOrAfter);

        if (endOrBefore != null)
            q.setTimestamp("end", endOrBefore);

        if (cohort != null && !cohort.isEmpty())
            q.setParameter("personIds", cohort.getMemberIds());
        if (toBeExcludedCohort != null && !toBeExcludedCohort.isEmpty())
            q.setParameterList("toBeExcludedCohort", toBeExcludedCohort.getMemberIds());

        List list = q.list();

        if (list != null) {
            return (List<Integer>) list;
        } else {
            return new ArrayList<Integer>();
        }

    }
	
	public Collection<Integer> getArtStartedCohort(List<Integer> encounters, Date startOnOrAfter, Date endOrBefore,
                                                   Cohort cohort) {

        if (encounters.isEmpty())
            return new ArrayList<>();

        StringBuilder sql = baseQuery(ART_START_DATE);

        sql.append(" and " + OBS_ALIAS + "encounter_id in (:encounters)");
        if (startOnOrAfter != null)
            sql.append(" and " + OBS_ALIAS + "value_datetime >= :start ");
        if (cohort != null && cohort.size() != 0)
            sql.append("and p.person_id in (:personIds) ");

        if (endOrBefore != null)
            sql.append("and " + OBS_ALIAS + "value_datetime <= :end ");

        Query q = getSession().createSQLQuery(sql.toString());
        q.setParameterList("encounters", encounters);

        if (startOnOrAfter != null)
            q.setTimestamp("start", startOnOrAfter);

        if (endOrBefore != null)
            q.setTimestamp("end", endOrBefore);

        if (cohort != null && cohort.size() != 0)
            q.setParameter("personIds", cohort.getMemberIds());

        List list = q.list();

        if (list != null) {
            return (List<Integer>) list;
        } else {
            return new ArrayList<Integer>();
        }

    }
	
	@Override
	public Cohort getActiveOnArtCohort(String gender, Date startOnOrAfter, Date endOnOrBefore, Cohort cohort,
	        List<Integer> encounters) {
		
		Cohort onTreatmentCohort = getCurrentOnTreatmentCohort(gender, endOnOrBefore, null, encounters);
		
		return onTreatmentCohort;
		
	}
	
	@org.jetbrains.annotations.NotNull
	@Contract("_, _, _, null -> new")
	private Cohort getCurrentOnTreatmentCohort(String gender, Date endOnOrBefore, Cohort cohort, List<Integer> encounters) {
		
		if (encounters == null || encounters.isEmpty())
			return new Cohort();
		
		StringBuilder sql = baseQuery(TREATMENT_END_DATE);
		
		sql.append("and ").append(OBS_ALIAS).append("encounter_id in (:encounterIds) ");
		
		if (!Objects.isNull(gender) && !gender.trim().isEmpty())
			sql.append("and p.gender = '").append(gender).append("' ");
		if (endOnOrBefore != null)
			sql.append(" and ").append(OBS_ALIAS).append("value_datetime >= :endOnOrBefore ");
		if (cohort != null && !cohort.isEmpty())
			sql.append("and p.person_id in (:personIds) ");
		
		Query q = getSession().createSQLQuery(sql.toString());
		q.setParameter("encounterIds", encounters);
		
		if (endOnOrBefore != null)
			q.setTimestamp("endOnOrBefore", endOnOrBefore);
		if (cohort != null && !cohort.isEmpty())
			q.setParameter("personIds", cohort.getMemberIds());
		
		return new Cohort(q.list());
	}
	
	@Override
	public List<Person> getPersons(@NotNull Cohort cohort) {
		Set<Integer> pIntegers = cohort.getMemberIds();
		Criteria criteria = getSession().createCriteria(Person.class);
		
		criteria.setCacheable(false);
		criteria.add(Restrictions.eq("voided", false));
		criteria.add(Restrictions.in("personId", pIntegers));
		
		return criteria.list();
		
	}
	
	@Override
	public Cohort getPatientByPregnantStatus(Cohort patient, String conceptUUID, List<Integer> encounters) {
		StringBuilder sql = baseQuery(PREGNANT_STATUS);
		if (encounters == null || encounters.isEmpty())
			return new Cohort();
		sql.append(" and ").append(OBS_ALIAS).append("value_coded = (select concept_id from concept where uuid='")
		        .append(conceptUUID).append("')");
		
		sql.append(" and ").append(OBS_ALIAS).append("encounter_id in (:encounters) ");
		if (patient != null && !patient.isEmpty())
			sql.append("and p.person_id in (:personIds) ");
		
		Query q = getSession().createSQLQuery(sql.toString());
		
		q.setParameterList("encounters", encounters);
		
		if (patient != null && !patient.isEmpty())
			q.setParameter("personIds", patient.getMemberIds());
		
		return new Cohort(q.list());
		
	}
	
	@NotNull
	@Contract("_ -> new")
	private Cohort transferredInFacility(Date endOnOrBefore) {
		
		StringBuilder sql = baseQuery(REASON_FOR_ART_ELIGIBILITY);
		
		sql.append(" and ").append(OBS_ALIAS).append("value_coded = ").append(conceptQuery(TRANSFERRED_IN));
		
		if (endOnOrBefore != null)
			sql.append(" and ").append(OBS_ALIAS).append("obs_datetime <= :endOnOrBefore ");
		
		Query q = getSession().createSQLQuery(sql.toString());
		
		if (endOnOrBefore != null)
			q.setTimestamp("endOnOrBefore", endOnOrBefore);
		
		return new Cohort(q.list());
	}
	
	public HashMap<Integer, Object> getObValue(String conceptUUId, Cohort cohort, @NotNull ObsValueType type,
	        List<Integer> encounter) {
		
		StringBuilder sqlBuilder = new StringBuilder();
		String rawQuery = "";
		switch (type) {
		
			case NUMERIC_VALUE:
				rawQuery = " select ob.person_id,ob.value_numeric from obs as ob WHERE ob.value_numeric is not null ";
				break;
			case DATE_VALUE:
				rawQuery = " select ob.person_id,ob.value_datetime from obs as ob WHERE ob.value_datetime is not null ";
				break;
			case CONCEPT_NAME:
				rawQuery = "select ob.person_id, cn.name from obs as ob \n"
				        + "inner join concept_name as cn on cn.concept_id = ob.concept_id\n"
				        + "WHERE cn.locale_preferred =1 ";
				break;
			case CONCEPT_UUID:
				rawQuery = "select ob.person_id, c.uuid from obs as ob \n"
				        + " inner join concept as c on c.concept_id = ob.value_coded where";
				break;
		}
		if (rawQuery.endsWith("where")) {
			rawQuery.concat(" and ");
		}
		
		sqlBuilder.append(rawQuery);
		sqlBuilder.append(" ob.concept_id = ").append(conceptQuery(conceptUUId));
		sqlBuilder.append(" and ob.encounter_id in (:encounters)");
		sqlBuilder.append(" and ob.person_id in (:ids)");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
		query.setParameterList("encounters", encounter);
		query.setParameterList("ids", cohort.getMemberIds());
		return HMISUtilies.getDictionary(query);
	}
	
	public enum ObsValueType {
		NUMERIC_VALUE, DATE_VALUE, CONCEPT_NAME, CONCEPT_UUID
	}
}
