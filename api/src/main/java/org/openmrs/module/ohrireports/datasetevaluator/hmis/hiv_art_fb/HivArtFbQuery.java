package org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_art_fb;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Component
public class HivArtFbQuery extends PatientQueryImpDao {
	
	private DbSessionFactory sessionFactory;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	private Date startDate, endDate;
	
	private Cohort cohort = null;
	
	private List<Integer> baseEncounter;
	
	public Cohort getCohort() {
		return cohort;
	}
	
	public List<Integer> getBaseEncounter() {
		return baseEncounter;
	}
	
	@Autowired
	public HivArtFbQuery(DbSessionFactory sessionFactory) {
		
		setSessionFactory(sessionFactory);
		this.sessionFactory = sessionFactory;
	}
	
	public void generateReport(Date start, Date end) {
		startDate = start;
		endDate = end;
		baseEncounter = encounterQuery.getAliveFollowUpEncounters(null, end);
		cohort = getActiveOnArtCohort("F", null, endDate, null, baseEncounter);
		cohort = getPatientsOnFamilyPlanning();
	}
	
	private Cohort getPatientsOnFamilyPlanning() {
		StringBuilder sqlBuilder = new StringBuilder("select distinct fb.person_id from obs as fb where fb.concept_id  =")
		        .append(conceptQuery(FAMILY_PLANNING_METHODS));
		sqlBuilder
		        .append(" and fb.value_coded is not null and fb.encounter_id in (:encounters) and fb.person_id in (select distinct p.person_id from obs as p where ");
		sqlBuilder.append("  p.concept_id =").append(conceptQuery(PREGNANT_STATUS)).append(" and p.value_coded <> ")
		        .append(conceptQuery(YES));
		sqlBuilder.append(" and p.encounter_id in (:pEncounters) and p.person_id in(:pPersonIds)) ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
		query.setParameterList("encounters", baseEncounter);
		query.setParameterList("pEncounters", baseEncounter);
		query.setParameterList("pPersonIds", cohort.getMemberIds());
		
		return new Cohort(query.list());
		
	}
	
	public Integer getPatientByMethodOfOtherFP(List<String> conceptTypeUUID) {
		String sqlBuilder = "select obt.person_id from obs as obt where obt.concept_id = "
		        + conceptQuery(FAMILY_PLANNING_METHODS) + " and obt.value_coded in " + conceptQuery(conceptTypeUUID)
		        + " and obt.encounter_id in (:encounter)" + " and obt.person_id in (:cohort)";
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder);
		query.setParameterList("encounter", baseEncounter);
		query.setParameterList("cohort", cohort.getMemberIds());
		
		return query.list().size();
	}
	
}
