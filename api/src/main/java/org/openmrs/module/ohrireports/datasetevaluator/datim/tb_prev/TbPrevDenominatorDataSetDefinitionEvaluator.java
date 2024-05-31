package org.openmrs.module.ohrireports.datasetevaluator.datim.tb_prev;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.TPT_START_DATE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.TBQuery;
import org.openmrs.module.ohrireports.api.query.AggregateBuilder;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tb_prev.TbPrevDominatorDatasetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = {TbPrevDominatorDatasetDefinition.class})
public class TbPrevDenominatorDataSetDefinitionEvaluator implements DataSetEvaluator {

    private TbPrevDominatorDatasetDefinition hdsd;

    @Autowired
    private TBQuery tbQuery;

    List<Integer> baseEncounters = new ArrayList<>();
    private Date endDate = null;

    private int maleTotal = 0;

    private int femaleTotal = 0;
    @Autowired
    private EncounterQuery encounterQuery;

    @Autowired
    private AggregateBuilder _AggregateBuilder;

    @Override
    public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext)
            throws EvaluationException {

        hdsd = (TbPrevDominatorDatasetDefinition) dataSetDefinition;
        SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);

        if (!hdsd.getHeader()) {

            _AggregateBuilder.setCalculateAgeFrom(hdsd.getEndDate());
            Date prevSixMonth = getPrevSixMonth();

            if (Objects.isNull(endDate) || !endDate.equals(hdsd.getEndDate()))
                baseEncounters = encounterQuery.getEncounters(Arrays.asList(TPT_START_DATE), prevSixMonth, hdsd.getStartDate());

            endDate = hdsd.getEndDate();
            Cohort tptCohort = tbQuery.getTPTCohort(baseEncounters, TPT_START_DATE, prevSixMonth,
                    hdsd.getStartDate());
            Cohort onArtCorCohort = new Cohort(
                    tbQuery.getArtStartedCohort(baseEncounters, null, hdsd.getEndDate(), tptCohort));

            if (!hdsd.getAggregateType()) {

                buildRowForTotalValue(set, onArtCorCohort.size());

            } else {
                Cohort newOnARTCohort = new Cohort(
                        tbQuery.getArtStartedCohort("", prevSixMonth, endDate, onArtCorCohort, null, baseEncounters));
                Cohort oldOnACohort = new Cohort(tbQuery.getArtStartedCohort("", null, prevSixMonth, onArtCorCohort, null, baseEncounters));

                buildRowForDisaggregation(set, newOnARTCohort, oldOnACohort);

            }
        }

        return set;
    }

    private Date getPrevSixMonth() {
        Calendar subSixMonth = Calendar.getInstance();
        subSixMonth.setTime(hdsd.getStartDate());
        subSixMonth.add(Calendar.MONTH, -6);
        return subSixMonth.getTime();
    }

    private void buildRowForDisaggregation(SimpleDataSet set, Cohort newOnARTCohort, Cohort oldOnACohort) {

        Cohort cohortByArt = new Cohort(tbQuery.getArtStartedCohort(baseEncounters, hdsd.getStartDate(), endDate,
                newOnARTCohort));
        buildDataRow(set, tbQuery.getPersons(cohortByArt), "Newly enrolled on ART");
        // #endregion

        // #region already enrolled on ART with TPT completed
        cohortByArt = new Cohort(tbQuery.getArtStartedCohort(baseEncounters, null, hdsd.getStartDate(),
                oldOnACohort));
        buildDataRow(set, tbQuery.getPersons(cohortByArt), "Previously enrolled on ART");
        // #endregion
        // Disaggregated By ART Start by Age/Sex
//        DataSetRow dataSetRow = new DataSetRow();
//        dataSetRow.addColumnValue(new DataSetColumn("", "", String.class), "Newly enrolled on ART");
//        _AggregateBuilder.setPersonList(tbQuery.getPersons(newOnARTCohort));
//        DataSetRow femaleDataRowNew = new DataSetRow();
//
//        _AggregateBuilder.buildDataSetColumn(femaleDataRowNew, "F", 15);
//        set.addRow(femaleDataRowNew);
//
//        DataSetRow maleDataRowNew = new DataSetRow();
//        _AggregateBuilder.buildDataSetColumn(maleDataRowNew, "M", 15);
//        set.addRow(maleDataRowNew);
//
//
//        DataSetRow dataSetRowPrev = new DataSetRow();
//        dataSetRowPrev.addColumnValue(new DataSetColumn("", "", String.class), "Previously enrolled on ART");
//
//        _AggregateBuilder.setPersonList(tbQuery.getPersons(oldOnACohort));
//
//        DataSetRow femaleRowPrev = new DataSetRow();
//        _AggregateBuilder.buildDataSetColumn(femaleRowPrev, "F", 15);
//        set.addRow(femaleRowPrev);
//
//        DataSetRow maleRowPrev = new DataSetRow();
//        _AggregateBuilder.buildDataSetColumn(maleRowPrev, "M", 15);
//        set.addRow(maleRowPrev);
    }

    private void buildRowForTotalValue(SimpleDataSet set, int total) {
        DataSetRow dataSet = new DataSetRow();
        dataSet.addColumnValue(new DataSetColumn("AutoCalculate", "Auto-Calculate", String.class), "Denominator");
        dataSet.addColumnValue(new DataSetColumn("description",
                "Number of ART patients who were initiated on any course of TPT during the previous reporting period",
                Integer.class), total);
        set.addRow(dataSet);
    }

    private void buildDataRow(SimpleDataSet set, List<Person> persons, String desc) {
        DataSetRow row = new DataSetRow();
        int _total = 0;

        int value = getUnknownAgeByGender(persons, "F");
        _total = value;
        row.addColumnValue(new DataSetColumn("", "", String.class), desc);
        row.addColumnValue(new DataSetColumn("femaleKnownAge", "Female Unknown Age", Integer.class), value);
        value = getEnrolledByAgeAndGender(0, 14, persons, "F");
        _total += value;
        row.addColumnValue(new DataSetColumn("f<15", "Female <15", Integer.class), value);

        value = getEnrolledByAgeAndGender(15, 150, persons, "F");
        _total += value;
        row.addColumnValue(new DataSetColumn("f+15", "female +15", Integer.class), value);

        value = getUnknownAgeByGender(persons, "M");
        _total += value;
        row.addColumnValue(new DataSetColumn("maleKnownAge", "Male Unknown Age", Integer.class), value);

        value = getEnrolledByAgeAndGender(0, 14, persons, "M");
        _total += value;
        row.addColumnValue(new DataSetColumn("m<15", "Male <15", Integer.class), value);

        value = getEnrolledByAgeAndGender(15, 150, persons, "M");
        _total += value;
        row.addColumnValue(new DataSetColumn("m+15", "Male +15", Integer.class), value);

        row.addColumnValue(new DataSetColumn("total", "Total", Integer.class), _total);

        set.addRow(row);
    }

    private int getUnknownAgeByGender(List<Person> persons, String gender) {
        int count = 0;
        int age = 0;
        for (Person person : persons) {
            age = person.getAge(hdsd.getEndDate());
            if (person.getGender().equals(gender) && (age <= 0)) {
                count++;
            }

        }
        if (gender.equals("M")) {
            femaleTotal = femaleTotal + count;
        } else {
            maleTotal = maleTotal + count;
        }
        return count;
    }

    private int getEnrolledByAgeAndGender(int min, int max, List<Person> persons, String gender) {
        int count = 0;
        int age = 0;
        for (Person person : persons) {
            age = person.getAge(hdsd.getEndDate());
            if (person.getGender().equals(gender) && (age >= min && age <= max)) {
                count++;
            }

        }

        if (gender.equals("M")) {
            femaleTotal = femaleTotal + count;
        } else {
            maleTotal = maleTotal + count;
        }
        return count;
    }

}
