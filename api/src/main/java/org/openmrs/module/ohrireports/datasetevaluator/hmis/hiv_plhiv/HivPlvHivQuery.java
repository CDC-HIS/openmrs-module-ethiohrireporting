package org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_plhiv;

import java.util.*;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Component
public class HivPlvHivQuery extends PatientQueryImpDao {
	
	private Date startDate;
	
	private Date endDate;
	
	private List<Integer> baseEncounter;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public void setEndDate(Date endDate, String conceptQuestion) {
		this.endDate = endDate;
		baseEncounter = encounterQuery.getEncounters(Collections.singletonList(conceptQuestion), startDate, endDate);
	}
	
	public List<Integer> getBaseEncounter() {
		return baseEncounter;
	}
	
	private DbSessionFactory sessionFactory;
	
	private Cohort baseCohort;
	
	@Autowired
	public HivPlvHivQuery(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		setSessionFactory(sessionFactory);
	}
	
	public void setDates(Date _start, Date _date, List<Integer> encounters) {
		startDate = _start;
		endDate = _date;
		if (baseCohort == null)
			baseCohort = getActiveOnArtCohort("", startDate, endDate, null, encounters);
	}
	
	public Set<Integer> getAssessedPatients() {
		
		StringBuilder sql = getPatientBySupplementType(NUTRITIONAL_STATUS_ADULT,
		    Arrays.asList(MILD_MAL_NUTRITION, MODERATE_MAL_NUTRITION, UNDERNOURISHED, SEVERE_MAL_NUTRITION));
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("person_id", baseCohort.getMemberIds());
		query.setDate("startDate", startDate);
		query.setDate("endDate", endDate);
		
		return new HashSet<Integer>(query.list());
	}
	
	public Set<Integer> getPatientUndernourished() {
        StringBuilder sql = getPatientBySupplementType(NUTRITIONAL_STATUS_ADULT, Arrays.asList(UNDERNOURISHED));

        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
        query.setParameterList("person_id", baseCohort.getMemberIds());
        query.setDate("startDate", startDate);
        query.setDate("endDate", endDate);

        return new HashSet<>(query.list());
    }
	
	public Set<Integer> getPatientModerateMalNutrition() {

        StringBuilder sql = getPatientBySupplementType(NUTRITIONAL_STATUS_ADULT,
                Arrays.asList(MODERATE_MAL_NUTRITION, MILD_MAL_NUTRITION));

        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());

        query.setParameterList("person_id", baseCohort.getMemberIds());
        query.setDate("startDate", startDate);
        query.setDate("endDate", endDate);

        return new HashSet<>(query.list());
    }
	
	public Set<Integer> getPatientSevereMalNutrition() {

        StringBuilder sql = getPatientBySupplementType(NUTRITIONAL_STATUS_ADULT, Arrays.asList(SEVERE_MAL_NUTRITION));

        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
        query.setParameterList("person_id", baseCohort.getMemberIds());
        query.setDate("startDate", startDate);
        query.setDate("endDate", endDate);

        return new HashSet<>(query.list());
    }
	
	public Set<Integer> getPatientMATookSupplement() {
		
		Cohort cohort = new Cohort(getPatientByStatus(THERAPEUTIC_SUPPLEMENTARY_FOOD, YES));
		StringBuilder sql = getPatientBySupplementType(NUTRITIONAL_STATUS_ADULT,
		    Arrays.asList(MILD_MAL_NUTRITION, MODERATE_MAL_NUTRITION));
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("person_id", cohort.getMemberIds());
		query.setDate("startDate", startDate);
		query.setDate("endDate", endDate);
		
		return new HashSet<Integer>(query.list());
	}
	
	public Set<Integer> getPatientSVTookSupplement() {
		
		Cohort cohort = new Cohort(getPatientByStatus(THERAPEUTIC_SUPPLEMENTARY_FOOD, YES));
		StringBuilder sql = getPatientBySupplementType(NUTRITIONAL_STATUS_ADULT, Arrays.asList(SEVERE_MAL_NUTRITION));
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("person_id", cohort.getMemberIds());
		query.setDate("startDate", startDate);
		query.setDate("endDate", endDate);
		
		return new HashSet<Integer>(query.list());
	}
	
	public Cohort getAllNUTMAMForAdult(List<Integer> encounters, Cohort cohort, List<String> conceptUUids) {
		StringBuilder stringBuilder = baseQuery(NUTRITIONAL_STATUS_ADULT);
		stringBuilder.append(" and ob.encounter_id in (:encounters)");
		stringBuilder.append(" and ob.person_id in (:cohort)");
		stringBuilder.append(" and value_coded in ").append(conceptQuery(conceptUUids));
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringBuilder.toString());
		query.setParameterList("encounters", encounters);
		query.setParameterList("cohort", cohort.getMemberIds());
		
		return new Cohort(query.list());
	}
	
	public Cohort getAllNUTSAMForAdult(List<Integer> encounters, Cohort cohort, List<String> conceptUUids) {
		StringBuilder stringBuilder = baseQuery(NUTRITIONAL_STATUS_ADULT);
		stringBuilder.append(" and ob.encounter_id in (:encounters)");
		stringBuilder.append(" and ob.person_id in (:cohort)");
		stringBuilder.append(" and value_coded in ").append(conceptQuery(conceptUUids));
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringBuilder.toString());
		query.setParameterList("encounters", encounters);
		query.setParameterList("cohort", cohort.getMemberIds());
		
		return new Cohort(query.list());
	}
	
	public Cohort getAllSUP(List<Integer> encounters, Cohort cohort) {
		StringBuilder stringBuilder = baseQuery(THERAPEUTIC_SUPPLEMENTARY_FOOD);
		stringBuilder.append(" and ob.encounter_id in (:encounters)");
		stringBuilder.append(" and ob.person_id in (:cohort)");
		stringBuilder.append(" and value_coded = ").append(conceptQuery(YES));
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringBuilder.toString());
		query.setParameterList("encounters", encounters);
		query.setParameterList("cohort", cohort.getMemberIds());
		
		return new Cohort(query.list());
	}
	
	@Override
	public Cohort getPatientByPregnantStatus(Cohort patient, String conceptUUID, List<Integer> encounters) {
		StringBuilder sql = baseQuery(PREGNANT_STATUS);
		if (encounters == null || encounters.isEmpty())
			return new Cohort();
		sql.append(" and ").append(OBS_ALIAS).append("value_coded =").append(conceptQuery(conceptUUID));
		
		sql.append(" and ").append(OBS_ALIAS).append("encounter_id in (:encounters) ");
		sql.append("and p.person_id in (:personIds) ");
		
		Query q = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		q.setParameterList("encounters", encounters);
		
		q.setParameter("personIds", patient.getMemberIds());
		
		return new Cohort(q.list());
		
	}
	
	public Set<Integer> getPregnant() {
		
		Set<Integer> pregnantIntegers = getPatientByStatus(PREGNANT_STATUS, YES);
		return pregnantIntegers;
	}
	
	private StringBuilder getPatientBySupplementType(String supplementType, java.util.List<String> list) {
		StringBuilder sql = new StringBuilder("select distinct enc.patient_id from encounter as enc where encounter_id in");
		
		sql.append("(select Max(encounter_id) from obs as ob where ");
		sql.append("ob.person_id in (");
		sql.append("select person_id from obs as obt ");
		sql.append(" where obt.concept_id =" + conceptQuery(supplementType));
		sql.append(" and obt.value_coded in (select concept_id from concept where uuid in ('" + String.join("','", list)
		        + "')) and obt.obs_datetime >= :startDate and obt.obs_datetime <= :endDate)");
		
		sql.append(" GROUP BY person_id) and enc.patient_id in (:person_id)");
		
		return sql;
		
	}
	
	private Set<Integer> getPatientByStatus(String concept, String value) {
        StringBuilder sql = baseQuery(concept);
        sql.append(" and ob.value_coded = " + conceptQuery(value));
        sql.append(
                " and ob.obs_datetime >= :startDate and ob.obs_datetime <= :endDate and ob.person_id in (:personIds)");
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());

        query.setDate("startDate", startDate);
        query.setDate("endDate", endDate);
        query.setParameterList("personIds", baseCohort.getMemberIds());

        return new HashSet<>(query.list());

    }
}
