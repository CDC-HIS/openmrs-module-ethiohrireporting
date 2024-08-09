package org.openmrs.module.ohrireports.datasetevaluator.linelist.cervicalCancer;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.CervicalCancerQuery;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.openmrs.module.ohrireports.constants.Identifiers;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.CervicalCancerDataSetDefinition;
import org.openmrs.module.ohrireports.datasetevaluator.linelist.LineListUtilities;
import org.openmrs.module.reporting.dataset.*;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Handler(supports = { CervicalCancerDataSetDefinition.class })
public class CervicalCancerDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private CervicalCancerQuery cervicalCancerQuery;
	
	CervicalCancerDataSetDefinition _dataSetDefinition;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	@Autowired
	private CervicalCancerLIneListQuery cervicalCancerLineListQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		_dataSetDefinition = (CervicalCancerDataSetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		
		Date startDate = _dataSetDefinition.getStartDate();
		Date endDate = _dataSetDefinition.getEndDate();
		
		// Check start date and end date are valid
		// If start date is greater than end date
		if (startDate != null && endDate != null && startDate.compareTo(endDate) > 0) {
			//throw new EvaluationException("Start date cannot be greater than end date");
			DataSetRow row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("Error", "Error", Integer.class),
			    "Report start date cannot be after report end date");
			dataSet.addRow(row);
			return dataSet;
		}
		
		if (endDate == null) {
			_dataSetDefinition.setEndDate(new Date());
		}
		
		cervicalCancerQuery.setStartDate(_dataSetDefinition.getStartDate());
		cervicalCancerQuery.setEndDate(_dataSetDefinition.getEndDate());
		Cohort baseCohort = cervicalCancerQuery.loadScreenedCohort();
		List<Person> persons = LineListUtilities.sortPatientByName(cervicalCancerQuery.getPersons(baseCohort));
		
		List<Integer> latestFollowupEncounter = encounterQuery.getLatestDateByFollowUpDate(null, endDate);
		
		HashMap<Integer, Object> mrnIdentifierHashMap = cervicalCancerLineListQuery.getIdentifier(baseCohort,
		    Identifiers.MRN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> uanIdentifierHashMap = cervicalCancerLineListQuery.getIdentifier(baseCohort,
		    Identifiers.UAN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> followUpDate = cervicalCancerLineListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), FollowUpConceptQuestions.FOLLOW_UP_DATE, baseCohort);
		HashMap<Integer, Object> counselledDate = cervicalCancerLineListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), FollowUpConceptQuestions.DATE_COUNSELING_GIVEN, baseCohort);
		
		HashMap<Integer, Object> treatmentReceivedDate = cervicalCancerLineListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), FollowUpConceptQuestions.CXCA_TREATMENT_STARTING_DATE, baseCohort);
		HashMap<Integer, Object> nextFollowupVisitDate = cervicalCancerLineListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), FollowUpConceptQuestions.CXCA_NEXT_FOLLOWUP_SCREENING_DATE, baseCohort);
		HashMap<Integer, Object> dateLinkedToCxCaUnit = cervicalCancerLineListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), FollowUpConceptQuestions.DATE_LINKED_TO_CXCA_UNIT, baseCohort);
		HashMap<Integer, Object> cxcaScreeningAcceptedDate = cervicalCancerLineListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), FollowUpConceptQuestions.CXC_SCREENING_ACCEPTED_DATE, baseCohort);
		HashMap<Integer, Object> hpvDnaSampleCollectionDate = cervicalCancerLineListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), FollowUpConceptQuestions.HPV_DNA_SAMPLE_COLLECTION_DATE, baseCohort);
		HashMap<Integer, Object> hpvDnaResultReceivedDate = cervicalCancerLineListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), FollowUpConceptQuestions.HPV_DNA_RESULT_RECEIVED_DATE, baseCohort);
		HashMap<Integer, Object> viaScreeningDate = cervicalCancerLineListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), FollowUpConceptQuestions.VIA_SCREENING_DATE, baseCohort);
		HashMap<Integer, Object> cytologySampleCollectionDate = cervicalCancerLineListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), FollowUpConceptQuestions.CYTOLOGY_SAMPLE_COLLECTION_DATE, baseCohort);
		HashMap<Integer, Object> cytologyResultReceivedDate = cervicalCancerLineListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), FollowUpConceptQuestions.CYTOLOGY_RESULT_RECEIVED_DATE, baseCohort);
		HashMap<Integer, Object> colposcopyExamDate = cervicalCancerLineListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), FollowUpConceptQuestions.COLPOSCOPY_EXAM_DATE, baseCohort);
		HashMap<Integer, Object> biopsyResultReceivedDate = cervicalCancerLineListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), FollowUpConceptQuestions.BIOPSY_RESULT_RECEIVED_DATE, baseCohort);
		HashMap<Integer, Object> dateOfReferralToOtherHF = cervicalCancerLineListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), FollowUpConceptQuestions.DATE_OF_REFERRAL_TO_OTHER_HF, baseCohort);
		HashMap<Integer, Object> dateClientArrivedInReferredHF = cervicalCancerLineListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), FollowUpConceptQuestions.DATE_CLIENT_ARRIVED_IN_REFERRED_HF, baseCohort);
		HashMap<Integer, Object> dateClientServedInReferredHF = cervicalCancerLineListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), FollowUpConceptQuestions.DATE_CLIENT_SERVED_IN_REFERRED_HF, baseCohort);
		
		HashMap<Integer, Object> artStartDate = cervicalCancerLineListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), FollowUpConceptQuestions.ART_START_DATE, baseCohort);
		HashMap<Integer, Object> followUpStatus = cervicalCancerLineListQuery.getFollowUpStatus(latestFollowupEncounter,
		    baseCohort);
		HashMap<Integer, Object> regimentHashMap = cervicalCancerLineListQuery.getRegiment(latestFollowupEncounter,
		    baseCohort);
		HashMap<Integer, Object> artDispenseDose = cervicalCancerLineListQuery.getByResult(
		    FollowUpConceptQuestions.ART_DISPENSE_DOSE, baseCohort, latestFollowupEncounter);
		HashMap<Integer, Object> adherence = cervicalCancerLineListQuery.getByResult(FollowUpConceptQuestions.ARV_ADHERENCE,
		    baseCohort, latestFollowupEncounter);
		HashMap<Integer, Object> screeningType = cervicalCancerLineListQuery.getByResult(
		    FollowUpConceptQuestions.CXCA_TYPE_OF_SCREENING, baseCohort, cervicalCancerQuery.getBaseEncounter());
		HashMap<Integer, Object> hpvSubType = cervicalCancerLineListQuery.getByResult(FollowUpConceptQuestions.HPV_SUB_TYPE,
		    baseCohort, cervicalCancerQuery.getBaseEncounter());
		HashMap<Integer, Object> screeningMechanism = cervicalCancerLineListQuery.getByResult(
		    FollowUpConceptQuestions.SCREENING_STRATEGY, baseCohort, cervicalCancerQuery.getBaseEncounter());
		HashMap<Integer, Object> hpvScreeningResult = cervicalCancerLineListQuery.getByResult(
		    FollowUpConceptQuestions.HPV_DNA_SCREENING_RESULT, baseCohort, cervicalCancerQuery.getBaseEncounter());
		HashMap<Integer, Object> colposcopyExamFinding = cervicalCancerLineListQuery.getByResult(
		    FollowUpConceptQuestions.COLPOSCOPY_EXAM_FINDING, baseCohort, cervicalCancerQuery.getBaseEncounter());
		HashMap<Integer, Object> cytologyResult = cervicalCancerLineListQuery.getByResult(
		    FollowUpConceptQuestions.CYTOLOGY_RESULT, baseCohort, cervicalCancerQuery.getBaseEncounter());
		HashMap<Integer, Object> viaScreeningResult = cervicalCancerLineListQuery.getByResult(
		    FollowUpConceptQuestions.VIA_SCREENING_RESULT, baseCohort, cervicalCancerQuery.getBaseEncounter());
		HashMap<Integer, Object> biopsyResult = cervicalCancerLineListQuery.getByResult(
		    FollowUpConceptQuestions.BIOPSY_RESULT, baseCohort, cervicalCancerQuery.getBaseEncounter());
		HashMap<Integer, Object> cxcaTreatmentPreCancerousLesionsReceived = cervicalCancerLineListQuery
		        .getByResult(FollowUpConceptQuestions.CXCA_TREATMENT_PRECANCEROUS_LESIONS, baseCohort,
		            cervicalCancerQuery.getBaseEncounter());
		HashMap<Integer, Object> cxcaTreatmentForConfirmedCxCa = cervicalCancerLineListQuery.getByResult(
		    FollowUpConceptQuestions.CXCA_CONFIRMED_BY_BIOPSY_RESULT, baseCohort, cervicalCancerQuery.getBaseEncounter());
		HashMap<Integer, Object> reasonForReferral = cervicalCancerLineListQuery.getByResult(
		    FollowUpConceptQuestions.REASON_FOR_REFERRAL, baseCohort, cervicalCancerQuery.getBaseEncounter());
		HashMap<Integer, Object> referralStatus = cervicalCancerLineListQuery.getByResult(
		    FollowUpConceptQuestions.CXCA_REFERRAL_STATUS, baseCohort, cervicalCancerQuery.getBaseEncounter());
		HashMap<Integer, Object> feedback = cervicalCancerLineListQuery.getByResult(FollowUpConceptQuestions.FEEDBACK,
		    baseCohort, cervicalCancerQuery.getBaseEncounter());
		HashMap<Integer, Object> nextVisitDate = cervicalCancerLineListQuery.getObsValueDate(latestFollowupEncounter,
		    FollowUpConceptQuestions.NEXT_VISIT_DATE, baseCohort);
		HashMap<Integer, Object> treatmentEndDate = cervicalCancerLineListQuery.getObsValueDate(latestFollowupEncounter,
		    FollowUpConceptQuestions.TREATMENT_END_DATE, baseCohort);
		
		DataSetRow row;
		if (!persons.isEmpty()) {
			
			row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("#", "#", Integer.class), "TOTAL");
			row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", Integer.class), persons.size());
			
			dataSet.addRow(row);
		} else {
			dataSet.addRow(LineListUtilities.buildEmptyRow(Arrays.asList("#", "Patient Name", "MRN", "UAN", "Age", "Sex",
			    "Follow-Up Date E.C", "Art Start Date E.C", "Date Counseled For CCA E.C",
			    "Date Accepted CxCa Screening in E.C.", "Date Linked to CxCa Screening Unit in E.C.", "Type of Screening",
			    "Screening Strategy", "VIA Screening Result", "VIA Screening Date E.C", "HPV Sub Type",
			    "HPV DNA Screening Result", "HPV DNA Sample Collection Date E.C", "HPV Subtype",
			    "HPV DNA Result Received Date E.C", "Cytology Result", "Cytology Sample Collection Date E.C",
			    "Cytology Result Received Date E.C", "Colposcopy Exam Finding", "Colposcopy Exam Date E.C", "Biopsy Result",
			    "Biopsy Sample Collection Date E.C", "Biopsy Result Received Date E.C", "Treatment for Precancerous Lesion",
			    "Treatment for Confirmed CxCa", "Date Treatment Received E.C", "Next Follow-up Screening Date E.C.",
			    "Referral/ Linkage Status", "Reason for Referral", "Date of Referral to other HF in E.C.",
			    "Referral Confirmed Date E.C", "Feedback from the other HF",
			    "Date Client Served in the referred HF in E.C.", "Latest Follow-Up Status", "Latest Regimen",
			    "Latest ARV Dose Days", "Latest Adherence", "Next Visit Date in E.C.", "Treatment End Date in E.C.")));
		}
		int i = 1;
		for (Person person : persons) {
			
			Date followUpDateEC = cervicalCancerLineListQuery.getDate(followUpDate.get(person.getPersonId()));
			Date artStartDateEC = cervicalCancerLineListQuery.getDate(artStartDate.get(person.getPersonId()));
			Date counselledDateEC = cervicalCancerLineListQuery.getDate(counselledDate.get(person.getPersonId()));
			Date treatmentReceivedDateEC = cervicalCancerLineListQuery.getDate(treatmentReceivedDate.get(person
			        .getPersonId()));
			Date nextFollowupVisitDateEC = cervicalCancerLineListQuery.getDate(nextFollowupVisitDate.get(person
			        .getPersonId()));
			Date dateLinkedToCxCaUnitEC = cervicalCancerLineListQuery
			        .getDate(dateLinkedToCxCaUnit.get(person.getPersonId()));
			Date cxcaScreeningAcceptedDateEC = cervicalCancerLineListQuery.getDate(cxcaScreeningAcceptedDate.get(person
			        .getPersonId()));
			Date hpvDnaSampleCollectionDateEC = cervicalCancerLineListQuery.getDate(hpvDnaSampleCollectionDate.get(person
			        .getPersonId()));
			Date hpvDnaResultReceivedDateEC = cervicalCancerLineListQuery.getDate(hpvDnaResultReceivedDate.get(person
			        .getPersonId()));
			Date viaScreeningDateEC = cervicalCancerLineListQuery.getDate(viaScreeningDate.get(person.getPersonId()));
			Date cytologySampleCollectionDateEC = cervicalCancerLineListQuery.getDate(cytologySampleCollectionDate
			        .get(person.getPersonId()));
			Date cytologyResultReceivedDateEC = cervicalCancerLineListQuery.getDate(cytologyResultReceivedDate.get(person
			        .getPersonId()));
			Date colposcopyExamDateEC = cervicalCancerLineListQuery.getDate(colposcopyExamDate.get(person.getPersonId()));
			Date biopsyResultReceivedDateEC = cervicalCancerLineListQuery.getDate(biopsyResultReceivedDate.get(person
			        .getPersonId()));
			Date biopsySampleCollectionDateEC = cervicalCancerLineListQuery.getDate(biopsyResultReceivedDate.get(person
			        .getPersonId()));
			Date dateOfReferralToOtherHFEC = cervicalCancerLineListQuery.getDate(dateOfReferralToOtherHF.get(person
			        .getPersonId()));
			Date dateClientArrivedInReferredHFEC = cervicalCancerLineListQuery.getDate(dateClientArrivedInReferredHF
			        .get(person.getPersonId()));
			Date dateClientServedInReferredHFEC = cervicalCancerLineListQuery.getDate(dateClientServedInReferredHF
			        .get(person.getPersonId()));
			Date nextVisitDateEC = cervicalCancerLineListQuery.getDate(nextVisitDate.get(person.getPersonId()));
			Date treatmentEndDateEC = cervicalCancerLineListQuery.getDate(treatmentEndDate.get(person.getPersonId()));
			
			row = new DataSetRow();
			
			row.addColumnValue(new DataSetColumn("#", "#", Integer.class), i++);
			row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", String.class), person.getNames());
			addColumnValue("MRN", "MRN", mrnIdentifierHashMap, row, person);
			addColumnValue("UAN", "UAN", uanIdentifierHashMap, row, person);
			row.addColumnValue(new DataSetColumn("Age", "Current Age", String.class),
			    person.getAge(_dataSetDefinition.getEndDate()));
			row.addColumnValue(new DataSetColumn("Gender", "Sex", Integer.class), person.getGender());
			row.addColumnValue(new DataSetColumn("followUp", "Follow-Up Date E.C", String.class),
			    cervicalCancerLineListQuery.getEthiopianDate(followUpDateEC));
			row.addColumnValue(new DataSetColumn("artStartDate", "Art Start Date E.C", String.class),
			    cervicalCancerLineListQuery.getEthiopianDate(artStartDateEC));
			row.addColumnValue(new DataSetColumn("counseledGiven", "Date Counseled For CCA E.C", String.class),
			    cervicalCancerLineListQuery.getEthiopianDate(counselledDateEC));
			row.addColumnValue(new DataSetColumn("cxcaScreeningAcceptedDate", "Date Accepted CxCa Screening in E.C.",
			        String.class), cervicalCancerLineListQuery.getEthiopianDate(cxcaScreeningAcceptedDateEC));
			row.addColumnValue(new DataSetColumn("dateLinkedToCxCaUnit", "Date Linked to CxCa Screening Unit in E.C.",
			        String.class), cervicalCancerLineListQuery.getEthiopianDate(dateLinkedToCxCaUnitEC));
			addColumnValue("ScreeningVisitType", "Type of Screening", screeningType, row, person);
			addColumnValue("screeningMechanism", "Screening Strategy", screeningMechanism, row, person);
			addColumnValue("viaScreeningResult", "VIA Screening Result", viaScreeningResult, row, person);
			row.addColumnValue(new DataSetColumn("viaScreeningDate", "VIA Screening Date E.C", String.class),
			    cervicalCancerLineListQuery.getEthiopianDate(viaScreeningDateEC));
			addColumnValue("hpvScreeningResult", "HPV DNA Screening Result", hpvScreeningResult, row, person);
			row.addColumnValue(new DataSetColumn("hpvDnaSampleCollectionDate", "HPV DNA Sample Collection Date E.C",
			        String.class), cervicalCancerLineListQuery.getEthiopianDate(hpvDnaSampleCollectionDateEC));
			addColumnValue("hpvSubType", "HPV Subtype", hpvSubType, row, person);
			row.addColumnValue(new DataSetColumn("hpvDnaResultReceivedDate", "HPV DNA Result Received Date E.C",
			        String.class), cervicalCancerLineListQuery.getEthiopianDate(hpvDnaResultReceivedDateEC));
			addColumnValue("cytologyResult", "Cytology Result", cytologyResult, row, person);
			row.addColumnValue(new DataSetColumn("cytologySampleCollectionDate", "Cytology Sample Collection Date E.C",
			        String.class), cervicalCancerLineListQuery.getEthiopianDate(cytologySampleCollectionDateEC));
			row.addColumnValue(new DataSetColumn("cytologyResultReceivedDate", "Cytology Result Received Date E.C",
			        String.class), cervicalCancerLineListQuery.getEthiopianDate(cytologyResultReceivedDateEC));
			addColumnValue("colposcopyExamFinding", "Colposcopy Exam Finding", colposcopyExamFinding, row, person);
			row.addColumnValue(new DataSetColumn("colposcopyExamDate", "Colposcopy Exam Date E.C", String.class),
			    cervicalCancerLineListQuery.getEthiopianDate(colposcopyExamDateEC));
			addColumnValue("biopsyResult", "Biopsy Result", biopsyResult, row, person);
			row.addColumnValue(new DataSetColumn("biopsySampleCollectionDate", "Biopsy Sample Collection Date E.C",
			        String.class), cervicalCancerLineListQuery.getEthiopianDate(biopsySampleCollectionDateEC));
			row.addColumnValue(
			    new DataSetColumn("biopsyResultReceivedDate", "Biopsy Result Received Date E.C", String.class),
			    cervicalCancerLineListQuery.getEthiopianDate(biopsyResultReceivedDateEC));
			addColumnValue("cxcaTreatmentResult", "Treatment for Precancerous Lesion",
			    cxcaTreatmentPreCancerousLesionsReceived, row, person);
			addColumnValue("cxcaTreatmentForConfirmedCxCa", "Treatment for Confirmed CxCa", cxcaTreatmentForConfirmedCxCa,
			    row, person);
			row.addColumnValue(new DataSetColumn("cxcaTreatmentReceived", "Date Treatment Received E.C", String.class),
			    cervicalCancerLineListQuery.getEthiopianDate(treatmentReceivedDateEC));
			row.addColumnValue(
			    new DataSetColumn("nextFollowupVisitDate", "Next Follow-up Screening Date E.C.", String.class),
			    cervicalCancerLineListQuery.getEthiopianDate(nextFollowupVisitDateEC));
			addColumnValue("ReferralStatus", "Referral/ Linkage Status", referralStatus, row, person);
			addColumnValue("reasonForReferral", "Reason for Referral", reasonForReferral, row, person);
			row.addColumnValue(new DataSetColumn("dateOfReferralToOtherHF", "Date of Referral to other HF in E.C.",
			        String.class), cervicalCancerLineListQuery.getEthiopianDate(dateOfReferralToOtherHFEC));
			row.addColumnValue(new DataSetColumn("dateClientArrivedInReferredHF", "Referral Confirmed Date E.C",
			        String.class), cervicalCancerLineListQuery.getEthiopianDate(dateClientArrivedInReferredHFEC));
			addColumnValue("feedback", "Feedback from the other HF", feedback, row, person);
			row.addColumnValue(new DataSetColumn("dateClientServedInReferredHFEC",
			        "Date Client Served in the referred HF in E.C.", String.class), cervicalCancerLineListQuery
			        .getEthiopianDate(dateClientServedInReferredHFEC));
			addColumnValue("followUpStatus", "Latest Follow-Up Status", followUpStatus, row, person);
			addColumnValue("regimen", "Latest Regimen", regimentHashMap, row, person);
			row.addColumnValue(new DataSetColumn("ARTDispenseDose", "Latest ARV Dose Days", String.class),
			    artDispenseDose.get(person.getPersonId()));
			addColumnValue("adherence", "Latest Adherence", adherence, row, person);
			row.addColumnValue(new DataSetColumn("nextVisitDateEC", "Next Visit Date in E.C.", String.class),
			    cervicalCancerLineListQuery.getEthiopianDate(nextVisitDateEC));
			row.addColumnValue(new DataSetColumn("treatmentEndDateEC", "Treatment End Date in E.C.", String.class),
			    cervicalCancerLineListQuery.getEthiopianDate(treatmentEndDateEC));
			
			dataSet.addRow(row);
		}
		
		return dataSet;
	}
	
	private void addColumnValue(String name, String label, HashMap<Integer, Object> object, DataSetRow row, Person person) {
		row.addColumnValue(new DataSetColumn(name, label, String.class), object.get(person.getPersonId()));
	}
}
