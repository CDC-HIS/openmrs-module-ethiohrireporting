package org.openmrs.module.ohrireports.datasetevaluator.linelist.art;

import java.util.*;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.openmrs.module.ohrireports.cohorts.util.EthiOhriUtil;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.TxCurrDataSetDefinition;
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

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;
import static org.openmrs.module.ohrireports.RegimentConstant.DSD_CATEGORY;

@Handler(supports = { TxCurrDataSetDefinition.class })
public class TxCurrDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private ArtQuery artQuery;
	
	@Autowired
	private EncounterQuery encounterQuery;

	List<Person> unsortedPersons = new ArrayList<>();
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		TxCurrDataSetDefinition hdsd = (TxCurrDataSetDefinition) dataSetDefinition;
		SimpleDataSet data = new SimpleDataSet(dataSetDefinition, evalContext);
		
		if (hdsd.getEndDate() == null) {
			hdsd.setEndDate(new Date());
		}
		
		PatientQueryService patientQuery = Context.getService(PatientQueryService.class);
		List<Integer> latestEncounters = encounterQuery.getAliveFollowUpEncounters(null, hdsd.getEndDate());
		Cohort cohort = patientQuery.getActiveOnArtCohort("", null, hdsd.getEndDate(), null, latestEncounters);
		
		unsortedPersons = patientQuery.getPersons(cohort);
		
		List<Person> persons = LineListUtilities.sortPatientByName(unsortedPersons);
		HashMap<Integer, Object> familyPlanningHashMap = artQuery.getConceptName(latestEncounters, cohort,
		    FAMILY_PLANNING_METHODS);
		HashMap<Integer, Object> mrnIdentifierHashMap = artQuery.getIdentifier(cohort, MRN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> uanIdentifierHashMap = artQuery.getIdentifier(cohort, UAN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> registrationDateDictionary = artQuery.getObsValueDate(null, ART_REGISTRATION_DATE, cohort,
		    INTAKE_A_ENCOUNTER_TYPE);
		HashMap<Integer, Object> weight = artQuery.getByValueNumeric(WEIGHT, cohort, latestEncounters);
		HashMap<Integer, Object> cd4Count = artQuery.getByValueNumeric(ADULT_CD4_COUNT, cohort, latestEncounters);
		HashMap<Integer, Object> hivConfirmedDate = artQuery.getObsValueDate(latestEncounters, HIV_CONFIRMED_DATE, cohort);
		HashMap<Integer, Object> artStartDate = artQuery.getObsValueDate(latestEncounters, ART_START_DATE, cohort);
		HashMap<Integer, Object> followUpDate = artQuery.getObsValueDate(latestEncounters, FOLLOW_UP_DATE, cohort);
		HashMap<Integer, Object> statusHashMap = artQuery.getFollowUpStatus(latestEncounters, cohort);
		HashMap<Integer, Object> regimentHashMap = artQuery.getRegiment(latestEncounters, cohort);
		HashMap<Integer, Object> tbScreeningResult = artQuery.getByResult(TB_SCREENED_RESULT, cohort, latestEncounters);
		HashMap<Integer, Object> adherenceHashMap = artQuery.getByResult(ARV_ADHERENCE, cohort, latestEncounters);
		HashMap<Integer, Object> pregnancyStatus = artQuery.getByResult(PREGNANCY_STATUS, cohort, latestEncounters);
		HashMap<Integer, Object> breastfeedingStatus = artQuery.getByResult(CURRENTLY_BREAST_FEEDING_CHILD, cohort,
		    latestEncounters);
		HashMap<Integer, Object> nutritionalStatusHashMap = artQuery.getByResult(NUTRITIONAL_STATUS, cohort,
		    latestEncounters);
		HashMap<Integer, Object> dispensDayHashMap = artQuery
		        .getConceptName(latestEncounters, cohort, ARV_DISPENSED_IN_DAYS);

		HashMap<Integer, Object> dsdCategoryHashMap = artQuery.getConceptName(latestEncounters, cohort, DSD_CATEGORY);
		HashMap<Integer, Object> tptStartDateHashMap = artQuery.getObsValueDate(latestEncounters, TPT_START_DATE, cohort);
		HashMap<Integer, Object> tptCompletedDateHashMap = artQuery.getObsValueDate(latestEncounters, TPT_COMPLETED_DATE,
		    cohort);
		HashMap<Integer, Object> tbTreatmentCompletedDateHashMap = artQuery.getObsValueDate(latestEncounters,
		    TB_TREATMENT_COMPLETED_DATE, cohort);
		HashMap<Integer, Object> vlSentDateHashMap = artQuery.getObsValueDate(latestEncounters, VL_SENT_DATE, cohort);
		HashMap<Integer, Object> vlStatusHashMap = artQuery.getByResult(VIRAL_LOAD_STATUS, cohort, latestEncounters);
		HashMap<Integer, Object> nextVisitDateHashMap = artQuery.getObsValueDate(latestEncounters, NEXT_VISIT_DATE, cohort);
		HashMap<Integer, Object> treatmentEndDateHashMap = artQuery.getObsValueDate(latestEncounters, TREATMENT_END_DATE,
		    cohort);
		
		DataSetRow row;
		
		if (!persons.isEmpty()) {
			
			row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("#", "#", Integer.class), "TOTAL");
			row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", Integer.class), persons.size());
			
			data.addRow(row);
		} else {
			data.addRow(LineListUtilities.buildEmptyRow(Arrays.asList("#", "Patient Name", "MRN", "UAN",
			    "Age at Enrollment", "Current Age", "Sex", "Weight", "CD4", "HIV Confirmed Date in E.C",
			    "ART Start Date in E.C", "TB Screening Result", "Follow-up Date in E.C", "Follow-up Status", "Regimen",
			    "ARV Dose Days", "Adherence", "DSD Category", "Nutritional Status", "Familiy Planning Method", "Pregnant?",
			    "Breastfeeding?", "On PMTCT?", "TPT Start Date", "TPT Completed Date", "TB Treatment Completed Date",
			    "VL Sent Date", "VL Status", "Next Visit Date in E.C", "Treatment End Date in E.C.", "Mobile No.")));
		}
		int i = 1;
		for (Person person : persons) {
			Date registrationDate = artQuery.getDate(registrationDateDictionary.get(person.getPersonId()));
			Date hivConfirmedDateET = artQuery.getDate(hivConfirmedDate.get(person.getPersonId()));
			Date artStartDateET = artQuery.getDate(artStartDate.get(person.getPersonId()));
			Date followupDateET = artQuery.getDate(followUpDate.get(person.getPersonId()));
			Date tptStartDateET = artQuery.getDate(tptStartDateHashMap.get(person.getPersonId()));
			Date tptCompletedDateET = artQuery.getDate(tptCompletedDateHashMap.get(person.getPersonId()));
			Date tbTreatmentCompletedDateET = artQuery.getDate(tbTreatmentCompletedDateHashMap.get(person.getPersonId()));
			Date vlSentDateET = artQuery.getDate(vlSentDateHashMap.get(person.getPersonId()));
			Date treatmentEndDateET = artQuery.getDate(treatmentEndDateHashMap.get(person.getPersonId()));
			
			// row should be filled with only patient data
			row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("#", "#", Integer.class), i++);
			row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", String.class), person.getNames());
			row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class),
			    getStringIdentifier(mrnIdentifierHashMap.get(person.getPersonId())));
			row.addColumnValue(new DataSetColumn("UAN", "UAN", Integer.class),
			    uanIdentifierHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Age at Enrollment", "Age at Enrollment", String.class),
			    person.getAge(registrationDate));
			row.addColumnValue(new DataSetColumn("Current Age", "Current Age", Integer.class),
			    person.getAge(hdsd.getEndDate()));
			row.addColumnValue(new DataSetColumn("Sex", "Sex", String.class), person.getGender());
			row.addColumnValue(new DataSetColumn("Weight", "Weight", String.class), weight.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("CD4", "CD4", String.class), cd4Count.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("HIVConfirmedDateETH", "HIV Confirmed Date in E.C", String.class),
			    artQuery.getEthiopianDate(hivConfirmedDateET));
			row.addColumnValue(new DataSetColumn("ARTStartDateETH", "ART Start Date in E.C", String.class),
			    artQuery.getEthiopianDate(artStartDateET));
			row.addColumnValue(new DataSetColumn("Follow-up Date in E.C", "Follow-up Date in E.C", String.class),
			    artQuery.getEthiopianDate(followupDateET));
			row.addColumnValue(new DataSetColumn("Follow-up Status", "Follow-up Status", String.class),
			    statusHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Regimen", "Regimen", String.class),
			    regimentHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("ARV Dose Days", "ARV Dose Days", String.class),
			    dispensDayHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Adherence", "Adherence", String.class),
			    adherenceHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("DSD Category", "DSD Category", String.class),
			    dsdCategoryHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Nutritional Status", "Nutritional Status", String.class),
			    nutritionalStatusHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Family Planning Method", "Family Planning Method", String.class),
			    familyPlanningHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("PregnancyStatus", "Pregnant?", String.class),
			    pregnancyStatus.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("BreastfeedingStatus", "Breastfeeding?", String.class),
			    breastfeedingStatus.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("On PMTCT?", "On PMTCT?", String.class), "");
			row.addColumnValue(new DataSetColumn("TBScreeningResult", "TB Screening Result", String.class),
			    tbScreeningResult.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("TPT Start Date", "TPT Start Date E.T.", String.class),
			    artQuery.getEthiopianDate(tptStartDateET));
			row.addColumnValue(new DataSetColumn("TPT Completed Date", "TPT Completed Date E.T.", String.class),
			    artQuery.getEthiopianDate(tptCompletedDateET));
			row.addColumnValue(
			    new DataSetColumn("TB Treatment Completed Date", "TB Treatment Completed Date", String.class),
			    artQuery.getEthiopianDate(tbTreatmentCompletedDateET));
			row.addColumnValue(new DataSetColumn("VL Sent Date", "VL Sent Date", String.class),
			    artQuery.getEthiopianDate(vlSentDateET));
			row.addColumnValue(new DataSetColumn("VL Status", "VL Status", String.class),
			    vlStatusHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Next Visit Date in E.C", "Next Visit Date in E.C", String.class),
			    nextVisitDateHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("TreatmentEndDate", "Treatment End Date in E.C.", Date.class),
			    artQuery.getEthiopianDate(treatmentEndDateET));
			row.addColumnValue(new DataSetColumn("Mobile No.", "Mobile No.", String.class),
			    getPhone(person.getActiveAttributes()));
			
			data.addRow(row);
		}
		return data;
	}
	
	private String getStringIdentifier(Object patientIdentifier) {
		return Objects.isNull(patientIdentifier) ? "--" : patientIdentifier.toString();
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
	
	private String getPhone(List<PersonAttribute> activeAttributes) {
		for (PersonAttribute personAttribute : activeAttributes) {
			if (personAttribute.getValue().startsWith("09") || personAttribute.getValue().startsWith("+251")) {
				return personAttribute.getValue();
			}
		}
		return "";
	}
	
}
