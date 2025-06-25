package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_curr;

import java.util.List;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_curr.TxCurrAutoCalculateDataSetDefinition;
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

@Handler(supports = { TxCurrAutoCalculateDataSetDefinition.class })
public class TxCurrAutoCalculateDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	private TxCurrAutoCalculateDataSetDefinition hdsd;
	
	private String title = "Number of adults and children currently enrolling on antiperspirant therapy (ART)";
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		hdsd = (TxCurrAutoCalculateDataSetDefinition) dataSetDefinition;
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		
		if (!hdsd.getHeader()) {
			PatientQueryService patientQueryService = Context.getService(PatientQueryService.class);
			List<Integer> encounters = encounterQuery.getAliveFollowUpEncounters(null, hdsd.getEndDate());
			DataSetRow dataSet = new DataSetRow();
			
			dataSet.addColumnValue(new DataSetColumn("adultAndChildrenEnrolled", "Numerator", Integer.class),
			    patientQueryService.getActiveOnArtCohort("", null, hdsd.getEndDate(), null, encounters).size());
			set.addRow(dataSet);
		}
		return set;
	}
}
