package org.openmrs.module.ohrireports.datasetevaluator.linelist.artPatient;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.ARTPatientListQuery;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.cohorts.util.EthiOhriUtil;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.ARTPatientListDatasetDefinition;
import org.openmrs.module.ohrireports.datasetevaluator.linelist.LineListUtilities;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Handler(supports = { ARTPatientListDatasetDefinition.class })
public class ARTPatientListDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private ARTPatientListQuery artPatientListQuery;
	
	@Autowired
	private ARTPatientListLineListQuery artPatientListLineListQuery;
	
	private ARTPatientListDatasetDefinition _dataSetDefinition;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	private List<Integer> latestFollowup;
	
	private HashMap<Integer, Object> mrnIdentifierHashMap, uanIdentifierHashMap, registrationDateDictionary,
	        hivConfirmedDateDictionary, artStartDateDictionary, latestFollowupDateDictionary, latestFollowupStatus, regimen,
	        arvDoseDays, adherence, nextVisitDateDictionary, tiHashMap;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		_dataSetDefinition = (ARTPatientListDatasetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		
		// Check start date and end date are valid
		// If start date is greater than end date
		if (_dataSetDefinition.getStartDate() != null && _dataSetDefinition.getEndDate() != null
		        && _dataSetDefinition.getStartDate().compareTo(_dataSetDefinition.getEndDate()) > 0) {
			//throw new EvaluationException("Start date cannot be greater than end date");
			DataSetRow row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("Error", "Error", Integer.class),
			    "Report start date cannot be after report end date");
			dataSet.addRow(row);
			return dataSet;
		}
		
		if (_dataSetDefinition.getEndDate() == null) {
			_dataSetDefinition.setEndDate(new Date());
		}
		
		// Here is the sudo code for getting the encounter for the ART patient list
		// If Patient Enrollment Date is <= Report Date
		//        AND
		// If Patient has MRN
		// AND
		// If Patient has at least 1 ART follow-up record
		// ONLY THEN
		// count the record
		artPatientListQuery.setEndDate(_dataSetDefinition.getEndDate());
		
		// remove cohort with no MRN
		Cohort baseCohort = getCohortOnlyHaveMRN(artPatientListQuery.getCohort(artPatientListQuery.getBaseEncounter()));
		
		List<Integer> encounterWithAtleastOneFollow = encounterQuery.getEncounters(
		    Collections.singletonList(FOLLOW_UP_DATE), null, new Date(), baseCohort);
		
		Cohort cohortWithAtleastOneFollow = artPatientListQuery.getCohort(encounterWithAtleastOneFollow);
		latestFollowup = encounterQuery.getLatestDateByFollowUpDate(cohortWithAtleastOneFollow, null, new Date());
		
		List<Person> persons = LineListUtilities.sortPatientByName(artPatientListQuery
		        .getPersons(cohortWithAtleastOneFollow));
		
		loadColumnDictionary(latestFollowup, cohortWithAtleastOneFollow);
		
		DataSetRow row;
		
		if (!persons.isEmpty()) {
			
			row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("#", "#", Integer.class), "TOTAL");
			row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", Integer.class), persons.size());
			
			dataSet.addRow(row);
		} else {
			dataSet.addRow(LineListUtilities.buildEmptyRow(Arrays.asList("#", "Patient Name", "MRN", "UAN",
			    "Age at Enrollment", "Current Age", "Sex", "Mobile No.", "Enrollment Date", "HIV Confirmed Date",
			    "ART Start Date", "Days Difference", "Latest Follow-up Date", "Latest Follow-up Status", "Latest Regimen",
			    "Latest ARV Dose Days", "Latest Adherence", "Next Visit Date", "TI?")));
		}
		int i = 1;
		for (Person person : persons) {
			
			Date registrationDate = artPatientListLineListQuery
			        .getDate(registrationDateDictionary.get(person.getPersonId()));
			Date hivConfirmedDate = artPatientListLineListQuery
			        .getDate(hivConfirmedDateDictionary.get(person.getPersonId()));
			Date artStartDate = artPatientListLineListQuery.getDate(artStartDateDictionary.get(person.getPersonId()));
			Date latestFollowupDate = artPatientListLineListQuery.getDate(latestFollowupDateDictionary.get(person
			        .getPersonId()));
			Date nextVisitDate = artPatientListLineListQuery.getDate(nextVisitDateDictionary.get(person.getPersonId()));
			row = new DataSetRow();
			
			row.addColumnValue(new DataSetColumn("#", "#", Integer.class), i++);
			row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", String.class), person.getNames());
			addColumnValue("MRN", "MRN", mrnIdentifierHashMap, row, person);
			addColumnValue("UAN", "UAN", uanIdentifierHashMap, row, person);
			row.addColumnValue(new DataSetColumn("AgeAtEnrollment", "Age at Enrollment", String.class),
			    getAgeByEnrollmentDate(person.getBirthDateTime(), registrationDate));
			row.addColumnValue(new DataSetColumn("Current Age", "Current Age", String.class), person.getAge(new Date()));
			row.addColumnValue(new DataSetColumn("Sex", "Sex", Integer.class), person.getGender());
			row.addColumnValue(new DataSetColumn("Mobile No.", "Mobile No.", String.class),
			    LineListUtilities.getPhone(person.getActiveAttributes()));
			row.addColumnValue(new DataSetColumn("enrollmentDate", "Enrollment Date", Integer.class),
			    artPatientListLineListQuery.getEthiopianDate(registrationDate));
			row.addColumnValue(new DataSetColumn("HIV Confirmed Date", "HIV Confirmed Date", Integer.class),
			    artPatientListLineListQuery.getEthiopianDate(hivConfirmedDate));
			row.addColumnValue(new DataSetColumn("ART Start Date", "ART Start Date", Integer.class),
			    artPatientListLineListQuery.getEthiopianDate(artStartDate));
			row.addColumnValue(new DataSetColumn("Days Difference", "Days Difference", Integer.class),
			    LineListUtilities.getDayDifference(artStartDate, hivConfirmedDate));
			row.addColumnValue(new DataSetColumn("Latest Follow-up Date", "Latest Follow-up Date", Integer.class),
			    artPatientListLineListQuery.getEthiopianDate(latestFollowupDate));
			row.addColumnValue(new DataSetColumn("Latest Follow-up Status", "Latest Follow-up Status", Integer.class),
			    latestFollowupStatus.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Latest Regimen", "Latest Regimen", Integer.class),
			    regimen.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Latest ARV Dose Days", "Latest ARV Dose Days", Integer.class),
			    arvDoseDays.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Latest Adherence", "Latest Adherence", Integer.class),
			    adherence.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Next Visit Date", "Next Visit Date", Integer.class),
			    artPatientListLineListQuery.getEthiopianDate(nextVisitDate));
			row.addColumnValue(new DataSetColumn("TI?", "TI?", Integer.class), tiHashMap.get(person.getPersonId()));
			
			dataSet.addRow(row);
		}
		
		return dataSet;
	}
	
	private void loadColumnDictionary(List<Integer> encounters, Cohort cohort) {
		uanIdentifierHashMap = artPatientListLineListQuery.getIdentifier(cohort, UAN_PATIENT_IDENTIFIERS);
		registrationDateDictionary = artPatientListLineListQuery.getObsValueDate(encounters, ART_REGISTRATION_DATE, cohort,
		    INTAKE_A_ENCOUNTER_TYPE);
		hivConfirmedDateDictionary = artPatientListLineListQuery.getObsValueDate(encounters, HIV_CONFIRMED_DATE, cohort);
		artStartDateDictionary = artPatientListLineListQuery.getObsValueDate(encounters, ART_START_DATE, cohort);
		latestFollowupDateDictionary = artPatientListLineListQuery.getObsValueDate(encounters, FOLLOW_UP_DATE, cohort);
		latestFollowupStatus = artPatientListLineListQuery.getByResult(FOLLOW_UP_STATUS, cohort, encounters);
		regimen = artPatientListLineListQuery.getByResult(REGIMEN, cohort, encounters);
		arvDoseDays = artPatientListLineListQuery.getByResult(ART_DISPENSE_DOSE, cohort, encounters);
		adherence = artPatientListLineListQuery.getByResult(ARV_ADHERENCE, cohort, encounters);
		nextVisitDateDictionary = artPatientListLineListQuery.getObsValueDate(encounters, NEXT_VISIT_DATE, cohort);
		tiHashMap = artPatientListLineListQuery.getConceptName(encounters, cohort, REASON_FOR_ART_ELIGIBILITY);
	}
	
	private void addColumnValue(String name, String label, HashMap<Integer, Object> object, DataSetRow row, Person person) {
		row.addColumnValue(new DataSetColumn(name, label, String.class), object.get(person.getPersonId()));
	}
	
	private String getAgeByEnrollmentDate(Object dateOfBirth, Object enrollmentDate) {
		if (Objects.isNull(dateOfBirth)) {
			return "";
		}
		Date birthDate = (Date) dateOfBirth;
		if (Objects.isNull(enrollmentDate)) {
			return birthDate.toString();
		}
		Date enrDate = (Date) enrollmentDate;
		return String.valueOf(EthiOhriUtil.getAgeInMonth(birthDate, enrDate));
		
	}
	
	public Cohort getCohortOnlyHaveMRN(Cohort cohort) {
        mrnIdentifierHashMap = artPatientListLineListQuery.getIdentifier(cohort, MRN_PATIENT_IDENTIFIERS);
        for (Map.Entry<Integer, Object> entry : mrnIdentifierHashMap.entrySet()) {
            if (Objects.isNull(entry.getValue())) {
                cohort.getMemberships().removeIf(c -> c.getPatientId().equals(entry.getKey()));
            }
        }
        return cohort;
    }
}
