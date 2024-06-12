package org.openmrs.module.ohrireports.api.impl.query;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.DSD_ASSESSMENT_DATE;
import static org.openmrs.module.ohrireports.RegimentConstant.DSD_CATEGORY;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class DSDQuery extends PatientQueryImpDao {

    private DbSessionFactory sessionFactory;
    @Autowired
    EncounterQuery encounterQuery;

    @Autowired
    public DSDQuery(DbSessionFactory _sessionFactory) {
        setSessionFactory(_sessionFactory);
        sessionFactory = _sessionFactory;
    }

    public List<Integer> getBaseEncounter() {
        return baseEncounter;
    }

    public Cohort getBaseCohort() {
        return baseCohort;
    }

    private List<Integer> baseEncounter = new ArrayList<>();
    private List<Integer> latestDSDAssessmentEncounter = new ArrayList<>();
    private List<Integer> initialDSDAssessmentEncounter = new ArrayList<>();

    public List<Integer> getLatestEncounter() {
        return latestEncounter;
    }

    public List<Integer> getLatestDSDAssessmentEncounter() {
        return latestDSDAssessmentEncounter;
    }

    public List<Integer> getInitialDSDAssessmentEncounter() {
        return initialDSDAssessmentEncounter;
    }

    private List<Integer> latestEncounter = new ArrayList<>();
    private Cohort baseCohort = new Cohort();

    public void generateBaseReport(Date start, Date end) {
        baseEncounter = encounterQuery.getAliveFollowUpEncounters(null, end);
        latestDSDAssessmentEncounter = encounterQuery.getEncounters(Collections.singletonList(DSD_ASSESSMENT_DATE), null, end, baseEncounter);
        initialDSDAssessmentEncounter = encounterQuery.getFirstEncounterByObsDate(null, null, DSD_ASSESSMENT_DATE);
        baseCohort = getActiveOnArtCohort("", null, end, null, initialDSDAssessmentEncounter);
        latestEncounter = encounterQuery.getLatestDateByFollowUpDate(null, null);
    }

    public Cohort getCohortByDSDCategories(String dsdCategoriesUUI) {
        String sqlBuilder = "SELECT ob.person_id FROM obs as ob WHERE " + " ob.concept_id =" + conceptQuery(DSD_CATEGORY) +
                " and ob.value_coded=" + conceptQuery(dsdCategoriesUUI) +
                " and ob.person_id in (:cohorts) " +
                " and ob.encounter_id in (:encounters) ";

        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder);

        query.setParameterList("cohorts", baseCohort.getMemberIds());
        query.setParameterList("encounters", baseEncounter);
        return new Cohort(query.list());
    }

    public List<Person> getPersonList(Cohort cohort) {
        return getPersons(cohort);
    }
}
