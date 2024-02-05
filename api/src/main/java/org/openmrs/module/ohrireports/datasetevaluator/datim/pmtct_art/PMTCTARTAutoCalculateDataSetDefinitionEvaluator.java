package org.openmrs.module.ohrireports.datasetevaluator.datim.pmtct_art;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.ART_START_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TB_DIAGNOSTIC_TEST_RESULT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TREATMENT_END_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TB_SCREENING_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.POSITIVE;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.module.ohrireports.api.impl.query.pmtct.ARTQuery;
import org.openmrs.module.ohrireports.datasetdefinition.datim.pmtct_art.PMTCTARTAutoCalculateDataSetDefinition;
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

@Handler(supports = { PMTCTARTAutoCalculateDataSetDefinition.class })
public class PMTCTARTAutoCalculateDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	private EvaluationContext context;
	
	private PMTCTARTAutoCalculateDataSetDefinition hdsd;
	
	@Autowired
	private EvaluationService evaluationService;
	
	@Autowired
	private ARTQuery artQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		hdsd = (PMTCTARTAutoCalculateDataSetDefinition) dataSetDefinition;
		context = evalContext;
		
		artQuery.setStartDate(hdsd.getStartDate());
		artQuery.setEndDate(hdsd.getEndDate());
		
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		DataSetRow dataSet = new DataSetRow();
		dataSet.addColumnValue(new DataSetColumn("Numerator", "Numerator", Integer.class), artQuery.getPmtctARTCohort()
		        .size());
		set.addRow(dataSet);
		return set;
	}
}
