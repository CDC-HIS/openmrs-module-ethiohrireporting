package org.openmrs.module.ohrireports.datasetevaluator.linelist.pep;

import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.PEPDataSetDefinition;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_linkage_new_ct.Linkage;
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

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Handler(supports = { PEPDataSetDefinition.class })
public class PEPDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private PEPQueryLineList pepQueryLineList;
	
	/**
	 * Evaluate a DataSet for the given EvaluationContext
	 * 
	 * @param dataSetDefinition
	 * @param evalContext
	 * @return the evaluated <code>DataSet</code>
	 */
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		List<Integer> twoWksFollowUp, fourWksFollowUp, sixWksFollowUp, threeMthFollowUp, sixMthFollowup;
		List<Person> persons = new ArrayList<>();
		PEPDataSetDefinition _dataSetDefinition = (PEPDataSetDefinition) dataSetDefinition;
		SimpleDataSet data = new SimpleDataSet(_dataSetDefinition, evalContext);
		
		pepQueryLineList.generateReport(_dataSetDefinition.getStartDate(), _dataSetDefinition.getEndDate());
		twoWksFollowUp = pepQueryLineList.getEncounterBaseOnPeriod(PEP_TWO_WEEK_FOLLOW_UP);
		fourWksFollowUp = pepQueryLineList.getEncounterBaseOnPeriod(PEP_FOUR_WEEK_FOLLOW_UP);
		sixWksFollowUp = pepQueryLineList.getEncounterBaseOnPeriod(PEP_SIX_WEEK_FOLLOW_UP);
		threeMthFollowUp = pepQueryLineList.getEncounterBaseOnPeriod(PEP_THREE_MONTH_FOLLOW_UP);
		sixMthFollowup = pepQueryLineList.getEncounterBaseOnPeriod(PEP_SIX_MONTH_FOLLOW_UP);
		
		
		HashMap<Integer, Object> reportDateHashMap = pepQueryLineList.getObsValueDate(pepQueryLineList.getBaseEncounter(),
				
				PEP_REGISTRATION_DATE, pepQueryLineList.getBaseCohort());
		//two week
		HashMap<Integer, Object> visitDateTwoWeek = pepQueryLineList.getObsValueDate(twoWksFollowUp, PEP_FOLLOWUP_DATE, pepQueryLineList.getBaseCohort());
		HashMap<Integer, Object> adherenceTwoWeek = pepQueryLineList.getConceptName(twoWksFollowUp, pepQueryLineList.getBaseCohort(), ARV_ADHERENCE);
		HashMap<Integer, Object> sideEffectTwoWeek = pepQueryLineList.getConceptName(twoWksFollowUp, pepQueryLineList.getBaseCohort(), PEP_SOURCE_PERSON_HIV_STATUS);
		HashMap<Integer, Object> exposedClientTwoWeek = pepQueryLineList.getConceptName(twoWksFollowUp, pepQueryLineList.getBaseCohort(), PEP_SIDE_EFFECT);
		
		//fourths week
		HashMap<Integer, Object> visitDateFourthWeek = pepQueryLineList.getObsValueDate(fourWksFollowUp, PEP_FOLLOWUP_DATE, pepQueryLineList.getBaseCohort());
		HashMap<Integer, Object> adherenceFourthWeek = pepQueryLineList.getConceptName(fourWksFollowUp, pepQueryLineList.getBaseCohort(), ARV_ADHERENCE);
		HashMap<Integer, Object> sideEffectFourthWeek = pepQueryLineList.getConceptName(fourWksFollowUp, pepQueryLineList.getBaseCohort(), PEP_SOURCE_PERSON_HIV_STATUS);
		HashMap<Integer, Object> exposedClientFourthWeek = pepQueryLineList.getConceptName(fourWksFollowUp, pepQueryLineList.getBaseCohort(), PEP_SIDE_EFFECT);
		
		//six week
		HashMap<Integer, Object> visitDateSixWeek = pepQueryLineList.getObsValueDate(sixWksFollowUp, PEP_FOLLOWUP_DATE, pepQueryLineList.getBaseCohort());
		HashMap<Integer, Object> adherenceSixWeek = pepQueryLineList.getConceptName(sixWksFollowUp, pepQueryLineList.getBaseCohort(), ARV_ADHERENCE);
		HashMap<Integer, Object> sideEffectSixWeek = pepQueryLineList.getConceptName(sixWksFollowUp, pepQueryLineList.getBaseCohort(), PEP_SOURCE_PERSON_HIV_STATUS);
		HashMap<Integer, Object> exposedClientSixWeek = pepQueryLineList.getConceptName(sixWksFollowUp, pepQueryLineList.getBaseCohort(), PEP_SIDE_EFFECT);
		
		//three month
		HashMap<Integer, Object> visitDateThreeMonth = pepQueryLineList.getObsValueDate(threeMthFollowUp, PEP_FOLLOWUP_DATE, pepQueryLineList.getBaseCohort());
		HashMap<Integer, Object> adherenceThreeMonth = pepQueryLineList.getConceptName(threeMthFollowUp, pepQueryLineList.getBaseCohort(), ARV_ADHERENCE);
		HashMap<Integer, Object> sideEffectThreeMonth = pepQueryLineList.getConceptName(threeMthFollowUp, pepQueryLineList.getBaseCohort(), PEP_SOURCE_PERSON_HIV_STATUS);
		HashMap<Integer, Object> exposedClientThreeMonth = pepQueryLineList.getConceptName(threeMthFollowUp, pepQueryLineList.getBaseCohort(), PEP_SIDE_EFFECT);
		
		//six month
		HashMap<Integer, Object> visitDateSixMonth = pepQueryLineList.getObsValueDate(sixMthFollowup, PEP_FOLLOWUP_DATE, pepQueryLineList.getBaseCohort());
		HashMap<Integer, Object> adherenceSixMonth = pepQueryLineList.getConceptName(sixMthFollowup, pepQueryLineList.getBaseCohort(), ARV_ADHERENCE);
		HashMap<Integer, Object> sideEffectSixMonth = pepQueryLineList.getConceptName(sixMthFollowup, pepQueryLineList.getBaseCohort(), PEP_SOURCE_PERSON_HIV_STATUS);
		HashMap<Integer, Object> exposedClientSixMonth = pepQueryLineList.getConceptName(sixMthFollowup, pepQueryLineList.getBaseCohort(), PEP_SIDE_EFFECT);
		
		
		HashMap<Integer, Object> occupationHashMap = pepQueryLineList.getConceptName(pepQueryLineList.getBaseEncounter(), pepQueryLineList.getBaseCohort(),
				OCCUPATIONAL);
		HashMap<Integer, Object> departementHashMap = pepQueryLineList.getConceptName(pepQueryLineList.getBaseEncounter(), pepQueryLineList.getBaseCohort(),
				PEP_DEPARTMENT);
		HashMap<Integer, Object> exposureDurationHashMap = pepQueryLineList.getConceptNumber(pepQueryLineList.getBaseEncounter(), PEP_EXPOSURE_DURATION);
		HashMap<Integer, Object> exposureTypeHashMap = pepQueryLineList.getConceptName(pepQueryLineList.getBaseEncounter(), pepQueryLineList.getBaseCohort(),
				PEP_EXPOSURE_TYPE);
		HashMap<Integer, Object> exposureCodeHashMap = pepQueryLineList.getConceptName(pepQueryLineList.getBaseEncounter(), pepQueryLineList.getBaseCohort(),
				PEP_EXPOSURE_CODE);
		HashMap<Integer, Object> sourcePersonHivStatusHashMap = pepQueryLineList.getConceptName(pepQueryLineList.getBaseEncounter(), pepQueryLineList.getBaseCohort(),
				PEP_SOURCE_PERSON_HIV_STATUS);
		HashMap<Integer, Object> exposedPersonHivStatusHashMap = pepQueryLineList.getConceptName(pepQueryLineList.getBaseEncounter(), pepQueryLineList.getBaseCohort(),
				PEP_EXPOSED_PERSON_HIV_STATUS);
		HashMap<Integer, Object> sourceOfExposureHashMap = pepQueryLineList.getConceptName(pepQueryLineList.getBaseEncounter(), pepQueryLineList.getBaseCohort(),
				PEP_SOURCE_EXPOSED);
		HashMap<Integer, Object> eligiblityHashMap = pepQueryLineList.getConceptName(pepQueryLineList.getBaseEncounter(), pepQueryLineList.getBaseCohort(),
				PEP_ELIGIBLE);
		HashMap<Integer, Object> arvRegimentHashMap = pepQueryLineList.getConceptName(pepQueryLineList.getBaseEncounter(), pepQueryLineList.getBaseCohort(),
				REGIMEN);
		HashMap<Integer, Object> inBetweenHashMap = pepQueryLineList.getConceptNumber(pepQueryLineList.getBaseEncounter(), PEP_TIME_BETWEEN);
		
		HashMap<Integer, Object> mrnIdentifierHashMap = pepQueryLineList.getIdentifier(pepQueryLineList.getBaseCohort(), MRN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> uanIdentifierHashMap = pepQueryLineList.getIdentifier(pepQueryLineList.getBaseCohort(), UAN_PATIENT_IDENTIFIERS);
		
		persons = pepQueryLineList.getPerson();
		
		DataSetRow row;
		if (!persons.isEmpty()) {
			
			row = new DataSetRow();
			
			row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class), "TOTAL");
			row.addColumnValue(new DataSetColumn("Name", "Name", Integer.class), persons.size());
			
			data.addRow(row);
		}
		
		for (Person person : persons) {
			
			Date reportDate = pepQueryLineList.getDate(reportDateHashMap.get(person.getPersonId()));
			Date twoWkVistDate = pepQueryLineList.getDate(visitDateTwoWeek.get(person.getPersonId()));
			Date fourWkVistDate = pepQueryLineList.getDate(visitDateFourthWeek.get(person.getPersonId()));
			Date sixWkVistDate = pepQueryLineList.getDate(visitDateSixWeek.get(person.getPersonId()));
			Date threeMthVisitDate = pepQueryLineList.getDate(visitDateThreeMonth.get(person.getPersonId()));
			Date sixMthVisitDate = pepQueryLineList.getDate(visitDateSixMonth.get(person.getPersonId()));
			
			row = new DataSetRow();
			
			row.addColumnValue(new DataSetColumn("Name", "Name", String.class), person.getNames());
			row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class),
					getStringIdentifier(mrnIdentifierHashMap.get(person.getPersonId())));
			row.addColumnValue(new DataSetColumn("UANO", "UANO", String.class),
					getStringIdentifier(uanIdentifierHashMap.get(person.getPersonId())));
			row.addColumnValue(new DataSetColumn("Age", "Age", Integer.class),
					person.getAge(_dataSetDefinition.getEndDate()));
			row.addColumnValue(new DataSetColumn("Gender", "Gender", String.class), person.getGender());
			row.addColumnValue(new DataSetColumn("Occupation", "occupation", String.class), occupationHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Department", "Department", String.class), departementHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("reportDate", "Report Date", Date.class), reportDate);
			row.addColumnValue(new DataSetColumn("reportDateETC", "Report  Date ETH", String.class),
					pepQueryLineList.getEthiopianDate(reportDate));
			row.addColumnValue(new DataSetColumn("Exposure-duration", "Exposure Duration (Hrs)", String.class), exposureDurationHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Exposure Type", "Exposure Type", String.class),
					exposureTypeHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Exposure Code", "Exposure Code", String.class),
					exposureCodeHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Source Person HIV Status", "Source Person HIV Status", String.class),
					sourcePersonHivStatusHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Exposed Person HIV Status", "Exposed Person HIV Status", String.class),
					exposedPersonHivStatusHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Source of Exposure", "Source of Exposure", String.class),
					sourceOfExposureHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Eligible", "Eligible", String.class),
					eligiblityHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Arv Regimen", "Arv Regimen", String.class),
					arvRegimentHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Time between exposure and PEP (Hrs)", "Time between exposure and PEP (Hrs)", String.class),
					inBetweenHashMap.get(person.getPersonId()));
			
			//two week
			row.addColumnValue(new DataSetColumn("Follow-Up Visit Date (at 2WK)", "Follow-Up Visit Date (at 2WK)", Date.class), twoWkVistDate);
			row.addColumnValue(new DataSetColumn("Follow-Up Visit Date (at 2WK)ETH", "Follow-Up Visit Date (at 2WK) ETH", String.class),
					pepQueryLineList.getEthiopianDate(twoWkVistDate));
			row.addColumnValue(new DataSetColumn("Adherence (at 2WK)", "Adherence (at 2WK)", String.class), adherenceTwoWeek.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Side Effect(at 2WK)", "Side Effect(at 2WK)", String.class), sideEffectTwoWeek.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Exposed client HIV Status (at 2WK)", "Exposed client HIV Status (at 2WK)", String.class), exposedClientTwoWeek.get(person.getPersonId()));
			
			//four week
			row.addColumnValue(new DataSetColumn("Follow-Up Visit Date (at 4WK)", "Follow-Up Visit Date (at 4WK)", Date.class), fourWkVistDate);
			row.addColumnValue(new DataSetColumn("Follow-Up Visit Date (at 4WK)ETH", "Follow-Up Visit Date (at 4WK) ETH", String.class),
					pepQueryLineList.getEthiopianDate(fourWkVistDate));
			row.addColumnValue(new DataSetColumn("Adherence (at 4WK)", "Adherence (at 4WK)", String.class), adherenceFourthWeek.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Side Effect(at 4WK)", "Side Effect(at 4WK)", String.class), sideEffectFourthWeek.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Exposed client HIV Status (at 2WK)", "Exposed client HIV Status (at 2WK)", String.class), exposedClientFourthWeek.get(person.getPersonId()));
			
			//six week
			row.addColumnValue(new DataSetColumn("Follow-Up Visit Date (at 6WK)", "Follow-Up Visit Date (at 6WK)", Date.class), sixWkVistDate);
			row.addColumnValue(new DataSetColumn("Follow-Up Visit Date (at 6WK)ETH", "Follow-Up Visit Date (at 6WK) ETH", String.class),
					pepQueryLineList.getEthiopianDate(sixWkVistDate));
			row.addColumnValue(new DataSetColumn("Adherence (at 6WK)", "Adherence (at 6WK)", String.class), adherenceSixWeek.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Side Effect(at 6WK)", "Side Effect(at 6WK)", String.class), sideEffectSixWeek.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Exposed client HIV Status (at 6WK)", "Exposed client HIV Status (at 6WK)", String.class), exposedClientSixWeek.get(person.getPersonId()));
			
			//three month
			row.addColumnValue(new DataSetColumn("Follow-Up Visit Date (at 3M)", "Follow-Up Visit Date (at 3M)", Date.class), threeMthVisitDate);
			row.addColumnValue(new DataSetColumn("Follow-Up Visit Date (at 3M)ETH", "Follow-Up Visit Date (at 3M) ETH", String.class),
					pepQueryLineList.getEthiopianDate(threeMthVisitDate));
			row.addColumnValue(new DataSetColumn("Adherence (at 3M)", "Adherence (at 3M)", String.class), adherenceThreeMonth.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Side Effect(at 3M)", "Side Effect(at 3M)", String.class), sideEffectThreeMonth.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Exposed client HIV Status (at 3M)", "Exposed client HIV Status (at 3M)", String.class), exposedClientThreeMonth.get(person.getPersonId()));
			
			//six month
			row.addColumnValue(new DataSetColumn("Follow-Up Visit Date (at 6M)", "Follow-Up Visit Date (at 6M)", Date.class), sixMthVisitDate);
			row.addColumnValue(new DataSetColumn("Follow-Up Visit Date (at 6M)ETH", "Follow-Up Visit Date (at 6M) ETH", String.class),
					pepQueryLineList.getEthiopianDate(sixMthVisitDate));
			row.addColumnValue(new DataSetColumn("Adherence (at 6M)", "Adherence (at 6M)", String.class), adherenceSixMonth.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Side Effect(at 6M)", "Side Effect(at 6M)", String.class), sideEffectSixMonth.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Exposed client HIV Status (at 6M)", "Exposed client HIV Status (at 6M)", String.class), exposedClientSixMonth.get(person.getPersonId()));
			
			data.addRow(row);
			
		}
		
		return data;
	}
	
	private Object getStringIdentifier(Object patientIdentifier) {
		return Objects.isNull(patientIdentifier) ? "--" : patientIdentifier.toString();
	}
}
