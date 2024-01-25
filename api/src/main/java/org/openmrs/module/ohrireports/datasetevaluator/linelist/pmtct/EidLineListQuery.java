package org.openmrs.module.ohrireports.datasetevaluator.linelist.pmtct;

import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.dao.PMTCTPatient;
import org.openmrs.module.ohrireports.api.impl.query.BaseLineListQuery;
import org.openmrs.module.ohrireports.api.impl.query.pmtct.EIDQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;

@Component
public class EidLineListQuery extends BaseLineListQuery {
	
	private DbSessionFactory sessionFactory;
	
	@Autowired
	private EIDQuery eidQuery;
	
	@Autowired
	public EidLineListQuery(DbSessionFactory _SessionFactory) {
		super(_SessionFactory);
		sessionFactory = _SessionFactory;
	}
	
	public void generateReport(Date start, Date end) {
		eidQuery.generateReportForLineList(start, end);
	}
	
	public HashMap<Integer, PMTCTPatient> getPMTCTPatient() {
		return eidQuery.getPatientEncounterHashMap();
	}
	
}
