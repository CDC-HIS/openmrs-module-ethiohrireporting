package org.openmrs.module.ohrireports.datasetevaluator.datim.prep_new;

import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.datasetdefinition.datim.pr_ep_new.DisaggregatedByPregnantDatasetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.datim.pr_ep_new.DisaggregatedByPrepDistributionDatasetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

@Handler(supports = { DisaggregatedByPrepDistributionDatasetDefinition.class })
public class DisaggregatedByPrepDistributionDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		DataSetRow fRow = new DataSetRow();
		fRow.addColumnValue(new DataSetColumn("Name", "Name", String.class), "Facility");
		fRow.addColumnValue(new DataSetColumn("-", "", Integer.class), 0);
		set.addRow(fRow);
		
		DataSetRow cDataSetRow = new DataSetRow();
		cDataSetRow.addColumnValue(new DataSetColumn("Name", "Name", String.class), "Community");
		cDataSetRow.addColumnValue(new DataSetColumn("-", "", Integer.class), 0);
		set.addRow(cDataSetRow);
		
		return set;
	}
}
