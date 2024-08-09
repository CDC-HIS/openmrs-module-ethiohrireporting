package org.openmrs.module.ohrireports.datasetevaluator.hmis.pr_ep;

import static org.openmrs.module.ohrireports.constants.ETHIOHRIReportsConstants.*;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_1_NAME;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_2_NAME;

import java.util.*;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.module.ohrireports.constants.EncounterType;
import org.openmrs.module.ohrireports.constants.PrepConceptQuestions;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.Gender;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class HIVPREPEvaluator {
	@Autowired
	private HivPrEpQuery hivPrEPQuery;
	private final Set<Integer> patientIds = new HashSet<>();
	private String baseName = "HIV_PrEP. ";
	private String COLUMN_3_NAME = "Number";
	private EvaluationContext context;
	List<Person> personList = new ArrayList<>();
	private Date start,end;
	private  int indexHeader =0;

	public void buildDataset(Date start,Date end,SimpleDataSet dataset) {
		this.start= start;
		this.end = end; 
	
		hivPrEPQuery.setStartDate(start);
		hivPrEPQuery.setEndDate(end, PrepConceptQuestions.PREP_SCREENED_DATE, EncounterType.PREP_SCREENING_ENCOUNTER_TYPE);


		Cohort prepScreeningCohort = hivPrEPQuery
				.getCohortByConceptAndBaseEncounter(PrepConceptQuestions.PREP_SCREENED_DATE);

		dataset.addRow(buildColumn("", "Number of individuals receiving Pre-Exposure Prophylaxis",""));
		dataset.addRow(buildColumn("1", "PrEP (New Number of individuals who were newly enrolled on PrEP", ""));


		personList = hivPrEPQuery.getPersons(prepScreeningCohort);
		dataset.addRow(buildColumn("1.1", "By Age and Sex",prepScreeningCohort.size()));
		dataset.addRow(buildColumn("1.1 .1", "15 - 19 years, Male", getCohortSizeByAgeAndGender(15, 19, Gender.Male)));
		dataset.addRow(buildColumn("1.1 .2", "15 - 19 years, Female", getCohortSizeByAgeAndGender(15, 19, Gender.Female)));
		dataset.addRow(buildColumn("1.1 .3", "20 - 24 years, Male", getCohortSizeByAgeAndGender(20, 24, Gender.Male)));
		dataset.addRow(buildColumn("1.1 .4", "20 - 24 years, Female", getCohortSizeByAgeAndGender(20, 24, Gender.Female)));
		dataset.addRow(buildColumn("1.1 .5", "25 - 29 years, Male", getCohortSizeByAgeAndGender(25, 29, Gender.Male)));
		dataset.addRow(buildColumn("1.1 .6", "25 - 29 years, Female", getCohortSizeByAgeAndGender(25, 29, Gender.Female)));
		dataset.addRow(buildColumn("1.1 .7", "30 - 34 years, Male", getCohortSizeByAgeAndGender(30, 34, Gender.Male)));
		dataset.addRow(buildColumn("1.1 .8", "30 - 34 years, Female", getCohortSizeByAgeAndGender(30, 34, Gender.Female)));
		dataset.addRow(buildColumn("1.1 .9", "35 - 39 years, Male", getCohortSizeByAgeAndGender(35, 39, Gender.Male)));
		dataset.addRow(buildColumn("1.1 .10", "35 - 39 years, Female", getCohortSizeByAgeAndGender(35, 39, Gender.Female)));
		dataset.addRow(buildColumn("1.1 .11", "40 - 44 years, Male", getCohortSizeByAgeAndGender(40, 44, Gender.Male)));
		dataset.addRow(buildColumn("1.1 .12", "40 - 44 years, Female", getCohortSizeByAgeAndGender(40, 44, Gender.Female)));
		dataset.addRow(buildColumn("1.1 .13", "45 - 49 years, Male", getCohortSizeByAgeAndGender(45, 49, Gender.Male)));
		dataset.addRow(buildColumn("1.1 .14", "45 - 49 years, Female", getCohortSizeByAgeAndGender(45, 49, Gender.Female)));
		dataset.addRow(buildColumn("1.1 .15", ">=50 years, Male", getCohortSizeByAgeAndGender(50, 150, Gender.Male)));
		dataset.addRow(buildColumn("1.1 .16", ">=50 years, Female", getCohortSizeByAgeAndGender(50, 150, Gender.Female)));

		int total = 0;
		Cohort fsw = hivPrEPQuery.getCategoryOnPrep(PrepConceptQuestions.FEMALE_SEX_WORKER, prepScreeningCohort);
		Cohort discordantCouple = hivPrEPQuery.getCategoryOnPrep(DISCORDANT_COUPLE, prepScreeningCohort);
		total = fsw.size() + discordantCouple.size();
		
		DataSetRow clientCategoryRow = new DataSetRow();
		
		clientCategoryRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), "HIV_PrEP.1.2");
		clientCategoryRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), "By Client Category");
		clientCategoryRow.addColumnValue(new DataSetColumn(COLUMN_3_NAME, COLUMN_3_NAME, Integer.class), total);
	
		dataset.addRow(clientCategoryRow);
	
		DataSetRow discordantCoupleCategoryRow = new DataSetRow();

		discordantCoupleCategoryRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), "HIV_PrEP.1.2.1");
		discordantCoupleCategoryRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), "Discordant Couple");
		discordantCoupleCategoryRow.addColumnValue(new DataSetColumn(COLUMN_3_NAME, COLUMN_3_NAME, Integer.class), discordantCouple.size());
		
		dataset.addRow(discordantCoupleCategoryRow);
		
		DataSetRow fwCategoryRow = new DataSetRow();

		fwCategoryRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), "HIV_PrEP.1.2.2");
		fwCategoryRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), "Female sex worker[FSW]");
		fwCategoryRow.addColumnValue(new DataSetColumn(COLUMN_3_NAME, COLUMN_3_NAME, Integer.class), fsw.size());

		dataset.addRow(fwCategoryRow);

	}

	private DataSetRow buildColumn(String col_1_value, String col_2_value, Integer col_3_value) {
		DataSetRow prepDataSetRow = new DataSetRow();
		prepDataSetRow.addColumnValue(
				new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class),
				baseName + "" + col_1_value);
		prepDataSetRow.addColumnValue(
				new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), col_2_value);

		prepDataSetRow.addColumnValue(new DataSetColumn(COLUMN_3_NAME, COLUMN_3_NAME, Integer.class),
				col_3_value);

		return prepDataSetRow;
	}
	private DataSetRow buildColumn(String col_1_value, String col_2_value, String col_3_value) {
		DataSetRow prepDataSetRow = new DataSetRow();
		prepDataSetRow.addColumnValue(
				new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class),
				baseName + "" + col_1_value);
		prepDataSetRow.addColumnValue(
				new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), col_2_value);

		prepDataSetRow.addColumnValue(new DataSetColumn(COLUMN_3_NAME, COLUMN_3_NAME, String.class),
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

			_age = person.getAge(end);

			if (!patients.contains(person.getPersonId())
					&& (_age >= minAge && _age < maxAge)
					&& (person.getGender().toLowerCase().equals(_gender))) {

				patients.add(person.getPersonId());

			}
		}
		return patients.size();
	}

}


