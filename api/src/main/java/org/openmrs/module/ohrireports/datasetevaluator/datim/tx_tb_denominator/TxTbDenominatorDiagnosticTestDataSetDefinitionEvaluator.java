package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_tb_denominator;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.TBQuery;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_tb_denominator.TxTbDenominatorDiagnosticTestDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { TxTbDenominatorDiagnosticTestDataSetDefinition.class })
public class TxTbDenominatorDiagnosticTestDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	private TxTbDenominatorDiagnosticTestDataSetDefinition hdsd;
	
	@Autowired
	private TBQuery tbQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		hdsd = (TxTbDenominatorDiagnosticTestDataSetDefinition) dataSetDefinition;
		
		DataSetRow dataSet = new DataSetRow();
		dataSet.addColumnValue(new DataSetColumn("", "", String.class),
		    "Number of ART patients whose specimen were sent for the following diagnosis test");
		dataSet.addColumnValue(new DataSetColumn("smear", "Smear Only", Integer.class),
		    tbQuery.getSmearOnly(tbQuery.getDenomiatorCohort(), hdsd.getStartDate(), hdsd.getEndDate()));
		dataSet.addColumnValue(new DataSetColumn("mwrd",
		        "mWRD : Molecular WHO Recommended Diagnostic PCR (with or without other testing)", Integer.class), tbQuery
		        .getLFMResult(tbQuery.getDenomiatorCohort(), hdsd.getStartDate(), hdsd.getEndDate()));
		dataSet.addColumnValue(new DataSetColumn("additional", "Additional test Other than mWRD ", Integer.class),
		    tbQuery.getOtherThanLFMResult(tbQuery.getDenomiatorCohort(), hdsd.getStartDate(), hdsd.getEndDate()));
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		set.addRow(dataSet);
		return set;
	}
	
}
