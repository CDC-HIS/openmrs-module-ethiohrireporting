package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_tb_numerator;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.ART_START_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.ALIVE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.FOLLOW_UP_STATUS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.RESTART;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TREATMENT_END_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.ARV_DISPENSED_IN_DAYS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TB_TREATMENT_START_DATE;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.TBQuery;
import org.openmrs.module.ohrireports.api.query.AggregateBuilder;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_tb_numerator.TxTbNumeratorARTByAgeAndSexDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { TxTbNumeratorARTByAgeAndSexDataSetDefinition.class })
public class TxTbNumeratorARTByAgeAndSexDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	private TxTbNumeratorARTByAgeAndSexDataSetDefinition hdsd;
	
	@Autowired
	private TBQuery tbQuery;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	@Autowired
	private AggregateBuilder _AggregateBuilder;
	
	private List<Integer> encounters;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		hdsd = (TxTbNumeratorARTByAgeAndSexDataSetDefinition) dataSetDefinition;
		
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		encounters = encounterQuery.getAliveFollowUpEncounters(hdsd.getEndDate());
		tbQuery.setEncountersByScreenDate(encounters);
		
		Cohort newOnArtCohort = tbQuery.getNewOnArtCohort("", hdsd.getStartDate(), hdsd.getEndDate(), null, encounters);
		Cohort alreadyOnArtCohort = tbQuery.getActiveOnArtCohort("", null, hdsd.getEndDate(), null, encounters);
		
		_AggregateBuilder.setCalculateAgeFrom(hdsd.getEndDate());
		
		buildRowWithAggregate(set, newOnArtCohort,
		    "Number of patients starting TB treatment who newly started ART during the reporting period:");
		buildRowWithAggregate(set, alreadyOnArtCohort,
		    "Number of patients starting TB treatment who were already on ART prior to the start of the reporting period:");
		
		return set;
	}
	
	private void buildRowWithAggregate(SimpleDataSet set, Cohort cohort, String type) {
		Cohort femaleCohort = tbQuery.getTBTreatmentStartedCohort(cohort, hdsd.getStartDate(), hdsd.getEndDate(), "F",
		    encounters);
		
		Cohort maleCohort = tbQuery.getTBTreatmentStartedCohort(cohort, hdsd.getStartDate(), hdsd.getEndDate(), "M",
		    encounters);
		
		DataSetRow positiveDescriptionDsRow = new DataSetRow();
		positiveDescriptionDsRow.addColumnValue(new DataSetColumn("", "Category", String.class), type);
		set.addRow(positiveDescriptionDsRow);
		_AggregateBuilder.setPersonList(tbQuery.getPersons(femaleCohort));
		
		DataSetRow _femalePositive = new DataSetRow();
		_AggregateBuilder.buildDataSetColumn(_femalePositive, "F");
		set.addRow(_femalePositive);
		_AggregateBuilder.setPersonList(tbQuery.getPersons(maleCohort));
		
		DataSetRow _malePositive = new DataSetRow();
		_AggregateBuilder.buildDataSetColumn(_malePositive, "M");
		set.addRow(_malePositive);
	}
	
}
