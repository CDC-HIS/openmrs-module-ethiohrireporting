package org.openmrs.module.ohrireports.datasetevaluator.hmis.pr_ep;

import java.util.*;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.FOLLOW_UP_DATE;

@Component
public class HivPrEpQuery extends PatientQueryImpDao {
	
	private DbSessionFactory sessionFactory;
	
	private Date startDate;
	
	private Date endDate;
	
	private List<Integer> baseEncounter;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	private List<Integer> currentEncounter;
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Date endDate, String filteringDate, String encounterTypeUUid) {
		this.endDate = endDate;
		baseEncounter = encounterQuery.getEncounters(Arrays.asList(filteringDate), startDate, endDate);
		currentEncounter = baseEncounter = refineBaseEncounter(encounterTypeUUid);
	}
	
	private List<Integer> refineBaseEncounter(String encounterTypeUUid) {
		StringBuilder stringQuery = new StringBuilder("select distinct ob.encounter_id from obs as ob ");
		stringQuery.append("Inner join encounter enc on ob.encounter_id = enc.encounter_id ");
		stringQuery.append("Inner join encounter_type et on  et.encounter_type_id = enc.encounter_type ");
		stringQuery.append(" where ob.encounter_id in (:baseEncounter) ");
		stringQuery.append(" and et.uuid = '").append(encounterTypeUUid).append("'");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringQuery.toString());
		
		query.setParameterList("baseEncounter", baseEncounter);
		List<Integer> response = (List<Integer>) query.list();
		return response;
	}
	
	@Autowired
	public HivPrEpQuery(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		super.setSessionFactory(sessionFactory);
	}
	
	/*public void initializeDate(Date start, Date end) {
		startDate = start;
		endDate = end;
	}*/
	
	public Set<Integer> getPatientOnPrEpCurr() {
        StringBuilder sql = personIdQuery(getCurrQueryClauses(), "");

        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());

        query.setParameter("endOnOrAfter", endDate);

        query.setParameterList("drugs", getPrEpDrugs().toArray());

        return new HashSet<>(query.list());
    }
	
	public Set<Integer> getPatientStartedPrep() {

        String subQueryClauses = getSubQueryClauses();
        StringBuilder sql = personIdQuery(subQueryClauses, "");
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());


        query.setParameter("endOnOrAfter", endDate);

        return new HashSet<>(query.list());
    }
	
	/*
	 * Newly enrolled patients to Prep
	 */
	private String getSubQueryClauses() {
		String subQueryClauses = "" + PERSON_ID_SUB_ALIAS_OBS + "concept_id =" + conceptQuery(PR_EP_STARTED) + " and "
		        + PERSON_ID_SUB_ALIAS_OBS + "voided = false and " + PERSON_ID_SUB_ALIAS_OBS
		        + "value_datetime >= :endOnOrAfter  ";
		return subQueryClauses;
	}
	
	/*
	 * curr concerned more about the drug they are taking
	 */
	private String getCurrQueryClauses() {
		String subQueryClauses = " " + PERSON_ID_SUB_ALIAS_OBS + "value_coded in (:drugs) and " + PERSON_ID_SUB_ALIAS_OBS
		        + "voided = false and  " + PERSON_ID_SUB_ALIAS_OBS + "obs_datetime >= :endOnOrAfter ";
		return subQueryClauses;
	}
	
	public Integer getFemaleSexWorkerOnPrep(Boolean isCurrent) {
		
		String condition = " and " + PERSON_ID_ALIAS_OBS + "concept_id =" + conceptQuery(FEMALE_SEX_WORKER) + " and "
		        + PERSON_ID_ALIAS_OBS + "value_coded = " + conceptQuery(YES) + " ";
		StringBuilder sql = personIdQuery(isCurrent ? getCurrQueryClauses() : getSubQueryClauses(), condition);
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameter("endOnOrAfter", endDate);
		
		if (isCurrent)
			query.setParameterList("drugs", getPrEpDrugs());
		
		return query.list().size();
		
	}
	
	public Cohort getCategoryOnPrep(String clientCategoryUUid, Cohort cohort) {
		
		String stringQuery = "select distinct ob.person_id from obs as ob " + " where person_id in (:cohorts) "
		        + "and  concept_id = " + conceptQuery(clientCategoryUUid) + " and value_coded = " + conceptQuery(YES);
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringQuery);
		query.setParameterList("cohorts", cohort.getMemberIds());
		return new Cohort(query.list());
	}
	
	public Integer getDiscordantCoupleOnPrep(Boolean isCurrent) {
		
		String condition = " and " + PERSON_ID_ALIAS_OBS + "value_coded = " + conceptQuery(DISCORDANT_COUPLE) + "";
		StringBuilder sql = personIdQuery(isCurrent ? getCurrQueryClauses() : getSubQueryClauses(), condition);
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameter("endOnOrAfter", endDate);
		
		if (isCurrent)
			query.setParameterList("drugs", getPrEpDrugs());
		
		return query.list().size();
		
	}
	
	public Set<Integer> getPrEpDrugs() {
		StringBuilder sql = new StringBuilder("select distinct concept_id from concept ");
		sql.append("where uuid in ('" + TDF_TENOFOVIR_DRUG + "','" + TDF_FTC_DRUG + "','" + TDF_3TC_DRUG + "')");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		return new HashSet<Integer>(query.list());
	}
	
	public Integer getCountByExposureType(String uuid) {
		
		String condition = " and " + PERSON_ID_ALIAS_OBS + "concept_id =" + conceptQuery(EXPOSURE_TYPE) + " and "
		        + PERSON_ID_ALIAS_OBS + "value_coded = " + conceptQuery(uuid) + "";
		StringBuilder sql = personIdQuery(getCurrQueryClauses(), condition);
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameter("endOnOrAfter", endDate);
		
		query.setParameterList("drugs", getPrEpDrugs());
		
		return query.list().size();
		
	}
	
	public Cohort getCohortByConceptAndBaseEncounter(String questionConcept) {
		String stringQuery = "SELECT distinct person_id\n" + "FROM obs\n" + "WHERE concept_id = "
		        + conceptQuery(questionConcept) + "and encounter_id in ( :baseEncounter)";
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringQuery);
		query.setParameterList("baseEncounter", baseEncounter);
		return new Cohort(query.list());
	}
	
}
