package org.openmrs.module.ohrireports.datasetevaluator.hmis.tb_scrn;

import static org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions.TB_SCREENING_DATE;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_1_NAME;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_2_NAME;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.TBQuery;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.Gender;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class TPSCRNEvaluator {
    @Autowired
    private TBQuery tbQuery;
    @Autowired
    private EncounterQuery encounterQuery;
    private Date end;

    List<Person> persons = new ArrayList<>();


    public void buildDataset(Date start, Date end, SimpleDataSet dataSet) {
        this.end = end;

        //List<Integer> encounters = encounterQuery.getAliveFollowUpEncounters(null,end);
        // find list of encounter by TB screened date
        List<Integer> encounters = encounterQuery.getEncounters(Collections.singletonList(TB_SCREENING_DATE), start, end);
        Cohort newOnARTCohort = tbQuery.getNewOnArtCohort("", start, end, null, encounters);
        Cohort existingOnARTCohort = new Cohort(tbQuery.getArtStartedCohort("", null, end,
                null, newOnARTCohort, encounters));

        Cohort newOnARTScreenedCohort = tbQuery.getActiveOnArtCohort("", null, end,
                newOnARTCohort, encounters);
        //getTBScreenedCohort(newOnARTCohort, encounters);
        persons = tbQuery.getPersons(newOnARTScreenedCohort);

        dataSet.addRow(
                buildRow("HIV_TB_SCRN",
                        "Proportion of patients enrolled in HIV care who were screened for TB (FD)", String.class,
                        " "));

        dataSet.addRow(buildRow("HIV_TB_SCRN.1",
                "Number of NEWLY Enrolled ART clients who were screened for TB during the reporting period",
                Integer.class, persons.size()));

        dataSet.addRow(buildRow("HIV_TB_SCRN.1.1", "< 15 years, Male", Integer.class,
                gettbscrnByAgeAndGender(0, 15, Gender.Male)));

        dataSet.addRow(buildRow("HIV_TB_SCRN.1.2", "< 15 years, female", Integer.class,
                gettbscrnByAgeAndGender(0, 15, Gender.Female)));
        dataSet.addRow(buildRow("HIV_TB_SCRN.1.3", ">= 15 years, Male", Integer.class,
                gettbscrnByAgeAndGender(15, 150, Gender.Male)));
        dataSet.addRow(buildRow("HIV_TB_SCRN.1.4", ">= 15 years, female", Integer.class,
                gettbscrnByAgeAndGender(15, 150, Gender.Female)));

        Cohort newOnARTScreenedPositiveCohort = tbQuery.getCohortByTbScreenedPositive(newOnARTCohort, "");

        persons = tbQuery.getPersons(newOnARTScreenedPositiveCohort);

        dataSet.addRow(buildRow("HIV_TB_SCRN_P", "Screened Positive for TB", Integer.class,
                persons.size()));
        dataSet.addRow(buildRow("HIV_TB_SCRN_P.1", "< 15 years, Male", Integer.class,
                gettbscrnByAgeAndGender(0, 15, Gender.Male)));
        dataSet.addRow(buildRow("HIV_TB_SCRN_P.2", "< 15 years, female", Integer.class,
                gettbscrnByAgeAndGender(0, 15, Gender.Female)));
        dataSet.addRow(buildRow("HIV_TB_SCRN_P.3", ">= 15 years, Male", Integer.class,
                gettbscrnByAgeAndGender(15, 150, Gender.Male)));
        dataSet.addRow(buildRow("HIV_TB_SCRN_P.4", ">= 15 years, female", Integer.class,
                gettbscrnByAgeAndGender(15, 150, Gender.Female)));

        Cohort existingScreenedOnArtCohort = tbQuery.getActiveOnArtCohort("", null, end,
                existingOnARTCohort, encounters);
        //getTBScreenedCohort(existingOnARTCohort, encounters);
        persons = tbQuery
                .getPersons(existingScreenedOnArtCohort);

        dataSet.addRow(buildRow("HIV_TB_SCRN_ART", "Number of PLHIVs PREVIOUSLY on ART and screened for TB",
                Integer.class, persons.size()));

        dataSet.addRow(buildRow("HIV_TB_SCRN_ART. 1", "< 15 years, Male", Integer.class,
                gettbscrnByAgeAndGender(0, 15, Gender.Male)));
        dataSet.addRow(buildRow("HIV_TB_SCRN_ART. 2", "< 15 years, female", Integer.class,
                gettbscrnByAgeAndGender(0, 15, Gender.Female)));
        dataSet.addRow(buildRow("HIV_TB_SCRN_ART. 3", ">= 15 years, Male", Integer.class,
                gettbscrnByAgeAndGender(15, 150, Gender.Male)));
        dataSet.addRow(buildRow("HIV_TB_SCRN_ART. 4", ">= 15 years, female", Integer.class,
                gettbscrnByAgeAndGender(15, 150, Gender.Female)));

        Cohort existingScreenedPositiveCohort = tbQuery.getCohortByTbScreenedPositive(existingScreenedOnArtCohort, "");
        persons = tbQuery.getPersons(existingScreenedPositiveCohort);

        dataSet.addRow(buildRow("HIV_TB_SCRN_ART_P", "Screened Positive for TB", Integer.class, persons.size()));
        dataSet.addRow(buildRow("HIV_TB_SCRN_ART_P. 1", "< 15 years, Male", Integer.class,
                gettbscrnByAgeAndGender(0, 15, Gender.Male)));
        dataSet.addRow(buildRow("HIV_TB_SCRN_ART_P. 2", "< 15 years, female", Integer.class,
                gettbscrnByAgeAndGender(0, 15, Gender.Female)));
        dataSet.addRow(buildRow("HIV_TB_SCRN_ART_P. 3", ">= 15 years, Male", Integer.class,
                gettbscrnByAgeAndGender(15, 150, Gender.Male)));
        dataSet.addRow(buildRow("HIV_TB_SCRN_ART_P. 4", ">= 15 years, female", Integer.class,
                gettbscrnByAgeAndGender(15, 150, Gender.Female)));

    }

    private DataSetRow buildRow(String col_1_value, String col_2_value, Class<?> dataType, Object col_3_value) {
        DataSetRow setRow = new DataSetRow();
        setRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, dataType), col_1_value);
        setRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, dataType), col_2_value);
        String column_3_name = "Number";
        setRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, dataType), col_3_value);
        return setRow;
    }

    private Integer gettbscrnByAgeAndGender(int minAge, int maxAge, Gender gender) {
        int _age = 0;
        List<Integer> patients = new ArrayList<>();
        String _gender = gender.equals(Gender.Female) ? "f" : "m";
        if (maxAge > 1) {
            maxAge = maxAge + 1;
        }
        for (Person person : persons) {
            _age = person.getAge();
            if (!patients.contains(person.getPersonId())
                    && (_age >= minAge && _age < maxAge)
                    && (person.getGender().toLowerCase().equals(_gender))) {

                patients.add(person.getPersonId());

            }
        }
        return patients.size();
    }

}

