package org.openmrs.module.ohrireports.datasetevaluator.hmis.art_tpt_cr_2;

import static org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions.TPT_COMPLETED_DATE;
import static org.openmrs.module.ohrireports.constants.RegimentConstant.*;

import java.util.*;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.TBQuery;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.art_tpt_cr_1.HMISARTTPTCrOneEvaluator;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;

import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.*;

@Component
@Scope("prototype")
public class HMISArtTptCrTwoEvaluator {
	
	private Date start, end;
	List<Person> persons = new ArrayList<>();
	@Autowired
	private TBQuery tbQuery;
	private Cohort baseCohort;
	Date startDate = new Date();
	Date endDate = new Date();

	@Autowired
	private HMISARTTPTCrOneEvaluator hmisarttptCrOneEvaluator;

	
	public void buildDataset(Date start,Date end,SimpleDataSet data) {

		baseCohort = tbQuery.getTPTCohort(hmisarttptCrOneEvaluator.getBaseCohort(),hmisarttptCrOneEvaluator.getBaseEncounter(),TPT_COMPLETED_DATE);

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

	}

//	private Cohort getBaseCohort(List<Integer> baseEncounter) {
//		Cohort _baseCohort = new Cohort(
//				tbQuery.getArtStartedCohort(baseEncounter, null, end, null));
//
//		Cohort tptInitiatedCohort = tbQuery.getTPTCohort(baseEncounter, FollowUpConceptQuestions.TPT_START_DATE, startDate,
//				endDate);
//		baseCohort = new Cohort(
//				tbQuery.getArtStartedCohort(baseEncounter, null, end, tptInitiatedCohort));
//
//		Cohort tptCompleteCohort = tbQuery.getTPTByCompletedConceptCohort(baseEncounter, baseCohort);
//
//		_baseCohort = new Cohort(
//				tbQuery.getArtStartedCohort(baseEncounter, null, end, tptCompleteCohort));
//		return _baseCohort;
//	}

//	private List<Integer> getBaseEncounter() {
//		List<Integer> tptEncounterEncounter = encounterQuery.getEncounters(Collections.singletonList(FollowUpConceptQuestions.TPT_START_DATE),
//				start,end);
//		baseEncounter = encounterQuery.getEncounters(Collections.singletonList(FollowUpConceptQuestions.FOLLOW_UP_DATE), null,
//				end,
//				tptEncounterEncounter);
//		Cohort cohort = tbQuery.getCohort(baseEncounter);
//
//		return encounterQuery.getEncounters(Collections.singletonList(FollowUpConceptQuestions.TPT_COMPLETED_DATE),
//				null, end, cohort);
//
//	}

	private Date getSubTwelveMonth(Date date) {
		Calendar newDate = Calendar.getInstance();
		newDate.setTime(date);
		newDate.add(Calendar.MONTH, -12);
		return newDate.getTime();
	}

	private DataSetRow buildColumn(String col_1_value, String col_2_value, Integer col_3_value) {
		DataSetRow dataRow = new DataSetRow();
		String baseName = "HIV_ART_TPT_CR.";
		dataRow.addColumnValue(
				new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class),
				baseName + col_1_value);
		dataRow.addColumnValue(
				new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), col_2_value);
		
		String COLUMN_3_NAME = "Number";
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

				_age = person.getAge(end);
				if (_age >= minAge && _age < maxAge) {

					patients.add(person.getPersonId());

				}
			}

		}

		for (Integer pa:patients){
			persons.removeIf(p->
                    Objects.equals(p.getPersonId(), pa)
            );

		}

		return patients.size();
	}

	public Cohort getTPTTreatmentBYType(String question, String answer) {

		return tbQuery.getTPTByConceptCohort(hmisarttptCrOneEvaluator.getBaseEncounter(), hmisarttptCrOneEvaluator.getBaseCohort(), question, Arrays.asList(answer));

	}

}

enum Gender {
	Female,
	Male
}