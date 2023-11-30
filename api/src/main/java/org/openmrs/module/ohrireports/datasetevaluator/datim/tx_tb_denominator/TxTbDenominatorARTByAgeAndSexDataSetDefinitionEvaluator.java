package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_tb_denominator;

import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.TBQuery;
import org.openmrs.module.ohrireports.api.query.AggregateBuilder;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_tb_denominator.TxTbDenominatorARTByAgeAndSexDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { TxTbDenominatorARTByAgeAndSexDataSetDefinition.class })
public class TxTbDenominatorARTByAgeAndSexDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	private TxTbDenominatorARTByAgeAndSexDataSetDefinition hdsd;
	
	@Autowired
	private TBQuery tbQuery;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	@Autowired
	private AggregateBuilder _AggregateBuilder;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		hdsd = (TxTbDenominatorARTByAgeAndSexDataSetDefinition) dataSetDefinition;
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		
		List<Integer> encounters = encounterQuery.getAliveFollowUpEncounters(hdsd.getEndDate());
		tbQuery.setEncountersByScreenDate(encounters);
		
		Cohort newOnArtCohort = tbQuery.getNewOnArtCohort("", hdsd.getStartDate(), hdsd.getEndDate(), null, encounters);
		Cohort alreadyOnArtCohort = tbQuery.getActiveOnArtCohort("", null, hdsd.getEndDate(), null, encounters);
		_AggregateBuilder.setCalculateAgeFrom(hdsd.getEndDate());
		
		buildRowWithAggregate(set, newOnArtCohort, "New On ART");
		buildRowWithAggregate(set, alreadyOnArtCohort, "Already On ART");
		
		return set;
	}
	
	private void buildRowWithAggregate(SimpleDataSet set, Cohort cohort, String type) {
		Cohort femalePositiveCohort = tbQuery.getCohortByTbScreenedPositive(cohort, "F");
		Cohort femaleNegativeCohort = tbQuery.getCohortByTbScreenedNegative(cohort, "F");
		
		Cohort malePositiveCohort = tbQuery.getCohortByTbScreenedPositive(cohort, "M");
		Cohort maleNegativeCohort = tbQuery.getCohortByTbScreenedNegative(cohort, "M");
		
		DataSetRow positiveDescriptionDsRow = new DataSetRow();
		positiveDescriptionDsRow.addColumnValue(new DataSetColumn("", "Category", String.class), type + "/Screen Positive ");
		set.addRow(positiveDescriptionDsRow);
		
		_AggregateBuilder.setPersonList(tbQuery.getPersons(femalePositiveCohort));
		DataSetRow _femalePositive = new DataSetRow();
		_AggregateBuilder.buildDataSetColumn(_femalePositive, "F");
		set.addRow(_femalePositive);
		
		_AggregateBuilder.setPersonList(tbQuery.getPersons(malePositiveCohort));
		DataSetRow _malePositive = new DataSetRow();
		_AggregateBuilder.buildDataSetColumn(_malePositive, "M");
		set.addRow(_malePositive);
		
		DataSetRow negativeDescriptionDsRow = new DataSetRow();
		negativeDescriptionDsRow.addColumnValue(new DataSetColumn("", "Category", String.class), type + "/Screen Negative ");
		set.addRow(negativeDescriptionDsRow);
		
		_AggregateBuilder.setPersonList(tbQuery.getPersons(femaleNegativeCohort));
		DataSetRow _femaleNegative = new DataSetRow();
		_AggregateBuilder.buildDataSetColumn(_femaleNegative, "F");
		set.addRow(_femaleNegative);
		
		_AggregateBuilder.setPersonList(tbQuery.getPersons(maleNegativeCohort));
		DataSetRow _maleNegative = new DataSetRow();
		_AggregateBuilder.buildDataSetColumn(_maleNegative, "M");
		set.addRow(_maleNegative);
	}
	
}
