package org.openmrs.module.ohrireports.api.impl.query;

import org.hibernate.Query;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.ohrireports.api.query.GlobalPropertyService;
import org.springframework.beans.factory.annotation.Autowired;

public class GlobalPropertyServiceImp extends BaseOpenmrsService implements GlobalPropertyService {
	
	@Autowired
	private DbSessionFactory sessionFactory;
	
	public DbSessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public void setSessionFactory(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public String getGlobalProperty(String propertyName) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(
		    "select property_value from global_property where property=:propertyName");
		query.setParameter("propertyName", propertyName);
		return (String) query.uniqueResult();
	}
}
