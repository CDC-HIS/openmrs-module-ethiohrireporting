package org.openmrs.module.ohrireports.datasetevaluator.linelist.missedAppointments;

import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.annotation.Handler;
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

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Handler(supports = { MissedAppointmentDatasetDefinition.class })
public class MissedAppointmentDatasetEvaluator implements DataSetEvaluator {
	
	@Autowired
	MissedAppointmentQuery appointmentQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		MissedAppointmentDatasetDefinition dsd = (MissedAppointmentDatasetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(dsd, evalContext);
		appointmentQuery.generateReport(dsd.getEndDate());
		
		if (dsd.getEndDate() == null) {
			dsd.setEndDate(new Date());
		}
		
		HashMap<Integer, Object> mrnIdentifierHashMap = appointmentQuery.getIdentifier(appointmentQuery.getBaseCohort(),
		    MRN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> uaIdentifierHashMap = appointmentQuery.getIdentifier(appointmentQuery.getBaseCohort(),
		    UAN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> artStartDictionary = appointmentQuery.getArtStartDate(appointmentQuery.getBaseCohort(),
		    null, dsd.getEndDate());
		HashMap<Integer, Object> adherenceHashmap = appointmentQuery.getByResult(ARV_ADHERENCE,
		    appointmentQuery.getBaseCohort(), appointmentQuery.getEncounter());
		HashMap<Integer, Object> regimentHashmap = appointmentQuery.getByResult(REGIMEN, appointmentQuery.getBaseCohort(),
		    appointmentQuery.getEncounter());
		HashMap<Integer, Object> arvDoseHashmap = appointmentQuery.getByResult(ARV_DISPENSED_IN_DAYS,
		    appointmentQuery.getBaseCohort(), appointmentQuery.getEncounter());
		HashMap<Integer, Object> followUpDate = appointmentQuery.getObsValueDate(appointmentQuery.getEncounter(),
		    FOLLOW_UP_DATE, appointmentQuery.getBaseCohort());
		HashMap<Integer, Object> followUpStatus = appointmentQuery.getFollowUpStatus(appointmentQuery.getEncounter(),
		    appointmentQuery.getBaseCohort());
		HashMap<Integer, Object> nextVisitDate = appointmentQuery.getObsValueDate(appointmentQuery.getEncounter(),
		    NEXT_VISIT_DATE, appointmentQuery.getBaseCohort());
		HashMap<Integer, Object> lastCurrDate = appointmentQuery.getObsValueDate(appointmentQuery.getEncounter(),
		    TREATMENT_END_DATE, appointmentQuery.getBaseCohort());
		DataSetRow row = new DataSetRow();
		
		List<Person> personList = LineListUtilities.sortPatientByName(appointmentQuery.getPersons(appointmentQuery
		        .getBaseCohort()));
		row.addColumnValue(new DataSetColumn("#", "#", Integer.class), "TOTAL");
		row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", Integer.class), personList.size());
		dataSet.addRow(row);
		int missedDate = 0;
		int i = 1;
		for (Person person : personList) {
			row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("#", "#", Integer.class), i++);
			row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", String.class), person.getNames());
			row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class), mrnIdentifierHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("UAN", "UAN", String.class), uaIdentifierHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Age", "Age", String.class), person.getAge());
			row.addColumnValue(new DataSetColumn("Sex", "Sex", String.class), person.getGender());
			row.addColumnValue(new DataSetColumn("ART Start Date", "ART Start Date", String.class),
			    artStartDictionary.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Last Follow-up DateEth", "Last Follow-up Date Eth", String.class),
			    appointmentQuery.getEthiopianDate((Date) followUpDate.get(person.getPersonId())));
			row.addColumnValue(new DataSetColumn("Last Appointment Date Eth", "Last Appointment Date Eth", String.class),
			    appointmentQuery.getEthiopianDate((Date) nextVisitDate.get(person.getPersonId())));
			missedDate = getDaysDiff((Date) nextVisitDate.get(person.getPersonId()), dsd.getEndDate());
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
			row.addColumnValue(new DataSetColumn("Last TX_CURR Date Eth", "Last TX_CURR Date Eth", String.class),
			    appointmentQuery.getEthiopianDate((Date) lastCurrDate.get(person.getPersonId())));
			row.addColumnValue(new DataSetColumn("Mobile#", "Mobile#", String.class), getPhone(person.getActiveAttributes()));
			
			dataSet.addRow(row);
		}
		
		return dataSet;
	}
	
	private String getTracingStatus(int missedDays) {
		String output = "";
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
		for (PersonAttribute personAttribute : activeAttributes) {
			if (personAttribute.getValue().startsWith("09") || personAttribute.getValue().startsWith("+251")) {
				return personAttribute.getValue();
			}
		}
		return "";
	}
	
	private int getDaysDiff(Date appointmentDate, Date toDate) {
		if (Objects.isNull(appointmentDate)) {
			return 0;
		}
		Calendar apntCal = Calendar.getInstance();
		apntCal.setTime(appointmentDate);
		Calendar toDCal = Calendar.getInstance();
		toDCal.setTime(toDate);
		int yearInDays = (toDCal.get(Calendar.YEAR) - apntCal.get(Calendar.YEAR)) * 365;
		int monthInDays = (toDCal.get(Calendar.MONTH) - apntCal.get(Calendar.MONTH)) * 30;
		
		int days = toDCal.get(Calendar.DATE) - apntCal.get(Calendar.DATE);
		return yearInDays + monthInDays + days;
	}
}
