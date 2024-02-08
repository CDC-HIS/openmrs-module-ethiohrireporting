package org.openmrs.module.ohrireports.datasetevaluator.linelist.artPatient;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.ARTPatientListQuery;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.ARTPatientListDatasetDefinition;
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

@Handler(supports = { ARTPatientListDatasetDefinition.class })
public class ARTPatientListDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private ARTPatientListQuery artPatientListQuery;
	
	@Autowired
	private ARTPatientListLineListQuery artPatientListLineListQuery;
	
	private ARTPatientListDatasetDefinition _dataSetDefinition;
	
	private HashMap<Integer, Object> mrnIdentifierHashMap, uanIdentifierHashMap, regimenHashMap, followUpStatus,
	        artStartDictionary, adherenceHashMap, scheduleTypeHashMap, appointmentDateDictionary;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		_dataSetDefinition = (ARTPatientListDatasetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		
		artPatientListQuery.setStartDate(_dataSetDefinition.getStartDate());
		artPatientListQuery.setEndDate(_dataSetDefinition.getEndDate());
		
		Cohort baseCohort = artPatientListQuery.getArtStartedCohort(null, artPatientListQuery.getEndDate());
		List<Person> persons = artPatientListQuery.getPersons(baseCohort);
		
		loadColumnDictionary(baseCohort);
		
		DataSetRow row;
		
		if (persons.size() > 0) {
			
			row = new DataSetRow();
			
			row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class), "TOTAL");
			row.addColumnValue(new DataSetColumn("Name", "Name", Integer.class), persons.size());
			
			dataSet.addRow(row);
		}
		
		for (Person person : persons) {
			
			Date artStartDate = artPatientListLineListQuery.getDate(artStartDictionary.get(person.getPersonId()));
			Date appointmentDate = artPatientListLineListQuery.getDate(appointmentDateDictionary.get(person.getPersonId()));
			
			row = new DataSetRow();
			
			row.addColumnValue(new DataSetColumn("Name", "Patient Name", String.class), person.getNames());
			addColumnValue("MRN", "MRN", mrnIdentifierHashMap, row, person);
			addColumnValue("UAN", "UAN", uanIdentifierHashMap, row, person);
			row.addColumnValue(new DataSetColumn("Age", "Age", String.class), person.getAge(_dataSetDefinition.getEndDate()));
			row.addColumnValue(new DataSetColumn("Gender", "Sex", Integer.class), person.getGender());
			addColumnValue("regimen", "Regimen", regimenHashMap, row, person);
			addColumnValue("followUpStatus", "FollowUp Status", followUpStatus, row, person);
			row.addColumnValue(new DataSetColumn("ArtStartDateETH", "Art Start Date ETH", Date.class),
			    artPatientListLineListQuery.getEthiopianDate(artStartDate));
			addColumnValue("adherence", "Adherence", adherenceHashMap, row, person);
			addColumnValue("scheduleType", "Schedule Type", scheduleTypeHashMap, row, person);
			row.addColumnValue(new DataSetColumn("appointmentDate", "Appointment Date ETH", Date.class),
			    artPatientListLineListQuery.getEthiopianDate(appointmentDate));
			
			dataSet.addRow(row);
		}
		
		return dataSet;
	}
	
	private void loadColumnDictionary(Cohort baseCohort) {
		mrnIdentifierHashMap = artPatientListLineListQuery.getIdentifier(baseCohort, MRN_PATIENT_IDENTIFIERS);
		uanIdentifierHashMap = artPatientListLineListQuery.getIdentifier(baseCohort, UAN_PATIENT_IDENTIFIERS);
		regimenHashMap = artPatientListLineListQuery.getRegiment(artPatientListQuery.getBaseEncounter(), baseCohort);
		followUpStatus = artPatientListLineListQuery.getFollowUpStatus(artPatientListQuery.getBaseEncounter(), baseCohort);
		adherenceHashMap = artPatientListLineListQuery.getConceptName(artPatientListQuery.getBaseEncounter(), baseCohort,
		    ARV_ADHERENCE);
		scheduleTypeHashMap = artPatientListLineListQuery.getConceptName(artPatientListQuery.getBaseEncounter(), baseCohort,
		    SCHEDULE_TYPE);
		artStartDictionary = artPatientListLineListQuery.getArtStartDate(baseCohort, null, artPatientListQuery.getEndDate());
		appointmentDateDictionary = artPatientListLineListQuery.getObsValueDate(artPatientListQuery.getBaseEncounter(),
		    ART_START_DATE, baseCohort);
	}
	
	private void addColumnValue(String name, String label, HashMap<Integer, Object> object, DataSetRow row, Person person) {
		row.addColumnValue(new DataSetColumn(name, label, String.class), object.get(person.getPersonId()));
	}
}
