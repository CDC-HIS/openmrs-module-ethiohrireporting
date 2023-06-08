package org.openmrs.module.ohrireports.reports.datasetevaluator.hmis.tx_new;

import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.reports.datasetdefinition.hmis.tx_new.HIVTXNewDatasetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { HIVTXNewDatasetDefinition.class })
public class HIVTXNewDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private EvaluationService evaluationService;
	private EvaluationContext context;
	private HIVTXNewDatasetDefinition datasetDefinition;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		dataSetDefinition = (HIVTXNewDatasetDefinition) datasetDefinition;
		context= evalContext;
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		buildDataSet(dataSet);

		return dataSet;
	}
	
	public void buildDataSet(SimpleDataSet dataSet){
		DataSetRow headDataSetRow = new DataSetRow();
		headDataSetRow.addColumnValue(null, headDataSetRow);

	}
}
