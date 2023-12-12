package org.openmrs.module.ohrireports.datasetevaluator.linelist;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.impl.query.ArtQuery;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.TxCurrDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Handler(supports = { TxCurrDataSetDefinition.class })
public class TxCurrDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private ArtQuery artQuery;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		TxCurrDataSetDefinition hdsd = (TxCurrDataSetDefinition) dataSetDefinition;
		SimpleDataSet data = new SimpleDataSet(dataSetDefinition, evalContext);
		PatientQueryService patientQuery = Context.getService(PatientQueryService.class);
		List<Integer> latestEncounters = encounterQuery.getAliveFollowUpEncounters(null, hdsd.getEndDate());
		Cohort cohort = patientQuery.getActiveOnArtCohort("", null, hdsd.getEndDate(), null, latestEncounters);
		
		List<Person> persons = patientQuery.getPersons(cohort);
		HashMap<Integer, Object> treatmentHashMap = artQuery.getTreatmentEndDates(hdsd.getEndDate(), latestEncounters);
		HashMap<Integer, Object> mrnIdentifierHashMap = artQuery.getIdentifier(cohort, MRN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> statusHashMap = artQuery.getFollowUpStatus(latestEncounters, cohort);
		HashMap<Integer, Object> regimentHashMap = artQuery.getRegiment(latestEncounters, cohort);
		HashMap<Integer, Object> dispensDayHashMap = artQuery
		        .getConceptName(latestEncounters, cohort, ARV_DISPENSED_IN_DAYS);
		DataSetRow row = new DataSetRow();
		
		if (!persons.isEmpty()) {
			
			row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class), "TOTAL");
			row.addColumnValue(new DataSetColumn("Name", "Name", Integer.class), persons.size());
			
			data.addRow(row);
		}
		
		for (Person person : persons) {
			
			// row should be filled with only patient data
			Date treatmentEndDate = artQuery.getDate(treatmentHashMap.get(person.getPersonId()));
			
			row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class),
			    getStringIdentifier(mrnIdentifierHashMap.get(person.getPersonId())));
			
			row.addColumnValue(new DataSetColumn("Name", "Name", String.class), person.getNames());
			
			row.addColumnValue(new DataSetColumn("Age", "Age", Integer.class), person.getAge(hdsd.getEndDate()));
			
			row.addColumnValue(new DataSetColumn("Gender", "Gender", String.class), person.getGender());
			
			row.addColumnValue(new DataSetColumn("TreatmentEndDate", "Treatment End Date", Date.class), treatmentEndDate);
			
			row.addColumnValue(new DataSetColumn("TreatmentEndDateETC", "Treatment End Date ETH", String.class),
			    artQuery.getEthiopianDate(treatmentEndDate));
			
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
