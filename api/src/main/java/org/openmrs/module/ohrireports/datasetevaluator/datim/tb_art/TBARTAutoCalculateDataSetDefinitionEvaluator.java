package org.openmrs.module.ohrireports.datasetevaluator.datim.tb_art;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.TBARTQuery;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tb_art.TBARTAutoCalculateDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { TBARTAutoCalculateDataSetDefinition.class })
public class TBARTAutoCalculateDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private TBARTQuery tbQuery;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		TBARTAutoCalculateDataSetDefinition _datasetDefinition = (TBARTAutoCalculateDataSetDefinition) dataSetDefinition;
		Cohort activeTbCohort = tbQuery.getCohortByTBTreatmentStartDate(_datasetDefinition.getStartDate(),
		    _datasetDefinition.getEndDate());
		
		DataSetRow dataSet = new DataSetRow();
		dataSet.addColumnValue(new DataSetColumn("auto-calculate", "Numerator", Integer.class), activeTbCohort.size());
		
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		
		set.addRow(dataSet);
		return set;
	}
	
}
