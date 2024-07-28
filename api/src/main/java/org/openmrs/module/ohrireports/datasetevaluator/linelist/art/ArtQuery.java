package org.openmrs.module.ohrireports.datasetevaluator.linelist.art;

import static org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions.TREATMENT_END_DATE;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.query.ObsElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ArtQuery extends ObsElement {
	
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
	
	public HashMap<Integer, Object> getConceptName(List<Integer> encounters, Cohort cohort, String conceptsUUid) {
		return getDictionary(getObs(encounters, conceptsUUid, cohort));
	}
	
}
