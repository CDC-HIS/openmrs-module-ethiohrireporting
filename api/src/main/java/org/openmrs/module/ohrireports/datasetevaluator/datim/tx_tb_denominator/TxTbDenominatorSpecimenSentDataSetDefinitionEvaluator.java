package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_tb_denominator;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.ALIVE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.FOLLOW_UP_STATUS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.RESTART;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TREATMENT_END_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.ARV_DISPENSED_IN_DAYS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.TBQuery;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_tb_denominator.TxTbDenominatorSpecimenSentDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { TxTbDenominatorSpecimenSentDataSetDefinition.class })
public class TxTbDenominatorSpecimenSentDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	private TxTbDenominatorSpecimenSentDataSetDefinition hdsd;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	@Autowired
	private TBQuery tbQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		hdsd = (TxTbDenominatorSpecimenSentDataSetDefinition) dataSetDefinition;
		
		List<Integer> encounter = encounterQuery.getAliveFollowUpEncounters(hdsd.getEndDate());
		tbQuery.setEncountersByScreenDate(encounter);
		
		Cohort cohort = tbQuery.getActiveOnArtCohort("UNDERNOURISHED", null, hdsd.getEndDate(), null, encounter);
		
		DataSetRow dataSet = new DataSetRow();
		dataSet.addColumnValue(new DataSetColumn("", "", String.class),
		    "Number of ART patients who had a specimen sent for bacteriological diagnosis of active TB disease");
		dataSet.addColumnValue(new DataSetColumn("num", "Num", Integer.class),
		    tbQuery.getSpecimenSent(cohort, hdsd.getStartDate(), hdsd.getEndDate()).getMemberIds().size());
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		set.addRow(dataSet);
		return set;
	}
	
}
