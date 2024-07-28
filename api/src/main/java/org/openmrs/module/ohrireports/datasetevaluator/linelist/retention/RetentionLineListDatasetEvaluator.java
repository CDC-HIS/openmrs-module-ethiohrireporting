package org.openmrs.module.ohrireports.datasetevaluator.linelist.retention;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.openmrs.module.ohrireports.constants.Identifiers;
import org.openmrs.module.ohrireports.constants.PositiveCaseTrackingConceptQuestions;
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
		RetentionLineListDataSetDefinition linkageDataset = (RetentionLineListDataSetDefinition) dataSetDefinition;
		SimpleDataSet data = new SimpleDataSet(linkageDataset, evalContext);
		
		retentionLineListQuery.generateRetentionReport(linkageDataset.getStartDate(), linkageDataset.getEndDate());
		Cohort cohort = retentionLineListQuery.getBaseCohort();
		List<Person> persons = LineListUtilities.sortPatientByName(retentionLineListQuery.getPerson(cohort));
		
		HashMap<Integer, Object> followUpDateHashMap = retentionLineListQuery.getObsValueDate(
		    retentionLineListQuery.getBaseEncounter(), FollowUpConceptQuestions.FOLLOW_UP_DATE, cohort);
		HashMap<Integer, Object> confirmedDateHashMap = retentionLineListQuery.getObsValueDate(
		    retentionLineListQuery.getBaseEncounter(), PositiveCaseTrackingConceptQuestions.HIV_CONFIRMED_DATE, cohort);
		HashMap<Integer, Object> artStartDateHashMap = retentionLineListQuery.getObsValueDate(
		    retentionLineListQuery.getBaseEncounter(), FollowUpConceptQuestions.ART_START_DATE, cohort);
		HashMap<Integer, Object> nextVistDateHashMap = retentionLineListQuery.getObsValueDate(
		    retentionLineListQuery.getBaseEncounter(), FollowUpConceptQuestions.NEXT_VISIT_DATE, cohort);
		HashMap<Integer, Object> treatmentDateHashMap = retentionLineListQuery.getObsValueDate(
		    retentionLineListQuery.getBaseEncounter(), FollowUpConceptQuestions.TREATMENT_END_DATE, cohort);
		HashMap<Integer, Object> transferedHashMap = retentionLineListQuery.getConceptName(
		    retentionLineListQuery.getBaseEncounter(), cohort, FollowUpConceptQuestions.REASON_FOR_ART_ELIGIBILITY);
		HashMap<Integer, Object> doseDisPenseHashMap = retentionLineListQuery.getConceptName(
		    retentionLineListQuery.getBaseEncounter(), cohort, FollowUpConceptQuestions.ARV_DISPENSED_IN_DAYS);
		HashMap<Integer, Object> regimentHashMap = retentionLineListQuery.getRegiment(
		    retentionLineListQuery.getBaseEncounter(), cohort);
		HashMap<Integer, Object> adherenceHashMap = retentionLineListQuery.getConceptName(
		    retentionLineListQuery.getBaseEncounter(), cohort, FollowUpConceptQuestions.ARV_ADHERENCE);
		HashMap<Integer, Object> followUpStatusHashMap = retentionLineListQuery.getConceptName(
		    retentionLineListQuery.getBaseEncounter(), cohort, FollowUpConceptQuestions.FOLLOW_UP_STATUS);
		HashMap<Integer, Object> mrnIdentifierHashMap = retentionLineListQuery.getIdentifier(cohort,
		    Identifiers.MRN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> uanIdentifierHashMap = retentionLineListQuery.getIdentifier(cohort,
		    Identifiers.UAN_PATIENT_IDENTIFIERS);
		//TODO: identify still on treatment  concept
		//		HashMap<Integer, Object> stillOnTreatmentHashMap = retentionLineListQuery.getConceptName(
		//				retentionLineListQuery.getBaseEncounter(), cohort, FOLLOW_UP_STATUS);
		//
		DataSetRow row;
		if (!persons.isEmpty()) {
			
			row = new DataSetRow();
			
			row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class), "TOTAL");
			row.addColumnValue(new DataSetColumn("Name", "Name", Integer.class), persons.size());
			
			data.addRow(row);
		}
		
		for (Person person : persons) {
			
			Date followUpDateDate = retentionLineListQuery.getDate(followUpDateHashMap.get(person.getPersonId()));
			Date confirmedDate = retentionLineListQuery.getDate(confirmedDateHashMap.get(person.getPersonId()));
			Date artStartDate = retentionLineListQuery.getDate(artStartDateHashMap.get(person.getPersonId()));
			Date nextVisitDate = retentionLineListQuery.getDate(nextVistDateHashMap.get(person.getPersonId()));
			Date treatmentDate = retentionLineListQuery.getDate(treatmentDateHashMap.get(person.getPersonId()));
			
			row = new DataSetRow();
			
			row.addColumnValue(new DataSetColumn("Name", "Name", String.class), person.getNames());
			row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class),
			    getStringIdentifier(mrnIdentifierHashMap.get(person.getPersonId())));
			row.addColumnValue(new DataSetColumn("UANO", "UANO", String.class),
			    getStringIdentifier(uanIdentifierHashMap.get(person.getPersonId())));
			row.addColumnValue(new DataSetColumn("Age", "Age", Integer.class), person.getAge(linkageDataset.getEndDate()));
			row.addColumnValue(new DataSetColumn("Gender", "Gender", String.class), person.getGender());
			
			row.addColumnValue(new DataSetColumn("HIVConfirmedDate", "HIV Confirmed Date", Date.class), confirmedDate);
			row.addColumnValue(new DataSetColumn("HIVConfirmedDateETC", "HIV Confirmed  Date ETH", String.class),
			    retentionLineListQuery.getEthiopianDate(confirmedDate));
			
			row.addColumnValue(new DataSetColumn("artStartDate", " ART Start Date ", Date.class), artStartDate);
			row.addColumnValue(new DataSetColumn("artStartDateETC", "ART Start  Date ETH", String.class),
			    retentionLineListQuery.getEthiopianDate(artStartDate));
			
			row.addColumnValue(new DataSetColumn("transferIn", "TI?", String.class),
			    transferedHashMap.get(person.getPersonId()));
			
			row.addColumnValue(new DataSetColumn("followUpDateDate", "Follow-up Date", Date.class), followUpDateDate);
			row.addColumnValue(new DataSetColumn("follow-upDateETC", "Follow-up  Date ETH", String.class),
			    retentionLineListQuery.getEthiopianDate(followUpDateDate));
			
			row.addColumnValue(new DataSetColumn("follow-up-status", "Follow-up status", String.class),
			    followUpStatusHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("regimen", "Regimen", String.class),
			    regimentHashMap.get(person.getPersonId()));
			
			row.addColumnValue(new DataSetColumn("dose", "Dose", String.class),
			    doseDisPenseHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("adherence", "Adherence", String.class),
			    adherenceHashMap.get(person.getPersonId()));
			
			row.addColumnValue(new DataSetColumn("nextVisitDate", "Next Visit date", Date.class), nextVisitDate);
			row.addColumnValue(new DataSetColumn("nextVisitDateETH", "Next Visit date ETH", String.class),
			    retentionLineListQuery.getEthiopianDate(nextVisitDate));
			
			row.addColumnValue(new DataSetColumn("treatmentDate", "Treatment End date", Date.class), treatmentDate);
			row.addColumnValue(new DataSetColumn("treatmentDateETH", "Treatment End DateETH", String.class),
			    retentionLineListQuery.getEthiopianDate(treatmentDate));
			
			data.addRow(row);
			
		}
		
		return data;
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
