package org.openmrs.module.ohrireports.datasetevaluator.linelist.transferredInOut;

import java.util.*;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.impl.query.TransferInOutQuery;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.openmrs.module.ohrireports.constants.Identifiers;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.TransferredInOutDataSetDefinition;
import org.openmrs.module.ohrireports.datasetevaluator.linelist.LineListUtilities;
import org.openmrs.module.ohrireports.reports.linelist.TransferInOutReport;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

/*
 *
 * =================================================== Report Name ====================================================================
 * Transferred IN/OUT
 * ================================================== Report objective ===================================================================
 * To list all clients who are transferred to another health facility during the reporting period (TO)
 * To list all clients who are transferred to the facility from another health facility for ARV treatment during the reporting period (TI)
 * =======================================================================================================================================
 * =============================================== Columns ===============================================================================
 * Description
 * Concept ID
 * Patient Name
 * MRN
 * UAN
 * Age
 * Sex
 * ART Start Date
 * Last Follow-up Date
 * Follow-up Status
 * Adherence
 * Regimen
 * Next Visit Date
 * Referral Status
 * Total Patients
 * ================================================================ Key Assumptions ==============================================================
 * The report will include all patients who have TO as the value for their follow-up status in the maximum available follow-up record which is greater than or equal to the Reporting Start Date and less than or equal to the Reporting End date.
 *
 * The report will also include patients whose Reason for Eligibility is TI in the maximum available follow-up record which is greater than or equal to the Reporting Start Date and less than or equal to the Reporting End date.
 * The current date (today) will be the Reporting End date if no date filtration criteria is selected.
 *
 * The default value for all the filtration criteria will be ‘All’
 * The report query range will include data on the start and end date of the reporting period 
 * When counting the number of days between two dates, the count will always include both weekends and public holidays.
 * The user will have the option to filter the report using the criteria listed on the Filtration Criteria section of this document.
 * Pseudo Algorithm
 * Before a patient record may be included into the report dataset, the following pseudo algorithm must pass:
 *
 * If ART Started = Yes
 *
 *      AND
 * If there is an ART Start Date and the ART Start Date is <= the reporting end date
 *      AND
 * If patient has Follow-up record >=Report Start Date and <=Report End Date
 *      AND
 * If follow-up status = TO in the maximum available follow-up record
 *      OR
 * If Reason for Eligibility = TI in the maximum available follow-up record
 *      ONLY THEN
 * count the record
 * Filtration Criteria
 * Regimen - (List of Regimens)
 * Age – (From…..To…..)
 * Sex – (All, Male, Female)
 * Adherence – (All, Good, Fair, Poor)
 * Referral Status – (All, TI, TO)
 * TI/TO Date – Date range (From(Date) – To(Date))
 * Disaggregation
 *
 */
@Handler(supports = { TransferredInOutDataSetDefinition.class })
public class TransferredInOutDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	private TransferredInOutDataSetDefinition tDataSetDefinition;
	
	private EvaluationContext evalContext;
	
	@Autowired
	private TransferInOutQuery transferInOutQuery;
	
	@Autowired
	private TransferredInOutLineListQuery transferredInOutLineListQuery;
	
	private HashMap<Integer, Object> mrnIdentifierHashMap, uanIdentifierHashMap, artStartDictionary, followUpDate,
	        followUpStatus, regimen, arvDose, adherence;
	
	@Override
	public DataSet evaluate(DataSetDefinition _dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		tDataSetDefinition = (TransferredInOutDataSetDefinition) _dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(tDataSetDefinition, this.evalContext);
		
		// Check start date and end date are valid
		// If start date is greater than end date
		if (tDataSetDefinition.getStartDate() != null && tDataSetDefinition.getEndDate() != null
		        && tDataSetDefinition.getStartDate().compareTo(tDataSetDefinition.getEndDate()) > 0) {
			//throw new EvaluationException("Start date cannot be greater than end date");
			DataSetRow row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("Error", "Error", Integer.class),
			    "Report start date cannot be after report end date");
			dataSet.addRow(row);
			return dataSet;
		}
		
		if (Objects.isNull(tDataSetDefinition.getEndDate()))
			tDataSetDefinition.setEndDate(Calendar.getInstance().getTime());
		
		transferInOutQuery.setStartDate(tDataSetDefinition.getStartDate());
		transferInOutQuery.setEndDate(tDataSetDefinition.getEndDate());
		transferInOutQuery.setStatus(tDataSetDefinition.getStatus());
		
		if (transferInOutQuery.getStatus().equals(TransferInOutReport.to)) {
			
			Cohort cohort = transferInOutQuery.getTOCohort();
			List<Person> persons = LineListUtilities.sortPatientByName(transferInOutQuery.getPersons(cohort));
			
			loadColumnDictionary(cohort, transferInOutQuery.getBaseEncounter());
			DataSetRow row;
			
			if (!persons.isEmpty()) {
				
				row = new DataSetRow();
				
				row.addColumnValue(new DataSetColumn("#", "#", String.class), "TOTAL");
				row.addColumnValue(new DataSetColumn("GUID", "GUID", Integer.class), persons.size());
				
				dataSet.addRow(row);
			} else {
				dataSet.addRow(LineListUtilities.buildEmptyRow(Arrays.asList("#", "GUID", "Patient Name", "MRN", "UAN",
				    "Age", "Sex", "ART Start Date", "Follow-up Date (Date of TO)", "Follow-up Status", "Last Regimen",
				    "Last ARV Dose", "Last Adherence")));
			}
			int i = 1;
			for (Person person : persons) {
				
				Date artStartDate = transferredInOutLineListQuery.getDate(artStartDictionary.get(person.getPersonId()));
				Date _followUpDate = transferredInOutLineListQuery.getDate(followUpDate.get(person.getPersonId()));
				String followUpEthiopianDate = transferredInOutLineListQuery.getEthiopianDate(_followUpDate);
				
				row = new DataSetRow();
				row.addColumnValue(new DataSetColumn("#", "#", Integer.class), i++);
				row.addColumnValue(new DataSetColumn("GUID", "GUID", String.class), person.getUuid());
				row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", String.class), person.getNames());
				addColumnValue("MRN", "MRN", mrnIdentifierHashMap, row, person);
				addColumnValue("UAN", "UAN", uanIdentifierHashMap, row, person);
				row.addColumnValue(new DataSetColumn("Age", "Age", String.class),
				    person.getAge(tDataSetDefinition.getEndDate()));
				row.addColumnValue(new DataSetColumn("Sex", "Sex", Integer.class), person.getGender());
				row.addColumnValue(new DataSetColumn("ArtStartDateETH", "Art Start Date ETH", Date.class),
				    transferredInOutLineListQuery.getEthiopianDate(artStartDate));
				row.addColumnValue(new DataSetColumn("Follow-up Date (Date of TO)", "Follow-up Date (Date of TO)",
				        String.class), followUpEthiopianDate);
				row.addColumnValue(new DataSetColumn("followUpStatus", "Follow-up Status", Integer.class),
				    followUpStatus.get(person.getPersonId()));
				row.addColumnValue(
				    new DataSetColumn("LastRegimen", "Last Regimen", String.class),
				    transferredInOutLineListQuery.getByResult(FollowUpConceptQuestions.REGIMEN, cohort,
				        transferInOutQuery.getBeforeLastEncounter()).get(person.getPersonId()));
				row.addColumnValue(
				    new DataSetColumn("LastDose", "Last ARV Dose", Integer.class),
				    transferredInOutLineListQuery.getByResult(FollowUpConceptQuestions.ART_DISPENSE_DOSE, cohort,
				        transferInOutQuery.getBeforeLastEncounter()).get(person.getPersonId()));
				row.addColumnValue(
				    new DataSetColumn("LastAdherence", "Last Adherence", Integer.class),
				    transferredInOutLineListQuery.getByResult(FollowUpConceptQuestions.ARV_ADHERENCE, cohort,
				        transferInOutQuery.getBeforeLastEncounter()).get(person.getPersonId()));
				dataSet.addRow(row);
			}
			
			return dataSet;
			
		} else {
			Cohort cohort = transferInOutQuery.getTICohort();
			List<Person> persons = LineListUtilities.sortPatientByName(transferInOutQuery.getPersons(cohort));
			
			loadColumnDictionary(cohort, transferInOutQuery.getFirstEncounter());
			HashMap<Integer, Object> nextVisitDateHashMap = transferredInOutLineListQuery.getObsValueDate(
			    transferInOutQuery.getLastEncounter(), FollowUpConceptQuestions.NEXT_VISIT_DATE, cohort);
			DataSetRow row;
			
			if (!persons.isEmpty()) {
				
				row = new DataSetRow();
				
				row.addColumnValue(new DataSetColumn("#", "#", String.class), "TOTAL");
				row.addColumnValue(new DataSetColumn("GUID", "GUID", Integer.class), persons.size());
				
				dataSet.addRow(row);
			} else {
				dataSet.addRow(LineListUtilities.buildEmptyRow(Arrays.asList("#", "GUID", "Patient Name", "MRN", "UAN",
				    "Age", "Sex", "ART Start Date", "Follow-up Date (Date of TI)", "Latest Follow-up Status",
				    "Last Regimen", "Last ARV Dose", "Last Adherence", "Next Visit Date")));
			}
			int i = 1;
			for (Person person : persons) {
				
				Date artStartDate = transferredInOutLineListQuery.getDate(artStartDictionary.get(person.getPersonId()));
				
				Date _followUpDate = transferredInOutLineListQuery.getDate(followUpDate.get(person.getPersonId()));
				Date nextVisitDate = transferredInOutLineListQuery.getDate(nextVisitDateHashMap.get(person.getPersonId()));
				
				row = new DataSetRow();
				row.addColumnValue(new DataSetColumn("#", "#", Integer.class), i++);
				row.addColumnValue(new DataSetColumn("GUID", "GUID", String.class), person.getUuid());
				row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", String.class), person.getNames());
				addColumnValue("MRN", "MRN", mrnIdentifierHashMap, row, person);
				addColumnValue("UAN", "UAN", uanIdentifierHashMap, row, person);
				row.addColumnValue(new DataSetColumn("Age", "Age", String.class),
				    person.getAge(tDataSetDefinition.getEndDate()));
				row.addColumnValue(new DataSetColumn("Gender", "Sex", Integer.class), person.getGender());
				row.addColumnValue(new DataSetColumn("ArtStartDateETH", "Art Start Date", Date.class),
				    transferredInOutLineListQuery.getEthiopianDate(artStartDate));
				row.addColumnValue(new DataSetColumn("Follow-up Date (Date of TI)", "Follow-up Date (Date of TI)",
				        Integer.class), transferredInOutLineListQuery.getEthiopianDate(_followUpDate));
				row.addColumnValue(
				    new DataSetColumn("followUpStatus", "Latest Follow-up Status", Integer.class),
				    transferredInOutLineListQuery.getByResult(FollowUpConceptQuestions.FOLLOW_UP_STATUS, cohort,
				        transferInOutQuery.getLastEncounter()).get(person.getPersonId()));
				row.addColumnValue(
				    new DataSetColumn("LastRegimen", "Last Regimen", String.class),
				    transferredInOutLineListQuery.getByResult(FollowUpConceptQuestions.REGIMEN, cohort,
				        transferInOutQuery.getLastEncounter()).get(person.getPersonId()));
				row.addColumnValue(
				    new DataSetColumn("LastDose", "Last ARV Dose", Integer.class),
				    transferredInOutLineListQuery.getByResult(FollowUpConceptQuestions.ART_DISPENSE_DOSE, cohort,
				        transferInOutQuery.getLastEncounter()).get(person.getPersonId()));
				row.addColumnValue(
				    new DataSetColumn("LastAdherence", "Last Adherence", Integer.class),
				    transferredInOutLineListQuery.getByResult(FollowUpConceptQuestions.ARV_ADHERENCE, cohort,
				        transferInOutQuery.getLastEncounter()).get(person.getPersonId()));
				row.addColumnValue(new DataSetColumn("NextVisitDate", "Next Visit Date", Date.class),
				    transferredInOutLineListQuery.getEthiopianDate(nextVisitDate));
				dataSet.addRow(row);
			}
			
			return dataSet;
			
		}
	}
	
	private void loadColumnDictionary(Cohort baseCohort, List<Integer> followUpEncounters) {
		mrnIdentifierHashMap = transferredInOutLineListQuery.getIdentifier(baseCohort, Identifiers.MRN_PATIENT_IDENTIFIERS);
		uanIdentifierHashMap = transferredInOutLineListQuery.getIdentifier(baseCohort, Identifiers.UAN_PATIENT_IDENTIFIERS);
		artStartDictionary = transferredInOutLineListQuery
		        .getArtStartDate(baseCohort, null, transferInOutQuery.getEndDate());
		followUpDate = transferredInOutLineListQuery.getObsValueDate(followUpEncounters,
		    FollowUpConceptQuestions.FOLLOW_UP_DATE, baseCohort);
		followUpStatus = transferredInOutLineListQuery.getFollowUpStatus(followUpEncounters, baseCohort);
		regimen = transferredInOutLineListQuery
		        .getByResult(FollowUpConceptQuestions.REGIMEN, baseCohort, followUpEncounters);
		arvDose = transferredInOutLineListQuery.getByResult(FollowUpConceptQuestions.ART_DISPENSE_DOSE, baseCohort,
		    followUpEncounters);
		adherence = transferredInOutLineListQuery.getByResult(FollowUpConceptQuestions.ARV_ADHERENCE, baseCohort,
		    followUpEncounters);
	}
	
	private void addColumnValue(String name, String label, HashMap<Integer, Object> object, DataSetRow row, Person person) {
		row.addColumnValue(new DataSetColumn(name, label, String.class), object.get(person.getPersonId()));
	}
	
}
