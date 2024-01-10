package org.openmrs.module.ohrireports.datasetevaluator.linelist.rtt;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.RTTDataSetDefinition;
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
import java.util.Objects;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Handler(supports = { RTTDataSetDefinition.class })
public class RTTDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private RTTLineListQuery rttLineListQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		RTTDataSetDefinition _datasetDefinition = (RTTDataSetDefinition) dataSetDefinition;
		SimpleDataSet data = new SimpleDataSet(dataSetDefinition, evalContext);
		
		Cohort cohort = rttLineListQuery.getRTTCohort(_datasetDefinition.getStartDate(), _datasetDefinition.getEndDate());
		
		List<Person> persons = rttLineListQuery.getPerson(cohort);
		
		HashMap<Integer, Object> artStartDateHashMap = rttLineListQuery.getObsValueDate(rttLineListQuery.getBaseEncounter(),
		    ART_START_DATE, cohort);
		HashMap<Integer, Object> returnedFollowUpDate = rttLineListQuery.getObsValueDate(
		    rttLineListQuery.getBaseEncounter(), FOLLOW_UP_DATE, cohort);
		
		HashMap<Integer, Object> weightDateHashMap = rttLineListQuery.getByResult(WEIGHT, cohort,
		    rttLineListQuery.getBaseEncounter());
		HashMap<Integer, Object> mrnIdentifierHashMap = rttLineListQuery.getIdentifier(cohort, MRN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> uanIdentifierHashMap = rttLineListQuery.getIdentifier(cohort, UAN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> statusHashMap = rttLineListQuery.getFollowUpStatus(rttLineListQuery.getBaseEncounter(),
		    cohort);
		HashMap<Integer, Object> regimentHashMap = rttLineListQuery.getRegiment(rttLineListQuery.getBaseEncounter(), cohort);
		HashMap<Integer, Object> dispensDayHashMap = rttLineListQuery.getConceptName(rttLineListQuery.getBaseEncounter(),
		    cohort, ARV_DISPENSED_IN_DAYS);
		
		DataSetRow row;
		if (persons.size() > 0) {
			row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class), "TOTAL");
			row.addColumnValue(new DataSetColumn("Name", "Name", Integer.class), persons.size());
			data.addRow(row);
		}
		
		for (Person person : persons) {
			
			Date artStartDate = rttLineListQuery.getDate(artStartDateHashMap.get(person.getPersonId()));
			Date treatmentEndDate = rttLineListQuery.getDate(returnedFollowUpDate.get(person.getPersonId()));
			
			row = new DataSetRow();
			
			row.addColumnValue(new DataSetColumn("Name", "Name", String.class), person.getNames());
			row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class),
			    getStringIdentifier(mrnIdentifierHashMap.get(person.getPersonId())));
			row.addColumnValue(new DataSetColumn("UANO", "UANO", String.class),
			    getStringIdentifier(uanIdentifierHashMap.get(person.getPersonId())));
			row.addColumnValue(new DataSetColumn("Age", "Age", Integer.class),
			    person.getAge(_datasetDefinition.getEndDate()));
			row.addColumnValue(new DataSetColumn("Gender", "Gender", String.class), person.getGender());
			row.addColumnValue(new DataSetColumn("ArtStartDate", "ART start  Date", Date.class), artStartDate);
			row.addColumnValue(new DataSetColumn("ArtStartDateETC", "ART star  Date ETH", String.class),
			    rttLineListQuery.getEthiopianDate(artStartDate));
			row.addColumnValue(new DataSetColumn("dateReturnedTreatment", "Date Returned Treatment", Date.class),
			    treatmentEndDate);
			row.addColumnValue(new DataSetColumn("dateReturnedTreatmentETC", "Date ETH Returned Treatment", String.class),
			    rttLineListQuery.getEthiopianDate(treatmentEndDate));
			row.addColumnValue(new DataSetColumn("Regimen", "Regimen", String.class),
			    regimentHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Weight", "Weight", String.class),
			    weightDateHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("arvDispensDay", "ARV Dispense Day", String.class),
			    dispensDayHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Status", "Status", String.class), statusHashMap.get(person.getPersonId()));
			data.addRow(row);
			
		}
		
		return data;
	}
	
	private String getStringIdentifier(Object patientIdentifier) {
		return Objects.isNull(patientIdentifier) ? "--" : patientIdentifier.toString();
	}
	
}
