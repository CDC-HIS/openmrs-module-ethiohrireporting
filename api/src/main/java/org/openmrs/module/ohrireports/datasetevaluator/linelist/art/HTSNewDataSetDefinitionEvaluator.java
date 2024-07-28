package org.openmrs.module.ohrireports.datasetevaluator.linelist.art;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.constants.*;
import org.openmrs.module.ohrireports.datasetevaluator.linelist.LineListUtilities;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.HTSNewDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { HTSNewDataSetDefinition.class })
public class HTSNewDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private ArtQuery artQuery;
	
	private PatientQueryService patientQuery;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		HTSNewDataSetDefinition hdsd = (HTSNewDataSetDefinition) dataSetDefinition;
		
		SimpleDataSet data = new SimpleDataSet(dataSetDefinition, evalContext);
		
		// Check start date and end date are valid
		// If start date is greater than end date
		if (hdsd.getStartDate() != null && hdsd.getEndDate() != null && hdsd.getStartDate().compareTo(hdsd.getEndDate()) > 0) {
			//throw new EvaluationException("Start date cannot be greater than end date");
			DataSetRow row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("Error", "Error", Integer.class),
			    "Report start date cannot be after report end date");
			data.addRow(row);
			return data;
		}
		
		patientQuery = Context.getService(PatientQueryService.class);
		List<Integer> encounters = encounterQuery.getAliveFirstFollowUpEncounters(hdsd.getStartDate(), hdsd.getEndDate());
		Cohort cohort = patientQuery.getNewOnArtCohort("", hdsd.getStartDate(), hdsd.getEndDate(), null, encounters);
		HashMap<Integer, Object> mrnIdentifierHashMap = artQuery.getIdentifier(cohort, Identifiers.MRN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> uanIdentifierHashMap = artQuery.getIdentifier(cohort, Identifiers.UAN_PATIENT_IDENTIFIERS);
		List<Person> persons = LineListUtilities.sortPatientByName(patientQuery.getPersons(cohort));
		HashMap<Integer, Object> weight = artQuery.getByValueNumeric(FollowUpConceptQuestions.WEIGHT, cohort, encounters);
		HashMap<Integer, Object> cd4Count = artQuery.getByValueNumeric(FollowUpConceptQuestions.ADULT_CD4_COUNT, cohort,
		    encounters);
		HashMap<Integer, Object> whoStage = artQuery.getByResult(FollowUpConceptQuestions.WHO_STAGE, cohort, encounters);
		HashMap<Integer, Object> nutritionalStatus = artQuery.getByResult(FollowUpConceptQuestions.NUTRITIONAL_STATUS_ADULT,
		    cohort, encounters);
		HashMap<Integer, Object> tbScreeningResult = artQuery.getByResult(FollowUpConceptQuestions.TB_SCREENED_RESULT,
		    cohort, encounters);
		
		HashMap<Integer, Object> enrollmentDate = artQuery.getObsValueDate(null,
		    FollowUpConceptQuestions.ART_REGISTRATION_DATE, cohort, EncounterType.INTAKE_A_ENCOUNTER_TYPE);
		
		HashMap<Integer, Object> hivConfirmedDate = artQuery.getObsValueDate(encounters,
		    PositiveCaseTrackingConceptQuestions.HIV_CONFIRMED_DATE, cohort);
		
		HashMap<Integer, Object> artStartDate = artQuery.getObsValueDate(encounters,
		    FollowUpConceptQuestions.ART_START_DATE, cohort);
		
		HashMap<Integer, Object> pregnancyStatus = artQuery.getByResult(FollowUpConceptQuestions.PREGNANCY_STATUS, cohort,
		    encounters);
		HashMap<Integer, Object> breastfeedingStatus = artQuery.getByResult(
		    FollowUpConceptQuestions.CURRENTLY_BREAST_FEEDING_CHILD, cohort, encounters);
		HashMap<Integer, Object> followUpDate = artQuery.getObsValueDate(encounters,
		    FollowUpConceptQuestions.FOLLOW_UP_DATE, cohort);
		HashMap<Integer, Object> statusHashMap = artQuery.getFollowUpStatus(encounters, cohort);
		
		HashMap<Integer, Object> regimentDictionary = artQuery.getRegiment(encounters, cohort);
		
		HashMap<Integer, Object> artDispenseDose = artQuery.getByResult(FollowUpConceptQuestions.ART_DISPENSE_DOSE, cohort,
		    encounters);
		
		HashMap<Integer, Object> transferedHashMap = artQuery.getConceptName(encounters, cohort,
		    FollowUpConceptQuestions.REASON_FOR_ART_ELIGIBILITY);
		
		HashMap<Integer, Object> nextVisitDate = artQuery.getObsValueDate(encounters,
		    FollowUpConceptQuestions.NEXT_VISIT_DATE, cohort);
		
		HashMap<Integer, Object> treatmentEndDate = artQuery.getObsValueDate(encounters,
		    FollowUpConceptQuestions.TREATMENT_END_DATE, cohort);
		
		DataSetRow row;
		if (!persons.isEmpty()) {
			
			row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("#", "#", Integer.class), "TOTAL");
			row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", Integer.class), persons.size());
			
			data.addRow(row);
		} else {
			data.addRow(LineListUtilities.buildEmptyRow(Arrays.asList("#", "Patient Name", "MRN", "UAN", "Age", "Sex",
			    "Weight", "CD4", "WHO Stage", "Nutritional Status", "TB Screening Result", "Enrollment Date in E.C",
			    "HIV Confirmed Date in E.C", "ART Start Date in E.C", "Days Difference", "Pregnant?", "Breastfeeding?",
			    "Regimen", "ARV Dose Days", "Next Visit Date in E.C", "Treatment End Date in E.C", "Mobile No.")));
		}
		int i = 1;
		for (Person person : persons) {
			Date enrollmentDateET = artQuery.getDate(enrollmentDate.get(person.getPersonId()));
			Date hivConfirmedDateET = artQuery.getDate(hivConfirmedDate.get(person.getPersonId()));
			Date artStartDateET = artQuery.getDate(artStartDate.get(person.getPersonId()));
			Date nextVisitDateET = artQuery.getDate(nextVisitDate.get(person.getPersonId()));
			Date treatmentEndDateET = artQuery.getDate(treatmentEndDate.get(person.getPersonId()));
			Date followupDateET = artQuery.getDate(followUpDate.get(person.getPersonId()));
			long daysDifference = getDayDifference(hivConfirmedDateET, artStartDateET);
			
			row = new DataSetRow();
			Date date = artQuery.getDate(artStartDate.get(person.getPersonId()));
			String ethiopianDate = artQuery.getEthiopianDate(date);
			row.addColumnValue(new DataSetColumn("#", "#", Integer.class), i++);
			row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", String.class), person.getNames());
			row.addColumnValue(new DataSetColumn("MRN", "MRN", Integer.class),
			    mrnIdentifierHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("UAN", "UAN", Integer.class),
			    uanIdentifierHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Age", "Age", Integer.class), person.getAge(date));
			row.addColumnValue(new DataSetColumn("Gender", "Sex", Integer.class), person.getGender());
			row.addColumnValue(new DataSetColumn("Weight", "Weight", String.class), weight.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("CD4", "CD4", String.class), cd4Count.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("WHOStaging", "WHO Staging", String.class),
			    whoStage.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("NutritionalStatus", "Nutritional Status", String.class),
			    nutritionalStatus.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("TBScreeningResult", "TB Screening Result", String.class),
			    tbScreeningResult.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("EnrollmentDateInETH", "Enrollment Date in E.C", String.class),
			    artQuery.getEthiopianDate(enrollmentDateET));
			row.addColumnValue(new DataSetColumn("HIVConfirmedDateETH", "HIV Confirmed Date in E.C", String.class),
			    artQuery.getEthiopianDate(hivConfirmedDateET));
			row.addColumnValue(new DataSetColumn("ARTStartDateETH", "ART Start Date in E.C", String.class),
			    artQuery.getEthiopianDate(artStartDateET));
			row.addColumnValue(new DataSetColumn("daysDifference", "Days Difference", String.class), daysDifference);
			row.addColumnValue(new DataSetColumn("PregnancyStatus", "Pregnant?", String.class),
			    pregnancyStatus.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("BreastfeedingStatus", "Breastfeeding?", String.class),
			    breastfeedingStatus.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Follow-up Date in E.C", "Follow-up Date in E.C", String.class),
			    artQuery.getEthiopianDate(followupDateET));
			row.addColumnValue(new DataSetColumn("Follow-up Status", "Follow-up Status", String.class),
			    statusHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Regimen", "Regimen", String.class),
			    regimentDictionary.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("ARTDispenseDose", "ARV Dose Days", String.class),
			    artDispenseDose.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("TI", "TI?", String.class), "");
			row.addColumnValue(new DataSetColumn("NextVisitDate", "Next Visit Date in E.C", Date.class),
			    artQuery.getEthiopianDate(nextVisitDateET));
			row.addColumnValue(new DataSetColumn("TreatmentEndDate", "Treatment End Date in E.C", Date.class),
			    artQuery.getEthiopianDate(treatmentEndDateET));
			row.addColumnValue(new DataSetColumn("MobileNumber", "Mobile No.", String.class),
			    getPhone(person.getActiveAttributes()));
			
			data.addRow(row);
		}
		
		return data;
	}
	
	private String getPhone(List<PersonAttribute> activeAttributes) {
		for (PersonAttribute personAttribute : activeAttributes) {
			if (personAttribute.getValue().startsWith("09") || personAttribute.getValue().startsWith("+251")) {
				return personAttribute.getValue();
			}
		}
		return "";
	}
	
	private long getDayDifference(Date from, Date to) {
		
		if (from == null || to == null) {
			return 0;
		}
		
		// Convert java.util.Date to Instant
		Instant instantFrom = from.toInstant();
		Instant instantTo = to.toInstant();
		
		// Define date format
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		
		// Convert Instant to LocalDateTime
		LocalDateTime start = LocalDateTime.ofInstant(instantFrom, ZoneId.systemDefault());
		LocalDateTime end = LocalDateTime.ofInstant(instantTo, ZoneId.systemDefault());
		
		// Calculate the difference in days
		return ChronoUnit.DAYS.between(start, end);
	}
	
}
