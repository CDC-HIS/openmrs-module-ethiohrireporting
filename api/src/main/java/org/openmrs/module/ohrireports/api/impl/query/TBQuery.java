package org.openmrs.module.ohrireports.api.impl.query;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.TB_TREATMENT_START_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TPT_COMPLETED_DATE;
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
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HTS_FOLLOW_UP_ENCOUNTER_TYPE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.ADDITIONAL_TEST_OTHERTHAN_GENE_XPERT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TB_ACTIVE_DATE;

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

	public void setEncountersByScreenDate(List<Integer> encounters) {
		screenedOnDateEncounters = encounters;
	}

	public Cohort getCohortByTbScreenedNegative(Cohort cohort,String gender) {
		
		Query query = getTBScreenedByResult(cohort,  gender, NEGATIVE);

		return new Cohort(query.list());
	}

	public Cohort getCohortByTbScreenedPositive(Cohort cohort,  String gender) {
		

		Query query = getTBScreenedByResult(cohort, gender, POSITIVE);

		return new Cohort(query.list());
	}

	private Query getTBScreenedByResult(Cohort cohort, String gender,
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

	public Cohort getSpecimenSent(Cohort cohort) {
		Query query = getByResultTypeQuery(cohort, SPECIMEN_SENT, YES);
		return new Cohort(query.list());
	}

	public Cohort getSmearOnly(Cohort cohort) {
		Query query = getByResultTypeQuery(cohort, DIAGNOSTIC_TEST, SMEAR_ONLY);
		return new Cohort(query.list());

	}

	public Cohort getLFMResult(Cohort cohort, Date startDate, Date endDate) {
		Query query = getByResultTypeQuery(cohort, startDate, endDate, DIAGNOSTIC_TEST,
				Arrays.asList(LF_LAM_RESULT, GENE_XPERT_RESULT));
		return new Cohort(query.list());

	}

	public Cohort getOtherThanLFMResult(Cohort cohort) {
		Query query = getByResultTypeQuery(cohort, DIAGNOSTIC_TEST,
				ADDITIONAL_TEST_OTHERTHAN_GENE_XPERT);
		return new Cohort(query.list());

	}

	public Cohort getTBDiagnosticPositiveResult(Cohort cohort) {
		Query query = getByResultTypeQuery(cohort, TB_DIAGNOSTIC_TEST_RESULT, POSITIVE);

		return new Cohort(query.list());

	}

	private Query getByResultTypeQuery(Cohort cohort,String ConceptQuestionUUId,
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

	public Cohort getTBScreenedCohort(Cohort cohort, Date startDate, Date endDate) {
		StringBuilder sql = baseQuery(TB_SCREENING_DATE);
		sql.append(" and " + OBS_ALIAS + "encounter_id in (:encounters)");
		sql.append(" and  " + OBS_ALIAS + "person_id in (:cohorts)");

		sql.append(" and ").append(OBS_ALIAS).append("value_datetime >= :start");
		sql.append(" and ").append(OBS_ALIAS).append("value_datetime <= :end");
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());

		query.setParameterList("encounters", screenedOnDateEncounters);
		query.setParameterList("cohorts", cohort.getMemberIds());

		query.setDate("start",startDate);
		query.setDate("end",endDate);
		return new Cohort(query.list());
	}

	public Cohort getTBTreatmentStartedCohort(Cohort cohort, String gender,List<Integer> treatmentStatedDateEncounters) {

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

	public Cohort getTPTByConceptCohort(List<Integer> treatmentStatedDateEncounters, Cohort cohort, String conceptUUIDs,
			List<String> conceptAnswerUUIDS) {

		StringBuilder sql = baseQuery(conceptUUIDs);
		sql.append(" and " + OBS_ALIAS + " encounter_id in (:encounters)");
		sql.append(" and " + OBS_ALIAS + "value_coded in " + conceptQuery(conceptAnswerUUIDS));

		if (cohort != null && !cohort.isEmpty())
			sql.append(" and " + OBS_ALIAS + " person_id in (:cohorts)");

		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());

		query.setParameterList("encounters", treatmentStatedDateEncounters);

		if (cohort != null && !cohort.isEmpty())
			query.setParameterList("cohorts", cohort.getMemberIds());

		return new Cohort(query.list());
	}

	public Cohort getTPTByCompletedConceptCohort(List<Integer> treatmentStatedDateEncounters, Cohort cohort) {

		StringBuilder sql = new StringBuilder();
		sql.append("select distinct " + OBS_ALIAS + "person_id from obs as ob ");
		sql.append("inner join patient as pa on pa.patient_id = " + OBS_ALIAS + "person_id ");
		sql.append("inner join person as p on pa.patient_id = p.person_id ");
		sql.append("inner join concept as c on c.concept_id = " + OBS_ALIAS + "concept_id and c.retired = false ");
		sql.append("and c.uuid= '" + TPT_COMPLETED_DATE + "' ");
		sql.append("inner join encounter as e on e.encounter_id = " + OBS_ALIAS + "encounter_id ");
		sql.append("inner join encounter_type as et on et.encounter_type_id = e.encounter_type ");
		sql.append("left join (select * from obs where concept_id=" + conceptQuery(TPT_START_DATE)
				+ " and encounter_id in (:joinedEncounters) ) as otherOb on otherOb.person_id = ob.person_id ");
		sql.append(" and et.uuid= '" + HTS_FOLLOW_UP_ENCOUNTER_TYPE + "' ");
		sql.append(" where pa.voided = false and " + OBS_ALIAS
				+ "voided = false and otherOb.value_datetime < ob.value_datetime");
		sql.append(" and " + OBS_ALIAS + " encounter_id in (:encounters)");

		if (cohort != null && !cohort.isEmpty())
			sql.append(" and " + OBS_ALIAS + " person_id in (:cohorts)");

		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());

		query.setParameterList("encounters", treatmentStatedDateEncounters);
		query.setParameterList("joinedEncounters", treatmentStatedDateEncounters);

		if (cohort != null && !cohort.isEmpty())
			query.setParameterList("cohorts", cohort.getMemberIds());

		return new Cohort(query.list());
	}

	public Cohort getCohort(List<Integer> encounterIds) {
		StringBuilder sqlBuilder = new StringBuilder(
				"select distinct (person_id) from obs where encounter_id in (:encounterIds) ");

		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
		query.setParameterList("encounterIds", encounterIds);

		return new Cohort(query.list());

	}

	public Cohort getCurrentOnActiveTB(Cohort cohort, Date start, Date end,List<Integer> encounters) {
		List<String> concepts = Arrays.asList(TB_TREATMENT_START_DATE, TB_ACTIVE_DATE);

	//	List<Integer> encounters = getEncounters(cohort, start, end);

		StringBuilder sql = new StringBuilder("select distinct person_id from obs where ");
		sql.append(" concept_id in " + conceptQuery(concepts));
		sql.append(" and person_id in (:cohort)");
		sql.append(" and encounter_id in (:encounters)");
		sql.append(" and value_datetime >= :start");
		sql.append(" and value_datetime <= :end");

		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());

		query.setParameterList("encounters", encounters);
		query.setParameterList("cohort", cohort.getMemberIds());
		query.setDate("start", start);
		query.setDate("end", end);

		return new Cohort(query.list());
	}

	// public List<Integer> getEncounters(Cohort cohort, Date start, Date end) {
	// 	List<String> concepts = Arrays.asList(TB_TREATMENT_START_DATE, TB_ACTIVE_DATE);
	// 	return (concepts, start, end, cohort);
	// }
}
