package org.openmrs.module.ohrireports.datasetevaluator.linelist.monthlyVisit;

import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.constants.ConceptAnswer;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.openmrs.module.ohrireports.constants.Identifiers;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.MonthlyVisitDatasetDefinition;
import org.openmrs.module.ohrireports.datasetevaluator.linelist.LineListUtilities;
import org.openmrs.module.ohrireports.helper.EthiOhriUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Handler(supports = { MonthlyVisitDatasetDefinition.class })
public class MonthlyVisitDatasetEvaluator implements DataSetEvaluator {
	
	@Autowired
	MonthlyVisitQuery monthlyVisitQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		MonthlyVisitDatasetDefinition dsd = (MonthlyVisitDatasetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(dsd, evalContext);
		
		SimpleDataSet _dataSet = EthiOhriUtil.isValidReportDateRange(dsd.getStartDate(), dsd.getEndDate(), dataSet);
		if (_dataSet != null)
			return _dataSet;
		
		monthlyVisitQuery.generateReport(dsd.getStartDate(), dsd.getEndDate());
		
		HashMap<Integer, Object> mrnIdentifierHashMap = monthlyVisitQuery.getIdentifier(monthlyVisitQuery.getBaseCohort(),
		    Identifiers.MRN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> uaIdentifierHashMap = monthlyVisitQuery.getIdentifier(monthlyVisitQuery.getBaseCohort(),
		    Identifiers.UAN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> artStartDictionary = monthlyVisitQuery.getArtStartDate(monthlyVisitQuery.getBaseCohort(),
		    null, dsd.getEndDate());
		HashMap<Integer, Object> regimentDictionary = monthlyVisitQuery.getRegiment(monthlyVisitQuery.getEncounter(),
		    monthlyVisitQuery.getBaseCohort());
		HashMap<Integer, Object> followUpDate = monthlyVisitQuery.getObsValueDate(monthlyVisitQuery.getEncounter(),
		    FollowUpConceptQuestions.FOLLOW_UP_DATE, monthlyVisitQuery.getBaseCohort());
		HashMap<Integer, Object> followUpStatus = monthlyVisitQuery.getFollowUpStatus(monthlyVisitQuery.getEncounter(),
		    monthlyVisitQuery.getBaseCohort());
		HashMap<Integer, Object> viralLoadStatus = monthlyVisitQuery.getConceptLabel(monthlyVisitQuery.getEncounter(),
		    monthlyVisitQuery.getBaseCohort(), FollowUpConceptQuestions.VIRAL_LOAD_STATUS);
		HashMap<Integer, Object> pregnantHashMap = monthlyVisitQuery.getByResult(FollowUpConceptQuestions.PREGNANCY_STATUS,
		    monthlyVisitQuery.getBaseCohort(), monthlyVisitQuery.getEncounter());
		HashMap<Integer, Object> dose = monthlyVisitQuery.getByResult(FollowUpConceptQuestions.ARV_DISPENSED_IN_DAYS,
		    monthlyVisitQuery.getBaseCohort(), monthlyVisitQuery.getEncounter());
		HashMap<Integer, Object> weightHashMap = monthlyVisitQuery.getByValueText(FollowUpConceptQuestions.WEIGHT,
		    monthlyVisitQuery.getBaseCohort(), monthlyVisitQuery.getEncounter());
		HashMap<Integer, Object> bmiHashMap = monthlyVisitQuery.getByValueText(FollowUpConceptQuestions.BMI,
		    monthlyVisitQuery.getBaseCohort(), monthlyVisitQuery.getEncounter());
		HashMap<Integer, Object> nutritionalStatusHashMap = monthlyVisitQuery.getConceptName(
		    monthlyVisitQuery.getEncounter(), monthlyVisitQuery.getBaseCohort(),
		    FollowUpConceptQuestions.NUTRITIONAL_SCREENING_RESULT);
		HashMap<Integer, Object> adherence = monthlyVisitQuery.getByResult(FollowUpConceptQuestions.ARV_ADHERENCE,
		    monthlyVisitQuery.getBaseCohort(), monthlyVisitQuery.getEncounter());
		HashMap<Integer, Object> nextVisitDate = monthlyVisitQuery.getObsValueDate(monthlyVisitQuery.getEncounter(),
		    FollowUpConceptQuestions.NEXT_VISIT_DATE, monthlyVisitQuery.getBaseCohort());
		HashMap<Integer, Object> vlRequestDate = monthlyVisitQuery.getObsValueDate(monthlyVisitQuery.getEncounter(),
		    FollowUpConceptQuestions.DATE_VL_REQUESTED, monthlyVisitQuery.getBaseCohort());
		HashMap<Integer, Object> vlReceivedDate = monthlyVisitQuery.getObsValueDate(monthlyVisitQuery.getEncounter(),
		    FollowUpConceptQuestions.VL_RECEIVED_DATE, monthlyVisitQuery.getBaseCohort());
		HashMap<Integer, Object> cd4CatagoriesHashMap = monthlyVisitQuery.getByResult(
		    FollowUpConceptQuestions.ADULT_CD4_COUNT, monthlyVisitQuery.getBaseCohort(), monthlyVisitQuery.getEncounter());
		HashMap<Integer, Object> nutirationStatusHashMap = monthlyVisitQuery.getConceptLabel(
		    monthlyVisitQuery.getEncounter(), monthlyVisitQuery.getBaseCohort(),
		    FollowUpConceptQuestions.NUTRITIONAL_STATUS_ADULT);
		HashMap<Integer, Object> tptStatusCatagoriesHashmap = monthlyVisitQuery.getByResult(
		    FollowUpConceptQuestions.TPT_FOLLOW_UP_STATUS, monthlyVisitQuery.getBaseCohort(),
		    monthlyVisitQuery.getEncounter());
		HashMap<Integer, Object> dsdCatagories = monthlyVisitQuery.getByResult(FollowUpConceptQuestions.DSD_CATGORIES,
		    monthlyVisitQuery.getBaseCohort(), monthlyVisitQuery.getEncounter());
		HashMap<Integer, Object> dsdAssessmentDate = monthlyVisitQuery.getObsValueDate(monthlyVisitQuery.getEncounter(),
		    FollowUpConceptQuestions.DSD_ASSESSMENT_DATE, monthlyVisitQuery.getBaseCohort());
		HashMap<Integer, Object> tbScreeningResult = monthlyVisitQuery
		        .getByResult(FollowUpConceptQuestions.TB_SCREENED_RESULT, monthlyVisitQuery.getBaseCohort(),
		            monthlyVisitQuery.getEncounter());
		HashMap<Integer, Object> treatmentEndDateHashMap = monthlyVisitQuery
		        .getObsValueDate(monthlyVisitQuery.getEncounter(), FollowUpConceptQuestions.TREATMENT_END_DATE,
		            monthlyVisitQuery.getBaseCohort());
		
		DataSetRow row;
		List<Person> personList = LineListUtilities.sortPatientByName(monthlyVisitQuery.getPersons(monthlyVisitQuery
		        .getBaseCohort()));
		if (!personList.isEmpty()) {
			
			row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("#", "#", Integer.class), "TOTAL");
			row.addColumnValue(new DataSetColumn("GUID", "GUID", Integer.class), personList.size());
			
			dataSet.addRow(row);
		} else {
			dataSet.addRow(LineListUtilities.buildEmptyRow(Arrays.asList("#", "GUID", "Patient Name", "MRN", "UAN", "Age",
			    "Sex", "Weight", "BMI", "Nutritional Screening Result", "Pregnant?", "ART Start Date", "Follow-up Date E.C",
			    "Follow-up Status", "Regimen", "ARV Dose", "Adherence", "VL Request Date", "VL Received Date", "VL Status",
			    "CD4", "Nutrition Status ", "TPT Status", "TB Screening Result", "DSD Category",
			    "Current DSD Assessment Date", "Next Visit Date", "Treatment End Date in E.C.", "Mobile No.")));
		}
		
		int i = 1;
		for (Person person : personList) {
			row = new DataSetRow();
			
			row.addColumnValue(new DataSetColumn("#", "#", Integer.class), i++);
			row.addColumnValue(new DataSetColumn("GUID", "GUID", String.class), person.getUuid());
			row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", String.class), person.getNames());
			row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class), mrnIdentifierHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("UAN", "UAN", String.class), uaIdentifierHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Age", "Age", String.class), person.getAge());
			row.addColumnValue(new DataSetColumn("Sex", "Sex", String.class), person.getGender());
			row.addColumnValue(new DataSetColumn("Weight", "Weight", String.class), weightHashMap.get(person.getPersonId()));
			
			row.addColumnValue(new DataSetColumn("BMI", "BMI", String.class), bmiHashMap.get(person.getPersonId()));
			
			row.addColumnValue(new DataSetColumn("Nutritional Screening Result", "Nutritional Screening Result",
			        String.class), nutritionalStatusHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Pregnant?", "Pregnant?", String.class),
			    pregnantHashMap.get(person.getPersonId()));
			
			row.addColumnValue(new DataSetColumn("ARTStartDateEth", "ART StartDate E.C", String.class),
			    monthlyVisitQuery.getEthiopianDate((Date) artStartDictionary.get(person.getPersonId())));
			row.addColumnValue(new DataSetColumn("Follow-up Date E.C", "Follow-up Date E.C", String.class),
			    monthlyVisitQuery.getEthiopianDate((Date) followUpDate.get(person.getPersonId())));
			row.addColumnValue(new DataSetColumn("followUpStatus", "Follow-up Status", String.class),
			    followUpStatus.get(person.getPersonId()));
			
			row.addColumnValue(new DataSetColumn("ARVRegiment", "Regimen", String.class),
			    regimentDictionary.get(person.getPersonId()));
			
			row.addColumnValue(new DataSetColumn("ARVDoseDays", "ARV Dose", String.class), dose.get(person.getPersonId()));
			
			row.addColumnValue(new DataSetColumn("adherence", "Adherence", String.class),
			    adherence.get(person.getPersonId()));
			
			row.addColumnValue(new DataSetColumn("vl-request-date", "VL Request Date", String.class),
			    monthlyVisitQuery.getEthiopianDate((Date) vlRequestDate.get(person.getPersonId())));
			row.addColumnValue(new DataSetColumn("vl-received-date", "VL Received Date", String.class),
			    monthlyVisitQuery.getEthiopianDate((Date) vlReceivedDate.get(person.getPersonId())));
			
			row.addColumnValue(new DataSetColumn("vl-status", "VL Status", String.class),
			    viralLoadStatus.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("CD4", "CD4", String.class), cd4CatagoriesHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Nutrition Status", "Nutrition Status", String.class),
			    nutirationStatusHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("TPT-Status", "TPT Status", String.class),
			    tptStatusCatagoriesHashmap.get(person.getPersonId()));
			
			row.addColumnValue(new DataSetColumn("TBScreeningResult", "TB Screening Result", String.class),
			    tbScreeningResult.get(person.getPersonId()));
			
			row.addColumnValue(new DataSetColumn("DSD Category", "DSD Category", String.class),
			    dsdCatagories.get(person.getPersonId()));
			row.addColumnValue(
			    new DataSetColumn("Current DSD Assessment Date", "Current DSD Assessment Date", String.class),
			    dsdAssessmentDate.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("nextVisitDate", "Next Visit Date", String.class),
			    monthlyVisitQuery.getEthiopianDate((Date) nextVisitDate.get(person.getPersonId())));
			
			row.addColumnValue(new DataSetColumn("Treatment End Date in E.C.", "Treatment End Date in E.C.", String.class),
			    monthlyVisitQuery.getEthiopianDate((Date) treatmentEndDateHashMap.get(person.getPersonId())));
			
			row.addColumnValue(new DataSetColumn("Mobile", "Mobile No.", String.class),
			    getPhone(person.getActiveAttributes()));
			dataSet.addRow(row);
		}
		
		return dataSet;
	}
	
	private String getPhone(List<PersonAttribute> activeAttributes) {
		return LineListUtilities.getPhone(activeAttributes);
	}
}
