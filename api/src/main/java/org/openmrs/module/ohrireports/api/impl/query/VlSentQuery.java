package org.openmrs.module.ohrireports.api.impl.query;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.constants.ConceptAnswer;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions.HIV_VIRAL_LOAD_COUNT;

@Component
public class VlSentQuery extends ObsElement {
	
	private final DbSessionFactory sessionFactory;
	
	public Cohort cohort;
	
	public Cohort suppressedCohort;
	
	public Date start, end = null;
	
	private List<Integer> VlSentEncounters;
	
	@Autowired
	public VlSentQuery(DbSessionFactory _SessionFactory) {
		super(_SessionFactory);
		sessionFactory = _SessionFactory;
	}
	
	public Cohort getSuppressedCohort() {
		return suppressedCohort;
	}
	
	public void setSuppressedCohort(Cohort supperessedCohort) {
		this.suppressedCohort = supperessedCohort;
	}
	
	public List<Integer> getVlSentEncounters() {
		return VlSentEncounters;
	}
	
	public void loadInitialCohort(Date _start, Date _end, List<Integer> vList) {
		start = _start;
		end = _end;
		VlSentEncounters = vList;
		cohort = getCohort(vList);
	}
	
	private List<Integer> getEncountersWithVLStatusOnly(List<Integer> vList) {
        List<Integer> encounterIdList = new ArrayList<>();
        if (vList == null || vList.isEmpty())
            return encounterIdList;

        Query query = sessionFactory.getCurrentSession().createSQLQuery(" select encounter_id from obs where concept_id =" + conceptQuery(FollowUpConceptQuestions.VIRAL_LOAD_STATUS) + " " + " and value_coded is not null and encounter_id in (:latestEncounterIds)");

        query.setParameterList("latestEncounterIds", vList);

        List list = query.list();
        if (list != null) {
            encounterIdList = query.list();
        }
        return encounterIdList;

    }
	
	private Cohort getCohort(List<Integer> encounterIds) {
		if (Objects.isNull(encounterIds) || encounterIds.isEmpty()) {
			return new Cohort();
		}
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(
		    "select distinct (person_id) from obs where encounter_id in (:encounterIds) ");
		query.setParameterList("encounterIds", encounterIds);
		
		return new Cohort(query.list());
		
	}
	
	public Cohort getViralLoadReceivedCohort() {
		
		StringBuilder sql = baseQuery(FollowUpConceptQuestions.DATE_VIRAL_TEST_RESULT_RECEIVED);
		
		sql.append(" and ").append(OBS_ALIAS).append("encounter_id in  (:latestEncounterId) ");
		sql.append(" and ").append(OBS_ALIAS).append("value_datetime >= :start and ").append(OBS_ALIAS)
		        .append("value_datetime<= :end ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameter("start", start);
		query.setParameter("end", end);
		query.setParameterList("latestEncounterId", VlSentEncounters);
		
		return new Cohort(query.list());
		
	}
	
	public Cohort getViralLoadSuppressed(List<String> concepts) {
        Set<Integer> allPatients = new HashSet<>();
        Query query = getByViralLoadStatus(concepts);
        allPatients.addAll(query.list());

//		List<Integer> personIds = getSuppressedByVLCount(null,50);
//		allPatients.addAll(personIds);
        return new Cohort(allPatients);

    }
	
	private Query getByViralLoadStatus(List<String> statusConceptUUID) {
		StringBuilder sql = baseQuery(FollowUpConceptQuestions.VIRAL_LOAD_STATUS);
		sql.append(" and ").append(OBS_ALIAS).append("encounter_id in (:encounters) ");
		sql.append(" and ").append(OBS_ALIAS).append("value_coded in ").append(conceptQuery(statusConceptUUID));
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		query.setParameterList("encounters", VlSentEncounters);
		return query;
	}
	
	public Cohort getRoutingViralLoad(Cohort _cohort) {
		return getByVLTestIndication(_cohort, ConceptAnswer.ROUTINE_VIRAL_LOAD);
	}
	
	public Cohort getTargetViralLoad(Cohort _cohort) {
		return getByVLTestIndication(_cohort, ConceptAnswer.TARGET_VIRAL_LOAD);
	}
	
	public List<Integer> getSuppressedByVLCount(Integer minVlCount, Integer maxVlCount) {
        StringBuilder sql = baseQuery(HIV_VIRAL_LOAD_COUNT);
        sql.append(" and ").append(OBS_ALIAS).append("encounter_id in (:baseEncounterId) ");
        if (!Objects.isNull(minVlCount))
            sql.append(" and ").append(OBS_ALIAS).append("value_numeric > ").append(minVlCount);
        if (!Objects.isNull(maxVlCount))
            sql.append(" and ").append(OBS_ALIAS).append("value_numeric <= ").append(maxVlCount);

        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());

        query.setParameterList("baseEncounterId", VlSentEncounters);

        List list = query.list();
        if (list == null)
            return new ArrayList<>();
        return (ArrayList<Integer>) list;
    }
	
	private Cohort getByVLTestIndication(Cohort _cohort, String answer) {
		StringBuilder sql = basePersonIdQuery(FollowUpConceptQuestions.REASON_VIRAL_LOAD_TEST, answer);
		sql.append(" and " + PERSON_BASE_ALIAS_OBS + "encounter_id in (:baseEncounterId) ");
		sql.append(" and " + PERSON_BASE_ALIAS_OBS + "person_id in (:cohorts) ");
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("cohorts", _cohort.getMemberIds());
		query.setParameterList("baseEncounterId", VlSentEncounters);
		
		return new Cohort(query.list());
	}
	
	public Cohort getPregnantCohort(Cohort _cohort) {
		StringBuilder sql = basePersonIdQuery(FollowUpConceptQuestions.PREGNANCY_STATUS, ConceptAnswer.YES);
		sql.append(" and " + PERSON_BASE_ALIAS_OBS + "encounter_id in (:baseEncounterId) ");
		sql.append(" and " + PERSON_BASE_ALIAS_OBS + "person_id in (:cohorts) ");
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("cohorts", _cohort.getMemberIds());
		query.setParameterList("baseEncounterId", VlSentEncounters);
		
		return new Cohort(query.list());
	}
	
	public Cohort getBreastFeedingCohort(Cohort _cohort) {
		StringBuilder sql = basePersonIdQuery(FollowUpConceptQuestions.CURRENTLY_BREAST_FEEDING_CHILD, ConceptAnswer.YES);
		sql.append(" and " + PERSON_BASE_ALIAS_OBS + "encounter_id in (:baseEncounterId) ");
		sql.append(" and " + PERSON_BASE_ALIAS_OBS + "person_id in (:cohorts) ");
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("cohorts", _cohort.getMemberIds());
		query.setParameterList("baseEncounterId", VlSentEncounters);
		
		return new Cohort(query.list());
	}
	
	public HashMap<Integer, Object> getViralLoadPerformDate() {
		StringBuilder sql = baseValueDateQuery(FollowUpConceptQuestions.DATE_VIRAL_TEST_RESULT_RECEIVED);
		sql.append(" and " + VALUE_DATE_BASE_ALIAS_OBS + "encounter_id in (:encounters)");
		sql.append(" and " + VALUE_DATE_BASE_ALIAS_OBS + "person_id in (:cohorts)");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("encounters", VlSentEncounters);
		query.setParameterList("cohorts", cohort.getMemberIds());
		
		return getDictionary(query);
		
	}
	
	public HashMap<Integer, Object> getRoutineViralLoad() {
		Query query = getObs(VlSentEncounters, ConceptAnswer.ROUTINE_VIRAL_LOAD, cohort);
		
		return getDictionary(query);
	}
	
	public HashMap<Integer, Object> getPregnantStatus() {
		Query query = getObs(VlSentEncounters, FollowUpConceptQuestions.PREGNANCY_STATUS, cohort);
		
		return getDictionary(query);
	}
	
	public HashMap<Integer, Object> getBreastFeedingStatus() {
		Query query = getObs(VlSentEncounters, FollowUpConceptQuestions.CURRENTLY_BREAST_FEEDING_CHILD, cohort);
		
		return getDictionary(query);
	}
	
	public HashMap<Integer, Object> getTargetViralLoad() {
		Query query = getObs(VlSentEncounters, ConceptAnswer.TARGET_VIRAL_LOAD, cohort);
		return getDictionary(query);
	}
	
	public HashMap<Integer, Object> getStatusViralLoad() {
		Query query = getObs(VlSentEncounters, FollowUpConceptQuestions.VIRAL_LOAD_STATUS, cohort);
		return getDictionary(query);
	}
	
	public HashMap<Integer, Object> getArtDose() {
		Query query = getObs(VlSentEncounters, FollowUpConceptQuestions.ARV_DISPENSED_IN_DAYS, cohort);
		return getDictionary(query);
	}
	
	public HashMap<Integer, Object> getViralLoadCount() {
		Query query = getObsNumber(VlSentEncounters, HIV_VIRAL_LOAD_COUNT, cohort);
		return getDictionary(query);
	}
}
