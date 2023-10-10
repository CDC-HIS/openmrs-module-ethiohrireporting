package org.openmrs.module.ohrireports.datasetevaluator.linelist;

import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.BaseEthiOhriQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TBQuery extends BaseEthiOhriQuery {
	
	@Autowired
	private DbSessionFactory sessionFactory;
    
}
