package org.openmrs.module.ohrireports.api.impl.query;

import java.util.*;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Component
public class TBQuery extends PatientQueryImpDao {

    private final DbSessionFactory sessionFactory;
    private List<Integer> tbTreatmentEncounter = new ArrayList<>();
    private List<Integer> tbScreeningEncounter = new ArrayList<>();
    private List<Integer> followUpEncounter = new ArrayList<>();
    private Cohort numeratorCohort;
    private Cohort denomiatorCohort;
    public List<Integer> geTBTreatmentEncounter() {
        return tbTreatmentEncounter;
    }
    @Autowired
    private EncounterQuery encounterQuery;

    @Autowired
    public TBQuery(DbSessionFactory _SessionFactory) {

        sessionFactory = _SessionFactory;
        setSessionFactory(sessionFactory);

    }
    
    public void generateDenominatorReport(Date start, Date end) {

        tbScreeningEncounter = encounterQuery.getEncounters(Collections.singletonList(TB_SCREENING_DATE), start,end);
        Cohort cohort = getCohort(tbScreeningEncounter);
        cohort =   new Cohort(getArtStartedCohort(tbScreeningEncounter,null,end,cohort));

        denomiatorCohort = new Cohort(getArtStartedCohort(tbScreeningEncounter, null, end, cohort));
        followUpEncounter = encounterQuery.getEncounters(Collections.singletonList(FOLLOW_UP_DATE),null,end,denomiatorCohort);
        
    }
    public void generateNumeratorReport(Date start, Date end) {
        tbTreatmentEncounter = encounterQuery.getEncounters(
		        Collections.singletonList(TB_TREATMENT_START_DATE), start, end);
        
        Cohort cohort = getCohort(tbTreatmentEncounter);
        
         cohort =   new Cohort(getArtStartedCohort(tbTreatmentEncounter,null,end,cohort));
        
        numeratorCohort = getActiveOnArtCohort("", null,end, cohort, tbTreatmentEncounter);
        followUpEncounter = encounterQuery.getEncounters(Collections.singletonList(FOLLOW_UP_DATE),null,end,numeratorCohort);
    }

    public void setEncountersByScreenDate(List<Integer> encounters) {
        tbTreatmentEncounter = encounters;
    }

    public Cohort getCohortByTbScreenedNegative(Cohort cohort, String gender) {
        Query query = getTBScreenedByResult(cohort, gender, NEGATIVE);
        return new Cohort(query.list());
    }

    public Cohort getCohortByTbScreenedPositive(Cohort cohort, String gender) {
        Query query = getTBScreenedByResult(cohort, gender, POSITIVE);
        return new Cohort(query.list());
    }

    private Query getTBScreenedByResult(Cohort cohort, String gender, String resultConcept) {
        StringBuilder sql = baseQuery(TB_SCREENING_RESULT);
        sql.append(" and " + OBS_ALIAS + "value_coded = " + conceptQuery(resultConcept));

        if (!Objects.isNull(gender) && !gender.isEmpty()) sql.append(" and p.gender = '" + gender + "' ");

        sql.append(" and ").append(OBS_ALIAS).append("encounter_id in (:encounters)");
        sql.append(" and  ").append(OBS_ALIAS).append("person_id in (:cohorts)");

        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());

        query.setParameterList("encounters", tbScreeningEncounter);
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
        Query query = getByResultTypeQuery(cohort, startDate, endDate, DIAGNOSTIC_TEST, Arrays.asList(LF_LAM_RESULT, GENE_XPERT_RESULT));
        return new Cohort(query.list());

    }

    public Cohort getOtherThanLFMResult(Cohort cohort, Date startDate, Date endDate) {
        Query query = getByResultTypeQuery(cohort, startDate, endDate, DIAGNOSTIC_TEST, ADDITIONAL_TEST_OTHERTHAN_GENE_XPERT);
        return new Cohort(query.list());

    }

    public Cohort getTBDiagnosticPositiveResult(Cohort cohort, Date startDate, Date endDate) {
        Query query = getByResultTypeQuery(cohort, startDate, endDate, TB_DIAGNOSTIC_TEST_RESULT, POSITIVE);

        return new Cohort(query.list());

    }

    private Query getByResultTypeQuery(Cohort cohort, Date startDate, Date endDate, String ConceptQuestionUUId, String answerUUId) {
        StringBuilder sqBuilder = basePersonIdQuery(ConceptQuestionUUId, answerUUId);
        sqBuilder.append(" and ").append(PERSON_BASE_ALIAS_OBS).append("encounter_id in (:encounters)");
        sqBuilder.append(" and  ").append(PERSON_BASE_ALIAS_OBS).append("person_id in (:cohorts)");

        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqBuilder.toString());

        query.setParameterList("encounters", tbScreeningEncounter);
        query.setParameterList("cohorts", cohort.getMemberIds());

        return query;
    }

    private Query getByResultTypeQuery(Cohort cohort, Date startDate, Date endDate, String ConceptQuestionUUId, List<String> answerUUId) {
        StringBuilder sqBuilder = basePersonIdQuery(ConceptQuestionUUId, answerUUId);
        sqBuilder.append(" and " + PERSON_BASE_ALIAS_OBS + "encounter_id in (:encounters)");
        sqBuilder.append(" and  " + PERSON_BASE_ALIAS_OBS + "person_id in (:cohorts)");

        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqBuilder.toString());

        query.setParameterList("encounters", tbScreeningEncounter);
        query.setParameterList("cohorts", cohort.getMemberIds());

        return query;
    }

    public Cohort getTBScreenedCohort(Cohort cohort,List<Integer> encounter) {
        StringBuilder sql = baseQuery(TB_SCREENING_DATE);
        sql.append(" and ").append(OBS_ALIAS).append("encounter_id in (:encounters)");
        sql.append(" and  ").append(OBS_ALIAS).append("person_id in (:cohorts)");

        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());

        query.setParameterList("encounters", encounter);
        query.setParameterList("cohorts", cohort.getMemberIds());


        return new Cohort(query.list());
    }

    public Cohort getTBTreatmentStartedCohort(Cohort cohort, String gender, List<Integer> treatmentStatedDateEncounters) {
        //getBaseEncounters(TB_TREATMENT_START_DATE, starDate, endDate);

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
        if (cohort != null && !cohort.isEmpty()) sql.append(" and " + OBS_ALIAS + " person_id in (:cohorts)");

        if (!Objects.isNull(gender) && !gender.isEmpty()) {
            sql.append(" and p.gender = '" + gender + "'");
        }

        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());

        query.setParameterList("encounters", treatmentStatedDateEncounters);
        if (cohort != null && !cohort.isEmpty()) query.setParameterList("cohorts", cohort.getMemberIds());

        return new Cohort(query.list());
    }

    public Cohort getTPTCohort(List<Integer> treatmentStatedDateEncounters, String concept, Date starDate, Date endDate) {

        StringBuilder sql = baseQuery(concept);
        sql.append(" and " + OBS_ALIAS + " encounter_id in (:encounters)");

        if (starDate != null) sql.append(" and " + OBS_ALIAS + "value_datetime >= :start");
        if (endDate != null) sql.append(" and " + OBS_ALIAS + "value_datetime <= :end");

        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());

        if (starDate != null) query.setDate("start", starDate);
        if (endDate != null) query.setDate("end", endDate);

        query.setParameterList("encounters", treatmentStatedDateEncounters);

        return new Cohort(query.list());
    }

    public Cohort getTPTByConceptCohort(List<Integer> treatmentStatedDateEncounters, Cohort cohort, String conceptUUIDs, List<String> conceptAnswerUUIDS) {

        StringBuilder sql = baseQuery(conceptUUIDs);
        sql.append(" and " + OBS_ALIAS + " encounter_id in (:encounters)");
        sql.append(" and " + OBS_ALIAS + "value_coded in " + conceptQuery(conceptAnswerUUIDS));

        if (cohort != null && !cohort.isEmpty()) sql.append(" and " + OBS_ALIAS + " person_id in (:cohorts)");

        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());

        query.setParameterList("encounters", treatmentStatedDateEncounters);

        if (cohort != null && !cohort.isEmpty()) query.setParameterList("cohorts", cohort.getMemberIds());

        return new Cohort(query.list());
    }

    public Cohort getTPTByCompletedConceptCohort(List<Integer> treatmentStatedDateEncounters, Cohort cohort) {

        StringBuilder sql = new StringBuilder();
        sql.append("select distinct ").append(OBS_ALIAS).append("person_id from obs as ob ");
        sql.append("inner join patient as pa on pa.patient_id = " + OBS_ALIAS + "person_id ");
        sql.append("inner join person as p on pa.patient_id = p.person_id ");
        sql.append("inner join concept as c on c.concept_id = " + OBS_ALIAS + "concept_id and c.retired = false ");
        sql.append("and c.uuid= '" + TPT_COMPLETED_DATE + "' ");
        sql.append("inner join encounter as e on e.encounter_id = " + OBS_ALIAS + "encounter_id ");
        sql.append("inner join encounter_type as et on et.encounter_type_id = e.encounter_type ");
        sql.append("left join (select * from obs where concept_id=" + conceptQuery(TPT_START_DATE) + " and encounter_id in (:joinedEncounters) ) as otherOb on otherOb.person_id = ob.person_id ");
        sql.append(" and et.uuid= '" + HTS_FOLLOW_UP_ENCOUNTER_TYPE + "' ");
        sql.append(" where pa.voided = false and " + OBS_ALIAS + "voided = false and otherOb.value_datetime < ob.value_datetime");
        sql.append(" and " + OBS_ALIAS + " encounter_id in (:encounters)");

        if (cohort != null && !cohort.isEmpty()) sql.append(" and " + OBS_ALIAS + " person_id in (:cohorts)");

        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());

        query.setParameterList("encounters", treatmentStatedDateEncounters);
        query.setParameterList("joinedEncounters", treatmentStatedDateEncounters);

        if (cohort != null && !cohort.isEmpty()) query.setParameterList("cohorts", cohort.getMemberIds());

        return new Cohort(query.list());
    }
    
    
    public Cohort getDenomiatorCohort() {
        return denomiatorCohort;
    }
    
    public Cohort getNumeratorCohort() {
        return numeratorCohort;
    }
    
    public List<Integer> getTbScreeningEncounter() {
        return tbScreeningEncounter;
    }
    
    public List<Integer> getFollowUpEncounter() {
        return followUpEncounter;
    }
}
