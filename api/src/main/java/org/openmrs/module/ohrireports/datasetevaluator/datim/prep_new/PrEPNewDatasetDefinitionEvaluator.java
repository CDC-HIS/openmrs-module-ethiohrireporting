package org.openmrs.module.ohrireports.datasetevaluator.datim.prep_new;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.module.ohrireports.api.impl.query.CervicalCancerQuery;
import org.openmrs.module.ohrireports.api.impl.query.PrEPNewQuery;
import org.openmrs.module.ohrireports.api.impl.query.PreExposureProphylaxisQuery;
import org.openmrs.module.ohrireports.api.query.AggregateBuilder;
import org.openmrs.module.ohrireports.datasetdefinition.datim.pr_ep_new.PrEPNewDatasetDefinition;
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

@Handler(supports = { PrEPNewDatasetDefinition.class })
public class PrEPNewDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	ConceptService conceptService;
	
	@Autowired
	EvaluationService evaluationService;
	
	private PrEPNewDatasetDefinition auCDataSetDefinition;
	
	private EvaluationContext context;
	
	private Concept tdfConcept, tdf_ftcConcept, tdf3tcConcept, prEpStatedConcept;
	
	private int total, minCount, maxCount;
	
	private List<Obs> obses;
	
	@Autowired
	private PreExposureProphylaxisQuery preExposureProphylaxisQuery;
	
	@Autowired
	private AggregateBuilder aggregateBuilder;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		auCDataSetDefinition = (PrEPNewDatasetDefinition) dataSetDefinition;
		
		context = evalContext;
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		
		aggregateBuilder.setCalculateAgeFrom(auCDataSetDefinition.getEndDate());
		Cohort cohort = preExposureProphylaxisQuery.getAllNewPrEP();
		
		List<Person> prepNewFemalePersonList = preExposureProphylaxisQuery.getPersons(preExposureProphylaxisQuery
		        .getCohortByGender("F", cohort));
		aggregateBuilder.setPersonList(prepNewFemalePersonList);
		aggregateBuilder.setLowerBoundAge(14);
		DataSetRow prEPFemaleDataSetRow = new DataSetRow();
		aggregateBuilder.buildDataSetColumn(prEPFemaleDataSetRow, "F");
		dataSet.addRow(prEPFemaleDataSetRow);
		
		List<Person> prepNewMalePersonList = preExposureProphylaxisQuery.getPersons(preExposureProphylaxisQuery
		        .getCohortByGender("M", cohort));
		aggregateBuilder.setPersonList(prepNewMalePersonList);
		aggregateBuilder.setLowerBoundAge(14);
		DataSetRow prEPMaleDataSetRow = new DataSetRow();
		aggregateBuilder.buildDataSetColumn(prEPMaleDataSetRow, "M");
		dataSet.addRow(prEPMaleDataSetRow);
		
		return dataSet;
	}
}
