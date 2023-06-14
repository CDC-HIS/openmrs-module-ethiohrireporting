package org.openmrs.module.ohrireports.reports.datasetevaluator.hmis.hiv_art_ret;

import static org.openmrs.module.ohrireports.reports.datasetevaluator.hmis.HMISConstant.*;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.YES;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.NO;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.UNKNOWN;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.CohortMembership;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.query.PatientQuery;
import org.openmrs.module.ohrireports.reports.datasetdefinition.hmis.hiv_art_ret.HIVARTRETDatasetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

@Handler(supports = { HIVARTRETDatasetDefinition.class })
public class HIVARTRETDatasetDefinitionEvaluator implements DataSetEvaluator {

	private HIVARTRETDatasetDefinition _datasetDefinition;
	private String baseName = "HIV_ART_RET ";
	private String column_3_name = "Tir 15";
	private PatientQuery patientQuery;

	List<Person> persons = new ArrayList<>();

	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext)
			throws EvaluationException {
		_datasetDefinition = (HIVARTRETDatasetDefinition) dataSetDefinition;
		patientQuery = Context.getService(PatientQuery.class);
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		buildDataSet(dataSet);

		return dataSet;
	}

	public void buildDataSet(SimpleDataSet dataSet) {

		dataSet.addRow(buildColumn(" ",
				"Number of adults and children who are still on treatment at 12 months after\n" + //
						"initiating ART",
				new QueryParameter(0D, 0D, "", UNKNOWN)));

		dataSet.addRow(buildColumn(".1", "< 1 year, Male",
				new QueryParameter(0D, 0.9, "M", UNKNOWN)));

		dataSet.addRow(buildColumn(".3", "< 1 year, Female-non-pregnant",
				new QueryParameter(0D, 0.9, "F", UNKNOWN)));
		// 1-4
		dataSet.addRow(buildColumn(".4", "1-4 year, Male",
				new QueryParameter(1D, 4D, "M", UNKNOWN)));

		dataSet.addRow(buildColumn(".6", "1-4 year, Female-non-pregnant",
				new QueryParameter(1D, 4D, "F", UNKNOWN)));

		// 5-9
		dataSet.addRow(buildColumn(".7", "5-9 year, Male",
				new QueryParameter(5D, 9D, "M", UNKNOWN)));

		dataSet.addRow(buildColumn(".9", "5-9 year, Female-non-pregnant",
				new QueryParameter(5D, 9D, "F", UNKNOWN)));

		// 10-14
		dataSet.addRow(buildColumn(".10", "10-14 year, Male",
				new QueryParameter(10D, 14D, "M", UNKNOWN)));

		dataSet.addRow(buildColumn(".12", "10-14 year, Female-non-pregnant",
				new QueryParameter(10D, 14D, "F", UNKNOWN)));

		// 15-19
		dataSet.addRow(buildColumn(".13", "15-19 year, Male",
				new QueryParameter(15D, 19D, "M", UNKNOWN)));

		dataSet.addRow(buildColumn(".14", "15-19 year, Female-pregnant",
				new QueryParameter(15D, 19D, "F", YES)));

		dataSet.addRow(buildColumn(".15", "15-19 year, Female-non-pregnant",
				new QueryParameter(15D, 19D, "F", NO)));

		// 20-24
		dataSet.addRow(buildColumn(".16", "20-24 year, Male",
				new QueryParameter(20D, 24D, "M", UNKNOWN)));

		dataSet.addRow(buildColumn(".17", "20-24 year, Female-pregnant",
				new QueryParameter(20D, 24D, "F", YES)));

		dataSet.addRow(buildColumn(".18", "20-24 year, Female-non-pregnant",
				new QueryParameter(20D, 24D, "F", NO)));

		// 25-29
		dataSet.addRow(buildColumn(".19", "25-29 year, Male",
				new QueryParameter(25D, 29D, "M", UNKNOWN)));

		dataSet.addRow(buildColumn(".20", "25-29 year, Female-pregnant",
				new QueryParameter(25D, 29D, "F", YES)));

		dataSet.addRow(buildColumn(".21", "25-29 year, Female-non-pregnant",
				new QueryParameter(25D, 29D, "F", NO)));

		// 30-34
		dataSet.addRow(buildColumn(".22", "30-34 year, Male",
				new QueryParameter(30D, 34D, "M", UNKNOWN)));

		dataSet.addRow(buildColumn(".23", "30-34 year, Female-pregnant",
				new QueryParameter(30D, 34D, "F", YES)));

		dataSet.addRow(buildColumn(".24", "30-34 year, Female-non-pregnant",
				new QueryParameter(30D, 34D, "F", NO)));

		// 35-39
		dataSet.addRow(buildColumn(".25", "35-39 year, Male",
				new QueryParameter(35D, 39D, "M", UNKNOWN)));

		dataSet.addRow(buildColumn(".26", "35-39 year, Female-pregnant",
				new QueryParameter(35D, 39D, "F", YES)));

		dataSet.addRow(buildColumn(".27", "35-39 year, Female-non-pregnant",
				new QueryParameter(35D, 39D, "F", NO)));

		// 40-44
		dataSet.addRow(buildColumn(".28", "40-44 year, Male",
				new QueryParameter(40D, 44D, "M", UNKNOWN)));

		dataSet.addRow(buildColumn(".29", "40-44 year, Female-pregnant",
				new QueryParameter(40D, 44D, "F", YES)));

		dataSet.addRow(buildColumn(".30", "40-44 year, Female-non-pregnant",
				new QueryParameter(40D, 44D, "F", NO)));

		// 45-49
		dataSet.addRow(buildColumn(".31", "45-49 year, Male",
				new QueryParameter(45D, 49D, "M", UNKNOWN)));

		dataSet.addRow(buildColumn(".32", "45-49 year, Female-pregnant",
				new QueryParameter(45D, 49D, "F", YES)));

		dataSet.addRow(buildColumn(".33", "45-49 year, Female-non-pregnant",
				new QueryParameter(45D, 49D, "F", NO)));

		// >=50
		dataSet.addRow(buildColumn(".34", ">=50 year, Male",
				new QueryParameter(50D, 200D, "M", UNKNOWN)));

		dataSet.addRow(buildColumn(".36", ">=50 year, Female-non-pregnant",
				new QueryParameter(50D, 200D, "F", NO)));
	}

	private DataSetRow buildColumn(String col_1_value, String col_2_value, QueryParameter parameter) {
		DataSetRow hivTxNewDataSetRow = new DataSetRow();
		hivTxNewDataSetRow.addColumnValue(
				new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class),
				baseName + "" + col_1_value);
		hivTxNewDataSetRow.addColumnValue(
				new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), col_2_value);
		hivTxNewDataSetRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class),
				getHTXNew(parameter));
		return hivTxNewDataSetRow;
	}

	private Integer getHTXNew(QueryParameter parameter) {
		Cohort cohort = new Cohort();

		if (parameter.maxAge == 0 && parameter.minAge == 0) {
			Calendar startDate = Calendar.getInstance();
			startDate.setTime(_datasetDefinition.getStartDate());
			startDate.add(Calendar.MONTH, -12);
			startDate.set(startDate.get(Calendar.YEAR),startDate.get(Calendar.MONTH) ,startDate.get(Calendar.DATE));
			cohort = patientQuery.getActiveOnArtCohort(parameter.gender, startDate.getTime(),
					_datasetDefinition.getEndDate(), cohort);
			persons = patientQuery.getPersons(cohort);
			return cohort.getMemberIds().size();
		}

		if (parameter.maxAge < 1) {

			List<Person> countPersons = new ArrayList<>();
			for (Person person : persons) {
				if (person.getAge() < parameter.maxAge && person.getGender().equals(parameter.gender)) {
					countPersons.add(person);
					cohort.addMembership(new CohortMembership(person.getPersonId()));

				}

			}
			persons.removeAll(countPersons);

		}
		// For older than 50 or 65 generalization
		else if (parameter.maxAge >= 200) {
			List<Person> countPersons = new ArrayList<>();
			for (Person person : persons) {
				if (person.getAge() >= parameter.minAge && person.getGender().equals(parameter.gender)) {
					countPersons.add(person);
					cohort.addMembership(new CohortMembership(person.getPersonId()));
				}

			}
			persons.removeAll(countPersons);

		}
		// For Age Range
		else {
			List<Person> countPersons = new ArrayList<>();
			for (Person person : persons) {
				if (person.getAge() >= parameter.minAge && person.getAge() <= parameter.maxAge
						&& person.getGender().equals(parameter.gender)) {
					countPersons.add(person);
					cohort.addMembership(new CohortMembership(person.getPersonId()));
				}

			}

			if (parameter.isPregnant != UNKNOWN) {
				Cohort pregnantCohort = patientQuery.getPatientByPregnantStatus(cohort, YES,
						_datasetDefinition.getStartDate(), _datasetDefinition.getEndDate());
				if (parameter.isPregnant == YES) {

					for (CohortMembership cohortMembership : pregnantCohort.getMemberships()) {
						persons.removeIf(p -> p.getPersonId().equals(cohortMembership.getPatientId()));
					}
					cohort = pregnantCohort;

				} else {
					for (CohortMembership cohortMembership : pregnantCohort.getMemberships()) {
						persons.removeIf(p -> p.getPersonId().equals(cohortMembership.getPatientId()));
						cohort.removeMembership(cohortMembership);
					}

				}
			}

		}
		return cohort.getMemberIds().size();
	}

	/**
	 * QueryParameter
	 */
	public class QueryParameter {

		private Double minAge;
		private Double maxAge;
		private String gender;
		private String isPregnant;

		public QueryParameter(Double minAge, Double maxAge, String gender, String isPregnant) {
			this.minAge = minAge;
			this.maxAge = maxAge;
			this.gender = gender;
			this.isPregnant = isPregnant;
		}
	}

}
