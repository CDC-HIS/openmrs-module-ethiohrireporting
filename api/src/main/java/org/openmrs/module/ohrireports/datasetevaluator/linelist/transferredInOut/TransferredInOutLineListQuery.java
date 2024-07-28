package org.openmrs.module.ohrireports.datasetevaluator.linelist.transferredInOut;

import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.query.ObsElement;
import org.springframework.stereotype.Component;

@Component
public class TransferredInOutLineListQuery extends ObsElement {
	
	private final DbSessionFactory sessionFactory;
	
	public TransferredInOutLineListQuery(DbSessionFactory sessionFactory) {
		
		super(sessionFactory);
		this.sessionFactory = sessionFactory;
	}
}
