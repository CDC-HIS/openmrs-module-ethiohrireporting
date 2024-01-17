package org.openmrs.module.ohrireports.datasetevaluator.linelist.pep;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.query.BaseLineListQuery;
import org.openmrs.module.ohrireports.api.impl.query.PepQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.PEP_VISIT_PERIOD;

@Component
public class PEPQueryLineList extends BaseLineListQuery {
	
	@Autowired
	private PepQuery pepQuery;
	
	private DbSessionFactory sessionFactory;
	
	@Autowired
	public PEPQueryLineList(DbSessionFactory _SessionFactory) {
		super(_SessionFactory);
		sessionFactory = _SessionFactory;
	}
	
	public List<Integer> getEncounterBaseOnPeriod(String conceptUUId){
		StringBuilder sqlBuilder = new StringBuilder("select distinct ob.encounter_id from obs as ob where ob.encounter_id in (:encounter) and ob.concept_id =")
				                           .append(conceptQuery(PEP_VISIT_PERIOD));
		sqlBuilder.append(" and ob.value_coded =").append(conceptQuery(conceptUUId));
		sqlBuilder.append(" and ob.person_id in (:personIds) ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
		query.setParameterList("encounter",getPepFollowUpEncounter());
		query.setParameterList("personIds",getBaseCohort().getMemberIds());
		
		List list = query.list();
		if(Objects.nonNull(list)){
			return (List<Integer>) list;
		}
		return new ArrayList<>();
	}
	
	public HashMap<Integer, Object> getConceptNumber(List<Integer> encounters, String conceptUUId) {
		return getDictionary(super.getObsNumber(encounters, conceptUUId, getBaseCohort()));
	}
	
	public void generateReport(Date start, Date end) {
		pepQuery.generateReport(start, end);
	}
	
	public List<Integer> getBaseEncounter() {
		return pepQuery.getBaseEncounter();
	}
	
	public List<Integer> getPepFollowUpEncounter() {
		return pepQuery.getPepFollowUpEncounter();
	}
	
	public Cohort getBaseCohort() {
		return pepQuery.getBaseCohort();
	}
	
	public List<Person> getPerson() {
		return pepQuery.getPersons(getBaseCohort());
	}
	
}
