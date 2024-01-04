package org.openmrs.module.ohrireports.datasetevaluator.linelist.rtt;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.query.BaseLineListQuery;
import org.openmrs.module.ohrireports.api.impl.query.MLQuery;
import org.openmrs.module.ohrireports.api.impl.query.RTTQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class RTTLineListQuery extends BaseLineListQuery {
	
	private DbSessionFactory sessionFactory;
	
	@Autowired
	private RTTQuery rttQuery;
	
	/**
	 * @param _SessionFactory
	 */
	public RTTLineListQuery(DbSessionFactory _SessionFactory) {
		super(_SessionFactory);
		sessionFactory = _SessionFactory;
	}
	
	public Cohort getRTTCohort(Date start, Date end) {
		return rttQuery.getRttCohort(start, end);
	}
	
	public List<Person> getPerson(Cohort cohort) {
		return rttQuery.getPersons(cohort);
	}
	
	public List<Integer> getBaseEncounter() {
		return rttQuery.getBaseEncounter();
	}
}
