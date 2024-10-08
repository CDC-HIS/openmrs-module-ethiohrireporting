package org.openmrs.module.ohrireports.api.impl.query;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.CohortMembership;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.constants.ConceptAnswer;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISUtilies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

import static java.util.Arrays.asList;

/**
 * Cohort of patient for those discontinued ART in previous reporting period and restarted int the
 * current reporting period
 */
@Component
public class RTTQuery extends PatientQueryImpDao {
	
	private DbSessionFactory sessionFactory;
	public Cohort getBaseCohort() {
		return baseCohort;
	}
	public HashMap<Integer, BigDecimal> restartedMonthList = new HashMap<>();

	private Cohort baseCohort;
	@Autowired
	private EncounterQuery encounterQuery;
	private List<Integer> baseEncounter;
	private List<Integer> interruptedEncounter;
	/**
	 * @return latest followup for each patient before reporting end
	 */
	public List<Integer> getBaseEncounter() {
		return baseEncounter;
	}

	public  List<Integer> getInterruptedEncounter() {
		return interruptedEncounter;
	}
	@Autowired
	public RTTQuery(DbSessionFactory _SessionFactory) {
		sessionFactory = _SessionFactory;
		setSessionFactory(sessionFactory);
	}
	/**
	 * @param start
	 * @param end
	 * @return
	 */
	public Cohort getRttCohort(Date start, Date end) {
		/* fetching the whole encounter when follow-up date  is before reporting start date */
		List<Integer> encounter = encounterQuery.getLatestDateByFollowUpDate(null, start);
		// fetching encounter when follow-up date is  before reporting start and follow-up status is ALIVE AND RESTART
		List<Integer> encounterAlive = encounterQuery.getAliveFollowUpEncounters(null, start);
		
		// cohort is with the treatment end date is before reporting start date from encounters alive and restart
		Cohort doseEndCohort = getCohortByARTDoseEndDate(encounterAlive, start);
		// cohort is with the
		Cohort followUpCohort = getCohortByFollowUp(encounter);
		Cohort interruptedCohort = Cohort.union(doseEndCohort, followUpCohort);

		interruptedEncounter = getEncounterIds(encounter,interruptedCohort);

		baseEncounter = encounterQuery.getAliveFollowUpEncounters(interruptedCohort, start, end);
		return baseCohort = getActiveOnArtCohort("", null, end, interruptedCohort, baseEncounter);
	}
	private Cohort getCohortByARTDoseEndDate(List<Integer> encounter, Date startDate) {
		StringBuilder sqlBuilder = new StringBuilder("select ob.person_id from obs as ob where ob.concept_id =");
		sqlBuilder.append(conceptQuery(FollowUpConceptQuestions.TREATMENT_END_DATE));
		sqlBuilder.append(" and ").append(OBS_ALIAS).append("value_datetime < :start ");
		sqlBuilder.append(" and ").append(OBS_ALIAS).append("encounter_id in (:encounter) ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
		
		query.setDate("start", startDate);
		query.setParameterList("encounter", encounter);
		
		return new Cohort(query.list());
	}

	private Cohort getCohortByFollowUp(List<Integer> encounter) {
		StringBuilder sqlBuilder = baseQuery(FollowUpConceptQuestions.FOLLOW_UP_STATUS);
		sqlBuilder.append(" and ").append(OBS_ALIAS).append("value_coded in")
		        .append(conceptQuery(Arrays.asList(ConceptAnswer.LOST_TO_FOLLOW_UP, ConceptAnswer.DROP, ConceptAnswer.STOP)));
		sqlBuilder.append(" and ").append(OBS_ALIAS).append("encounter_id in (:encounter) ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
		
		query.setParameterList("encounter", encounter);
		
		return new Cohort(query.list());
	}
	public HashMap<Integer, BigDecimal> getInterruptionMonth(Date endDate) {
		restartedMonthList.putAll(getInterruptionMonthByFollowUpStatus());
		restartedMonthList.putAll(getInterruptionMonthByTreatmentEnd(endDate));
		return restartedMonthList;
	}
	private HashMap<Integer, BigDecimal> getInterruptionMonthByTreatmentEnd(Date end) {
		StringBuilder sqlBuilder = new StringBuilder(
				" SELECT ob.person_id, FLOOR (TIMESTAMPDIFF(MONTH, innterrup.value_datetime, treatmentEnd.value_datetime)");
		sqlBuilder
				.append(" + DATEDIFF(   treatmentEnd.value_datetime,    innterrup.value_datetime + INTERVAL TIMESTAMPDIFF(MONTH, innterrup.value_datetime,");
		sqlBuilder
				.append(" treatmentEnd.value_datetime) MONTH ) / DATEDIFF(   innterrup.value_datetime + INTERVAL TIMESTAMPDIFF(MONTH, innterrup.value_datetime, ");
		sqlBuilder.append(" treatmentEnd.value_datetime) + 1 MONTH,");
		sqlBuilder
				.append("    innterrup.value_datetime + INTERVAL TIMESTAMPDIFF(MONTH, innterrup.value_datetime, treatmentEnd.value_datetime) MONTH 	)) as interruptionMonth");
		sqlBuilder.append(" from  obs as ob ");
		sqlBuilder.append(" inner join ");
		sqlBuilder.append("(   SELECT  ob_followup.value_datetime , ob_followup.person_id  from  obs as ob_followup");
		sqlBuilder.append(" where  ob_followup.concept_id =").append(conceptQuery(FollowUpConceptQuestions.TREATMENT_END_DATE));
		sqlBuilder
				.append(" and ob_followup.encounter_id in (:restartedEncounter) ) as treatmentEnd on treatmentEnd.person_id =ob.person_id");
		sqlBuilder.append(" inner join ");
		sqlBuilder.append(" ( SELECT ob_followup.value_datetime , ob_followup.person_id  from  obs as ob_followup");
		sqlBuilder.append(" where ob_followup.concept_id =").append(conceptQuery(FollowUpConceptQuestions.TREATMENT_END_DATE));
		sqlBuilder.append(" and ob_followup.encounter_id in (:interruptedEncounter) ) as innterrup on innterrup.person_id =ob.person_id");

		sqlBuilder.append(" where ob.concept_id =  ").append(conceptQuery(FollowUpConceptQuestions.TREATMENT_END_DATE))
				.append(" AND ob.value_datetime > :endDate ")
				.append(" and ob.person_id in (:personIds) and ob.encounter_id in (:betweenEncounters) ");

		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());

		query.setDate("endDate", end);
		query.setParameterList("personIds", baseCohort.getMemberIds());
		query.setParameterList("interruptedEncounter", interruptedEncounter);
		query.setParameterList("restartedEncounter", baseEncounter);
		query.setParameterList("betweenEncounters", baseEncounter);

		return HMISUtilies.getDictionaryWithBigDecimal (query);
	}

	private HashMap<Integer, BigDecimal> getInterruptionMonthByFollowUpStatus() {
		StringBuilder sqlBuilder = new StringBuilder(
				" SELECT ob.person_id, FLOOR (TIMESTAMPDIFF(MONTH, innterrup.value_datetime, treatmentEnd.value_datetime)");
		sqlBuilder
				.append(" + DATEDIFF(   treatmentEnd.value_datetime,    innterrup.value_datetime + INTERVAL TIMESTAMPDIFF(MONTH, innterrup.value_datetime,");
		sqlBuilder
				.append(" treatmentEnd.value_datetime) MONTH ) / DATEDIFF(   innterrup.value_datetime + INTERVAL TIMESTAMPDIFF(MONTH, innterrup.value_datetime, ");
		sqlBuilder.append(" treatmentEnd.value_datetime) + 1 MONTH,");
		sqlBuilder
				.append("    innterrup.value_datetime + INTERVAL TIMESTAMPDIFF(MONTH, innterrup.value_datetime, treatmentEnd.value_datetime) MONTH 	)) as interruptionMonth");
		sqlBuilder.append(" from  obs as ob ");
		sqlBuilder.append(" inner join ");
		sqlBuilder.append("(    SELECT       ob_followup.value_datetime , ob_followup.person_id  from  obs as ob_followup");
		sqlBuilder.append(" where ob_followup.concept_id =").append(conceptQuery(FollowUpConceptQuestions.TREATMENT_END_DATE));
		sqlBuilder
				.append(" and ob_followup.encounter_id in (:restartedEncounter) ) as treatmentEnd on treatmentEnd.person_id =ob.person_id");
		sqlBuilder.append(" inner join ");
		sqlBuilder.append(" ( SELECT ob_followup.value_datetime , ob_followup.person_id  from  obs as ob_followup");
		sqlBuilder.append(" where    ob_followup.concept_id =").append(conceptQuery(FollowUpConceptQuestions.FOLLOW_UP_DATE));
		sqlBuilder
				.append(" and ob_followup.encounter_id in (:interruptedEncounter) ) as innterrup on innterrup.person_id =ob.person_id");

		sqlBuilder.append(" where ob.concept_id =  ").append(conceptQuery(FollowUpConceptQuestions.FOLLOW_UP_STATUS))
				.append(" AND ob.value_coded in ").append(conceptQuery(asList(ConceptAnswer.LOST_TO_FOLLOW_UP, ConceptAnswer.DROP)))
				.append(" and ob.person_id in (:personIds) and ob.encounter_id in (:betweenEncounters) ");

		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());

		query.setParameterList("personIds", baseCohort.getMemberIds());
		query.setParameterList("interruptedEncounter", interruptedEncounter);
		query.setParameterList("restartedEncounter", baseEncounter);
		query.setParameterList("betweenEncounters", baseEncounter);

		return HMISUtilies.getDictionaryWithBigDecimal (query);
	}

	public Cohort getInterruptionMonth(int minMonth, int maxMonth){
		Cohort cohort = new Cohort();
		for (Map.Entry<Integer, BigDecimal> entry : restartedMonthList.entrySet()) {
			Integer k = entry.getKey();
			BigDecimal o = entry.getValue();
			if (o.intValue() >= minMonth && o.intValue() < maxMonth) {
				cohort.addMembership(new CohortMembership(k));
			}
		}
		return cohort;
	}
}
