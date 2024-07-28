package org.openmrs.module.ohrireports.datasetevaluator.hmis.art_tpt_cr_1;

import static org.openmrs.module.ohrireports.constants.RegimentConstant.*;

import java.util.*;

import org.openmrs.Cohort;
import org.openmrs.Person;

import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.TBQuery;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.*;

@Component
@Scope("prototype")
public class HMISARTTPTCrOneEvaluator {


	@Autowired
	private TBQuery tbQuery;

	@Autowired
	private EncounterQuery encounterQuery;
	List<Person> persons = new ArrayList<>();
	private Cohort baseCohort;
	private List<Integer> baseEncounter;
	Date startDate = new Date();
	Date endDate = new Date();

	
	public void buildDataset(Date start, Date end,SimpleDataSet dataSet) {

		startDate = getSubTwelveMonth(start);
		endDate = getSubTwelveMonth(end);

		List<Integer> tptEncounterEncounter = encounterQuery.getEncounters(Collections.singletonList(FollowUpConceptQuestions.TPT_START_DATE),
				start, end);
		baseEncounter = encounterQuery.getEncounters(Collections.singletonList(FollowUpConceptQuestions.FOLLOW_UP_DATE), null, end,
				tptEncounterEncounter);

		baseCohort = new Cohort(
				tbQuery.getArtStartedCohort(baseEncounter, null, end, null));

		Cohort tptInitiatedCohort = tbQuery.getTPTCohort(baseEncounter, FollowUpConceptQuestions.TPT_START_DATE, startDate,
				endDate);
		baseCohort = new Cohort(
				tbQuery.getArtStartedCohort(baseEncounter, null, end, tptInitiatedCohort));
		
		dataSet.addRow(buildColumn("1",
				"Number of ART patients who were initiated on any course of TPT 12 months before the reporting period",
				baseCohort.size()));

		Cohort tempCohort = getTPTTreatmentBYType(TB_PROPHYLAXIS_TYPE, TB_PROPHYLAXIS_TYPE_INH);
		persons = tbQuery.getPersons(tempCohort);
		dataSet.addRow(buildColumn("1.1", "Patients on 6H 12 months prior to the reporting period", tempCohort.size()));
		dataSet.addRow(buildColumn("1.1. 1", "< 15 years, Male", gettbscrnByAgeAndGender(0, 15, Gender.Male)));
		dataSet.addRow(buildColumn("1.1. 2", "< 15 years, female", gettbscrnByAgeAndGender(0, 15, Gender.Female)));
		dataSet.addRow(buildColumn("1.1. 3", ">= 15 years, Male", gettbscrnByAgeAndGender(15, 150, Gender.Male)));
		dataSet.addRow(buildColumn("1.1. 4", ">= 15 years, female", gettbscrnByAgeAndGender(15, 150, Gender.Female)));

		tempCohort = getTPTTreatmentBYType(TB_PROPHYLAXIS_TYPE_ALTERNATE, TB_PROPHYLAXIS_TYPE_ALTERNATE_3HP);
		persons = tbQuery.getPersons(tempCohort);
		
		dataSet.addRow(buildColumn("1.2", "Patients on 3HP 12 months prior to the reporting period", tempCohort.size()));
		dataSet.addRow(buildColumn("1.2. 1", "< 15 years, Male", gettbscrnByAgeAndGender(0, 15, Gender.Male)));
		dataSet.addRow(buildColumn("1.2 2", "< 15 years, female", gettbscrnByAgeAndGender(0, 15, Gender.Female)));
		dataSet.addRow(buildColumn("1.2. 3", ">= 15 years, Male", gettbscrnByAgeAndGender(15, 150, Gender.Male)));
		dataSet.addRow(buildColumn("1.2. 4", ">= 15 years, female", gettbscrnByAgeAndGender(15, 150, Gender.Female)));

		tempCohort = getTPTTreatmentBYType(TB_PROPHYLAXIS_TYPE_ALTERNATE, TB_PROPHYLAXIS_TYPE_ALTERNATE_3HR);
		persons = tbQuery.getPersons(tempCohort);
		
		dataSet.addRow(buildColumn("1.3", "Patients on 3HR 12 months prior to the reporting period", tempCohort.size()));
		dataSet.addRow(buildColumn("1.3. 1", "< 15 years, Male", gettbscrnByAgeAndGender(0, 15, Gender.Male)));
		dataSet.addRow(buildColumn("1.3. 2", "< 15 years, female", gettbscrnByAgeAndGender(0, 15, Gender.Female)));
		dataSet.addRow(buildColumn("1.3. 3", ">= 15 years, Male", gettbscrnByAgeAndGender(15, 150, Gender.Male)));
		dataSet.addRow(buildColumn("1.3. 4", ">= 15 years, female", gettbscrnByAgeAndGender(15, 150, Gender.Female)));

	}

	private Date getSubTwelveMonth(Date date) {
		Calendar newDate = Calendar.getInstance();
		newDate.setTime(date);
		newDate.add(Calendar.MONTH, -12);
		return newDate.getTime();
	}

	private DataSetRow buildColumn(String col_1_value, String col_2_value, Integer col_3_value) {
		DataSetRow hivCxcarxDataSetRow = new DataSetRow();
		String baseName = "HIV_ART_TPT_CR.";
		hivCxcarxDataSetRow.addColumnValue(
				new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class),
				baseName + "" + col_1_value);
		hivCxcarxDataSetRow.addColumnValue(
				new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), col_2_value);
		
		String COLUMN_3_NAME = "Number";
		hivCxcarxDataSetRow.addColumnValue(new DataSetColumn(COLUMN_3_NAME, COLUMN_3_NAME, Integer.class),
				col_3_value);

		return hivCxcarxDataSetRow;
	}

	private Integer gettbscrnByAgeAndGender(int minAge, int maxAge, Gender gender) {
		int _age = 0;
		List<Integer> patients = new ArrayList<>();
		String _gender = gender.equals(Gender.Female) ? "f" : "m";
		if (maxAge > 1) {
			maxAge = maxAge + 1;
		}

		for (Person person : persons) {
			if (!(patients.contains(person.getPersonId())) && person.getGender().toLowerCase().equals(_gender)) {

				_age = person.getAge();
				if ((_age >= minAge && _age < maxAge)) {

				}

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