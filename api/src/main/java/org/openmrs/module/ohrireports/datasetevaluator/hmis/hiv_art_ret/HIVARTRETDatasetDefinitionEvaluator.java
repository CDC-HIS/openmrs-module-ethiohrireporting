package org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_art_ret;

import org.openmrs.Cohort;
import org.openmrs.CohortMembership;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.hiv_art_ret.HIVARTRETDatasetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_1_NAME;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_2_NAME;

@Handler(supports = { HIVARTRETDatasetDefinition.class })
public class HIVARTRETDatasetDefinitionEvaluator implements DataSetEvaluator {

	private HIVARTRETDatasetDefinition _datasetDefinition;
	private String baseName = "HIV_ART_RET.1 ";
	private String baseNameForNet = "HIV_ART_RET_NET";
	private String column_3_name = "Number";
	private String description = "Number of adults and children who are still on treatment at 12 months after initiating ART";
	private String descriptionNet = "Number of persons on ART in the original cohort including those transferred in, minus those transferred out (net current cohort)";

	@Autowired
	private HivArtRetQuery hivArtRetQuery;
	List<Person> persons = new ArrayList<>();
	private boolean isNetRetention = false;
	Cohort currentCohort ;
	Cohort pregnantCohort;
	List<Integer> currentEncounter ;
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext)
			throws EvaluationException {
		_datasetDefinition = (HIVARTRETDatasetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		//this forbid the initializeRetentionCohort method to not be called twice for per single report execution
		isNetRetention = _datasetDefinition.getNetRetention();
		if (!isNetRetention) {
			hivArtRetQuery.initializeRetentionCohort(_datasetDefinition.getStartDate(), _datasetDefinition.getEndDate());
		}
		currentCohort = isNetRetention ? hivArtRetQuery.netRetCohort : hivArtRetQuery.retCohort;
		currentEncounter = isNetRetention ? hivArtRetQuery.netRetEncounter : hivArtRetQuery.retEncounter;
		 pregnantCohort = hivArtRetQuery.getPatientByPregnantStatus(currentCohort, YES, currentEncounter);
		buildDataSet(dataSet);

		return dataSet;
	}

	public void buildDataSet(SimpleDataSet dataSet) {
		if (isNetRetention) {
			DataSetRow headerDataSetRow = new DataSetRow();
			headerDataSetRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class),
					"HIV_ART_RET");
			headerDataSetRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class),
					"ART retention rate (Percentage of adult and children on ART treatment after 12 month of initiation of ARV therapy )");
			headerDataSetRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Double.class), hivArtRetQuery.getPercentage() + "%");
			dataSet.addRow(headerDataSetRow);
		}
		dataSet.addRow(buildColumn(" ",
				_datasetDefinition.getNetRetention() ? descriptionNet : description,
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
				(_datasetDefinition.getNetRetention() ? baseNameForNet : baseName) + "" + col_1_value);
		hivTxNewDataSetRow.addColumnValue(
				new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), col_2_value);
		hivTxNewDataSetRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class),
				getHTXNew(parameter));
		return hivTxNewDataSetRow;
	}

	private Integer getHTXNew(QueryParameter parameter) {

		int count = 0;
		if (parameter.maxAge == 0 && parameter.minAge == 0) {
				persons = hivArtRetQuery.getPersons(currentCohort);
			return  currentCohort.size();
		}

		if (parameter.maxAge < 1) {

			List<Person> countPersons = new ArrayList<>();
			for (Person person : persons) {
				if (person.getGender().equals(parameter.gender) && person.getAge(_datasetDefinition.getEndDate()) < parameter.maxAge) {
					countPersons.add(person);
				}

			}
			persons.removeAll(countPersons);

		}
		// For older than 50 or 65 generalization
		else if (parameter.maxAge >= 200) {
			int age;
			List<Person> countPersons = new ArrayList<>();
			for (Person person : persons) {
				age = person.getAge(_datasetDefinition.getEndDate());
				if (person.getGender().equals(parameter.gender) && age >= parameter.minAge) {
					countPersons.add(person);
					count++;
				}

			}
			persons.removeAll(countPersons);

		}
		// For Age Range
		else {
			List<Person> countPersons = new ArrayList<>();
				if (Objects.equals(parameter.isPregnant, YES)) {
					for (Person person : persons) {
						if (pregnantCohort.contains(person.getPersonId()) &&
							person.getAge(_datasetDefinition.getEndDate()) >= parameter.minAge &&
							person.getAge() <= parameter.maxAge) {
							countPersons.add(person);
							count++;
						}

					}
					persons.removeAll(countPersons);

				} else if(parameter.gender.equals("F")){
					for (Person person : persons) {
						if (!(pregnantCohort.contains(person.getPersonId())) &&
								person.getAge(_datasetDefinition.getEndDate()) >= parameter.minAge &&
								person.getAge() <= parameter.maxAge) {
							countPersons.add(person);
							count++;
						}

					}
					persons.removeAll(countPersons);
				}else {
				for (Person person : persons) {
					if (person.getGender().equals(parameter.gender) && person.getAge(_datasetDefinition.getEndDate()) >= parameter.minAge && person.getAge() <= parameter.maxAge) {
						countPersons.add(person);
						count++;
					}

				}
				persons.removeAll(countPersons);
			}

		}
		return count;
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
