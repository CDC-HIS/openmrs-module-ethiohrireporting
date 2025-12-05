package org.openmrs.module.ohrireports.api.impl.query.pmtct;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.dao.PMTCTPatient;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.constants.ConceptAnswer;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.openmrs.module.ohrireports.constants.PMTCTConceptQuestions;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISUtilies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions.LMP_DATE;

@Component
public class ARTQuery extends PatientQueryImpDao {
	
	private DbSessionFactory sessionFactory;
	
	private Cohort baseCohort;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	public Cohort getBaseCohort() {
		return baseCohort;
	}
	
	List<Integer> latestFollowUpEncounter;
	
	public List<Integer> getLatestFollowUpEncounter() {
		return latestFollowUpEncounter;
	}
	
	public Cohort pmtctARTCohort;
	
	public Cohort newOnARTPMTCTARTCohort;
	
	public Cohort getNewOnARTPMTCTARTCohort() {
		return newOnARTPMTCTARTCohort;
	}
	
	public void setNewOnARTPMTCTARTCohort(Cohort newOnARTPMTCTARTCohort) {
		this.newOnARTPMTCTARTCohort = newOnARTPMTCTARTCohort;
	}
	
	public Cohort alreadyOnARTPMTCTARTCohort;
	
	public Cohort getAlreadyOnARTPMTCTARTCohort() {
		return alreadyOnARTPMTCTARTCohort;
	}
	
	public Cohort getPmtctARTCohort() {
		return pmtctARTCohort;
	}
	
	public List<Integer> getBaseEncounter() {
		return baseEncounter;
	}
	
	private List<Integer> baseEncounter;
	
	private Date startDate;
	
	private Date endDate;
	
	private HashMap<Integer, PMTCTPatient> patientEncounterHashMap;
	
	public ARTQuery(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		super.setSessionFactory(sessionFactory);
	}
	
	public void setSessionFactory(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
		latestFollowUpEncounter = encounterQuery.getAliveFollowUpEncounters(startDate, endDate);
		/*	baseEncounter = encounterQuery.getEncounters(
			    Collections.singletonList(PMTCTConceptQuestions.PMTCT_OTZ_ENROLLMENT_DATE), startDate, endDate,
			    EncounterType.PMTC_ENROLLMENT_ENCOUNTER_TYPE);*/
		baseCohort = getCohort(latestFollowUpEncounter);
		
		baseCohort = getByPregnantStatus();
		baseCohort = deduplicatedBaseOnLMP();
		//		pmtctARTCohort = getPMTCTARTCohort();
		newOnARTPMTCTARTCohort = getNewOnArtCohort("F", startDate, endDate, baseCohort, latestFollowUpEncounter);
		alreadyOnARTPMTCTARTCohort = HMISUtilies.getOuterUnion(baseCohort, newOnARTPMTCTARTCohort);
	}
	
	private Cohort deduplicatedBaseOnLMP() {
		if (latestFollowUpEncounter == null || latestFollowUpEncounter.isEmpty()) {
			return new Cohort();
		}
		StringBuilder stringBuilder = baseValueDateQuery(LMP_DATE);
		stringBuilder.append(" AND ");
		stringBuilder.append(VALUE_DATE_BASE_ALIAS_OBS).append("encounter_id in (:encounter_ids)");
		stringBuilder.append(" AND ");
		stringBuilder.append(VALUE_DATE_BASE_ALIAS_OBS).append("person_id in (:person_ids)");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringBuilder.toString());
		query.setParameterList("encounter_ids", latestFollowUpEncounter);
		query.setParameterList("person_ids", baseCohort.getMemberIds());
		
		HashMap<Integer, Object> hashMap = HMISUtilies.getDictionary(query);
		Set<Integer> patientIds = baseCohort.getMemberIds();
		for (Map.Entry<Integer, Object> entry : hashMap.entrySet()) {
			if ((entry.getValue() instanceof Date)) {
				Date date = (Date) entry.getValue();
				if (!date.before(startDate) && !date.after(endDate)) {
					continue;
				}
				
				patientIds.remove(entry.getKey());
				
			}
		}
		
		return new Cohort(patientIds);
		
	}
	
	public Cohort getByPregnantStatus() {
		/*if (isCohortAndEncounterHasRecord())
			return new Cohort();
		*/
		StringBuilder sql = baseQuery(FollowUpConceptQuestions.PREGNANCY_STATUS);
		sql.append(" and " + OBS_ALIAS + " value_coded = " + conceptQuery(ConceptAnswer.YES));
		sql.append(" and " + OBS_ALIAS + "encounter_id in (:latestEncounter) and " + OBS_ALIAS + "person_id in (:persons)");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		query.setParameterList("latestEncounter", latestFollowUpEncounter);
		
		query.setParameterList("persons", baseCohort.getMemberIds());
		return new Cohort(query.list());
	}
	
	public Cohort getPMTCTARTCohort() {
		
		if (baseCohort.isEmpty() || baseEncounter.isEmpty())
			return new Cohort();
		
		String stringQuery = "select distinct person_id\n" + "from obs\n" + "where concept_id = "
		        + conceptQuery(PMTCTConceptQuestions.PMTCT_OTZ_ENROLLMENT_DATE) + "and value_datetime >= :start "
		        + " and value_datetime <= :end and encounter_id in (:pmtctEncounter) ";
		if (!baseCohort.getMemberIds().isEmpty())
			stringQuery = stringQuery + " and person_id in (:personIdList)";
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringQuery);
		query.setDate("start", startDate);
		query.setDate("end", endDate);
		if (!baseCohort.getMemberIds().isEmpty())
			query.setParameterList("personIdList", baseCohort.getMemberIds());
		query.setParameterList("pmtctEncounter", baseEncounter);
		return new Cohort(query.list());
	}
	
	public Cohort getCohortByPMTCTEnrollmentStatus(String PMTCTEnrollmentType) {
		
		/*if (!isCohortAndEncounterHasRecord())
			return new Cohort();*/
		
		String stringQuery = "select distinct person_id from obs where concept_id = "
		        + conceptQuery(PMTCTConceptQuestions.PMTCT_STATUS_AT_ENROLLMENT) + " and value_coded = "
		        + conceptQuery(PMTCTEnrollmentType) + " and encounter_id in (:encounterIdList)";
		
		stringQuery = stringQuery + " and person_id in (:personIdList)";
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringQuery);
		
		query.setParameterList("personIdList", pmtctARTCohort.getMemberIds());
		query.setParameterList("encounterIdList", baseEncounter);
		return new Cohort(query.list());
		
	}
	
	private boolean isCohortAndEncounterHasRecord() {
		return Objects.isNull(pmtctARTCohort) || Objects.isNull(baseEncounter) || pmtctARTCohort.isEmpty()
		        || baseEncounter.isEmpty();
	}
}
