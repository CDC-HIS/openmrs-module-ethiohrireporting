package org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_art_re_arv;

import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.RTTQuery;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.hiv_art_re_arv.HivArtReArvDatasetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_1_NAME;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_2_NAME;

@Handler(supports = { HivArtReArvDatasetDefinition.class })
public class HIVARTReArvDatasetDefinitionEvaluator implements DataSetEvaluator {

	private HivArtReArvDatasetDefinition _datasetDefinition;
	private String baseName = "HIV_ART_RE_ARV ";
	private String column_3_name = "Number";
	private String description = "Number of ART clients restarted ARV treatment in the reporting period";

	@Autowired
	private RTTQuery rttQuery;

	List<Person> persons = new ArrayList<>();

	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext)
			throws EvaluationException {

		_datasetDefinition = (HivArtReArvDatasetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);

		rttQuery.getRttCohort(_datasetDefinition.getStartDate(), _datasetDefinition.getEndDate());
		persons = rttQuery.getPersons(rttQuery.getBaseCohort());

		buildDataSet(dataSet);

		return dataSet;
	}

	public void buildDataSet(SimpleDataSet dataSet) {

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
		dataSetRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class),baseName+""+ index);
		dataSetRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), description);
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
				age = person.getAge(_datasetDefinition.getEndDate());
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
				age = person.getAge(_datasetDefinition.getEndDate());
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
