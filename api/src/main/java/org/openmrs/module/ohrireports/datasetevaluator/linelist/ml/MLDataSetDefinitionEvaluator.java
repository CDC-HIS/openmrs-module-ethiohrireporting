package org.openmrs.module.ohrireports.datasetevaluator.linelist.ml;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.MLDataSetDefinition;
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

@Handler(supports = { MLDataSetDefinition.class })
public class MLDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private MLQueryLineList mlQueryLineList;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		MLDataSetDefinition _datasetDefinition = (MLDataSetDefinition) dataSetDefinition;
		SimpleDataSet data = new SimpleDataSet(dataSetDefinition, evalContext);
		
		// if startDate is null then return
		if (Objects.isNull(_datasetDefinition.getStartDate())) {
			return data;
		}
		
		// if endDate is null then set today as default
		if (Objects.isNull(_datasetDefinition.getEndDate())) {
			// today
			Date today = new Date(System.currentTimeMillis());
			_datasetDefinition.setEndDate(today);
		}
		
		// if startDate is greater than endDate then return
		if (_datasetDefinition.getStartDate().after(_datasetDefinition.getEndDate())) {
			return data;
		}
		
		// if startDate is greater than today then return
		if (_datasetDefinition.getStartDate().after(new Date())) {
			return data;
		}
		
		// if endDate is greater than today then return
		if (_datasetDefinition.getEndDate().after(new Date())) {
			return data;
		}
		
		Cohort cohort = mlQueryLineList.getMLQuery(_datasetDefinition.getStartDate(), _datasetDefinition.getEndDate());
		
		List<Person> persons = mlQueryLineList.getPerson(cohort);
		
		HashMap<Integer, Object> artStartDateHashMap = mlQueryLineList.getObsValueDate(mlQueryLineList.getBaseEncounter(),
		    ART_START_DATE, cohort);
		HashMap<Integer, Object> nextVisitDateHashMap = mlQueryLineList.getObsValueDate(mlQueryLineList.getBaseEncounter(),
		    NEXT_VISIT_DATE, cohort);
		HashMap<Integer, Object> mrnIdentifierHashMap = mlQueryLineList.getIdentifier(cohort, MRN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> uanIdentifierHashMap = mlQueryLineList.getIdentifier(cohort, UAN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> statusHashMap = mlQueryLineList.getFollowUpStatus(mlQueryLineList.getBaseEncounter(),
		    cohort);
		HashMap<Integer, Object> regimentHashMap = mlQueryLineList.getRegiment(mlQueryLineList.getBaseEncounter(), cohort);
		HashMap<Integer, Object> dispensDayHashMap = mlQueryLineList.getConceptName(mlQueryLineList.getBaseEncounter(),
		    cohort, ARV_DISPENSED_IN_DAYS);
		
		DataSetRow row;
		if (persons.size() > 0) {
			
			row = new DataSetRow();
			
			row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class), "TOTAL");
			row.addColumnValue(new DataSetColumn("Name", "Name", Integer.class), persons.size());
			
			data.addRow(row);
		}
		
		for (Person person : persons) {
			
			Date artStartDate = mlQueryLineList.getDate(artStartDateHashMap.get(person.getPersonId()));
			Date nextVisitDate = mlQueryLineList.getDate(nextVisitDateHashMap.get(person.getPersonId()));
			
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
			    mlQueryLineList.getEthiopianDate(artStartDate));
			row.addColumnValue(new DataSetColumn("nextVisitDate", "Next Followup Date", Date.class), nextVisitDate);
			row.addColumnValue(new DataSetColumn("nextVisitDateETC", "Next Followup Date ETH", String.class),
			    mlQueryLineList.getEthiopianDate(nextVisitDate));
			row.addColumnValue(new DataSetColumn("Regimen", "Regimen", String.class),
			    regimentHashMap.get(person.getPersonId()));
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
