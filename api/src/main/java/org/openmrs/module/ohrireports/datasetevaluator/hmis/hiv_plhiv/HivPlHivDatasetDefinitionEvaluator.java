package org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_plhiv;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.hiv_plhiv.HivPlHivDatasetDefinition;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.Gender;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.FOLLOW_UP_DATE;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_1_NAME;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_2_NAME;

@Handler(supports = { HivPlHivDatasetDefinition.class })
public class HivPlHivDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private HivPlvHivQuery hivPlvHivQuery;

	private String baseName = "HIV_PLHIV_TSP. ";
	private String COLUMN_3_NAME = "Number";

	List<Person> personList = new ArrayList<>();
	
	@Autowired
	private EncounterQuery encounterQuery;
	private HivPlHivDatasetDefinition _datasetDefinition;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {

		_datasetDefinition = (HivPlHivDatasetDefinition) dataSetDefinition;

		hivPlvHivQuery.setStartDate(_datasetDefinition.getStartDate());
		hivPlvHivQuery.setEndDate(_datasetDefinition.getEndDate(), FOLLOW_UP_DATE);

		Cohort plhivCohort = hivPlvHivQuery.getAllPLHIVMalnutrition(hivPlvHivQuery.getBaseEncounter());

		SimpleDataSet dataSet = new SimpleDataSet(_datasetDefinition, evalContext);


		personList = hivPlvHivQuery.getPersons(plhivCohort);
		dataSet.addRow(buildColumn("", "Proportion of clinically undernourished People Living with HIV (PLHIV)" +
				" who received therapeutic or supplementary food", personList.size()));
		dataSet.addRow(buildColumn("HIV_PLHIV_TSP.1", "Number of PLHIV who were assessed/screened for malnutrition", personList.size()));

		dataSet.addRow(buildColumn("HIV_PLHIV_TSP.1.1", "< 15 years, Male", getCohortSizeByAgeAndGender(0, 15, Gender.Male)));
		dataSet.addRow(buildColumn("HIV_PLHIV_TSP.1.2", "< 15 years, Female", getCohortSizeByAgeAndGender(0, 15, Gender.Female)));
		dataSet.addRow(buildColumn("HIV_PLHIV_TSP.1.3", ">= 15 years, Male", getCohortSizeByAgeAndGender(15, 150, Gender.Male)));
		dataSet.addRow(buildColumn("HIV_PLHIV_TSP.1.4", ">= 15 years, Female", getCohortSizeByAgeAndGender(15, 150, Gender.Female)));

		dataSet.addRow(buildColumn("HIV_PLHIV_NUT", "Number of PLHIV who were nutritionally assessed" +
				"and found to be clinically undernourished (disaggregated by Age, Sex and Pregnancy)", 0));

		dataSet.addRow(buildColumn("HIV_PLHIV_NUT_MAM", "Number of PLHIV who were assessed/screened for malnutrition", personList.size()));
		dataSet.addRow(buildColumn("HIV_PLHIV_NUT_MAM.1", "< 15 years, Mal", personList.size()));
		dataSet.addRow(buildColumn("HIV_PLHIV_NUT_MAM.2", "< 15 years, Female", personList.size()));
		dataSet.addRow(buildColumn("HIV_PLHIV_NUT_MAM.3", ">= 15 years, Male", personList.size()));
		dataSet.addRow(buildColumn("HIV_PLHIV_NUT_MAM.4", ">= 15 years, Female", personList.size()));




		
		return dataSet;
	}

	private DataSetRow buildColumn(String col_1_value, String col_2_value, Integer col_3_value) {
		DataSetRow prepDataSetRow = new DataSetRow();
		prepDataSetRow.addColumnValue(
				new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class),
				 col_1_value);
		prepDataSetRow.addColumnValue(
				new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), col_2_value);

		prepDataSetRow.addColumnValue(new DataSetColumn(COLUMN_3_NAME, COLUMN_3_NAME, Integer.class),
				col_3_value);

		return prepDataSetRow;
	}

	private Integer getCohortSizeByAgeAndGender(int minAge, int maxAge, Gender gender) {
		int _age = 0;
		List<Integer> patients = new ArrayList<>();
		String _gender = gender.equals(gender.Female) ? "f" : "m";
		if (maxAge > 1) {
			maxAge = maxAge + 1;
		}
		for (Person person : personList) {

			_age = person.getAge(_datasetDefinition.getEndDate());

			if (!patients.contains(person.getPersonId())
					&& (_age >= minAge && _age < maxAge)
					&& (person.getGender().toLowerCase().equals(_gender))) {

				patients.add(person.getPersonId());

			}
		}
		return patients.size();
	}

}
