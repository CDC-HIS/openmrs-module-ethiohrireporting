package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_pvls;

import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_pvls.TX_PVLSDisaggregationByPopulationDatasetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

@Handler(supports = { TX_PVLSDisaggregationByPopulationDatasetDefinition.class })
public class TX_PVLSDisaggregationByPopulationDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		TX_PVLSDisaggregationByPopulationDatasetDefinition txDatasetDefinition = (TX_PVLSDisaggregationByPopulationDatasetDefinition) dataSetDefinition;
		
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		DataSetRow PWIDRow = new DataSetRow();
		
		//PWID
		PWIDRow.addColumnValue(new DataSetColumn("type", "", String.class), "PWID");
		PWIDRow.addColumnValue(new DataSetColumn("routine", "", Integer.class), 0);
		PWIDRow.addColumnValue(new DataSetColumn("target", "", Integer.class), 0);
		dataSet.addRow(PWIDRow);
		
		//MSM
		DataSetRow MSMRow = new DataSetRow();
		MSMRow.addColumnValue(new DataSetColumn("type", "", String.class), "MSM");
		MSMRow.addColumnValue(new DataSetColumn("routine", "", Integer.class), 0);
		MSMRow.addColumnValue(new DataSetColumn("target", "", Integer.class), 0);
		dataSet.addRow(MSMRow);
		
		//Transgender
		DataSetRow TransgenderRow = new DataSetRow();
		TransgenderRow.addColumnValue(new DataSetColumn("type", "", String.class), "Transgender People");
		TransgenderRow.addColumnValue(new DataSetColumn("routine", "", Integer.class), 0);
		TransgenderRow.addColumnValue(new DataSetColumn("target", "", Integer.class), 0);
		dataSet.addRow(TransgenderRow);
		
		//FSW
		DataSetRow FSWRow = new DataSetRow();
		FSWRow.addColumnValue(new DataSetColumn("type", "", String.class), "FSW");
		FSWRow.addColumnValue(new DataSetColumn("routine", "", Integer.class), 0);
		FSWRow.addColumnValue(new DataSetColumn("target", "", Integer.class), 0);
		dataSet.addRow(FSWRow);
		
		//People in prison and other closed setting
		DataSetRow ppAORow = new DataSetRow();
		ppAORow.addColumnValue(new DataSetColumn("type", "", String.class), "People in prison and other closed setting");
		ppAORow.addColumnValue(new DataSetColumn("routine", "", Integer.class), 0);
		ppAORow.addColumnValue(new DataSetColumn("target", "", Integer.class), 0);
		dataSet.addRow(ppAORow);
		
		return dataSet;
	}
	
}
