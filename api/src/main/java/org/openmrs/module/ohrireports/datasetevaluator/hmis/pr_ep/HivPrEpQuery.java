package org.openmrs.module.ohrireports.datasetevaluator.hmis.pr_ep;

import java.util.*;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.constants.*;
import org.openmrs.module.ohrireports.reports.linelist.PEPReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions.FOLLOW_UP_DATE;

@Component
public class HivPrEpQuery extends PatientQueryImpDao {
	
	private final DbSessionFactory sessionFactory;
	
	private Date startDate;
	
	private Date endDate;
	
	private List<Integer> baseEncounter;
	
	private Cohort baseCohort;
	
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
	
	public void setEndDate(Date endDate, String filteringConcept, String encounterTypeUUid) {
		this.endDate = endDate;
		baseEncounter = encounterQuery.getEncounters(Arrays.asList(filteringConcept), startDate, endDate);
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
		String subQueryClauses = "" + PERSON_ID_SUB_ALIAS_OBS + "concept_id =" + conceptQuery(PEPReport.PR_EP_STARTED)
		        + " and " + PERSON_ID_SUB_ALIAS_OBS + "voided = false and " + PERSON_ID_SUB_ALIAS_OBS
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
		
		String condition = " and " + PERSON_ID_ALIAS_OBS + "concept_id ="
		        + conceptQuery(PrepConceptQuestions.FEMALE_SEX_WORKER) + " and " + PERSON_ID_ALIAS_OBS + "value_coded = "
		        + conceptQuery(ConceptAnswer.YES) + " ";
		StringBuilder sql = personIdQuery(isCurrent ? getCurrQueryClauses() : getSubQueryClauses(), condition);
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameter("endOnOrAfter", endDate);
		
		if (isCurrent)
			query.setParameterList("drugs", getPrEpDrugs());
		
		return query.list().size();
		
	}
	
	public Cohort getCategoryOnPrep(String clientCategoryUUid, Cohort cohort) {
		
		String stringQuery = "select distinct ob.person_id from obs as ob " + " where person_id in (:cohorts) "
		        + "and  concept_id = " + conceptQuery(clientCategoryUUid) + " and value_coded = "
		        + conceptQuery(ConceptAnswer.YES);
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringQuery);
		query.setParameterList("cohorts", cohort.getMemberIds());
		return new Cohort(query.list());
	}
	
	public Set<Integer> getPrEpDrugs() {
		StringBuilder sql = new StringBuilder("select distinct concept_id from concept ");
		sql.append("where uuid in ('" + RegimentConstant.TDF_TENOFOVIR_DRUG + "','" + RegimentConstant.TDF_FTC_DRUG + "','"
		        + RegimentConstant.TDF_3TC_DRUG + "')");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		return new HashSet<Integer>(query.list());
	}
	
	public Integer getCountByExposureType(String uuid) {
		
		String condition = " and " + PERSON_ID_ALIAS_OBS + "concept_id ="
		        + conceptQuery(PostExposureConceptQuestions.PEP_EXPOSURE_TYPE) + " and " + PERSON_ID_ALIAS_OBS
		        + "value_coded = " + conceptQuery(uuid) + "";
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
	
	public Cohort getAllPrEPCurr() {
		List<Integer> basePrEPCTEncounter = encounterQuery.getEncounters(Arrays.asList(FOLLOW_UP_DATE), null, endDate,
		    EncounterType.PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		List<Integer> filteredEncounterForPrepCT = filterEncounterByPrePStatusForPrepCT(
		    Arrays.asList(PrepConceptQuestions.PREP_DOSE_END_DATE), null, endDate, basePrEPCTEncounter);
		
		baseCohort = getCohort(filteredEncounterForPrepCT);
		
		return baseCohort;
	}
	
	private List<Integer> filterEncounterByPrePStatusForPrepCT(List<String> questionConcept, Date startDate, Date endDate, List<Integer> encounters) {

		if (encounters.isEmpty())
			return encounters;

		if (questionConcept == null || questionConcept.isEmpty())
			return new ArrayList<>();

		StringBuilder builder = new StringBuilder("select distinct ob.encounter_id from obs as ob inner join ");
		builder.append("(select Max(obs_enc.value_datetime) as value_datetime, person_id as person_id from obs as obs_enc");

		builder.append(" where obs_enc.concept_id in ")
				.append(conceptQuery(questionConcept));

		if (startDate != null)
			builder.append(" and obs_enc.value_datetime <= :start ");

		if (endDate != null)
			builder.append(" and obs_enc.value_datetime >= :end ");
		builder.append(" and obs_enc.encounter_id in (:subLatestFollowUpDates)");

		builder.append(" GROUP BY obs_enc.person_id ) as sub ");
		builder.append(" on ob.value_datetime = sub.value_datetime and ob.person_id = sub.person_id ");

		builder.append(" and ob.concept_id in ").append(conceptQuery(questionConcept));
		builder.append(" and ob.encounter_id in (:latestFollowUpDates)");

		Query q = sessionFactory.getCurrentSession().createSQLQuery(builder.toString());

		if (startDate != null)
			q.setDate("start", startDate);

		if (endDate != null)
			q.setDate("end", endDate);

		q.setParameterList("latestFollowUpDates", encounters);
		q.setParameterList("subLatestFollowUpDates", encounters);

		List list = q.list();

		if (list != null) {
			return (List<Integer>) list;
		} else {
			return new ArrayList<Integer>();
		}
	}
}
