package org.openmrs.module.ohrireports.datasetevaluator.linelist.ml;

import org.jetbrains.annotations.NotNull;
import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.openmrs.module.ohrireports.constants.Identifiers;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.MLDataSetDefinition;
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

@Handler(supports = { MLDataSetDefinition.class })
public class MLDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private MLQueryLineList mlQueryLineList;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		MLDataSetDefinition _datasetDefinition = (MLDataSetDefinition) dataSetDefinition;
		SimpleDataSet data = new SimpleDataSet(dataSetDefinition, evalContext);
		
		// Check start date and end date are valid
		// If start date is greater than end date
		if (_datasetDefinition.getStartDate() != null && _datasetDefinition.getEndDate() != null
		        && _datasetDefinition.getStartDate().compareTo(_datasetDefinition.getEndDate()) > 0) {
			DataSetRow row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("Error", "Error", Integer.class),
			    "Report start date cannot be after report end date");
			data.addRow(row);
			return data;
		}
		
		Cohort cohort = mlQueryLineList.getMLQuery(_datasetDefinition.getStartDate(), _datasetDefinition.getEndDate());
		
		List<Person> persons = LineListUtilities.sortPatientByName(mlQueryLineList.getPerson(cohort));
		
		HashMap<Integer, Object> artStartDateHashMap = mlQueryLineList.getObsValueDate(mlQueryLineList.getBaseEncounter(),
		    FollowUpConceptQuestions.ART_START_DATE, cohort);
		HashMap<Integer, Object> lastFollowupDateHashMap = mlQueryLineList.getObsValueDate(
		    mlQueryLineList.getBaseEncounter(), FollowUpConceptQuestions.FOLLOW_UP_DATE, cohort);
		HashMap<Integer, Object> nextVisitDateHashMap = mlQueryLineList.getObsValueDate(mlQueryLineList.getBaseEncounter(),
		    FollowUpConceptQuestions.NEXT_VISIT_DATE, cohort);
		HashMap<Integer, Object> mrnIdentifierHashMap = mlQueryLineList.getIdentifier(cohort,
		    Identifiers.MRN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> uanIdentifierHashMap = mlQueryLineList.getIdentifier(cohort,
		    Identifiers.UAN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> statusHashMap = mlQueryLineList.getFollowUpStatus(mlQueryLineList.getBaseEncounter(),
		    cohort);
		HashMap<Integer, Object> regimentHashMap = mlQueryLineList.getRegiment(mlQueryLineList.getBaseEncounter(), cohort);
		HashMap<Integer, Object> dispensDayHashMap = mlQueryLineList.getConceptName(mlQueryLineList.getBaseEncounter(),
		    cohort, FollowUpConceptQuestions.ARV_DISPENSED_IN_DAYS);
		HashMap<Integer, Object> adherenceHashMap = mlQueryLineList.getByResult(FollowUpConceptQuestions.ARV_ADHERENCE,
		    cohort, mlQueryLineList.getBaseEncounter());
		HashMap<Integer, Object> lastTxCurrDateHashMap = mlQueryLineList.getObsValueDate(mlQueryLineList.getBaseEncounter(),
		    FollowUpConceptQuestions.TREATMENT_END_DATE, cohort);
		//HashMap<Integer, Object> onPMTCTHashMap = mlQueryLineList.getByResult(,cohort,mlQueryLineList.getBaseEncounter());
		
		DataSetRow row;
		if (persons.size() > 0) {
			
			row = new DataSetRow();
			
			row.addColumnValue(new DataSetColumn("#", "#", Integer.class), "TOTAL");
			row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", Integer.class), persons.size());
			data.addRow(row);
		} else {
			data.addRow(LineListUtilities.buildEmptyRow(Arrays.asList("#", "Patient Name", "MRN", "UAN", "Age", "Sex",
			    "ART Start Date in E.C", "Last Followup Date in E.C", "Next Visit Date in E.C", "Last Follow-up Status",
			    "Regiment", "ARV Dose Days", "Adherence", "Next Visit Date in E.C", "Last TX_CURR Date in E.C",
			    "On Treatment For (in months)", "On PMTCT", "MobileNo", "SubCity", "Woreda", "House No.")));
		}
		int i = 1;
		for (Person person : persons) {
			
			Date artStartDate = mlQueryLineList.getDate(artStartDateHashMap.get(person.getPersonId()));
			Date nextVisitDate = mlQueryLineList.getDate(nextVisitDateHashMap.get(person.getPersonId()));
			Date lastFollowupDate = mlQueryLineList.getDate(lastFollowupDateHashMap.get(person.getPersonId()));
			Date lastTxCurrDate = mlQueryLineList.getDate(lastTxCurrDateHashMap.get(person.getPersonId()));
			PersonAddress personAddress = LineListUtilities.getPersonAddress(person.getAddresses());
			row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("#", "#", Integer.class), i++);
			row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", String.class), person.getNames());
			row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class),
			    getStringIdentifier(mrnIdentifierHashMap.get(person.getPersonId())));
			row.addColumnValue(new DataSetColumn("UAN", "UAN", String.class),
			    getStringIdentifier(uanIdentifierHashMap.get(person.getPersonId())));
			row.addColumnValue(new DataSetColumn("Age", "Age", Integer.class), person.getAge(lastTxCurrDate));
			row.addColumnValue(new DataSetColumn("Gender", "Sex", String.class), person.getGender());
			row.addColumnValue(new DataSetColumn("ArtStartDateETC", "ART Start Date in E.C", String.class),
			    mlQueryLineList.getEthiopianDate(artStartDate));
			row.addColumnValue(new DataSetColumn("lastFollowupDate", "Last Follow-up Date in E.C", String.class),
			    mlQueryLineList.getEthiopianDate(lastFollowupDate));
			row.addColumnValue(new DataSetColumn("lastFollowupStatus", "Last Follow-up Status", String.class),
			    statusHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Regimen", "Regimen", String.class),
			    regimentHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("arvDispenseDay", "ARV Dose Days", String.class),
			    dispensDayHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("adherence", "Adherence", String.class),
			    adherenceHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("nextVisitDateETC", "Next Visit Date in E.C", String.class),
			    mlQueryLineList.getEthiopianDate(nextVisitDate));
			row.addColumnValue(new DataSetColumn("lastTxCurrDateETC", "Last TX_CURR Date in E.C", String.class),
			    mlQueryLineList.getEthiopianDate(lastTxCurrDate));
			row.addColumnValue(new DataSetColumn("OnTreatmentFor", "On Treatment For (in months)", String.class),
			    LineListUtilities.getMonthDifference(artStartDate, lastTxCurrDate));
			row.addColumnValue(new DataSetColumn("onPMTCT", "On PMTCT?", String.class), "");
			row.addColumnValue(new DataSetColumn("MobileNo", "Mobile No.", String.class),
			    getPhone(person.getActiveAttributes()));
			row.addColumnValue(new DataSetColumn("SubCity", "Sub-City", String.class), personAddress.getCountyDistrict());
			row.addColumnValue(new DataSetColumn("Woreda", "Woreda", String.class), personAddress.getCityVillage());
			row.addColumnValue(new DataSetColumn("HouseNo", "House No.", String.class), "");
			data.addRow(row);
		}
		
		return data;
	}
	
	@NotNull
	private static Optional<PersonAddress> get(Person person) {
		return person.getAddresses().stream().findFirst();
	}
	
	private String getStringIdentifier(Object patientIdentifier) {
		return Objects.isNull(patientIdentifier) ? "--" : patientIdentifier.toString();
	}
	
	private String getPhone(List<PersonAttribute> activeAttributes) {
		for (PersonAttribute personAttribute : activeAttributes) {
			if (personAttribute.getValue().startsWith("09") || personAttribute.getValue().startsWith("+251")) {
				return personAttribute.getValue();
			}
		}
		return "";
	}
	
	private String getHouseNumber(List<PersonAttribute> activeAttributes) {
		for (PersonAttribute personAttribute : activeAttributes) {
			if (personAttribute.getAttributeType().toString().equals("House Number")) {
				return personAttribute.getValue();
			}
		}
		return "";
	}
}
