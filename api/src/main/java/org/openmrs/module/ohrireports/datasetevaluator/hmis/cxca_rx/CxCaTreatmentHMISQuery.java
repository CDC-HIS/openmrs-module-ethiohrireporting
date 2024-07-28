package org.openmrs.module.ohrireports.datasetevaluator.hmis.cxca_rx;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.constants.ConceptAnswer;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class CxCaTreatmentHMISQuery extends PatientQueryImpDao {
	
	private DbSessionFactory sessionFactory;
	
	private Cohort baseCohort;
	
	List<Integer> encounters;
	
	public Cohort getBaseCohort() {
		return baseCohort;
	}
	
	public List<Integer> getBaseEncounter() {
		return baseEncounter;
	}
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	private List<Integer> baseEncounter;
	
	private List<Integer> currentEncounter;
	
	private Date startDate;
	
	private Date endDate;
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
		baseEncounter = encounterQuery.getEncounters(
		    Arrays.asList(FollowUpConceptQuestions.CXCA_TREATMENT_STARTING_DATE, FollowUpConceptQuestions.FOLLOW_UP_DATE),
		    startDate, endDate);
		currentEncounter = baseEncounter = refineBaseEncounter();
	}
	
	@Autowired
	public CxCaTreatmentHMISQuery(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		setSessionFactory(sessionFactory);
	}
	
	private List<Integer> refineBaseEncounter() {
		List<String> conceptUUIDs = Arrays.asList(ConceptAnswer.CXCA_TREATMENT_TYPE_THERMOCOAGULATION,
		    ConceptAnswer.CXCA_TREATMENT_TYPE_LEEP, FollowUpConceptQuestions.CXCA_TREATMENT_TYPE_CRYOTHERAPY);
		StringBuilder stringQuery = new StringBuilder("select distinct ob.encounter_id from obs as ob ");
		stringQuery.append(" where ob.encounter_id in (:baseEncounter) ");
		stringQuery.append(" and ob.concept_id = ").append(
		    conceptQuery(FollowUpConceptQuestions.CXCA_TREATMENT_PRECANCEROUS_LESIONS));
		stringQuery.append(" and ob.value_coded in ").append(conceptQuery(conceptUUIDs));
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringQuery.toString());
		
		query.setParameterList("baseEncounter", baseEncounter);
		List<Integer> response = (List<Integer>) query.list();
		return response;
	}
	
	public Cohort getCohortByConceptAndBaseEncounter(String questionConcept, String answerConcept) {
		String stringQuery = "SELECT distinct person_id\n" + "FROM obs\n" + "WHERE concept_id = "
		        + conceptQuery(questionConcept) + "AND value_coded = " + conceptQuery(answerConcept)
		        + "and encounter_id in ( :baseEncounter)";
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringQuery);
		query.setParameterList("baseEncounter", baseEncounter);
		return new Cohort(query.list());
	}
	
}
