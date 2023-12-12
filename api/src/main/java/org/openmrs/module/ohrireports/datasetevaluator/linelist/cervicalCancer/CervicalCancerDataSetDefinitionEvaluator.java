package org.openmrs.module.ohrireports.datasetevaluator.linelist.cervicalCancer;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.CervicalCancerQuery;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.CervicalCancerDataSetDefinition;
import org.openmrs.module.reporting.dataset.*;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Handler(supports = { CervicalCancerDataSetDefinition.class })
public class CervicalCancerDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private CervicalCancerQuery cervicalCancerQuery;
	
	CervicalCancerDataSetDefinition _dataSetDefinition;
	
	@Autowired
	private CervicalCancerLIneListQuery cervicalCancerLineListQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		_dataSetDefinition = (CervicalCancerDataSetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		
		cervicalCancerQuery.setStartDate(_dataSetDefinition.getStartDate());
		cervicalCancerQuery.setEndDate(_dataSetDefinition.getEndDate());
		Cohort baseCohort = cervicalCancerQuery.loadScreenedCohort();
		List<Person> persons = cervicalCancerQuery.getPersons(baseCohort);
		
		HashMap<Integer, Object> mrnIdentifierHashMap = cervicalCancerLineListQuery.getIdentifier(baseCohort,
		    MRN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> uanIdentifierHashMap = cervicalCancerLineListQuery.getIdentifier(baseCohort,
		    UAN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> followUpDate = cervicalCancerLineListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), FOLLOW_UP_DATE, baseCohort);
		HashMap<Integer, Object> counselledDate = cervicalCancerLineListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), DATE_COUNSELING_GIVEN, baseCohort);
		HashMap<Integer, Object> screeningReceivedDate = cervicalCancerLineListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), CXC_SCREENING_DATE, baseCohort);
		HashMap<Integer, Object> treatmentReceivedDate = cervicalCancerLineListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), CXCA_TREATMENT_STARTING_DATE, baseCohort);
		HashMap<Integer, Object> nextVisitDate = cervicalCancerLineListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), NEXT_VISIT_DATE, baseCohort);
		HashMap<Integer, Object> dateLinkedToCxCaUnit = cervicalCancerLineListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), DATE_LINKED_TO_CXCA_UNIT, baseCohort);
		HashMap<Integer, Object> cxcaScreeningAcceptedDate = cervicalCancerLineListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), CXCA_SCREENING_ACCEPTED_DATE, baseCohort);
		HashMap<Integer, Object> hpvDnaSampleCollectionDate = cervicalCancerLineListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), HPV_DNA_SAMPLE_COLLECTION_DATE, baseCohort);
		HashMap<Integer, Object> hpvDnaResultReceivedDate = cervicalCancerLineListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), HPV_DNA_RESULT_RECEIVED_DATE, baseCohort);
		HashMap<Integer, Object> viaScreeningDate = cervicalCancerLineListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), VIA_SCREENING_DATE, baseCohort);
		HashMap<Integer, Object> cytologySampleCollectionDate = cervicalCancerLineListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), CYTOLOGY_SAMPLE_COLLECTION_DATE, baseCohort);
		HashMap<Integer, Object> cytologyResultReceivedDate = cervicalCancerLineListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), CYTOLOGY_RESULT_RECEIVED_DATE, baseCohort);
		HashMap<Integer, Object> colposcopyExamDate = cervicalCancerLineListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), COLPOSCOPY_EXAM_DATE, baseCohort);
		HashMap<Integer, Object> biopsySampleCollectionDate = cervicalCancerLineListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), BIOPSY_SAMPLE_COLLECTED_DATE, baseCohort);
		HashMap<Integer, Object> biopsyResultReceivedDate = cervicalCancerLineListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), BIOPSY_RESULT_RECEIVED_DATE, baseCohort);
		HashMap<Integer, Object> dateOfReferralToOtherHF = cervicalCancerLineListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), DATE_OF_REFERRAL_TO_OTHER_HF, baseCohort);
		HashMap<Integer, Object> referralConfirmedDate = cervicalCancerLineListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), REFERRAL_CONFIRMED_DATE, baseCohort);
		
		HashMap<Integer, Object> artStartDate = cervicalCancerLineListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), ART_START_DATE, baseCohort);
		HashMap<Integer, Object> followUpStatus = cervicalCancerLineListQuery.getFollowUpStatus(
		    cervicalCancerQuery.getCurrentEncounter(), baseCohort);
		HashMap<Integer, Object> regimentHashMap = cervicalCancerLineListQuery.getRegiment(
		    cervicalCancerQuery.getCurrentEncounter(), baseCohort);
		HashMap<Integer, Object> adherence = cervicalCancerLineListQuery.getByResult(ARV_ADHERENCE, baseCohort,
		    cervicalCancerQuery.getCurrentEncounter());
		HashMap<Integer, Object> screeningType = cervicalCancerLineListQuery.getByResult(CXCA_TYPE_OF_SCREENING, baseCohort,
		    cervicalCancerQuery.getBaseEncounter());
		HashMap<Integer, Object> hpvSubType = cervicalCancerLineListQuery.getByResult(HPV_SUB_TYPE, baseCohort,
		    cervicalCancerQuery.getBaseEncounter());
		HashMap<Integer, Object> screeningMechanism = cervicalCancerLineListQuery.getByResult(SCREENING_STRATEGY,
		    baseCohort, cervicalCancerQuery.getBaseEncounter());
		HashMap<Integer, Object> hpvScreeningResult = cervicalCancerLineListQuery.getByResult(HPV_DNA_SCREENING_RESULT,
		    baseCohort, cervicalCancerQuery.getBaseEncounter());
		HashMap<Integer, Object> colposcopyExamFinding = cervicalCancerLineListQuery.getByResult(COLPOSCOPY_EXAM_FINDING,
		    baseCohort, cervicalCancerQuery.getBaseEncounter());
		HashMap<Integer, Object> cytologyResult = cervicalCancerLineListQuery.getByResult(CYTOLOGY_RESULT, baseCohort,
		    cervicalCancerQuery.getBaseEncounter());
		HashMap<Integer, Object> viaScreeningResult = cervicalCancerLineListQuery.getByResult(VIA_SCREENING_RESULT,
		    baseCohort, cervicalCancerQuery.getBaseEncounter());
		HashMap<Integer, Object> biopsyResult = cervicalCancerLineListQuery.getByResult(BIOPSY_RESULT, baseCohort,
		    cervicalCancerQuery.getBaseEncounter());
		HashMap<Integer, Object> cxcaTreatmentReceived = cervicalCancerLineListQuery.getByResult(CXCA_TREATMENT_TYPE,
		    baseCohort, cervicalCancerQuery.getBaseEncounter());
		HashMap<Integer, Object> reasonForReferral = cervicalCancerLineListQuery.getByResult(REASON_FOR_REFERRAL,
		    baseCohort, cervicalCancerQuery.getBaseEncounter());
		HashMap<Integer, Object> feedback = cervicalCancerLineListQuery.getByResult(FEEDBACK, baseCohort,
		    cervicalCancerQuery.getBaseEncounter());
		HashMap<Integer, Object> cxcaScreeningOffered = cervicalCancerLineListQuery.getByResult(CX_CA_SCREENING_OFFERED,
		    baseCohort, cervicalCancerQuery.getBaseEncounter());
		HashMap<Integer, Object> cxcaScreeningAccepted = cervicalCancerLineListQuery.getByResult(CX_CA_SCREENING_ACCEPTED,
		    baseCohort, cervicalCancerQuery.getBaseEncounter());
		HashMap<Integer, Object> linkedToCxCaUnit = cervicalCancerLineListQuery.getByResult(LINKED_TO_CX_CA_UNIT,
		    baseCohort, cervicalCancerQuery.getBaseEncounter());
		
		DataSetRow row = new DataSetRow();
		row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class), "Total");
		row.addColumnValue(new DataSetColumn("UAN", "UAN", Integer.class), baseCohort.getSize());
		dataSet.addRow(row);
		
		for (Person person : persons) {
			row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("Name", "Name", String.class), person.getNames());
			addColumnValue("MRN", "MRN", mrnIdentifierHashMap, row, person);
			addColumnValue("UAN", "UAN", uanIdentifierHashMap, row, person);
			row.addColumnValue(new DataSetColumn("Age", "Age", String.class), person.getAge(_dataSetDefinition.getEndDate()));
			addColumnValue("followUp", "Follow-Up Date", followUpDate, row, person);
			addColumnValue("followUpStatus", "Follow-Up Status", followUpStatus, row, person);
			addColumnValue("artStartDate", "Art Start Date", artStartDate, row, person);
			addColumnValue("regimen", "Regimen", regimentHashMap, row, person);
			addColumnValue("adherence", "Adherence", adherence, row, person);
			addColumnValue("ScreeningVisitType", "Screening Visit Type", screeningType, row, person);
			addColumnValue("counseledGiven", "Date Counseled For CCA", counselledDate, row, person);
			addColumnValue("counseledGiven", "Date Counseled For CCA", counselledDate, row, person);
			addColumnValue("screeningMechanism", "Screening Mechanism", screeningMechanism, row, person);
			addColumnValue("hpvScreeningResult", "HPV Screening Result", hpvScreeningResult, row, person);
			addColumnValue("viaScreeningResult", "VIA Screening Result", viaScreeningResult, row, person);
			addColumnValue("cxcaTreatmentResult", "Type Of Treatment Received", cxcaTreatmentReceived, row, person);
			addColumnValue("cxcaTreatmentReceived", "Date Treatment Received", treatmentReceivedDate, row, person);
			addColumnValue("nextVisitDate", "Next Visit Date", nextVisitDate, row, person);
			addColumnValue("dateLinkedToCxCaUnit", "Date Linked To CxCa Unit", dateLinkedToCxCaUnit, row, person);
			addColumnValue("cxcaScreeningAcceptedDate", "Cx_Ca Screening Accepted Date", cxcaScreeningAcceptedDate, row,
			    person);
			addColumnValue("hpvDnaSampleCollectionDate", "HPV DNA Sample Collection Date", hpvDnaSampleCollectionDate, row,
			    person);
			addColumnValue("hpvDnaResultReceivedDate", "HPV DNA Result Received Date", hpvDnaResultReceivedDate, row, person);
			addColumnValue("hpvSubType", "HPV Sub Type", hpvSubType, row, person);
			addColumnValue("viaScreeningDate", "VIA Screening Date", viaScreeningDate, row, person);
			addColumnValue("cytologySampleCollectionDate", "Cytology Sample Collection Date", cytologySampleCollectionDate,
			    row, person);
			addColumnValue("cytologyResultReceivedDate", "Cytology Result Received Date", cytologyResultReceivedDate, row,
			    person);
			addColumnValue("cytologyResult", "Cytology Result", cytologyResult, row, person);
			addColumnValue("colposcopyExamDate", "Colposcopy Exam Date", colposcopyExamDate, row, person);
			addColumnValue("colposcopyExamFinding", "Colposcopy Exam Finding", colposcopyExamFinding, row, person);
			addColumnValue("biopsyResultReceivedDate", "Biopsy Result Received Date", biopsyResultReceivedDate, row, person);
			addColumnValue("biopsySampleCollectionDate", "Biopsy Sample Collection Date", biopsySampleCollectionDate, row,
			    person);
			addColumnValue("biopsyResult", "Biopsy Result", biopsyResult, row, person);
			addColumnValue("dateOfReferralToOtherHF", "Referral/link Date Referred to Other HF", dateOfReferralToOtherHF,
			    row, person);
			addColumnValue("reasonForReferral", "Reason for Referral", reasonForReferral, row, person);
			addColumnValue("feedback", "Feedback", feedback, row, person);
			addColumnValue("referralConfirmedDate", "Referral Confirmed Date", referralConfirmedDate, row, person);
			addColumnValue("cxcaScreeningOffered", "CX_CA Screening Offered", cxcaScreeningOffered, row, person);
			addColumnValue("cxcaScreeningAccepted", "CX_CA Screening Accepted", cxcaScreeningAccepted, row, person);
			addColumnValue("linkedToCxCaUnit", "Linked To CX CA Unit", linkedToCxCaUnit, row, person);
			
			dataSet.addRow(row);
		}
		
		return dataSet;
	}
	
	private void addColumnValue(String name, String label, HashMap<Integer, Object> object, DataSetRow row, Person person) {
		row.addColumnValue(new DataSetColumn(name, label, String.class), object.get(person.getPersonId()));
	}
}
