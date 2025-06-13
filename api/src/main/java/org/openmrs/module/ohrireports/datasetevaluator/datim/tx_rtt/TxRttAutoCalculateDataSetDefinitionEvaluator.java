package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_rtt;

import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.RTTQuery;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_rtt.TxRttAutoCalculateDataSetDefinition;
import org.openmrs.module.ohrireports.helper.EthiOhriUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { TxRttAutoCalculateDataSetDefinition.class })
public class TxRttAutoCalculateDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private RTTQuery rttQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		TxRttAutoCalculateDataSetDefinition _datasetDefinition = (TxRttAutoCalculateDataSetDefinition) dataSetDefinition;
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		
		SimpleDataSet dataSet1 = EthiOhriUtil.isValidReportDateRange(_datasetDefinition.getStartDate(),
		    _datasetDefinition.getEndDate(), set);
		if (dataSet1 != null)
			return dataSet1;
		
		if (!_datasetDefinition.getHeader()) {
			rttQuery.getRttCohort(_datasetDefinition.getStartDate(), _datasetDefinition.getEndDate());
			
			DataSetRow dataSet = new DataSetRow();
			dataSet.addColumnValue(new DataSetColumn("adultAndChildrenEnrolled", "Numerator", Integer.class), rttQuery
			        .getBaseCohort().getSize());
			
			set.addRow(dataSet);
		}
		
		return set;
	}
}
