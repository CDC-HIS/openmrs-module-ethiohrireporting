package org.openmrs.module.ohrireports.api.impl.query;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.REGIMEN;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.FOLLOW_UP_STATUS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.ART_START_DATE;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.BaseEthiOhriQuery;
import org.openmrs.module.ohrireports.helper.EthiopianDate;
import org.openmrs.module.ohrireports.helper.EthiopianDateConverter;

public class BaseLineListQuery extends BaseEthiOhriQuery {
	
	private DbSessionFactory sessionFactory;
	
	public BaseLineListQuery(DbSessionFactory _SessionFactory) {
		sessionFactory = _SessionFactory;
	}
	
	public HashMap<Integer, Object> getArtStartDate(Cohort cohort, Date startDate, Date endDate) {
		
		StringBuilder sql = baseValueDateQuery(ART_START_DATE);
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
		        + SUB_QUERY_BASE_ALIAS_OBS + "concept_id = " + conceptQuery(ART_START_DATE);
		
		sql.append("and " + VALUE_DATE_BASE_ALIAS_OBS + "person_id in (:outerPatientIds)  ");
		sql.append(" and " + VALUE_DATE_BASE_ALIAS_OBS + "obs_id in ");
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
	
	public HashMap<Integer, Object> getRegiment(List<Integer> baseEncounters,Cohort cohort) {

		Query query = getObs(baseEncounters,REGIMEN, cohort);
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
	
	public HashMap<Integer, Object> getFollowUpStatus(List<Integer> baseEncounters, Cohort cohort) {
		return getDictionary(getObs(baseEncounters, FOLLOW_UP_STATUS, cohort));
	}
	
	public HashMap<Integer, Object> getIdentifier(Cohort cohort, String identifierType) {
		StringBuilder sql = new StringBuilder("SELECT pi.patient_id,pi.identifier FROM patient_identifier as pi ");
		sql.append("inner join patient_identifier_type as pit on pit.patient_identifier_type_id = pi.identifier_type and pit.uuid ='"
		        + identifierType + "' and pi.patient_id in (:cohort) ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		query.setParameterList("cohort", cohort.getMemberIds());
		
		return getDictionary(query);
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
	
	protected Query getObs(List<Integer> baseEncounters, String concept, Cohort cohort) {
		StringBuilder sql = baseConceptQuery(concept);
		
		sql.append(" and  " + CONCEPT_BASE_ALIAS_OBS + "encounter_id in (:encounters) ");
		sql.append(" and  " + CONCEPT_BASE_ALIAS_OBS + "person_id in (:cohorts) ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("encounters", baseEncounters);
		query.setParameterList("cohorts", cohort.getMemberIds());
		
		return query;
	}
	
	protected Query getObsNumber(List<Integer> baseEncounters, String concept, Cohort cohort) {
		StringBuilder sql = baseValueNumberQuery(concept);
		
		sql.append(" and  " + VALUE_NUMERIC_BASE_ALIAS_OBS + "encounter_id in (:encounters) ");
		sql.append(" and  " + VALUE_NUMERIC_BASE_ALIAS_OBS + "person_id in (:cohorts) ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("encounters", baseEncounters);
		query.setParameterList("cohorts", cohort.getMemberIds());
		
		return query;
	}
	
	public HashMap<Integer, Object> getObsValueDate(List<Integer> baseEncounters, String concept, Cohort cohort) {
		return getDictionary(getObsValueDateQuery(baseEncounters, concept, cohort));
	}
	
	protected Query getObsValueDateQuery(List<Integer> baseEncounters, String concept, Cohort cohort) {
		StringBuilder sql = baseValueDateQuery(concept);
		
		sql.append(" and  " + VALUE_DATE_BASE_ALIAS_OBS + "encounter_id in (:encounters) ");
		sql.append(" and  " + VALUE_DATE_BASE_ALIAS_OBS + "person_id in (:cohorts) ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("encounters", baseEncounters);
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
}
