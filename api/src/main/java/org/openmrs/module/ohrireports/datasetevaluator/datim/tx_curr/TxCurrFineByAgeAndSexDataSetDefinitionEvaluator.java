package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_curr;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.ALIVE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.FOLLOW_UP_STATUS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.RESTART;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TREATMENT_END_DATE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.openmrs.Cohort;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.query.AggregateBuilder;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_curr.TxCurrFineByAgeAndSexDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
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

        hdsd = (TxCurrFineByAgeAndSexDataSetDefinition) dataSetDefinition;
        SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);

        patientQueryService = Context.getService(PatientQueryService.class);

        aggregateBuilder.setCalculateAgeFrom(hdsd.getEndDate());
        List<Integer> encounters = encounterQuery.getAliveFollowUpEncounters(null,hdsd.getEndDate());
        Cohort baseCohort = patientQueryService.getActiveOnArtCohort("", null, hdsd.getEndDate(), null, encounters);
        personList = patientQueryService.getPersons(baseCohort);

        aggregateBuilder.setPersonList(personList);

        DataSetRow femaleDateSet = new DataSetRow();
        aggregateBuilder.buildDataSetColumn(femaleDateSet, "F");
        set.addRow(femaleDateSet);

        DataSetRow maleDataSet = new DataSetRow();
        aggregateBuilder.buildDataSetColumn(maleDataSet, "M");
        set.addRow(maleDataSet);
        return set;
    }


}
