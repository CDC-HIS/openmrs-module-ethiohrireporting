package org.openmrs.module.ohrireports.reports.datasetevaluator;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.openmrs.module.ohrireports.reports.datasetdefinition.HtsNewDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { HtsNewDataSetDefinition.class })
public class HTsNewDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private PatientService patientService;
	
	@Autowired
	private ArtQuery artQuery;
	
	private PatientQueryService patientQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		HtsNewDataSetDefinition hdsd = (HtsNewDataSetDefinition) dataSetDefinition;
		
		SimpleDataSet data = new SimpleDataSet(dataSetDefinition, evalContext);
		
		patientQuery = Context.getService(PatientQueryService.class);
		
		Cohort cohort = patientQuery.getOnArtCohorts("", hdsd.getStartDate(), hdsd.getEndDate(), null);
		
		PatientIdentifierType mrnIdentifierType = patientService.getPatientIdentifierTypeByUuid(MRN_PATIENT_IDENTIFIERS);
		PatientIdentifierType openmrsIdentifierType = patientService
		        .getPatientIdentifierTypeByUuid(OPENMRS_PATIENT_IDENTIFIERS);
		
		List<Person> persons = patientQuery.getPersons(cohort);
		HashMap<Integer, Object> regimentDictionary = artQuery.getRegiment(cohort, hdsd.getStartDate(), hdsd.getEndDate());
		
		HashMap<Integer, Object> artStartDictionary = artQuery.getArtStartDate(cohort, hdsd.getStartDate(),
		    hdsd.getEndDate());
		
		DataSetRow row = new DataSetRow();
		for (Person person : persons) {
			
			row = new DataSetRow();
			Date date = artQuery.getDate(artStartDictionary.get(person.getPersonId()));
			String ethiopianDate = artQuery.getEthiopianDate(date);
			Patient patient = patientService.getPatient(person.getPersonId());
			row.addColumnValue(new DataSetColumn("MRN", "MRN", Integer.class),
			    patient.getPatientIdentifier(mrnIdentifierType));
			row.addColumnValue(new DataSetColumn("OpenMRS-ID", "OpenMRS ID", Integer.class),
			    patient.getPatientIdentifier(openmrsIdentifierType));
			
			row.addColumnValue(new DataSetColumn("Name", "Name", String.class), person.getNames());
			row.addColumnValue(new DataSetColumn("Age", "Age", Integer.class), person.getAge(date));
			row.addColumnValue(new DataSetColumn("Gender", "Gender", Integer.class), person.getGender());
			row.addColumnValue(new DataSetColumn("ArtStartDate", "Art Start Date", Date.class), date);
			row.addColumnValue(new DataSetColumn("ArtStartDateEth", "Art Start Date ETH", String.class), ethiopianDate);
			
			row.addColumnValue(new DataSetColumn("Regimen", "Regimen", String.class),
			    regimentDictionary.get(person.getPersonId()));
			
			data.addRow(row);
		}
		
		if (persons.size() > 0) {
			
			row = new DataSetRow();
			
			row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class), "TOTAL");
			row.addColumnValue(new DataSetColumn("OpenMRS-ID", "OpenMRS ID", Integer.class), persons.size());
			
			data.addRow(row);
		}
		
		return data;
	}
	
}
