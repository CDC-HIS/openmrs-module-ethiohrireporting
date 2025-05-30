package org.openmrs.module.ohrireports.datasetevaluator.linelist.missedAppointments;

import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.openmrs.module.ohrireports.constants.Identifiers;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.MissedAppointmentDatasetDefinition;
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

import static org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions.ART_START_DATE;
import static org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions.ARV_ADHERENCE;

@Handler(supports = { MissedAppointmentDatasetDefinition.class })
public class MissedAppointmentDatasetEvaluator implements DataSetEvaluator {
	
	@Autowired
	MissedAppointmentQuery appointmentQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		MissedAppointmentDatasetDefinition _datasetDefinition = (MissedAppointmentDatasetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(_datasetDefinition, evalContext);
		
		if (_datasetDefinition.getEndDate() == null) {
			_datasetDefinition.setEndDate(new Date());
		}
		
		appointmentQuery.generateReport(_datasetDefinition.getEndDate());
		
		HashMap<Integer, Object> mrnIdentifierHashMap = appointmentQuery.getIdentifier(appointmentQuery.getBaseCohort(),
		    Identifiers.MRN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> uaIdentifierHashMap = appointmentQuery.getIdentifier(appointmentQuery.getBaseCohort(),
		    Identifiers.UAN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> artStartDictionary = appointmentQuery.getObsValueDate(appointmentQuery.getEncounter(),
		    ART_START_DATE, appointmentQuery.getBaseCohort());
		HashMap<Integer, Object> adherenceHashmap = appointmentQuery.getByResult(ARV_ADHERENCE,
		    appointmentQuery.getBaseCohort(), appointmentQuery.getEncounter());
		HashMap<Integer, Object> regimentHashmap = appointmentQuery.getByResult(FollowUpConceptQuestions.REGIMEN,
		    appointmentQuery.getBaseCohort(), appointmentQuery.getEncounter());
		HashMap<Integer, Object> arvDoseHashmap = appointmentQuery.getByResult(
		    FollowUpConceptQuestions.ARV_DISPENSED_IN_DAYS, appointmentQuery.getBaseCohort(),
		    appointmentQuery.getEncounter());
		HashMap<Integer, Object> followUpDate = appointmentQuery.getObsValueDate(appointmentQuery.getEncounter(),
		    FollowUpConceptQuestions.FOLLOW_UP_DATE, appointmentQuery.getBaseCohort());
		HashMap<Integer, Object> followUpStatus = appointmentQuery.getFollowUpStatus(appointmentQuery.getEncounter(),
		    appointmentQuery.getBaseCohort());
		HashMap<Integer, Object> nextVisitDate = appointmentQuery.getObsValueDate(appointmentQuery.getEncounter(),
		    FollowUpConceptQuestions.NEXT_VISIT_DATE, appointmentQuery.getBaseCohort());
		HashMap<Integer, Object> lastCurrDate = appointmentQuery.getObsValueDate(appointmentQuery.getEncounter(),
		    FollowUpConceptQuestions.TREATMENT_END_DATE, appointmentQuery.getBaseCohort());
		DataSetRow row;
		
		List<Person> personList = LineListUtilities.sortPatientByName(appointmentQuery.getPersons(appointmentQuery
		        .getBaseCohort()));
		if (!personList.isEmpty()) {
			
			row = new DataSetRow();
			
			row.addColumnValue(new DataSetColumn("#", "#", Integer.class), "TOTAL");
			row.addColumnValue(new DataSetColumn("GUID", "GUID", Integer.class), personList.size());
			
			dataSet.addRow(row);
		} else {
			dataSet.addRow(LineListUtilities.buildEmptyRow(Arrays.asList("#", "GUID", "Patient Name", "MRN", "UAN", "Age",
			    "Sex", "ART Start Date", "Last Follow-up Date E.C", "Last Appointment Date E.C", "No. of Missed Days",
			    "Tracing Status", "Last Follow-up Status", "Last Regimen", "Last ARV Dose", "Adherence",
			    "Last TX_CURR Date E.C", "Mobile#", "Zone", "Woreda")));
		}
		
		int missedDate = 0;
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
			row.addColumnValue(new DataSetColumn("ART Start Date", "ART Start Date", String.class),
			    artStartDictionary.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Last Follow-up DateEth", "Last Follow-up Date E.C", String.class),
			    appointmentQuery.getEthiopianDate((Date) followUpDate.get(person.getPersonId())));
			row.addColumnValue(new DataSetColumn("Last Appointment Date Eth", "Last Appointment Date E.C", String.class),
			    appointmentQuery.getEthiopianDate((Date) nextVisitDate.get(person.getPersonId())));
			
			missedDate = getDaysDiff((Date) nextVisitDate.get(person.getPersonId()), _datasetDefinition.getEndDate());
			
			row.addColumnValue(new DataSetColumn("No. of Missed Days", "No. of Missed Days", String.class), missedDate);
			row.addColumnValue(new DataSetColumn("tracing-status", "Tracing Status", String.class),
			    getTracingStatus(missedDate));
			row.addColumnValue(new DataSetColumn("Last Follow-up Status", "Last Follow-up Status", String.class),
			    followUpStatus.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Last Regimen", "Last Regimen", String.class),
			    regimentHashmap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Last ARV Dose", "Last ARV Dose", String.class),
			    arvDoseHashmap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Adherence", "Adherence", String.class),
			    adherenceHashmap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Last TX_CURR Date Eth", "Last TX_CURR Date E.C", String.class),
			    appointmentQuery.getEthiopianDate((Date) lastCurrDate.get(person.getPersonId())));
			row.addColumnValue(new DataSetColumn("Mobile#", "Mobile#", String.class), getPhone(person.getActiveAttributes()));
			row.addColumnValue(new DataSetColumn("Zone", "Zone", String.class), person.getPersonAddress()
			        .getCountyDistrict());
			row.addColumnValue(new DataSetColumn("Woreda", "Woreda", String.class), person.getPersonAddress()
			        .getCityVillage());
			dataSet.addRow(row);
		}
		
		return dataSet;
	}
	
	private String getTracingStatus(int missedDays) {
		if (missedDays <= 30) {
			return "Missed Appointment";
		} else if (missedDays < 60) {
			return "1st Lost";
		} else if (missedDays < 90) {
			return "2nd Lost ";
		} else {
			return "Dropped";
		}
	}
	
	private String getPhone(List<PersonAttribute> activeAttributes) {
		return LineListUtilities.getPhone(activeAttributes);
	}
	
	private int getDaysDiff(Date appointmentDate, Date toDate) {
		if (!Objects.isNull(appointmentDate)) {
			Calendar appointCalendar = Calendar.getInstance();
			appointCalendar.setTime(appointmentDate);
			Calendar toDCal = Calendar.getInstance();
			toDCal.setTime(toDate);
			int yearInDays = (toDCal.get(Calendar.YEAR) - appointCalendar.get(Calendar.YEAR)) * 365;
			int dayInMonth = toDCal.getActualMaximum(Calendar.DAY_OF_MONTH);
			int monthInDays = (toDCal.get(Calendar.MONTH) - appointCalendar.get(Calendar.MONTH)) * dayInMonth;
			
			int days = toDCal.get(Calendar.DATE) - appointCalendar.get(Calendar.DATE);
			return yearInDays + monthInDays + days;
		} else {
			return 0;
		}
	}
}
