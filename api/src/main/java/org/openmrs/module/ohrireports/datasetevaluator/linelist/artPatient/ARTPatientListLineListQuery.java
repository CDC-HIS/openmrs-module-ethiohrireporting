package org.openmrs.module.ohrireports.datasetevaluator.linelist.artPatient;

import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.query.BaseLineListQuery;
import org.springframework.stereotype.Component;

@Component
public class ARTPatientListLineListQuery extends BaseLineListQuery {
	
	private final DbSessionFactory sessionFactory;
	
	public ARTPatientListLineListQuery(DbSessionFactory sessionFactory) {
		
		super(sessionFactory);
		this.sessionFactory = sessionFactory;
	}
}
