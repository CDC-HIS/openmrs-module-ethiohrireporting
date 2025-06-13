package org.openmrs.module.ohrireports.datasetevaluator.datim.prep_new;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.PreExposureProphylaxisQuery;
import org.openmrs.module.ohrireports.datasetdefinition.datim.pr_ep_new.DisaggregatedByPregnantDatasetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.datim.pr_ep_new.DisaggregatedByPrepDistributionDatasetDefinition;
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

@Handler(supports = { DisaggregatedByPrepDistributionDatasetDefinition.class })
public class DisaggregatedByPrepDistributionDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private PreExposureProphylaxisQuery preExposureProphylaxisQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		
		SimpleDataSet _dataSet = EthiOhriUtil.isValidReportDateRange(preExposureProphylaxisQuery.getStartDate(),
		    preExposureProphylaxisQuery.getEndDate(), set);
		if (_dataSet != null)
			return _dataSet;
		
		Cohort cohort = preExposureProphylaxisQuery.getAllNewPrEP();
		DataSetRow fRow = new DataSetRow();
		fRow.addColumnValue(new DataSetColumn("Name", "", String.class), "Facility");
		fRow.addColumnValue(new DataSetColumn("-", "", Integer.class), cohort.size());
		set.addRow(fRow);
		
		DataSetRow cDataSetRow = new DataSetRow();
		cDataSetRow.addColumnValue(new DataSetColumn("Name", "", String.class), "Community");
		cDataSetRow.addColumnValue(new DataSetColumn("-", "", Integer.class), 0);
		set.addRow(cDataSetRow);
		
		return set;
	}
}
