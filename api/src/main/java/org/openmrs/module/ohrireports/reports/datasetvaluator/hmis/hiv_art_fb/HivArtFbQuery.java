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
	
	@Autowired
	public HivArtFbQuery(DbSessionFactory sessionFactory) {
		
		setSessionFactory(sessionFactory);
		
		this.sessionFactory = sessionFactory;
	}
	
	public void setDate(Date start, Date end) {
		startDate = start;
		endDate = end;
	}
	
	public Set<Integer> GetPatientsOnFamilyPlanning() {
		Cohort cohort = getOnArtCohorts("", startDate, endDate, null);
		
		StringBuilder sql = personIdQuery(getBaseQuery(" "),
		    " and ob.concept_id = (select distinct concept_id from concept where uuid = '" + PREGNANT_STATUS
		            + "' limit 1) and value_coded = (select distinct concept_id from concept where uuid = '" + NO
		            + "' limit 1) and ob.person_id in (:art_patients)");
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setTimestamp("startDate", startDate);
		query.setTimestamp("endDate", endDate);
		query.setParameterList("art_patients", cohort.getMemberIds());
		
		return new HashSet<Integer>(query.list());
		
	}
	
	public Integer getPatientByMethodOfFP(String conceptTypeUUID) {
		Cohort cohort = getOnArtCohorts("", startDate, endDate, null);
		
		String subQuery = " = (select distinct concept_id from concept where uuid = '" + conceptTypeUUID + "' limit 1) ";
		StringBuilder sql = personIdQuery(getBaseQuery(subQuery),
		    " and ob.concept_id = (select distinct concept_id from concept where uuid = '" + PREGNANT_STATUS
		            + "' limit 1) and value_coded = (select distinct concept_id from concept where uuid = '" + NO
		            + "' limit 1) and ob.person_id in (:art_patients)");
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setTimestamp("startDate", startDate);
		query.setTimestamp("endDate", endDate);
		query.setParameterList("art_patients", cohort.getMemberIds());
		
		return query.list().size();
	}
	
	public Integer getPatientByMethodOfOtherFP(List<String> conceptTypeUUID) {
		Cohort cohort = getOnArtCohorts("", startDate, endDate, null);
		
		String subQuery = " not in (select distinct concept_id from concept where uuid in (:uuids))";
		StringBuilder sql = personIdQuery(getBaseQuery(subQuery),
		    " and ob.concept_id = (select distinct concept_id from concept where uuid = '" + PREGNANT_STATUS
		            + "' limit 1) and value_coded = (select distinct concept_id from concept where uuid = '" + NO
		            + "' limit 1) and ob.person_id in  (:art_patients)");
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setTimestamp("startDate", startDate);
		query.setTimestamp("endDate", endDate);
		query.setParameterList("art_patients", cohort.getMemberIds());
		query.setParameterList("uuids", conceptTypeUUID);
		return query.list().size();
	}
	
	private String getBaseQuery(String valueCoded) {
		
		String _valueCoded = "is not null";
		
		if (!valueCoded.isEmpty())
			_valueCoded = valueCoded;
		
		return " ob.concept_id = (select distinct concept_id from concept where uuid = '" + FAMILY_PLANNING_METHODS
		        + "' limit 1)  and value_coded  " + _valueCoded
		        + " and obs_datetime >= :startDate and obs_datetime <= :endDate ";
	}
}
