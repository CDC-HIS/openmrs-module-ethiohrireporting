package org.openmrs.module.ohrireports.datasetevaluator.linelist.pmtct.ART;

import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.query.ObsElement;
import org.openmrs.module.ohrireports.api.impl.query.pmtct.ARTQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PMTCTARTLineListQuery extends ObsElement {
	
	private DbSessionFactory sessionFactory;
	
	@Autowired
	private ARTQuery artQuery;
	
	public PMTCTARTLineListQuery(DbSessionFactory _SessionFactory) {
		super(_SessionFactory);
		sessionFactory = _SessionFactory;
	}
}
