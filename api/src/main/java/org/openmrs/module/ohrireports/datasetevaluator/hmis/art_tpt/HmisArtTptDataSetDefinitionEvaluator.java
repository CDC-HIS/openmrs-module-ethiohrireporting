package org.openmrs.module.ohrireports.datasetevaluator.hmis.art_tpt;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;
import static org.openmrs.module.ohrireports.RegimentConstant.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.TBQuery;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.art_tpt.HmisArtTptDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.*;

@Handler(supports = { HmisArtTptDataSetDefinition.class })
public class HmisArtTptDataSetDefinitionEvaluator implements DataSetEvaluator {

	private String baseName = "HIV_ART_TPT";
	private String COLUMN_3_NAME = "Number";

	private HmisArtTptDataSetDefinition _dataSetDefinition;

	@Autowired
	private TBQuery tbQuery;


	@Autowired
	private EncounterQuery encounterQuery;
	List<Person> persons = new ArrayList<>();
	private Cohort baseCohort;
	private List<Integer> baseEncounter;

	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext)
			throws EvaluationException {

		_dataSetDefinition = (HmisArtTptDataSetDefinition) dataSetDefinition;
		SimpleDataSet data = new SimpleDataSet(dataSetDefinition, evalContext);
		List<Integer> tptEncounterEncounter = encounterQuery.getEncounters(Arrays.asList(TPT_START_DATE),
				_dataSetDefinition.getStartDate(), _dataSetDefinition.getEndDate());
		baseEncounter = encounterQuery.getEncounters(Arrays.asList(FOLLOW_UP_DATE), null, _dataSetDefinition.getEndDate(),
				tptEncounterEncounter);
		baseCohort = new Cohort(
				tbQuery.getArtStartedCohort(baseEncounter, null, _dataSetDefinition.getEndDate(), null));

		data.addRow(buildColumn(".1",
				"Number of ART patients who started on a standard course of TB Preventive Treatment (TPT) in the reporting period",
				baseCohort.size()));
		Cohort tempCohort = getTPTTreatmentBYType(TB_PROPHYLAXIS_TYPE, TB_PROPHYLAXIS_TYPE_INH);
		persons = tbQuery.getPersons(tempCohort);

		data.addRow(buildColumn("_6H", "Patients on 6H", tempCohort.size()));
		data.addRow(buildColumn("_6H. 1", "< 15 years, Male", getTPTscreenByAgeAndGender(0, 15, Gender.Male)));
		data.addRow(buildColumn("_6H. 2", "< 15 years, female", getTPTscreenByAgeAndGender(0, 15, Gender.Female)));
		data.addRow(buildColumn("_6H. 3", ">= 15 years, Male", getTPTscreenByAgeAndGender(15, 150, Gender.Male)));
		data.addRow(buildColumn("_6H. 4", ">= 15 years, female", getTPTscreenByAgeAndGender(15, 150, Gender.Female)));

		tempCohort = getTPTTreatmentBYType(TB_PROPHYLAXIS_TYPE_ALTERNATE, TB_PROPHYLAXIS_TYPE_ALTERNATE_3HP);
		persons = tbQuery.getPersons(tempCohort);

		data.addRow(buildColumn("_3HP", "Patients on 3HP", tempCohort.size()));
		data.addRow(buildColumn("_3HP. 1", "< 15 years, Male", getTPTscreenByAgeAndGender(0, 15, Gender.Male)));
		data.addRow(buildColumn("_3HP. 2", "< 15 years, female", getTPTscreenByAgeAndGender(0, 15, Gender.Female)));
		data.addRow(buildColumn("_3HP. 3", ">= 15 years, Male", getTPTscreenByAgeAndGender(15, 150, Gender.Male)));
		data.addRow(buildColumn("_3HP. 4", ">= 15 years, female", getTPTscreenByAgeAndGender(15, 150, Gender.Female)));

		tempCohort = getTPTTreatmentBYType(TB_PROPHYLAXIS_TYPE_ALTERNATE, TB_PROPHYLAXIS_TYPE_ALTERNATE_3HR);
		persons = tbQuery.getPersons(tempCohort);

		data.addRow(buildColumn("_3HR", "Patients on 3HR", tempCohort.size()));
		data.addRow(buildColumn("_3HR. 1", "< 15 years, Male", getTPTscreenByAgeAndGender(0, 15, Gender.Male)));
		data.addRow(buildColumn("_3HR. 2", "< 15 years, female", getTPTscreenByAgeAndGender(0, 15, Gender.Female)));
		data.addRow(buildColumn("_3HR. 3", ">= 15 years, Male", getTPTscreenByAgeAndGender(15, 150, Gender.Male)));
		data.addRow(buildColumn("_3HR. 4", ">= 15 years, female", getTPTscreenByAgeAndGender(15, 150, Gender.Female)));

		return data;
	}

	private DataSetRow buildColumn(String col_1_value, String col_2_value, Integer col_3_value) {
		DataSetRow dataRow = new DataSetRow();
		dataRow.addColumnValue(
				new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class),
				baseName + "" + col_1_value);
		dataRow.addColumnValue(
				new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), col_2_value);

		dataRow.addColumnValue(new DataSetColumn(COLUMN_3_NAME, COLUMN_3_NAME, Integer.class),
				col_3_value);

		return dataRow;
	}

	private Integer getTPTscreenByAgeAndGender(int minAge, int maxAge, Gender gender) {
		int _age = 0;
		List<Integer> patients = new ArrayList<>();
		String _gender = gender.equals(Gender.Female) ? "f" : "m";
		if (maxAge > 1) {
			maxAge = maxAge + 1;
		}

		for (Person person : persons) {
			_age = person.getAge();
			if (!patients.contains(person.getPersonId())
					&& (_age >= minAge && _age < maxAge)
					&& (person.getGender().toLowerCase().equals(_gender))) {

				patients.add(person.getPersonId());

			}
		}
		return patients.size();
	}

	public Cohort getTPTTreatmentBYType(String question, String answer) {

		return tbQuery.getTPTByConceptCohort(baseEncounter, baseCohort, question, Arrays.asList(answer));

	}

}

enum Gender {
	Female,
	Male
}