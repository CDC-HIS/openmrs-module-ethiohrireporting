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
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_new.FineByAgeAndSexAndCD4DataSetDefinition;
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
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	private Cohort femaleCohort;
	
	private Cohort maleCohort;
	
	private int total = 0;
	
	private List<Integer> encounter;
	
	private List<Person> persons;
	
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
	private ConceptService conceptService;
	
	private PatientQueryService patientQuery;
	
	private int minCount = 0;
	
	private int maxCount = 4;
	
	private CD4Status cd4Status;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		total = 0;
		hdsd = (FineByAgeAndSexAndCD4DataSetDefinition) dataSetDefinition;
		cd4Status = hdsd.getCountCD4GreaterThan200();
		if (hdsd.getHeader()) {
			patientQuery = Context.getService(PatientQueryService.class);
			encounter = encounterQuery.getAliveFollowUpEncounters(hdsd.getStartDate(), hdsd.getEndDate());
			femaleCohort = patientQuery.getNewOnArtCohort("F", hdsd.getStartDate(), hdsd.getEndDate(), null, encounter);
			maleCohort = patientQuery.getNewOnArtCohort("M", hdsd.getStartDate(), hdsd.getEndDate(), null, encounter);
		}
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		
		if (!hdsd.getHeader()) {
			
			clearTotal();
			if (hdsd.getCountCD4GreaterThan200().equals(CD4Status.CD4Unknown)) {
				persons = patientQuery.getPersons(femaleCohort);
				DataSetRow femaleCD4StatusDataset = new DataSetRow();
				buildDataSet(femaleCD4StatusDataset, "F", persons);
				set.addRow(femaleCD4StatusDataset);
				
				persons = patientQuery.getPersons(maleCohort);
				DataSetRow maleCD4StatusDataset = new DataSetRow();
				buildDataSet(maleCD4StatusDataset, "M", persons);
				set.addRow(maleCD4StatusDataset);
				
				DataSetRow totalCD4Status = new DataSetRow();
				buildDataSet(totalCD4Status, "T", null);
				set.addRow(totalCD4Status);
			} else {
				Cohort femaleCD4Status = patientQuery.getCD4ByCohort(femaleCohort,
				    hdsd.getCountCD4GreaterThan200() == CD4Status.CD4GreaterThan200, encounter);
				persons = patientQuery.getPersons(femaleCD4Status);
				
				DataSetRow femaleCD4StatusDataset = new DataSetRow();
				buildDataSet(femaleCD4StatusDataset, "F", persons);
				set.addRow(femaleCD4StatusDataset);
				
				removeCohort(femaleCohort, femaleCD4Status);
				
				Cohort maleCD4Status = patientQuery.getCD4ByCohort(maleCohort,
				    hdsd.getCountCD4GreaterThan200() == CD4Status.CD4GreaterThan200, encounter);
				persons = patientQuery.getPersons(maleCD4Status);
				
				DataSetRow maleCD4StatusDataset = new DataSetRow();
				buildDataSet(maleCD4StatusDataset, "M", persons);
				set.addRow(maleCD4StatusDataset);
				
				removeCohort(maleCohort, maleCD4Status);
				
				DataSetRow totalCD4Status = new DataSetRow();
				buildDataSet(totalCD4Status, "T", null);
				set.addRow(totalCD4Status);
			}
			
		}
		
		return set;
	}
	
	private void removeCohort(Cohort cohort, Cohort toBeRemoved) {
		for (CohortMembership cohortMember : toBeRemoved.getMemberships()) {
			cohort.removeMembership(cohortMember);
		}
	}
	
	private void buildDataSet(DataSetRow dataSet, String gender, List<Person> persons) {
		if (gender == "F" || gender == "M") {
			total = 0;
			minCount = cd4Status == CD4Status.CD4Unknown ? 1 : 5;
			maxCount = cd4Status == CD4Status.CD4Unknown ? 4 : 9;
			
			dataSet.addColumnValue(new DataSetColumn("FineByAgeAndSexData", "Gender", Integer.class),
			    gender.equals("F") ? "Female" : "Male");
			
			dataSet.addColumnValue(new DataSetColumn("unknownAge", "Unknown Age", Integer.class),
			    getEnrolledByUnknownAge(persons));
			
			dataSet.addColumnValue(new DataSetColumn("<1", "<1", Integer.class), getEnrolledBelowOneYear(persons));
			
			while (minCount <= 65) {
				if (minCount == 65) {
					dataSet.addColumnValue(new DataSetColumn("65+", "65+", Integer.class),
					    getEnrolledByAgeAndGender(65, 200, persons));
				} else {
					dataSet.addColumnValue(new DataSetColumn(minCount + "-" + maxCount, minCount + "-" + maxCount,
					        Integer.class), getEnrolledByAgeAndGender(minCount, maxCount, persons));
				}
				minCount = maxCount + 1;
				maxCount = minCount + 4;
			}
			dataSet.addColumnValue(new DataSetColumn("Sub-total", "Subtotal", Integer.class), total);
			setcD4Total(total);
		} else if (Objects.equals(gender, "T")) {
			dataSet.addColumnValue(new DataSetColumn("FineByAgeAndSexData", "Total", Integer.class), "Total");
			dataSet.addColumnValue(new DataSetColumn("unknownAge", "Unknown Age", Integer.class), getcD4Total());
		}
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
