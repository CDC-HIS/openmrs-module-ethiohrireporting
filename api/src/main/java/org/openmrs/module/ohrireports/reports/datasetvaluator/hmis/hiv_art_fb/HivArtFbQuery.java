package org.openmrs.module.ohrireports.reports.datasetvaluator.hmis.hiv_art_fb;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.PREGNANT_STATUS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.FAMILY_PLANNING_METHODS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.NO;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HivArtFbQuery extends PatientQueryImpDao {
	
	private DbSessionFactory sessionFactory;
	
	private Date startDate, endDate;
	
	private Cohort cohort = null;
	
	@Autowired
	public HivArtFbQuery(DbSessionFactory sessionFactory) {
		
		setSessionFactory(sessionFactory);
		
		this.sessionFactory = sessionFactory;
	}
	
	public void setDate(Date start, Date end) {
		startDate = start;
		endDate = end;
		if (cohort == null)
			cohort = getActiveOnArtCohort("", startDate, endDate, null);
		
	}
	
	public Set<Integer> GetPatientsOnFamilyPlanning() {
		
		StringBuilder sqlBuilder = new StringBuilder(
		        "select distinct patient_id from encounter as enc where encounter_id in ");
		sqlBuilder.append(" (select obt.encounter_id from obs as obt where obt.concept_id = "
		        + conceptQuery(FAMILY_PLANNING_METHODS));
		sqlBuilder.append(" and obt.value_coded  is not null and obs_datetime >= :startDate and obs_datetime <= :endDate");
		sqlBuilder.append(" and obt.person_id in (select subOb.person_id from obs as subOb where ");
		sqlBuilder.append(" subOb.concept_id =" + conceptQuery(PREGNANT_STATUS) + " and subOb.value_coded ="
		        + conceptQuery(NO) + " and obs_datetime >= :subStartDate and obs_datetime <= :subEndDate))");
		sqlBuilder.append(" and enc.patient_id in (:cohort)");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
		
		query.setTimestamp("startDate", startDate);
		query.setTimestamp("endDate", endDate);
		query.setTimestamp("subStartDate", startDate);
		query.setTimestamp("subEndDate", endDate);
		query.setParameterList("cohort", cohort.getMemberIds());
		
		return new HashSet<Integer>(query.list());
		
	}
	
	public Integer getPatientByMethodOfFP(String conceptTypeUUID) {
		
		StringBuilder sqlBuilder = new StringBuilder(
		        "select distinct patient_id from encounter as enc where encounter_id in ");
		sqlBuilder.append(" (select obt.encounter_id from obs as obt where obt.concept_id = "
		        + conceptQuery(FAMILY_PLANNING_METHODS));
		sqlBuilder.append(" and obt.value_coded = " + conceptQuery(conceptTypeUUID));
		sqlBuilder.append(" and obs_datetime >= :startDate and obs_datetime <= :endDate");
		sqlBuilder.append(" and obt.person_id in (select subOb.person_id from obs as subOb where ");
		sqlBuilder.append(" subOb.concept_id =" + conceptQuery(PREGNANT_STATUS) + " and subOb.value_coded ="
		        + conceptQuery(NO) + " and obs_datetime >= :subStartDate and obs_datetime <= :subEndDate))");
		sqlBuilder.append(" and enc.patient_id in (:cohort)");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
		
		query.setTimestamp("startDate", startDate);
		query.setTimestamp("endDate", endDate);
		query.setTimestamp("subStartDate", startDate);
		query.setTimestamp("subEndDate", endDate);
		query.setParameterList("cohort", cohort.getMemberIds());
		
		return query.list().size();
	}
	
	public Integer getPatientByMethodOfOtherFP(List<String> conceptTypeUUID) {
		StringBuilder sqlBuilder = new StringBuilder(
		        "select distinct patient_id from encounter as enc where encounter_id in ");
		sqlBuilder.append(" (select obt.encounter_id from obs as obt where obt.concept_id = "
		        + conceptQuery(FAMILY_PLANNING_METHODS));
		sqlBuilder.append(" and obt.value_coded in " + conceptQuery(conceptTypeUUID));
		sqlBuilder.append(" and obs_datetime >= :startDate and obs_datetime <= :endDate");
		sqlBuilder.append(" and obt.person_id in (select subOb.person_id from obs as subOb where ");
		sqlBuilder.append(" subOb.concept_id =" + conceptQuery(PREGNANT_STATUS) + " and subOb.value_coded ="
		        + conceptQuery(NO) + " and obs_datetime >= :subStartDate and obs_datetime <= :subEndDate))");
		sqlBuilder.append(" and enc.patient_id in (:cohort)");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
		
		query.setTimestamp("startDate", startDate);
		query.setTimestamp("endDate", endDate);
		query.setTimestamp("subStartDate", startDate);
		query.setTimestamp("subEndDate", endDate);
		query.setParameterList("cohort", cohort.getMemberIds());
		
		return query.list().size();
	}
	
}
