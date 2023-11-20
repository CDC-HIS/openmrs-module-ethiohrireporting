package org.openmrs.module.ohrireports.datasetevaluator.datim.tb_art;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.TB_ACTIVE_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TB_TREATMENT_START_DATE;

import java.util.Arrays;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.TBQuery;
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
	
	private TBARTAutoCalculateDataSetDefinition hdsd;
	
	@Autowired
	private TBQuery tbQuery;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		hdsd = (TBARTAutoCalculateDataSetDefinition) dataSetDefinition;
		
		List<Integer> baseEncounter = encounterQuery.getEncounters(Arrays.asList(TB_TREATMENT_START_DATE, TB_ACTIVE_DATE),
		    null, hdsd.getEndDate());
		Cohort cohort = tbQuery.getActiveOnArtCohort("", null, hdsd.getEndDate(), null, baseEncounter);
		Cohort activeTBCohort = tbQuery.getCurrentOnActiveTB(cohort, hdsd.getStartDate(), hdsd.getEndDate(), baseEncounter);
		
		DataSetRow dataSet = new DataSetRow();
		dataSet.addColumnValue(new DataSetColumn("auto-calculate", "Numerator", Integer.class), activeTBCohort.size());
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		set.addRow(dataSet);
		return set;
	}
	
}
