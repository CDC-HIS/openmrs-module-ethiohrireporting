package org.openmrs.module.ohrireports.datasetevaluator.linelist.hivPositiveTracking;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.api.impl.query.BaseLineListQuery;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.STOP;

public class HIVPositiveTrackingLineListQuery extends BaseLineListQuery {
	
	private final DbSessionFactory sessionFactory;
	
	private List<Integer> baseEncounter;
	
	@Autowired
	EncounterQuery encounterQuery;
	
	public List<Integer> getBaseEncounter() {
		return baseEncounter;
	}
	
	@Autowired
	PatientQueryImpDao patientQueryImpDao;
	
	private Cohort baseCohort;
	
	public Cohort getBaseCohort() {
		return baseCohort;
	}
	
	public HIVPositiveTrackingLineListQuery(DbSessionFactory sessionFactory) {
		super(sessionFactory);
		this.sessionFactory = sessionFactory;
	}
	
	public void generateReport(Date start, Date end) {
		baseEncounter = encounterQuery.getAllEncounters(Collections.singletonList(POSITIVE_TRACKING_REGISTRATION_DATE),
		    start, end, HIV_POSITIVE_TRACKING_ENCOUNTER_TYPE);
		baseCohort = getCohort(baseEncounter);
		
	}
	
	public Cohort getCohort(List<Integer> encounterIds) {
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(
		    "select distinct (person_id) from obs where encounter_id in (:encounterIds) ");
		query.setParameterList("encounterIds", encounterIds);
		
		return new Cohort(query.list());
		
	}
	
	public List<Person> getPersons(Cohort baseCohort) {
		return patientQueryImpDao.getPersons(baseCohort);
	}
}
