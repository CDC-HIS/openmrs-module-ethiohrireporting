package org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_art_fp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.constants.ConceptAnswer;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.ohrireports.constants.ConceptAnswer.YES;
import static org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions.*;

@Component
public class HivArtFpQuery extends PatientQueryImpDao {
	
	private DbSessionFactory sessionFactory;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	private Date startDate, endDate;
	
	private Cohort cohort = null;

	public HashMap<Integer, Object> getFollowUpDateMap() {
		return followUpDateMap;
	}

	private HashMap<Integer, Object> followUpDateMap = new HashMap<>();
	
	private List<Integer> baseEncounter;
	
	public Cohort getCohort() {
		return cohort;
	}
	
	public List<Integer> getBaseEncounter() {
		return baseEncounter;
	}
	
	@Autowired
	public HivArtFpQuery(DbSessionFactory sessionFactory) {
		
		setSessionFactory(sessionFactory);
		this.sessionFactory = sessionFactory;
	}
	
	public void generateReport(Date start, Date end) {
		startDate = start;
		endDate = end;
		baseEncounter = encounterQuery.getAliveFollowUpEncounters(null, end);
		cohort = getActiveOnArtCohort("F", null, endDate, null, baseEncounter);
		cohort = getPatientsOnFamilyPlanning();
		followUpDateMap=getObValue(FOLLOW_UP_DATE,cohort,ObsValueType.DATE_VALUE,baseEncounter);

	}
	
	private Cohort getPatientsOnFamilyPlanning() {
		StringBuilder sqlBuilder = new StringBuilder("select distinct fb.person_id from obs as fb where fb.concept_id  =")
		        .append(conceptQuery(FAMILY_PLANNING_METHODS));
		sqlBuilder
		        .append(" and fb.value_coded not in ")
		        .append("(select concept_id from concept where uuid ='" + ConceptAnswer.ABSTINENCE + "')")
		        .append(
		            " and fb.encounter_id in (:encounters) and fb.person_id in(:fbPersonIds) and fb.person_id not in (select distinct p.person_id from obs as p where ");
		sqlBuilder.append("  p.concept_id =").append(conceptQuery(PREGNANCY_STATUS)).append(" and p.value_coded = ")
		        .append(conceptQuery(YES));
		sqlBuilder.append(" and p.encounter_id in (:pEncounters) and p.person_id in(:pPersonIds))");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
		query.setParameterList("encounters", baseEncounter);
		query.setParameterList("pEncounters", baseEncounter);
		query.setParameterList("pPersonIds", cohort.getMemberIds());
		query.setParameterList("fbPersonIds", cohort.getMemberIds());
		
		return new Cohort(query.list());
		
	}
	
	public Integer getPatientByMethodOfOtherFP(List<String> conceptTypeUUID, boolean include) {
		String valueCodedCondition = include ? "in " : "not in ";
		
		String sqlBuilder = String.format(
		    "select obt.person_id from obs as obt where obt.concept_id = %s and obt.value_coded %s %s "
		            + "and obt.encounter_id in (:encounter) and obt.person_id in (:cohort)",
		    conceptQuery(FAMILY_PLANNING_METHODS), valueCodedCondition, conceptQuery(conceptTypeUUID));
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder);
		query.setParameterList("encounter", baseEncounter);
		query.setParameterList("cohort", cohort.getMemberIds());
		return getCount(getPersons(new Cohort(query.list())), 15, 49);
	}
	
	private Integer getCount(List<Person> personList, int initialAge, int maxAge) {
		List<Person> _personList = new ArrayList<>();
		for (Person person : personList) {
			int age = person.getAge(endDate);
			if (person.getGender().equals("F") && age >= initialAge && age <= maxAge) {
				_personList.add(person);
			}
		}
		return _personList.size();
	}
}
