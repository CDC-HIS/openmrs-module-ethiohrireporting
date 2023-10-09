package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_new;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_new.CoarseByAgeAndSexDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { CoarseByAgeAndSexDataSetDefinition.class })
public class CoarseByAgeAndSexDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	private CoarseByAgeAndSexDataSetDefinition hdsd;
	
	private int total = 0;
	
	private PatientQueryService patientQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		total = 0;
		hdsd = (CoarseByAgeAndSexDataSetDefinition) dataSetDefinition;
		
		patientQuery = Context.getService(PatientQueryService.class);
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		DataSetRow femaleDateSet = new DataSetRow();
		Cohort femalCohort = patientQuery.getOnArtCohorts("F", hdsd.getStartDate(), hdsd.getEndDate(), null);
		
		List<Person> persons = patientQuery.getPersons(femalCohort);
		
		femaleDateSet.addColumnValue(new DataSetColumn("FineByAgeAndSexData", "Gender", Integer.class), "Female");
		femaleDateSet.addColumnValue(new DataSetColumn("unkownAge", "Unkown Age", Integer.class),
		    getEnrolledByUnknownAge(persons));
		femaleDateSet.addColumnValue(new DataSetColumn("<15", "<15", Integer.class),
		    getEnrolledByAgeAndGender(0, 15, persons));
		femaleDateSet.addColumnValue(new DataSetColumn("15+", "15+", Integer.class),
		    getEnrolledByAgeAndGender(15, 200, persons));
		
		set.addRow(femaleDateSet);
		
		persons.clear();
		Cohort maleCohort = patientQuery.getOnArtCohorts("M", hdsd.getStartDate(), hdsd.getEndDate(), null);
		persons = patientQuery.getPersons(maleCohort);
		DataSetRow maleDataSet = new DataSetRow();
		maleDataSet.addColumnValue(new DataSetColumn("FineByAgeAndSexData", "Gender", Integer.class), "Male");
		maleDataSet.addColumnValue(new DataSetColumn("unkownAge", "Unknown Age", Integer.class),
		    getEnrolledByUnknownAge(persons));
		
		maleDataSet
		        .addColumnValue(new DataSetColumn("<15", "<15", Integer.class), getEnrolledByAgeAndGender(0, 15, persons));
		maleDataSet.addColumnValue(new DataSetColumn("15+", "15+", Integer.class),
		    getEnrolledByAgeAndGender(15, 200, persons));
		
		set.addRow(maleDataSet);
		DataSetRow tSetRow = new DataSetRow();
		tSetRow.addColumnValue(new DataSetColumn("subtotal", "Sub-Total", Integer.class), total);
		set.addRow(tSetRow);
		return set;
	}
	
	private int getEnrolledByUnknownAge(List<Person> persons) {
		int count = 0;
		int _age = 0;
		List<Integer> personIds = new ArrayList<>();
		for (Person person : persons) {
			_age = person.getAge(hdsd.getStartDate());

			if (personIds.contains(person.getPersonId()))
				continue;

			if (Objects.isNull(_age) ||
					_age <= 0) {
				count++;
				personIds.add(person.getPersonId());
			}

		}
		incrementTotalCount(count);
		for (int pId : personIds) {
			persons.removeIf(p -> p.getPersonId() == pId);
		}
		return count;
	}
	
	private int getEnrolledByAgeAndGender(int min, int max, List<Person> persons) {
		int count = 0;
		int _age = 0;
		List<Integer> personIds = new ArrayList<>();
		for (Person person : persons) {
			_age = person.getAge(hdsd.getStartDate());

			if (personIds.contains(person.getPersonId()))
				continue;

			if (_age >= min &&
					_age <= max) {
				personIds.add(person.getPersonId());
				count++;
			}

		}
		incrementTotalCount(count);

		for (int pId : personIds) {
			persons.removeIf(p -> p.getPersonId() == pId);
		}

		return count;
	}
	
	private void incrementTotalCount(int count) {
		if (count > 0)
			total = total + count;
	}
	
}
