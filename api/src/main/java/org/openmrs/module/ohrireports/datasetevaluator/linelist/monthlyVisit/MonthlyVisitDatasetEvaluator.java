package org.openmrs.module.ohrireports.datasetevaluator.linelist.monthlyVisit;

import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.MonthlyVisitDatasetDefinition;
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

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Handler(supports = { MonthlyVisitDatasetDefinition.class })
public class MonthlyVisitDatasetEvaluator implements DataSetEvaluator {
	
	@Autowired
	MonthlyVisitQuery monthlyVisitQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		MonthlyVisitDatasetDefinition dsd = (MonthlyVisitDatasetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(dsd, evalContext);
		monthlyVisitQuery.generateReport(dsd.getStartDate(), dsd.getEndDate());
		
		HashMap<Integer, Object> mrnIdentifierHashMap = monthlyVisitQuery.getIdentifier(monthlyVisitQuery.getBaseCohort(),
		    MRN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> uaIdentifierHashMap = monthlyVisitQuery.getIdentifier(monthlyVisitQuery.getBaseCohort(),
		    UAN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> artStartDictionary = monthlyVisitQuery.getArtStartDate(monthlyVisitQuery.getBaseCohort(),
		    null, dsd.getEndDate());
		HashMap<Integer, Object> regimentDictionary = monthlyVisitQuery.getRegiment(monthlyVisitQuery.getEncounter(),
		    monthlyVisitQuery.getBaseCohort());
		HashMap<Integer, Object> followUpDate = monthlyVisitQuery.getObsValueDate(monthlyVisitQuery.getEncounter(),
		    FOLLOW_UP_DATE, monthlyVisitQuery.getBaseCohort());
		HashMap<Integer, Object> followUpStatus = monthlyVisitQuery.getFollowUpStatus(monthlyVisitQuery.getEncounter(),
		    monthlyVisitQuery.getBaseCohort());
		HashMap<Integer, Object> weight = monthlyVisitQuery.getByResult(WEIGHT, monthlyVisitQuery.getBaseCohort(),
		    monthlyVisitQuery.getEncounter());
		HashMap<Integer, Object> viralLoadStatus = monthlyVisitQuery.getByResult(VIRAL_LOAD_STATUS,
		    monthlyVisitQuery.getBaseCohort(), monthlyVisitQuery.getEncounter());
		HashMap<Integer, Object> pregnantHashMap = monthlyVisitQuery.getByResult(PREGNANT_STATUS,
		    monthlyVisitQuery.getBaseCohort(), monthlyVisitQuery.getEncounter());
		HashMap<Integer, Object> dose = monthlyVisitQuery.getByResult(ARV_DISPENSED_IN_DAYS,
		    monthlyVisitQuery.getBaseCohort(), monthlyVisitQuery.getEncounter());
		HashMap<Integer, Object> adherence = monthlyVisitQuery.getByResult(ON_ADHERENCE, monthlyVisitQuery.getBaseCohort(),
		    monthlyVisitQuery.getEncounter());
		HashMap<Integer, Object> nextVisitDate = monthlyVisitQuery.getObsValueDate(monthlyVisitQuery.getEncounter(),
		    NEXT_VISIT_DATE, monthlyVisitQuery.getBaseCohort());
		HashMap<Integer, Object> vlRequestDate = monthlyVisitQuery.getObsValueDate(monthlyVisitQuery.getEncounter(),
		    VL_RECEIVED_DATE, monthlyVisitQuery.getBaseCohort());
		HashMap<Integer, Object> dsdCatagories = monthlyVisitQuery.getByResult(DSD_CATGORIES,
		    monthlyVisitQuery.getBaseCohort(), monthlyVisitQuery.getEncounter());
		HashMap<Integer, Object> tbScreeningResult = monthlyVisitQuery.getByResult(TB_SCREENED_RESULT,
		    monthlyVisitQuery.getBaseCohort(), monthlyVisitQuery.getEncounter());
		
		DataSetRow row = new DataSetRow();
		List<Person> personList = LineListUtilities.sortPatientByName(monthlyVisitQuery.getPersons(monthlyVisitQuery
		        .getBaseCohort()));
		for (Person person : personList) {
			row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("FullName", "FullName", String.class), person.getNames());
			row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class), mrnIdentifierHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("UAN", "UAN", String.class), uaIdentifierHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Age", "Age", String.class), person.getAge());
			row.addColumnValue(new DataSetColumn("Sex", "Sex", String.class), person.getGender());
			row.addColumnValue(new DataSetColumn("Weight", "Weight", String.class), weight.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Pregnant ?", "Pregnant ?", String.class),
			    pregnantHashMap.get(person.getPersonId()));
			
			row.addColumnValue(new DataSetColumn("ARTStartDateEth", "ART StartDate E.C", String.class),
			    monthlyVisitQuery.getEthiopianDate((Date) artStartDictionary.get(person.getPersonId())));
			row.addColumnValue(new DataSetColumn("ARTStartDate", "ART StartDate G.C", String.class),
			    artStartDictionary.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Follow-up Date E.C", "Follow-up Date E.C", String.class),
			    monthlyVisitQuery.getEthiopianDate((Date) followUpDate.get(person.getPersonId())));
			row.addColumnValue(new DataSetColumn("Follow-up Date G.C", "Follow-up Date G.C", String.class),
			    followUpDate.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("followUpStatus", "Follow-up Status", String.class),
			    followUpStatus.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("ARVRegiment", "Regiment", String.class),
			    regimentDictionary.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("ARVDoseDays", "ARV Dose", String.class), dose.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("adherence", "Adherence", String.class),
			    adherence.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("vl-request-date", "VL Request Date", String.class),
			    monthlyVisitQuery.getEthiopianDate((Date) vlRequestDate.get(person.getPersonId())));
			row.addColumnValue(new DataSetColumn("vl-status", "VL Status", String.class),
			    viralLoadStatus.get(person.getPersonId()));
			
			row.addColumnValue(new DataSetColumn("TBScreeningResult", "TB Screening Result", String.class),
			    tbScreeningResult.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("DSD", "DSD", String.class), dsdCatagories.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("nextVisitDate", "Next Visit Date", String.class),
			    monthlyVisitQuery.getEthiopianDate((Date) nextVisitDate.get(person.getPersonId())));
			row.addColumnValue(new DataSetColumn("Mobile", "Mobile No.", String.class),
			    getPhone(person.getActiveAttributes()));
			dataSet.addRow(row);
		}
		
		return dataSet;
	}
	
	private String getPhone(List<PersonAttribute> activeAttributes) {
		for (PersonAttribute personAttribute : activeAttributes) {
			if (personAttribute.getValue().startsWith("09") || personAttribute.getValue().startsWith("+251")) {
				return personAttribute.getValue();
			}
		}
		return "";
	}
}
