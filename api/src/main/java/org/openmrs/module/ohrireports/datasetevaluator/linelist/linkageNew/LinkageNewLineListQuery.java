package org.openmrs.module.ohrireports.datasetevaluator.linelist.linkageNew;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.jetbrains.annotations.NotNull;
import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.query.BaseLineListQuery;
import org.openmrs.module.ohrireports.api.impl.query.HivLinkageNewCtQuery;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_linkage_new_ct.Linkage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Component
public class LinkageNewLineListQuery extends BaseLineListQuery {
	
	@Autowired
	private HivLinkageNewCtQuery linkageNewCtQuery;
	
	private DbSessionFactory sessionFactory;
	
	/**
	 * @param _SessionFactory
	 */
	@Autowired
	public LinkageNewLineListQuery(DbSessionFactory _SessionFactory) {
		super(_SessionFactory);
		sessionFactory = _SessionFactory;
	}
	
	public List<Integer> getBaseEncounters() {
		return linkageNewCtQuery.baseEncounter;
	}
	
	public Cohort getBaseCohort() {
		return linkageNewCtQuery.baseCohort;
	}
	
	public List<Linkage> getLinkages() {
		return linkageNewCtQuery.linkageList;
	}
	
	public void initializeLinkage(Date start, Date end) {
		linkageNewCtQuery.initialize(start, end);
	}
	
	public List<Person> getPersons() {
		Set<Integer> pIntegers = linkageNewCtQuery.baseCohort.getMemberIds();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Person.class);
		
		criteria.setCacheable(false);
		criteria.add(Restrictions.eq("voided", false));
		criteria.add(Restrictions.in("personId", pIntegers));
		criteria.addOrder(Order.desc("birthdate"));
		return criteria.list();
		
	}
	
	/**
	 * @param baseEncounters
	 * @param concept
	 * @param cohort
	 * @return
	 */
	@Override
	public HashMap<Integer, Object> getObsValueDate(List<Integer> baseEncounters, String concept, Cohort cohort) {
		StringBuilder stringQuery = baseValueDateQuery(concept);
		stringQuery.append(" and ");
		stringQuery.append(VALUE_DATE_BASE_ALIAS_OBS).append("encounter_id in (:encounter) ");
		stringQuery.append(" and ");
		stringQuery.append(VALUE_DATE_BASE_ALIAS_OBS).append("person_id in (:personIds) ");
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringQuery.toString());
		query.setParameterList("encounter", baseEncounters);
		query.setParameterList("personIds", cohort.getMemberIds());
		return getDictionary(query);
	}
	
	public HashMap<Integer, Object> getConceptValue(String conceptUUID, List<Integer> encounter, Cohort cohort) {
		StringBuilder stringQuery = baseConceptQuery(conceptUUID);
		stringQuery.append(" and ");
		stringQuery.append(CONCEPT_BASE_ALIAS_OBS).append("encounter_id in (:encounter) ");
		stringQuery.append(" and ");
		stringQuery.append(CONCEPT_BASE_ALIAS_OBS).append("person_id in (:personIds) ");
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringQuery.toString());
		query.setParameterList("encounter", encounter);
		query.setParameterList("personIds", cohort.getMemberIds());
		return getDictionary(query);
		
	}
	
	protected StringBuilder baseConceptQuery(String conceptQuestionUUid) {
		
		StringBuilder sql = new StringBuilder();
		sql.append("select ").append(CONCEPT_BASE_ALIAS_OBS).append("person_id,cn.name from obs as  obc");
		sql.append(" inner join patient as pa on pa.patient_id = ").append(CONCEPT_BASE_ALIAS_OBS).append("person_id ");
		sql.append(" inner join person as p on pa.patient_id = p.person_id ");
		sql.append(" inner join concept as c on c.concept_id = ").append(CONCEPT_BASE_ALIAS_OBS).append(" concept_id  ");
		sql.append(" inner join concept_name as cn on cn.concept_id = ").append(CONCEPT_BASE_ALIAS_OBS)
		        .append(" value_coded and cn.locale_preferred =1 and cn.locale='en' ");
		sql.append(" inner join encounter as e on e.encounter_id = ").append(CONCEPT_BASE_ALIAS_OBS).append("encounter_id ");
		sql.append(" inner join encounter_type as et on et.encounter_type_id = e.encounter_type ");
		sql.append(" and et.uuid= '" + POSITIVE_TRACKING + "' ");
		sql.append(" where obc.concept_id =" + conceptQuery(conceptQuestionUUid));
		return sql;
	}
	
	public HashMap<Integer, Object> getDate(Cohort cohort, String conceptUUId) {
		StringBuilder stringQuery = super.baseValueDateQuery(conceptUUId);
		stringQuery.append(" and ");
		stringQuery.append(VALUE_DATE_BASE_ALIAS_OBS).append("person_id in (:personIds) ");
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringQuery.toString());
		query.setParameterList("personIds", cohort.getMemberIds());
		return getDictionary(query);
	}
	
	@Override
	protected StringBuilder baseValueDateQuery(String conceptQuestionUUid) {
		
		StringBuilder sql = new StringBuilder();
		sql.append("select " + VALUE_DATE_BASE_ALIAS_OBS + "person_id," + VALUE_DATE_BASE_ALIAS_OBS
		        + "value_datetime from obs as  obvd ");
		sql.append(" inner join patient as pa on pa.patient_id = ").append(VALUE_DATE_BASE_ALIAS_OBS).append("person_id ");
		sql.append(" inner join person as p on pa.patient_id = p.person_id ");
		sql.append(" inner join concept as c on c.concept_id = ").append(VALUE_DATE_BASE_ALIAS_OBS)
		        .append(" concept_id and c.retired = false ");
		sql.append(" and c.uuid= '" + conceptQuestionUUid + "' ");
		sql.append(" inner join encounter as e on e.encounter_id = " + VALUE_DATE_BASE_ALIAS_OBS + "encounter_id ");
		sql.append(" inner join encounter_type as et on et.encounter_type_id = e.encounter_type ");
		sql.append(" and et.uuid= '" + POSITIVE_TRACKING + "' ");
		sql.append(" where pa.voided = false and " + VALUE_DATE_BASE_ALIAS_OBS + "voided = false ");
		return sql;
	}
}
