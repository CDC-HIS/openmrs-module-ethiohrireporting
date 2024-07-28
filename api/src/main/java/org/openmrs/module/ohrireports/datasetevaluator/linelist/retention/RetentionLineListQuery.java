package org.openmrs.module.ohrireports.datasetevaluator.linelist.retention;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.query.ObsElement;
import org.openmrs.module.ohrireports.api.impl.query.HivArtRetQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class RetentionLineListQuery extends ObsElement {
	
	private DbSessionFactory sessionFactory;
	
	@Autowired
	private HivArtRetQuery hivArtRetQuery;
	
	/**
	 * @param _SessionFactory
	 */
	public RetentionLineListQuery(DbSessionFactory _SessionFactory) {
		super(_SessionFactory);
		sessionFactory = _SessionFactory;
	}
	
	public void generateRetentionReport(Date start, Date end) {
		hivArtRetQuery.initializeRetentionCohort(start, end);
	}
	
	public List<Person> getPerson(Cohort cohort) {
		return hivArtRetQuery.getPersons(cohort);
	}
	
	public List<Integer> getBaseEncounter() {
		return hivArtRetQuery.getNetRetEncounter();
	}
	
	public Cohort getBaseCohort() {
		return Cohort.union(hivArtRetQuery.getRetCohort(), hivArtRetQuery.getNetRetCohort());
	}
}
