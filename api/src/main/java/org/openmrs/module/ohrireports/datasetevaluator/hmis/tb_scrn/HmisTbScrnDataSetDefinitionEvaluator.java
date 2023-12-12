package org.openmrs.module.ohrireports.datasetevaluator.hmis.tb_scrn;

import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_1_NAME;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_2_NAME;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.TBQuery;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.tb_scrn.HmisTbScrnDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { HmisTbScrnDataSetDefinition.class })
public class HmisTbScrnDataSetDefinitionEvaluator implements DataSetEvaluator {

	private EvaluationContext context;
	private String column_3_name = "Number";
	@Autowired
	private TBQuery tbQuery;
	@Autowired
	private EncounterQuery encounterQuery;

	private HmisTbScrnDataSetDefinition hdsd;


	List<Person> persons = new ArrayList<>();

	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext)
			throws EvaluationException {

		hdsd = (HmisTbScrnDataSetDefinition) dataSetDefinition;
		context = evalContext;
		SimpleDataSet data = new SimpleDataSet(dataSetDefinition, context);

		List<Integer> encounters = encounterQuery.getAliveFollowUpEncounters(hdsd.getStartDate(),hdsd.getEndDate());
		Cohort newOnARTCohort = tbQuery.getNewOnArtCohort("", hdsd.getStartDate(), hdsd.getEndDate(), null,encounters);
		Cohort existingOnARTCohort = new Cohort(tbQuery.getArtStartedCohort("",null, hdsd.getEndDate(),
				null, newOnARTCohort,encounters));

		Cohort newOnARTScreenedCohort = tbQuery.getTBScreenedCohort(newOnARTCohort, encounters);
		persons = tbQuery.getPersons(newOnARTScreenedCohort);

		data.addRow(
				buildRow("HIV_TB_SCRN",
						"Proportion of patients enrolled in HIV care who were screened for TB (FD)", String.class,
						" "));

		data.addRow(buildRow("HIV_TB_SCRN.1",
				"Number of NEWLY Enrolled ART clients who were screened for TB during the reporting period",
				Integer.class, persons.size()));

		data.addRow(buildRow("HIV_TB_SCRN.1.1", "< 15 years, Male", Integer.class,
				gettbscrnByAgeAndGender(0, 15, Gender.Male)));

		data.addRow(buildRow("HIV_TB_SCRN.1.2", "< 15 years, female", Integer.class,
				gettbscrnByAgeAndGender(0, 15, Gender.Female)));
		data.addRow(buildRow("HIV_TB_SCRN.1.3", ">= 15 years, Male", Integer.class,
				gettbscrnByAgeAndGender(15, 150, Gender.Male)));
		data.addRow(buildRow("HIV_TB_SCRN.1.4", ">= 15 years, female", Integer.class,
				gettbscrnByAgeAndGender(15, 150, Gender.Female)));

		Cohort newOnARTScreenedPositiveCohort = tbQuery.getCohortByTbScreenedPositive(newOnARTCohort, "");

		persons = tbQuery.getPersons(newOnARTScreenedPositiveCohort);

		data.addRow(buildRow("HIV_TB_SCRN_P", "Screened Positive for TB", Integer.class,
		 persons.size()));
		data.addRow(buildRow("HIV_TB_SCRN_P.1", "< 15 years, Male", Integer.class,
				gettbscrnByAgeAndGender(0, 15, Gender.Male)));
		data.addRow(buildRow("HIV_TB_SCRN_P.2", "< 15 years, female", Integer.class,
				gettbscrnByAgeAndGender(0, 15, Gender.Female)));
		data.addRow(buildRow("HIV_TB_SCRN_P.3", ">= 15 years, Male", Integer.class,
				gettbscrnByAgeAndGender(15, 150, Gender.Male)));
		data.addRow(buildRow("HIV_TB_SCRN_P.4", ">= 15 years, female", Integer.class,
				gettbscrnByAgeAndGender(15, 150, Gender.Female)));
		
		Cohort existingScreenedOnArtCohort = tbQuery.getTBScreenedCohort(existingOnARTCohort,encounters);
		persons = tbQuery
				.getPersons(existingScreenedOnArtCohort);

		data.addRow(buildRow("HIV_TB_SCRN_ART", "Number of PLHIVs PREVIOUSLY on ART and screened for TB",
				Integer.class, persons.size()));

		data.addRow(buildRow("HIV_TB_SCRN_ART. 1", "< 15 years, Male", Integer.class,
				gettbscrnByAgeAndGender(0, 15, Gender.Male)));
		data.addRow(buildRow("HIV_TB_SCRN_ART. 2", "< 15 years, female", Integer.class,
				gettbscrnByAgeAndGender(0, 15, Gender.Female)));
		data.addRow(buildRow("HIV_TB_SCRN_ART. 3", ">= 15 years, Male", Integer.class,
				gettbscrnByAgeAndGender(15, 150, Gender.Male)));
		data.addRow(buildRow("HIV_TB_SCRN_ART. 4", ">= 15 years, female", Integer.class,
				gettbscrnByAgeAndGender(15, 150, Gender.Female)));

		Cohort existingScreenedPositiveCohort = tbQuery.getCohortByTbScreenedPositive(existingScreenedOnArtCohort, "");
		persons = tbQuery.getPersons(existingScreenedPositiveCohort);

		data.addRow(buildRow("HIV_TB_SCRN_ART_P", "Screened Positive for TB", Integer.class, persons.size()));
		data.addRow(buildRow("HIV_TB_SCRN_ART_P. 1", "< 15 years, Male", Integer.class,
				gettbscrnByAgeAndGender(0, 15, Gender.Male)));
		data.addRow(buildRow("HIV_TB_SCRN_ART_P. 2", "< 15 years, female", Integer.class,
				gettbscrnByAgeAndGender(0, 15, Gender.Female)));
		data.addRow(buildRow("HIV_TB_SCRN_ART_P. 3", ">= 15 years, Male", Integer.class,
				gettbscrnByAgeAndGender(15, 150, Gender.Male)));
		data.addRow(buildRow("HIV_TB_SCRN_ART_P. 4", ">= 15 years, female", Integer.class,
				gettbscrnByAgeAndGender(15, 150, Gender.Female)));

		return data;
	}

	private DataSetRow buildRow(String col_1_value, String col_2_value, Class<?> dataType, Object col_3_value) {
		DataSetRow setRow = new DataSetRow();
		setRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, dataType), col_1_value);
		setRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, dataType), col_2_value);
		setRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, dataType), col_3_value);
		return setRow;
	}

	private Integer gettbscrnByAgeAndGender(int minAge, int maxAge, Gender gender) {
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

}

enum Gender {
	Female,
	Male
}