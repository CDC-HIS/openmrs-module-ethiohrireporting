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
		
		Cohort baseCohort = artQuery.getBaseCohort();
		List<Person> persons = artQuery.getPersons(baseCohort);
		
		loadColumnDictionary(baseCohort);
		
		DataSetRow row;
		
		if (!persons.isEmpty()) {
			
			row = new DataSetRow();
			
			row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class), "TOTAL");
			row.addColumnValue(new DataSetColumn("Name", "Name", Integer.class), persons.size());
			
			dataSet.addRow(row);
		} else {
			dataSet.addRow(LineListUtilities.buildEmptyRow(Arrays.asList("Name", "Sex", "MRN", "UAN", "Age", "ArtStartDate",
			    "ArtStartDate Eth", "PMTCTReferredDate GC", "PMTCT Booking Date EC.", "Status At Enrollment", "Is Pregnant",
			    "Is BreastFeeding", "Latest FollowupDate", "Latest FollowupDate ETH", "Regimen", "Dose",
			    "NutritionalStatus", "Latest VL Status", "Adherence", "NextVisitDateGC.", "Next Visit Date EC."), "Name",
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
			row.addColumnValue(new DataSetColumn("ArtStartDate", "Art Start Date GC.", String.class),
			    pmtctARTLineListQuery.getEthiopianDate(artStartDate));
			row.addColumnValue(new DataSetColumn("ArtStartDateEth", "Art Start Date EC.", String.class),
			    pmtctARTLineListQuery.getEthiopianDate(artStartDate));
			/*row.addColumnValue(new DataSetColumn("PMTCTBookingDateEth", "PMTCT Booking Date ETH", String.class),
			        pmtctARTLineListQuery.getEthiopianDate(pmtctBookingDate));*/
			addColumnValue("StatusAtEnrollment", "Status at Enrollment", statusAtEnrollment, row, person);
			row.addColumnValue(new DataSetColumn("PMTCTReferredDate GC", "Date Referred to PMTCT GC.", String.class),
			    pmtctRefferedDate);
			row.addColumnValue(new DataSetColumn("PMTCTReferredDate", "Date Referred to PMTCT EC.", String.class),
			    pmtctARTLineListQuery.getEthiopianDate(pmtctRefferedDate));
			addColumnValue("IsPregnant", "Pregnant?", isPregnant, row, person);
			addColumnValue("IsBreastFeeding", "Breastfeeding?", isBreastFeeding, row, person);
			/*row.addColumnValue(new DataSetColumn("DischargeDateETH", "Date of Discharge ETH", String.class),
			    pmtctARTLineListQuery.getEthiopianDate(dischargeDate));
			addColumnValue("ReasonForDischarge", "Reason For Discharge", reasonForDischarge, row, person);
			addColumnValue("MaternalPMTCTFinalOutcome", "Maternal PMTCT Final Outcome", maternalPMTCTFinalOutcome, row,
			    person);
			row.addColumnValue(new DataSetColumn("DateOfFinalOutcomeETH", "Date of Final Outcome ETH", String.class),
			    pmtctARTLineListQuery.getEthiopianDate(finalOutcomeDate));*/
			row.addColumnValue(new DataSetColumn("LatestFollowupDate GC.", "Latest Follow-up Date GC.", String.class),
			    latestFollowupDate);
			row.addColumnValue(new DataSetColumn("LatestFollowupDateETH", "Latest Follow-up Date EC.", String.class),
			    pmtctARTLineListQuery.getEthiopianDate(latestFollowupDate));
			addColumnValue("Regimen", "Regimen", regimen, row, person);
			addColumnValue("Dose", "Dose", dose, row, person);
			addColumnValue("NutritionalStatus", "Nutritional Status", nutritionalStatus, row, person);
			addColumnValue("LatestVLStatus", "Latest VL Status", latestVLStatus, row, person);
			addColumnValue("Adherence", "Adherence", adherence, row, person);
			row.addColumnValue(new DataSetColumn("NextVisitDateGC.", "Next Visit Date GC.", String.class), nextVisitDate);
			
			row.addColumnValue(new DataSetColumn("NextVisitDateETH", "Next Visit Date EC.", String.class),
			    pmtctARTLineListQuery.getEthiopianDate(nextVisitDate));
			
			dataSet.addRow(row);
			
		}
		return dataSet;
	}
	
	private void loadColumnDictionary(Cohort baseCohort) {
		mrnIdentifierHashMap = pmtctARTLineListQuery.getIdentifier(baseCohort, Identifiers.MRN_PATIENT_IDENTIFIERS);
		uanIdentifierHashMap = pmtctARTLineListQuery.getIdentifier(baseCohort, Identifiers.UAN_PATIENT_IDENTIFIERS);
		
		artStartDate = pmtctARTLineListQuery.getObsValueDate(artQuery.getLatestFollowUpEncounter(),
		    FollowUpConceptQuestions.ART_START_DATE, baseCohort);
		pmtctBookingDate = pmtctARTLineListQuery.getObsValueDate(artQuery.getLatestFollowUpEncounter(),
		    PMTCTConceptQuestions.PMTCT_OTZ_ENROLLMENT_DATE, baseCohort);
		statusAtEnrollment = pmtctARTLineListQuery.getByResult(PMTCTConceptQuestions.PMTCT_STATUS_AT_ENROLLMENT, baseCohort,
		    artQuery.getLatestFollowUpEncounter());
		pmtctReferredDate = pmtctARTLineListQuery.getObsValueDate(artQuery.getLatestFollowUpEncounter(),
		    PMTCTConceptQuestions.PMTCT_REFERRED_DATE, baseCohort);
		isPregnant = pmtctARTLineListQuery.getByResult(FollowUpConceptQuestions.PREGNANCY_STATUS, baseCohort,
		    artQuery.getLatestFollowUpEncounter());
		isBreastFeeding = pmtctARTLineListQuery.getByResult(FollowUpConceptQuestions.CURRENTLY_BREAST_FEEDING_CHILD,
		    baseCohort, artQuery.getLatestFollowUpEncounter());
		dischargeDate = pmtctARTLineListQuery.getObsValueDate(artQuery.getLatestFollowUpEncounter(),
		    PMTCTConceptQuestions.PMTCT_DISCHARGE_DATE, baseCohort);
		reasonForDischarge = pmtctARTLineListQuery.getByResult(PMTCTConceptQuestions.REASON_FOR_DISCHARGE, baseCohort,
		    artQuery.getLatestFollowUpEncounter());
		maternalPMTCTFinalOutcome = pmtctARTLineListQuery.getByResult(PMTCTConceptQuestions.PMTCT_FINAL_OUTCOME, baseCohort,
		    artQuery.getLatestFollowUpEncounter());
		finalOutcomeDate = pmtctARTLineListQuery.getObsValueDate(artQuery.getLatestFollowUpEncounter(),
		    FollowUpConceptQuestions.FINAL_OUTCOME_DATE, baseCohort);
		latestFollowupDate = pmtctARTLineListQuery.getObsValueDate(artQuery.getLatestFollowUpEncounter(),
		    FollowUpConceptQuestions.FOLLOW_UP_DATE, baseCohort);
		regimen = pmtctARTLineListQuery.getByResult(FollowUpConceptQuestions.REGIMEN, baseCohort,
		    artQuery.getLatestFollowUpEncounter());
		dose = pmtctARTLineListQuery.getByResult(PMTCTConceptQuestions.PMTCT_DOSE, baseCohort,
		    artQuery.getLatestFollowUpEncounter());
		nutritionalStatus = pmtctARTLineListQuery.getConceptLabel(artQuery.getLatestFollowUpEncounter(), baseCohort,
		    FollowUpConceptQuestions.NUTRITIONAL_STATUS_ADULT);
		latestVLStatus = pmtctARTLineListQuery.getConceptLabel(artQuery.getLatestFollowUpEncounter(), baseCohort,
		    FollowUpConceptQuestions.LATEST_VL_STATUS);
		adherence = pmtctARTLineListQuery.getByResult(FollowUpConceptQuestions.ARV_ADHERENCE, baseCohort,
		    artQuery.getLatestFollowUpEncounter());
		nextVisitDate = pmtctARTLineListQuery.getObsValueDate(artQuery.getLatestFollowUpEncounter(),
		    FollowUpConceptQuestions.NEXT_VISIT_DATE, baseCohort);
		
	}
	
	private void addColumnValue(String name, String label, HashMap<Integer, Object> object, DataSetRow row, Person person) {
		row.addColumnValue(new DataSetColumn(name, label, String.class), object.get(person.getPersonId()));
	}
}
