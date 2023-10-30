package org.openmrs.module.ohrireports.api.impl.query;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.DATE_VIRAL_TEST_RESULT_RECEIVED;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HIV_HIGH_VIRAL_LOAD;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HIV_ROUTINE_VIRAL_LOAD_COUNT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HIV_TARGET_VIRAL_LOAD_COUNT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HIV_VIRAL_LOAD_LOW_LEVEL_VIREMIA;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HIV_VIRAL_LOAD_COUNT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HIV_VIRAL_LOAD_STATUS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HIV_VIRAL_LOAD_SUPPRESSED;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HIV_VIRAL_LOAD_UNSUPPRESSED;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.CURRENTLY_BREAST_FEEDING_CHILD;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.PREGNANT_STATUS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.DISPENSED_DOSE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.VIRAL_LOAD_TEST_INDICATION;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.YES;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VlQuery extends BaseLineListQuery {
	
	private DbSessionFactory sessionFactory;
	
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

		StringBuilder sql = new StringBuilder(" select encounter_id from obs where concept_id ="+conceptQuery(HIV_VIRAL_LOAD_STATUS)+" ");
		sql.append(" and value_coded is not null and encounter_id in (:latestEncounterIds)");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());

		query.setParameterList("latestEncounterIds", vList);
	
			List list =  query.list();
			if(list == null){
				return encounterIdList;
			}else{
				encounterIdList = query.list();
				return encounterIdList;
			}

	}
	
	public Cohort getViralLoadReceivedCohort() {
		
		StringBuilder sql = baseQuery(DATE_VIRAL_TEST_RESULT_RECEIVED);
		
		sql.append(" and " + OBS_ALIAS + "encounter_id in  (:latestEncounterId) ");
		sql.append(" and " + OBS_ALIAS + "value_datetime >= :start and " + OBS_ALIAS + "value_datetime<= :end ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameter("start", start);
		query.setParameter("end", end);
		query.setParameterList("latestEncounterId", VlTakenEncounters);
		
		return new Cohort(query.list());
		
	}
	
	public Cohort getViralLoadSuppressed() {
		Set<Integer> allPatients = new HashSet<>();
		Query query = getByViralLoadStatus(Arrays.asList(HIV_VIRAL_LOAD_SUPPRESSED, HIV_VIRAL_LOAD_LOW_LEVEL_VIREMIA));
		allPatients.addAll(query.list());

		List<Integer> personIds = getSuppressedByVLCount();
		allPatients.addAll(personIds);
		return new Cohort(allPatients);

	}
	
	public Cohort getViralLoadUnSuppressed() {
		
		Query query = getByViralLoadStatus(Arrays.asList(HIV_HIGH_VIRAL_LOAD, HIV_VIRAL_LOAD_UNSUPPRESSED));
		return new Cohort(query.list());
	}
	
	public Cohort getHighViralLoad() {
		
		Query query = getByViralLoadStatus(Arrays.asList(HIV_HIGH_VIRAL_LOAD, HIV_VIRAL_LOAD_UNSUPPRESSED));
		return new Cohort(query.list());
	}
	
	private Query getByViralLoadStatus(List<String> statusConceptUUID) {
		StringBuilder sql = baseQuery(HIV_VIRAL_LOAD_STATUS);
		sql.append(" and " + OBS_ALIAS + "encounter_id in (:encounters) ");
		sql.append(" and " + OBS_ALIAS + "value_coded in " + conceptQuery(statusConceptUUID));
		sql.append(" and " + OBS_ALIAS + "person_id in (:cohorts)");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		query.setParameterList("cohorts", cohort.getMemberIds());
		query.setParameterList("encounters", VlTakenEncounters);
		return query;
	}
	
	public Cohort getRoutingViralLoad(Cohort _cohort) {
		return getByVLTestIndication(_cohort, HIV_ROUTINE_VIRAL_LOAD_COUNT);
	}
	
	public Cohort getTargetViralLoad(Cohort _cohort) {
		return getByVLTestIndication(_cohort, HIV_TARGET_VIRAL_LOAD_COUNT);
	}
	
	private List<Integer> getSuppressedByVLCount() {
		StringBuilder sql = baseQuery(HIV_VIRAL_LOAD_COUNT);
		sql.append(" and " + OBS_ALIAS + "encounter_id in (:baseEncounterId) ");
		sql.append(" and " + OBS_ALIAS + "value_numeric <= 1000");

		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());

		query.setParameterList("baseEncounterId", VlTakenEncounters);

		List list = query.list();
		if (list == null)
			return new ArrayList<>();

		return (ArrayList<Integer>) list;
	}
	
	private Cohort getByVLTestIndication(Cohort _cohort, String answer) {
		StringBuilder sql = basePersonIdQuery(VIRAL_LOAD_TEST_INDICATION, answer);
		sql.append(" and " + PERSON_BASE_ALIAS_OBS + "encounter_id in (:baseEncounterId) ");
		sql.append(" and " + PERSON_BASE_ALIAS_OBS + "person_id in (:cohorts) ");
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("cohorts", _cohort.getMemberIds());
		query.setParameterList("baseEncounterId", VlTakenEncounters);
		
		return new Cohort(query.list());
	}
	
	public Cohort getPregnantCohort(Cohort _cohort) {
		StringBuilder sql = basePersonIdQuery(PREGNANT_STATUS, YES);
		sql.append(" and " + PERSON_BASE_ALIAS_OBS + "encounter_id in (:baseEncounterId) ");
		sql.append(" and " + PERSON_BASE_ALIAS_OBS + "person_id in (:cohorts) ");
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("cohorts", _cohort.getMemberIds());
		query.setParameterList("baseEncounterId", VlTakenEncounters);
		
		return new Cohort(query.list());
	}
	
	public Cohort getBreastFeedingCohort(Cohort _cohort) {
		StringBuilder sql = basePersonIdQuery(CURRENTLY_BREAST_FEEDING_CHILD, YES);
		sql.append(" and " + PERSON_BASE_ALIAS_OBS + "encounter_id in (:baseEncounterId) ");
		sql.append(" and " + PERSON_BASE_ALIAS_OBS + "person_id in (:cohorts) ");
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("cohorts", _cohort.getMemberIds());
		query.setParameterList("baseEncounterId", VlTakenEncounters);
		
		return new Cohort(query.list());
	}
	
	public HashMap<Integer, Object> getViralLoadPerformDate() {
		StringBuilder sql = baseValueDateQuery(DATE_VIRAL_TEST_RESULT_RECEIVED);
		sql.append(" and " + VALUE_DATE_BASE_ALIAS_OBS + "encounter_id in (:encounters)");
		sql.append(" and " + VALUE_DATE_BASE_ALIAS_OBS + "person_id in (:cohorts)");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("encounters", VlTakenEncounters);
		query.setParameterList("cohorts", cohort.getMemberIds());
		
		return getDictionary(query);
		
	}
	
	public HashMap<Integer, Object> getRoutineViralLoad() {
		Query query = getObs(VlTakenEncounters, HIV_ROUTINE_VIRAL_LOAD_COUNT, cohort);
		
		return getDictionary(query);
	}
	
	public HashMap<Integer, Object> getPregnantStatus() {
		Query query = getObs(VlTakenEncounters, PREGNANT_STATUS, cohort);
		
		return getDictionary(query);
	}
	
	public HashMap<Integer, Object> getBreastFeedingStatus() {
		Query query = getObs(VlTakenEncounters, CURRENTLY_BREAST_FEEDING_CHILD, cohort);
		
		return getDictionary(query);
	}
	
	public HashMap<Integer, Object> getTargetViralLoad() {
		Query query = getObs(VlTakenEncounters, HIV_TARGET_VIRAL_LOAD_COUNT, cohort);
		return getDictionary(query);
	}
	
	public HashMap<Integer, Object> getStatusViralLoad() {
		Query query = getObs(VlTakenEncounters, HIV_VIRAL_LOAD_STATUS, cohort);
		return getDictionary(query);
	}
	
	public HashMap<Integer, Object> getArtDose() {
		Query query = getObs(VlTakenEncounters, DISPENSED_DOSE, cohort);
		return getDictionary(query);
	}
	
	public HashMap<Integer, Object> getViralLoadCount() {
		Query query = getObsNumber(VlTakenEncounters, HIV_VIRAL_LOAD_COUNT, cohort);
		return getDictionary(query);
	}
}
