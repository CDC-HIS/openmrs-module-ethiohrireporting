package org.openmrs.module.ohrireports.datasetevaluator.linelist.scheduleVisit;

import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.ScheduleVisitDatasetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TB_SCREENED_RESULT;

@Handler(supports = { ScheduleVisitDatasetDefinition.class })
public class ScheduleVisitDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private ScheduleVisitQuery scheduleVisitQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		ScheduleVisitDatasetDefinition dsd = (ScheduleVisitDatasetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(dsd, evalContext);
		DataSetRow dataSetRow = new DataSetRow();
		scheduleVisitQuery.generateReport(dsd.getStartDate(), dsd.getEndDate());
		HashMap<Integer, Object> mrnIdentifierHashMap = scheduleVisitQuery.getIdentifier(scheduleVisitQuery.getBaseCohort(),
		    MRN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> uaIdentifierHashMap = scheduleVisitQuery.getIdentifier(scheduleVisitQuery.getBaseCohort(),
		    UAN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> artStartDictionary = scheduleVisitQuery.getArtStartDate(scheduleVisitQuery.getBaseCohort(),
		    null, dsd.getEndDate());
		HashMap<Integer, Object> regimentDictionary = scheduleVisitQuery.getRegiment(scheduleVisitQuery.getEncounter(),
		    scheduleVisitQuery.getBaseCohort());
		HashMap<Integer, Object> followUpDate = scheduleVisitQuery.getObsValueDate(scheduleVisitQuery.getEncounter(),
		    FOLLOW_UP_DATE, scheduleVisitQuery.getBaseCohort());
		HashMap<Integer, Object> followUpStatus = scheduleVisitQuery.getFollowUpStatus(scheduleVisitQuery.getEncounter(),
		    scheduleVisitQuery.getBaseCohort());
		HashMap<Integer, Object> weight = scheduleVisitQuery.getObNumericValue(WEIGHT);
		HashMap<Integer, Object> dose = scheduleVisitQuery.getByResult(ARV_DISPENSED_IN_DAYS,
		    scheduleVisitQuery.getBaseCohort(), scheduleVisitQuery.getEncounter());
		HashMap<Integer, Object> adherenceHashMap = scheduleVisitQuery.getByResult(ARV_ADHERENCE,
		    scheduleVisitQuery.getBaseCohort(), scheduleVisitQuery.getEncounter());
		HashMap<Integer, Object> nextVisitDate = scheduleVisitQuery.getObsValueDate(scheduleVisitQuery.getEncounter(),
		    NEXT_VISIT_DATE, scheduleVisitQuery.getBaseCohort());
		HashMap<Integer, Object> tbScreening = scheduleVisitQuery.getByResult(TB_SCREENED,
		    scheduleVisitQuery.getBaseCohort(), scheduleVisitQuery.getEncounter());
		HashMap<Integer, Object> tbScreeningResult = scheduleVisitQuery.getByResult(TB_SCREENED_RESULT,
		    scheduleVisitQuery.getBaseCohort(), scheduleVisitQuery.getEncounter());
		
		DataSetRow row = new DataSetRow();
		List<Person> personList = scheduleVisitQuery.getPersons(scheduleVisitQuery.getBaseCohort());
		
		row.addColumnValue(new DataSetColumn("Appointment Date", "Appointment Date", String.class), "Total");
		row.addColumnValue(new DataSetColumn("Appointment Date ETH", "Appointment Date ETH", String.class),
		    personList.size());
		dataSet.addRow(row);
		
		for (Person person : personList) {
			row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("Appointment Date", "Appointment Date", String.class),
			    nextVisitDate.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Appointment Date ETH", "Appointment Date ETH", String.class),
			    scheduleVisitQuery.getEthiopianDate((Date) nextVisitDate.get(person.getPersonId())));
			
			row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", String.class), person.getNames());
			row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class), mrnIdentifierHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("UAN", "UAN", String.class), uaIdentifierHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Age", "Age", String.class), person.getAge());
			row.addColumnValue(new DataSetColumn("Sex", "Sex", String.class), person.getGender());
			row.addColumnValue(new DataSetColumn("ART Start Date", "ART Start Date", String.class),
			    artStartDictionary.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("ART Start Date ETH", "ART Start Date ETH", String.class),
			    scheduleVisitQuery.getEthiopianDate((Date) artStartDictionary.get(person.getPersonId())));
			
			row.addColumnValue(new DataSetColumn("Last Follow-up Date", "Last Follow-up Date", String.class),
			    followUpDate.get(person.getPersonId()));
			
			row.addColumnValue(new DataSetColumn("Last Follow-up Date ETH", "Last Follow-up Date ETH", String.class),
			    scheduleVisitQuery.getEthiopianDate((Date) followUpDate.get(person.getPersonId())));
			row.addColumnValue(new DataSetColumn("Last Follow-up Status", "Last Follow-up Status", String.class),
			    followUpStatus.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Last Regimen", "Last Regimen", String.class),
			    regimentDictionary.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Last ARV Dose", "Last ARV Dose", String.class),
			    dose.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Adherence", "Adherence", String.class),
			    adherenceHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Weight", "Weight", String.class), weight.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Mobile", "Mobile", String.class), getPhone(person.getActiveAttributes()));
			
			dataSet.addRow(row);
		}
		
		return dataSet;
	}
	
	private String getPhone(List<PersonAttribute> activeAttributes) {
		for (PersonAttribute personAttribute : activeAttributes) {
			if (personAttribute.getValue().startsWith("09") || personAttribute.getValue().startsWith("+251")) {
				return personAttribute.getValue();
			}
		}
		return "";
	}
}
