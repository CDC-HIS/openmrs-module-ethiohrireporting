package org.openmrs.module.ohrireports.datasetevaluator.datim.prep_new;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.PreExposureProphylaxisQuery;
import org.openmrs.module.ohrireports.datasetdefinition.datim.pr_ep_new.DisaggregatedByPrepTypeDatasetDefinition;
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

@Handler(supports = { DisaggregatedByPrepTypeDatasetDefinition.class })
public class DisaggregatedByPrepTypeDatasetDefinitionEvaluator implements DataSetEvaluator {
	
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
		DataSetRow oralRow = new DataSetRow();
		oralRow.addColumnValue(new DataSetColumn("Name", "", String.class), "Oral");
		oralRow.addColumnValue(new DataSetColumn("-", "", Integer.class), cohort.size());
		set.addRow(oralRow);
		
		DataSetRow iRow = new DataSetRow();
		iRow.addColumnValue(new DataSetColumn("Name", "", String.class), "Injectable");
		iRow.addColumnValue(new DataSetColumn("-", "", Integer.class), 0);
		set.addRow(iRow);
		
		DataSetRow otherRow = new DataSetRow();
		otherRow.addColumnValue(new DataSetColumn("Name", "", String.class), "Other");
		otherRow.addColumnValue(new DataSetColumn("-", "", Integer.class), 0);
		set.addRow(otherRow);
		
		return set;
	}
}
