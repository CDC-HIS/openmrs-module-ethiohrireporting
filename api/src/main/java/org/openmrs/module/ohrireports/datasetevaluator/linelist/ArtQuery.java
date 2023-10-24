package org.openmrs.module.ohrireports.datasetevaluator.linelist;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.ART_START_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TREATMENT_END_DATE;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.query.BaseLineListQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ArtQuery extends BaseLineListQuery {
	
	private DbSessionFactory sessionFactory;
	
	@Autowired
	public ArtQuery(DbSessionFactory _SessionFactory) {
		super(_SessionFactory);
		sessionFactory = _SessionFactory;
		
	}
	
	public HashMap<Integer, Object> getTreatmentEndDates(Cohort cohort, Date endDate, List<Integer> lateEncounterIds) {
		
		StringBuilder sql = baseValueDateQuery(TREATMENT_END_DATE);
		sql.append(" and " + VALUE_DATE_BASE_ALIAS_OBS + "person_id in (:patientIds)");
		sql.append(" and " + VALUE_DATE_BASE_ALIAS_OBS + "encounter_id in (:lateEncounterIds)");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("lateEncounterIds", lateEncounterIds);
		query.setParameterList("patientIds", cohort.getMemberIds());
		
		return getDictionary(query);
		
	}
	
}
