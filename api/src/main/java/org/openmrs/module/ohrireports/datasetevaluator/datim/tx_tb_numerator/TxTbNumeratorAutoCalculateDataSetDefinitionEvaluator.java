package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_tb_numerator;

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

@Handler(supports = { TxTbNumeratorAutoCalculateDataSetDefinition.class })
public class TxTbNumeratorAutoCalculateDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	private TxTbNumeratorAutoCalculateDataSetDefinition hdsd;
	
	@Autowired
	private TBQuery tbQuery;
	
	private List<Integer> encounters;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		hdsd = (TxTbNumeratorAutoCalculateDataSetDefinition) dataSetDefinition;
		
		encounters = encounterQuery.getAliveFollowUpEncounters(hdsd.getEndDate());
		tbQuery.setEncountersByScreenDate(encounters);
		
		Cohort cohort = tbQuery.getActiveOnArtCohort("", null, hdsd.getEndDate(), null, encounters);
		
		cohort = tbQuery.getTBTreatmentStartedCohort(cohort, hdsd.getStartDate(), hdsd.getEndDate(), null, encounters);
		
		DataSetRow dataSet = new DataSetRow();
		dataSet.addColumnValue(new DataSetColumn("adultAndChildrenEnrolled", "Numerator", Integer.class), cohort
		        .getMemberIds().size());
		
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		set.addRow(dataSet);
		return set;
	}
	
}
