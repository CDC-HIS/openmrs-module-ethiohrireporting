package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_tb_denominator;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.TBQuery;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_tb_denominator.TxTbDenominatorSpecimenSentDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { TxTbDenominatorSpecimenSentDataSetDefinition.class })
public class TxTbDenominatorSpecimenSentDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private TBQuery tbQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		TxTbDenominatorSpecimenSentDataSetDefinition hdsd = (TxTbDenominatorSpecimenSentDataSetDefinition) dataSetDefinition;
		
		DataSetRow dataSet = new DataSetRow();
		
		dataSet.addColumnValue(new DataSetColumn("", "", String.class),
		    "Number of ART patients who had a specimen sent for bacteriological diagnosis of active TB disease");
		
		dataSet.addColumnValue(new DataSetColumn("num", "Num", Integer.class),
		    tbQuery.getSpecimenSent(tbQuery.getDenomiatorCohort(), hdsd.getStartDate(), hdsd.getEndDate()).size());
		
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		
		set.addRow(dataSet);
		return set;
	}
	
}
