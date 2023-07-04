package org.openmrs.module.ohrireports.reports.datasetevaluator.hmis.pr_ep.hiv_p_rep_cat;

import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.reports.datasetdefinition.hmis.hiv_p_r_ep_cat.HivPrEpCatagoriesDatasetDefinition;
import org.openmrs.module.ohrireports.reports.datasetevaluator.hmis.pr_ep.HivPrEpQuery;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { HivPrEpCatagoriesDatasetDefinition.class })
public class HivPrEpCatagoriesDatasetDefinitionEvaluators implements DataSetEvaluator {
	
	@Autowired
	private HivPrEpQuery query;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		HivPrEpCatagoriesDatasetDefinition _DatasetDefinition = (HivPrEpCatagoriesDatasetDefinition) dataSetDefinition;
		
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		
		query.initializeDate(_DatasetDefinition.getStartDate(), _DatasetDefinition.getEndDate());
		
		return dataSet;
	}
	
}
