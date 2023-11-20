package org.openmrs.module.ohrireports.api.impl.query;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.TREATMENT_END_DATE;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Query;
import org.openmrs.api.db.hibernate.DbSessionFactory;
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
	
	public HashMap<Integer, Object> getTreatmentEndDates(Date endDate, List<Integer> lateEncounterIds) {
		
		StringBuilder sql = baseValueDateQuery(TREATMENT_END_DATE);
		sql.append(" and " + VALUE_DATE_BASE_ALIAS_OBS + "encounter_id in (:lateEncounterIds)");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("lateEncounterIds", lateEncounterIds);
		return getDictionary(query);
		
	}
	
}
