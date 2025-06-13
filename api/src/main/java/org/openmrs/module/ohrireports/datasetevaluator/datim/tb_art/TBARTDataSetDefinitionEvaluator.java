package org.openmrs.module.ohrireports.datasetevaluator.datim.tb_art;

import org.openmrs.Cohort;
import org.openmrs.CohortMembership;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.TBARTQuery;
import org.openmrs.module.ohrireports.api.query.AggregateBuilder;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tb_art.TBARTDataSetDefinition;
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

import java.util.List;

@Handler(supports = { TBARTDataSetDefinition.class })
public class TBARTDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private TBARTQuery tbQuery;
	
	private SimpleDataSet simpleDataSet;
	
	@Autowired
	private AggregateBuilder _AggregateBuilder;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		TBARTDataSetDefinition _datasetDefinition = (TBARTDataSetDefinition) dataSetDefinition;
		simpleDataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		
		SimpleDataSet dataSet1 = EthiOhriUtil.isValidReportDateRange(_datasetDefinition.getStartDate(),
		    _datasetDefinition.getEndDate(), simpleDataSet);
		if (dataSet1 != null)
			return dataSet1;
		
		Cohort activeTBCohort = tbQuery.getCohortByTBTreatmentStartDate(_datasetDefinition.getEndDate());
		Cohort newOnARTCohort = tbQuery.getNewOnArtCohort(activeTBCohort, _datasetDefinition.getStartDate(),
		    _datasetDefinition.getEndDate());
		Cohort alreadyOnARTCohort = getAlreadyOnARTCohort(newOnARTCohort, activeTBCohort);
		
		DataSetRow descRow = new DataSetRow();
		descRow.addColumnValue(new DataSetColumn("ByAgeAndSexData", "Gender", String.class), "Already On ART");
		simpleDataSet.addRow(descRow);
		buildRow(alreadyOnARTCohort);
		
		DataSetRow newDescRow = new DataSetRow();
		newDescRow.addColumnValue(new DataSetColumn("ByAgeAndSexData", "Gender", String.class), "New On ART");
		simpleDataSet.addRow(newDescRow);
		buildRow(newOnARTCohort);
		
		return simpleDataSet;
		
	}
	
	private void buildRow(Cohort cohort) {
		List<Person> persons = tbQuery.getPersons(cohort);
		_AggregateBuilder.setPersonList(persons);
		_AggregateBuilder.setLowerBoundAge(0);
		_AggregateBuilder.setUpperBoundAge(65);
		DataSetRow femaleSetRow = new DataSetRow();
		_AggregateBuilder.buildDataSetColumn(femaleSetRow, "F");
		simpleDataSet.addRow(femaleSetRow);
		
		DataSetRow maleSetRow = new DataSetRow();
		_AggregateBuilder.buildDataSetColumn(maleSetRow, "M");
		simpleDataSet.addRow(maleSetRow);
	}
	
	private Cohort getAlreadyOnARTCohort(Cohort newCohort, Cohort allCohort) {
		Cohort alreadyOnArtCohort = new Cohort();
		for (CohortMembership membership : allCohort.getMemberships()) {
			if (!newCohort.contains(membership.getPatientId())) {
				alreadyOnArtCohort.addMembership(membership);
			}
		}
		return alreadyOnArtCohort;
	}
}
