package org.openmrs.module.ohrireports.datasetevaluator.linelist.cervicalCancer;

import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.query.ObsElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CervicalCancerLIneListQuery extends ObsElement {
	
	private final DbSessionFactory sessionFactory;
	
	@Autowired
	public CervicalCancerLIneListQuery(DbSessionFactory sessionFactory) {
		super(sessionFactory);
		this.sessionFactory = sessionFactory;
	}
}
