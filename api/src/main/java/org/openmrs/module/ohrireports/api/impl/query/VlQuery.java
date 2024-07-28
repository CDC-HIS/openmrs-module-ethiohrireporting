package org.openmrs.module.ohrireports.api.impl.query;

import java.util.*;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.constants.ConceptAnswer;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions.HIV_VIRAL_LOAD_COUNT;

@Component
public class VlQuery extends ObsElement {
	
	private final DbSessionFactory sessionFactory;
	
	public Cohort cohort;
	
	private List<Integer> VlTakenEncounters;
	
	public List<Integer> getVlTakenEncounters() {
		return VlTakenEncounters;
	}
	
	public Date start, end = null;
	
	@Autowired
	public VlQuery(DbSessionFactory _SessionFactory) {
		super(_SessionFactory);
		sessionFactory = _SessionFactory;
	}
	
	public void loadInitialCohort(Date _start, Date _end, List<Integer> vList) {
		if ((start == null || !start.equals(_start)) || (end == null || !end.equals(_end))) {
			start = _start;
			end = _end;
			VlTakenEncounters = getEncountersWithVLStatusOnly(vList);
			cohort = getViralLoadReceivedCohort();
		}
		
	}
	
	private List<Integer> getEncountersWithVLStatusOnly(List<Integer> vList) {
		List<Integer> encounterIdList = new ArrayList<>();
		 if(vList==null || vList.isEmpty())
		  return encounterIdList;
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(" select encounter_id from obs where concept_id =" + conceptQuery(FollowUpConceptQuestions.VIRAL_LOAD_STATUS) + " " + " and value_coded is not null and encounter_id in (:latestEncounterIds)");

		query.setParameterList("latestEncounterIds", vList);
	
			List list =  query.list();
		if (list != null) {
			encounterIdList = query.list();
		}
		return encounterIdList;
		
	}
	
	public Cohort getViralLoadReceivedCohort() {
		
		StringBuilder sql = baseQuery(FollowUpConceptQuestions.DATE_VIRAL_TEST_RESULT_RECEIVED);
		
		sql.append(" and ").append(OBS_ALIAS).append("encounter_id in  (:latestEncounterId) ");
		sql.append(" and ").append(OBS_ALIAS).append("value_datetime >= :start and ").append(OBS_ALIAS)
		        .append("value_datetime<= :end ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameter("start", start);
		query.setParameter("end", end);
		query.setParameterList("latestEncounterId", VlTakenEncounters);
		
		return new Cohort(query.list());
		
	}
	
	public Cohort getViralLoadSuppressed() {
		Set<Integer> allPatients = new HashSet<>();
		Query query = getByViralLoadStatus(Arrays.asList(ConceptAnswer.HIV_VIRAL_LOAD_SUPPRESSED, ConceptAnswer.HIV_VIRAL_LOAD_LOW_LEVEL_VIREMIA));
		allPatients.addAll(query.list());

		List<Integer> personIds = getSuppressedByVLCount(null,50);
		allPatients.addAll(personIds);
		return new Cohort(allPatients);

	}
	
	public Cohort getViralLoadUnSuppressed() {
		
		Query query = getByViralLoadStatus(Arrays.asList(ConceptAnswer.HIV_HIGH_VIRAL_LOAD,
		    ConceptAnswer.HIV_VIRAL_LOAD_UNSUPPRESSED));
		return new Cohort(query.list());
	}
	
	public Cohort getHighViralLoad() {
		Query query = getByViralLoadStatus(Arrays.asList(ConceptAnswer.HIV_HIGH_VIRAL_LOAD,
		    ConceptAnswer.HIV_VIRAL_LOAD_UNSUPPRESSED));
		return new Cohort(query.list());
	}
	
	private Query getByViralLoadStatus(List<String> statusConceptUUID) {
		StringBuilder sql = baseQuery(FollowUpConceptQuestions.VIRAL_LOAD_STATUS);
		sql.append(" and ").append(OBS_ALIAS).append("encounter_id in (:encounters) ");
		sql.append(" and ").append(OBS_ALIAS).append("value_coded in ").append(conceptQuery(statusConceptUUID));
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		query.setParameterList("encounters", VlTakenEncounters);
		return query;
	}
	
	public Cohort getRoutingViralLoad(Cohort _cohort) {
		return getByVLTestIndication(_cohort, ConceptAnswer.ROUTINE_VIRAL_LOAD);
	}
	
	public Cohort getTargetViralLoad(Cohort _cohort) {
		return getByVLTestIndication(_cohort, ConceptAnswer.TARGET_VIRAL_LOAD);
	}
	
	public List<Integer> getSuppressedByVLCount(Integer minVlCount,Integer maxVlCount) {
		StringBuilder sql = baseQuery(HIV_VIRAL_LOAD_COUNT);
		sql.append(" and ").append(OBS_ALIAS).append("encounter_id in (:baseEncounterId) ");
		if (!Objects.isNull(minVlCount))
			sql.append(" and ").append(OBS_ALIAS).append("value_numeric > ").append(minVlCount);
		if (!Objects.isNull(maxVlCount))
			sql.append(" and ").append(OBS_ALIAS).append("value_numeric <= ").append(maxVlCount);

		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());

		query.setParameterList("baseEncounterId", VlTakenEncounters);

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
		query.setParameterList("baseEncounterId", VlTakenEncounters);
		
		return new Cohort(query.list());
	}
	
	public Cohort getPregnantCohort(Cohort _cohort) {
		StringBuilder sql = basePersonIdQuery(FollowUpConceptQuestions.PREGNANCY_STATUS, ConceptAnswer.YES);
		sql.append(" and " + PERSON_BASE_ALIAS_OBS + "encounter_id in (:baseEncounterId) ");
		sql.append(" and " + PERSON_BASE_ALIAS_OBS + "person_id in (:cohorts) ");
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("cohorts", _cohort.getMemberIds());
		query.setParameterList("baseEncounterId", VlTakenEncounters);
		
		return new Cohort(query.list());
	}
	
	public Cohort getBreastFeedingCohort(Cohort _cohort) {
		StringBuilder sql = basePersonIdQuery(FollowUpConceptQuestions.CURRENTLY_BREAST_FEEDING_CHILD, ConceptAnswer.YES);
		sql.append(" and " + PERSON_BASE_ALIAS_OBS + "encounter_id in (:baseEncounterId) ");
		sql.append(" and " + PERSON_BASE_ALIAS_OBS + "person_id in (:cohorts) ");
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("cohorts", _cohort.getMemberIds());
		query.setParameterList("baseEncounterId", VlTakenEncounters);
		
		return new Cohort(query.list());
	}
	
	public HashMap<Integer, Object> getViralLoadPerformDate() {
		StringBuilder sql = baseValueDateQuery(FollowUpConceptQuestions.DATE_VIRAL_TEST_RESULT_RECEIVED);
		sql.append(" and " + VALUE_DATE_BASE_ALIAS_OBS + "encounter_id in (:encounters)");
		sql.append(" and " + VALUE_DATE_BASE_ALIAS_OBS + "person_id in (:cohorts)");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("encounters", VlTakenEncounters);
		query.setParameterList("cohorts", cohort.getMemberIds());
		
		return getDictionary(query);
		
	}
	
	public HashMap<Integer, Object> getRoutineViralLoad() {
		Query query = getObs(VlTakenEncounters, ConceptAnswer.ROUTINE_VIRAL_LOAD, cohort);
		
		return getDictionary(query);
	}
	
	public HashMap<Integer, Object> getPregnantStatus() {
		Query query = getObs(VlTakenEncounters, FollowUpConceptQuestions.PREGNANCY_STATUS, cohort);
		
		return getDictionary(query);
	}
	
	public HashMap<Integer, Object> getBreastFeedingStatus() {
		Query query = getObs(VlTakenEncounters, FollowUpConceptQuestions.CURRENTLY_BREAST_FEEDING_CHILD, cohort);
		
		return getDictionary(query);
	}
	
	public HashMap<Integer, Object> getTargetViralLoad() {
		Query query = getObs(VlTakenEncounters, ConceptAnswer.TARGET_VIRAL_LOAD, cohort);
		return getDictionary(query);
	}
	
	public HashMap<Integer, Object> getStatusViralLoad() {
		Query query = getObs(VlTakenEncounters, FollowUpConceptQuestions.VIRAL_LOAD_STATUS, cohort);
		return getDictionary(query);
	}
	
	public HashMap<Integer, Object> getArtDose() {
		Query query = getObs(VlTakenEncounters, FollowUpConceptQuestions.ARV_DISPENSED_IN_DAYS, cohort);
		return getDictionary(query);
	}
	
	public HashMap<Integer, Object> getViralLoadCount() {
		Query query = getObsNumber(VlTakenEncounters, HIV_VIRAL_LOAD_COUNT, cohort);
		return getDictionary(query);
	}
}
