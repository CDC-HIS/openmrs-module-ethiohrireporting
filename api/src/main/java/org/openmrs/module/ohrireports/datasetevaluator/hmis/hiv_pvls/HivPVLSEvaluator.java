package org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_pvls;

import static org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions.DATE_VIRAL_TEST_RESULT_RECEIVED;
import static org.openmrs.module.ohrireports.constants.ConceptAnswer.NO;
import static org.openmrs.module.ohrireports.constants.ConceptAnswer.NOT_APPLICABLE;
import static org.openmrs.module.ohrireports.constants.ConceptAnswer.YES;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_1_NAME;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_2_NAME;

import java.util.*;

import org.openmrs.Cohort;
import org.openmrs.CohortMembership;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.query.GlobalPropertyService;
import org.openmrs.module.ohrireports.constants.ETHIOHRIReportsConstants;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.hiv_pvls.HivPvlsType;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class HivPVLSEvaluator {
	private String baseName;
	private final String column_3_name = "Number";
	private int cohortAll, cohortLV, cohortUN = 0;
	private Date endDate;
	@Autowired
	private HivPvlsQuery hivPvlsQuery;
	@Autowired
	private EncounterQuery encounterQuery;
	private HivPvlsType type;

	List<Person> persons = new ArrayList<>();

	
	public void buildDataset(Date start,Date end, SimpleDataSet dataSetDefinition,String prefix,HivPvlsType type,String description) {
		this.type= type;
		endDate = end;
		Date startDate = start;
;
		/*
		 * -11 is because calendar library start count month from zero,
		 * the idea is to check all record from past twelve months
		 */
		GlobalPropertyService globalPropertyService = Context.getService(GlobalPropertyService.class);
		Object viralLoadType = globalPropertyService.getGlobalProperty(ETHIOHRIReportsConstants.VIRAL_LOAD_CALCULATION_RANGE);

		if( Objects.nonNull(viralLoadType) && viralLoadType.toString().equalsIgnoreCase("YES")){
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(end);
			calendar.add(Calendar.MONTH,-12);
			startDate = calendar.getTime();
		}

		List<Integer> encounter = encounterQuery.getEncounters(Collections.singletonList(DATE_VIRAL_TEST_RESULT_RECEIVED),
				startDate,end);
		hivPvlsQuery.setData(startDate, end, encounter);

		baseName = "HIV_TX_PVLS";
		baseName = baseName + prefix;
		cohortAll = 0;
		cohortLV = 0;
		cohortUN = 0;
		buildDataSet(dataSetDefinition, type,description);
	}

	private void buildDataSet(SimpleDataSet dataSet,HivPvlsType type,String description) {
		int row = dataSet.getRows().size();
		  row++;
		if (type == HivPvlsType.TESTED) {
			DataSetRow headerDataSetRow = new DataSetRow();
			headerDataSetRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class),
					"HIV_TX_PVLS");
			headerDataSetRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class),
					description);
			headerDataSetRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, String.class),
					calculatePercentage() + "%");
			dataSet.addRow(row++, headerDataSetRow);
		}
		dataSet.addRow(row++, buildColumn(" ","Number of adult and pediatric ART patients for whom viral load test result received in the reporting period" ,
				new QueryParameter(0D, 0D, "", NOT_APPLICABLE)));

		dataSet.addRow(row++, buildColumn(".1", "< 1 year, Male",
				new QueryParameter(0D, 0.9, "M", NOT_APPLICABLE)));

		dataSet.addRow(row++, buildColumn(".3", "< 1 year, Female-non-pregnant",
				new QueryParameter(0D, 0.9, "F", NOT_APPLICABLE)));
		// 1-4
		dataSet.addRow(row++, buildColumn(".4", "1-4 year, Male",
				new QueryParameter(1D, 4D, "M", NOT_APPLICABLE)));

		dataSet.addRow(row++, buildColumn(".6", "1-4 year, Female-non-pregnant",
				new QueryParameter(1D, 4D, "F", NOT_APPLICABLE)));

		// 5-9
		dataSet.addRow(row++, buildColumn(".7", "5-9 year, Male",
				new QueryParameter(5D, 9D, "M", NOT_APPLICABLE)));

		dataSet.addRow(row++, buildColumn(".9", "5-9 year, Female-non-pregnant",
				new QueryParameter(5D, 9D, "F", NOT_APPLICABLE)));

		// 10-14
		dataSet.addRow(row++, buildColumn(".10", "10-14 year, Male",
				new QueryParameter(10D, 14D, "M", NOT_APPLICABLE)));

		dataSet.addRow(row++, buildColumn(".12", "10-14 year, Female-non-pregnant",
				new QueryParameter(10D, 14D, "F", NOT_APPLICABLE)));

		// 15-19
		dataSet.addRow(row++, buildColumn(".13", "15-19 year, Male",
				new QueryParameter(15D, 19D, "M", NOT_APPLICABLE)));

		dataSet.addRow(row++, buildColumn(".14", "15-19 year, Female-pregnant",
				new QueryParameter(15D, 19D, "F", YES)));

		dataSet.addRow(row++, buildColumn(".15", "15-19 year, Female-non-pregnant",
				new QueryParameter(15D, 19D, "F", NO)));

		// 20-24
		dataSet.addRow(row++, buildColumn(".16", "20-24 year, Male",
				new QueryParameter(20D, 24D, "M", NOT_APPLICABLE)));

		dataSet.addRow(row++, buildColumn(".17", "20-24 year, Female-pregnant",
				new QueryParameter(20D, 24D, "F", YES)));

		dataSet.addRow(row++, buildColumn(".18", "20-24 year, Female-non-pregnant",
				new QueryParameter(20D, 24D, "F", NO)));

		// 25-29
		dataSet.addRow(row++, buildColumn(".19", "25-29 year, Male",
				new QueryParameter(25D, 29D, "M", NOT_APPLICABLE)));

		dataSet.addRow(row++, buildColumn(".20", "25-29 year, Female-pregnant",
				new QueryParameter(25D, 29D, "F", YES)));

		dataSet.addRow(row++, buildColumn(".21", "25-29 year, Female-non-pregnant",
				new QueryParameter(25D, 29D, "F", NO)));

		// 30-34
		dataSet.addRow(row++, buildColumn(".22", "30-34 year, Male",
				new QueryParameter(30D, 34D, "M", NOT_APPLICABLE)));

		dataSet.addRow(row++, buildColumn(".23", "30-34 year, Female-pregnant",
				new QueryParameter(30D, 34D, "F", YES)));

		dataSet.addRow(row++, buildColumn(".24", "30-34 year, Female-non-pregnant",
				new QueryParameter(30D, 34D, "F", NO)));

		// 35-39
		dataSet.addRow(row++, buildColumn(".25", "35-39 year, Male",
				new QueryParameter(35D, 39D, "M", NOT_APPLICABLE)));

		dataSet.addRow(row++, buildColumn(".26", "35-39 year, Female-pregnant",
				new QueryParameter(35D, 39D, "F", YES)));

		dataSet.addRow(row++, buildColumn(".27", "35-39 year, Female-non-pregnant",
				new QueryParameter(35D, 39D, "F", NO)));

		// 40-44
		dataSet.addRow(row++, buildColumn(".28", "40-44 year, Male",
				new QueryParameter(40D, 44D, "M", NOT_APPLICABLE)));

		dataSet.addRow(row++, buildColumn(".29", "40-44 year, Female-pregnant",
				new QueryParameter(40D, 44D, "F", YES)));

		dataSet.addRow(row++, buildColumn(".30", "40-44 year, Female-non-pregnant",
				new QueryParameter(40D, 44D, "F", NO)));

		// 45-49
		dataSet.addRow(row++, buildColumn(".31", "45-49 year, Male",
				new QueryParameter(45D, 49D, "M", NOT_APPLICABLE)));

		dataSet.addRow(row++, buildColumn(".32", "45-49 year, Female-pregnant",
				new QueryParameter(45D, 49D, "F", YES)));

		dataSet.addRow(row++, buildColumn(".33", "45-49 year, Female-non-pregnant",
				new QueryParameter(45D, 49D, "F", NO)));

		// >=50
		dataSet.addRow(row++, buildColumn(".34", ">=50 year, Male",
				new QueryParameter(50D, 200D, "M", NOT_APPLICABLE)));

		dataSet.addRow(row++, buildColumn(".36", ">=50 year, Female-non-pregnant",
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
				getPVLSCount(parameter));
		return hivTxNewDataSetRow;
	}

	private Integer getPVLSCount(QueryParameter parameter) {
		Cohort cohort = new Cohort();

		if (parameter.maxAge == 0 && parameter.minAge == 0) {

			cohort = getAll(parameter);

			persons = hivPvlsQuery.getPersons(cohort);
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
			for (Person person : persons) {
				if (person.getAge() >= parameter.minAge && person.getAge() <= parameter.maxAge
						&& person.getGender().equals(parameter.gender)) {
			
					cohort.addMembership(new CohortMembership(person.getPersonId()));
				}

			}

			if (!Objects.equals(parameter.isPregnant, NOT_APPLICABLE)) {
				Cohort pregnantCohort = hivPvlsQuery.getPatientByPregnantStatus(cohort, YES,
						hivPvlsQuery.getLastEncounterIds());
				if (Objects.equals(parameter.isPregnant, YES)) {

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
		if (cohortAll == 0)
			return 0;
		cohortUN = hivPvlsQuery.getPatientsWithViralLoadSuppressed("").size();
		cohortLV = hivPvlsQuery.getPatientWithViralLoadCountLowLevelViremia("",
				endDate).size();
		int total = (cohortLV + cohortUN) / cohortAll;

		return total / 100;
	}

	private Cohort getAll(QueryParameter parameter) {
		Cohort cohort;
		switch (type) {
			case SUPPRESSED:
				cohort = hivPvlsQuery.getPatientsWithViralLoadSuppressed(parameter.gender);
				break;
			case LOW_LEVEL_LIVERMIA:
				cohort = hivPvlsQuery.getPatientWithViralLoadCountLowLevelViremia(parameter.gender, endDate);
				break;

			default:
				cohort = hivPvlsQuery.getPatientWithViralLoadCount(parameter.gender,endDate);
				cohortAll = cohort.size();
				break;
		}
		return cohort;
	}

	/**
	 * QueryParameter
	 */
	public class QueryParameter {

		private final Double minAge;
		private final Double maxAge;
		private final String gender;
		private final String isPregnant;

		public QueryParameter(Double minAge, Double maxAge, String gender, String isPregnant) {
			this.minAge = minAge;
			this.maxAge = maxAge;
			this.gender = gender;
			this.isPregnant = isPregnant;
		}
	}

}