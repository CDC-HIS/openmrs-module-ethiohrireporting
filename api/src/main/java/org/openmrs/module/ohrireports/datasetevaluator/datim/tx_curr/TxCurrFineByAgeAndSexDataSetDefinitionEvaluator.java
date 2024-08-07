package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_curr;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.query.AggregateBuilder;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_curr.TxCurrFineByAgeAndSexDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = {TxCurrFineByAgeAndSexDataSetDefinition.class})
public class TxCurrFineByAgeAndSexDataSetDefinitionEvaluator implements DataSetEvaluator {

    private TxCurrFineByAgeAndSexDataSetDefinition hdsd;
    private String title = "Number of adults and children Currently enrolling on antiretroviral therapy (ART)";
    private PatientQueryService patientQueryService;
    @Autowired
    private AggregateBuilder aggregateBuilder;
    @Autowired
    private EncounterQuery encounterQuery;
    List<Person> personList = new ArrayList<>();

    @Override
    public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
        aggregateBuilder.clearTotal();
        TxCurrFineByAgeAndSexDataSetDefinition hdsd = (TxCurrFineByAgeAndSexDataSetDefinition) dataSetDefinition;
        SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);

        PatientQueryService patientQueryService = Context.getService(PatientQueryService.class);

        aggregateBuilder.setCalculateAgeFrom(hdsd.getEndDate());
        List<Integer> encounters = encounterQuery.getAliveFollowUpEncounters(null,hdsd.getEndDate());
        Cohort baseCohort = patientQueryService.getActiveOnArtCohort("", null, hdsd.getEndDate(), null, encounters);
        personList = patientQueryService.getPersons(baseCohort);

        aggregateBuilder.setPersonList(personList);
        aggregateBuilder.setLowerBoundAge(0);
        aggregateBuilder.setUpperBoundAge(65);
        DataSetRow femaleDateSet = new DataSetRow();
        aggregateBuilder.buildDataSetColumn(femaleDateSet, "F");
        set.addRow(femaleDateSet);

        DataSetRow maleDataSet = new DataSetRow();
        aggregateBuilder.buildDataSetColumn(maleDataSet, "M");
        set.addRow(maleDataSet);

        DataSetRow totalSet = new DataSetRow();
        aggregateBuilder.buildDataSetColumn(totalSet, "T");
        set.addRow(totalSet);

        return set;
    }


}
