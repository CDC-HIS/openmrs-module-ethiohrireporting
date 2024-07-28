package org.openmrs.module.ohrireports.datasetevaluator.linelist.ml;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.query.ObsElement;
import org.openmrs.module.ohrireports.api.impl.query.MLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class MLQueryLineList extends ObsElement {
	
	private DbSessionFactory sessionFactory;
	
	@Autowired
	private MLQuery mlQuery;
	
	/**
	 * @param _SessionFactory
	 */
	public MLQueryLineList(DbSessionFactory _SessionFactory) {
		super(_SessionFactory);
		sessionFactory = _SessionFactory;
	}
	
	public Cohort getMLQuery(Date start, Date end) {
		return mlQuery.getCohortML(start, end);
	}
	
	public List<Person> getPerson(Cohort cohort) {
		return mlQuery.getPersons(cohort);
	}
	
	public List<Integer> getBaseEncounter() {
		return mlQuery.getBaseEncounter();
	}
}
