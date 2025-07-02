package org.openmrs.module.ohrireports.datasetevaluator.linelist.hivPositiveTracking;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.constants.EncounterType;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.HIVPositiveDatasetDefinition;
import org.openmrs.module.ohrireports.datasetevaluator.linelist.LineListUtilities;
import org.openmrs.module.ohrireports.helper.EthiOhriUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions.ART_START_DATE;
import static org.openmrs.module.ohrireports.constants.Identifiers.MRN_PATIENT_IDENTIFIERS;
import static org.openmrs.module.ohrireports.constants.Identifiers.UAN_PATIENT_IDENTIFIERS;
import static org.openmrs.module.ohrireports.constants.IntakeAConceptQuestions.ENTRE_POINT;
import static org.openmrs.module.ohrireports.constants.PositiveCaseTrackingConceptQuestions.*;
import static org.openmrs.module.ohrireports.datasetevaluator.linelist.LineListUtilities.getDayDifference;

@Handler(supports = HIVPositiveDatasetDefinition.class)
public class HivPositiveTrackingDatasetEvaluator implements DataSetEvaluator {
	
	@Autowired
	HIVPositiveTrackingLineListQuery hivPositiveTrackingLineListQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		HIVPositiveDatasetDefinition _datasetDefinition = (HIVPositiveDatasetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(_datasetDefinition, evalContext);
		
		SimpleDataSet _dataSet = EthiOhriUtil.isValidReportDateRange(_datasetDefinition.getStartDate(),
		    _datasetDefinition.getEndDate(), dataSet);
		if (_dataSet != null)
			return _dataSet;
		
		if (_datasetDefinition.getEndDate() == null) {
			_datasetDefinition.setEndDate(new Date());
		}
		hivPositiveTrackingLineListQuery.generateReport(_datasetDefinition.getStartDate(), _datasetDefinition.getEndDate());
		
		Cohort cohort = hivPositiveTrackingLineListQuery.getBaseCohort();
		List<Integer> encounter = hivPositiveTrackingLineListQuery.getBaseEncounter();
		
		HashMap<Integer, Object> mrnIdentifierHashMap = hivPositiveTrackingLineListQuery.getIdentifier(cohort,
		    MRN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> uaIdentifierHashMap = hivPositiveTrackingLineListQuery.getIdentifier(cohort,
		    UAN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> registrationDateHashMap = hivPositiveTrackingLineListQuery.getObsValueDate(encounter,
		    POSITIVE_TRACKING_REGISTRATION_DATE, cohort, EncounterType.POSITIVE_TRACKING_ENCOUNTER_TYPE);
		
		HashMap<Integer, Object> dateTestedHivPositiveHashMap = hivPositiveTrackingLineListQuery.getObsValueDate(encounter,
		    HIV_CONFIRMED_DATE, cohort, EncounterType.POSITIVE_TRACKING_ENCOUNTER_TYPE);
		HashMap<Integer, Object> entryPointHashMap = hivPositiveTrackingLineListQuery.getByResult(ENTRE_POINT, cohort,
		    encounter, EncounterType.POSITIVE_TRACKING_ENCOUNTER_TYPE);
		HashMap<Integer, Object> hivConfirmedDateHashMap = hivPositiveTrackingLineListQuery.getObsValueDate(encounter,
		    HIV_CONFIRMED_DATE, cohort, EncounterType.POSITIVE_TRACKING_ENCOUNTER_TYPE);
		
		HashMap<Integer, Object> artStartDateHashMap = hivPositiveTrackingLineListQuery.getObsValueDate(
		    hivPositiveTrackingLineListQuery.getFollowUpEncounter(), ART_START_DATE, cohort,
		    EncounterType.HTS_FOLLOW_UP_ENCOUNTER_TYPE);
		
		HashMap<Integer, Object> linkedToCareAndTreatmentHashMap = hivPositiveTrackingLineListQuery.getByResult(
		    LINKED_TO_CARE_TREATMENT, cohort, encounter, EncounterType.POSITIVE_TRACKING_ENCOUNTER_TYPE);
		
		HashMap<Integer, Object> linkedToCareAndTreatmentDateHashMap = hivPositiveTrackingLineListQuery.getObsValueDate(
		    encounter, LINKED_TO_CARE_DATE, cohort, EncounterType.POSITIVE_TRACKING_ENCOUNTER_TYPE);
		
		HashMap<Integer, Object> reasonForNotStartingARTheSameDayHashMap = hivPositiveTrackingLineListQuery.getByResult(
		    REASON_FOR_NOT_STARTING_ART_THE_SAME_DAY, cohort, encounter, EncounterType.POSITIVE_TRACKING_ENCOUNTER_TYPE);
		
		HashMap<Integer, Object> planForNextStepHashMap = hivPositiveTrackingLineListQuery.getByResult(
		    PLAN_FOR_NEXT_STEP_POSITIVE_TRACKING, cohort, encounter, EncounterType.POSITIVE_TRACKING_ENCOUNTER_TYPE);

		HashMap<Integer, Object> finalOutcomeKnownDateHashMap = hivPositiveTrackingLineListQuery.getObsValueDate(encounter,
		    FINAL_OUT_COME_DATE, cohort, EncounterType.POSITIVE_TRACKING_ENCOUNTER_TYPE);
		HashMap<Integer, Object> finalOutcomeKnownHashMap = hivPositiveTrackingLineListQuery.getByResult(FINAL_OUT_COME,
		    cohort, encounter, EncounterType.POSITIVE_TRACKING_ENCOUNTER_TYPE);
		
		DataSetRow row;
		List<Person> personList = LineListUtilities.sortPatientByName(hivPositiveTrackingLineListQuery.getPersons(cohort));
		if (!personList.isEmpty()) {
			
			row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("#", "#", Integer.class), "TOTAL");
			row.addColumnValue(new DataSetColumn("GUID", "GUID", Integer.class), personList.size());
			
			dataSet.addRow(row);
		} else {
			dataSet.addRow(LineListUtilities.buildEmptyRow(Arrays.asList("#", "GUID", "Patient Name", "MRN", "UAN", "Age",
			    "Sex", "Mobile No.", "Registration Date in E.C.", "Date Tested HIV +ve in E.C.", "Entry Point",
			    "HIV Confirmed Date in E.C.", "ART Start Date in E.C.", "Days Difference", "Linked to Care & Treatment?",
			    "Date Linked to Care & Treatment in E.C.", "Reason for not Starting ART the same day", "Plan for next Step",
			    "Final Outcome Known Date", "Final Outcome")));
		}
		
		int i = 1;
		for (Person person : personList) {
			row = new DataSetRow();
			Date registrationDate = hivPositiveTrackingLineListQuery.getDate(registrationDateHashMap.get(person
			        .getPersonId()));
			Date dateTestedHivPositive = hivPositiveTrackingLineListQuery.getDate(dateTestedHivPositiveHashMap.get(person
			        .getPersonId()));
			Date hivConfirmedDate = hivPositiveTrackingLineListQuery.getDate(hivConfirmedDateHashMap.get(person
			        .getPersonId()));
			Date artStartDate = hivPositiveTrackingLineListQuery.getDate(artStartDateHashMap.get(person.getPersonId()));
			Date linkedToCareAndTreatmentDate = hivPositiveTrackingLineListQuery.getDate(linkedToCareAndTreatmentDateHashMap
			        .get(person.getPersonId()));
			Date finalOutcomeKnownDate = hivPositiveTrackingLineListQuery.getDate(finalOutcomeKnownDateHashMap.get(person
			        .getPersonId()));
			String daysDifference = getDayDifference(artStartDate, hivConfirmedDate);
			
			row.addColumnValue(new DataSetColumn("#", "#", Integer.class), i++);
			row.addColumnValue(new DataSetColumn("GUID", "GUID", String.class), person.getUuid());
			row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", String.class), person.getNames());
			row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class), mrnIdentifierHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("UAN", "UAN", String.class), uaIdentifierHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Age", "Age", Integer.class), person.getAge());
			row.addColumnValue(new DataSetColumn("Sex", "Sex", String.class), person.getGender());
			row.addColumnValue(new DataSetColumn("Mobile No.", "Mobile No.", String.class),
			    LineListUtilities.getPhone(person.getActiveAttributes()));
			row.addColumnValue(new DataSetColumn("Registration Date in E.C.", "Registration Date in E.C.", String.class),
			    hivPositiveTrackingLineListQuery.getEthiopianDate(registrationDate));
			row.addColumnValue(
			    new DataSetColumn("Date Tested HIV +ve in E.C.", "Date Tested HIV +ve in E.C.", String.class),
			    hivPositiveTrackingLineListQuery.getEthiopianDate(dateTestedHivPositive));
			row.addColumnValue(new DataSetColumn("Entry Point", "Entry Point", String.class),
			    entryPointHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("HIV Confirmed Date in E.C.", "HIV Confirmed Date in E.C.", String.class),
			    hivPositiveTrackingLineListQuery.getEthiopianDate(hivConfirmedDate));
			row.addColumnValue(new DataSetColumn("ART Start Date in E.C.", "ART Start Date in E.C.", String.class),
			    hivPositiveTrackingLineListQuery.getEthiopianDate(artStartDate));
			row.addColumnValue(new DataSetColumn("Days Difference", "Days Difference", Integer.class), daysDifference);
			row.addColumnValue(
			    new DataSetColumn("Linked to Care & Treatment?", "Linked to Care & Treatment?", String.class),
			    linkedToCareAndTreatmentHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Date Linked to Care & Treatment in E.C.",
			        "Date Linked to Care & Treatment in E.C.", String.class), hivPositiveTrackingLineListQuery
			        .getEthiopianDate(linkedToCareAndTreatmentDate));
			row.addColumnValue(new DataSetColumn("Reason for not Starting ART the same day",
			        "Reason for not Starting ART the same day", String.class), reasonForNotStartingARTheSameDayHashMap
			        .get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Plan for next step", "Plan for next step", String.class),
			    planForNextStepHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Final Outcome Known Date", "Final Outcome Known Date", String.class),
			    hivPositiveTrackingLineListQuery.getEthiopianDate(finalOutcomeKnownDate));
			row.addColumnValue(new DataSetColumn("Final Outcome", "Final Outcome", String.class),
			    finalOutcomeKnownHashMap.get(person.getPersonId()));
			
			dataSet.addRow(row);
		}
		return dataSet;
	}
}
