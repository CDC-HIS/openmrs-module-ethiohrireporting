package org.openmrs.module.ohrireports.datasetevaluator.linelist.PreExposureProphylaxis;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.PreExposureProphylaxisQuery;
import org.openmrs.module.ohrireports.constants.EncounterType;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.openmrs.module.ohrireports.constants.Identifiers;
import org.openmrs.module.ohrireports.constants.PrepConceptQuestions;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.PreExposureProphylaxisDataSetDefinition;
import org.openmrs.module.ohrireports.datasetevaluator.linelist.LineListUtilities;
import org.openmrs.module.ohrireports.reports.linelist.PEPReport;
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

@Handler(supports = { PreExposureProphylaxisDataSetDefinition.class })
public class PreExposureProphylaxisDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	PreExposureProphylaxisQuery preExposureProphylaxisQuery;
	
	@Autowired
	private PreExposureProphylaxisLineListQuery preExposureProphylaxisLineListQuery;
	
	PreExposureProphylaxisDataSetDefinition _dataSetDefinition;
	
	List<Integer> screeningEncounter;
	
	List<Integer> baseFollowUpEncounter;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		_dataSetDefinition = (PreExposureProphylaxisDataSetDefinition) dataSetDefinition;
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
		
		if (Objects.isNull(_dataSetDefinition.getEndDate()))
			_dataSetDefinition.setEndDate(Calendar.getInstance().getTime());
		
		preExposureProphylaxisQuery.setStartDate(_dataSetDefinition.getStartDate());
		preExposureProphylaxisQuery.setEndDate(_dataSetDefinition.getEndDate());
		baseFollowUpEncounter = preExposureProphylaxisQuery.getBaseFollowupEncounter();
		screeningEncounter = preExposureProphylaxisQuery.getBaseScreeningEncounter();
		
		Cohort baseCohort = preExposureProphylaxisQuery.loadPrepCohort();
		List<Person> persons = LineListUtilities.sortPatientByName(preExposureProphylaxisQuery.getPersons(baseCohort));
		
		HashMap<Integer, Object> mrnIdentifierHashMap = preExposureProphylaxisLineListQuery.getIdentifier(baseCohort,
		    Identifiers.MRN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> uniqueIdentificationCode = preExposureProphylaxisLineListQuery.getConceptValue(
		    PrepConceptQuestions.UIC, baseCohort, EncounterType.PREP_SCREENING_ENCOUNTER_TYPE, screeningEncounter);
		
		HashMap<Integer, Object> screenedDateHashMap = preExposureProphylaxisLineListQuery.getScreeningObsValueDate(
		    PrepConceptQuestions.PREP_SCREENED_DATE, baseCohort, screeningEncounter);
		HashMap<Integer, Object> prepStartedHashMap = preExposureProphylaxisLineListQuery.getConceptName(screeningEncounter,
		    PEPReport.PR_EP_STARTED, baseCohort, EncounterType.PREP_SCREENING_ENCOUNTER_TYPE);
		HashMap<Integer, Object> prepStartDateHashMap = preExposureProphylaxisLineListQuery.getScreeningObsValueDate(
		    PrepConceptQuestions.PREP_STARTED_DATE, baseCohort, screeningEncounter);
		HashMap<Integer, Object> typeOfClient = preExposureProphylaxisLineListQuery.getConceptName(screeningEncounter,
		    PrepConceptQuestions.PREP_TYPE_OF_CLIENT, baseCohort, EncounterType.PREP_SCREENING_ENCOUNTER_TYPE);
		
		HashMap<Integer, Object> followUpDateHashMapFromScreening = preExposureProphylaxisLineListQuery
		        .getScreeningObsValueDate(PrepConceptQuestions.PREP_SCREENED_DATE, baseCohort, screeningEncounter);
		HashMap<Integer, Object> statusFromScreening = preExposureProphylaxisLineListQuery.getConceptName(
		    screeningEncounter, PrepConceptQuestions.PREP_TYPE_OF_CLIENT, baseCohort,
		    EncounterType.PREP_SCREENING_ENCOUNTER_TYPE);
		HashMap<Integer, Object> prepRegimenFromScreening = preExposureProphylaxisLineListQuery.getConceptName(
		    screeningEncounter, PrepConceptQuestions.PREP_REGIMEN, baseCohort, EncounterType.PREP_SCREENING_ENCOUNTER_TYPE);
		HashMap<Integer, Object> prepDoseFromScreening = preExposureProphylaxisLineListQuery.getConceptName(
		    screeningEncounter, PrepConceptQuestions.PREP_DOSE_DAY, baseCohort, EncounterType.PREP_SCREENING_ENCOUNTER_TYPE);
		HashMap<Integer, Object> hivTestFinalResultFromScreening = preExposureProphylaxisLineListQuery.getConceptName(
		    screeningEncounter, PrepConceptQuestions.HIV_TEST_FINAL_RESULT, baseCohort,
		    EncounterType.PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		HashMap<Integer, Object> pregnantFromScreening = preExposureProphylaxisLineListQuery.getConceptName(
		    screeningEncounter, FollowUpConceptQuestions.PREGNANCY_STATUS, baseCohort,
		    EncounterType.PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		HashMap<Integer, Object> familyPlanningMethodFromScreening = preExposureProphylaxisLineListQuery.getConceptName(
		    screeningEncounter, FollowUpConceptQuestions.FAMILY_PLANNING_METHODS, baseCohort,
		    EncounterType.PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		HashMap<Integer, Object> tbScreenedResultFromScreening = preExposureProphylaxisLineListQuery.getConceptName(
		    screeningEncounter, FollowUpConceptQuestions.TB_SCREENED_RESULT, baseCohort,
		    EncounterType.PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		HashMap<Integer, Object> stiScreenResultFromScreening = preExposureProphylaxisLineListQuery.getConceptName(
		    screeningEncounter, PrepConceptQuestions.STI_SCREENING_RESULT, baseCohort,
		    EncounterType.PREP_SCREENING_ENCOUNTER_TYPE);
		HashMap<Integer, Object> eGFREstimateFromScreening = preExposureProphylaxisLineListQuery.getConceptName(
		    screeningEncounter, PrepConceptQuestions.EGFR_ESTIMATE, baseCohort, EncounterType.PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		HashMap<Integer, Object> followUpDateHashMap = preExposureProphylaxisLineListQuery.getObsValueDate(
		    baseFollowUpEncounter, FollowUpConceptQuestions.FOLLOW_UP_DATE, baseCohort,
		    EncounterType.PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		HashMap<Integer, Object> status = preExposureProphylaxisLineListQuery.getConceptName(baseFollowUpEncounter,
		    PrepConceptQuestions.PREP_FOLLOWUP_STATUS, baseCohort, EncounterType.PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		HashMap<Integer, Object> prepRegimen = preExposureProphylaxisLineListQuery.getConceptName(
		    PrepConceptQuestions.PREP_REGIMEN, baseCohort, EncounterType.PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		HashMap<Integer, Object> prepDose = preExposureProphylaxisLineListQuery.getConceptName(baseFollowUpEncounter,
		    PrepConceptQuestions.PREP_DOSE_DAY, baseCohort, EncounterType.PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		HashMap<Integer, Object> hivTestFinalResult = preExposureProphylaxisLineListQuery.getConceptName(
		    baseFollowUpEncounter, PrepConceptQuestions.HIV_TEST_FINAL_RESULT, baseCohort,
		    EncounterType.PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		HashMap<Integer, Object> pregnant = preExposureProphylaxisLineListQuery.getConceptName(baseFollowUpEncounter,
		    FollowUpConceptQuestions.PREGNANCY_STATUS, baseCohort, EncounterType.PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		HashMap<Integer, Object> familyPlanningMethod = preExposureProphylaxisLineListQuery.getConceptName(
		    baseFollowUpEncounter, FollowUpConceptQuestions.FAMILY_PLANNING_METHODS, baseCohort,
		    EncounterType.PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		HashMap<Integer, Object> tbScreenedResult = preExposureProphylaxisLineListQuery.getConceptName(
		    baseFollowUpEncounter, FollowUpConceptQuestions.TB_SCREENED_RESULT, baseCohort,
		    EncounterType.PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		HashMap<Integer, Object> stiScreenResult = preExposureProphylaxisLineListQuery.getConceptName(baseFollowUpEncounter,
		    PrepConceptQuestions.STI_SCREENING_RESULT, baseCohort, EncounterType.PREP_SCREENING_ENCOUNTER_TYPE);
		HashMap<Integer, Object> eGFREstimate = preExposureProphylaxisLineListQuery.getConceptName(baseFollowUpEncounter,
		    PrepConceptQuestions.EGFR_ESTIMATE, baseCohort, EncounterType.PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		
		HashMap<Integer, Object> missedTablets = preExposureProphylaxisLineListQuery.getConceptName(baseFollowUpEncounter,
		    PrepConceptQuestions.PREP_MISSED_TABLETS, baseCohort, EncounterType.PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		HashMap<Integer, Object> nextVisitDateHashMap = preExposureProphylaxisLineListQuery.getObsValueDate(
		    baseFollowUpEncounter, PrepConceptQuestions.PREP_NEXT_VISIT_DATE, baseCohort,
		    EncounterType.PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		HashMap<Integer, Object> prepDoseEndDateHashMap = preExposureProphylaxisLineListQuery.getObsValueDate(
		    baseFollowUpEncounter, PrepConceptQuestions.PREP_DOSE_END_DATE, baseCohort,
		    EncounterType.PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		HashMap<Integer, Object> breastFeeding = preExposureProphylaxisLineListQuery
		        .getConceptName(baseFollowUpEncounter, FollowUpConceptQuestions.CURRENTLY_BREAST_FEEDING_CHILD, baseCohort,
		            EncounterType.PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		HashMap<Integer, Object> sideEffects = preExposureProphylaxisLineListQuery.getConceptName(baseFollowUpEncounter,
		    PrepConceptQuestions.SIDE_EFFECTS, baseCohort, EncounterType.PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		HashMap<Integer, Object> reasonToStopPrep = preExposureProphylaxisLineListQuery.getConceptName(
		    baseFollowUpEncounter, PrepConceptQuestions.REASON_FOR_STOPPING_PREP, baseCohort,
		    EncounterType.PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		HashMap<Integer, Object> contraindication = preExposureProphylaxisLineListQuery.getConceptName(screeningEncounter,
		    PrepConceptQuestions.CONTRADICTION_TO_PREP_MEDICINE, baseCohort, EncounterType.PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		HashMap<Integer, Object> selfIdentifyingFSW = preExposureProphylaxisLineListQuery.getConceptName(screeningEncounter,
		    PrepConceptQuestions.SELF_IDENTIFYING_FSW, baseCohort, EncounterType.PREP_SCREENING_ENCOUNTER_TYPE);
		HashMap<Integer, Object> haveHIVPositivePartner = preExposureProphylaxisLineListQuery.getConceptName(
		    screeningEncounter, PrepConceptQuestions.HAVE_HIV_POSITIVE_PARTNER, baseCohort,
		    EncounterType.PREP_SCREENING_ENCOUNTER_TYPE);
		HashMap<Integer, Object> prepProvisionSite = preExposureProphylaxisLineListQuery.getConceptName(screeningEncounter,
		    PrepConceptQuestions.PREP_PROVISION_SITE, baseCohort, EncounterType.PREP_SCREENING_ENCOUNTER_TYPE);
		HashMap<Integer, Object> referredFrom = preExposureProphylaxisLineListQuery.getConceptName(screeningEncounter,
		    PrepConceptQuestions.REFERRED_FROM, baseCohort, EncounterType.PREP_SCREENING_ENCOUNTER_TYPE);
		
		DataSetRow row;
		if (!persons.isEmpty()) {
			
			row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("#", "#", Integer.class), "TOTAL");
			row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", Integer.class), persons.size());
			dataSet.addRow(row);
		} else {
			dataSet.addRow(LineListUtilities.buildEmptyRow(Arrays.asList("#", "Patient Name", "MRN", "UAN", "Age", "Sex",
			    "UIC", "PrEP Screening Date in E.C.", "PrEP Started?", "PrEP Start Date in E.C", "Type of Client",
			    "PrEP Follow-up Date in E.C.", "PrEP Follow-up Status", "PrEP Regimen", "PrEP Dose", "Missed Tablets",
			    "Next Visit Date in E.C.", "Dose End Date in E.C.", "HIV Test Final Result", "Pregnant?", "Breast Feeding?",
			    "Family Planning Method", "TB Screening Result", "STI Screening Result", "eGFR Estimate", "Side Effects",
			    "Reason to stop PrEP", "Contraindication for PrEP Medicine", "Self-Identifying FSW", "Have HIV +ve Partner",
			    "PrEP Provision Site", "Referred From")));
		}
		int i = 1;
		for (Person person : persons) {
			Date screenedDate = preExposureProphylaxisLineListQuery.getDate(screenedDateHashMap.get(person.getPersonId()));
			Date prepStartDate = preExposureProphylaxisLineListQuery.getDate(prepStartDateHashMap.get(person.getPersonId()));
			Date followUpDate;
			if (followUpDateHashMap.get(person.getPersonId()) != null) {
				followUpDate = preExposureProphylaxisLineListQuery.getDate(followUpDateHashMap.get(person.getPersonId()));
			} else {
				followUpDate = preExposureProphylaxisLineListQuery.getDate(followUpDateHashMapFromScreening.get(person
				        .getPersonId()));
				status = statusFromScreening;
				prepRegimen = prepRegimenFromScreening;
				prepDose = prepDoseFromScreening;
				hivTestFinalResult = hivTestFinalResultFromScreening;
				pregnant = pregnantFromScreening;
				familyPlanningMethod = familyPlanningMethodFromScreening;
				tbScreenedResult = tbScreenedResultFromScreening;
				stiScreenResult = stiScreenResultFromScreening;
				eGFREstimate = eGFREstimateFromScreening;
			}
			
			Date nextVisitDate = preExposureProphylaxisLineListQuery.getDate(nextVisitDateHashMap.get(person.getPersonId()));
			Date prepDoseEndDate = preExposureProphylaxisLineListQuery.getDate(prepDoseEndDateHashMap.get(person
			        .getPersonId()));
			
			row = new DataSetRow();
			
			row.addColumnValue(new DataSetColumn("#", "#", Integer.class), i++);
			row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", String.class), person.getNames());
			addColumnValue("MRN", "MRN", mrnIdentifierHashMap, row, person);
			
			row.addColumnValue(new DataSetColumn("Age", "Age", String.class), person.getAge(_dataSetDefinition.getEndDate()));
			row.addColumnValue(new DataSetColumn("Sex", "Sex", String.class), person.getGender());
			addColumnValue("UIC", "UIC", uniqueIdentificationCode, row, person);
			row.addColumnValue(
			    new DataSetColumn("PrEP Screening Date in E.C.", "PrEP Screening Date in E.C.", String.class),
			    preExposureProphylaxisLineListQuery.getEthiopianDate(screenedDate));
			addColumnValue("PrEP Started?", "PrEP Started?", prepStartedHashMap, row, person);
			row.addColumnValue(new DataSetColumn("PrEP Start Date in E.C", "PrEP Start Date in E.C", String.class),
			    preExposureProphylaxisLineListQuery.getEthiopianDate(prepStartDate));
			addColumnValue("Type of Client", "Type of Client", typeOfClient, row, person);
			row.addColumnValue(
			    new DataSetColumn("PrEP Follow-up Date in E.C.", "PrEP Follow-up Date in E.C.", String.class),
			    preExposureProphylaxisLineListQuery.getEthiopianDate(followUpDate));
			addColumnValue("PrEP Follow-up Status", "PrEP Follow-up Status", status, row, person);
			addColumnValue("PrEP Regimen", "PrEP Regimen", prepRegimen, row, person);
			addColumnValue("PrEP Dose", "PrEP Dose", prepDose, row, person);
			addColumnValue("Missed Tablets", "Missed Tablets", missedTablets, row, person);
			row.addColumnValue(new DataSetColumn("Next Visit Date in E.C.", "Next Visit Date in E.C.", String.class),
			    preExposureProphylaxisLineListQuery.getEthiopianDate(nextVisitDate));
			row.addColumnValue(new DataSetColumn("Dose End Date in E.C.", "Dose End Date in E.C.", String.class),
			    preExposureProphylaxisLineListQuery.getEthiopianDate(prepDoseEndDate));
			addColumnValue("HIV Test Final Result", "HIV Test Final Result", hivTestFinalResult, row, person);
			addColumnValue("Pregnant?", "Pregnant?", pregnant, row, person);
			addColumnValue("Breast Feeding?", "Breast Feeding?", breastFeeding, row, person);
			addColumnValue("Family Planning Method", "Family Planning Method", familyPlanningMethod, row, person);
			addColumnValue("TB Screening Result", "TB Screening Result", tbScreenedResult, row, person);
			addColumnValue("STI Screening Result", "STI Screening Result", stiScreenResult, row, person);
			addColumnValue("eGFR Estimate", "eGFR Estimate", eGFREstimate, row, person);
			addColumnValue("Side Effects", "Side Effects", sideEffects, row, person);
			addColumnValue("Reason to stop PrEP", "Reason to stop PrEP", reasonToStopPrep, row, person);
			addColumnValue("Contraindication for PrEP Medicine", "Contraindication for PrEP Medicine", contraindication,
			    row, person);
			addColumnValue("Self-Identifying FSW ", "Self-Identifying FSW ", selfIdentifyingFSW, row, person);
			addColumnValue("Have HIV +ve Partner", "Have HIV +ve Partner", haveHIVPositivePartner, row, person);
			addColumnValue("PrEP Provision Site", "PrEP Provision Site", prepProvisionSite, row, person);
			addColumnValue("Referred From", "Referred From", referredFrom, row, person);
			dataSet.addRow(row);
			
		}
		return dataSet;
		
	}
	
	private void addColumnValue(String name, String label, HashMap<Integer, Object> object, DataSetRow row, Person person) {
		row.addColumnValue(new DataSetColumn(name, label, String.class), object.get(person.getPersonId()));
	}
}
