package org.openmrs.module.ohrireports.datasetevaluator.datim.prep_new;

import java.util.Arrays;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.module.ohrireports.api.impl.query.PreExposureProphylaxisQuery;
import org.openmrs.module.ohrireports.api.query.AggregateBuilder;
import org.openmrs.module.ohrireports.datasetdefinition.datim.pr_ep_new.AutoCalculatePrepNewDataSetDefinition;
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

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Handler(supports = { AutoCalculatePrepNewDataSetDefinition.class })
public class PrepNewAutoCalculateDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	ConceptService conceptService;
	
	@Autowired
	EvaluationService evaluationService;
	
	@Autowired
	private PreExposureProphylaxisQuery preExposureProphylaxisQuery;
	
	private AutoCalculatePrepNewDataSetDefinition auCDataSetDefinition;
	
	private EvaluationContext context;
	
	//private Concept tdfConcept, tdf_ftcConcept, tdf3tcConcept, prEpStatedConcept;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		auCDataSetDefinition = (AutoCalculatePrepNewDataSetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		
		if (!auCDataSetDefinition.getHeader()) {
			preExposureProphylaxisQuery.setStartDate(auCDataSetDefinition.getStartDate());
			preExposureProphylaxisQuery.setEndDate(auCDataSetDefinition.getEndDate());
			
			context = evalContext;
			
			DataSetRow dRow = new DataSetRow();
			Cohort cohort = preExposureProphylaxisQuery.getAllNewPrEP();
			
			dRow.addColumnValue(new DataSetColumn("Numerator", "Numerator", Integer.class), cohort.size());
			dataSet.addRow(dRow);
		}
		return dataSet;
	}
}
