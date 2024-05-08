package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_rtt;

import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.CohortMembership;

import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.RTTQuery;
import org.openmrs.module.ohrireports.api.query.AggregateBuilder;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_rtt.TxRttByAgeAndSexDataSetDefinition;
import org.openmrs.module.ohrireports.datasetevaluator.datim.tx_new.CD4Status;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { TxRttByAgeAndSexDataSetDefinition.class })
public class TxRttByAgeAndSexDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private RTTQuery rttQuery;
	
	@Autowired
	private AggregateBuilder aggregateBuilder;
	
	private CD4Status cd4Status;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	private List<Person> personList;
	
	private Cohort remainingCohort;
	
	TxRttByAgeAndSexDataSetDefinition _datasetDefinition;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		_datasetDefinition = (TxRttByAgeAndSexDataSetDefinition) dataSetDefinition;
		cd4Status = _datasetDefinition.getCountCD4GreaterThan200();
		
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		if (_datasetDefinition.getHeader()) {
			
			remainingCohort = rttQuery.getBaseCohort();
		}
		if (!_datasetDefinition.getHeader()) {
			aggregateBuilder.clearTotal();
			if (_datasetDefinition.getCountCD4GreaterThan200().equals(CD4Status.CD4Unknown)) {
				Cohort cd4CohortWithStatus = rttQuery.getCD4ByCohort(remainingCohort,
				    _datasetDefinition.getCountCD4GreaterThan200() == CD4Status.CD4GreaterThan200,
				    rttQuery.getBaseEncounter());
				buildDataset(set, cd4CohortWithStatus);
			} else {
				Cohort cd4CohortWithStatus = rttQuery.getCD4ByCohort(rttQuery.getBaseCohort(),
				    _datasetDefinition.getCountCD4GreaterThan200() == CD4Status.CD4GreaterThan200,
				    rttQuery.getBaseEncounter());
				
				buildDataset(set, cd4CohortWithStatus);
				removeCohort(remainingCohort, cd4CohortWithStatus);
			}
		}
		
		return set;
	}
	
	private void buildDataset(SimpleDataSet set, Cohort cd4CohortWithStatus) {
		personList = rttQuery.getPersons(cd4CohortWithStatus);
		
		aggregateBuilder.setCalculateAgeFrom(_datasetDefinition.getEndDate());
		aggregateBuilder.setLowerBoundAge(0);
		aggregateBuilder.setUpperBoundAge(65);
		aggregateBuilder.setPersonList(personList);
		
		DataSetRow femaleDateSet = new DataSetRow();
		aggregateBuilder.buildDataSetColumn(femaleDateSet, "F");
		set.addRow(femaleDateSet);
		
		DataSetRow maleDataSet = new DataSetRow();
		aggregateBuilder.buildDataSetColumn(maleDataSet, "M");
		set.addRow(maleDataSet);
		
		DataSetRow totalSet = new DataSetRow();
		aggregateBuilder.buildDataSetColumn(totalSet, "T");
		set.addRow(totalSet);
	}
	
	private void removeCohort(Cohort cohort, Cohort toBeRemoved) {
		for (CohortMembership cohortMember : toBeRemoved.getMemberships()) {
			cohort.removeMembership(cohortMember);
		}
	}
}
