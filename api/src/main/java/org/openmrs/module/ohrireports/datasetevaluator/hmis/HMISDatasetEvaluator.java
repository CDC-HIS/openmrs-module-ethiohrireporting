package org.openmrs.module.ohrireports.datasetevaluator.hmis;

import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.HMISDatasetDefinition;
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
		
		// Check start date and end date are valid
		// If start date is greater than end date
		if (definition.getStartDate() != null && definition.getEndDate() != null
		        && definition.getStartDate().compareTo(definition.getEndDate()) > 0) {
			//throw new EvaluationException("Start date cannot be greater than end date");
			DataSetRow row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("Error", "Error", Integer.class),
			    "Report start date cannot be after report end date");
			dataSet.addRow(row);
			return dataSet;
		}
		hmisQuery.run(definition.getStartDate(), definition.getEndDate(), dataSet);
		return dataSet;
	}
}
