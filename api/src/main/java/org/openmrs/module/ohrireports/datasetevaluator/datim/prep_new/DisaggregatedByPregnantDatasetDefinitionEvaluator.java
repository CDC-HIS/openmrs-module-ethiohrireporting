package org.openmrs.module.ohrireports.datasetevaluator.datim.prep_new;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.PreExposureProphylaxisQuery;
import org.openmrs.module.ohrireports.datasetdefinition.datim.pr_ep_new.DisaggregatedByPopulationTypDatasetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.datim.pr_ep_new.DisaggregatedByPregnantDatasetDefinition;
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

@Handler(supports = { DisaggregatedByPregnantDatasetDefinition.class })
public class DisaggregatedByPregnantDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private PreExposureProphylaxisQuery preExposureProphylaxisQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		DisaggregatedByPregnantDatasetDefinition definition = (DisaggregatedByPregnantDatasetDefinition) dataSetDefinition;
		SimpleDataSet set = new SimpleDataSet(definition, evalContext);
		
		SimpleDataSet _dataSet = EthiOhriUtil
		        .isValidReportDateRange(definition.getStartDate(), definition.getEndDate(), set);
		if (_dataSet != null)
			return _dataSet;
		
		DataSetRow pRow = new DataSetRow();
		Cohort baseCohort = preExposureProphylaxisQuery.getAllNewPrEP();
		Cohort pregnantCohort = preExposureProphylaxisQuery.getAllPregnantPrep(baseCohort);
		
		pRow.addColumnValue(new DataSetColumn("Name", "", String.class), "Pregnant");
		pRow.addColumnValue(new DataSetColumn("-", "", Integer.class), pregnantCohort.size());
		set.addRow(pRow);
		
		DataSetRow bRow = new DataSetRow();
		Cohort breastFeedingCohort = preExposureProphylaxisQuery.getAllBreastFeedingPrep(baseCohort);
		bRow.addColumnValue(new DataSetColumn("Name", "", String.class), "Breastfeeding");
		bRow.addColumnValue(new DataSetColumn("-", "", Integer.class), breastFeedingCohort.size());
		set.addRow(bRow);
		
		return set;
	}
}
