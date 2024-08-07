package org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_art_ret;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.module.ohrireports.api.impl.query.HivArtRetQuery;
import org.openmrs.module.ohrireports.constants.ConceptAnswer;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_1_NAME;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_2_NAME;

@Component
@Scope("prototype")
public class HIVARTRETEvaluator {
	
	private final String column_3_name = "Number";
	
	@Autowired
	private HivArtRetQuery hivArtRetQuery;
	List<Person> persons = new ArrayList<>();
	private boolean isNetRetention = false;
	private Date endDate;
	Cohort currentCohort ;
	Cohort pregnantCohort;
	List<Integer> currentEncounter ;
	
	public void buildDataSet(SimpleDataSet dataSetDefinition, Boolean isNetRetention, Date startDate, Date endDate){
		
		//this forbid the initializeRetentionCohort method to not be called twice for per single report execution
		this.isNetRetention = isNetRetention;
		this.endDate = endDate;
		if (!isNetRetention) {
			hivArtRetQuery.initializeRetentionCohort(startDate,endDate);
		}
		currentCohort = isNetRetention ? hivArtRetQuery.getNetRetCohort() : hivArtRetQuery.getRetCohort();
		currentEncounter = isNetRetention ? hivArtRetQuery.getNetRetEncounter() : hivArtRetQuery.getRetEncounter();
		 pregnantCohort = hivArtRetQuery.getPatientByPregnantStatus(currentCohort, ConceptAnswer.YES, currentEncounter);
		buildData(dataSetDefinition);

	}

	private void buildData(SimpleDataSet dataSet) {
		if (!isNetRetention) {
			DataSetRow headerDataSetRow = new DataSetRow();
			headerDataSetRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class),
					"HIV_ART_RET");
			headerDataSetRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class),
					"ART retention rate (Percentage of adult and children on ART treatment after 12 month of initiation of ARV therapy )");
			headerDataSetRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Double.class), hivArtRetQuery.getPercentage() + "%");
			dataSet.addRow(headerDataSetRow);
		}
		String description = "Number of adults and children who are still on treatment at 12 months after initiating ART";
		String descriptionNet = "Number of persons on ART in the original cohort including those transferred in, minus those transferred out (net current cohort)";
		dataSet.addRow(buildColumn(" ",
				isNetRetention ? descriptionNet : description, getTotalCount()));

		dataSet.addRow(buildColumn(".1", "< 1 year, Male", getCount(new QueryParameter(0D, 0.9, "M", ConceptAnswer.NOT_APPLICABLE))));

		dataSet.addRow(buildColumn(".3", "< 1 year, Female-non-pregnant",
				getCount(new QueryParameter(0D, 0.9, "F", ConceptAnswer.NOT_APPLICABLE))));
		// 1-4
		dataSet.addRow(buildColumn(".4", "1-4 year, Male",
				getCount( new QueryParameter(1D, 4D, "M", ConceptAnswer.NOT_APPLICABLE))));

		dataSet.addRow(buildColumn(".6", "1-4 year, Female-non-pregnant",
				getCount( new QueryParameter(1D, 4D, "F", ConceptAnswer.NOT_APPLICABLE))));

		// 5-9
		dataSet.addRow(buildColumn(".7", "5-9 year, Male",
			getCount(	new QueryParameter(5D, 9D, "M", ConceptAnswer.NOT_APPLICABLE))));

		dataSet.addRow(buildColumn(".9", "5-9 year, Female-non-pregnant",
			getCount(new QueryParameter(5D, 9D, "F", ConceptAnswer.NOT_APPLICABLE))));

		// 10-14
		dataSet.addRow(buildColumn(".10", "10-14 year, Male",
			getCount(new QueryParameter(10D, 14D, "M", ConceptAnswer.NOT_APPLICABLE))));

		dataSet.addRow(buildColumn(".12", "10-14 year, Female-non-pregnant",
			getCount(	new QueryParameter(10D, 14D, "F", ConceptAnswer.NOT_APPLICABLE))));

		// 15-19
		dataSet.addRow(buildColumn(".13", "15-19 year, Male",
				getCount( new QueryParameter(15D, 19D, "M", ConceptAnswer.NOT_APPLICABLE))));

		dataSet.addRow(buildColumn(".14", "15-19 year, Female-pregnant",
				getCount(new QueryParameter(15D, 19D, "F", ConceptAnswer.YES))));

		dataSet.addRow(buildColumn(".15", "15-19 year, Female-non-pregnant",
				getCount( new QueryParameter(15D, 19D, "F", ConceptAnswer.NO))));

		// 20-24
		dataSet.addRow(buildColumn(".16", "20-24 year, Male",
				getCount(	new QueryParameter(20D, 24D, "M", ConceptAnswer.NOT_APPLICABLE))));

		dataSet.addRow(buildColumn(".17", "20-24 year, Female-pregnant",
				getCount(	new QueryParameter(20D, 24D, "F", ConceptAnswer.YES))));

		dataSet.addRow(buildColumn(".18", "20-24 year, Female-non-pregnant",
				getCount(	new QueryParameter(20D, 24D, "F", ConceptAnswer.NO))));

		// 25-29
		dataSet.addRow(buildColumn(".19", "25-29 year, Male",
				getCount(	new QueryParameter(25D, 29D, "M", ConceptAnswer.NOT_APPLICABLE))));

		dataSet.addRow(buildColumn(".20", "25-29 year, Female-pregnant",
				getCount(new QueryParameter(25D, 29D, "F", ConceptAnswer.YES))));

		dataSet.addRow(buildColumn(".21", "25-29 year, Female-non-pregnant",
				getCount	(new QueryParameter(25D, 29D, "F", ConceptAnswer.NO))));

		// 30-34
		dataSet.addRow(buildColumn(".22", "30-34 year, Male",
				getCount( 	new QueryParameter(30D, 34D, "M", ConceptAnswer.NOT_APPLICABLE))));

		dataSet.addRow(buildColumn(".23", "30-34 year, Female-pregnant",
				getCount	(new QueryParameter(30D, 34D, "F", ConceptAnswer.YES))));

		dataSet.addRow(buildColumn(".24", "30-34 year, Female-non-pregnant",
				getCount(	new QueryParameter(30D, 34D, "F", ConceptAnswer.NO))));

		// 35-39
		dataSet.addRow(buildColumn(".25", "35-39 year, Male",
				getCount	(new QueryParameter(35D, 39D, "M", ConceptAnswer.NOT_APPLICABLE))));

		dataSet.addRow(buildColumn(".26", "35-39 year, Female-pregnant",
				getCount(	new QueryParameter(35D, 39D, "F", ConceptAnswer.YES))));

		dataSet.addRow(buildColumn(".27", "35-39 year, Female-non-pregnant",
				getCount	(new QueryParameter(35D, 39D, "F", ConceptAnswer.NO))));

		// 40-44
		dataSet.addRow(buildColumn(".28", "40-44 year, Male",
				getCount	(new QueryParameter(40D, 44D, "M", ConceptAnswer.NOT_APPLICABLE))));

		dataSet.addRow(buildColumn(".29", "40-44 year, Female-pregnant",
				getCount(	new QueryParameter(40D, 44D, "F", ConceptAnswer.YES))));

		dataSet.addRow(buildColumn(".30", "40-44 year, Female-non-pregnant",
				getCount	(new QueryParameter(40D, 44D, "F", ConceptAnswer.NO))));

		// 45-49
		dataSet.addRow(buildColumn(".31", "45-49 year, Male",
				getCount(	new QueryParameter(45D, 49D, "M", ConceptAnswer.NOT_APPLICABLE))));

		dataSet.addRow(buildColumn(".32", "45-49 year, Female-pregnant",
				getCount(	new QueryParameter(45D, 49D, "F", ConceptAnswer.YES))));

		dataSet.addRow(buildColumn(".33", "45-49 year, Female-non-pregnant",
				getCount(	new QueryParameter(45D, 49D, "F", ConceptAnswer.NO))));

		// >=50
		dataSet.addRow(buildColumn(".34", ">=50 year, Male",
				getCount( new QueryParameter(50D, 200D, "M", ConceptAnswer.NOT_APPLICABLE))));

		dataSet.addRow(buildColumn(".36", ">=50 year, Female-non-pregnant",
				getCount(new QueryParameter(50D, 200D, "F", ConceptAnswer.NO))));
	}

	private int getTotalCount() {
		persons = hivArtRetQuery.getPersons(currentCohort);
		return currentCohort.size();
	}

	private DataSetRow buildColumn(String col_1_value, String col_2_value, int value) {
		DataSetRow hivTxNewDataSetRow = new DataSetRow();
		String baseName = "HIV_ART_RET.1 ";
		String baseNameForNet = "HIV_ART_RET_NET";
		hivTxNewDataSetRow.addColumnValue(
				new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class),
				(isNetRetention? baseNameForNet : baseName) + "" + col_1_value);
		hivTxNewDataSetRow.addColumnValue(
				new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), col_2_value);
		hivTxNewDataSetRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class), value);
		return hivTxNewDataSetRow;
	}

	private Integer getCount(QueryParameter parameter) {
		int age;
		int count = 0;
		List<Integer> countPersons = new ArrayList<>();

		if (parameter.maxAge < 1) {
			for (Person person : persons) {
				age = person.getAge(endDate);
				if (person.getGender().equals(parameter.gender) && age <= parameter.maxAge) {
					countPersons.add(person.getPersonId());
				}

			}
		}// For older than 50 or 65 generalization
		else if (parameter.maxAge >= 200) {
			for (Person person : persons) {
				age = person.getAge(endDate);
				if (person.getGender().equals(parameter.gender) && age >= parameter.minAge) {
					countPersons.add(person.getPersonId());
					count++;
				}

			}
		}// For Age Range
		else if(Objects.equals(parameter.gender,"F")) {
			if (Objects.equals(parameter.isPregnant, ConceptAnswer.YES)) {
				Optional personOption;
				Person person;
				for (Integer personId : pregnantCohort.getMemberIds()) {
					personOption = persons.stream().filter(p -> p.getPersonId().equals(personId)).findFirst();
					if (!personOption.isPresent())
						continue;
					person = (Person) personOption.get();
					age = person.getAge(endDate);
					if (person.getGender().equals(parameter.gender) &&
							age >= parameter.minAge &&
							age <= parameter.maxAge) {
						countPersons.add(person.getPersonId());
						count++;
					}

				}

			} else {
				for (Person person : persons) {
					age = person.getAge(endDate);
					if (person.getGender().equals(parameter.gender) &&
							age >= parameter.minAge &&
							age <= parameter.maxAge) {
						countPersons.add(person.getPersonId());
						count++;
					}

				}
			}
		}else{
			for (Person person : persons) {
				age = person.getAge(endDate);
				if (person.getGender().equals(parameter.gender) && age >= parameter.minAge && age <= parameter.maxAge) {
					countPersons.add(person.getPersonId());
					count++;
				}

			}
		}


		countPersons.forEach(p->persons.remove(p));
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
