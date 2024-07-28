package org.openmrs.module.ohrireports.datasetevaluator.datim.pmtct_art;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.pmtct.ARTQuery;
import org.openmrs.module.ohrireports.datasetdefinition.datim.pmtct_art.PMTCTARTDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { PMTCTARTDataSetDefinition.class })
public class PMTCTARTDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	private EvaluationContext context;
	
	private int total = 0;
	
	private int minCount = 0;
	
	private int maxCount = 4;
	
	private PMTCTARTDataSetDefinition hdsd;
	
	private List<Person> pmtctARTPersonList;
	
	@Autowired
	private ARTQuery artQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		hdsd = (PMTCTARTDataSetDefinition) dataSetDefinition;
		context = evalContext;
		
		artQuery.setStartDate(hdsd.getStartDate());
		artQuery.setEndDate(hdsd.getEndDate());
		
		SimpleDataSet simpleDataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		if (hdsd.getPmtctType().equals("NEW_ON_ART")) {
			pmtctARTPersonList = artQuery.getPersons(artQuery.getNewOnARTPMTCTARTCohort());
		} else {
			pmtctARTPersonList = artQuery.getPersons(artQuery.getAlreadyOnARTPMTCTARTCohort());
		}
		
		buildDataSet(simpleDataSet);
		
		return simpleDataSet;
		
	}
	
	private void buildDataSet(SimpleDataSet simpleDataSet) {
		DataSetRow setRow = new DataSetRow();
		buildDataSet(setRow, "F");
		simpleDataSet.addRow(setRow);
	}
	
	private void buildDataSet(DataSetRow dataSet, String gender) {
		total = 0;
		minCount = 1;
		maxCount = 4;
		
		dataSet.addColumnValue(new DataSetColumn("ByAgeAndSexData", "Gender", Integer.class), gender.equals("F") ? "Female"
		        : "Male");
		dataSet.addColumnValue(new DataSetColumn("unknownAge", "Unknown Age", Integer.class),
		    getEnrolledByUnknownAge(pmtctARTPersonList));
		
		dataSet.addColumnValue(new DataSetColumn("<1", "Below One (<1)", Integer.class),
		    getEnrolledBelowOneYear(pmtctARTPersonList));
		
		while (minCount <= 65) {
			if (minCount == 65) {
				dataSet.addColumnValue(new DataSetColumn("65+", "65+", Integer.class),
				    getEnrolledByAgeAndGender(65, 200, pmtctARTPersonList));
			} else {
				dataSet.addColumnValue(
				    new DataSetColumn(minCount + "-" + maxCount, minCount + "-" + maxCount, Integer.class),
				    getEnrolledByAgeAndGender(minCount, maxCount, pmtctARTPersonList));
			}
			minCount = maxCount + 1;
			maxCount = minCount + 4;
		}
		dataSet.addColumnValue(new DataSetColumn("Sub-total", "Subtotal", Integer.class), total);
	}
	
	private int getEnrolledByAgeAndGender(int min, int max, List<Person> persons) {
        int count = 0;
        int _age = 0;
        List<Integer> personIds = new ArrayList<>();
        for (Person person : persons) {
            _age = person.getAge(hdsd.getEndDate());
            if (personIds.contains(person.getPersonId()))
                continue;

            if (_age >= min && _age <= max) {
                personIds.add(person.getPersonId());
                count++;
            }
        }
        incrementTotalCount(count);
        clearCountedPerson(personIds, persons);
        return count;
    }
	
	private int getEnrolledByUnknownAge(List<Person> persons) {
        int count = 0;
        int _age = 0;

        List<Integer> personIds = new ArrayList<>();
        for (Person person : persons) {
            _age = person.getAge(hdsd.getEndDate());

            if (personIds.contains(person.getPersonId()))
                continue;

            if (Objects.isNull(_age) ||
                    _age <= 0) {
                count++;
                personIds.add(person.getPersonId());
            }

        }
        incrementTotalCount(count);
        clearCountedPerson(personIds, persons);
        return count;
    }
	
	private int getEnrolledBelowOneYear(List<Person> persons) {
        int count = 0;
        int _age = 0;
        List<Integer> personIds = new ArrayList<>();
        for (Person person : persons) {
            _age = person.getAge(hdsd.getEndDate());

            if (personIds.contains(person.getPersonId()))
                continue;

            if (_age < 1) {
                count++;
                personIds.add(person.getPersonId());
            }
        }
        incrementTotalCount(count);
        clearCountedPerson(personIds, persons);
        return count;
    }
	
	private void incrementTotalCount(int count) {
		if (count > 0)
			total = total + count;
	}
	
	private void clearCountedPerson(List<Integer> personIds, List<Person> persons) {
        for (int pId : personIds) {
            persons.removeIf(p -> p.getPersonId() == pId);
        }
    }
}
