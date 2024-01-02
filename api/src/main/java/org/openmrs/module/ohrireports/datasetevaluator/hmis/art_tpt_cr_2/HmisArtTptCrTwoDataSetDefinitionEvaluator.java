package org.openmrs.module.ohrireports.datasetevaluator.hmis.art_tpt_cr_2;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;
import static org.openmrs.module.ohrireports.RegimentConstant.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.TBQuery;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.art_tpt_cr_2.HmisArtTptCrTwoDataSetDefinition;
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

@Handler(supports = { HmisArtTptCrTwoDataSetDefinition.class })
public class HmisArtTptCrTwoDataSetDefinitionEvaluator implements DataSetEvaluator {

	private String baseName = "HIV_ART_TPT_CR.";
	private String COLUMN_3_NAME = "Number";
	private HmisArtTptCrTwoDataSetDefinition _dataSetDefinition;

	List<Person> persons = new ArrayList<>();
	@Autowired
	private TBQuery tbQuery;
	private Cohort baseCohort;
	Date startDate = new Date();
	Date endDate = new Date();

	@Autowired
	private EncounterQuery encounterQuery;
	private List<Integer> baseEncounter;

	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext)
			throws EvaluationException {

		_dataSetDefinition = (HmisArtTptCrTwoDataSetDefinition) dataSetDefinition;
		startDate = getSubTwelveMonth(_dataSetDefinition.getStartDate());
		endDate = getSubTwelveMonth(_dataSetDefinition.getEndDate());

		SimpleDataSet data = new SimpleDataSet(dataSetDefinition, evalContext);
		baseEncounter = getBaseEncounter();

		baseCohort = getBaseCohort(baseEncounter);

		data.addRow(buildColumn("2",
				"Number of ART patients who started TPT 12 months prior to the reporting period that completed a full course of therapy",
				baseCohort.size()));
		Cohort tempCohort = getTPTTreatmentBYType(TB_PROPHYLAXIS_TYPE, TB_PROPHYLAXIS_TYPE_INH);
		persons = tbQuery.getPersons(tempCohort);
		data.addRow(buildColumn("2.1", "Patients who completed 6H", tempCohort.size()));
		data.addRow(buildColumn("2.1. 1", "< 15 years, Male", getTPTScreenByAgeAndGender(0, 15, Gender.Male)));
		data.addRow(buildColumn("2.1. 2", "< 15 years, female", getTPTScreenByAgeAndGender(0, 15, Gender.Female)));
		data.addRow(buildColumn("2.1. 3", ">= 15 years, Male", getTPTScreenByAgeAndGender(15, 150, Gender.Male)));
		data.addRow(buildColumn("2.1. 4", ">= 15 years, female", getTPTScreenByAgeAndGender(15, 150, Gender.Female)));

		tempCohort = getTPTTreatmentBYType(TB_PROPHYLAXIS_TYPE_ALTERNATE, TB_PROPHYLAXIS_TYPE_ALTERNATE_3HP);
		persons = tbQuery.getPersons(tempCohort);

		data.addRow(buildColumn("2.2", "Patients who completed 3HP", tempCohort.size()));
		data.addRow(buildColumn("2.2. 1", "< 15 years, Male", getTPTScreenByAgeAndGender(0, 15, Gender.Male)));
		data.addRow(buildColumn("2.2. 2", "< 15 years, female", getTPTScreenByAgeAndGender(0, 15, Gender.Female)));
		data.addRow(buildColumn("2.2. 3", ">= 15 years, Male", getTPTScreenByAgeAndGender(15, 150, Gender.Male)));
		data.addRow(buildColumn("2.2. 4", ">= 15 years, female", getTPTScreenByAgeAndGender(15, 150, Gender.Female)));

		tempCohort = getTPTTreatmentBYType(TB_PROPHYLAXIS_TYPE_ALTERNATE, TB_PROPHYLAXIS_TYPE_ALTERNATE_3HR);
		persons = tbQuery.getPersons(tempCohort);

		data.addRow(buildColumn("2.3", "Patients who completed 3HR", tempCohort.size()));
		data.addRow(buildColumn("2.3. 1", "< 15 years, Male", getTPTScreenByAgeAndGender(0, 15, Gender.Male)));
		data.addRow(buildColumn("2.3. 2", "< 15 years, female", getTPTScreenByAgeAndGender(0, 15, Gender.Female)));
		data.addRow(buildColumn("2.3. 3", ">= 15 years, Male", getTPTScreenByAgeAndGender(15, 150, Gender.Male)));
		data.addRow(buildColumn("2.3. 4", ">= 15 years, female", getTPTScreenByAgeAndGender(15, 150, Gender.Female)));

		return data;
	}

	private Cohort getBaseCohort(List<Integer> baseEncounter) {
		Cohort _baseCohort = new Cohort(
				tbQuery.getArtStartedCohort(baseEncounter, null, _dataSetDefinition.getEndDate(), null));

		Cohort tptInitiatedCohort = tbQuery.getTPTCohort(baseEncounter, TPT_START_DATE, startDate,
				endDate);
		baseCohort = new Cohort(
				tbQuery.getArtStartedCohort(baseEncounter, null, _dataSetDefinition.getEndDate(), tptInitiatedCohort));

		Cohort tptCompleteCohort = tbQuery.getTPTByCompletedConceptCohort(baseEncounter, baseCohort);

		_baseCohort = new Cohort(
				tbQuery.getArtStartedCohort(baseEncounter, null, _dataSetDefinition.getEndDate(), tptCompleteCohort));
		return _baseCohort;
	}

	private List<Integer> getBaseEncounter() {
		List<Integer> tptEncounterEncounter = encounterQuery.getEncounters(Arrays.asList(TPT_START_DATE),
				_dataSetDefinition.getStartDate(), _dataSetDefinition.getEndDate());
		baseEncounter = encounterQuery.getEncounters(Arrays.asList(FOLLOW_UP_DATE), null,
				_dataSetDefinition.getEndDate(),
				tptEncounterEncounter);
		Cohort cohort = tbQuery.getCohort(baseEncounter);

		List<Integer> tptCompletedEncounter = encounterQuery.getEncounters(Arrays.asList(TPT_COMPLETED_DATE),
				null, _dataSetDefinition.getEndDate(), cohort);

		return tptCompletedEncounter;

	}

	private Date getSubTwelveMonth(Date date) {
		Calendar newDate = Calendar.getInstance();
		newDate.setTime(date);
		newDate.add(Calendar.MONTH, -12);
		return newDate.getTime();
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

	private Integer getTPTScreenByAgeAndGender(int minAge, int maxAge, Gender gender) {
		int _age = 0;
		List<Integer> patients = new ArrayList<>();
		String _gender = gender.equals(Gender.Female) ? "f" : "m";
		if (maxAge > 1) {
			maxAge = maxAge + 1;
		}

		for (Person person : persons) {
			if (!(patients.contains(person.getPersonId())) && person.getGender().toLowerCase().equals(_gender)) {

				_age = person.getAge(_dataSetDefinition.getEndDate());
				if (_age >= minAge && _age < maxAge) {

					patients.add(person.getPersonId());

				}
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