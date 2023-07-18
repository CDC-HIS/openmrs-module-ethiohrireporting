package org.openmrs.module.ohrireports.reports.datasetvaluator.hmis.pr_ep.hiv_p_rep_curr;

import static org.openmrs.module.ohrireports.reports.datasetvaluator.hmis.HMISConstant.COLUMN_1_NAME;
import static org.openmrs.module.ohrireports.reports.datasetvaluator.hmis.HMISConstant.COLUMN_2_NAME;

import java.util.HashSet;
import java.util.Set;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.reports.datasetdefinition.hmis.pr_ep_curr.HivPrEpCurrDatasetDefinition;
import org.openmrs.module.ohrireports.reports.datasetvaluator.hmis.pr_ep.HivPrEpQuery;
import org.openmrs.module.ohrireports.reports.datasetvaluator.hmis.pr_ep.HmisPrepDatasetBuilder;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { HivPrEpCurrDatasetDefinition.class })
public class HivPrEpCurrDatasetDefinitionEvaluator implements DataSetEvaluator {

    @Autowired
    private HivPrEpQuery hivPrEpQuery;
    private Set<Integer> patientIds = new HashSet<>();
    private String column_3_name = "Tir 15";

    @Override
    public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext)
            throws EvaluationException {

        HivPrEpCurrDatasetDefinition _Definition = (HivPrEpCurrDatasetDefinition) dataSetDefinition;

        SimpleDataSet dataSet = new SimpleDataSet(_Definition, evalContext);

        hivPrEpQuery.initializeDate(_Definition.getStartDate(), _Definition.getEndDate());
        patientIds = hivPrEpQuery.getPatientOnPrEpCurr();

        new HmisPrepDatasetBuilder(dataSet, hivPrEpQuery.getPersons(new Cohort(patientIds)), "HIV_PrEP_CURR.1");
        int total = 0;
        int fw = hivPrEpQuery.getFemaleSexWorkerOnPrep(true);
        int discordantCouple = hivPrEpQuery.getDiscordantCoupleOnPrep(true);
        total = fw + discordantCouple;

        DataSetRow clientCategoryRow = new DataSetRow();

        clientCategoryRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), "HIV_PrEP_CURR.2");
        clientCategoryRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class),
                "By Client Category");
        clientCategoryRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class), total);

        dataSet.addRow(clientCategoryRow);

        DataSetRow discordantCoupleCategoryRow = new DataSetRow();

        discordantCoupleCategoryRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class),
                "HIV_PrEP_CURR.2.1");
        discordantCoupleCategoryRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class),
                "Discordant Couple");
        discordantCoupleCategoryRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class),
                discordantCouple);

        dataSet.addRow(discordantCoupleCategoryRow);

        DataSetRow fwCategoryRow = new DataSetRow();

        fwCategoryRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), "HIV_PrEP_CURR.2.2");
        fwCategoryRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class),
                "Female sex worker[FSW]");
        fwCategoryRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class), fw);

        dataSet.addRow(fwCategoryRow);
        return dataSet;

    }

}
