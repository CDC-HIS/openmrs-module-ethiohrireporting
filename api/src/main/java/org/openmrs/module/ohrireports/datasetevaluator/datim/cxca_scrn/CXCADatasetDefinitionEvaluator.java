package org.openmrs.module.ohrireports.datasetevaluator.datim.cxca_scrn;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.CervicalCancerQuery;
import org.openmrs.module.ohrireports.api.query.AggregateBuilder;
import org.openmrs.module.ohrireports.constants.ConceptAnswer;
import org.openmrs.module.ohrireports.datasetdefinition.datim.cxca_scrn.CXCADatasetDefinition;
import org.openmrs.module.ohrireports.helper.EthiOhriUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = {CXCADatasetDefinition.class})
public class CXCADatasetDefinitionEvaluator implements DataSetEvaluator {
    private int total = 0;
    List<Person> persons = new ArrayList<>();
    private CXCADatasetDefinition cxcaDatasetDefinition;

    private EvaluationContext context;
    @Autowired
    private CervicalCancerQuery cervicalCancerQuery;

    @Autowired
    private AggregateBuilder aggregateBuilder;

    @Override
    public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext)
            throws EvaluationException {
        aggregateBuilder.clearTotal();
        cxcaDatasetDefinition = (CXCADatasetDefinition) dataSetDefinition;
        context = evalContext;
        SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);

        SimpleDataSet _dataSet = EthiOhriUtil.isValidReportDateRange(cxcaDatasetDefinition.getStartDate(),
                cxcaDatasetDefinition.getEndDate(), dataSet);
        if (_dataSet != null) return _dataSet;

        aggregateBuilder.setCalculateAgeFrom(cxcaDatasetDefinition.getEndDate());

        CxcaScreening cxcaScreening = getCxcaScreening(cxcaDatasetDefinition.getScreeningType());

        List<Person> negativeCxCaPersonList = cervicalCancerQuery.getPersons(cxcaScreening.getNegivetCohort());

        aggregateBuilder.setPersonList(negativeCxCaPersonList);
        DataSetRow negativeCxCaRow = new DataSetRow();
        aggregateBuilder.buildDataSetColumnForScreening(negativeCxCaRow, "Negative");
        dataSet.addRow(negativeCxCaRow);

        List<Person> positiveCxCaPersonList = cervicalCancerQuery.getPersons(cxcaScreening.getPositiveCohort());
        aggregateBuilder.setPersonList(positiveCxCaPersonList);
        DataSetRow positiveCxCaRow = new DataSetRow();
        aggregateBuilder.buildDataSetColumnForScreening(positiveCxCaRow, "Positive");
        dataSet.addRow(positiveCxCaRow);

        List<Person> suspeciousCxCaPersonList = cervicalCancerQuery.getPersons(cxcaScreening.getSuspectedCohort());
        aggregateBuilder.setPersonList(suspeciousCxCaPersonList);
        DataSetRow suspiciousCxCaRow = new DataSetRow();
        aggregateBuilder.buildDataSetColumnForScreening(suspiciousCxCaRow, "Suspicious");
        dataSet.addRow(suspiciousCxCaRow);

        DataSetRow totalSet = new DataSetRow();
        aggregateBuilder.buildDataSetColumnForScreening(totalSet, "T");
        dataSet.addRow(totalSet);

        return dataSet;
    }

    private CxcaScreening getCxcaScreening(String screeningType) {
        switch (screeningType) {
            case ConceptAnswer.CXCA_FIRST_TIME_SCREENING_TYPE:
                return cervicalCancerQuery.getFirstScreening();
            case ConceptAnswer.CXCA_TYPE_OF_SCREENING_RESCREEN:
                return cervicalCancerQuery.getReScreening();
            case ConceptAnswer.CXCA_TYPE_OF_SCREENING_POST_TREATMENT:
                return cervicalCancerQuery.getPostScreening();
            default:
                return null;
        }
    }


    private int getEnrolledByAgeAndGender(int min, int max) {
        int count = 0;
        List<Integer> personIds = new ArrayList<>();
        for (Person person : persons) {

            if (personIds.contains(person.getPersonId()))
                continue;

            if (person.getAge() >= min && person.getAge() <= max) {
                personIds.add(person.getPersonId());
                count++;
            }
        }
        incrementTotalCount(count);
        clearCountedPerson(personIds);
        return count;
    }

    private int getEnrolledByUnknownAge() {
        int count = 0;
        List<Integer> personIds = new ArrayList<>();
        for (Person person : persons) {
            if (personIds.contains(person.getPersonId()))
                continue;

            if (Objects.isNull(person.getAge()) ||
                    person.getAge() <= 0) {
                count++;
                personIds.add(person.getPersonId());
            }

        }
        incrementTotalCount(count);
        clearCountedPerson(personIds);
        return count;
    }

    private void incrementTotalCount(int count) {
        if (count > 0)
            total = total + count;
    }

    private void clearCountedPerson(List<Integer> personIds) {
        for (int pId : personIds) {
            persons.removeIf(p -> p.getPersonId().equals(pId));
        }
    }

}
