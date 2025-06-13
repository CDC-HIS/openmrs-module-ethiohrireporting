package org.openmrs.module.ohrireports.api.impl.query;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.BaseEthiOhriQuery;
import org.openmrs.module.ohrireports.constants.ConceptAnswer;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.openmrs.module.ohrireports.helper.EthiopianDate;
import org.openmrs.module.ohrireports.helper.EthiopianDateConverter;

/**
 * Specialized methods for retrieving value of particular cohort with given concept value type as
 * key value pair
 */
public class ObsElement extends BaseEthiOhriQuery {
	
	private DbSessionFactory sessionFactory;
	
	/**
	 * @param _SessionFactory
	 */
	public ObsElement(DbSessionFactory _SessionFactory) {
		sessionFactory = _SessionFactory;
	}
	
	/**
	 * @param cohort
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public HashMap<Integer, Object> getArtStartDate(Cohort cohort, Date startDate, Date endDate) {
		
		StringBuilder sql = baseValueDateQuery(FollowUpConceptQuestions.ART_START_DATE);
		String subQuery = " ";
		if (!Objects.isNull(startDate)) {
			sql.append(" and " + VALUE_DATE_BASE_ALIAS_OBS + "value_datetime >= :start ");
			subQuery = " " + SUB_QUERY_BASE_ALIAS_OBS + "value_datetime >= :startOnOrAfter and ";
		}
		
		if (!Objects.isNull(endDate)) {
			sql.append(" and " + VALUE_DATE_BASE_ALIAS_OBS + "value_datetime <= :end ");
			subQuery = subQuery + " " + SUB_QUERY_BASE_ALIAS_OBS + "value_datetime <= :endOnOrBefore and ";
		}
		
		subQuery = subQuery + "   " + SUB_QUERY_BASE_ALIAS_OBS + "person_id in (:patientIds) and "
		        + SUB_QUERY_BASE_ALIAS_OBS + "concept_id = " + conceptQuery(FollowUpConceptQuestions.ART_START_DATE);
		
		sql.append("and ").append(VALUE_DATE_BASE_ALIAS_OBS).append("person_id in (:outerPatientIds)  ");
		sql.append(" and ").append(VALUE_DATE_BASE_ALIAS_OBS).append("obs_id in ");
		sql.append(baseSubQuery(subQuery).toString());
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		if (!Objects.isNull(startDate)) {
			
			query.setParameter("start", startDate);
			query.setParameter("startOnOrAfter", startDate);
			
		}
		
		if (!Objects.isNull(endDate)) {
			query.setParameter("endOnOrBefore", endDate);
			query.setParameter("end", endDate);
		}
		
		query.setParameterList("outerPatientIds", cohort.getMemberIds());
		
		query.setParameterList("patientIds", cohort.getMemberIds());
		
		return getDictionary(query);
		
	}
	
	/**
	 * @param baseEncounters
	 * @param cohort
	 * @return
	 */
	public HashMap<Integer, Object> getRegiment(List<Integer> baseEncounters, Cohort cohort) {

        Query query = getObs(baseEncounters, FollowUpConceptQuestions.REGIMEN, cohort);
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
	
	/**
	 * @param baseEncounters
	 * @param cohort
	 * @return
	 */
	public HashMap<Integer, Object> getFollowUpStatus(List<Integer> baseEncounters, Cohort cohort) {
		return getLabelDictionary(getObsWithUuid(baseEncounters, FollowUpConceptQuestions.FOLLOW_UP_STATUS, cohort));
	}
	
	public HashMap<Integer, Object> getNutritionalStatus(List<Integer> baseEncounters, Cohort cohort) {
		return getDictionary(getObs(baseEncounters, FollowUpConceptQuestions.NUTRITIONAL_SCREENING_RESULT, cohort));
	}
	
	/**
	 * @param cohort
	 * @param identifierType
	 * @return
	 */
	public HashMap<Integer, Object> getIdentifier(Cohort cohort, String identifierType) {
		StringBuilder sql = new StringBuilder("SELECT pi.patient_id,pi.identifier FROM patient_identifier as pi ");
		sql.append(
		    "inner join patient_identifier_type as pit on pit.patient_identifier_type_id = pi.identifier_type and pit.uuid ='")
		        .append(identifierType).append("' and pi.patient_id in (:cohort) ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		query.setParameterList("cohort", cohort.getMemberIds());
		
		return getDictionary(query);
	}
	
	public HashMap<Integer, Object> getByResult(String concept, Cohort cohort, List<Integer> encounterIds) {
		
		StringBuilder sql = baseConceptQuery(concept);
		sql.append(" and " + CONCEPT_BASE_ALIAS_OBS + "encounter_id in (:encounters)");
		sql.append(" and  " + CONCEPT_BASE_ALIAS_OBS + "person_id in (:cohorts)");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("encounters", encounterIds);
		query.setParameterList("cohorts", cohort.getMemberIds());
		
		return getDictionary(query);
		
	}
	
	public HashMap<Integer, Object> getByResult(String concept, Cohort cohort, List<Integer> encounterIds,
	        String encounterType) {
		
		StringBuilder sql = baseConceptQuery(concept, encounterType);
		sql.append(" and ").append(CONCEPT_BASE_ALIAS_OBS).append("encounter_id in (:encounters)");
		sql.append(" and  ").append(CONCEPT_BASE_ALIAS_OBS).append("person_id in (:cohorts)");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("encounters", encounterIds);
		query.setParameterList("cohorts", cohort.getMemberIds());
		
		return getDictionary(query);
		
	}
	
	public HashMap<Integer, Object> getByResultByUUID(String concept, String answerConcept, Cohort cohort,
	        List<Integer> encounterIds) {
		
		StringBuilder sql = baseConceptUUIDQuery(concept);
		sql.append(" and ").append(CONCEPT_BASE_ALIAS_OBS).append("encounter_id in (:encounters)");
		sql.append(" and  ").append(CONCEPT_BASE_ALIAS_OBS).append("person_id in (:cohorts)");
		sql.append(" and  ").append(CONCEPT_BASE_ALIAS_OBS).append("value_coded =").append(conceptQuery(answerConcept));
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("encounters", encounterIds);
		query.setParameterList("cohorts", cohort.getMemberIds());
		
		return getDictionary(query);
		
	}
	
	public HashMap<Integer, Object> getByValueNumeric(String concept, Cohort cohort, List<Integer> encounterIds) {
		return getDictionary(getObsNumber(encounterIds, concept, cohort));
	}
	
	public HashMap<Integer, Object> getByValueText(String concept, Cohort cohort, List<Integer> encounterIds) {
		return getDictionary(getObsText(encounterIds, concept, cohort));
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
	
	protected HashMap<Integer, Object> getLabelDictionary(Query query) {
		List list = query.list();
		HashMap<Integer, Object> dictionary = new HashMap<>();
		int personId = 0;
		Object[] objects;
		for (Object object : list) {
			objects = (Object[]) object;
			personId = (Integer) objects[0];
			if (dictionary.get((Integer) personId) == null) {
				dictionary.put(personId, ConceptAnswer.CONCEPT_LABELS.get(objects[1]));
			}

		}

		return dictionary;
	}
	
	protected HashMap<Integer, Object> getPrepLabelDictionary(Query query) {
		List list = query.list();
		HashMap<Integer, Object> dictionary = new HashMap<>();
		int personId = 0;
		Object[] objects;
		for (Object object : list) {
			objects = (Object[]) object;
			personId = (Integer) objects[0];
			if (dictionary.get((Integer) personId) == null) {
				dictionary.put(personId, ConceptAnswer.PREP_STATUS_CONCEPT_LABELS.get(objects[1]));
			}

		}

		return dictionary;
	}
	
	protected Query getObs(List<Integer> baseEncounters, String concept, Cohort cohort) {
		StringBuilder sql = baseConceptQuery(concept);
		
		sql.append(" and  ").append(CONCEPT_BASE_ALIAS_OBS).append("encounter_id in (:encounters) ");
		sql.append(" and  ").append(CONCEPT_BASE_ALIAS_OBS).append("person_id in (:cohorts) ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("encounters", baseEncounters);
		query.setParameterList("cohorts", cohort.getMemberIds());
		
		return query;
	}
	
	protected Query getObs(List<Integer> baseEncounters, String concept, Cohort cohort, String encounterType) {
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(
		    baseConceptQuery(concept, encounterType).append(" and  ").append(CONCEPT_BASE_ALIAS_OBS)
		            .append("encounter_id in (:encounters) ").append(" and  ").append(CONCEPT_BASE_ALIAS_OBS)
		            .append("person_id in (:cohorts) ").toString());
		
		query.setParameterList("encounters", baseEncounters);
		query.setParameterList("cohorts", cohort.getMemberIds());
		
		return query;
	}
	
	protected Query getObsWithUuid(List<Integer> baseEncounters, String concept, Cohort cohort) {
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(
		    baseConceptUUIDQuery(concept).append(" and  ").append(CONCEPT_BASE_ALIAS_OBS)
		            .append("encounter_id in (:encounters) ").append(" and  ").append(CONCEPT_BASE_ALIAS_OBS)
		            .append("person_id in (:cohorts) ").toString());
		
		query.setParameterList("encounters", baseEncounters);
		query.setParameterList("cohorts", cohort.getMemberIds());
		
		return query;
	}
	
	protected Query getObsNumber(List<Integer> baseEncounters, String concept, Cohort cohort) {
		StringBuilder sql = baseValueNumberQuery(concept);
		
		sql.append(" and  ").append(VALUE_NUMERIC_BASE_ALIAS_OBS).append("encounter_id in (:encounters) ");
		sql.append(" and  ").append(VALUE_NUMERIC_BASE_ALIAS_OBS).append("person_id in (:cohorts) ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("encounters", baseEncounters);
		query.setParameterList("cohorts", cohort.getMemberIds());
		
		return query;
	}
	
	protected Query getObsNumber(List<Integer> baseEncounters, String concept, Cohort cohort, String encounterTypeUUID) {
		StringBuilder sql = baseValueNumberQuery(concept, encounterTypeUUID);
		
		sql.append(" and  ").append(VALUE_NUMERIC_BASE_ALIAS_OBS).append("encounter_id in (:encounters) ");
		sql.append(" and  ").append(VALUE_NUMERIC_BASE_ALIAS_OBS).append("person_id in (:cohorts) ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("encounters", baseEncounters);
		query.setParameterList("cohorts", cohort.getMemberIds());
		
		return query;
	}
	
	protected Query getObsText(List<Integer> baseEncounters, String concept, Cohort cohort) {
		StringBuilder sql = baseValueTextQuery(concept);
		
		sql.append(" and  ").append(VALUE_TEXT_BASE_ALIAS_OBS).append("encounter_id in (:encounters) ");
		sql.append(" and  ").append(VALUE_TEXT_BASE_ALIAS_OBS).append("person_id in (:cohorts) ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("encounters", baseEncounters);
		query.setParameterList("cohorts", cohort.getMemberIds());
		
		return query;
	}
	
	public HashMap<Integer, Object> getObsValueDate(List<Integer> baseEncounters, String concept, Cohort cohort) {
		return getDictionary(getObsValueDateQuery(baseEncounters, concept, cohort));
	}
	
	public HashMap<Integer, Object> getObsValueDate(List<Integer> baseEncounters, String concept, Cohort cohort,
	        String encounterType) {
		return getDictionary(getObsValueDateQuery(baseEncounters, concept, cohort, encounterType));
	}
	
	protected Query getObsValueDateQuery(List<Integer> baseEncounters, String concept, Cohort cohort) {
		StringBuilder sql = baseValueDateQuery(concept);
		
		sql.append(" and  ").append(VALUE_DATE_BASE_ALIAS_OBS).append("encounter_id in (:encounters) ");
		sql.append(" and  ").append(VALUE_DATE_BASE_ALIAS_OBS).append("person_id in (:cohorts) ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("encounters", baseEncounters);
		query.setParameterList("cohorts", cohort.getMemberIds());
		
		return query;
	}
	
	protected Query getObsValueDateQuery(List<Integer> baseEncounters, String concept, Cohort cohort, String encounterType) {
		StringBuilder sql = baseValueDateQuery(concept, encounterType);
		if (baseEncounters != null) {
			sql.append(" and  ").append(VALUE_DATE_BASE_ALIAS_OBS).append("encounter_id in (:encounters) ");
		}
		sql.append(" and  ").append(VALUE_DATE_BASE_ALIAS_OBS).append("person_id in (:cohorts) ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		if (baseEncounters != null) {
			query.setParameterList("encounters", baseEncounters);
		}
		query.setParameterList("cohorts", cohort.getMemberIds());
		
		return query;
	}
	
	public Date getDate(Object object) {
		if (object instanceof Date) {
			Date date = (Date) object;
			return date;
		}
		
		return null;
	}
	
	public String getEthiopianDate(Date date) {
		if (date == null) {
			return "--";
		}
		LocalDate lDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		EthiopianDate ethiopianDate = null;
		try {
			ethiopianDate = EthiopianDateConverter.ToEthiopianDate(lDate);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ethiopianDate == null ? "" : ethiopianDate.getDay() + "/" + ethiopianDate.getMonth() + "/"
		        + ethiopianDate.getYear();
	}
	
	/**
	 * @param encounters
	 * @param cohort
	 * @param arvDispensedInDays
	 * @return
	 */
	public HashMap<Integer, Object> getConceptName(List<Integer> encounters, Cohort cohort, String arvDispensedInDays) {
		return getDictionary(getObs(encounters, arvDispensedInDays, cohort));
	}
	
	public HashMap<Integer, Object> getConceptName(List<Integer> encounters, Cohort cohort, String conceptUUId,
	        String encounterType) {
		return getDictionary(getObs(encounters, conceptUUId, cohort, encounterType));
	}
	
	public HashMap<Integer, Object> getConceptLabel(List<Integer> encounters, Cohort cohort, String conceptUUId) {
		return getLabelDictionary(getObsWithUuid(encounters, conceptUUId, cohort));
	}
}
