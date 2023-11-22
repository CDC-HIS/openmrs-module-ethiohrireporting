package org.openmrs.module.ohrireports.datasetevaluator.linelist.cervicalCancer;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.CervicalCancerQuery;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.CervicalCancerDataSetDefinition;
import org.openmrs.module.ohrireports.datasetevaluator.linelist.cervicalCancer.CervicalCancerLIneListQuery;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Handler(supports = { CervicalCancerDataSetDefinition.class })
public class CervicalCancerDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private CervicalCancerQuery cervicalCancerQuery;
	
	CervicalCancerDataSetDefinition _dataSetDefinition;
	
	@Autowired
	private CervicalCancerLIneListQuery cancerLIneListQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		_dataSetDefinition = (CervicalCancerDataSetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		
		cervicalCancerQuery.setStartDate(_dataSetDefinition.getStartDate());
		cervicalCancerQuery.setEndDate(_dataSetDefinition.getEndDate());
		Cohort baseCohort = cervicalCancerQuery.loadScreenedCohort();
		List<Person> persons = cervicalCancerQuery.getPersons(baseCohort);
		
		HashMap<Integer, Object> mrnIdentifierHashMap = cancerLIneListQuery.getIdentifier(baseCohort,
		    MRN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> uanIdentifierHashMap = cancerLIneListQuery.getIdentifier(baseCohort,
		    UAN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> followUpDate = cancerLIneListQuery.getObsValueDate(cervicalCancerQuery.getBaseEncounter(),
		    FOLLOW_UP_DATE, baseCohort);
		HashMap<Integer, Object> counselledDate = cancerLIneListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), DATE_COUNSELING_GIVEN, baseCohort);
		HashMap<Integer, Object> screeningReceivedDate = cancerLIneListQuery.getObsValueDate(
		    cervicalCancerQuery.getBaseEncounter(), CXC_SCREENING_DATE, baseCohort);
		HashMap<Integer, Object> artStartDate = cancerLIneListQuery.getArtStartDate(baseCohort,
		    _dataSetDefinition.getStartDate(), _dataSetDefinition.getEndDate());
		HashMap<Integer, Object> followUpStatus = cancerLIneListQuery.getFollowUpStatus(
		    cervicalCancerQuery.getCurrentEncounter(), baseCohort);
		HashMap<Integer, Object> regimentHashMap = cancerLIneListQuery.getRegiment(
		    cervicalCancerQuery.getCurrentEncounter(), baseCohort);
		HashMap<Integer, Object> adherence = cancerLIneListQuery.getByResult(ARV_ADHERENCE, baseCohort,
		    cervicalCancerQuery.getCurrentEncounter());
		HashMap<Integer, Object> screeningType = cancerLIneListQuery.getByResult(CXCA_TYPE_OF_SCREENING, baseCohort,
		    cervicalCancerQuery.getBaseEncounter());
		HashMap<Integer, Object> screeningMechanism = cancerLIneListQuery.getByResult(SCREENING_STRATEGY, baseCohort,
		    cervicalCancerQuery.getBaseEncounter());
		HashMap<Integer, Object> hpvScreeningResult = cancerLIneListQuery.getByResult(HPV_DNA_SCREENING_RESULT, baseCohort,
		    cervicalCancerQuery.getBaseEncounter());
		HashMap<Integer, Object> viaScreeningResult = cancerLIneListQuery.getByResult(VIA_SCREENING_RESULT, baseCohort,
		    cervicalCancerQuery.getBaseEncounter());
		HashMap<Integer, Object> cxcaTreatmentReceived = cancerLIneListQuery.getByResult(CXCA_TREATMENT_TYPE, baseCohort,
		    cervicalCancerQuery.getBaseEncounter());
		
		DataSetRow row;
		for (Person person : persons) {
			row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("Name", "Name", String.class), person.getNames());
			row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class), uanIdentifierHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("UAN", "UAN", String.class), mrnIdentifierHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Age", "Age", String.class), person.getAge(_dataSetDefinition.getEndDate()));
			row.addColumnValue(new DataSetColumn("followUp", "Follow-Up Date", String.class),
			    followUpDate.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("followUpStatus", "Follow-Up Status", String.class),
			    followUpStatus.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("artStartDate", "Art Start Date", String.class),
			    artStartDate.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("regiment", "Regiment", String.class),
			    regimentHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("adherence", "Adherence", String.class),
			    adherence.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("ScreeningVisitType", "Screening Visit Type", String.class),
			    screeningType.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("counseledGiven", "Date Counseled For CCA", String.class),
			    counselledDate.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("dateScreeningReceived", "Date Screening Received", String.class),
			    screeningReceivedDate.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("screeningMechanism", "Screening Mechanism", String.class),
			    screeningMechanism.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("hpvScreeningResult", "HPV Screening Result", String.class),
			    hpvScreeningResult.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("viaScreeningResult", "VIA Screening Result", String.class),
			    viaScreeningResult.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("cxcaTreatmentResult", "Type Of Treatment Received", String.class),
			    cxcaTreatmentReceived.get(person.getPersonId()));
			
			dataSet.addRow(row);
		}
		
		return dataSet;
	}
}
