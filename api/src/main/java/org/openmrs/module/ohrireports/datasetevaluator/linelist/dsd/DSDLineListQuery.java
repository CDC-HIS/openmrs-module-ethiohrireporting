package org.openmrs.module.ohrireports.datasetevaluator.linelist.dsd;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.query.ObsElement;
import org.openmrs.module.ohrireports.api.impl.query.DSDQuery;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.HivArtRetQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.DSD_ASSESSMENT_DATE;

@Component
public class DSDLineListQuery extends ObsElement {
	
	private DbSessionFactory sessionFactory;
	
	@Autowired
	private DSDQuery dsdQuery;
	@Autowired
	EncounterQuery encounterQuery;

	public List<Integer> getBaseEncounter() {
		return baseEncounter;
	}

	public Cohort getBaseCohort() {
		return baseCohort;
	}

	private List<Integer> baseEncounter = new ArrayList<>();
	private List<Integer> latestDSDAssessmentEncounter = new ArrayList<>();
	private List<Integer> initialDSDAssessmentEncounter = new ArrayList<>();

	public List<Integer> getLatestEncounter() {
		return latestEncounter;
	}

	public List<Integer> getLatestDSDAssessmentEncounter() {
		return latestDSDAssessmentEncounter;
	}

	public List<Integer> getInitialDSDAssessmentEncounter() {
		return initialDSDAssessmentEncounter;
	}

	private List<Integer> latestEncounter = new ArrayList<>();
	private Cohort baseCohort = new Cohort();

	/**
	 * @param _SessionFactory
	 */
	public DSDLineListQuery(DbSessionFactory _SessionFactory) {
		super(_SessionFactory);
		sessionFactory = _SessionFactory;
	}
	
	public void generateReport(Date start, Date end) {
		//dsdQuery.generateBaseReport(start, end);
		baseEncounter = encounterQuery.getEncounters(Collections.singletonList(DSD_ASSESSMENT_DATE), start, end);
		latestDSDAssessmentEncounter = encounterQuery.getEncounters(Collections.singletonList(DSD_ASSESSMENT_DATE), null, end, getCohort(baseEncounter));
		initialDSDAssessmentEncounter = encounterQuery.getFirstEncounterByObsDate(null, null, DSD_ASSESSMENT_DATE, getCohort(baseEncounter));
		baseCohort = getCohort(initialDSDAssessmentEncounter);
		latestEncounter = encounterQuery.getLatestDateByFollowUpDate(null, null);
	}

	public Cohort getCohort(List<Integer> encounterIds) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(
				"select distinct (person_id) from obs where encounter_id in (:encounterIds) ");
		query.setParameterList("encounterIds", encounterIds);

		return new Cohort(query.list());

	}
	
//	public List<Integer> getBaseEncounter() {
//		return dsdQuery.getBaseEncounter();
//	}
//
//	public List<Integer> getLatestEncounter() {
//		return dsdQuery.getLatestEncounter();
//	}
//
//	public List<Integer> getLatestDSDAssessmentEncounter() {
//		return dsdQuery.getLatestDSDAssessmentEncounter();
//	}
//
//	public List<Integer> getInitialDSDAssessmentEncounter() {
//		return dsdQuery.getInitialDSDAssessmentEncounter();
//	}
//
//	public Cohort getBaseCohort() {
//		return dsdQuery.getBaseCohort();
//	}
//
	public List<Person> getPersons(Cohort cohort) {
		return dsdQuery.getPersonList(cohort);
	}
}
