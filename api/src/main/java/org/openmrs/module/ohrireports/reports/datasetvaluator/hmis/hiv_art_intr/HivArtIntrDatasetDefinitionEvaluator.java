package org.openmrs.module.ohrireports.reports.datasetvaluator.hmis.hiv_art_intr;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.DIED;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TRANSFER_OUT;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.query.PatientQuery;
import org.openmrs.module.ohrireports.reports.datasetdefinition.hmis.hiv_art_intr.HivArtIntrDatasetDefinition;
import org.openmrs.module.ohrireports.reports.datasetdefinition.hmis.hiv_linkage_new_ct.HIVLinkageNewCtDatasetDefinition;
import org.openmrs.module.ohrireports.reports.datasetvaluator.hmis.hiv_art_intr.HivArtIntrQuery.Range;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.LOST_TO_FOLLOW_UP;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.STOP;

import static org.openmrs.module.ohrireports.reports.datasetvaluator.hmis.HMISConstant.COLUMN_1_NAME;
import static org.openmrs.module.ohrireports.reports.datasetvaluator.hmis.HMISConstant.COLUMN_2_NAME;

import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { HivArtIntrDatasetDefinition.class })
public class HivArtIntrDatasetDefinitionEvaluator implements DataSetEvaluator {

	// Last treatment end date less-than current reporting end date
	// check if it's more than three month or less than three month
	// transferred out
	// refused stopped treatment
	// died
	//

	private HivArtIntrDatasetDefinition _datasetDefinition;
	private String baseName = "HIV_ART_INTR_OUT ";
	private String column_3_name = "Number";

	@Autowired
	private HivArtIntrQuery hivArtIntrQuery;

	private PatientQuery patientQuery;
	List<Person> persons = new ArrayList<>();

	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext)
			throws EvaluationException {
		_datasetDefinition = (HivArtIntrDatasetDefinition) dataSetDefinition;

		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);

		hivArtIntrQuery.initialize(_datasetDefinition.getStartDate(), _datasetDefinition.getEndDate());
		patientQuery = Context.getService(PatientQuery.class);
		buildDataSet(dataSet);

		return dataSet;
	}

	public void buildDataSet(SimpleDataSet dataSet) {

		// lost to follow-up Lost after treatemnt < 3month
		dataSet.addRow(buildColumn(".1", "Lost after treatment < 3month", Range.NONE,
				Range.LESS_THAN_THREE_MONTH, LOST_TO_FOLLOW_UP, ""));

		dataSet.addRow(buildColumn(".1. 1", "< 15 years, Male", Range.LESS_THAN_FIFTY,
				Range.NONE, LOST_TO_FOLLOW_UP, "M"));

		dataSet.addRow(
				buildColumn(".1. 2", "< 15 years, Female", Range.LESS_THAN_FIFTY, Range.NONE, LOST_TO_FOLLOW_UP, "F"));

		dataSet.addRow(buildColumn(".1. 3", ">= 15 years, Male", Range.ABOVE_OR_EQUAL_TO_FIFTY,
				Range.NONE, LOST_TO_FOLLOW_UP, "M"));

		dataSet.addRow(
				buildColumn(".1. 4", ">= 15 years, Female", Range.ABOVE_OR_EQUAL_TO_FIFTY, Range.NONE,
						LOST_TO_FOLLOW_UP, "F"));

		// lost to follow-up Lost after treatemnt > 3month
		dataSet.addRow(buildColumn(".2", "Lost after treatement > 3month", Range.NONE,
				Range.ABOVE_THREE_MONTH, LOST_TO_FOLLOW_UP, ""));

		dataSet.addRow(buildColumn(".2. 1", "< 15 years, Male", Range.LESS_THAN_FIFTY,
				Range.NONE, LOST_TO_FOLLOW_UP, "M"));

		dataSet.addRow(
				buildColumn(".2. 2", "< 15 years, Female", Range.LESS_THAN_FIFTY, Range.NONE, LOST_TO_FOLLOW_UP, "F"));

		dataSet.addRow(buildColumn(".2. 3", ">= 15 years, Male", Range.ABOVE_OR_EQUAL_TO_FIFTY,
				Range.NONE, LOST_TO_FOLLOW_UP, "M"));

		dataSet.addRow(
				buildColumn(".2. 4", ">= 15 years, Female", Range.ABOVE_OR_EQUAL_TO_FIFTY, Range.NONE,
						LOST_TO_FOLLOW_UP, "F"));

		// Transferred out
		dataSet.addRow(buildColumn(".3", "Transferred out", Range.NONE,
				Range.NONE, TRANSFER_OUT, ""));

		dataSet.addRow(buildColumn(".3. 1", "< 15 years, Male", Range.LESS_THAN_FIFTY,
				Range.NONE, TRANSFER_OUT, "M"));

		dataSet.addRow(
				buildColumn(".3. 2", "< 15 years, Female", Range.LESS_THAN_FIFTY, Range.NONE, TRANSFER_OUT, "F"));

		dataSet.addRow(buildColumn(".3. 3", ">= 15 years, Male", Range.ABOVE_OR_EQUAL_TO_FIFTY,
				Range.NONE, TRANSFER_OUT, "M"));

		dataSet.addRow(
				buildColumn(".3. 4", ">= 15 years, Female", Range.ABOVE_OR_EQUAL_TO_FIFTY, Range.NONE,
						TRANSFER_OUT, "F"));

		// Refused (stopped) treatment
		dataSet.addRow(buildColumn(".4", "Refused (stopped) treatment", Range.NONE,
				Range.NONE, STOP, ""));

		dataSet.addRow(buildColumn(".4. 1", "< 15 years, Male", Range.LESS_THAN_FIFTY,
				Range.NONE, STOP, "M"));

		dataSet.addRow(
				buildColumn(".4. 2", "< 15 years, Female", Range.LESS_THAN_FIFTY, Range.NONE, STOP, "F"));

		dataSet.addRow(buildColumn(".4. 3", ">= 15 years, Male", Range.ABOVE_OR_EQUAL_TO_FIFTY,
				Range.NONE, STOP, "M"));

		dataSet.addRow(
				buildColumn(".4. 4", ">= 15 years, Female", Range.ABOVE_OR_EQUAL_TO_FIFTY, Range.NONE,
						STOP, "F"));

		//Died
		dataSet.addRow(buildColumn(".5", "Died", Range.NONE,
				Range.NONE, DIED, ""));

		dataSet.addRow(buildColumn(".5. 1", "< 15 years, Male", Range.LESS_THAN_FIFTY,
				Range.NONE, DIED, "M"));

		dataSet.addRow(
				buildColumn(".5. 2", "< 15 years, Female", Range.LESS_THAN_FIFTY, Range.NONE, DIED, "F"));

		dataSet.addRow(buildColumn(".5. 3", ">= 15 years, Male", Range.ABOVE_OR_EQUAL_TO_FIFTY,
				Range.NONE, DIED, "M"));

		dataSet.addRow(
				buildColumn(".5. 4", ">= 15 years, Female", Range.ABOVE_OR_EQUAL_TO_FIFTY, Range.NONE,
						DIED, "F"));

	}

	private DataSetRow buildColumn(String col_1_value, String col_2_value, Range age, Range range, String type,
			String gender) {
		DataSetRow hivTxNewDataSetRow = new DataSetRow();
		hivTxNewDataSetRow.addColumnValue(
				new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class),
				baseName + "" + col_1_value);
		hivTxNewDataSetRow.addColumnValue(
				new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), col_2_value);

		hivTxNewDataSetRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class),
				age == Range.NONE ? getArtIntr(range, type) : getByAgeAndGender(age, gender));

		return hivTxNewDataSetRow;
	}

	private Integer getArtIntr(Range range, String type) {
	
		Set<Integer> patientId = new HashSet<>();
	
		persons.clear();
	
		if (type.equals(LOST_TO_FOLLOW_UP)) {
			patientId = hivArtIntrQuery.getAllPatientExceedsTreatmentEndDate(range, type);
		} else {
			patientId = hivArtIntrQuery.getPatientByFollowUpStatus(type);
		}

		persons.addAll(patientQuery.getPersons(new Cohort(patientId)));

		return patientId.size();
	}

	private Integer getByAgeAndGender(Range age, String gender) {

		List<Integer> countedId = new ArrayList<>();
		for (Person person : persons) {
			if (age == Range.LESS_THAN_FIFTY && person.getAge() < 15 && person.getGender().equals(gender)) {
				countedId.add(person.getPersonId());
			} else if (age == Range.ABOVE_OR_EQUAL_TO_FIFTY && person.getAge() >= 15
					&& person.getGender().equals(gender)) {
				countedId.add(person.getPersonId());
			}
		}

		return countedId.size();
	}
}
