package org.openmrs.module.ohrireports.datasetevaluator.linelist.artPatient;

import org.jetbrains.annotations.Nullable;
import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.ARTPatientListQuery;
import org.openmrs.module.ohrireports.constants.*;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.ARTPatientListDatasetDefinition;
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
import sun.security.pkcs11.wrapper.CK_DATE;

import java.util.*;

/**
 * Here is the sudo code for getting the encounter for the ART patient list If Patient Enrollment
 * Date is <= Report Date AND If Patient has MRN AND If Patient has at least 1 ART follow-up record
 * ONLY THEN count the record
 */
@Handler(supports = { ARTPatientListDatasetDefinition.class })
public class ARTPatientListDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private ARTPatientListQuery artPatientListQuery;
	
	@Autowired
	private ARTPatientListLineListQuery artPatientLineListQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		ARTPatientListDatasetDefinition _dataSetDefinition = (ARTPatientListDatasetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(_dataSetDefinition, evalContext);
		
		SimpleDataSet dataSet1 = EthiOhriUtil.isValidReportDateRange(_dataSetDefinition.getStartDate(),
		    _dataSetDefinition.getEndDate(), dataSet);
		if (dataSet1 != null)
			return dataSet1;
		
		if (_dataSetDefinition.getStartDate() == null || _dataSetDefinition.getEndDate() == null) {
			_dataSetDefinition.setEndDate(new Date());
			artPatientListQuery.generateReport();
			
		} else if (FollowUpConstant.getUuidRepresentation(_dataSetDefinition.getFollowupStatus()).equalsIgnoreCase("all")) {
			artPatientListQuery.generateReport(_dataSetDefinition.getStartDate(), _dataSetDefinition.getEndDate());
		} else {
			artPatientListQuery.generateReport(_dataSetDefinition.getStartDate(), _dataSetDefinition.getEndDate(),
			    FollowUpConstant.getUuidRepresentation(_dataSetDefinition.getFollowupStatus()));
		}
		
		List<Person> persons = LineListUtilities.sortPatientByName(artPatientListQuery.getPersons(artPatientListQuery
		        .getBaseCohort()));
		
		getCohortOnlyHaveMRN(artPatientListQuery.getBaseCohort());
		loadColumnDictionary(artPatientListQuery.getFollowupEncounter(), artPatientListQuery.getBaseCohort());
		
		DataSetRow row;
		
		if (!persons.isEmpty()) {
			
			row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("#", "#", Integer.class), "TOTAL");
			row.addColumnValue(new DataSetColumn("Patient UUID", "Patient UUID", String.class), "");
			row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", Integer.class), persons.size());
			
			dataSet.addRow(row);
		} else {
			dataSet.addRow(LineListUtilities.buildEmptyRow(Arrays.asList("#", "Patient UUID", "Patient Name", "MRN", "UAN",
			    "Age at Enrollment", "Current Age", "Sex", "Mobile No.", "Enrollment Date", "HIV Confirmed Date",
			    "ART Start Date", "Time to ART Initiation (in days)", "Latest Follow-up Date", "Latest Follow-up Status",
			    "Latest Regimen", "Latest ARV Dose Days", "Adherence", "Next Visit Date", "Last TX_CURR Date E.C", "TI?",
			    "TI Date", "Regions", "Zone", "Woreda", "Kebele", "House #", "Mobile #")));
		}
		int rowNumber = 1;
		for (Person person : persons) {
			
			Date registrationDate = artPatientLineListQuery.getDate(registrationDateDictionary.get(person.getPersonId()));
			Date hivConfirmedDate = artPatientLineListQuery.getDate(hivConfirmedDateDictionary.get(person.getPersonId()));
			Date artStartDate = artPatientLineListQuery.getDate(artStartDateDictionary.get(person.getPersonId()));
			Date latestFollowupDate = artPatientLineListQuery
			        .getDate(latestFollowupDateDictionary.get(person.getPersonId()));
			Date nextVisitDate = artPatientLineListQuery.getDate(nextVisitDateDictionary.get(person.getPersonId()));
			Date lastCurrDate = artPatientLineListQuery.getDate(lastCurrDateHashMap.get(person.getPersonId()));
			Date tiDate = artPatientLineListQuery.getDate(tiDateHashMap.get(person.getPersonId()));
			row = new DataSetRow();
			
			row.addColumnValue(new DataSetColumn("#", "#", Integer.class), rowNumber++);
			row.addColumnValue(new DataSetColumn("Patient UUID", "Patient UUID", String.class), person.getUuid());
			
			row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", String.class), person.getNames());
			addColumnValue("MRN", "MRN", mrnIdentifierHashMap, row, person);
			addColumnValue("UAN", "UAN", uanIdentifierHashMap, row, person);
			row.addColumnValue(new DataSetColumn("AgeAtEnrollment", "Age at Enrollment", String.class),
			    getAgeByEnrollmentDate(person.getBirthdate(), registrationDate));
			row.addColumnValue(new DataSetColumn("Current Age", "Current Age", String.class), person.getAge(new Date()));
			row.addColumnValue(new DataSetColumn("Sex", "Sex", Integer.class), person.getGender());
			row.addColumnValue(new DataSetColumn("Mobile No.", "Mobile No.", String.class),
			    LineListUtilities.getPhone(person.getActiveAttributes()));
			row.addColumnValue(new DataSetColumn("enrollmentDate", "Enrollment Date", Integer.class),
			    artPatientLineListQuery.getEthiopianDate(registrationDate));
			row.addColumnValue(new DataSetColumn("HIV Confirmed Date", "HIV Confirmed Date", Integer.class),
			    artPatientLineListQuery.getEthiopianDate(hivConfirmedDate));
			row.addColumnValue(new DataSetColumn("ART Start Date", "ART Start Date", Integer.class),
			    artPatientLineListQuery.getEthiopianDate(artStartDate));
			row.addColumnValue(new DataSetColumn("Days Difference", "Time to ART Initiation (in days)", Integer.class),
			    LineListUtilities.getDayDifference(artStartDate, hivConfirmedDate));
			row.addColumnValue(new DataSetColumn("Latest Follow-up Date", "Latest Follow-up Date", Integer.class),
			    artPatientLineListQuery.getEthiopianDate(latestFollowupDate));
			row.addColumnValue(new DataSetColumn("Latest Follow-up Status", "Latest Follow-up Status", Integer.class),
			    latestFollowupStatus.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Latest Regimen", "Latest Regimen", Integer.class),
			    regimen.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Regimen Dose Days", "Regimen Dose Days", Integer.class),
			    arvDoseDays.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Adherence", "Adherence", Integer.class),
			    adherence.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Next Visit Date", "Next Visit Date", Integer.class),
			    artPatientLineListQuery.getEthiopianDate(nextVisitDate));
			row.addColumnValue(new DataSetColumn("Last TX_CURR Date E.C", "Last TX_CURR Date E.C", String.class),
			    artPatientLineListQuery.getEthiopianDate(lastCurrDate));
			row.addColumnValue(new DataSetColumn("TI?", "TI?", Integer.class), tiHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("TI Date", "TI Date", String.class),
			    artPatientLineListQuery.getEthiopianDate(tiDate));
			
			row.addColumnValue(new DataSetColumn("Regions", "Regions", String.class),
			    getStateProvince(person.getPersonAddress()));
			row.addColumnValue(new DataSetColumn("Zone", "Zone", String.class), getCountyDistrict(person.getPersonAddress()));
			row.addColumnValue(new DataSetColumn("Woreda", "Woreda", String.class),
			    getCityVillage(person.getPersonAddress()));
			row.addColumnValue(new DataSetColumn("Kebele", "Kebele", String.class), person.getAttribute("kebele"));
			row.addColumnValue(new DataSetColumn("House #", "House #", String.class), person.getAttribute("House Number"));
			row.addColumnValue(new DataSetColumn("Mobile #", "Mobile #", String.class),
			    LineListUtilities.getPhone(person.getActiveAttributes()));
			dataSet.addRow(row);
		}
		
		return dataSet;
	}
	
	private void loadColumnDictionary(List<Integer> encounters, Cohort cohort) {
		
		uanIdentifierHashMap = artPatientLineListQuery.getIdentifier(cohort, Identifiers.UAN_PATIENT_IDENTIFIERS);
		registrationDateDictionary = artPatientLineListQuery.getObsValueDate(null,
		    FollowUpConceptQuestions.ART_REGISTRATION_DATE, cohort, EncounterType.REGISTRATION_ENCOUNTER_TYPE);
		hivConfirmedDateDictionary = artPatientLineListQuery.getObsValueDate(null,
		    IntakeAConceptQuestions.HIV_CONFIRMED_DATE, cohort, EncounterType.INTAKE_A_ENCOUNTER_TYPE);
		lastCurrDateHashMap = artPatientLineListQuery.getObsValueDate(encounters,
		    FollowUpConceptQuestions.TREATMENT_END_DATE, cohort);
		artStartDateDictionary = artPatientLineListQuery.getObsValueDate(encounters,
		    FollowUpConceptQuestions.ART_START_DATE, cohort);
		latestFollowupDateDictionary = artPatientLineListQuery.getObsValueDate(encounters,
		    FollowUpConceptQuestions.FOLLOW_UP_DATE, cohort);
		latestFollowupStatus = artPatientLineListQuery.getConceptLabel(encounters, cohort,
		    FollowUpConceptQuestions.FOLLOW_UP_STATUS);
		regimen = artPatientLineListQuery.getByResult(FollowUpConceptQuestions.REGIMEN, cohort, encounters);
		arvDoseDays = artPatientLineListQuery.getByResult(FollowUpConceptQuestions.ART_DISPENSE_DOSE, cohort, encounters);
		adherence = artPatientLineListQuery.getByResult(FollowUpConceptQuestions.ARV_ADHERENCE, cohort, encounters);
		nextVisitDateDictionary = artPatientLineListQuery.getObsValueDate(encounters,
		    FollowUpConceptQuestions.NEXT_VISIT_DATE, cohort);
		tiHashMap = artPatientLineListQuery.getConceptName(artPatientListQuery.getFirstFollowUp(), cohort,
		    ConceptAnswer.TRANSFERRED_IN);
		tiDateHashMap = artPatientLineListQuery.getObsValueDate(artPatientListQuery.getFirstFollowUp(),
				FollowUpConceptQuestions.FOLLOW_UP_DATE,cohort
		   );
	}
	
	private void addColumnValue(String name, String label, HashMap<Integer, Object> object, DataSetRow row, Person person) {
		row.addColumnValue(new DataSetColumn(name, label, String.class), object.get(person.getPersonId()));
	}
	
	private String getAgeByEnrollmentDate(Object dateOfBirth, Object enrollmentDate) {
		if (Objects.isNull(dateOfBirth) || Objects.isNull(enrollmentDate)) {
			return "--";
		}
		
		Date birthDate = (Date) dateOfBirth;
		Date enrDate = (Date) enrollmentDate;
		
		return String.valueOf(EthiOhriUtil.getAgeInYear(birthDate, enrDate));
		
	}
	
	public void getCohortOnlyHaveMRN(Cohort cohort) {
        mrnIdentifierHashMap = artPatientLineListQuery.getIdentifier(cohort, Identifiers.MRN_PATIENT_IDENTIFIERS);
        for (Map.Entry<Integer, Object> entry : mrnIdentifierHashMap.entrySet()) {
            if (Objects.isNull(entry.getValue())) {
                cohort.getMemberships().removeIf(c -> c.getPatientId().equals(entry.getKey()));
            }
        }
    }
	
	private HashMap<Integer, Object> mrnIdentifierHashMap, uanIdentifierHashMap, registrationDateDictionary,
	        hivConfirmedDateDictionary, artStartDateDictionary, latestFollowupDateDictionary, latestFollowupStatus, regimen,
	        arvDoseDays, adherence, nextVisitDateDictionary, tiHashMap, tiDateHashMap, lastCurrDateHashMap;
	
	private static String getCityVillage(PersonAddress address) {
		return Objects.isNull(address) ? "--" : address.getStateProvince() == null ? "--" : address.getCityVillage();
	}
	
	private static String getCountyDistrict(PersonAddress address) {
		return Objects.isNull(address) ? "--" : address.getStateProvince() == null ? "--" : address.getCountyDistrict();
	}
	
	private static String getStateProvince(PersonAddress address) {
		return Objects.isNull(address) ? "--" : address.getStateProvince() == null ? "--" : address.getStateProvince();
	}
}
