package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_tb_numerator;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.TBQuery;
import org.openmrs.module.ohrireports.api.query.AggregateBuilder;
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
	private AggregateBuilder _AggregateBuilder;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		hdsd = (TxTbNumeratorARTByAgeAndSexDataSetDefinition) dataSetDefinition;
		
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		Cohort cohort = tbQuery.getNumerator(hdsd.getStartDate(), hdsd.getEndDate());
		
		Cohort newOnArtCohort = tbQuery.getNewOnArtCohort("", hdsd.getStartDate(), hdsd.getEndDate(), cohort,
		    tbQuery.getBaseEncounter());
		Cohort alreadyOnArtCohort = new Cohort(tbQuery.getArtStartedCohort(tbQuery.getBaseEncounter(), null,
		    hdsd.getStartDate(), cohort));
		
		_AggregateBuilder.setCalculateAgeFrom(hdsd.getEndDate());
		_AggregateBuilder.setLowerBoundAge(0);
		_AggregateBuilder.setUpperBoundAge(65);
		buildRowWithAggregate(set, newOnArtCohort,
		    "Number of patients starting TB treatment who newly started ART during the reporting period:");
		buildRowWithAggregate(set, alreadyOnArtCohort,
		    "Number of patients starting TB treatment who were already on ART prior to the start of the reporting period:");
		
		return set;
	}
	
	private void buildRowWithAggregate(SimpleDataSet set, Cohort cohort, String type) {
		Cohort femaleCohort = tbQuery.getTBTreatmentStartedCohort(cohort, "F", tbQuery.getBaseEncounter());
		
		Cohort maleCohort = tbQuery.getTBTreatmentStartedCohort(cohort, "M", tbQuery.getBaseEncounter());
		
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
