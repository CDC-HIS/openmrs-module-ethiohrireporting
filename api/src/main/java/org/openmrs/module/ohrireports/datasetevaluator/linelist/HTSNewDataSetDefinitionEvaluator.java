package org.openmrs.module.ohrireports.datasetevaluator.linelist;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.impl.query.ArtQuery;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.HTSNewDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { HTSNewDataSetDefinition.class })
public class HTSNewDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private ArtQuery artQuery;
	
	private PatientQueryService patientQuery;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		HTSNewDataSetDefinition hdsd = (HTSNewDataSetDefinition) dataSetDefinition;
		
		SimpleDataSet data = new SimpleDataSet(dataSetDefinition, evalContext);
		
		patientQuery = Context.getService(PatientQueryService.class);
		List<Integer> encounters = encounterQuery.getAliveFollowUpEncounters(hdsd.getEndDate());
		Cohort cohort = patientQuery.getNewOnArtCohort("", hdsd.getStartDate(), hdsd.getEndDate(), null, encounters);
		HashMap<Integer, Object> mrnIdentifierHashMap = artQuery.getIdentifier(cohort, MRN_PATIENT_IDENTIFIERS);
		
		List<Person> persons = patientQuery.getPersons(cohort);
		HashMap<Integer, Object> regimentDictionary = artQuery.getRegiment(encounters, cohort);
		
		HashMap<Integer, Object> artStartDictionary = artQuery.getArtStartDate(cohort, hdsd.getStartDate(),
		    hdsd.getEndDate());
		
		DataSetRow row = new DataSetRow();
		if (persons.size() > 0) {
			
			row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class), "TOTAL");
			row.addColumnValue(new DataSetColumn("Name", "Name", Integer.class), persons.size());
			
			data.addRow(row);
		}
		for (Person person : persons) {
			
			row = new DataSetRow();
			Date date = artQuery.getDate(artStartDictionary.get(person.getPersonId()));
			String ethiopianDate = artQuery.getEthiopianDate(date);
			row.addColumnValue(new DataSetColumn("MRN", "MRN", Integer.class),
			    mrnIdentifierHashMap.get(person.getPersonId()));
			
			row.addColumnValue(new DataSetColumn("Name", "Name", String.class), person.getNames());
			row.addColumnValue(new DataSetColumn("Age", "Age", Integer.class), person.getAge(date));
			row.addColumnValue(new DataSetColumn("Gender", "Gender", Integer.class), person.getGender());
			row.addColumnValue(new DataSetColumn("ArtStartDate", "Art Start Date", Date.class), date);
			row.addColumnValue(new DataSetColumn("ArtStartDateEth", "Art Start Date ETH", String.class), ethiopianDate);
			
			row.addColumnValue(new DataSetColumn("Regimen", "Regimen", String.class),
			    regimentDictionary.get(person.getPersonId()));
			
			data.addRow(row);
		}
		
		return data;
	}
	
}
