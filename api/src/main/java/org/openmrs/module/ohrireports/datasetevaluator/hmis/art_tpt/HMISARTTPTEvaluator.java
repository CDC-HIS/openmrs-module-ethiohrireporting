package org.openmrs.module.ohrireports.datasetevaluator.hmis.art_tpt;

import static org.openmrs.module.ohrireports.constants.RegimentConstant.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.TBQuery;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.Gender;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.*;

@Component
@Scope("prototype")
public class HMISARTTPTEvaluator {
	
	@Autowired
	private TBQuery tbQuery;


	@Autowired
	private EncounterQuery encounterQuery;
	List<Person> persons = new ArrayList<>();
	private Cohort baseCohort;
	private List<Integer> baseEncounter;

	
	public void buildDataset(Date start, Date end,SimpleDataSet dataset){
		
	
		List<Integer> tptEncounterEncounter = encounterQuery.getEncounters(Arrays.asList(FollowUpConceptQuestions.TPT_START_DATE),
				start, end);
		baseEncounter = encounterQuery.getEncounters(Arrays.asList(FollowUpConceptQuestions.FOLLOW_UP_DATE), null, end,
				tptEncounterEncounter);
		baseCohort = new Cohort(
				tbQuery.getArtStartedCohort(baseEncounter, null, end, null));
		
		dataset.addRow(buildColumn(".1",
				"Number of ART patients who started on a standard course of TB Preventive Treatment (TPT) in the reporting period",
				baseCohort.size()));
		Cohort tempCohort = getTPTTreatmentBYType(TB_PROPHYLAXIS_TYPE, TB_PROPHYLAXIS_TYPE_INH);
		persons = tbQuery.getPersons(tempCohort);
		
		dataset.addRow(buildColumn("_6H", "Patients on 6H", tempCohort.size()));
		dataset.addRow(buildColumn("_6H. 1", "< 15 years, Male", getTPTscreenByAgeAndGender(0, 15, Gender.Male)));
		dataset.addRow(buildColumn("_6H. 2", "< 15 years, female", getTPTscreenByAgeAndGender(0, 15, Gender.Female)));
		dataset.addRow(buildColumn("_6H. 3", ">= 15 years, Male", getTPTscreenByAgeAndGender(15, 150, Gender.Male)));
		dataset.addRow(buildColumn("_6H. 4", ">= 15 years, female", getTPTscreenByAgeAndGender(15, 150, Gender.Female)));

		tempCohort = getTPTTreatmentBYType(TB_PROPHYLAXIS_TYPE_ALTERNATE, TB_PROPHYLAXIS_TYPE_ALTERNATE_3HP);
		persons = tbQuery.getPersons(tempCohort);
		
		dataset.addRow(buildColumn("_3HP", "Patients on 3HP", tempCohort.size()));
		dataset.addRow(buildColumn("_3HP. 1", "< 15 years, Male", getTPTscreenByAgeAndGender(0, 15, Gender.Male)));
		dataset.addRow(buildColumn("_3HP. 2", "< 15 years, female", getTPTscreenByAgeAndGender(0, 15, Gender.Female)));
		dataset.addRow(buildColumn("_3HP. 3", ">= 15 years, Male", getTPTscreenByAgeAndGender(15, 150, Gender.Male)));
		dataset.addRow(buildColumn("_3HP. 4", ">= 15 years, female", getTPTscreenByAgeAndGender(15, 150, Gender.Female)));

		tempCohort = getTPTTreatmentBYType(TB_PROPHYLAXIS_TYPE_ALTERNATE, TB_PROPHYLAXIS_TYPE_ALTERNATE_3HR);
		persons = tbQuery.getPersons(tempCohort);
		
		dataset.addRow(buildColumn("_3HR", "Patients on 3HR", tempCohort.size()));
		dataset.addRow(buildColumn("_3HR. 1", "< 15 years, Male", getTPTscreenByAgeAndGender(0, 15, Gender.Male)));
		dataset.addRow(buildColumn("_3HR. 2", "< 15 years, female", getTPTscreenByAgeAndGender(0, 15, Gender.Female)));
		dataset.addRow(buildColumn("_3HR. 3", ">= 15 years, Male", getTPTscreenByAgeAndGender(15, 150, Gender.Male)));
		dataset.addRow(buildColumn("_3HR. 4", ">= 15 years, female", getTPTscreenByAgeAndGender(15, 150, Gender.Female)));

	}

	private DataSetRow buildColumn(String col_1_value, String col_2_value, Integer col_3_value) {
		DataSetRow dataRow = new DataSetRow();
		String baseName = "HIV_ART_TPT";
		dataRow.addColumnValue(
				new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class),
				baseName + "" + col_1_value);
		dataRow.addColumnValue(
				new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), col_2_value);
		
		String COLUMN_3_NAME = "Number";
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

