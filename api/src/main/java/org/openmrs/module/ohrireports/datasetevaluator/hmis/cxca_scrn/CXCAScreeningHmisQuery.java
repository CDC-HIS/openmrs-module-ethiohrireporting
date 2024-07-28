package org.openmrs.module.ohrireports.datasetevaluator.hmis.cxca_scrn;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions.DATE_COUNSELING_GIVEN;

@Component
public class CXCAScreeningHmisQuery extends PatientQueryImpDao {
	
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
		baseEncounter = encounterQuery.getEncounters(Arrays.asList(DATE_COUNSELING_GIVEN), startDate, endDate);
		currentEncounter = baseEncounter;
	}
	
	@Autowired
	public CXCAScreeningHmisQuery(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		setSessionFactory(sessionFactory);
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
