package org.openmrs.module.ohrireports.api.impl.query.cohort;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.ObsElement;
import org.openmrs.module.ohrireports.constants.ConceptAnswer;
import org.openmrs.module.ohrireports.constants.EncounterType;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.openmrs.module.ohrireports.constants.PMTCTConceptQuestions;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PMTCTCohort extends ObsElement {
	
	private final DbSessionFactory sessionFactory;
	
	HashMap<Integer, Object> transferred = new HashMap<Integer, Object>();
	
	HashMap<Integer, Object> transferredInDatePatientId = new HashMap<Integer, Object>();
	
	HashMap<Integer, Object> transferredOutDatePatientId = new HashMap<Integer, Object>();
	
	List<Integer> firstEncounters = new ArrayList<Integer>();
	
	private Cohort pmtctEnrolledCohort;
	
	private HashMap<PMTCTCalculationType, Double> countByTypes;
	
	private HashMap<Integer, Object> pmtctEnrolledDate = new HashMap<Integer, Object>();
	
	private Date start, end;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	@Autowired
	public PMTCTCohort(DbSessionFactory _SessionFactory) {
		super(_SessionFactory);
		sessionFactory = _SessionFactory;
	}
	
	public Double getCount(PMTCTCalculationType pmtctCalculationType) {
		Double result = countByTypes.get(pmtctCalculationType);
		return Objects.isNull(result) ? 0.0 : result;
	}
	
	public Cohort getEnrolledCohort() {
		return pmtctEnrolledCohort;
	}
	
	public void generateBaseReport(Date startDate, Date endDate) {
		
		countByTypes = new HashMap<>();
		
		start = startDate;
		end = endDate;
		
		enrolledToPMTCT(startDate, endDate);
		
		if(!pmtctEnrolledCohort.isEmpty()){
			reportForInFacilityEnrolled();
		}
	}
	
	private void enrolledToPMTCT(Date startDate, Date endDate) {
		StringBuilder baseQuery = baseQuery(PMTCTConceptQuestions.PMTCT_OTZ_ENROLLMENT_DATE,
		    EncounterType.PMTC_ENROLLMENT_ENCOUNTER_TYPE);
		baseQuery.append(OBS_ALIAS).append("value_datetime>= :start").append(" and ").append(OBS_ALIAS)
		        .append("value_datetime<= :end");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(baseQuery.toString());
		query.setDate("start", startDate);
		query.setDate("end", endDate);
		
		//Loading list of encounter id with  PMTCT registration date with given date
		List<Integer> pmtctEnrolledEncounter = encounterQuery.getEncounters(
		    Collections.singletonList(PMTCTConceptQuestions.PMTCT_OTZ_ENROLLMENT_DATE), startDate, endDate,
		    EncounterType.PMTC_ENROLLMENT_ENCOUNTER_TYPE);
		
		//fetches cohort base on encounter id
		pmtctEnrolledCohort = getCohort(pmtctEnrolledEncounter);
		
		if (!pmtctEnrolledCohort.isEmpty()) {
			pmtctEnrolledDate = getObsValueDate(pmtctEnrolledEncounter, PMTCTConceptQuestions.PMTCT_OTZ_ENROLLMENT_DATE,
			    pmtctEnrolledCohort);
		}
		
	}
	
	public void generateMonthlyRangeReport(Date startDate, Date endDate) {
		if (!pmtctEnrolledCohort.isEmpty()) {
			generateTotalTI(startDate, endDate);
			generateTotalTO(startDate, endDate);
			calculateCohort();
			generateAliveAndOnART(startDate, endDate);
			generateLost(startDate, endDate);
			generateKnownDead(startDate, endDate);
			calculatePercentOfMaternalRetention();
		}
		
	}
	
	private void reportForInFacilityEnrolled() {
		
		List<Integer> firstEncounters = encounterQuery.getFirstEncounterByFollowUpDate(null, null);
		
		List<Integer> enrolledPatients = new ArrayList<>(pmtctEnrolledCohort.getMemberIds());
		transferred = getByResultByUUID(FollowUpConceptQuestions.REASON_FOR_ART_ELIGIBILITY, ConceptAnswer.TRANSFERRED_IN, pmtctEnrolledCohort, firstEncounters);
		transferredInDatePatientId = getObsValueDate(firstEncounters, FollowUpConceptQuestions.FOLLOW_UP_DATE, pmtctEnrolledCohort);
		
		for (Map.Entry<Integer, Object> entry : transferred.entrySet()) {
			
			Object _transferredInDate = transferredInDatePatientId.get(entry.getKey());
			Object _enrolledDateDate = pmtctEnrolledDate.get(entry.getKey());
			
			if (ObjectUtil.notNull(_transferredInDate) && ObjectUtil.notNull(_enrolledDateDate)) {
				if (_transferredInDate instanceof Date && _enrolledDateDate instanceof Date) {
					Date td = (Date) _transferredInDate;
					Date _ed = (Date) _enrolledDateDate;
					if (td.after(_ed)) {
						//If the patient enrolled to PMTCT before coming to the facility then remove
						enrolledPatients.remove(entry.getKey());
					}
				}
			}
		}
		
		Cohort enrolledInFacilities = getCurrentOnTreatmentCohort(end, new Cohort(enrolledPatients), firstEncounters);
		countByTypes.put(PMTCTCalculationType.IN_FACILITY_ENROLLED, (double) enrolledInFacilities.size());
		
	}
	
	private Cohort getCohort(List<Integer> encounterIds) {
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(
		    "select distinct (person_id) from obs where encounter_id in (:encounterIds) ");
		query.setParameterList("encounterIds", encounterIds);
		
		return new Cohort(query.list());
		
	}
	
	private Cohort getCurrentOnTreatmentCohort(Date endOnOrBefore, Cohort cohort, List<Integer> encounters) {
		
		if (encounters == null || encounters.isEmpty())
			return new Cohort();
		
		StringBuilder sql = baseQuery(FollowUpConceptQuestions.TREATMENT_END_DATE);
		
		sql.append("and ").append(OBS_ALIAS).append("encounter_id in (:encounterIds) ");
		
		if (endOnOrBefore != null)
			sql.append(" and ").append(OBS_ALIAS).append("value_datetime >= :endOnOrBefore ");
		if (cohort != null && !cohort.isEmpty())
			sql.append("and p.person_id in (:personIds) ");
		
		Query q = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		q.setParameter("encounterIds", encounters);
		
		if (endOnOrBefore != null)
			q.setTimestamp("endOnOrBefore", endOnOrBefore);
		if (cohort != null && !cohort.isEmpty())
			q.setParameter("personIds", cohort.getMemberIds());
		
		return new Cohort(q.list());
	}
	
	private void generateTotalTI(Date startDate, Date endDate) {
		List<Integer> enrolledAtOtherFacility = new ArrayList<Integer>();
		List<Integer> firstEncounters = encounterQuery.getFirstEncounterByFollowUpDate(startDate, endDate);
		for (Map.Entry<Integer, Object> entry : transferred.entrySet()) {
			
			Object _transferredInDate = transferredInDatePatientId.get(entry.getKey());
			Object _enrolledDateDate = pmtctEnrolledDate.get(entry.getKey());
			
			if (ObjectUtil.notNull(_transferredInDate) && ObjectUtil.notNull(_enrolledDateDate)) {
				if (_transferredInDate instanceof Date && _enrolledDateDate instanceof Date) {
					Date td = (Date) _transferredInDate;
					Date _ed = (Date) _enrolledDateDate;
					if (td.before(_ed)) {
						//If the patient enrolled to PMTCT before coming to the facility then count
						enrolledAtOtherFacility.add(entry.getKey());
					}
				}
			}
		}
		Cohort enrolledInFacilities = getCurrentOnTreatmentCohort(end, new Cohort(enrolledAtOtherFacility), firstEncounters);
		countByTypes.put(PMTCTCalculationType.ALL_TI, (double) enrolledInFacilities.size());
	}
	
	private void generateTotalTO(Date startDate, Date endDate) {
		List<Integer> enrolledTOFacility = new ArrayList<Integer>();
		HashMap<Integer, Object> transferredOut = getByResultByUUID(FollowUpConceptQuestions.FOLLOW_UP_STATUS,
		    ConceptAnswer.TRANSFERRED_OUT_UUID, pmtctEnrolledCohort, firstEncounters);
		transferredOutDatePatientId = getObsValueDate(firstEncounters, FollowUpConceptQuestions.FOLLOW_UP_DATE,
		    pmtctEnrolledCohort);
		for (Map.Entry<Integer, Object> entry : transferredOut.entrySet()) {
			
			Object _transferredOutDate = transferredOutDatePatientId.get(entry.getKey());
			
			if (ObjectUtil.notNull(_transferredOutDate)) {
				if (_transferredOutDate instanceof Date) {
					Date td = (Date) _transferredOutDate;
					if (td.after(startDate) && td.before(endDate)) {
						//If the patient enrolled to PMTCT before coming to the facility then count
						enrolledTOFacility.add(entry.getKey());
					}
				}
			}
		}
		Cohort enrolledInFacilities = getCurrentOnTreatmentCohort(end, new Cohort(enrolledTOFacility), firstEncounters);
		countByTypes.put(PMTCTCalculationType.ALL_TO, (double) enrolledInFacilities.size());
	}
	
	private void generateAliveAndOnART(Date startDate, Date endDate) {
		Cohort enrolledAliveAndOnARTFacilities = getCurrentOnTreatmentCohort(end, pmtctEnrolledCohort, firstEncounters);
		
		countByTypes.put(PMTCTCalculationType.MOTHER_ALIVE_AND_ON_ART, (double) enrolledAliveAndOnARTFacilities.size());
	}
	
	private void generateLost(Date startDate, Date endDate) {
		List<Integer> encounters = encounterQuery.getEncounters(
		    Collections.singletonList(FollowUpConceptQuestions.FOLLOW_UP_DATE), startDate, endDate);
		HashMap<Integer, Object> lostStatusHashMap = getByResultByUUID(FollowUpConceptQuestions.FOLLOW_UP_STATUS,
		    ConceptAnswer.LOST_TO_FOLLOW_UP, pmtctEnrolledCohort, encounters);
		
		countByTypes.put(PMTCTCalculationType.LOST_TO_FOLLOW_UP, (double) lostStatusHashMap.size());
	}
	
	private void generateKnownDead(Date startDate, Date endDate) {
		List<Integer> encounters = encounterQuery.getEncounters(
		    Collections.singletonList(FollowUpConceptQuestions.FOLLOW_UP_DATE), startDate, endDate);
		HashMap<Integer, Object> lostStatusHashMap = getByResultByUUID(FollowUpConceptQuestions.FOLLOW_UP_STATUS,
		    ConceptAnswer.DEAD, pmtctEnrolledCohort, encounters);
		
		countByTypes.put(PMTCTCalculationType.LOST_TO_FOLLOW_UP, (double) lostStatusHashMap.size());
	}
	
	private void calculateCohort() {
		countByTypes.put(PMTCTCalculationType.NET_CURRENT_COHORT,
		    countByTypes.get(PMTCTCalculationType.ALL_TI) + countByTypes.get(PMTCTCalculationType.IN_FACILITY_ENROLLED)
		            - countByTypes.get(PMTCTCalculationType.ALL_TO));
	}
	
	private void calculatePercentOfMaternalRetention() {
		double percent = countByTypes.get(PMTCTCalculationType.MOTHER_ALIVE_AND_ON_ART)
		        / countByTypes.get(PMTCTCalculationType.NET_CURRENT_COHORT) * 100;
		countByTypes.put(PMTCTCalculationType.PERCENTAGE_NET_CURRENT_COHORT_ALIVE_AND_ON_ART, percent);
		
		percent = countByTypes.get(PMTCTCalculationType.LOST_TO_FOLLOW_UP)
		        / countByTypes.get(PMTCTCalculationType.NET_CURRENT_COHORT) * 100;
		countByTypes.put(PMTCTCalculationType.PERCENTAGE_NET_CURRENT_COHORT_LOST_TO_FOLLOW_UP, percent);
	}
}
