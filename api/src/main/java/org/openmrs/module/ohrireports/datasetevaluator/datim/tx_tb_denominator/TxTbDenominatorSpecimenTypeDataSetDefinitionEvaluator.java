package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_tb_denominator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.TBQuery;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_tb_denominator.TxTbDenominatorSpecimenTypeDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { TxTbDenominatorSpecimenTypeDataSetDefinition.class })
public class TxTbDenominatorSpecimenTypeDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private TBQuery tbQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		TxTbDenominatorSpecimenTypeDataSetDefinition hdsd = (TxTbDenominatorSpecimenTypeDataSetDefinition) dataSetDefinition;
		
		DataSetRow row = new DataSetRow();
		int subTotal = 0;
		int xRayValue = 0;
		int molecularWRD = 0;
		row.addColumnValue(new DataSetColumn("", "", String.class),
		    "Number of patient who had a TB screening by Screening Type ");
		
		row.addColumnValue(new DataSetColumn("num", "Num", String.class), "");
		
		//TODO: calculate value
		row.addColumnValue(new DataSetColumn("Symptom Screen Only", "Symptom Screen Only ", Integer.class), tbQuery
		        .getDenomiatorCohort().size());
		
		//TODO: calculate value
		row.addColumnValue(new DataSetColumn("Chest X-Ray", "Chest X-Ray ", Integer.class), xRayValue);
		
		//TODO: calculate value
		row.addColumnValue(new DataSetColumn("Molecular WHO Recommended Diagnostic Test(mWRD)",
		        "Molecular WHO Recommended Diagnostic Test(mWRD)", Integer.class), molecularWRD);
		
		//TODO: calculate value
		subTotal += tbQuery.getDenomiatorCohort().size() + xRayValue + molecularWRD;
		row.addColumnValue(new DataSetColumn("Sub total ", "Subtotal ", Integer.class), subTotal);
		
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		
		set.addRow(row);
		return set;
	}
	
}
