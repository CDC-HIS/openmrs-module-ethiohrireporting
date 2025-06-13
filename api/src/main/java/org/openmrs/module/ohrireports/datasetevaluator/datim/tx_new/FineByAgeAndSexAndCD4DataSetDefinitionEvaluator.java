package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_new;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.ObjectUtils;
import org.openmrs.Cohort;
import org.openmrs.CohortMembership;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.TXNewQuery;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_new.FineByAgeAndSexAndCD4DataSetDefinition;
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

@Handler(supports = { FineByAgeAndSexAndCD4DataSetDefinition.class })
public class FineByAgeAndSexAndCD4DataSetDefinitionEvaluator implements DataSetEvaluator {
	
	private FineByAgeAndSexAndCD4DataSetDefinition hdsd;
	
	private int total = 0;
	
	private List<Person> unkownPersons;
	
	public int getcD4Total() {
		return cD4Total;
	}
	
	public void setcD4Total(int cD4Total) {
		this.cD4Total = this.cD4Total + cD4Total;
	}
	
	private int cD4Total = 0;
	
	public void clearTotal() {
		this.cD4Total = 0;
	}
	
	@Autowired
	TXNewQuery txNewQuery;
	
	private int minCount = 0;
	
	private int maxCount = 4;
	
	private CD4Status cd4Status;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		total = 0;
		hdsd = (FineByAgeAndSexAndCD4DataSetDefinition) dataSetDefinition;
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);

		SimpleDataSet dataSet1 = EthiOhriUtil.isValidReportDateRange(hdsd.getStartDate(),
				hdsd.getEndDate(), set);
		if (dataSet1 != null) return dataSet1;

		cd4Status = hdsd.getCountCD4GreaterThan200();
		if (hdsd.getHeader()) {

			unkownPersons = new ArrayList<>();
			unkownPersons.addAll(txNewQuery.getPersonList());

		}

		if (!hdsd.getHeader()) {
			
			clearTotal();
			if (hdsd.getCountCD4GreaterThan200().equals(CD4Status.CD4Unknown)) {

				DataSetRow femaleCD4StatusDataset = new DataSetRow();
				buildDataSet(femaleCD4StatusDataset, "F", unkownPersons);
				set.addRow(femaleCD4StatusDataset);
				
				DataSetRow maleCD4StatusDataset = new DataSetRow();
				buildDataSet(maleCD4StatusDataset, "M", unkownPersons);
				set.addRow(maleCD4StatusDataset);
				
				DataSetRow totalCD4Status = new DataSetRow();
				buildDataSet(totalCD4Status, "T", null);
				set.addRow(totalCD4Status);
			} else {

				Cohort cd4ByCohort = txNewQuery.getCD4ByCohort(txNewQuery.getBaseCohort(),
				    hdsd.getCountCD4GreaterThan200() == CD4Status.CD4GreaterThan200, txNewQuery.getBaseEncounter());
				List<Person> list = txNewQuery.getPersons(cd4ByCohort);

				DataSetRow femaleCD4StatusDataset = new DataSetRow();
				buildDataSet(femaleCD4StatusDataset, "F", list);
				set.addRow(femaleCD4StatusDataset);


				DataSetRow maleCD4StatusDataset = new DataSetRow();
				buildDataSet(maleCD4StatusDataset, "M", list);
				set.addRow(maleCD4StatusDataset);

				
				DataSetRow totalCD4Status = new DataSetRow();
				buildDataSet(totalCD4Status, "T", null);
				set.addRow(totalCD4Status);
			}
			
		}
		
		return set;
	}
	
	private void buildDataSet(DataSetRow dataSet, String gender, List<Person> persons) {
		if (Objects.equals(gender, "F") || Objects.equals(gender, "M")) {
			total = 0;
			minCount = cd4Status == CD4Status.CD4Unknown ? 1 : 5;
			maxCount = cd4Status == CD4Status.CD4Unknown ? 4 : 9;
			
			dataSet.addColumnValue(new DataSetColumn("FineByAgeAndSexData", "Gender", Integer.class),
			    gender.equals("F") ? "Female" : "Male");
			
			dataSet.addColumnValue(new DataSetColumn("unknownAge", "Unknown Age", Integer.class),
			    getEnrolledByUnknownAge(persons, gender));
			
			if (cd4Status == CD4Status.CD4Unknown)
				dataSet.addColumnValue(new DataSetColumn("<1", "<1", Integer.class),
				    getEnrolledBelowOneYear(unkownPersons, gender));
			
			while (minCount <= 65) {
				if (minCount == 65) {
					dataSet.addColumnValue(new DataSetColumn("65+", "65+", Integer.class),
					    getEnrolledByAgeAndGender(65, 200, persons, gender));
				} else {
					dataSet.addColumnValue(new DataSetColumn(minCount + "-" + maxCount, minCount + "-" + maxCount,
					        Integer.class), getEnrolledByAgeAndGender(minCount, maxCount, persons, gender));
				}
				minCount = maxCount + 1;
				maxCount = minCount + 4;
			}
			dataSet.addColumnValue(new DataSetColumn("Sub-total", "Subtotal", Integer.class), total);
			setcD4Total(total);
		} else if (Objects.equals(gender, "T")) {
			dataSet.addColumnValue(new DataSetColumn("FineByAgeAndSexData", "Sub-total", Integer.class), "Sub-total");
			dataSet.addColumnValue(new DataSetColumn("unknownAge", "Unknown Age", Integer.class), getcD4Total());
		}
	}
	
	private int getEnrolledByAgeAndGender(int min, int max, List<Person> persons,String gender) {
        int count = 0;
        int _age = 0;
        List<Integer> personIds = new ArrayList<>();
        for (Person person : persons) {
            _age = person.getAge(hdsd.getEndDate());
            if (personIds.contains(person.getPersonId()))
                continue;

            if (_age >= min && _age <= max && person.getGender().equals(gender)) {
                personIds.add(person.getPersonId());
                count++;
            }
        }
        incrementTotalCount(count);
        clearCountedPerson(personIds, persons);

        return count;
    }
	
	private int getEnrolledByUnknownAge(List<Person> persons,String gender) {
        int count = 0;
        int _age = 0;

        List<Integer> personIds = new ArrayList<>();
        for (Person person : persons) {
            _age = person.getAge(hdsd.getEndDate());

            if (personIds.contains(person.getPersonId()))
                continue;

            if (_age <= 0 && person.getGender().equals(gender)) {
                count++;
                personIds.add(person.getPersonId());
            }

        }
        incrementTotalCount(count);
        clearCountedPerson(personIds, persons);
        return count;
    }
	
	private int getEnrolledBelowOneYear(List<Person> persons,String gender) {
        int count = 0;
        int _age = 0;
        List<Integer> personIds = new ArrayList<>();
        for (Person person : persons) {
            _age = person.getAge(hdsd.getEndDate());

            if (personIds.contains(person.getPersonId()))
                continue;

            if (_age < 1 && person.getGender().equals(gender)) {
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
			unkownPersons.removeIf(p -> p.getPersonId() == pId);

        }
	}
}
