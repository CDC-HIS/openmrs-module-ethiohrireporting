package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_tb_denominator;

import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
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
	
	private PatientQueryService patientQuery;
	
	@Autowired
	private AggregateBuilder _AggregateBuilder;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		hdsd = (TxTbDenominatorARTByAgeAndSexDataSetDefinition) dataSetDefinition;
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		patientQuery = Context.getService(PatientQueryService.class);
		
		Cohort newOnArtCohort = patientQuery.getNewOnArtCohort("", hdsd.getStartDate(), hdsd.getEndDate(), null);
		Cohort alreadyOnArtCohort = patientQuery.getArtStartedCohort("", null, hdsd.getEndDate(), null, newOnArtCohort);
		
		_AggregateBuilder.setCalculateAgeFrom(hdsd.getEndDate());
		
		buildRowWithAggregate(set, newOnArtCohort, "New On ART");
		buildRowWithAggregate(set, alreadyOnArtCohort, "Already On ART");
		
		return set;
	}
	
	private void buildRowWithAggregate(SimpleDataSet set, Cohort cohort, String type) {
		Cohort femalePositiveCohort = tbQuery.getCohortByTbScreenedPositive(cohort, hdsd.getStartDate(), hdsd.getEndDate(),
		    "F");
		Cohort femaleNegativeCohort = tbQuery.getCohortByTbScreenedNegative(cohort, hdsd.getStartDate(), hdsd.getEndDate(),
		    "F");
		
		Cohort malePositiveCohort = tbQuery.getCohortByTbScreenedPositive(cohort, hdsd.getStartDate(), hdsd.getEndDate(),
		    "M");
		Cohort maleNegativeCohort = tbQuery.getCohortByTbScreenedNegative(cohort, hdsd.getStartDate(), hdsd.getEndDate(),
		    "M");
		
		DataSetRow positiveDescriptionDsRow = new DataSetRow();
		positiveDescriptionDsRow.addColumnValue(new DataSetColumn("", "Category", String.class), type + "/Screen Positive ");
		set.addRow(positiveDescriptionDsRow);
		
		_AggregateBuilder.setPersonList(patientQuery.getPersons(femalePositiveCohort));
		DataSetRow _femalePositive = new DataSetRow();
		_AggregateBuilder.buildDataSetColumn(_femalePositive, "F");
		set.addRow(_femalePositive);
		
		_AggregateBuilder.setPersonList(patientQuery.getPersons(malePositiveCohort));
		DataSetRow _malePositive = new DataSetRow();
		_AggregateBuilder.buildDataSetColumn(_malePositive, "M");
		set.addRow(_malePositive);
		
		DataSetRow negativeDescriptionDsRow = new DataSetRow();
		negativeDescriptionDsRow.addColumnValue(new DataSetColumn("", "Category", String.class), type + "/Screen Negative ");
		set.addRow(negativeDescriptionDsRow);
		
		_AggregateBuilder.setPersonList(patientQuery.getPersons(femaleNegativeCohort));
		DataSetRow _femaleNegative = new DataSetRow();
		_AggregateBuilder.buildDataSetColumn(_femaleNegative, "F");
		set.addRow(_femaleNegative);
		
		_AggregateBuilder.setPersonList(patientQuery.getPersons(maleNegativeCohort));
		DataSetRow _maleNegative = new DataSetRow();
		_AggregateBuilder.buildDataSetColumn(_maleNegative, "M");
		set.addRow(_maleNegative);
	}
	
}
