package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_tb_denominator;

import java.util.List;
import org.openmrs.annotation.Handler;
import org.openmrs.Cohort;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.TBQuery;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_tb_denominator.TxTbDenominatorPositiveResultReturnedDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { TxTbDenominatorPositiveResultReturnedDataSetDefinition.class })
public class TxTbDenominatorPositiveResultReturnedDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private TBQuery tbQuery;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	private TxTbDenominatorPositiveResultReturnedDataSetDefinition hdsd;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		hdsd = (TxTbDenominatorPositiveResultReturnedDataSetDefinition) dataSetDefinition;
		
		List<Integer> encounter = encounterQuery.getAliveFollowUpEncounters(hdsd.getEndDate());
		tbQuery.setEncountersByScreenDate(encounter);
		
		Cohort cohort = tbQuery.getActiveOnArtCohort("", null, hdsd.getEndDate(), null, encounter);
		DataSetRow dataSet = new DataSetRow();
		dataSet.addColumnValue(new DataSetColumn("", "", String.class),
		    "Number of ART patients who had a positive result returned for bacteriological diagnosis of active TB disease");
		dataSet.addColumnValue(new DataSetColumn("num", "Num", Integer.class),
		    tbQuery.getTBDiagnosticPositiveResult(cohort, hdsd.getStartDate(), hdsd.getEndDate()).size());
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		set.addRow(dataSet);
		return set;
	}
	
}
