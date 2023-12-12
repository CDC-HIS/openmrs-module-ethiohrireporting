package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_tb_denominator;

import java.util.Arrays;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.TBQuery;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
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

import static org.openmrs.module.ohrireports.OHRIReportsConstants.TB_SCREENING_DATE;

@Handler(supports = { TxTbDenominatorDiagnosticTestDataSetDefinition.class })
public class TxTbDenominatorDiagnosticTestDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	private TxTbDenominatorDiagnosticTestDataSetDefinition hdsd;
	
	@Autowired
	private TBQuery tbQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		hdsd = (TxTbDenominatorDiagnosticTestDataSetDefinition) dataSetDefinition;
		Cohort cohort = tbQuery.getDenominator(hdsd.getStartDate(), hdsd.getEndDate());
		
		DataSetRow dataSet = new DataSetRow();
		dataSet.addColumnValue(new DataSetColumn("", "", String.class),
		    "Number of ART patients whose specimen were sent for the following diagnosis test");
		dataSet.addColumnValue(new DataSetColumn("smear", "Smear Only", Integer.class), tbQuery.getSmearOnly(cohort,hdsd.getStartDate(),hdsd.getEndDate()));
		dataSet.addColumnValue(new DataSetColumn("mwrd",
		        "mWRD : Molecular WHO Recommended Diagnostic PCR (with or without other testing)", Integer.class), tbQuery
		        .getLFMResult(cohort, hdsd.getStartDate(), hdsd.getEndDate()));
		dataSet.addColumnValue(new DataSetColumn("additional", "Additional test Other than mWRD ", Integer.class),
		    tbQuery.getOtherThanLFMResult(cohort,hdsd.getStartDate(),hdsd.getEndDate()));
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		set.addRow(dataSet);
		return set;
	}
	
}
