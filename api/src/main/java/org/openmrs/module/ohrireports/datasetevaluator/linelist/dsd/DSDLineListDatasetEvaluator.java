package org.openmrs.module.ohrireports.datasetevaluator.linelist.dsd;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.DSDDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;
import static org.openmrs.module.ohrireports.RegimentConstant.DSD_CATEGORY;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Handler(supports = { DSDDataSetDefinition.class })
public class DSDLineListDatasetEvaluator implements DataSetEvaluator {
	
	@Autowired
	private DSDLineListQuery dsdLineListQuery;
	
	/**
	 * Evaluate a DataSet for the given EvaluationContext
	 * 
	 * @return the evaluated <code>DataSet</code>
	 */
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		DSDDataSetDefinition linkageDataset = (DSDDataSetDefinition) dataSetDefinition;
		SimpleDataSet data = new SimpleDataSet(linkageDataset, evalContext);
		
		dsdLineListQuery.generateReport(linkageDataset.getStartDate(), linkageDataset.getEndDate());
		Cohort cohort = dsdLineListQuery.getBaseCohort();
		List<Person> persons = dsdLineListQuery.getPersons(cohort);
		
		HashMap<Integer, Object> followUpDateHashMap = dsdLineListQuery.getObsValueDate(dsdLineListQuery.getBaseEncounter(),
		    FOLLOW_UP_DATE, cohort);
		HashMap<Integer, Object> confirmedDateHashMap = dsdLineListQuery.getObsValueDate(
		    dsdLineListQuery.getBaseEncounter(), HIV_CONFIRMED_DATE, cohort);
		HashMap<Integer, Object> artStartDateHashMap = dsdLineListQuery.getObsValueDate(dsdLineListQuery.getBaseEncounter(),
		    ART_START_DATE, cohort);
		HashMap<Integer, Object> nextVistDateHashMap = dsdLineListQuery.getObsValueDate(dsdLineListQuery.getBaseEncounter(),
		    NEXT_VISIT_DATE, cohort);
		HashMap<Integer, Object> treatmentDateHashMap = dsdLineListQuery.getObsValueDate(
		    dsdLineListQuery.getBaseEncounter(), TREATMENT_END_DATE, cohort);
		HashMap<Integer, Object> categoryAssesumentDateHashMap = dsdLineListQuery.getObsValueDate(
		    dsdLineListQuery.getBaseEncounter(), DSD_ASSESSMENT_DATE, cohort);
		HashMap<Integer, Object> transferedHashMap = dsdLineListQuery.getConceptName(dsdLineListQuery.getBaseEncounter(),
		    cohort, REASON_FOR_ART_ELIGIBILITY);
		HashMap<Integer, Object> currentDSDCategoryHashMap = dsdLineListQuery.getConceptName(
		    dsdLineListQuery.getBaseEncounter(), cohort, DSD_CATEGORY);
		HashMap<Integer, Object> doseDisPenseHashMap = dsdLineListQuery.getConceptName(dsdLineListQuery.getBaseEncounter(),
		    cohort, ARV_DISPENSED_IN_DAYS);
		HashMap<Integer, Object> regimentHashMap = dsdLineListQuery.getRegiment(dsdLineListQuery.getBaseEncounter(), cohort);
		HashMap<Integer, Object> adherenceHashMap = dsdLineListQuery.getConceptName(dsdLineListQuery.getBaseEncounter(),
		    cohort, ARV_ADHERENCE);
		HashMap<Integer, Object> followUpStatusHashMap = dsdLineListQuery.getConceptName(
		    dsdLineListQuery.getBaseEncounter(), cohort, FOLLOW_UP_STATUS);
		HashMap<Integer, Object> mrnIdentifierHashMap = dsdLineListQuery.getIdentifier(cohort, MRN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> uanIdentifierHashMap = dsdLineListQuery.getIdentifier(cohort, UAN_PATIENT_IDENTIFIERS);
		
		DataSetRow row;
		if (!persons.isEmpty()) {
			
			row = new DataSetRow();
			
			row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class), "TOTAL");
			row.addColumnValue(new DataSetColumn("Name", "Name", Integer.class), persons.size());
			
			data.addRow(row);
		}
		
		for (Person person : persons) {
			
			Date followUpDateDate = dsdLineListQuery.getDate(followUpDateHashMap.get(person.getPersonId()));
			Date confirmedDate = dsdLineListQuery.getDate(confirmedDateHashMap.get(person.getPersonId()));
			Date artStartDate = dsdLineListQuery.getDate(artStartDateHashMap.get(person.getPersonId()));
			Date nextVisitDate = dsdLineListQuery.getDate(nextVistDateHashMap.get(person.getPersonId()));
			Date assesementDate = dsdLineListQuery.getDate(categoryAssesumentDateHashMap.get(person.getPersonId()));
			
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
			    dsdLineListQuery.getEthiopianDate(confirmedDate));
			
			row.addColumnValue(new DataSetColumn("artStartDate", " ART Start Date ", Date.class), artStartDate);
			row.addColumnValue(new DataSetColumn("artStartDateETC", "ART Start  Date ETH", String.class),
			    dsdLineListQuery.getEthiopianDate(artStartDate));
			
			row.addColumnValue(new DataSetColumn("transferIn", "TI?", String.class),
			    transferedHashMap.get(person.getPersonId()));
			
			row.addColumnValue(new DataSetColumn("followUpDateDate", "Follow-up Date", Date.class), followUpDateDate);
			row.addColumnValue(new DataSetColumn("follow-upDateETC", "Follow-up  Date ETH", String.class),
			    dsdLineListQuery.getEthiopianDate(followUpDateDate));
			
			row.addColumnValue(new DataSetColumn("follow-up-status", "Follow-up status", String.class),
			    followUpStatusHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("regimen", "Regimen", String.class),
			    regimentHashMap.get(person.getPersonId()));
			
			row.addColumnValue(new DataSetColumn("dose", "Dose", String.class),
			    doseDisPenseHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("adherence", "Adherence", String.class),
			    adherenceHashMap.get(person.getPersonId()));
			
			row.addColumnValue(new DataSetColumn("enrollmentDate", "Current DSD Category Enrollment Date", Date.class),
			    assesementDate);
			row.addColumnValue(new DataSetColumn("enrollmentDateETH", "Current DSD Category Enrollment DateETH",
			        String.class), dsdLineListQuery.getEthiopianDate(assesementDate));
			
			row.addColumnValue(new DataSetColumn("dsdCategory", "Current DSD Category", String.class),
			    currentDSDCategoryHashMap.get(person.getPersonId()));
			
			row.addColumnValue(new DataSetColumn("nextVisitDate", "Next Visit date", Date.class), nextVisitDate);
			row.addColumnValue(new DataSetColumn("nextVisitDateETH", "Next Visit date ETH", String.class),
			    dsdLineListQuery.getEthiopianDate(nextVisitDate));
			
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
