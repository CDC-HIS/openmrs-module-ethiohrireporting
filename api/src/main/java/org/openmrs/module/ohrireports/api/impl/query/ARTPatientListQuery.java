package org.openmrs.module.ohrireports.api.impl.query;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.constants.EncounterType;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class ARTPatientListQuery extends PatientQueryImpDao {
	
	private DbSessionFactory sessionFactory;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	private Cohort baseCohort;
	
	private Date startDate;
	
	private Date endDate;
	
	private List<Integer> baseEncounter;
	
	private List<Integer> followupEncounter;
	
	private List<Integer> firstFollowUp;
	
	public List<Integer> getFirstFollowUp() {
		return firstFollowUp;
	}
	
	public Cohort getBaseCohort() {
		return baseCohort;
	}
	
	public List<Integer> getFollowupEncounter() {
		return followupEncounter;
	}
	
	public List<Integer> getBaseEncounter() {
		return baseEncounter;
	}
	
	@Autowired
	public ARTPatientListQuery(DbSessionFactory _SessionFactory) {
		sessionFactory = _SessionFactory;
		setSessionFactory(sessionFactory);
	}
	
	public void generateReport(Date startDate, Date endDate) {
		followupEncounter = encounterQuery.getLatestDateByFollowUpDate(startDate, endDate);
		
		Cohort followupCohort = getCohort(followupEncounter);
		baseEncounter = encounterQuery.getLatestDateByEnrollmentDate(followupCohort.getMemberIds(),
		    EncounterType.INTAKE_A_ENCOUNTER_TYPE);
		
		baseCohort = getCohort(baseEncounter);
		baseCohort = Cohort.union(baseCohort, followupCohort);
		
		firstFollowUp = encounterQuery.getFirstEncounterByObsDate(null, endDate, FollowUpConceptQuestions.FOLLOW_UP_DATE,
		    baseCohort);
		
	}
	
	public void generateReport(Date startDate, Date endDate, String followupStatusUUID) {
		followupEncounter = encounterQuery.getLatestDateByFollowUpDate(startDate, endDate);
		followupEncounter = encounterQuery.getEncounters(followupEncounter, FollowUpConceptQuestions.FOLLOW_UP_STATUS,
		    Collections.singletonList(followupStatusUUID));
		
		Cohort followupCohort = getCohort(followupEncounter);
		baseEncounter = encounterQuery.getLatestDateByEnrollmentDate(followupCohort.getMemberIds(),
		    EncounterType.INTAKE_A_ENCOUNTER_TYPE);
		
		baseCohort = getCohort(baseEncounter);
		baseCohort = Cohort.union(baseCohort, followupCohort);
		
	}
	
	public void generateReport() {
		this.endDate = new Date();
		followupEncounter = encounterQuery.getLatestDateByFollowUpDate(null, endDate);
		
		Cohort followupCohort = getCohort(followupEncounter);
		baseEncounter = encounterQuery.getLatestDateByEnrollmentDate(followupCohort.getMemberIds(),
		    EncounterType.INTAKE_A_ENCOUNTER_TYPE);
		
		baseCohort = getCohort(baseEncounter);
		baseCohort = Cohort.union(baseCohort, followupCohort);
	}
	
}
