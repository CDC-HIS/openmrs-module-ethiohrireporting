package org.openmrs.module.ohrireports.datasetevaluator.hmis.cxca_scrn;

import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CXCAScreeningHmisQuery extends PatientQueryImpDao {
	
	private DbSessionFactory sessionFactory;
	
	private Cohort baseCohort;
	
	List<Integer> encounters;
	
	public Cohort getBaseCohort() {
		return baseCohort;
	}
	
	@Autowired
	public CXCAScreeningHmisQuery(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		setSessionFactory(sessionFactory);
	}
	
	//public int getVIACount
}
