package org.openmrs.module.ohrireports.datasetevaluator.linelist.PreExposureProphylaxis;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.PreExposureProphylaxisQuery;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.PreExposureProphylaxisDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Handler(supports = { PreExposureProphylaxisDataSetDefinition.class })
public class PreExposureProphylaxisDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	PreExposureProphylaxisQuery preExposureProphylaxisQuery;
	
	@Autowired
	private PreExposureProphylaxisLineListQuery preExposureProphylaxisLineListQuery;
	
	PreExposureProphylaxisDataSetDefinition _dataSetDefinition;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		_dataSetDefinition = (PreExposureProphylaxisDataSetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		
		preExposureProphylaxisQuery.setStartDate(_dataSetDefinition.getStartDate());
		preExposureProphylaxisQuery.setEndDate(_dataSetDefinition.getEndDate());
		
		Cohort baseCohort = preExposureProphylaxisQuery.loadPrepCohort();
		List<Person> persons = preExposureProphylaxisQuery.getPersons(baseCohort);
		
		HashMap<Integer, Object> mrnIdentifierHashMap = preExposureProphylaxisLineListQuery.getIdentifier(baseCohort,
		    MRN_PATIENT_IDENTIFIERS);
		/*HashMap<Integer, Object> uanIdentifierHashMap = preExposureProphylaxisLineListQuery.getIdentifier(baseCohort,
		    UAN_PATIENT_IDENTIFIERS);*/
		
		HashMap<Integer, Object> screenedDate = preExposureProphylaxisLineListQuery.getScreeningObsValueDate(
		    PREP_SCREENED_DATE, baseCohort);
		HashMap<Integer, Object> prepStartDate = preExposureProphylaxisLineListQuery.getScreeningObsValueDate(
		    PREP_STARTED_DATE, baseCohort);
		
		HashMap<Integer, Object> status = preExposureProphylaxisLineListQuery.getConceptName(PREP_FOLLOWUP_STATUS,
		    baseCohort, PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		HashMap<Integer, Object> uniqueIdentificationCode = preExposureProphylaxisLineListQuery.getConceptValue(
		    UNIQUE_IDENTIFICATION_CODE, baseCohort, PREP_SCREENING_ENCOUNTER_TYPE);
		HashMap<Integer, Object> followUpDate = preExposureProphylaxisLineListQuery.getObsValueDate(
		    preExposureProphylaxisQuery.getBaseFollowupEncounter(), FOLLOW_UP_DATE, baseCohort,
		    PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		HashMap<Integer, Object> nextVisitDate = preExposureProphylaxisLineListQuery.getObsValueDate(
		    preExposureProphylaxisQuery.getBaseFollowupEncounter(), PREP_NEXT_VISIT_DATE, baseCohort,
		    PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		HashMap<Integer, Object> prepRegimen = preExposureProphylaxisLineListQuery.getConceptName(PREP_REGIMEN, baseCohort,
		    PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		HashMap<Integer, Object> prepDose = preExposureProphylaxisLineListQuery.getConceptName(PREP_DOSE, baseCohort,
		    PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		HashMap<Integer, Object> prepDoseEndDate = preExposureProphylaxisLineListQuery.getObsValueDate(
		    preExposureProphylaxisQuery.getBaseFollowupEncounter(), PREP_DOSE_END_DATE, baseCohort,
		    PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		HashMap<Integer, Object> selfIdentifyingFSW = preExposureProphylaxisLineListQuery.getConceptName(
		    SELF_IDENTIFYING_FSW, baseCohort, PREP_SCREENING_ENCOUNTER_TYPE);
		HashMap<Integer, Object> haveHIVPositivePartner = preExposureProphylaxisLineListQuery.getConceptName(
		    HAVE_HIV_POSITIVE_PARTNER, baseCohort, PREP_SCREENING_ENCOUNTER_TYPE);
		HashMap<Integer, Object> hivTestFinalResult = preExposureProphylaxisLineListQuery.getConceptName(
		    HIV_TEST_FINAL_RESULT, baseCohort, PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		HashMap<Integer, Object> tbScreenedResult = preExposureProphylaxisLineListQuery.getConceptName(TB_SCREENED_RESULT,
		    baseCohort, PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		HashMap<Integer, Object> stiScreenResult = preExposureProphylaxisLineListQuery.getConceptName(STI_SCREENING_RESULT,
		    baseCohort, PREP_SCREENING_ENCOUNTER_TYPE);
		HashMap<Integer, Object> eGFREstimate = preExposureProphylaxisLineListQuery.getConceptName(EGFR_ESTIMATE,
		    baseCohort, PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		HashMap<Integer, Object> adherence = preExposureProphylaxisLineListQuery.getConceptName(PREP_MISSED_TABLETS,
		    baseCohort, PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		HashMap<Integer, Object> reasonToStopPrep = preExposureProphylaxisLineListQuery.getConceptName(
		    REASON_FOR_STOPPING_PREP, baseCohort, PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		
		DataSetRow row = new DataSetRow();
		row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class), "Total");
		row.addColumnValue(new DataSetColumn("Name", "Name", Integer.class), baseCohort.getSize());
		dataSet.addRow(row);
		for (Person person : persons) {
			row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("Name", "Name", String.class), person.getNames());
			addColumnValue("MRN", "MRN", mrnIdentifierHashMap, row, person);
			//addColumnValue("UAN", "UAN", uanIdentifierHashMap, row, person);
			addColumnValue("screenedDate", "Screened Date", screenedDate, row, person);
			row.addColumnValue(new DataSetColumn("Age", "Age", String.class), person.getAge(_dataSetDefinition.getEndDate()));
			row.addColumnValue(new DataSetColumn("Sex", "Sex", String.class), person.getGender());
			addColumnValue("prepStartDate", "PrEPStart Date", prepStartDate, row, person);
			addColumnValue("prepFollowupStatus", "Status", status, row, person);
			addColumnValue("uniqueIdentificationCode", "UIC", uniqueIdentificationCode, row, person);
			addColumnValue("followUpDate", "FollowUp Date", followUpDate, row, person);
			addColumnValue("nextVisitDate", "Next Visit Date", nextVisitDate, row, person);
			addColumnValue("prepRegimen", "Regimen", prepRegimen, row, person);
			addColumnValue("prepDose", "Dose", prepDose, row, person);
			addColumnValue("prepDoseEndDate", "Dose End", prepDoseEndDate, row, person);
			addColumnValue("selfIdentifyingFSW", "Self Identifying FSW", selfIdentifyingFSW, row, person);
			addColumnValue("haveHIVPositivePartner", "Have HIV Positive Partner", haveHIVPositivePartner, row, person);
			addColumnValue("hivTestFinalResult", "HIV Test Final Result", hivTestFinalResult, row, person);
			addColumnValue("tbScreenedResult", "TB Screened Result", tbScreenedResult, row, person);
			addColumnValue("stiScreenResult", "STI Screened Result", stiScreenResult, row, person);
			addColumnValue("eGFREstimate", "EGFR Estimate", eGFREstimate, row, person);
			addColumnValue("adherence", "Adherence", adherence, row, person);
			addColumnValue("reasonToStopPrep", "Reason To Stop PREP", reasonToStopPrep, row, person);
			dataSet.addRow(row);
			
		}
		return dataSet;
		
	}
	
	private void addColumnValue(String name, String label, HashMap<Integer, Object> object, DataSetRow row, Person person) {
		row.addColumnValue(new DataSetColumn(name, label, String.class), object.get(person.getPersonId()));
	}
}
