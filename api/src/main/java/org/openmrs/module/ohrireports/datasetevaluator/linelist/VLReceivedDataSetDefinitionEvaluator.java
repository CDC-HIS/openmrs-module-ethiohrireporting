package org.openmrs.module.ohrireports.datasetevaluator.linelist;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.MRN_PATIENT_IDENTIFIERS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.DATE_VIRAL_TEST_RESULT_RECEIVED;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.impl.query.VlQuery;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.VLReceivedDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { VLReceivedDataSetDefinition.class })
public class VLReceivedDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private VlQuery vlQuery;
	
	private PatientQueryService patientQueryService;
	
	private List<Integer> baseEncounters;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		VLReceivedDataSetDefinition _DataSetDefinition = (VLReceivedDataSetDefinition) dataSetDefinition;
		
		SimpleDataSet data = new SimpleDataSet(dataSetDefinition, evalContext);
		patientQueryService = Context.getService(PatientQueryService.class);
		
		baseEncounters = patientQueryService.getBaseEncounters(DATE_VIRAL_TEST_RESULT_RECEIVED,
		    _DataSetDefinition.getStartDate(), _DataSetDefinition.getEndDate());
		vlQuery.loadInitialCohort(_DataSetDefinition.getStartDate(), _DataSetDefinition.getEndDate(), baseEncounters);
		
		List<Person> persons = patientQueryService.getPersons(vlQuery.cohort);
		
		HashMap<Integer, Object> regimentDictionary = vlQuery.getRegiment(vlQuery.getVlTakenEncounters(), vlQuery.cohort);
		HashMap<Integer, Object> mrnIdentifierHashMap = vlQuery.getIdentifier(vlQuery.cohort, MRN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> artStartDictionary = vlQuery.getArtStartDate(vlQuery.cohort, null,
		    _DataSetDefinition.getEndDate());
		HashMap<Integer, Object> viralLoadPerformedDate = vlQuery.getViralLoadPerformDate();
		HashMap<Integer, Object> routingLoadPerformed = vlQuery.getRoutineViralLoad();
		HashMap<Integer, Object> targetLoadPerformed = vlQuery.getTargetViralLoad();
		HashMap<Integer, Object> viralLoadStatus = vlQuery.getStatusViralLoad();
		HashMap<Integer, Object> pregnantStatus = vlQuery.getPregnantStatus();
		HashMap<Integer, Object> breastFeedingStatus = vlQuery.getBreastFeedingStatus();
		HashMap<Integer, Object> dispensedDose = vlQuery.getArtDose();
		
		HashMap<Integer, Object> followUpStatusDictionary = vlQuery.getFollowUpStatus(vlQuery.getVlTakenEncounters(),
		    vlQuery.cohort);
		HashMap<Integer, Object> viralLoadCount = vlQuery.getViralLoadCount();
		
		DataSetRow row = new DataSetRow();
		if (persons.size() > 0) {
			
			row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class), "TOTAL");
			row.addColumnValue(new DataSetColumn("Name", "Name", Integer.class), persons.size());
			
			data.addRow(row);
		}
		for (Person person : persons) {
			
			row = new DataSetRow();
			Date artDate = vlQuery.getDate(artStartDictionary.get(person.getPersonId()));
			Date _viralLoadPerformedDate = vlQuery.getDate(viralLoadPerformedDate.get(person.getPersonId()));
			
			row.addColumnValue(new DataSetColumn("MRN", "MRN", Integer.class),
			    mrnIdentifierHashMap.get(person.getPersonId()));
			
			row.addColumnValue(new DataSetColumn("Name", "Name", String.class), person.getNames());
			row.addColumnValue(new DataSetColumn("Age", "Age", Integer.class), person.getAge(artDate));
			row.addColumnValue(new DataSetColumn("Gender", "Gender", Integer.class), person.getGender());
			
			getDateRow(row, _viralLoadPerformedDate, "viral_load_perform_date");
			
			row.addColumnValue(new DataSetColumn("routine_viral_load", "routine_viral_load", String.class),
			    routingLoadPerformed.get(person.getPersonId()));
			
			row.addColumnValue(new DataSetColumn("target_viral_load", "target_viral_load", String.class),
			    targetLoadPerformed.get(person.getPersonId()));
			
			row.addColumnValue(new DataSetColumn("viral_load_count", "viral_load_count", Integer.class),
			    viralLoadCount.get(person.getPersonId()));
			
			row.addColumnValue(new DataSetColumn("viral_load_status", "viral_load_status", String.class),
			    viralLoadStatus.get(person.getPersonId()));
			
			getDateRow(row, artDate, "art_start_date");
			
			row.addColumnValue(new DataSetColumn("ArtDose", "art_dose", String.class),
			    dispensedDose.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Pregnant", "Pregnant", String.class),
			    pregnantStatus.get(person.getPersonId()));
			
			row.addColumnValue(new DataSetColumn("BreastFeeding", "BreastFeeding", String.class),
			    breastFeedingStatus.get(person.getPersonId()));
			
			row.addColumnValue(new DataSetColumn("follow_up_status", "follow_up_status", String.class),
			    followUpStatusDictionary.get(person.getPersonId()));
			
			row.addColumnValue(new DataSetColumn("Regimen", "Regimen", String.class),
			    regimentDictionary.get(person.getPersonId()));
			
			data.addRow(row);
		}
		
		return data;
		
	}
	
	private void getDateRow(DataSetRow row, Date artDate, String labelName) {
		String artEthiopianDate = vlQuery.getEthiopianDate(artDate);
		row.addColumnValue(new DataSetColumn(labelName, labelName, Date.class), artDate);
		row.addColumnValue(new DataSetColumn(labelName + "-ETH", labelName + " ETH", String.class), artEthiopianDate);
	}
	
}
