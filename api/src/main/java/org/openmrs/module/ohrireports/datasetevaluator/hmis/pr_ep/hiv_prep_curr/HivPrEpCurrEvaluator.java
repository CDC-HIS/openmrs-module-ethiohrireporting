package org.openmrs.module.ohrireports.datasetevaluator.hmis.pr_ep.hiv_prep_curr;

import org.openmrs.Cohort;
import org.openmrs.CohortMembership;
import org.openmrs.Person;
import org.openmrs.module.ohrireports.constants.EncounterType;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.openmrs.module.ohrireports.constants.PrepConceptQuestions;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.Gender;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.pr_ep.HivPrEpQuery;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.openmrs.module.ohrireports.constants.ETHIOHRIReportsConstants.DISCORDANT_COUPLE;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_1_NAME;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_2_NAME;

@Component
@Scope("prototype")
public class HivPrEpCurrEvaluator {

    private final String COLUMN_3_NAME = "Number";
    List<Person> personList = new ArrayList<>();
    @Autowired
    private HivPrEpQuery hivPrEPQuery;
    private Date end;
    private Set<Person> personSet;

    public void buildDataset(Date start, Date end, SimpleDataSet dataset) {

        this.end = end;

        hivPrEPQuery.setStartDate(start);
        hivPrEPQuery.setEndDate(end, FollowUpConceptQuestions.FOLLOW_UP_DATE, EncounterType.PREP_FOLLOW_UP_ENCOUNTER_TYPE);

        Cohort prepFollowupCohort = hivPrEPQuery.getAllPrEPCurr();
        Cohort prepNewCohort = hivPrEPQuery.getNewOnPrep();
        Cohort cohort = Cohort.union(prepFollowupCohort, prepNewCohort);
        dataset.addRow(buildColumn("", "PrEP Curr (Number of individuals that received oral PrEP during the reporting period)", ""));

        personList = hivPrEPQuery.getPersons(cohort);
        personSet = new HashSet<>(personList);
        removeAllBelowAge();

        dataset.addRow(buildColumn("", "By Age and Sex", personSet.size()));
        dataset.addRow(buildColumn(".1", "15 - 19 years, Male", getCohortSizeByAgeAndGender(15, 19, Gender.Male)));
        dataset.addRow(buildColumn(".2", "15 - 19 years, Female", getCohortSizeByAgeAndGender(15, 19, Gender.Female)));
        dataset.addRow(buildColumn(".3", "20 - 24 years, Male", getCohortSizeByAgeAndGender(20, 24, Gender.Male)));
        dataset.addRow(buildColumn(".4", "20 - 24 years, Female", getCohortSizeByAgeAndGender(20, 24, Gender.Female)));
        dataset.addRow(buildColumn(".5", "25 - 29 years, Male", getCohortSizeByAgeAndGender(25, 29, Gender.Male)));
        dataset.addRow(buildColumn(".6", "25 - 29 years, Female", getCohortSizeByAgeAndGender(25, 29, Gender.Female)));
        dataset.addRow(buildColumn(".7", "30 - 34 years, Male", getCohortSizeByAgeAndGender(30, 34, Gender.Male)));
        dataset.addRow(buildColumn(".8", "30 - 34 years, Female", getCohortSizeByAgeAndGender(30, 34, Gender.Female)));
        dataset.addRow(buildColumn(".9", "35 - 39 years, Male", getCohortSizeByAgeAndGender(35, 39, Gender.Male)));
        dataset.addRow(buildColumn(".10", "35 - 39 years, Female", getCohortSizeByAgeAndGender(35, 39, Gender.Female)));
        dataset.addRow(buildColumn(".11", "40 - 44 years, Male", getCohortSizeByAgeAndGender(40, 44, Gender.Male)));
        dataset.addRow(buildColumn(".12", "40 - 44 years, Female", getCohortSizeByAgeAndGender(40, 44, Gender.Female)));
        dataset.addRow(buildColumn(".13", "45 - 49 years, Male", getCohortSizeByAgeAndGender(45, 49, Gender.Male)));
        dataset.addRow(buildColumn(".14", "45 - 49 years, Female", getCohortSizeByAgeAndGender(45, 49, Gender.Female)));
        dataset.addRow(buildColumn(".15", ">=50 years, Male", getCohortSizeByAgeAndGender(50, 150, Gender.Male)));
        dataset.addRow(buildColumn(".16", ">=50 years, Female", getCohortSizeByAgeAndGender(50, 150, Gender.Female)));

        int total = 0;
        Cohort finalCohort =new Cohort();
        personSet.forEach(p->{finalCohort.addMembership(new CohortMembership(p.getPersonId()));});

        Cohort fsw = hivPrEPQuery.getCategoryOnPrep(PrepConceptQuestions.SELF_IDENTIFYING_FSW, finalCohort);
        Cohort discordantCouple = hivPrEPQuery.getCategoryOnPrep(DISCORDANT_COUPLE, finalCohort);
        total = fsw.size() + discordantCouple.size();

        DataSetRow clientCategoryRow = new DataSetRow();

        clientCategoryRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), "HIV_PrEP_CURR.2");
        clientCategoryRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class),
                "By Client Category");
        clientCategoryRow.addColumnValue(new DataSetColumn(COLUMN_3_NAME, COLUMN_3_NAME, Integer.class), total);

        dataset.addRow(clientCategoryRow);

        DataSetRow discordantCoupleCategoryRow = new DataSetRow();

        discordantCoupleCategoryRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class),
                "HIV_PrEP_CURR.2 .1");
        discordantCoupleCategoryRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class),
                "Discordant Couple");
        discordantCoupleCategoryRow.addColumnValue(new DataSetColumn(COLUMN_3_NAME, COLUMN_3_NAME, Integer.class),
                discordantCouple.size());

        dataset.addRow(discordantCoupleCategoryRow);

        DataSetRow fwCategoryRow = new DataSetRow();

        fwCategoryRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), "HIV_PrEP_CURR.2 .2");
        fwCategoryRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class),
                "Female sex worker[FSW]");
        fwCategoryRow.addColumnValue(new DataSetColumn(COLUMN_3_NAME, COLUMN_3_NAME, Integer.class), fsw.size());

        dataset.addRow(fwCategoryRow);


    }

    private DataSetRow buildColumn(String col_1_value, String col_2_value, Integer col_3_value) {
        DataSetRow prepDataSetRow = new DataSetRow();
        String baseName = "HIV_PrEP_CURR.1 ";
        prepDataSetRow.addColumnValue(
                new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class),
                baseName + col_1_value);
        prepDataSetRow.addColumnValue(
                new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), col_2_value);

        prepDataSetRow.addColumnValue(new DataSetColumn(COLUMN_3_NAME, COLUMN_3_NAME, Integer.class),
                col_3_value);

        return prepDataSetRow;
    }

    private DataSetRow buildColumn(String col_1_value, String col_2_value, String col_3_value) {
        DataSetRow prepDataSetRow = new DataSetRow();
        String baseName = "HIV_PrEP_CURR.1 ";
        prepDataSetRow.addColumnValue(
                new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class),
                baseName + col_1_value);
        prepDataSetRow.addColumnValue(
                new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), col_2_value);

        prepDataSetRow.addColumnValue(new DataSetColumn(COLUMN_3_NAME, COLUMN_3_NAME, String.class),
                col_3_value);

        return prepDataSetRow;
    }

    private void removeAllBelowAge() {
        personSet.removeIf(person -> person.getAge(end) < 15);
    }

    private Integer getCohortSizeByAgeAndGender(int minAge, int maxAge, Gender gender) {
        int _age;
        int count = 0;
        if (maxAge > 1) {
            maxAge = maxAge + 1;
        }
        for (Person person : personSet) {
            _age = person.getAge(end);
            if ((_age >= minAge && _age < maxAge)
                    && (person.getGender().equalsIgnoreCase(gender.getValue()))) {
                count++;
            }
        }
        return count;
    }

}
