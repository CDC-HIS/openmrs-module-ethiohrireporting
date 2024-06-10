package org.openmrs.module.ohrireports.datasetevaluator.linelist.retention;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.RetentionLineListDataSetDefinition;
import org.openmrs.module.ohrireports.datasetevaluator.linelist.LineListUtilities;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Handler(supports = { RetentionLineListDataSetDefinition.class })
public class RetentionLineListDatasetEvaluator implements DataSetEvaluator {
	
	@Autowired
	private RetentionLineListQuery retentionLineListQuery;
	
	/**
	 * Evaluate a DataSet for the given EvaluationContext
	 * 
	 * @return the evaluated <code>DataSet</code>
	 */
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		RetentionLineListDataSetDefinition _datasetDefinition = (RetentionLineListDataSetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(_datasetDefinition, evalContext);
		
		// Check start date and end date are valid
		// If start date is greater than end date
		if (_datasetDefinition.getStartDate() != null && _datasetDefinition.getEndDate() != null
		        && _datasetDefinition.getStartDate().compareTo(_datasetDefinition.getEndDate()) > 0) {
			//throw new EvaluationException("Start date cannot be greater than end date");
			DataSetRow row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("Error", "Error", Integer.class),
			    "Report start date cannot be after report end date");
			dataSet.addRow(row);
			return dataSet;
		}
		
		retentionLineListQuery.generateRetentionReport(_datasetDefinition.getStartDate(), _datasetDefinition.getEndDate());
		Cohort cohort = retentionLineListQuery.getBaseCohort();
		List<Person> persons = LineListUtilities.sortPatientByName(retentionLineListQuery.getPerson(cohort));
		
		HashMap<Integer, Object> followUpDateHashMap = retentionLineListQuery.getObsValueDate(
		    retentionLineListQuery.getBaseEncounter(), FOLLOW_UP_DATE, cohort);
		HashMap<Integer, Object> confirmedDateHashMap = retentionLineListQuery.getObsValueDate(
		    retentionLineListQuery.getBaseEncounter(), HIV_CONFIRMED_DATE, cohort);
		HashMap<Integer, Object> artStartDateHashMap = retentionLineListQuery.getObsValueDate(
		    retentionLineListQuery.getBaseEncounter(), ART_START_DATE, cohort);
		HashMap<Integer, Object> nextVistDateHashMap = retentionLineListQuery.getObsValueDate(
		    retentionLineListQuery.getBaseEncounter(), NEXT_VISIT_DATE, cohort);
		HashMap<Integer, Object> treatmentDateHashMap = retentionLineListQuery.getObsValueDate(
		    retentionLineListQuery.getBaseEncounter(), TREATMENT_END_DATE, cohort);
		HashMap<Integer, Object> transferedHashMap = retentionLineListQuery.getConceptName(
		    retentionLineListQuery.getBaseEncounter(), cohort, REASON_FOR_ART_ELIGIBILITY);
		HashMap<Integer, Object> doseDisPenseHashMap = retentionLineListQuery.getConceptName(
		    retentionLineListQuery.getBaseEncounter(), cohort, ARV_DISPENSED_IN_DAYS);
		HashMap<Integer, Object> regimentHashMap = retentionLineListQuery.getRegiment(
		    retentionLineListQuery.getBaseEncounter(), cohort);
		HashMap<Integer, Object> pregnancyStatusHashMap = retentionLineListQuery.getConceptName(
		    retentionLineListQuery.getBaseEncounter(), cohort, PREGNANCY_STATUS);
		HashMap<Integer, Object> adherenceHashMap = retentionLineListQuery.getConceptName(
		    retentionLineListQuery.getBaseEncounter(), cohort, ARV_ADHERENCE);
		HashMap<Integer, Object> followUpStatusHashMap = retentionLineListQuery.getConceptName(
		    retentionLineListQuery.getBaseEncounter(), cohort, FOLLOW_UP_STATUS);
		HashMap<Integer, Object> mrnIdentifierHashMap = retentionLineListQuery
		        .getIdentifier(cohort, MRN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> uanIdentifierHashMap = retentionLineListQuery
		        .getIdentifier(cohort, UAN_PATIENT_IDENTIFIERS);
		//TODO: identify still on treatment  concept
		//		HashMap<Integer, Object> stillOnTreatmentHashMap = retentionLineListQuery.getConceptName(
		//				retentionLineListQuery.getBaseEncounter(), cohort, FOLLOW_UP_STATUS);
		//
		DataSetRow row;
		
		if (!persons.isEmpty()) {
			
			row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("#", "#", Integer.class), "TOTAL");
			row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", Integer.class), persons.size());
			
			dataSet.addRow(row);
		} else {
			dataSet.addRow(LineListUtilities.buildEmptyRow(Arrays.asList("#", "Patient Name", "MRN", "UAN", "Age", "Sex",
			    "HIV Confirmed Date in E.C.", "ART Start Date in E.C.", "TI/TO?", "Latest Follow-up Date in E.C.",
			    "Latest Follow-up status", "Latest Regimen", "Latest ARV Dose Days", "Latest Adherence", "Pregnant",
			    "Next Visit date in E.C.", "Treatment End Date in E.C.")));
		}
		int i = 1;
		for (Person person : persons) {
			
			Date followUpDateDate = retentionLineListQuery.getDate(followUpDateHashMap.get(person.getPersonId()));
			Date confirmedDate = retentionLineListQuery.getDate(confirmedDateHashMap.get(person.getPersonId()));
			Date artStartDate = retentionLineListQuery.getDate(artStartDateHashMap.get(person.getPersonId()));
			Date nextVisitDate = retentionLineListQuery.getDate(nextVistDateHashMap.get(person.getPersonId()));
			Date treatmentDate = retentionLineListQuery.getDate(treatmentDateHashMap.get(person.getPersonId()));
			
			row = new DataSetRow();
			
			row.addColumnValue(new DataSetColumn("#", "#", Integer.class), i++);
			row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", String.class), person.getNames());
			row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class), mrnIdentifierHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("UAN", "UAN", String.class),
			    getStringIdentifier(uanIdentifierHashMap.get(person.getPersonId())));
			row.addColumnValue(new DataSetColumn("Age", "Age", Integer.class),
			    person.getAge(_datasetDefinition.getEndDate()));
			row.addColumnValue(new DataSetColumn("Sex", "Sex", String.class), person.getGender());
			row.addColumnValue(new DataSetColumn("HIV Confirmed Date in E.C.", "HIV Confirmed Date in E.C.", String.class),
			    retentionLineListQuery.getEthiopianDate(confirmedDate));
			row.addColumnValue(new DataSetColumn("ART Start Date in E.C.", "ART Start Date in E.C.", String.class),
			    retentionLineListQuery.getEthiopianDate(artStartDate));
			row.addColumnValue(new DataSetColumn("TI/TO?", "TI/TO?", String.class),
			    transferedHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Latest Follow-up Date in E.C.", "Latest Follow-up Date in E.C.",
			        String.class), retentionLineListQuery.getEthiopianDate(followUpDateDate));
			row.addColumnValue(new DataSetColumn("Latest Follow-up status", "Latest Follow-up status", String.class),
			    followUpStatusHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Latest Regimen", "Latest Regimen", String.class),
			    regimentHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Latest ARV Dose Days", "Latest ARV Dose Days", String.class),
			    doseDisPenseHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Latest Adherence", "Latest Adherence", String.class),
			    adherenceHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Pregnant?", "Pregnant?", String.class),
			    pregnancyStatusHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Next Visit date in E.C.", "Next Visit date in E.C.", String.class),
			    retentionLineListQuery.getEthiopianDate(nextVisitDate));
			row.addColumnValue(new DataSetColumn("Treatment End Date in E.C.", "Treatment End Date in E.C.", String.class),
			    retentionLineListQuery.getEthiopianDate(treatmentDate));
			
			dataSet.addRow(row);
			
		}
		
		return dataSet;
	}
	
	private int getDateDifference(Date confirmedDate, Date startArtDate) {
		if (Objects.isNull(confirmedDate) || Objects.isNull(startArtDate))
			return 0;
		return (int) TimeUnit.DAYS
		        .convert(Math.abs(startArtDate.getTime() - confirmedDate.getTime()), TimeUnit.MILLISECONDS);
	}
	
	private Object getStringIdentifier(Object patientIdentifier) {
		return Objects.isNull(patientIdentifier) ? "--" : patientIdentifier.toString();
	}
}
