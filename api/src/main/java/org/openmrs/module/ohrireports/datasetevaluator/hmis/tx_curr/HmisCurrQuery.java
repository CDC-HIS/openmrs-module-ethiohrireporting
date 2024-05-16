package org.openmrs.module.ohrireports.datasetevaluator.hmis.tx_curr;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.REGIMEN;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.PREGNANCY_STATUS;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HmisCurrQuery extends PatientQueryImpDao {
	
	private Date endDate;
	
	private final DbSessionFactory sessionFactory;
	
	private Cohort baseCohort;
	
	List<Integer> encounters;
	
	public Cohort getBaseCohort() {
		return baseCohort;
	}
	
	@Autowired
	public HmisCurrQuery(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		setSessionFactory(sessionFactory);
	}
	
	public void loadInitialCohort(Date end, List<Integer> _encounters) {
		endDate = end;
		encounters = _encounters;
		baseCohort = getActiveOnArtCohort("", null, end, null, encounters);
	}
	
	public Cohort getCohortByGender(String gender, Cohort cohort) {
		StringBuilder sql = new StringBuilder("select person_id from person where gender='" + gender
		        + "' and person_id in (:personIdList)");
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		query.setParameterList("personIdList", cohort.getMemberIds());
		return new Cohort(query.list());
	}
	
	public Cohort getByRegiment(List<String> regimentConceptUUIDS, Cohort cohort) {
		StringBuilder sql = baseQuery(REGIMEN);
		sql.append(" and " + OBS_ALIAS + "value_coded in " + conceptQuery(regimentConceptUUIDS));
		sql.append(" and " + OBS_ALIAS + "encounter_id in (:latestEncounter)");
		if (cohort != null && !cohort.isEmpty())
			sql.append(" and " + OBS_ALIAS + "person_id in (:cohorts)");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("latestEncounter", encounters);
		
		if (cohort != null && !cohort.isEmpty())
			query.setParameterList("cohorts", cohort.getMemberIds());
		return new Cohort(query.list());
		
	}
	
	public List<Regiment> getPatientCountByRegiment(List<String> regimentConceptUUIDS, Cohort cohort) {
		List<Regiment> regiments = getConcepts(regimentConceptUUIDS);
		if (cohort == null || cohort.size() == 0)
			return regiments;
		
		StringBuilder sql = baseConceptCountQuery(REGIMEN);
		
		sql.append(" and ").append(CONCEPT_BASE_ALIAS_OBS).append("encounter_id in (:latestEncounter)");
		
		sql.append(" and " + CONCEPT_BASE_ALIAS_OBS + "person_id in (:cohorts)");
		sql.append(" and " + CONCEPT_BASE_ALIAS_OBS + "value_coded in " + conceptQuery(regimentConceptUUIDS));
		
		sql.append(" group by " + CONCEPT_BASE_ALIAS_OBS + "value_coded");
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("latestEncounter", encounters);
		query.setParameterList("cohorts", cohort.getMemberIds());
		
		List list = query.list();
		
		for (Object object : list) {
			if (object instanceof Object[]) {
				Object[] objects = (Object[]) object;
				for (Regiment regiment : regiments) {
					Integer conceptId = (Integer) objects[1];
					if (conceptId.equals(regiment.getConceptId())) {
						regiment.setCount((BigInteger) objects[0]);
					}
				}
				
			}
		}
		return regiments;
		
	}
	
	public Cohort getGenderSpecificCohort(String gender, Cohort cohort) {
		if (cohort == null || cohort.isEmpty())
			return new Cohort();
		
		StringBuilder sql = new StringBuilder(" select person_id from person where gender ='" + gender + "' ");
		sql.append(" and  person_id in (:cohorts)");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("cohorts", cohort.getMemberIds());
		List list = query.list();
		return new Cohort(list);
		
	}
	
	public List<Regiment> getConcepts(List<String> concepts) {
		StringBuilder sql = new StringBuilder(
				"select c.concept_id,cn.name from concept as c inner join concept_name as cn on c.concept_id = cn.concept_id ");
		sql.append(" where c.concept_id in " + conceptQuery(concepts));
		sql.append(" order by cn.name asc");

		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());

		List list = query.list();

		List<Regiment> regiments = new ArrayList<>();

		for (Object object : list) {
			if (object != null && object instanceof Object[]) {
				Object[] objects = (Object[]) object;
				regiments.add(new Regiment(new BigInteger("0"), (Integer) objects[0], (String) objects[1]));

			}
		}
		return regiments;
	}
	
	public HashMap<Integer, Object> getByPregnantStatus(Cohort cohort) {
		StringBuilder sql = baseConceptQuery(PREGNANCY_STATUS);
		sql.append(" and " + CONCEPT_BASE_ALIAS_OBS + "encounter_id in (:latestEncounter) ");
		sql.append(" and " + CONCEPT_BASE_ALIAS_OBS + "person_id in (:cohorts)");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		query.setParameterList("latestEncounter", encounters);
		query.setParameterList("cohorts", cohort.getMemberIds());
		
		return getDictionary(query);
	}
	
	public int countByPregnancyStatus(HashMap<Integer, Object> lHashMap, String status) {
		int count = 0;
		String castStatus = "";
		for (Object o : lHashMap.values()) {
			if (!Objects.isNull(o)) {
				castStatus = ((String) o);
				if (status.toLowerCase().equals(castStatus.toLowerCase())) {
					count++;
				}
			}
		}
		return count;
	}
	
	protected HashMap<Integer, Object> getDictionary(Query query) {
		List list = query.list();
		HashMap<Integer, Object> dictionary = new HashMap<>();
		int personId = 0;
		Object[] objects;
		for (Object object : list) {

			objects = (Object[]) object;
			personId = (Integer) objects[0];

			if (dictionary.get((Integer) personId) == null) {
				dictionary.put(personId, objects[1]);
			}

		}

		return dictionary;
	}
	
	public Cohort getLessThanOrEqualAge(int maxAge, Cohort cohort) {
		List<Integer> list = new ArrayList<>();
		List<Person> persons = getPersons(cohort);
		int age = 0;
		for (Person person : persons) {
			age = person.getAge(endDate);
			if (age <= maxAge) {
				list.add(person.getPersonId());
			}

		}
		return new Cohort(list);
	}
	
	public Cohort getGreaterOrEqualToAge(int minAge, Cohort cohort) {
		List<Integer> list = new ArrayList<>();
		List<Person> persons = getPersons(cohort);

		int age = 0;
		for (Person person : persons) {
			age = person.getAge(endDate);
			if (age >= minAge) {
				list.add(person.getPersonId());
			}

		}
		return new Cohort(list);
	}
	
	public Cohort getBetweenAge(int minAge, int maxAge, Cohort cohort) {
		List<Integer> list = new ArrayList<>();
		List<Person> persons = getPersons(cohort);

		int age = 0;
		for (Person person : persons) {
			age = person.getAge(endDate);
			if (age >= minAge && age <= maxAge) {
				list.add(person.getPersonId());
			}

		}
		return new Cohort(list);
	}
	
	public Cohort getLessThanAge(int maxAge, Cohort cohort) {
		List<Integer> list = new ArrayList<>();
		List<Person> persons = getPersons(cohort);
		int age = 0;
		for (Person person : persons) {
			age = person.getAge(endDate);
			if (age < maxAge) {
				list.add(person.getPersonId());
			}

		}
		return new Cohort(list);
	}
	
	public class Regiment {
		
		private BigInteger count;
		
		public void setCount(BigInteger count) {
			this.count = count;
		}
		
		private int conceptId;
		
		private String name;
		
		private String gender;
		
		public void setGender(String gender) {
			this.gender = gender;
		}
		
		public String getGender() {
			return gender;
		}
		
		public BigInteger getCount() {
			return count;
		}
		
		public int getConceptId() {
			return conceptId;
		}
		
		public String getName() {
			return name;
		}
		
		public Regiment(BigInteger count, int conceptId, String name, String gender) {
			this.count = count;
			this.conceptId = conceptId;
			this.name = name;
			this.gender = gender;
		}
		
		public Regiment(BigInteger count, int conceptId, String name) {
			this.count = count;
			this.conceptId = conceptId;
			this.name = name;
		}
		
	}
	
}
