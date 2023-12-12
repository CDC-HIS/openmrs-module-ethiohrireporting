package org.openmrs.module.ohrireports.api.impl.query;

import org.hibernate.Query;
import org.jetbrains.annotations.NotNull;
import org.openmrs.Cohort;
import org.openmrs.CohortMembership;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Component
public class TBARTQuery extends PatientQueryImpDao {

    private DbSessionFactory sessionFactory;
    private List<Integer> baseEncounter = new ArrayList<>();

    public List<Integer> getTbTreatmentStartDateEncounter() {
        return tbTreatmentStartDateEncounter;
    }

    private List<Integer> tbTreatmentStartDateEncounter = new ArrayList<>();
    @Autowired
    private EncounterQuery encounterQuery;

    public List<Integer> getBaseEncounter() {
        return baseEncounter;
    }

    @Autowired
    public TBARTQuery(DbSessionFactory _SessionFactory) {

        sessionFactory = _SessionFactory;
        setSessionFactory(sessionFactory);

    }

    public Cohort getCohortByTBTreatmentStartDate(Date start, Date end) {
        //Retrieve latest followup according end date
        List<Integer> latestFollowUp = encounterQuery.getAliveFollowUpEncounters(null, end);
        baseEncounter = latestFollowUp;
        // Check and fetch patient that they are on ART from the latest follow up
        Cohort cohort = getActiveOnArtCohort("", null, end, null, latestFollowUp);
        // Retrieve all patient from active on art of started TB treatment
        cohort = getCurrentOnActiveTB(cohort, start, end);
        return cohort;
    }

    public Cohort getAlreadyOnArtCohort(Cohort cohort, Date beforeDate) {
        StringBuilder sqlBuilder = getQuery();

        sqlBuilder.append("  and value_datetime < :start");

        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());

        query.setParameterList("encounterIds", baseEncounter);
        query.setParameterList("personId", cohort.getMemberIds());
        query.setDate("start", beforeDate);

        return new Cohort(query.list());
    }

    public Cohort getNewOnArtCohort(Cohort cohort, Date onOrAfter, Date beforeOrOn) {
        StringBuilder sqlBuilder = getQuery();

        sqlBuilder.append(" and value_datetime >= :start");
        sqlBuilder.append(" and value_datetime <= :end");

        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());

        query.setParameterList("encounterIds", baseEncounter);
        query.setParameterList("personId", cohort.getMemberIds());
        query.setDate("start", onOrAfter);
        query.setDate("end", beforeOrOn);

        return new Cohort(query.list());
    }

    @NotNull
    private StringBuilder getQuery() {
        StringBuilder sqlBuilder = new StringBuilder("select distinct (person_id) from obs ");
        sqlBuilder.append(" where encounter_id in (:encounterIds) and person_id in (:personId)");
        sqlBuilder.append(" and concept_id=").append(conceptQuery(ART_START_DATE));
        return sqlBuilder;
    }

    private Cohort getCurrentOnActiveTB(Cohort cohort, Date start, Date end) {


        List<Integer> treatmentStartedEncounter = encounterQuery.getEncounters(Arrays.asList(TB_TREATMENT_START_DATE), start, end, cohort);
        List<Integer> activeDiagnosticStartDateEncounter = encounterQuery.getEncounters(Arrays.asList(TB_ACTIVE_DATE), start, end, cohort);

        Cohort treatmentStartedCohort = getCohort(treatmentStartedEncounter);
        Cohort activeDiagonosticCohort = getCohort(activeDiagnosticStartDateEncounter);

        for (CohortMembership treatmentMembership : treatmentStartedCohort.getMemberships()) {
            if (!activeDiagonosticCohort.contains(treatmentMembership.getPatientId())) {
                activeDiagonosticCohort.addMembership(treatmentMembership);
            }
        }
        return activeDiagonosticCohort;
    }
}
