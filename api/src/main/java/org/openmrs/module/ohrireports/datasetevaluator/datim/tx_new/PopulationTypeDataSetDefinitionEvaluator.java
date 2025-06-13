package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_new;

import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_new.PopulationTypeDataSetDefinition;
import org.openmrs.module.ohrireports.helper.EthiOhriUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

@Handler(supports = { PopulationTypeDataSetDefinition.class })
public class PopulationTypeDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		PopulationTypeDataSetDefinition _dataSetDefinition1 = (PopulationTypeDataSetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		SimpleDataSet dataSet1 = EthiOhriUtil
				.isValidReportDateRange(_dataSetDefinition1.getStartDate(), _dataSetDefinition1.getEndDate(), dataSet);
		if (dataSet1 != null)
			return dataSet1;

		DataSetRow PWIDRow = new DataSetRow();
		PWIDRow.addColumnValue(new DataSetColumn("PopulationType", "Population Type", String.class), "PWID");
		PWIDRow.addColumnValue(new DataSetColumn("total", "Total", Integer.class), 0);
		dataSet.addRow(PWIDRow);
		
		DataSetRow mSMRow = new DataSetRow();
		mSMRow.addColumnValue(new DataSetColumn("PopulationType", "Population Type", String.class), "MSM");
		mSMRow.addColumnValue(new DataSetColumn("total", "Total", Integer.class), 0);
		dataSet.addRow(mSMRow);
		
		DataSetRow transGRow = new DataSetRow();
		transGRow.addColumnValue(new DataSetColumn("PopulationType", "Population Type", String.class), "Transgender People");
		transGRow.addColumnValue(new DataSetColumn("total", "Total", Integer.class), 0);
		dataSet.addRow(transGRow);
		
		DataSetRow fSWRow = new DataSetRow();
		fSWRow.addColumnValue(new DataSetColumn("PopulationType", "Population Type", String.class), "FSW");
		fSWRow.addColumnValue(new DataSetColumn("total", "Total", Integer.class), 0);
		dataSet.addRow(fSWRow);
		
		DataSetRow pPCRow = new DataSetRow();
		pPCRow.addColumnValue(new DataSetColumn("PopulationType", "Population Type", String.class),
		    "People in prison and other closed settings");
		pPCRow.addColumnValue(new DataSetColumn("total", "Total", Integer.class), 0);
		dataSet.addRow(pPCRow);
		
		/*DataSetRow totalSet = new DataSetRow();
		totalSet.addColumnValue(new DataSetColumn("PopulationType", "Population Type", String.class), "Total");
		pPCRow.buildDataSetColumn(totalSet, "T");
		set.addRow(totalSet);*/
		
		return dataSet;
	}
	
}
