package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_rtt;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.CohortMembership;

import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.RTTQuery;
import org.openmrs.module.ohrireports.api.query.AggregateBuilder;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_rtt.TxRttByAgeAndSexDataSetDefinition;
import org.openmrs.module.ohrireports.datasetevaluator.datim.tx_new.CD4Status;
import org.openmrs.module.ohrireports.helper.EthiOhriUtil;
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
	
	private Cohort remainingCohort;
	
	private Cohort notEligibleCohort;
	
	private List<Integer> lessThanFive = new ArrayList<Integer>();
	
	TxRttByAgeAndSexDataSetDefinition _datasetDefinition;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		_datasetDefinition = (TxRttByAgeAndSexDataSetDefinition) dataSetDefinition;
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);

		SimpleDataSet dataSet1 = EthiOhriUtil.isValidReportDateRange(_datasetDefinition.getStartDate(),
				_datasetDefinition.getEndDate(), set);
		if (dataSet1 != null) return dataSet1;

		if (_datasetDefinition.getHeader()) {
			//CD4Status cd4Status = _datasetDefinition.getCountCD4GreaterThan200();
			rttQuery.getInterruptionMonth(_datasetDefinition.getEndDate());
			//A cohort of interrupted less than six months
			notEligibleCohort = rttQuery.getInterruptionMonth(0, 6);
			//remove cohort of interrupted less than age 5
			removeCohortAgeLessThan5(notEligibleCohort);
			remainingCohort = rttQuery.getBaseCohort();
			
		}
		if (!_datasetDefinition.getHeader()) {
			aggregateBuilder.clearTotal();
			if (_datasetDefinition.getCountCD4GreaterThan200().equals(CD4Status.CD4Unknown)) {
				// remove notEligible cd4 Cohort from remainingCohort
				removeCohort(remainingCohort, notEligibleCohort);
				//add less than five year age to unknown
				lessThanFive.forEach(r->remainingCohort.addMember(r));
				buildDataset(set, remainingCohort, 0, 65);
				
			} else if (_datasetDefinition.getCountCD4GreaterThan200().equals(CD4Status.CD4NotEligible)) {
				removeCohort(remainingCohort, notEligibleCohort);
				buildDataset(set, notEligibleCohort, 4, 65);
			} else {
				Cohort cd4CohortWithStatus = rttQuery.getCD4ByCohort(rttQuery.getBaseCohort(),
				    _datasetDefinition.getCountCD4GreaterThan200() == CD4Status.CD4GreaterThan200,
				    rttQuery.getBaseEncounter());
				
				buildDataset(set, cd4CohortWithStatus, 4, 65);
				removeCohort(remainingCohort, cd4CohortWithStatus);
				removeCohort(notEligibleCohort, cd4CohortWithStatus);
			}
		}
		
		return set;
	}
	
	private void removeCohortAgeLessThan5(Cohort notEligibleCohort) {
        List<Person> personList = rttQuery.getPersons(notEligibleCohort);

        for (Person person : personList) {
            if (person.getAge(_datasetDefinition.getEndDate()) < 5) {
                notEligibleCohort.getMemberships().removeIf(c->c.getPatientId().equals(person.getPersonId()));
				lessThanFive.add(person.getPersonId() );
            }
        }
    }
	
	private void buildDataset(SimpleDataSet set, Cohort cd4CohortWithStatus, int min, int max) {
		List<Person> personList = rttQuery.getPersons(cd4CohortWithStatus);
		
		aggregateBuilder.setCalculateAgeFrom(_datasetDefinition.getEndDate());
		aggregateBuilder.setLowerBoundAge(min);
		aggregateBuilder.setUpperBoundAge(max);
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
			cohort.getMemberships().removeIf(c->c.getPatientId().equals(cohortMember.getPatientId()));
		}
	}
}
