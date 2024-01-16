package org.openmrs.module.ohrireports.api.impl.query;

import org.hibernate.Query;
import org.jetbrains.annotations.NotNull;
import org.openmrs.Cohort;
import org.openmrs.CohortMembership;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISUtilies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.*;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISUtilies.getDictionary;

@Component
public class MLQuery extends PatientQueryImpDao {
	
	private DbSessionFactory sessionFactory;
	
	public Cohort cohort;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	private List<Integer> baseEncounter;
	
	public HashMap<Integer, BigDecimal> getInterruptionMonthList() {
		return interruptionMonthList;
	}
	
	private HashMap<Integer, BigDecimal> interruptionMonthList;
	
	public List<Integer> getBaseEncounter() {
		return baseEncounter;
	}
	
	@Autowired
	public MLQuery(DbSessionFactory _SessionFactory) {
		sessionFactory = _SessionFactory;
		setSessionFactory(sessionFactory);
	}
	
	public Cohort getCohortML(Date start, Date end) {
		
		baseEncounter = encounterQuery.getLatestDateByFollowUpDate(null, end);
		List<Integer> betweenDateEncounter = encounterQuery.getLatestDateByFollowUpDate(start, end);
		List<Integer> encounters = encounterQuery.getAliveFollowUpEncounters(null, start);
		Cohort currOnStartDateOfReportCohort = getActiveOnArtCohort("", null, start, null, encounters);
		
		Cohort newWithInTheReportingDate = getNewOnArtCohort("", start, end, null, baseEncounter);
		
		for (CohortMembership membership : newWithInTheReportingDate.getMemberships()) {
			currOnStartDateOfReportCohort.addMembership(membership);
		}
		
		cohort = getCohort(end, currOnStartDateOfReportCohort, betweenDateEncounter);
		return cohort;
	}
	
	public Cohort getDied(Cohort cohort) {
		return getByConcept(asList(DIED), cohort);
	}
	
	public HashMap<Integer, BigDecimal> getInterruptionMonth(Date endDate) {
        interruptionMonthList = new HashMap<>();
        interruptionMonthList.putAll(getInterruptionMonthByFollowUpStatus());
        interruptionMonthList.putAll(getInterruptionMonthByTreatmentEnd(endDate));
        return interruptionMonthList;
    }
	
	private HashMap<Integer, BigDecimal> getInterruptionMonthByFollowUpStatus() {
		StringBuilder sqlBuilder = new StringBuilder(
		        " SELECT ob.person_id, FLOOR (TIMESTAMPDIFF(MONTH, artDate.value_datetime, treatmentEnd.value_datetime)");
		sqlBuilder
		        .append(" + DATEDIFF(   treatmentEnd.value_datetime,    artDate.value_datetime + INTERVAL TIMESTAMPDIFF(MONTH, artDate.value_datetime,");
		sqlBuilder
		        .append(" treatmentEnd.value_datetime) MONTH ) / DATEDIFF(   artDate.value_datetime + INTERVAL TIMESTAMPDIFF(MONTH, artDate.value_datetime, ");
		sqlBuilder.append(" treatmentEnd.value_datetime) + 1 MONTH,");
		sqlBuilder
		        .append("    artDate.value_datetime + INTERVAL TIMESTAMPDIFF(MONTH, artDate.value_datetime, treatmentEnd.value_datetime) MONTH ) ) as interruptionMonth");
		sqlBuilder.append(" from  obs as ob ");
		sqlBuilder.append(" inner join ");
		sqlBuilder.append(" ( SELECT ob_followup.value_datetime , ob_followup.person_id  from  obs as ob_followup");
		sqlBuilder.append(" where  ob_followup.concept_id =").append(conceptQuery(ART_START_DATE));
		sqlBuilder
		        .append(" and ob_followup.encounter_id in (:artEncounter) ) as artDate on artDate.person_id =ob.person_id");
		sqlBuilder.append(" inner join ");
		sqlBuilder.append(" ( SELECT ob_followup.value_datetime , ob_followup.person_id  from  obs as ob_followup");
		sqlBuilder.append(" where ob_followup.concept_id =").append(conceptQuery(FOLLOW_UP_DATE));
		sqlBuilder
		        .append(" and ob_followup.encounter_id in (:followUpEncounter) ) as treatmentEnd on treatmentEnd.person_id =ob.person_id");
		sqlBuilder.append(" where ob.concept_id =  ").append(conceptQuery(FOLLOW_UP_STATUS))
		        .append(" AND ob.value_coded in ").append(conceptQuery(asList(LOST_TO_FOLLOW_UP, DROP)))
		        .append(" and ob.person_id in (:personIds) and ob.encounter_id in (:betweenEncounters) ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
		
		query.setParameterList("personIds", cohort.getMemberIds());
		query.setParameterList("followUpEncounter", baseEncounter);
		query.setParameterList("artEncounter", baseEncounter);
		query.setParameterList("betweenEncounters", baseEncounter);
		
		return HMISUtilies.getDictionaryWithBigDecimal(query);
	}
	
	private HashMap<Integer, BigDecimal> getInterruptionMonthByTreatmentEnd(Date end) {
		StringBuilder sqlBuilder = new StringBuilder(
		        " SELECT ob.person_id, FLOOR (TIMESTAMPDIFF(MONTH, artDate.value_datetime, treatmentEnd.value_datetime)");
		sqlBuilder
		        .append(" + DATEDIFF(   treatmentEnd.value_datetime,    artDate.value_datetime + INTERVAL TIMESTAMPDIFF(MONTH, artDate.value_datetime,");
		sqlBuilder
		        .append(" treatmentEnd.value_datetime) MONTH ) / DATEDIFF(   artDate.value_datetime + INTERVAL TIMESTAMPDIFF(MONTH, artDate.value_datetime, ");
		sqlBuilder.append(" treatmentEnd.value_datetime) + 1 MONTH,");
		sqlBuilder
		        .append("    artDate.value_datetime + INTERVAL TIMESTAMPDIFF(MONTH, artDate.value_datetime, treatmentEnd.value_datetime) MONTH 	)) as interruptionMonth");
		sqlBuilder.append(" from  obs as ob ");
		sqlBuilder.append(" inner join ");
		sqlBuilder.append("(    SELECT       ob_followup.value_datetime , ob_followup.person_id  from  obs as ob_followup");
		sqlBuilder.append(" where    ob_followup.concept_id =").append(conceptQuery(ART_START_DATE));
		sqlBuilder
		        .append(" and ob_followup.encounter_id in (:artEncounter) ) as artDate on artDate.person_id =ob.person_id");
		sqlBuilder.append(" inner join ");
		sqlBuilder.append(" ( SELECT ob_followup.value_datetime , ob_followup.person_id  from  obs as ob_followup");
		sqlBuilder.append(" where ob_followup.concept_id =").append(conceptQuery(TREATMENT_END_DATE));
		sqlBuilder
		        .append(" and ob_followup.encounter_id in (:followUpEncounter) ) as treatmentEnd on treatmentEnd.person_id =ob.person_id");
		sqlBuilder.append(" where ob.concept_id =  ").append(conceptQuery(TREATMENT_END_DATE))
		        .append(" AND ob.value_datetime < :endDate ")
		        .append(" and ob.person_id in (:personIds) and ob.encounter_id in (:betweenEncounters) ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
		
		query.setDate("endDate", end);
		query.setParameterList("personIds", cohort.getMemberIds());
		query.setParameterList("followUpEncounter", baseEncounter);
		query.setParameterList("artEncounter", baseEncounter);
		query.setParameterList("betweenEncounters", baseEncounter);
		
		return HMISUtilies.getDictionaryWithBigDecimal(query);
	}
	
	public Cohort getStopOrRefused(Cohort cohort) {
		return getByConcept(asList(STOP), cohort);
	}
	
	public Cohort getTransferredOut(Cohort cohort) {
		return getByConcept(asList(TRANSFERRED_OUT_UUID), cohort);
	}
	
	private Cohort getByConcept(List<String> followUpStatus, Cohort cohort) {
		StringBuilder stringBuilder = baseQuery(FOLLOW_UP_STATUS);
		stringBuilder.append(" and ").append(OBS_ALIAS).append("value_coded =").append(conceptQuery(followUpStatus))
		        .append(" and ").append(OBS_ALIAS).append("person_id in (:personIds) ").append(" and ").append(OBS_ALIAS)
		        .append("encounter_id in (:encounterIds)");
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringBuilder.toString());
		
		query.setParameterList("personIds", cohort.getMemberIds());
		query.setParameterList("encounterIds", baseEncounter);
		
		return new Cohort(query.list());
	}
	
	@NotNull
	private Cohort getCohort(Date end, Cohort cohort, List<Integer> encounter) {
		StringBuilder sql = new StringBuilder("select person_id from obs");
		sql.append(" where concept_id =  ").append(conceptQuery(FOLLOW_UP_STATUS)).append(" AND value_coded in ")
		        .append(conceptQuery(asList(LOST_TO_FOLLOW_UP, DEAD, DROP, TRANSFERRED_OUT_UUID)))
		        .append(" and person_id in (:personIds) and encounter_id in (:betweenEncounters) ");
		sql.append("Union ");
		
		sql.append("select person_id from obs");
		sql.append(" where  concept_id =").append(conceptQuery(TREATMENT_END_DATE))
		        .append(" and value_datetime < :endDate ")
		        .append(" and person_id in (:personIds) and encounter_id in (:encounters) ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setDate("endDate", end);
		query.setParameterList("personIds", cohort.getMemberIds());
		query.setParameterList("encounters", baseEncounter);
		query.setParameterList("betweenEncounters", encounter);
		return new Cohort(query.list());
	}
	
}
