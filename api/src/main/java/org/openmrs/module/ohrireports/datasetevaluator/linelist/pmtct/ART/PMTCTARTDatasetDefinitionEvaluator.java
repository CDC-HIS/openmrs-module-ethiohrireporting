package org.openmrs.module.ohrireports.datasetevaluator.linelist.pmtct.ART;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.pmtct.ARTQuery;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.openmrs.module.ohrireports.constants.Identifiers;
import org.openmrs.module.ohrireports.constants.PMTCTConceptQuestions;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.PMTCTARTClientDataSetDefinition;
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

@Handler(supports = { PMTCTARTClientDataSetDefinition.class })
public class PMTCTARTDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private PMTCTARTLineListQuery pmtctARTLineListQuery;
	
	@Autowired
	private ARTQuery artQuery;
	
	PMTCTARTClientDataSetDefinition _dataSetDefinition;
	
	private HashMap<Integer, Object> mrnIdentifierHashMap, uanIdentifierHashMap, artStartDate, pmtctBookingDate,
	        statusAtEnrollment, pmtctReferredDate, isPregnant, isBreastFeeding, dischargeDate, reasonForDischarge,
	        maternalPMTCTFinalOutcome, finalOutcomeDate, latestFollowupDate, regimen, dose, nutritionalStatus,
	        latestVLStatus, adherence, nextVisitDate;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		_dataSetDefinition = (PMTCTARTClientDataSetDefinition) dataSetDefinition;
		
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		
		SimpleDataSet _dataSet = EthiOhriUtil.isValidReportDateRange(_dataSetDefinition.getStartDate(),
		    _dataSetDefinition.getEndDate(), dataSet);
		if (_dataSet != null)
			return _dataSet;
		
		artQuery.setStartDate(_dataSetDefinition.getStartDate());
		artQuery.setEndDate(_dataSetDefinition.getEndDate());
		
		Cohort baseCohort = artQuery.getPmtctARTCohort();
		List<Person> persons = artQuery.getPersons(baseCohort);
		
		loadColumnDictionary(baseCohort);
		
		DataSetRow row;
		
		if (!persons.isEmpty()) {
			
			row = new DataSetRow();
			
			row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class), "TOTAL");
			row.addColumnValue(new DataSetColumn("Name", "Name", Integer.class), persons.size());
			
			dataSet.addRow(row);
		} else {
			dataSet.addRow(LineListUtilities.buildEmptyRow(Arrays.asList("Name", "Sex", "MRN", "UAN", "Age",
			    "ArtStartDate Eth", "PMTCT Booking DateEth", "Status At Enrollment", "Is Pregnant", "Is BreastFeeding",
			    "Reason For Discharge", "MaternalPMTCTFinalOutcome", "Date Of FinalOutcome ETH", "Latest FollowupDate ETH",
			    "Regimen", "Dose", "NutritionalStatus", "Latest VL Status", "Adherence", "Next Visit DateETH"), "Name",
			    "Sex"));
			return dataSet;
		}
		for (Person person : persons) {
			
			Date artStartDate = pmtctARTLineListQuery.getDate(this.artStartDate.get(person.getPersonId()));
			Date pmtctBookingDate = pmtctARTLineListQuery.getDate(this.pmtctBookingDate.get(person.getPersonId()));
			Date pmtctRefferedDate = pmtctARTLineListQuery.getDate(this.pmtctReferredDate.get(person.getPersonId()));
			Date dischargeDate = pmtctARTLineListQuery.getDate(this.dischargeDate.get(person.getPersonId()));
			Date finalOutcomeDate = pmtctARTLineListQuery.getDate(this.finalOutcomeDate.get(person.getPersonId()));
			Date latestFollowupDate = pmtctARTLineListQuery.getDate(this.latestFollowupDate.get(person.getPersonId()));
			Date nextVisitDate = pmtctARTLineListQuery.getDate(this.nextVisitDate.get(person.getPersonId()));
			
			row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("Name", "Patient Name", String.class), person.getNames());
			addColumnValue("MRN", "MRN", mrnIdentifierHashMap, row, person);
			addColumnValue("UAN", "UAN", uanIdentifierHashMap, row, person);
			row.addColumnValue(new DataSetColumn("Age", "Age", String.class), person.getAge(_dataSetDefinition.getEndDate()));
			row.addColumnValue(new DataSetColumn("ArtStartDateEth", "Art Start Date ETH", String.class),
			    pmtctARTLineListQuery.getEthiopianDate(artStartDate));
			row.addColumnValue(new DataSetColumn("PMTCTBookingDateEth", "PMTCT Booking Date ETH", String.class),
			    pmtctARTLineListQuery.getEthiopianDate(pmtctBookingDate));
			addColumnValue("StatusAtEnrollment", "Status at Enrollment", statusAtEnrollment, row, person);
			row.addColumnValue(new DataSetColumn("PMTCTReferredDate", "Date Referred to PMTCT ETH", String.class),
			    pmtctARTLineListQuery.getEthiopianDate(pmtctRefferedDate));
			addColumnValue("IsPregnant", "Pregnant?", isPregnant, row, person);
			addColumnValue("IsBreastFeeding", "Breastfeeding?", isBreastFeeding, row, person);
			row.addColumnValue(new DataSetColumn("DischargeDateETH", "Date of Discharge ETH", String.class),
			    pmtctARTLineListQuery.getEthiopianDate(dischargeDate));
			addColumnValue("ReasonForDischarge", "Reason For Discharge", reasonForDischarge, row, person);
			addColumnValue("MaternalPMTCTFinalOutcome", "Maternal PMTCT Final Outcome", maternalPMTCTFinalOutcome, row,
			    person);
			row.addColumnValue(new DataSetColumn("DateOfFinalOutcomeETH", "Date of Final Outcome ETH", String.class),
			    pmtctARTLineListQuery.getEthiopianDate(finalOutcomeDate));
			row.addColumnValue(new DataSetColumn("LatestFollowupDateETH", "Latest Follow-up Date ETH", String.class),
			    pmtctARTLineListQuery.getEthiopianDate(latestFollowupDate));
			addColumnValue("Regimen", "Regimen", regimen, row, person);
			addColumnValue("Dose", "Dose", dose, row, person);
			addColumnValue("NutritionalStatus", "Nutritional Status", nutritionalStatus, row, person);
			addColumnValue("LatestVLStatus", "Latest VL Status", latestVLStatus, row, person);
			addColumnValue("Adherence", "Adherence", adherence, row, person);
			row.addColumnValue(new DataSetColumn("NextVisitDateETH", "Next Visit Date ETH", String.class),
			    pmtctARTLineListQuery.getEthiopianDate(nextVisitDate));
			
			dataSet.addRow(row);
			
		}
		return dataSet;
	}
	
	private void loadColumnDictionary(Cohort baseCohort) {
		mrnIdentifierHashMap = pmtctARTLineListQuery.getIdentifier(baseCohort, Identifiers.MRN_PATIENT_IDENTIFIERS);
		uanIdentifierHashMap = pmtctARTLineListQuery.getIdentifier(baseCohort, Identifiers.UAN_PATIENT_IDENTIFIERS);
		
		artStartDate = pmtctARTLineListQuery.getObsValueDate(artQuery.getBaseEncounter(),
		    FollowUpConceptQuestions.ART_START_DATE, baseCohort);
		pmtctBookingDate = pmtctARTLineListQuery.getObsValueDate(artQuery.getBaseEncounter(),
		    PMTCTConceptQuestions.PMTCT_OTZ_ENROLLMENT_DATE, baseCohort);
		statusAtEnrollment = pmtctARTLineListQuery.getByResult(PMTCTConceptQuestions.PMTCT_STATUS_AT_ENROLLMENT, baseCohort,
		    artQuery.getBaseEncounter());
		pmtctReferredDate = pmtctARTLineListQuery.getObsValueDate(artQuery.getBaseEncounter(),
		    PMTCTConceptQuestions.PMTCT_REFERRED_DATE, baseCohort);
		isPregnant = pmtctARTLineListQuery.getByResult(FollowUpConceptQuestions.PREGNANCY_STATUS, baseCohort,
		    artQuery.getBaseEncounter());
		isBreastFeeding = pmtctARTLineListQuery.getByResult(FollowUpConceptQuestions.CURRENTLY_BREAST_FEEDING_CHILD,
		    baseCohort, artQuery.getBaseEncounter());
		dischargeDate = pmtctARTLineListQuery.getObsValueDate(artQuery.getBaseEncounter(),
		    PMTCTConceptQuestions.PMTCT_DISCHARGE_DATE, baseCohort);
		reasonForDischarge = pmtctARTLineListQuery.getByResult(PMTCTConceptQuestions.REASON_FOR_DISCHARGE, baseCohort,
		    artQuery.getBaseEncounter());
		maternalPMTCTFinalOutcome = pmtctARTLineListQuery.getByResult(PMTCTConceptQuestions.PMTCT_FINAL_OUTCOME, baseCohort,
		    artQuery.getBaseEncounter());
		finalOutcomeDate = pmtctARTLineListQuery.getObsValueDate(artQuery.getBaseEncounter(),
		    FollowUpConceptQuestions.FINAL_OUTCOME_DATE, baseCohort);
		latestFollowupDate = pmtctARTLineListQuery.getObsValueDate(artQuery.getBaseEncounter(),
		    FollowUpConceptQuestions.LATEST_FOLLOWUP_DATE, baseCohort);
		regimen = pmtctARTLineListQuery.getByResult(FollowUpConceptQuestions.REGIMEN, baseCohort,
		    artQuery.getBaseEncounter());
		dose = pmtctARTLineListQuery.getByResult(PMTCTConceptQuestions.PMTCT_DOSE, baseCohort, artQuery.getBaseEncounter());
		nutritionalStatus = pmtctARTLineListQuery.getByResult(FollowUpConceptQuestions.NUTRITIONAL_STATUS_ADULT, baseCohort,
		    artQuery.getBaseEncounter());
		latestVLStatus = pmtctARTLineListQuery.getByResult(FollowUpConceptQuestions.LATEST_VL_STATUS, baseCohort,
		    artQuery.getBaseEncounter());
		adherence = pmtctARTLineListQuery.getByResult(FollowUpConceptQuestions.ARV_ADHERENCE, baseCohort,
		    artQuery.getBaseEncounter());
		nextVisitDate = pmtctARTLineListQuery.getObsValueDate(artQuery.getBaseEncounter(),
		    FollowUpConceptQuestions.NEXT_VISIT_DATE, baseCohort);
		
	}
	
	private void addColumnValue(String name, String label, HashMap<Integer, Object> object, DataSetRow row, Person person) {
		row.addColumnValue(new DataSetColumn(name, label, String.class), object.get(person.getPersonId()));
	}
}
