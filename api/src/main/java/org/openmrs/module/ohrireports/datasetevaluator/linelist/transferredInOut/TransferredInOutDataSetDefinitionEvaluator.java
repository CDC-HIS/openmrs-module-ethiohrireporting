package org.openmrs.module.ohrireports.datasetevaluator.linelist.transferredInOut;

import java.util.*;

import org.openmrs.*;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.impl.query.TransferInOutQuery;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.TransferredInOutDataSetDefinition;
import org.openmrs.module.ohrireports.datasetevaluator.linelist.LineListUtilities;
import org.openmrs.module.ohrireports.reports.linelist.TXTBReport;
import org.openmrs.module.ohrireports.reports.linelist.TransferInOutReport;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.SCHEDULE_TYPE;

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
	
	private PatientQueryService patientQuery;
	
	private EvaluationContext evalContext;
	
	@Autowired
	private TransferInOutQuery transferInOutQuery;
	
	@Autowired
	private TransferredInOutLineListQuery transferredInOutLineListQuery;
	
	private HashMap<Integer, Object> mrnIdentifierHashMap, uanIdentifierHashMap, artStartDictionary, followUpDate;
	
	@Override
	public DataSet evaluate(DataSetDefinition _dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		tDataSetDefinition = (TransferredInOutDataSetDefinition) _dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(tDataSetDefinition, this.evalContext);
		
		if (Objects.isNull(tDataSetDefinition.getEndDate()))
			tDataSetDefinition.setEndDate(Calendar.getInstance().getTime());
		
		transferInOutQuery.setStartDate(tDataSetDefinition.getStartDate());
		transferInOutQuery.setEndDate(tDataSetDefinition.getEndDate());
		transferInOutQuery.setStatus(tDataSetDefinition.getStatus());
		
		transferInOutQuery.setToCohort(transferInOutQuery.getTOCohort());
		
		patientQuery = Context.getService(PatientQueryService.class);
		String status = transferInOutQuery.getStatus();
		
		if (transferInOutQuery.getStatus().equals(TransferInOutReport.to)) {
			
			Cohort cohort = transferInOutQuery.getTOCohort();
			List<Person> persons = LineListUtilities.sortPatientByName(transferInOutQuery.getPersons(cohort));
			
			loadColumnDictionary(cohort, transferInOutQuery.getBaseEncounter());
			DataSetRow row;
			
			if (persons.size() > 0) {
				
				row = new DataSetRow();
				
				row.addColumnValue(new DataSetColumn("Name", "Name", String.class), "TOTAL");
				row.addColumnValue(new DataSetColumn("MRN", "MRN", Integer.class), persons.size());
				
				dataSet.addRow(row);
				
				for (Person person : persons) {
					
					Date artStartDate = transferredInOutLineListQuery.getDate(artStartDictionary.get(person.getPersonId()));
					Date _followUpDate = transferredInOutLineListQuery.getDate(followUpDate.get(person.getPersonId()));
					String followUpEthiopianDate = transferredInOutLineListQuery.getEthiopianDate(_followUpDate);
					row = new DataSetRow();
					
					row.addColumnValue(new DataSetColumn("Name", "Patient Name", String.class), person.getNames());
					addColumnValue("MRN", "MRN", mrnIdentifierHashMap, row, person);
					addColumnValue("UAN", "UAN", uanIdentifierHashMap, row, person);
					row.addColumnValue(new DataSetColumn("Age", "Age", String.class),
					    person.getAge(tDataSetDefinition.getEndDate()));
					row.addColumnValue(new DataSetColumn("Gender", "Sex", Integer.class), person.getGender());
					row.addColumnValue(new DataSetColumn("ArtStartDateETH", "Art Start Date ETH", Date.class),
					    transferredInOutLineListQuery.getEthiopianDate(artStartDate));
					row.addColumnValue(new DataSetColumn("followUpDateEth", "FollowUp Date ETH", String.class),
					    followUpEthiopianDate);
					dataSet.addRow(row);
				}
				
				return dataSet;
			}
			return null;
			
		} else {
			Cohort cohort = transferInOutQuery.getTICohort();
			List<Person> persons = transferInOutQuery.getPersons(cohort);
			
			loadColumnDictionary(cohort, transferInOutQuery.getFirstEncounter());
			DataSetRow row;
			
			if (persons.size() > 0) {
				
				row = new DataSetRow();
				
				row.addColumnValue(new DataSetColumn("Name", "Name", String.class), "TOTAL");
				row.addColumnValue(new DataSetColumn("MRN", "MRN", Integer.class), persons.size());
				
				dataSet.addRow(row);
				
				for (Person person : persons) {
					
					Date artStartDate = transferredInOutLineListQuery.getDate(artStartDictionary.get(person.getPersonId()));
					
					Date _followUpDate = transferredInOutLineListQuery.getDate(followUpDate.get(person.getPersonId()));
					String followUpEthiopianDate = transferredInOutLineListQuery.getEthiopianDate(_followUpDate);
					
					row = new DataSetRow();
					
					row.addColumnValue(new DataSetColumn("Name", "Patient Name", String.class), person.getNames());
					addColumnValue("MRN", "MRN", mrnIdentifierHashMap, row, person);
					addColumnValue("UAN", "UAN", uanIdentifierHashMap, row, person);
					row.addColumnValue(new DataSetColumn("Age", "Age", String.class),
					    person.getAge(tDataSetDefinition.getEndDate()));
					row.addColumnValue(new DataSetColumn("Gender", "Sex", Integer.class), person.getGender());
					row.addColumnValue(new DataSetColumn("followUpDate", "TI Date", Date.class), followUpEthiopianDate);
					row.addColumnValue(new DataSetColumn("ArtStartDateETH", "Art Start Date ETH", Date.class),
					    transferredInOutLineListQuery.getEthiopianDate(artStartDate));
					dataSet.addRow(row);
				}
				
				return dataSet;
			}
			
			return null;
			
		}
	}
	
	private void loadColumnDictionary(Cohort baseCohort, List<Integer> followUpEncounters) {
		mrnIdentifierHashMap = transferredInOutLineListQuery.getIdentifier(baseCohort, MRN_PATIENT_IDENTIFIERS);
		uanIdentifierHashMap = transferredInOutLineListQuery.getIdentifier(baseCohort, UAN_PATIENT_IDENTIFIERS);
		artStartDictionary = transferredInOutLineListQuery
		        .getArtStartDate(baseCohort, null, transferInOutQuery.getEndDate());
		followUpDate = transferredInOutLineListQuery.getObsValueDate(followUpEncounters, FOLLOW_UP_DATE, baseCohort);
	}
	
	private void addColumnValue(String name, String label, HashMap<Integer, Object> object, DataSetRow row, Person person) {
		row.addColumnValue(new DataSetColumn(name, label, String.class), object.get(person.getPersonId()));
	}
	
}
