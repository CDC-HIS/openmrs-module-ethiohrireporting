package org.openmrs.module.ohrireports.datasetevaluator.linelist.art;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

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
import org.openmrs.logic.op.In;
import org.openmrs.module.ohrireports.api.impl.query.BaseLineListQuery;
import org.openmrs.module.ohrireports.datasetevaluator.linelist.LineListUtilities;
import org.openmrs.module.ohrireports.datasetevaluator.linelist.art.ArtQuery;
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
		
		patientQuery = Context.getService(PatientQueryService.class);
		List<Integer> encounters = encounterQuery.getAliveFollowUpEncounters(hdsd.getStartDate(), hdsd.getEndDate());
		Cohort cohort = patientQuery.getNewOnArtCohort("", hdsd.getStartDate(), hdsd.getEndDate(), null, encounters);
		HashMap<Integer, Object> mrnIdentifierHashMap = artQuery.getIdentifier(cohort, MRN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> uanIdentifierHashMap = artQuery.getIdentifier(cohort, UAN_PATIENT_IDENTIFIERS);
		List<Person> persons = patientQuery.getPersons(cohort);
		HashMap<Integer, Object> weight = artQuery.getByValueNumeric(WEIGHT, cohort, encounters);
		HashMap<Integer, Object> cd4Count = artQuery.getByValueNumeric(ADULT_CD4_COUNT, cohort, encounters);
		HashMap<Integer, Object> whoStage = artQuery.getByResult(WHO_STAGE, cohort, encounters);
		HashMap<Integer, Object> nutritionalStatus = artQuery.getByResult(NUTRITIONAL_STATUS, cohort, encounters);
		HashMap<Integer, Object> tbScreeningResult = artQuery.getByResult(TB_SCREENED_RESULT, cohort, encounters);
		
		HashMap<Integer, Object> enrollmentDate = artQuery.getObsValueDate(null, ART_REGISTRATION_DATE, cohort,
		    INTAKE_A_ENCOUNTER_TYPE);
		
		HashMap<Integer, Object> hivConfirmedDate = artQuery.getObsValueDate(encounters, HIV_CONFIRMED_DATE, cohort);
		
		HashMap<Integer, Object> artStartDate = artQuery.getObsValueDate(encounters, ART_START_DATE, cohort);
		
		HashMap<Integer, Object> pregnancyStatus = artQuery.getByResult(PREGNANCY_STATUS, cohort, encounters);
		
		HashMap<Integer, Object> regimentDictionary = artQuery.getRegiment(encounters, cohort);
		
		HashMap<Integer, Object> artDispenseDose = artQuery.getObsValueDate(encounters, ART_DISPENSE_DOSE, cohort);
		
		HashMap<Integer, Object> transferedHashMap = artQuery.getConceptName(encounters, cohort, REASON_FOR_ART_ELIGIBILITY);
		
		HashMap<Integer, Object> nextVisitDate = artQuery.getObsValueDate(encounters, NEXT_VISIT_DATE, cohort);
		
		HashMap<Integer, Object> treatmentEndDate = artQuery.getObsValueDate(encounters, TREATMENT_END_DATE, cohort);
		
		DataSetRow row;
		if (!persons.isEmpty()) {
			
			row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("#", "#", Integer.class), "TOTAL");
			row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", Integer.class), persons.size());
			
			data.addRow(row);
		} else {
			data.addRow(LineListUtilities.buildEmptyRow(Arrays.asList("#", "Patient Name", "MRN", "UAN", "Age", "Sex",
			    "Weight", "CD4", "WHO Stage", "Nutritional Status", "TB Screening Result", "Enrollment Date in E.C",
			    "HIV Confirmed Date in E.C", "ART Start Date in E.C", "Days Difference", "Pregnancy/ Breastfeeding Status",
			    "Regimen", "ARV Dose Days", "TI?", "Next Visit Date in E.C", "Treatment End Date in E.C", "Mobile No.")));
		}
		int i = 1;
		for (Person person : persons) {
			Date enrollmentDateET = artQuery.getDate(enrollmentDate.get(person.getPersonId()));
			Date hivConfirmedDateET = artQuery.getDate(hivConfirmedDate.get(person.getPersonId()));
			Date artStartDateET = artQuery.getDate(artStartDate.get(person.getPersonId()));
			Date nextVisitDateET = artQuery.getDate(nextVisitDate.get(person.getPersonId()));
			Date treatmentEndDateET = artQuery.getDate(treatmentEndDate.get(person.getPersonId()));
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
			row.addColumnValue(new DataSetColumn("PregnancyStatus", "Pregnancy/ Breastfeeding Status", String.class),
			    pregnancyStatus.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Regimen", "Regimen", String.class),
			    regimentDictionary.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("ARTDispenseDose", "ARV Dose Days", String.class),
			    artDispenseDose.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("TI", "TI?", String.class), transferedHashMap.get(person.getPersonId()));
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
