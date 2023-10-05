package org.openmrs.module.ohrireports.reports.datasetvaluator.hmis.hiv_plhiv;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.UNDERNOURISHED;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.THERAPEUTIC_SUPPLEMENTARY_FOOD;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.NUTRITIONAL_STATUS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.YES;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.PREGNANT_STATUS;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.MILD_MAL_NUTRITION;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.MODERATE_MAL_NUTRITION;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.SEVERE_MAL_NUTRITION;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import antlr.collections.List;

@Component
public class HivPlvHivQuery extends PatientQueryImpDao {
	
	private Date startDate, endDate;
	
	private DbSessionFactory sessionFactory;
	
	private Cohort baseCohort;
	
	@Autowired
	public HivPlvHivQuery(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		setSessionFactory(sessionFactory);
	}
	
	public void setDates(Date _start, Date _date) {
		startDate = _start;
		endDate = _date;
		if (baseCohort == null)
			baseCohort = getActiveOnArtCohort("", startDate, endDate, null);
	}
	
	public Set<Integer> getAssessedPatients() {
		
		StringBuilder sql = getPatientBySupplementType(NUTRITIONAL_STATUS,
		    Arrays.asList(MILD_MAL_NUTRITION, MODERATE_MAL_NUTRITION, UNDERNOURISHED, SEVERE_MAL_NUTRITION));
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("person_id", baseCohort.getMemberIds());
		query.setDate("startDate", startDate);
		query.setDate("endDate", endDate);
		
		return new HashSet<Integer>(query.list());
	}
	
	public Set<Integer> getPatientUndernourished() {
		StringBuilder sql = getPatientBySupplementType(NUTRITIONAL_STATUS, Arrays.asList(UNDERNOURISHED));

		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		query.setParameterList("person_id", baseCohort.getMemberIds());
		query.setDate("startDate", startDate);
		query.setDate("endDate", endDate);

		return new HashSet<>(query.list());
	}
	
	public Set<Integer> getPatientModerateMalNutrition() {

		StringBuilder sql = getPatientBySupplementType(NUTRITIONAL_STATUS,
				Arrays.asList(MODERATE_MAL_NUTRITION, MILD_MAL_NUTRITION));

		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());

		query.setParameterList("person_id", baseCohort.getMemberIds());
		query.setDate("startDate", startDate);
		query.setDate("endDate", endDate);

		return new HashSet<>(query.list());
	}
	
	public Set<Integer> getPatientSevereMalNutrition() {

		StringBuilder sql = getPatientBySupplementType(NUTRITIONAL_STATUS, Arrays.asList(SEVERE_MAL_NUTRITION));

		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		query.setParameterList("person_id", baseCohort.getMemberIds());
		query.setDate("startDate", startDate);
		query.setDate("endDate", endDate);

		return new HashSet<>(query.list());
	}
	
	public Set<Integer> getPatientMATookSupplement() {
		
		Cohort cohort = new Cohort(getPatientByStatus(THERAPEUTIC_SUPPLEMENTARY_FOOD, YES));
		StringBuilder sql = getPatientBySupplementType(NUTRITIONAL_STATUS,
		    Arrays.asList(MILD_MAL_NUTRITION, MODERATE_MAL_NUTRITION));
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("person_id", cohort.getMemberIds());
		query.setDate("startDate", startDate);
		query.setDate("endDate", endDate);
		
		return new HashSet<Integer>(query.list());
	}
	
	public Set<Integer> getPatientSVTookSupplement() {
		
		Cohort cohort = new Cohort(getPatientByStatus(THERAPEUTIC_SUPPLEMENTARY_FOOD, YES));
		StringBuilder sql = getPatientBySupplementType(NUTRITIONAL_STATUS, Arrays.asList(SEVERE_MAL_NUTRITION));
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("person_id", cohort.getMemberIds());
		query.setDate("startDate", startDate);
		query.setDate("endDate", endDate);
		
		return new HashSet<Integer>(query.list());
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
