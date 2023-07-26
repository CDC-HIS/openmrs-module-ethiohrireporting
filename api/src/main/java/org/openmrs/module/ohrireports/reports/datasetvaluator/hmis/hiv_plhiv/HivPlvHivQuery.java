package org.openmrs.module.ohrireports.reports.datasetvaluator.hmis.hiv_plhiv;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.UNDERNOURISHED;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.THERAPEUTIC_SUPPLEMENTARY_FOOD;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.NUTRITIONAL_STATUS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.YES;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.PREGNANT_STATUS;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.MILD_MAL_NUTRITION;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.MODERATE_MAL_NUTRITION;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.SEVERE_MAL_NUTRITION;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HivPlvHivQuery extends PatientQueryImpDao {
	
	private Date startDate, endDate;
	
	private DbSessionFactory sessionFactory;
	
	@Autowired
	public HivPlvHivQuery(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		setSessionFactory(sessionFactory);
	}
	
	public void setDates(Date _start, Date _date) {
		startDate = _start;
		endDate = _date;
	}
	
	public Set<Integer> getAssessedPatients() {
		
		String outerQuery = "and person_id in (:person_id)";
		
		StringBuilder sql = personIdQuery(getBaseQuery(""), outerQuery);
		
		Cohort cohort = getActiveOnArtCohort("", startDate, endDate, null);
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("person_id", cohort.getMemberIds());
		query.setDate("startDate", startDate);
		query.setDate("endDate", endDate);
		
		return new HashSet<Integer>(query.list());
	}
	
	public Set<Integer> getPatientUndernourished() {
        String outerQuery = "and person_id in (:person_id) and ob.concept_id = (select distinct concept_id from concept where uuid = '"
                + NUTRITIONAL_STATUS
                + "' limit 1) and  ob.value_coded = (select distinct concept_id from concept where uuid = '"
                + UNDERNOURISHED + "' limit 1)";

        StringBuilder sql = personIdQuery(getBaseQuery(""), outerQuery);
        Cohort cohort = getActiveOnArtCohort("", startDate, endDate, null);

        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
        query.setParameterList("person_id", cohort.getMemberIds());
        query.setDate("startDate", startDate);
        query.setDate("endDate", endDate);

        return new HashSet<>(query.list());
    }
	
	private String getBaseQuery(String valueCoded) {
		// Nutrition assessed patient
		String _valueCoded = "is not null";
		
		if (!valueCoded.isEmpty())
			_valueCoded = valueCoded;
		
		return " ob.concept_id = (select distinct concept_id from concept where uuid = '" + NUTRITIONAL_STATUS
		        + "' limit 1)  and value_coded  " + _valueCoded
		        + " and obs_datetime >= :startDate and obs_datetime <= :endDate ";
	}
	
	public Set<Integer> getPatientModerateMalNutrition() {
         String outerQuery = "and person_id in (:person_id) and ob.concept_id = (select distinct concept_id from concept where uuid = '"
                + NUTRITIONAL_STATUS
                + "' limit 1) and  ob.value_coded in (select distinct concept_id from concept where uuid in ('" + MILD_MAL_NUTRITION
                + "','" + MODERATE_MAL_NUTRITION + "' ) ) ";

        StringBuilder sql = personIdQuery(getBaseQuery(""),outerQuery);
        Cohort cohort = getActiveOnArtCohort("", startDate, endDate, null);
     ;

        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
        query.setParameterList("person_id", cohort.getMemberIds());
        query.setDate("startDate", startDate);
        query.setDate("endDate", endDate);

        return new HashSet<>(query.list());
    }
	
	public Set<Integer> getPatientSevereMalNutrition() {
            String outerQuery = "and person_id in (:person_id) and ob.concept_id = (select distinct concept_id from concept where uuid = '"
                + NUTRITIONAL_STATUS
                + "' limit 1) and  ob.value_coded = (select distinct concept_id from concept where uuid  = '" + SEVERE_MAL_NUTRITION
                + "' limit 1) ";
        StringBuilder sql = personIdQuery(getBaseQuery(""), outerQuery);
        Cohort cohort = getActiveOnArtCohort("", startDate, endDate, null);
       
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
        query.setParameterList("person_id", cohort.getMemberIds());
        query.setDate("startDate", startDate);
        query.setDate("endDate", endDate);

        return new HashSet<>(query.list());
    }
	
	public Set<Integer> getPatientMATookSupplement() {
		Cohort cohort = getActiveOnArtCohort("", startDate, endDate, null);
		
		String outerQuery = "and person_id in (:person_id) and ob.concept_id = (select distinct concept_id from concept where uuid = '"
		        + THERAPEUTIC_SUPPLEMENTARY_FOOD
		        + "' limit 1) and  ob.value_coded = (select distinct concept_id from concept where uuid ='" + YES + "') ";
		String subQueryAnswer = " in (select distinct concept_id from concept where uuid in ('" + MILD_MAL_NUTRITION + "','"
		        + MODERATE_MAL_NUTRITION + "') )";
		StringBuilder sql = personIdQuery(getBaseQuery(subQueryAnswer), outerQuery);
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		query.setParameterList("person_id", cohort.getMemberIds());
		query.setDate("startDate", startDate);
		query.setDate("endDate", endDate);
		
		return new HashSet<Integer>(query.list());
	}
	
	public Set<Integer> getPatientSVTookSupplement() {
		Cohort cohort = getActiveOnArtCohort("", startDate, endDate, null);
		
		String outerQuery = "and person_id in (:person_id) and ob.concept_id = (select distinct concept_id from concept where uuid = '"
		        + THERAPEUTIC_SUPPLEMENTARY_FOOD
		        + "' limit 1) and  ob.value_coded = (select distinct concept_id from concept where uuid ='"
		        + YES
		        + "' limit 1 )";
		String subQueryAnswer = "= (select distinct concept_id from concept where uuid = '" + SEVERE_MAL_NUTRITION
		        + "' limit 1 )";
		StringBuilder sql = personIdQuery(getBaseQuery(subQueryAnswer), outerQuery);
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		query.setParameterList("person_id", cohort.getMemberIds());
		query.setDate("startDate", startDate);
		query.setDate("endDate", endDate);
		
		return new HashSet<Integer>(query.list());
	}
	
	public Set<Integer> getPregnant() {
		String outerQuery = "and person_id in (:person_id) and ob.concept_id = (select distinct concept_id from concept where uuid = '"
		        + PREGNANT_STATUS
		        + "' limit 1) and  ob.value_coded = (select distinct concept_id from concept where uuid ='"
		        + YES
		        + "' limit 1) ";
		StringBuilder sql = personIdQuery(getBaseQuery(""), outerQuery);
		Cohort cohort = getActiveOnArtCohort("", startDate, endDate, null);
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		query.setParameterList("person_id", cohort.getMemberIds());
		query.setDate("startDate", startDate);
		query.setDate("endDate", endDate);
		
		return new HashSet<Integer>(query.list());
	}
}
