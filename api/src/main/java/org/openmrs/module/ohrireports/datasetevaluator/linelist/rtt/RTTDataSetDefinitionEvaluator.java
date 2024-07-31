package org.openmrs.module.ohrireports.datasetevaluator.linelist.rtt;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.constants.ConceptAnswer;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.openmrs.module.ohrireports.constants.Identifiers;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.RTTDataSetDefinition;
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

import java.util.*;

@Handler(supports = { RTTDataSetDefinition.class })
public class RTTDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private RTTLineListQuery rttLineListQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		RTTDataSetDefinition _datasetDefinition = (RTTDataSetDefinition) dataSetDefinition;
		SimpleDataSet data = new SimpleDataSet(dataSetDefinition, evalContext);
		
		// Check start date and end date are valid
		// If start date is greater than end date
		if (_datasetDefinition.getStartDate() != null && _datasetDefinition.getEndDate() != null
		        && _datasetDefinition.getStartDate().compareTo(_datasetDefinition.getEndDate()) > 0) {
			DataSetRow row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("Error", "Error", Integer.class),
			    "Report start date cannot be after report end date");
			data.addRow(row);
			return data;
		}
		
		Cohort cohort = rttLineListQuery.getRTTCohort(_datasetDefinition.getStartDate(), _datasetDefinition.getEndDate());
		
		List<Person> persons = rttLineListQuery.getPerson(cohort);
		
		HashMap<Integer, Object> artStartDateHashMap = rttLineListQuery.getObsValueDate(rttLineListQuery.getBaseEncounter(),
		    FollowUpConceptQuestions.ART_START_DATE, cohort);
		HashMap<Integer, Object> followupDateHashMap = rttLineListQuery.getObsValueDate(rttLineListQuery.getBaseEncounter(),
		    FollowUpConceptQuestions.FOLLOW_UP_DATE, cohort);
		HashMap<Integer, Object> treatmentEndDate = rttLineListQuery.getObsValueDate(rttLineListQuery.getBaseEncounter(),
		    FollowUpConceptQuestions.TREATMENT_END_DATE, cohort);
		
		HashMap<Integer, Object> weightDateHashMap = rttLineListQuery.getByValueText(FollowUpConceptQuestions.WEIGHT,
		    cohort, rttLineListQuery.getBaseEncounter());
		HashMap<Integer, Object> cd4HashMap = rttLineListQuery.getByValueNumeric(FollowUpConceptQuestions.ADULT_CD4_COUNT,
		    cohort, rttLineListQuery.getBaseEncounter());
		HashMap<Integer, Object> mrnIdentifierHashMap = rttLineListQuery.getIdentifier(cohort,
		    Identifiers.MRN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> uanIdentifierHashMap = rttLineListQuery.getIdentifier(cohort,
		    Identifiers.UAN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> followUpStatus = rttLineListQuery.getFollowUpStatus(rttLineListQuery.getBaseEncounter(),
		    cohort);
		HashMap<Integer, Object> regimentHashMap = rttLineListQuery.getRegiment(rttLineListQuery.getBaseEncounter(), cohort);
		HashMap<Integer, Object> dispensDayHashMap = rttLineListQuery.getConceptName(rttLineListQuery.getBaseEncounter(),
		    cohort, FollowUpConceptQuestions.ARV_DISPENSED_IN_DAYS);
		HashMap<Integer, Object> adherence = rttLineListQuery.getByResult(ConceptAnswer.ON_ADHERENCE, cohort,
		    rttLineListQuery.getBaseEncounter());
		HashMap<Integer, Object> nextVisitDate = rttLineListQuery.getObsValueDate(rttLineListQuery.getBaseEncounter(),
		    FollowUpConceptQuestions.NEXT_VISIT_DATE, cohort);
		
		HashMap<Integer, Object> lastFollowUpDate = rttLineListQuery.getObsValueDate(
		    rttLineListQuery.getInterruptedEncounter(), FollowUpConceptQuestions.FOLLOW_UP_DATE, cohort);
		
		HashMap<Integer, Object> lastFollowUpStatus = rttLineListQuery.getFollowUpStatus(
		    rttLineListQuery.getInterruptedEncounter(), cohort);
		HashMap<Integer, Object> lastRegimen = rttLineListQuery.getRegiment(rttLineListQuery.getInterruptedEncounter(),
		    cohort);
		HashMap<Integer, Object> lastDispenseDay = rttLineListQuery.getConceptName(
		    rttLineListQuery.getInterruptedEncounter(), cohort, FollowUpConceptQuestions.ARV_DISPENSED_IN_DAYS);
		HashMap<Integer, Object> lastAdherence = rttLineListQuery.getByResult(ConceptAnswer.ON_ADHERENCE, cohort,
		    rttLineListQuery.getInterruptedEncounter());
		HashMap<Integer, Object> lastTreatmentEndDate = rttLineListQuery.getObsValueDate(
		    rttLineListQuery.getInterruptedEncounter(), FollowUpConceptQuestions.TREATMENT_END_DATE, cohort);
		
		// if last followup status is lost or dead then we can't find the treatment end date
		// thus we take last followup date as treatment end date
		if (lastTreatmentEndDate == null)
			lastTreatmentEndDate = lastFollowUpDate;
		
		DataSetRow row;
		if (!persons.isEmpty()) {
			row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("#", "#", Integer.class), "TOTAL");
			row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", Integer.class), persons.size());
			data.addRow(row);
		} else {
			data.addRow(LineListUtilities.buildEmptyRow(Arrays.asList("#", "Patient Name", "MRN", "UAN", "Age", "Sex",
			    "Weight", "ART Start Date in E.C", "Date Returned Treatment E.C.", "Follow-up Status", "Regimen",
			    "ARV Dose Days", "Adherence", "Next Visit Date in E.C", "Treatment End Date in E.C",
			    "Last Follow-up Date in E.C.", "Last Follow-up Status", "Last Regimen", "Last ARV Dose Days",
			    "Last Adherence", "Date Excluded from TX_CURR in E.C")));
		}
		int i = 1;
		for (Person person : persons) {
			
			Date artStartDate = rttLineListQuery.getDate(artStartDateHashMap.get(person.getPersonId()));
			Date treatmentEndDateET = rttLineListQuery.getDate(treatmentEndDate.get(person.getPersonId()));
			Date nextVisitDateET = rttLineListQuery.getDate(nextVisitDate.get(person.getPersonId()));
			Date followUpDateET = rttLineListQuery.getDate(followupDateHashMap.get(person.getPersonId()));
			Date lastFollowUpDateET = rttLineListQuery.getDate(lastFollowUpDate.get(person.getPersonId()));
			Date lastTreatmentEndDateET = rttLineListQuery.getDate(lastTreatmentEndDate.get(person.getPersonId()));
			
			row = new DataSetRow();
			
			row.addColumnValue(new DataSetColumn("#", "#", Integer.class), i++);
			row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", String.class), person.getNames());
			row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class),
			    getStringIdentifier(mrnIdentifierHashMap.get(person.getPersonId())));
			row.addColumnValue(new DataSetColumn("UAN", "UAN", String.class),
			    getStringIdentifier(uanIdentifierHashMap.get(person.getPersonId())));
			row.addColumnValue(new DataSetColumn("Age", "Age", Integer.class),
			    person.getAge(_datasetDefinition.getEndDate()));
			row.addColumnValue(new DataSetColumn("Gender", "Sex", String.class), person.getGender());
			row.addColumnValue(new DataSetColumn("Weight", "Weight", Integer.class),
			    weightDateHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("CD4", "CD4", Integer.class), cd4HashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("ArtStartDateETC", "ART Start Date in E.C", String.class),
			    rttLineListQuery.getEthiopianDate(artStartDate));
			row.addColumnValue(new DataSetColumn("dateReturnedTreatmentETC",
			        "Follow-up Date in E.C (Date returned to Treatment in E.C)", String.class), rttLineListQuery
			        .getEthiopianDate(followUpDateET));
			row.addColumnValue(new DataSetColumn("Follow-up Status", "Follow-up Status", String.class),
			    followUpStatus.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Regimen", "Regimen", String.class),
			    regimentHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("arvDispenseDay", "ARV Dose Days", String.class),
			    dispensDayHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("adherence", "Adherence", String.class),
			    adherence.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("nextVisitDateEC", "Next Visit Date in E.C", String.class),
			    rttLineListQuery.getEthiopianDate(nextVisitDateET));
			row.addColumnValue(new DataSetColumn("TreatmentEndDate", "Treatment End Date in E.C", Date.class),
			    rttLineListQuery.getEthiopianDate(treatmentEndDateET));
			row.addColumnValue(new DataSetColumn("LastFollowUpDate", "Last Follow-up Date in E.C", Date.class),
			    rttLineListQuery.getEthiopianDate(lastFollowUpDateET));
			row.addColumnValue(new DataSetColumn("LastFollowUpStatus", "Last Follow-up Status", String.class),
			    lastFollowUpStatus.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("LastRegimen", "Last Regimen", String.class),
			    lastRegimen.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("LastARVDispenseDay", "Last ARV Dose Days", String.class),
			    lastDispenseDay.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("LastAdherence", "Last Adherence", String.class),
			    lastAdherence.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("DateExcluded", "Date Excluded from TX_CURR in E.C", Date.class),
			    rttLineListQuery.getEthiopianDate(lastTreatmentEndDateET));
			
			data.addRow(row);
			
		}
		
		return data;
	}
	
	private String getStringIdentifier(Object patientIdentifier) {
		return Objects.isNull(patientIdentifier) ? "--" : patientIdentifier.toString();
	}
	
}
