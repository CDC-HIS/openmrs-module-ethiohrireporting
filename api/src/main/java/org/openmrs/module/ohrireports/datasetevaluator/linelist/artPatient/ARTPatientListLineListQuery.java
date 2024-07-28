package org.openmrs.module.ohrireports.datasetevaluator.linelist.artPatient;

import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.query.ObsElement;
import org.springframework.stereotype.Component;

@Component
public class ARTPatientListLineListQuery extends ObsElement {
	
	private final DbSessionFactory sessionFactory;
	
	public ARTPatientListLineListQuery(DbSessionFactory sessionFactory) {
		
		super(sessionFactory);
		this.sessionFactory = sessionFactory;
	}
}
