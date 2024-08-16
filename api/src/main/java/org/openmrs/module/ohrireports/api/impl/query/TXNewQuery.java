package org.openmrs.module.ohrireports.api.impl.query;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions.DSD_ASSESSMENT_DATE;
import static org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions.DSD_CATEGORY;

@Component
public class TXNewQuery extends PatientQueryImpDao {
	
	private DbSessionFactory sessionFactory;
	
	@Autowired
	EncounterQuery encounterQuery;
	
	private Date startDate;
	
	private Date endDate;
	
	private Cohort baseCohort;
	
	public List<Person> getPersonList() {
		return personList;
	}
	
	public List<Integer> getBaseEncounter() {
		return baseEncounter;
	}
	
	public Cohort getBaseCohort() {
		return baseCohort;
	}
	
	private List<Person> personList;
	
	private List<Integer> baseEncounter;
	
	@Autowired
	public TXNewQuery(DbSessionFactory _sessionFactory) {
		setSessionFactory(_sessionFactory);
		sessionFactory = _sessionFactory;
	}
	
	public void generateReport(Date start, Date end) {
		this.startDate = start;
		this.endDate = end;
		baseEncounter = encounterQuery.getAliveFirstFollowUpEncounters(start, end);
		baseCohort = getNewOnArtCohort("", start, end, null, baseEncounter);
		personList = getPersonList(baseCohort);
	}
	
	public List<Person> getPersonList(Cohort cohort) {
		return getPersons(cohort);
	}
}
