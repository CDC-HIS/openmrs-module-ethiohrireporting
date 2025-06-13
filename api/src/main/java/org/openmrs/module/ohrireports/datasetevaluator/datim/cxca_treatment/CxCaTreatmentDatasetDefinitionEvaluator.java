package org.openmrs.module.ohrireports.datasetevaluator.datim.cxca_treatment;

import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.CervicalCancerTreatmentQuery;
import org.openmrs.module.ohrireports.api.query.AggregateBuilder;
import org.openmrs.module.ohrireports.constants.ConceptAnswer;
import org.openmrs.module.ohrireports.datasetdefinition.datim.cxca_treatment.CxCaTreatmentDatasetDefinition;
import org.openmrs.module.ohrireports.helper.EthiOhriUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Handler(supports = {CxCaTreatmentDatasetDefinition.class})
public class CxCaTreatmentDatasetDefinitionEvaluator implements DataSetEvaluator {
    List<Person> persons = new ArrayList<>();
    private CxCaTreatmentDatasetDefinition cxCaTreatmentDatasetDefinition;

    @Autowired
    private CervicalCancerTreatmentQuery cervicalCancerTreatmentQuery;
    @Autowired
    private AggregateBuilder aggregateBuilder;


    @Override
    public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext)
            throws EvaluationException {
        aggregateBuilder.clearTotal();

        cxCaTreatmentDatasetDefinition = (CxCaTreatmentDatasetDefinition) dataSetDefinition;


        SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);

        SimpleDataSet _dataSet = EthiOhriUtil.isValidReportDateRange(cxCaTreatmentDatasetDefinition.getStartDate(),
                cxCaTreatmentDatasetDefinition.getEndDate(), dataSet);
        if (_dataSet != null) return _dataSet;

        aggregateBuilder.setCalculateAgeFrom(cxCaTreatmentDatasetDefinition.getEndDate());

        CxCaTreatment cxCaTreatment = getCxCaScreeningType(cxCaTreatmentDatasetDefinition.getScreeningType());

        assert cxCaTreatment != null;
        List<Person> cryotherapyPersonList = cervicalCancerTreatmentQuery.getPersons(cxCaTreatment.getCryotherapyCohort());

        aggregateBuilder.setPersonList(cryotherapyPersonList);
        DataSetRow cryotherapyCxCaTreatmentRow = new DataSetRow();
        aggregateBuilder.buildDataSetColumnForTreatment(cryotherapyCxCaTreatmentRow, "Cryotherapy");
        dataSet.addRow(cryotherapyCxCaTreatmentRow);

        List<Person> leepPersonList = cervicalCancerTreatmentQuery.getPersons(cxCaTreatment.getLeepCohort());
        aggregateBuilder.setPersonList(leepPersonList);
        DataSetRow leepCxCaTreatmentRow = new DataSetRow();
        aggregateBuilder.buildDataSetColumnForTreatment(leepCxCaTreatmentRow, "LEEP");
        dataSet.addRow(leepCxCaTreatmentRow);

        List<Person> thermocoagulationPersonList = cervicalCancerTreatmentQuery.getPersons(cxCaTreatment.getThermocoagulationCohort());
        aggregateBuilder.setPersonList(thermocoagulationPersonList);
        DataSetRow thermocoagulationCxCaTreatmentRow = new DataSetRow();
        aggregateBuilder.buildDataSetColumnForTreatment(thermocoagulationCxCaTreatmentRow, "Thermocoagulation");
        dataSet.addRow(thermocoagulationCxCaTreatmentRow);

        DataSetRow totalSet = new DataSetRow();
        aggregateBuilder.buildDataSetColumnForTreatment(totalSet, "T");
        dataSet.addRow(totalSet);

        return dataSet;

    }

    private CxCaTreatment getCxCaScreeningType(String screeningType) {
        switch (screeningType){
            case ConceptAnswer.CXCA_FIRST_TIME_SCREENING_TYPE:
                return cervicalCancerTreatmentQuery.getFirstScreening();
            case ConceptAnswer.CXCA_TYPE_OF_SCREENING_RESCREEN:
                return  cervicalCancerTreatmentQuery.getReScreening();
            case ConceptAnswer.CXCA_TYPE_OF_SCREENING_POST_TREATMENT:
                return  cervicalCancerTreatmentQuery.getPostScreening();
            default:
                return null;
        }
    }
}
