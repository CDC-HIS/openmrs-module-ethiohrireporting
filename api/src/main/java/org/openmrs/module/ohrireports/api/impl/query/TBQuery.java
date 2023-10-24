package org.openmrs.module.ohrireports.api.impl.query;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.TB_TREATMENT_START_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TB_SCREENING_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.NEGATIVE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.POSITIVE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TB_SCREENING_RESULT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.SPECIMEN_SENT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.YES;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TB_DIAGNOSTIC_TEST_RESULT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.DIAGNOSTIC_TEST;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.SMEAR_ONLY;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.LF_LAM_RESULT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.GENE_XPERT_RESULT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.ADDITIONAL_TEST_OTHERTHAN_GENE_XPERT;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TBQuery extends BaseLineListQuery {
	
	private DbSessionFactory sessionFactory;
	
	@Autowired
	public TBQuery(DbSessionFactory _SessionFactory) {
		super(_SessionFactory);
		sessionFactory = _SessionFactory;
	}
	
	public Cohort getCohortByTbScreenedNegative(Cohort cohort, Date startDate, Date endDate, String gender) {
		Query query = getTBScreenedByResult(cohort, startDate, endDate, gender, NEGATIVE);
		
		return new Cohort(query.list());
	}
	
	public Cohort getCohortByTbScreenedPositive(Cohort cohort, Date startDate, Date endDate, String gender) {
		Query query = getTBScreenedByResult(cohort, startDate, endDate, gender, POSITIVE);
		
		return new Cohort(query.list());
	}
	
	private Query getTBScreenedByResult(Cohort cohort, Date startDate, Date endDate, String gender, String resultConcept) {
		StringBuilder sql = baseQuery(TB_SCREENING_RESULT);
		sql.append(" and " + OBS_ALIAS + "value_coded = " + conceptQuery(resultConcept));
		
		if (!Objects.isNull(gender) && !gender.isEmpty())
			sql.append(" and p.gender = '" + gender + "' ");
		
		sql.append(" and " + OBS_ALIAS + "encounter_id in ");
		sql.append(baseLatestEncounter("" + LATEST_ENCOUNTER_BASE_ALIAS_OBS + "concept_id ="
		        + conceptQuery(TB_SCREENING_DATE) + " and " + " " + LATEST_ENCOUNTER_BASE_ALIAS_OBS
		        + " value_datetime >= :subStartDate and " + " " + LATEST_ENCOUNTER_BASE_ALIAS_OBS
		        + "value_datetime <= :subEndDate and " + " " + LATEST_ENCOUNTER_BASE_ALIAS_OBS + "person_id in (:subCohort)"));
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setTimestamp("subStartDate", startDate);
		query.setTimestamp("subEndDate", endDate);
		query.setParameterList("subCohort", cohort.getMemberIds());
		return query;
	}
	
	public Cohort getSpecimenSent(Cohort cohort, Date startDate, Date endDate) {
		Query query = getByResultTypeQuery(cohort, startDate, endDate, SPECIMEN_SENT, YES);
		return new Cohort(query.list());
	}
	
	public Cohort getSmearOnly(Cohort cohort, Date startDate, Date endDate) {
		Query query = getByResultTypeQuery(cohort, startDate, endDate, DIAGNOSTIC_TEST, SMEAR_ONLY);
		return new Cohort(query.list());
		
	}
	
	public Cohort getLFMResult(Cohort cohort, Date startDate, Date endDate) {
		Query query = getByResultTypeQuery(cohort, startDate, endDate, DIAGNOSTIC_TEST,
		    Arrays.asList(LF_LAM_RESULT, GENE_XPERT_RESULT));
		return new Cohort(query.list());
		
	}
	
	public Cohort getOtherThanLFMResult(Cohort cohort, Date startDate, Date endDate) {
		Query query = getByResultTypeQuery(cohort, startDate, endDate, DIAGNOSTIC_TEST, ADDITIONAL_TEST_OTHERTHAN_GENE_XPERT);
		return new Cohort(query.list());
		
	}
	
	public Cohort getTBDiagnosticPositiveResult(Cohort cohort, Date startDate, Date endDate) {
		Query query = getByResultTypeQuery(cohort, startDate, endDate, TB_DIAGNOSTIC_TEST_RESULT, POSITIVE);
		
		return new Cohort(query.list());
		
	}
	
	private Query getByResultTypeQuery(Cohort cohort, Date startDate, Date endDate, String ConceptQuestionUUId,
	        String answerUUId) {
		StringBuilder sqBuilder = basePersonIdQuery(ConceptQuestionUUId, answerUUId);
		sqBuilder.append(" and " + PERSON_BASE_ALIAS_OBS + "encounter_id in ");
		sqBuilder.append(baseLatestEncounter(
		    "" + LATEST_ENCOUNTER_BASE_ALIAS_OBS + "value_datetime >= :startOnOrAfter and " + ""
		            + LATEST_ENCOUNTER_BASE_ALIAS_OBS + "value_datetime <= :endOnOrBefore  and " + ""
		            + LATEST_ENCOUNTER_BASE_ALIAS_OBS + "person_id in (:patientIds) and " + LATEST_ENCOUNTER_BASE_ALIAS_OBS
		            + "concept_id = " + conceptQuery(TB_SCREENING_DATE)).toString());
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqBuilder.toString());
		
		query.setTimestamp("startOnOrAfter", startDate);
		query.setTimestamp("endOnOrBefore", endDate);
		query.setParameterList("patientIds", cohort.getMemberIds());
		return query;
	}
	
	public HashMap<Integer, Object> getByResultTypeQuery(Cohort cohort, Date startDate, Date endDate,
	        String ConceptQuestionUUId) {
		StringBuilder sqBuilder = baseConceptQuery(ConceptQuestionUUId);
		sqBuilder.append(" and " + CONCEPT_BASE_ALIAS_OBS + "encounter_id in ");
		sqBuilder.append(baseLatestEncounter(
		    "" + LATEST_ENCOUNTER_BASE_ALIAS_OBS + "value_datetime >= :startOnOrAfter and " + ""
		            + LATEST_ENCOUNTER_BASE_ALIAS_OBS + "value_datetime <= :endOnOrBefore  and " + ""
		            + LATEST_ENCOUNTER_BASE_ALIAS_OBS + "person_id in (:patientIds) and " + LATEST_ENCOUNTER_BASE_ALIAS_OBS
		            + "concept_id = " + conceptQuery(TB_SCREENING_DATE)).toString());
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqBuilder.toString());
		
		query.setTimestamp("startOnOrAfter", startDate);
		query.setTimestamp("endOnOrBefore", endDate);
		query.setParameterList("patientIds", cohort.getMemberIds());
		return getDictionary(query);
	}
	
	private Query getByResultTypeQuery(Cohort cohort, Date startDate, Date endDate, String ConceptQuestionUUId,
	        List<String> answerUUId) {
		StringBuilder sqBuilder = basePersonIdQuery(ConceptQuestionUUId, answerUUId);
		sqBuilder.append(" and " + PERSON_BASE_ALIAS_OBS + "encounter_id in ");
		sqBuilder.append(baseLatestEncounter(
		    "" + LATEST_ENCOUNTER_BASE_ALIAS_OBS + "value_datetime >= :startOnOrAfter and " + ""
		            + LATEST_ENCOUNTER_BASE_ALIAS_OBS + "value_datetime <= :endOnOrBefore  and " + ""
		            + LATEST_ENCOUNTER_BASE_ALIAS_OBS + "person_id in (:patientIds) and " + LATEST_ENCOUNTER_BASE_ALIAS_OBS
		            + "concept_id = " + conceptQuery(TB_SCREENING_DATE)).toString());
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqBuilder.toString());
		
		query.setTimestamp("startOnOrAfter", startDate);
		query.setTimestamp("endOnOrBefore", endDate);
		query.setParameterList("patientIds", cohort.getMemberIds());
		return query;
	}
	
	public Cohort getTBScreenedCohort(Cohort cohort, Date starDate, Date endDate) {
		StringBuilder sql = baseQuery(TB_SCREENING_DATE);
		sql.append(" and " + OBS_ALIAS + "obs_id in ");
		sql.append(baseSubQuery(" " + SUB_QUERY_BASE_ALIAS_OBS + " concept_id =" + conceptQuery(TB_SCREENING_DATE) + " and "
		        + SUB_QUERY_BASE_ALIAS_OBS + " value_datetime >= :subStartDate  and " + SUB_QUERY_BASE_ALIAS_OBS
		        + "value_datetime <= :subEndDate and " + " " + SUB_QUERY_BASE_ALIAS_OBS + "person_id in (:subCohort)"));
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setTimestamp("subStartDate", starDate);
		query.setTimestamp("subEndDate", endDate);
		query.setParameterList("subCohort", cohort.getMemberIds());
		
		return new Cohort(query.list());
	}
	
	public Cohort getTBTreatmentStartedCohort(Cohort cohort, Date starDate, Date endDate, String gender) {
		
		StringBuilder sql = baseQuery(TB_TREATMENT_START_DATE);
		
		if (!Objects.isNull(gender) && !gender.isEmpty()) {
			sql.append(" and p.gender = '" + gender + "'");
		}
		sql.append(" and " + OBS_ALIAS + "obs_id in ");
		sql.append(baseSubQuery(" " + SUB_QUERY_BASE_ALIAS_OBS + " concept_id =" + conceptQuery(TB_TREATMENT_START_DATE)
		        + " and " + SUB_QUERY_BASE_ALIAS_OBS + " value_datetime>= :subStartDate  and " + SUB_QUERY_BASE_ALIAS_OBS
		        + "value_datetime <= :subEndDate and " + " " + SUB_QUERY_BASE_ALIAS_OBS + "person_id in (:subCohort)"));
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setTimestamp("subStartDate", starDate);
		query.setTimestamp("subEndDate", endDate);
		query.setParameterList("subCohort", cohort.getMemberIds());
		
		return new Cohort(query.list());
	}
	
	public HashMap<Integer, Object> getTBScreenedDate(Cohort cohort, Date startDate, Date endDate) {
		
		StringBuilder sql = baseValueDateQuery(TB_SCREENING_DATE);
		sql.append(" and " + VALUE_DATE_BASE_ALIAS_OBS + "obs_id in ");
		sql.append(baseSubQuery(
		    "" + SUB_QUERY_BASE_ALIAS_OBS + "value_datetime >= :startOnOrAfter and " + "" + SUB_QUERY_BASE_ALIAS_OBS
		            + "value_datetime <= :endOnOrBefore  and " + "" + SUB_QUERY_BASE_ALIAS_OBS
		            + "person_id in (:patientIds) and " + SUB_QUERY_BASE_ALIAS_OBS + "concept_id = "
		            + conceptQuery(TB_SCREENING_DATE)).toString());
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameter("startOnOrAfter", startDate);
		query.setParameter("endOnOrBefore", endDate);
		
		query.setParameterList("patientIds", cohort.getMemberIds());
		
		return getDictionary(query);
		
	}
	
	public HashMap<Integer, Object> getTBTreatmentStartDate(Cohort cohort, Date startDate, Date endDate) {
		
		StringBuilder sql = baseValueDateQuery(TB_TREATMENT_START_DATE);
		sql.append(" and " + VALUE_DATE_BASE_ALIAS_OBS + "obs_id in ");
		sql.append(baseSubQuery(
		    "" + SUB_QUERY_BASE_ALIAS_OBS + "value_datetime >= :startOnOrAfter and " + "" + SUB_QUERY_BASE_ALIAS_OBS
		            + "value_datetime <= :endOnOrBefore  and " + "" + SUB_QUERY_BASE_ALIAS_OBS
		            + "person_id in (:patientIds) and " + SUB_QUERY_BASE_ALIAS_OBS + "concept_id = "
		            + conceptQuery(TB_TREATMENT_START_DATE)).toString());
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameter("startOnOrAfter", startDate);
		query.setParameter("endOnOrBefore", endDate);
		
		query.setParameterList("patientIds", cohort.getMemberIds());
		
		return getDictionary(query);
		
	}
	
	public HashMap<Integer, Object> getTBScreenedResult(Cohort cohort, Date startDate, Date endDate) {
		
		StringBuilder sql = baseConceptQuery(TB_SCREENING_RESULT);
		sql.append(" and " + CONCEPT_BASE_ALIAS_OBS + "person_id in (:outerPatientIds)  ");
		sql.append(" and " + CONCEPT_BASE_ALIAS_OBS + "encounter_id in ");
		sql.append(baseLatestEncounter(
		    "" + LATEST_ENCOUNTER_BASE_ALIAS_OBS + "value_datetime >= :startOnOrAfter and " + ""
		            + LATEST_ENCOUNTER_BASE_ALIAS_OBS + "value_datetime <= :endOnOrBefore  and " + ""
		            + LATEST_ENCOUNTER_BASE_ALIAS_OBS + "person_id in (:patientIds) and " + LATEST_ENCOUNTER_BASE_ALIAS_OBS
		            + "concept_id = " + conceptQuery(TB_SCREENING_DATE)).toString());
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("outerPatientIds", cohort.getMemberIds());
		
		query.setParameter("startOnOrAfter", startDate);
		query.setParameter("endOnOrBefore", endDate);
		
		query.setParameterList("patientIds", cohort.getMemberIds());
		
		return getDictionary(query);
		
	}
}
