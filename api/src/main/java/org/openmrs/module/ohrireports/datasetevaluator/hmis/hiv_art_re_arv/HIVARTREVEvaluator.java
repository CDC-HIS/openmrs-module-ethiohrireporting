package org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_art_re_arv;

import org.openmrs.Person;
import org.openmrs.module.ohrireports.api.impl.query.RTTQuery;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_1_NAME;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_2_NAME;

@Component
@Scope("prototype")
public class HIVARTREVEvaluator {
	
	private Date end;
	@Autowired
	private RTTQuery rttQuery;

	List<Person> persons = new ArrayList<>();

	public void buildDataset(Date start, Date end,SimpleDataSet dataset) {
		
		this.end= end;
		rttQuery.getRttCohort(start, end);
		persons = rttQuery.getPersons(rttQuery.getBaseCohort());

		buildDataSet(dataset);

	}

	private void buildDataSet(SimpleDataSet dataSet) {
		
		String description = "Number of ART clients restarted ARV treatment in the reporting period";
		dataSet.addRow(buildColumn("", description, Gender.All, persons.size()));

		dataSet.addRow(buildColumn(". 1", "< 15 years, Male",
				Gender.Male,
				14));

		dataSet.addRow(
				buildColumn(". 2", "< 15 years, Female", Gender.Female, 14));

		dataSet.addRow(buildColumn(". 3", ">= 15 years, Male", Gender.Male, 17));

		dataSet.addRow(
				buildColumn(". 4", ">= 15 years, Female", Gender.Female, 17));
	}

	private DataSetRow buildColumn(String index, String description, Gender gender, int age) {
		DataSetRow dataSetRow = new DataSetRow();
		String baseName = "HIV_ART_RE_ARV ";
		dataSetRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), baseName +""+ index);
		dataSetRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), description);
		String column_3_name = "Number";
		dataSetRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class), getCount(gender, age));
		return dataSetRow;
	}

	private Integer getCount(Gender gender, int age) {

		if (Gender.All == gender)
			return persons.size();

		String genderValue = gender == Gender.Male ? "M" : "F";
		List<Person> counted = new ArrayList<>();

		if (age >= 15) {
			getGreaterThanOrEqual15Age(genderValue, counted);
		} else {
			getLessThan15Age(genderValue, counted);
		}

		persons.removeAll(counted);
		return counted.size();
	}

	private void getLessThan15Age(String genderValue, List<Person> counted) {
		int age = 0;
		for (Person person : persons) {
			if (person.getGender().equals(genderValue)) {
				age = person.getAge(end);
				if (person.getAge() < 15) {
					counted.add(person);
				}
			}
		}
	}

	private void getGreaterThanOrEqual15Age(String genderValue, List<Person> counted) {
		int age = 0;
		for (Person person : persons) {
			if (person.getGender().equals(genderValue)) {
				age = person.getAge(end);
				if (person.getAge() >= 15) {
					counted.add(person);
				}
			}
		}

	}

	enum Gender {
		Female,
		Male,
		All
	}
}
