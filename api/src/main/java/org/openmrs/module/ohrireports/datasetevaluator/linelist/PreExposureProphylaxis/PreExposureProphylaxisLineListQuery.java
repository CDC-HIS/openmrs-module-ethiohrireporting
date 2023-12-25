package org.openmrs.module.ohrireports.datasetevaluator.linelist.PreExposureProphylaxis;

import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.query.BaseLineListQuery;
import org.springframework.stereotype.Component;

@Component
public class PreExposureProphylaxisLineListQuery extends BaseLineListQuery {
	
	private final DbSessionFactory sessionFactory;
	
	public PreExposureProphylaxisLineListQuery(DbSessionFactory _SessionFactory, DbSessionFactory sessionFactory) {
		super(_SessionFactory);
		this.sessionFactory = sessionFactory;
	}
}
