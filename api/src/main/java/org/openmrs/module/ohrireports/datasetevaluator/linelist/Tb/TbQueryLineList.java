package org.openmrs.module.ohrireports.datasetevaluator.linelist.Tb;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.TB_SCREENING_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TB_SCREENING_RESULT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TB_TREATMENT_START_DATE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.query.BaseLineListQuery;
import org.springframework.stereotype.Component;

@Component
public class TbQueryLineList extends BaseLineListQuery {
	private DbSessionFactory sessionFactory;

	public List<Integer> getScreenedOnDateEncounters() {
		return screenedOnDateEncounters;
	}

	List<Integer> screenedOnDateEncounters = new ArrayList<>();

	public TbQueryLineList(DbSessionFactory _SessionFactory) {
		super(_SessionFactory);
		sessionFactory = _SessionFactory;
	}

	public void setEncountersByScreenDate(List<Integer> encounters) {
		screenedOnDateEncounters = encounters;
	}

	public HashMap<Integer, Object> getTBScreenedDate(Cohort cohort) {

		StringBuilder sql = baseValueDateQuery(TB_SCREENING_DATE);
		sql.append(" and ").append(VALUE_DATE_BASE_ALIAS_OBS).append("encounter_id in (:encounters)");
		sql.append(" and  ").append(VALUE_DATE_BASE_ALIAS_OBS).append("person_id in (:cohorts)");

		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());

		query.setParameterList("encounters", screenedOnDateEncounters);
		query.setParameterList("cohorts", cohort.getMemberIds());

		return getDictionary(query);

	}

	public HashMap<Integer, Object> getTBTreatmentStartDate(Cohort cohort,
			List<Integer> treatmentStatedDateEncounters) {
		StringBuilder sql = baseValueDateQuery(TB_TREATMENT_START_DATE);
		sql.append(" and ").append(VALUE_DATE_BASE_ALIAS_OBS).append(" encounter_id in (:encounters)");
		sql.append(" and ").append(VALUE_DATE_BASE_ALIAS_OBS).append(" person_id in (:cohorts)");

		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());

		query.setParameterList("encounters", treatmentStatedDateEncounters);
		query.setParameterList("cohorts", cohort.getMemberIds());

		return getDictionary(query);

	}

	public HashMap<Integer, Object> getTBScreenedResult(Cohort cohort) {

		StringBuilder sql = baseConceptQuery(TB_SCREENING_RESULT);
		sql.append(" and " + CONCEPT_BASE_ALIAS_OBS + "encounter_id in (:encounters)");
		sql.append(" and  " + CONCEPT_BASE_ALIAS_OBS + "person_id in (:cohorts)");

		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());

		query.setParameterList("encounters", screenedOnDateEncounters);
		query.setParameterList("cohorts", cohort.getMemberIds());

		return getDictionary(query);

	}



	

	public HashMap<Integer, Object> getByResultTypeQuery(Cohort cohort, String ConceptQuestionUUId) {
		StringBuilder sqBuilder = baseConceptQuery(ConceptQuestionUUId);
		sqBuilder.append(" and " + CONCEPT_BASE_ALIAS_OBS + "encounter_id in (:encounters)");
		sqBuilder.append(" and  " + CONCEPT_BASE_ALIAS_OBS + "person_id in (:cohorts)");

		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqBuilder.toString());

		query.setParameterList("encounters", screenedOnDateEncounters);
		query.setParameterList("cohorts", cohort.getMemberIds());
		return getDictionary(query);
	}

}
