package org.openmrs.module.ohrireports.datasetevaluator.hmis.tx_new;

import static org.openmrs.module.ohrireports.constants.ConceptAnswer.YES;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.*;
import static org.openmrs.module.ohrireports.constants.ConceptAnswer.NO;
import static org.openmrs.module.ohrireports.constants.ConceptAnswer.NOT_APPLICABLE;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.openmrs.Cohort;
import org.openmrs.CohortMembership;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class HMISTXNewEvaluator {
	
	private  Date startDate ;
	private  Date endDate ;
	private  List<Integer> encounterIds;

	private  PatientQueryService patientQuery;

	
	private Cohort pregnantCohort;

	List<Person> persons = new ArrayList<>();




	public void buildDataSet(SimpleDataSet dataSet,Date startDate, Date endDate, List<Integer> encounter) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.encounterIds = encounter;
		patientQuery = Context.getService(PatientQueryService.class);
		dataSet.addRow(buildColumn(" ", "Number of adults and children with HIV infection newly started on ART",
				new QueryParameter(0D, 0D, "", NOT_APPLICABLE)));

		dataSet.addRow(buildColumn(".1", "< 1 year, Male",
				new QueryParameter(0D, 0.9, "M", NOT_APPLICABLE)));

		dataSet.addRow(buildColumn(".3", "< 1 year, Female-non-pregnant",
				new QueryParameter(0D, 0.9, "F", NOT_APPLICABLE)));
		// 1-4
		dataSet.addRow(buildColumn(".4", "1-4 year, Male",
				new QueryParameter(1D, 4D, "M", NOT_APPLICABLE)));

		dataSet.addRow(buildColumn(".6", "1-4 year, Female-non-pregnant",
				new QueryParameter(1D, 4D, "F", NOT_APPLICABLE)));

		// 5-9
		dataSet.addRow(buildColumn(".7", "5-9 year, Male",
				new QueryParameter(5D, 9D, "M", NOT_APPLICABLE)));

		dataSet.addRow(buildColumn(".9", "5-9 year, Female-non-pregnant",
				new QueryParameter(5D, 9D, "F", NOT_APPLICABLE)));

		// 10-14
		dataSet.addRow(buildColumn(".10", "10-14 year, Male",
				new QueryParameter(10D, 14D, "M", NOT_APPLICABLE)));

		dataSet.addRow(buildColumn(".12", "10-14 year, Female-non-pregnant",
				new QueryParameter(10D, 14D, "F", NOT_APPLICABLE)));

		// 15-19
		dataSet.addRow(buildColumn(".13", "15-19 year, Male",
				new QueryParameter(15D, 19D, "M", NOT_APPLICABLE)));

		dataSet.addRow(buildColumn(".14", "15-19 year, Female-pregnant",
				new QueryParameter(15D, 19D, "F", YES)));

		dataSet.addRow(buildColumn(".15", "15-19 year, Female-non-pregnant",
				new QueryParameter(15D, 19D, "F", NO)));

		// 20-24
		dataSet.addRow(buildColumn(".16", "20-24 year, Male",
				new QueryParameter(20D, 24D, "M", NOT_APPLICABLE)));

		dataSet.addRow(buildColumn(".17", "20-24 year, Female-pregnant",
				new QueryParameter(20D, 24D, "F", YES)));

		dataSet.addRow(buildColumn(".18", "20-24 year, Female-non-pregnant",
				new QueryParameter(20D, 24D, "F", NO)));

		// 25-29
		dataSet.addRow(buildColumn(".19", "25-29 year, Male",
				new QueryParameter(25D, 29D, "M", NOT_APPLICABLE)));

		dataSet.addRow(buildColumn(".20", "25-29 year, Female-pregnant",
				new QueryParameter(25D, 29D, "F", YES)));

		dataSet.addRow(buildColumn(".21", "25-29 year, Female-non-pregnant",
				new QueryParameter(25D, 29D, "F", NO)));

		// 30-34
		dataSet.addRow(buildColumn(".22", "30-34 year, Male",
				new QueryParameter(30D, 34D, "M", NOT_APPLICABLE)));

		dataSet.addRow(buildColumn(".23", "30-34 year, Female-pregnant",
				new QueryParameter(30D, 34D, "F", YES)));

		dataSet.addRow(buildColumn(".24", "30-34 year, Female-non-pregnant",
				new QueryParameter(30D, 34D, "F", NO)));

		// 35-39
		dataSet.addRow(buildColumn(".25", "35-39 year, Male",
				new QueryParameter(35D, 39D, "M", NOT_APPLICABLE)));

		dataSet.addRow(buildColumn(".26", "35-39 year, Female-pregnant",
				new QueryParameter(35D, 39D, "F", YES)));

		dataSet.addRow(buildColumn(".27", "35-39 year, Female-non-pregnant",
				new QueryParameter(35D, 39D, "F", NO)));

		// 40-44
		dataSet.addRow(buildColumn(".28", "40-44 year, Male",
				new QueryParameter(40D, 44D, "M", NOT_APPLICABLE)));

		dataSet.addRow(buildColumn(".29", "40-44 year, Female-pregnant",
				new QueryParameter(40D, 44D, "F", YES)));

		dataSet.addRow(buildColumn(".30", "40-44 year, Female-non-pregnant",
				new QueryParameter(40D, 44D, "F", NO)));

		// 45-49
		dataSet.addRow(buildColumn(".31", "45-49 year, Male",
				new QueryParameter(45D, 49D, "M", NOT_APPLICABLE)));

		dataSet.addRow(buildColumn(".32", "45-49 year, Female-pregnant",
				new QueryParameter(45D, 49D, "F", YES)));

		dataSet.addRow(buildColumn(".33", "45-49 year, Female-non-pregnant",
				new QueryParameter(45D, 49D, "F", NO)));

		// >=50
		dataSet.addRow(buildColumn(".34", ">=50 year, Male",
				new QueryParameter(50D, 200D, "M", NOT_APPLICABLE)));

		dataSet.addRow(buildColumn(".36", ">=50 year, Female-non-pregnant",
				new QueryParameter(50D, 200D, "F", NO)));
	}

	private DataSetRow buildColumn(String col_1_value, String col_2_value, QueryParameter parameter) {
		DataSetRow hivTxNewDataSetRow = new DataSetRow();
		String baseName = "HIV_TX_NEW ";
		hivTxNewDataSetRow.addColumnValue(
				new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class),
				baseName + col_1_value);
		hivTxNewDataSetRow.addColumnValue(
				new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), col_2_value);
		String column_3_name = "Number";
		hivTxNewDataSetRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class),
				getHTXNew(parameter));
		return hivTxNewDataSetRow;
	}

	private Integer getHTXNew(QueryParameter parameter) {
		int _age = 0;
		Cohort cohort = new Cohort();
		if (parameter.maxAge == 0 && parameter.minAge == 0) {
			cohort = patientQuery.getNewOnArtCohort("", startDate,
					endDate, null, encounterIds);
			persons = patientQuery.getPersons(cohort);
			pregnantCohort = patientQuery.getPatientByPregnantStatus(cohort, YES, encounterIds);

			return cohort.getMemberIds().size();
		}

		if (parameter.maxAge < 1) {

			List<Person> countPersons = new ArrayList<>();

			for (Person person : persons) {
				_age = person.getAge(endDate);
				if (_age < parameter.maxAge && person.getGender().equals(parameter.gender)) {
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
				_age = person.getAge(endDate);

				if (_age >= parameter.minAge && person.getGender().equals(parameter.gender)) {
					countPersons.add(person);
					cohort.addMembership(new CohortMembership(person.getPersonId()));
				}

			}
			persons.removeAll(countPersons);

		}
		// For Age Range
		else {
			//List<Person> countPersons = new ArrayList<>();
			for (Person person : persons) {
				_age = person.getAge(endDate);

				if (_age >= parameter.minAge && _age <= parameter.maxAge
						&& person.getGender().equals(parameter.gender)) {
				//	countPersons.add(person);
					cohort.addMembership(new CohortMembership(person.getPersonId()));
				}

			}

			if (!Objects.equals(parameter.isPregnant, NOT_APPLICABLE)) {
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

	/**
	 * QueryParameter
	 */
	public static class QueryParameter {

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
