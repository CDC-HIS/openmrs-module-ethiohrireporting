package org.openmrs.module.ohrireports.datasetevaluator.linelist.dsd;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.query.ObsElement;
import org.openmrs.module.ohrireports.api.impl.query.DSDQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class DSDLineListQuery extends ObsElement {
	
	private DbSessionFactory sessionFactory;
	
	@Autowired
	private DSDQuery dsdQuery;
	
	/**
	 * @param _SessionFactory
	 */
	public DSDLineListQuery(DbSessionFactory _SessionFactory) {
		super(_SessionFactory);
		sessionFactory = _SessionFactory;
	}
	
	public void generateReport(Date start, Date end) {
		dsdQuery.generateBaseReport(start, end);
	}
	
	public List<Integer> getBaseEncounter() {
		return dsdQuery.getBaseEncounter();
	}
	
	public Cohort getBaseCohort() {
		return dsdQuery.getBaseCohort();
	}
	
	public List<Person> getPersons(Cohort cohort) {
		return dsdQuery.getPersonList(cohort);
	}
}
