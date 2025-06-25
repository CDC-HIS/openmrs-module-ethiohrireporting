package org.openmrs.module.ohrireports.datasetevaluator.hmis;

import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.HMISDatasetDefinition;
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

@Handler(supports = { HMISDatasetDefinition.class })
public class HMISDatasetEvaluator implements DataSetEvaluator {
	
	@Autowired
	private HMISQuery hmisQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		HMISDatasetDefinition definition = (HMISDatasetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(definition, evalContext);
		
		SimpleDataSet _dataSet = EthiOhriUtil.isValidReportDateRange(definition.getStartDate(), definition.getEndDate(),
		    dataSet);
		if (_dataSet != null)
			return _dataSet;
		
		hmisQuery.run(definition.getStartDate(), definition.getEndDate(), dataSet);
		return dataSet;
	}
}
