package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_ml;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.MLQuery;
import org.openmrs.module.ohrireports.api.query.AggregateBuilder;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_ml.TxMlTransferOutByAgeAndSexDataSetDefinition;
import org.openmrs.module.ohrireports.helper.EthiOhriUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { TxMlTransferOutByAgeAndSexDataSetDefinition.class })
public class TxMlTransferOutByAgeAndSexDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private MLQuery mlQuery;
	
	@Autowired
	private AggregateBuilder aggregateBuilder;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		aggregateBuilder.clearTotal();
		TxMlTransferOutByAgeAndSexDataSetDefinition _datasetDefinition = (TxMlTransferOutByAgeAndSexDataSetDefinition) dataSetDefinition;
		
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		
		SimpleDataSet dataSet1 = EthiOhriUtil.isValidReportDateRange(_datasetDefinition.getStartDate(),
		    _datasetDefinition.getEndDate(), set);
		if (dataSet1 != null)
			return dataSet1;
		
		Cohort cohort = mlQuery.getTransferredOut(mlQuery.cohort);
		
		aggregateBuilder.setCalculateAgeFrom(_datasetDefinition.getEndDate());
		aggregateBuilder.setPersonList(mlQuery.getPersons(cohort));
		aggregateBuilder.setLowerBoundAge(0);
		aggregateBuilder.setUpperBoundAge(65);
		DataSetRow femaleDateSet = new DataSetRow();
		aggregateBuilder.buildDataSetColumn(femaleDateSet, "F");
		set.addRow(femaleDateSet);
		
		DataSetRow maleDateSet = new DataSetRow();
		aggregateBuilder.buildDataSetColumn(maleDateSet, "M");
		set.addRow(maleDateSet);
		
		DataSetRow totalSet = new DataSetRow();
		aggregateBuilder.buildDataSetColumn(totalSet, "T");
		set.addRow(totalSet);
		
		return set;
	}
}
