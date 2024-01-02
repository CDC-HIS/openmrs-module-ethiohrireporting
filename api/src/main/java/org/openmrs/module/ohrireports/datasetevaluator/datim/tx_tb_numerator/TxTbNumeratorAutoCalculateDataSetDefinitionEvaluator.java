package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_tb_numerator;

import java.util.Arrays;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.TBQuery;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_tb_numerator.TxTbNumeratorAutoCalculateDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.TB_TREATMENT_START_DATE;

@Handler(supports = { TxTbNumeratorAutoCalculateDataSetDefinition.class })
public class TxTbNumeratorAutoCalculateDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	@Autowired
	private TBQuery tbQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		TxTbNumeratorAutoCalculateDataSetDefinition hdsd = (TxTbNumeratorAutoCalculateDataSetDefinition) dataSetDefinition;
		
		Cohort cohort = tbQuery.getNumerator(hdsd.getStartDate(), hdsd.getEndDate());
		
		DataSetRow dataSet = new DataSetRow();
		dataSet.addColumnValue(new DataSetColumn("adultAndChildrenEnrolled", "Numerator", Integer.class), cohort
		        .getMemberIds().size());
		
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		set.addRow(dataSet);
		return set;
	}
	
}
