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
		latestFollowUpEncounter = encounterQuery.getAliveFollowUpEncounters(null, endDate);
		baseEncounter = encounterQuery.getEncounters(
		    Collections.singletonList(PMTCTConceptQuestions.PMTCT_OTZ_ENROLLMENT_DATE), startDate, endDate,
		    latestFollowUpEncounter);
		baseCohort = getByPregnantStatus();
		pmtctARTCohort = getPMTCTARTCohort();
		newOnARTPMTCTARTCohort = getNewOnArtCohort("F", startDate, endDate, pmtctARTCohort, latestFollowUpEncounter);
		alreadyOnARTPMTCTARTCohort = HMISUtilies.getOuterUnion(pmtctARTCohort, newOnARTPMTCTARTCohort);
	}
	
	public Cohort getByPregnantStatus() {
		StringBuilder sql = baseConceptQuery(FollowUpConceptQuestions.PREGNANCY_STATUS);
		sql.append(" and " + CONCEPT_BASE_ALIAS_OBS + " value_coded = " + conceptQuery(ConceptAnswer.YES));
		sql.append(" and " + CONCEPT_BASE_ALIAS_OBS + "encounter_id in (:latestEncounter) ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		query.setParameterList("latestEncounter", latestFollowUpEncounter);
		
		return new Cohort(query.list());
	}
	
	public Cohort getPMTCTARTCohort() {
		String stringQuery = "select distinct person_id\n" + "from obs\n" + "where concept_id = "
		        + conceptQuery(PMTCTConceptQuestions.PMTCT_OTZ_ENROLLMENT_DATE) + "and value_datetime >= :start "
		        + " and value_datetime <= :end ";
		if (!baseCohort.getMemberIds().isEmpty())
			stringQuery = stringQuery + " and person_id in (:personIdList)";
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringQuery);
		query.setDate("start", startDate);
		query.setDate("end", endDate);
		if (!baseCohort.getMemberIds().isEmpty())
			query.setParameterList("personIdList", baseCohort.getMemberIds());
		
		return new Cohort(query.list());
	}
	
	public Cohort getCohortByPMTCTEnrollmentStatus(String PMTCTEnrollmentType) {
		if (pmtctARTCohort.getMemberIds().isEmpty())
			return new Cohort();
		String stringQuery = "select distinct person_id\\n\" + \"from obs\\n\" + \"where concept_id = "
		        + conceptQuery(PMTCTConceptQuestions.PMTCT_STATUS_AT_ENROLLMENT) + " and value_coded = "
		        + conceptQuery(PMTCTEnrollmentType);
		if (!pmtctARTCohort.getMemberIds().isEmpty())
			stringQuery = stringQuery + " and person_id in (:personIdList)";
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringQuery);
		
		if (!pmtctARTCohort.getMemberIds().isEmpty())
			query.setParameterList("personIdList", pmtctARTCohort.getMemberIds());
		
		return new Cohort(query.list());
		
	}
}
