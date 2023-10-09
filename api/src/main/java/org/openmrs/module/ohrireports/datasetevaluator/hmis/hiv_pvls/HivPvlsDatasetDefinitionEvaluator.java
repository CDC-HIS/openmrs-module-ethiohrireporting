package org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_pvls;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.YES;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_1_NAME;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_2_NAME;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.NO;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.UNKNOWN;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.CohortMembership;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.hiv_pvls.HivPvlsDatasetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.hiv_pvls.HivPvlsType;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { HivPvlsDatasetDefinition.class })
public class HivPvlsDatasetDefinitionEvaluator implements DataSetEvaluator {

	private HivPvlsDatasetDefinition _datasetDefinition;
	private String baseName;
	private String column_3_name = "Number";

	@Autowired
	private HivPvlsQuery hivPvlsQuery;

	List<Person> persons = new ArrayList<>();

	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext)
			throws EvaluationException {
		_datasetDefinition = (HivPvlsDatasetDefinition) dataSetDefinition;
		 baseName = "HIV_TX_PVLS";
		 baseName = baseName + "" + _datasetDefinition.getPrefix();
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		buildDataSet(dataSet);

		return dataSet;
	}

	public void buildDataSet(SimpleDataSet dataSet) {

		if (_datasetDefinition.getType() == HivPvlsType.TESTED) {
			DataSetRow headerDataSetRow = new DataSetRow();
			headerDataSetRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class),
					"HIV_TX_PVLS");
			headerDataSetRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class),
					"Viral load Suppression (Percentage of ART clients with a suppressed viral load among those with a viral load test at 12 month in the reporting period)");
			headerDataSetRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, String.class),
					calculatePercentage() + "%");
			dataSet.addRow(headerDataSetRow);
		}
		dataSet.addRow(buildColumn(" ", _datasetDefinition.getDescription(),
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
				baseName.concat(col_1_value));
		hivTxNewDataSetRow.addColumnValue(
				new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), col_2_value);
		hivTxNewDataSetRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class),
				getPvls(parameter));
		return hivTxNewDataSetRow;
	}

	private Integer getPvls(QueryParameter parameter) {
		Cohort cohort = new Cohort();

		if (parameter.maxAge == 0 && parameter.minAge == 0) {

			cohort = getAll(parameter);

			persons = hivPvlsQuery.getPersons(cohort);
			return cohort.getMemberIds().size();
		}

		if (parameter.maxAge < 1){

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
				Cohort pregnantCohort = hivPvlsQuery.getPatientByPregnantStatus(cohort, YES,
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

	private int calculatePercentage() {
		Cohort cohortAll, cohortLV, cohortUN;
		cohortUN = hivPvlsQuery.getPatientsWithViralLoadSuppressed("",
				_datasetDefinition.getStartDate(),
				_datasetDefinition.getEndDate());

		cohortLV = hivPvlsQuery.getPatientWithViralLoadCountLowLevelViremia("",
				_datasetDefinition.getStartDate(),
				_datasetDefinition.getEndDate());

		cohortAll = hivPvlsQuery.getPatientWithViralLoadCount("",
				_datasetDefinition.getStartDate(),
				_datasetDefinition.getEndDate());

		if (cohortAll.size() == 0)
			return 0;

		int total = (cohortLV.size() + cohortUN.size()) / cohortAll.size();

		return total / 100;
	}

	private Cohort getAll(QueryParameter parameter) {
		Cohort cohort;
		switch (_datasetDefinition.getType()) {
			case SUPPRESSED:
				cohort = hivPvlsQuery.getPatientsWithViralLoadSuppressed(parameter.gender,
						_datasetDefinition.getStartDate(),
						_datasetDefinition.getEndDate());
				break;
			case LOW_LEVEL_LIVERMIA:
				cohort = hivPvlsQuery.getPatientWithViralLoadCountLowLevelViremia(parameter.gender,
						_datasetDefinition.getStartDate(),
						_datasetDefinition.getEndDate());
				break;

			default:
				cohort = hivPvlsQuery.getPatientWithViralLoadCount(parameter.gender,
						_datasetDefinition.getStartDate(),
						_datasetDefinition.getEndDate());
				break;
		}
		return cohort;
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