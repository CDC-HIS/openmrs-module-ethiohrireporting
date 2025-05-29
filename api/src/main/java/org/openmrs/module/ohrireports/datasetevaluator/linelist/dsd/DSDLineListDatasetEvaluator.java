package org.openmrs.module.ohrireports.datasetevaluator.linelist.dsd;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.constants.EncounterType;
import org.openmrs.module.ohrireports.constants.Identifiers;
import org.openmrs.module.ohrireports.constants.IntakeAConceptQuestions;
import org.openmrs.module.ohrireports.constants.PositiveCaseTrackingConceptQuestions;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.DSDDataSetDefinition;
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

import static org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Handler(supports = { DSDDataSetDefinition.class })
public class DSDLineListDatasetEvaluator implements DataSetEvaluator {
	
	@Autowired
	private DSDLineListQuery dsdLineListQuery;
	
	/**
	 * Evaluate a DataSet for the given EvaluationContext
	 * 
	 * @return the evaluated <code>DataSet</code>
	 */
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		DSDDataSetDefinition dsdDataset = (DSDDataSetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(dsdDataset, evalContext);
		
		// Check start date and end date are valid
		// If start date is greater than end date
		if (dsdDataset.getStartDate() != null && dsdDataset.getEndDate() != null
		        && dsdDataset.getStartDate().compareTo(dsdDataset.getEndDate()) > 0) {
			//throw new EvaluationException("Start date cannot be greater than end date");
			DataSetRow row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("Error", "Error", Integer.class),
			    "Report start date cannot be after report end date");
			dataSet.addRow(row);
			return dataSet;
		}
		
		if (dsdDataset.getEndDate() == null) {
			dsdDataset.setEndDate(new Date());
		}
		dsdLineListQuery.generateReport(dsdDataset.getStartDate(), dsdDataset.getEndDate());
		Cohort cohort = dsdLineListQuery.getBaseCohort();
		
		HashMap<Integer, Object> followUpDateHashMap = dsdLineListQuery.getObsValueDate(dsdLineListQuery.getBaseEncounter(),
		    FOLLOW_UP_DATE, cohort);
		HashMap<Integer, Object> latestFollowUpDateHashMap = dsdLineListQuery.getObsValueDate(
		    dsdLineListQuery.getLatestEncounter(), FOLLOW_UP_DATE, cohort);
		HashMap<Integer, Object> confirmedDateHashMap = dsdLineListQuery.getObsValueDate(null,
		    IntakeAConceptQuestions.HIV_CONFIRMED_DATE, cohort, EncounterType.INTAKE_A_ENCOUNTER_TYPE);
		HashMap<Integer, Object> artStartDateHashMap = dsdLineListQuery.getObsValueDate(dsdLineListQuery.getBaseEncounter(),
		    ART_START_DATE, cohort);
		HashMap<Integer, Object> nextVistDateHashMap = dsdLineListQuery.getObsValueDate(
		    dsdLineListQuery.getLatestEncounter(), NEXT_VISIT_DATE, cohort);
		HashMap<Integer, Object> treatmentDateHashMap = dsdLineListQuery.getObsValueDate(
		    dsdLineListQuery.getLatestEncounter(), TREATMENT_END_DATE, cohort);
		HashMap<Integer, Object> categoryInitialAssesumentDateHashMap = dsdLineListQuery.getObsValueDate(
		    dsdLineListQuery.getBaseEncounter(), DSD_ASSESSMENT_DATE, cohort);
		HashMap<Integer, Object> latestCategoryAssesumentDateHashMap = dsdLineListQuery.getObsValueDate(
		    dsdLineListQuery.getLatestDSDAssessmentEncounter(), DSD_ASSESSMENT_DATE, cohort);
		HashMap<Integer, Object> transferedHashMap = dsdLineListQuery.getConceptName(dsdLineListQuery.getBaseEncounter(),
		    cohort, REASON_FOR_ART_ELIGIBILITY);
		HashMap<Integer, Object> initialDSDCategoryHashMap = dsdLineListQuery.getConceptName(
		    dsdLineListQuery.getInitialDSDAssessmentEncounter(), cohort, DSD_CATEGORY);
		HashMap<Integer, Object> currentDSDCategoryHashMap = dsdLineListQuery.getConceptName(
		    dsdLineListQuery.getLatestDSDAssessmentEncounter(), cohort, DSD_CATEGORY);
		HashMap<Integer, Object> doseDisPenseHashMap = dsdLineListQuery.getConceptName(dsdLineListQuery.getBaseEncounter(),
		    cohort, ARV_DISPENSED_IN_DAYS);
		HashMap<Integer, Object> latestDoseDisPenseHashMap = dsdLineListQuery.getConceptName(
		    dsdLineListQuery.getLatestEncounter(), cohort, ARV_DISPENSED_IN_DAYS);
		HashMap<Integer, Object> regimentHashMap = dsdLineListQuery.getRegiment(dsdLineListQuery.getBaseEncounter(), cohort);
		HashMap<Integer, Object> latestRegimentHashMap = dsdLineListQuery.getRegiment(dsdLineListQuery.getLatestEncounter(),
		    cohort);
		HashMap<Integer, Object> adherenceHashMap = dsdLineListQuery.getConceptName(dsdLineListQuery.getBaseEncounter(),
		    cohort, ARV_ADHERENCE);
		HashMap<Integer, Object> latestAdherenceHashMap = dsdLineListQuery.getConceptName(
		    dsdLineListQuery.getLatestEncounter(), cohort, ARV_ADHERENCE);
		HashMap<Integer, Object> followUpStatusHashMap = dsdLineListQuery.getConceptName(
		    dsdLineListQuery.getBaseEncounter(), cohort, FOLLOW_UP_STATUS);
		HashMap<Integer, Object> latestFollowUpStatusHashMap = dsdLineListQuery.getConceptName(
		    dsdLineListQuery.getLatestEncounter(), cohort, FOLLOW_UP_STATUS);
		HashMap<Integer, Object> mrnIdentifierHashMap = dsdLineListQuery.getIdentifier(cohort,
		    Identifiers.MRN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> uanIdentifierHashMap = dsdLineListQuery.getIdentifier(cohort,
		    Identifiers.UAN_PATIENT_IDENTIFIERS);
		
		DataSetRow row;
		List<Person> persons = LineListUtilities.sortPatientByName(dsdLineListQuery.getPersons(cohort));
		if (!persons.isEmpty()) {
			
			row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("#", "#", Integer.class), "TOTAL");
			row.addColumnValue(new DataSetColumn("GUID", "GUID", Integer.class), persons.size());
			
			dataSet.addRow(row);
		} else {
			dataSet.addRow(LineListUtilities.buildEmptyRow(Arrays.asList("#", "GUID", "Patient Name", "MRN", "UAN", "Age",
			    "Sex", "HIV Confirmed Date in E.C.", "ART Start Date in E.C.", "Mobile No.",
			    "Latest Follow-up Date in E.C.", "Latest Follow-up Status", "Latest Regimen", "Latest ARV Dose Days",
			    "Latest Adherence", "Next Visit Date in E.C.", "Treatment End Date in E.C.",
			    "Initial DSD Enrollment Date in E.C.", "DSD Category at Enrollment",
			    "Current DSD Category Entry Date in E.C.", "Current DSD Category", "Reason for Category change")));
		}
		
		int i = 1;
		for (Person person : persons) {
			
			Date followUpDateDate = dsdLineListQuery.getDate(followUpDateHashMap.get(person.getPersonId()));
			Date latestFollowUpDate = dsdLineListQuery.getDate(latestFollowUpDateHashMap.get(person.getPersonId()));
			Date confirmedDate = dsdLineListQuery.getDate(confirmedDateHashMap.get(person.getPersonId()));
			Date artStartDate = dsdLineListQuery.getDate(artStartDateHashMap.get(person.getPersonId()));
			Date nextVisitDate = dsdLineListQuery.getDate(nextVistDateHashMap.get(person.getPersonId()));
			Date initialAssesementDate = dsdLineListQuery.getDate(categoryInitialAssesumentDateHashMap.get(person
			        .getPersonId()));
			Date latestAssesementDate = dsdLineListQuery.getDate(latestCategoryAssesumentDateHashMap.get(person
			        .getPersonId()));
			Date treatmentEndDate = dsdLineListQuery.getDate(treatmentDateHashMap.get(person.getPersonId()));
			
			row = new DataSetRow();
			
			row.addColumnValue(new DataSetColumn("#", "#", Integer.class), i++);
			row.addColumnValue(new DataSetColumn("GUID", "GUID", String.class), person.getUuid());
			row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", String.class), person.getNames());
			row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class), mrnIdentifierHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("UAN", "UAN", String.class), uanIdentifierHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Age", "Age", Integer.class), person.getAge());
			row.addColumnValue(new DataSetColumn("Sex", "Sex", String.class), person.getGender());
			
			row.addColumnValue(new DataSetColumn("HIV Confirmed Date in E.C.", "HIV Confirmed Date in E.C.", String.class),
			    dsdLineListQuery.getEthiopianDate(confirmedDate));
			row.addColumnValue(new DataSetColumn("ART Start Date in E.C.", "ART Start Date in E.C.", String.class),
			    dsdLineListQuery.getEthiopianDate(artStartDate));
			row.addColumnValue(new DataSetColumn("Mobile No.", "Mobile No.", String.class),
			    LineListUtilities.getPhone(person.getActiveAttributes()));
			row.addColumnValue(new DataSetColumn("Latest Follow-up Date in E.C.", "Latest Follow-up Date in E.C.",
			        String.class), dsdLineListQuery.getEthiopianDate(latestFollowUpDate));
			row.addColumnValue(new DataSetColumn("Latest Follow-up Status", "Latest Follow-up Status", String.class),
			    latestFollowUpStatusHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Latest Regimen", "Latest Regimen", String.class),
			    latestRegimentHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Latest ARV Dose Days", "Latest ARV Dose Days", Integer.class),
			    latestDoseDisPenseHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Latest Adherence", "Latest Adherence", String.class),
			    latestAdherenceHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Next Visit Date in E.C.", "Next Visit Date in E.C.", String.class),
			    dsdLineListQuery.getEthiopianDate(nextVisitDate));
			row.addColumnValue(new DataSetColumn("Treatment End Date in E.C.", "Treatment End Date in E.C.", String.class),
			    dsdLineListQuery.getEthiopianDate(treatmentEndDate));
			row.addColumnValue(new DataSetColumn("Initial DSD Enrollment Date in E.C.",
			        "Initial DSD Enrollment Date in E.C.", String.class), dsdLineListQuery
			        .getEthiopianDate(initialAssesementDate));
			row.addColumnValue(new DataSetColumn("DSD Category at Enrollment", "DSD Category at Enrollment", String.class),
			    initialDSDCategoryHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Current DSD Category Entry Date in E.C.",
			        "Current DSD Category Entry Date in E.C.", String.class), dsdLineListQuery
			        .getEthiopianDate(latestAssesementDate));
			row.addColumnValue(new DataSetColumn("Current DSD Category", "Current DSD Category", String.class),
			    currentDSDCategoryHashMap.get(person.getPersonId()));
			dataSet.addRow(row);
			
		}
		
		return dataSet;
	}
	
	private int getDateDifference(Date confirmedDate, Date startArtDate) {
		if (Objects.isNull(confirmedDate) || Objects.isNull(startArtDate))
			return 0;
		return (int) TimeUnit.DAYS
		        .convert(Math.abs(startArtDate.getTime() - confirmedDate.getTime()), TimeUnit.MILLISECONDS);
	}
	
	private Object getStringIdentifier(Object patientIdentifier) {
		return Objects.isNull(patientIdentifier) ? "--" : patientIdentifier.toString();
	}
}
