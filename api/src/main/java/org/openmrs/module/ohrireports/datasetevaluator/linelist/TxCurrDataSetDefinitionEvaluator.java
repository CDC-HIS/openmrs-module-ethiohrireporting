package org.openmrs.module.ohrireports.datasetevaluator.linelist;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.MRN_PATIENT_IDENTIFIERS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.OPENMRS_PATIENT_IDENTIFIERS;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
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

@Handler(supports = { TxCurrDataSetDefinition.class })
public class TxCurrDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private ArtQuery artQuery;
	
	private PatientQueryService patientQuery;
	
	private TxCurrDataSetDefinition hdsd;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		hdsd = (TxCurrDataSetDefinition) dataSetDefinition;
		SimpleDataSet data = new SimpleDataSet(dataSetDefinition, evalContext);
		patientQuery = Context.getService(PatientQueryService.class);
		
		Cohort cohort = patientQuery.getActiveOnArtCohort("", hdsd.getStartDate(), hdsd.getEndDate(), null);
		
		List<Person> persons = patientQuery.getPersons(cohort);
		HashMap<Integer, Object> treatmentHashMap = artQuery.getTreatmentEndDates(cohort, hdsd.getEndDate());
		HashMap<Integer, Object> mrnIdentifierHashMap = artQuery.getIdentifier(cohort, MRN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> openMRSIdentifierHashMap = artQuery.getIdentifier(cohort, OPENMRS_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> statusHashMap = artQuery.getFollowUpStatus(cohort, hdsd.getStartDate(), hdsd.getEndDate());
		HashMap<Integer, Object> regimentHashMap = artQuery.getRegiment(cohort, hdsd.getStartDate(), hdsd.getEndDate());
		
		DataSetRow row = new DataSetRow();
		for (Person person : persons) {
			
			// row should be filled with only patient data
			Date treatmentEndDate = artQuery.getDate(treatmentHashMap.get(person.getPersonId()));
			
			row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class),
			    getStringIdentifier(mrnIdentifierHashMap.get(person.getPersonId())));
			
			row.addColumnValue(new DataSetColumn("OpenMRS", "OpenMRS ID", String.class),
			    getStringIdentifier(openMRSIdentifierHashMap.get(person.getPersonId())));
			
			row.addColumnValue(new DataSetColumn("Name", "Name", String.class), person.getNames());
			
			row.addColumnValue(new DataSetColumn("Age", "Age", Integer.class), person.getAge());
			
			row.addColumnValue(new DataSetColumn("Gender", "Gender", String.class), person.getGender());
			
			row.addColumnValue(new DataSetColumn("TreatmentEndDate", "Treatment End Date", Date.class), treatmentEndDate);
			row.addColumnValue(new DataSetColumn("TreatmentEndDateETC", "Treatment End Date ETH", String.class),
			    artQuery.getEthiopianDate(treatmentEndDate));
			row.addColumnValue(new DataSetColumn("Regimen", "Regimen", String.class),
			    regimentHashMap.get(person.getPersonId()));
			
			row.addColumnValue(new DataSetColumn("Status", "Status", String.class), statusHashMap.get(person.getPersonId()));
			data.addRow(row);
			
		}
		
		if (persons.size() > 0) {
			
			row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class), "TOTAL");
			row.addColumnValue(new DataSetColumn("OpenMRS", "OpenMRS ID", Integer.class), persons.size());
			
			data.addRow(row);
		}
		return data;
	}
	
	private String getStringIdentifier(Object patientIdentifier) {
		return Objects.isNull(patientIdentifier) ? "--" : patientIdentifier.toString();
	}
	
}
