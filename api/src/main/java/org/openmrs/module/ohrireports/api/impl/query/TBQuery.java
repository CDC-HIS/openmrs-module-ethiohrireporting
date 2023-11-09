package org.openmrs.module.ohrireports.api.impl.query;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.TB_TREATMENT_START_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TPT_START_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TB_SCREENING_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.NEGATIVE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.POSITIVE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TB_SCREENING_RESULT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.SPECIMEN_SENT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.YES;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TB_DIAGNOSTIC_TEST_RESULT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.DIAGNOSTIC_TEST;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.SMEAR_ONLY;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.LF_LAM_RESULT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.GENE_XPERT_RESULT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.ADDITIONAL_TEST_OTHERTHAN_GENE_XPERT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TBQuery extends PatientQueryImpDao {

	private DbSessionFactory sessionFactory;
	private List<Integer> screenedOnDateEncounters = new ArrayList<>();

	public List<Integer> getScreenedOnDateEncounters() {
		return screenedOnDateEncounters;
	}

	@Autowired
	public TBQuery(DbSessionFactory _SessionFactory) {

		sessionFactory = _SessionFactory;
		setSessionFactory(sessionFactory);

	}

	private void setEncountersByScreenDate(Date endDate) {
		if (screenedOnDateEncounters == null || screenedOnDateEncounters.isEmpty()) {
			screenedOnDateEncounters = getBaseEncounters(TB_SCREENING_DATE, null, endDate);
		}
	}

	public Cohort getCohortByTbScreenedNegative(Cohort cohort, Date startDate, Date endDate, String gender) {
		init(endDate);
		setEncountersByScreenDate(endDate);
		Query query = getTBScreenedByResult(cohort, startDate, endDate, gender, NEGATIVE);

		return new Cohort(query.list());
	}

	public Cohort getCohortByTbScreenedPositive(Cohort cohort, Date startDate, Date endDate, String gender) {
		init(endDate);
		setEncountersByScreenDate(endDate);

		Query query = getTBScreenedByResult(cohort, startDate, endDate, gender, POSITIVE);

		return new Cohort(query.list());
	}

	private Query getTBScreenedByResult(Cohort cohort, Date startDate, Date endDate, String gender,
			String resultConcept) {
		StringBuilder sql = baseQuery(TB_SCREENING_RESULT);
		sql.append(" and " + OBS_ALIAS + "value_coded = " + conceptQuery(resultConcept));

		if (!Objects.isNull(gender) && !gender.isEmpty())
			sql.append(" and p.gender = '" + gender + "' ");

		sql.append(" and " + OBS_ALIAS + "encounter_id in (:encounters)");
		sql.append(" and  " + OBS_ALIAS + "person_id in (:cohorts)");

		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());

		query.setParameterList("encounters", screenedOnDateEncounters);
		query.setParameterList("cohorts", cohort.getMemberIds());
		return query;
	}

	public Cohort getSpecimenSent(Cohort cohort, Date startDate, Date endDate) {
		Query query = getByResultTypeQuery(cohort, startDate, endDate, SPECIMEN_SENT, YES);
		return new Cohort(query.list());
	}

	public Cohort getSmearOnly(Cohort cohort, Date startDate, Date endDate) {
		Query query = getByResultTypeQuery(cohort, startDate, endDate, DIAGNOSTIC_TEST, SMEAR_ONLY);
		return new Cohort(query.list());

	}

	public Cohort getLFMResult(Cohort cohort, Date startDate, Date endDate) {
		Query query = getByResultTypeQuery(cohort, startDate, endDate, DIAGNOSTIC_TEST,
				Arrays.asList(LF_LAM_RESULT, GENE_XPERT_RESULT));
		return new Cohort(query.list());

	}

	public Cohort getOtherThanLFMResult(Cohort cohort, Date startDate, Date endDate) {
		Query query = getByResultTypeQuery(cohort, startDate, endDate, DIAGNOSTIC_TEST,
				ADDITIONAL_TEST_OTHERTHAN_GENE_XPERT);
		return new Cohort(query.list());

	}

	public Cohort getTBDiagnosticPositiveResult(Cohort cohort, Date startDate, Date endDate) {
		Query query = getByResultTypeQuery(cohort, startDate, endDate, TB_DIAGNOSTIC_TEST_RESULT, POSITIVE);

		return new Cohort(query.list());

	}

	private Query getByResultTypeQuery(Cohort cohort, Date startDate, Date endDate, String ConceptQuestionUUId,
			String answerUUId) {
		StringBuilder sqBuilder = basePersonIdQuery(ConceptQuestionUUId, answerUUId);
		sqBuilder.append(" and " + PERSON_BASE_ALIAS_OBS + "encounter_id in (:encounters)");
		sqBuilder.append(" and  " + PERSON_BASE_ALIAS_OBS + "person_id in (:cohorts)");

		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqBuilder.toString());

		query.setParameterList("encounters", screenedOnDateEncounters);
		query.setParameterList("cohorts", cohort.getMemberIds());

		return query;
	}

	private Query getByResultTypeQuery(Cohort cohort, Date startDate, Date endDate, String ConceptQuestionUUId,
			List<String> answerUUId) {
		StringBuilder sqBuilder = basePersonIdQuery(ConceptQuestionUUId, answerUUId);
		sqBuilder.append(" and " + PERSON_BASE_ALIAS_OBS + "encounter_id in (:encounters)");
		sqBuilder.append(" and  " + PERSON_BASE_ALIAS_OBS + "person_id in (:cohorts)");

		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqBuilder.toString());

		query.setParameterList("encounters", screenedOnDateEncounters);
		query.setParameterList("cohorts", cohort.getMemberIds());

		return query;
	}

	public Cohort getTBScreenedCohort(Cohort cohort, Date starDate, Date endDate) {
		StringBuilder sql = baseQuery(TB_SCREENING_DATE);
		sql.append(" and " + OBS_ALIAS + "encounter_id in (:encounters)");
		sql.append(" and  " + OBS_ALIAS + "person_id in (:cohorts)");

		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());

		query.setParameterList("encounters", screenedOnDateEncounters);
		query.setParameterList("cohorts", cohort.getMemberIds());

		return new Cohort(query.list());
	}

	public Cohort getTBTreatmentStartedCohort(Cohort cohort, Date starDate, Date endDate, String gender) {
		List<Integer> treatmentStatedDateEncounters = getBaseEncounters(TB_TREATMENT_START_DATE, starDate, endDate);

		StringBuilder sql = baseQuery(TB_TREATMENT_START_DATE);
		sql.append(" and " + OBS_ALIAS + " encounter_id in (:encounters)");
		sql.append(" and " + OBS_ALIAS + " person_id in (:cohorts)");

		if (!Objects.isNull(gender) && !gender.isEmpty()) {
			sql.append(" and p.gender = '" + gender + "'");
		}

		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());

		query.setParameterList("encounters", treatmentStatedDateEncounters);
		query.setParameterList("cohorts", cohort.getMemberIds());

		return new Cohort(query.list());
	}

	public Cohort getTPTStartedCohort(Cohort cohort, List<Integer> treatmentStatedDateEncounters, String gender) {

		StringBuilder sql = baseQuery(TPT_START_DATE);
		sql.append(" and " + OBS_ALIAS + " encounter_id in (:encounters)");
		if (cohort != null && !cohort.isEmpty())
			sql.append(" and " + OBS_ALIAS + " person_id in (:cohorts)");

		if (!Objects.isNull(gender) && !gender.isEmpty()) {
			sql.append(" and p.gender = '" + gender + "'");
		}

		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());

		query.setParameterList("encounters", treatmentStatedDateEncounters);
		if (cohort != null && !cohort.isEmpty())
			query.setParameterList("cohorts", cohort.getMemberIds());

		return new Cohort(query.list());
	}

	public Cohort getTPTCohort(List<Integer> treatmentStatedDateEncounters, String concept,
			Date starDate, Date endDate) {

		StringBuilder sql = baseQuery(concept);
		sql.append(" and " + OBS_ALIAS + " encounter_id in (:encounters)");
		
		if (starDate != null)
			sql.append(" and " + OBS_ALIAS + "value_datetime >= :start");
		if (endDate != null)
			sql.append(" and " + OBS_ALIAS + "value_datetime <= :end");

		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());

		if (starDate != null)
			query.setDate("start", starDate);
		if (endDate != null)
			query.setDate("end", endDate);

		query.setParameterList("encounters", treatmentStatedDateEncounters);

		return new Cohort(query.list());
	}
}
