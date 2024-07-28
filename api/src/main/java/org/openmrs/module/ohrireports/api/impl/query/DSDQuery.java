package org.openmrs.module.ohrireports.api.impl.query;

import static org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions.DSD_ASSESSMENT_DATE;
import static org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions.DSD_CATEGORY;

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

@Component
public class DSDQuery extends PatientQueryImpDao {

	private DbSessionFactory sessionFactory;
	@Autowired
	EncounterQuery encounterQuery;
	@Autowired
	public DSDQuery(DbSessionFactory _sessionFactory) {
		setSessionFactory(_sessionFactory);
		sessionFactory = _sessionFactory;
	}
	
	public List<Integer> getBaseEncounter() {
		return baseEncounter;
	}
	
	public Cohort getBaseCohort() {
		return baseCohort;
	}
	
	private List<Integer> baseEncounter = new ArrayList<>();
	private Cohort baseCohort = new Cohort();
	public  void generateBaseReport(Date start, Date end){
		baseEncounter = encounterQuery.getAliveFollowUpEncounters(null,end);
		List<Integer> latestDSDAssessmentEncounter = encounterQuery.getEncounters(Collections.singletonList(DSD_ASSESSMENT_DATE),null,end,baseEncounter);
		baseCohort =  getActiveOnArtCohort("",null,end,null,latestDSDAssessmentEncounter);
	}
	
	public Cohort getCohortByDSDCategories(String dsdCategoriesUUI){
		StringBuilder sqlBuilder = new StringBuilder("SELECT ob.person_id FROM obs as ob WHERE ");
		sqlBuilder.append(" ob.concept_id =").append(conceptQuery(DSD_CATEGORY));
		sqlBuilder.append(" and ob.value_coded=").append(conceptQuery(dsdCategoriesUUI));
		sqlBuilder.append(" and ob.person_id in (:cohorts) ");
		sqlBuilder.append(" and ob.encounter_id in (:encounters) ");
		
		Query query    =sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
		
		query.setParameterList("cohorts",baseCohort.getMemberIds());
		query.setParameterList("encounters",baseEncounter);
		return new Cohort(query.list());
		
	}
	
	public List<Person> getPersonList(Cohort cohort){
		return getPersons(cohort);
	}
}
