package org.openmrs.module.ohrireports.datasetevaluator.linelist.missedAppointments;

import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.cohorts.util.EthiOhriUtil;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.MissedAppointmentDatasetDefinition;
import org.openmrs.module.ohrireports.helper.EthiopianDate;
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
		appointmentQuery.generateReport(dsd.getStartDate(), dsd.getEndDate());
		
		HashMap<Integer, Object> mrnIdentifierHashMap = appointmentQuery.getIdentifier(appointmentQuery.getBaseCohort(),
		    MRN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> uaIdentifierHashMap = appointmentQuery.getIdentifier(appointmentQuery.getBaseCohort(),
		    UAN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> artStartDictionary = appointmentQuery.getArtStartDate(appointmentQuery.getBaseCohort(),
		    null, dsd.getEndDate());
		HashMap<Integer, Object> adherenceHashmap = appointmentQuery.getByResult(ARV_ADHERENCE,
		    appointmentQuery.getBaseCohort(), appointmentQuery.getEncounter());
		HashMap<Integer, Object> followUpDate = appointmentQuery.getObsValueDate(appointmentQuery.getEncounter(),
		    FOLLOW_UP_DATE, appointmentQuery.getBaseCohort());
		HashMap<Integer, Object> followUpStatus = appointmentQuery.getFollowUpStatus(appointmentQuery.getEncounter(),
		    appointmentQuery.getBaseCohort());
		HashMap<Integer, Object> nextVisitDate = appointmentQuery.getObsValueDate(appointmentQuery.getEncounter(),
		    NEXT_VISIT_DATE, appointmentQuery.getBaseCohort());
		
		DataSetRow row = new DataSetRow();
		List<Person> personList = appointmentQuery.getPersons(appointmentQuery.getBaseCohort());
		for (Person person : personList) {
			row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("FullName", "FullName", String.class), person.getNames());
			row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class), mrnIdentifierHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("UAN", "UAN", String.class), uaIdentifierHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Age", "Age", String.class), person.getAge());
			row.addColumnValue(new DataSetColumn("Sex", "Sex", String.class), person.getGender());
			row.addColumnValue(new DataSetColumn("Mobile", "Mobile", String.class), getPhone(person.getActiveAttributes()));
			row.addColumnValue(new DataSetColumn("ARTStartDate", "ARTStartDate", String.class),
			    artStartDictionary.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("followUpDate", "followUpDate", String.class),
			    followUpDate.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("followUpDateEth", "followUp Date Eth", String.class),
			    appointmentQuery.getEthiopianDate((Date) followUpDate.get(person.getPersonId())));
			
			row.addColumnValue(new DataSetColumn("Appointment missed date", "Appointment missed date", String.class),
			    nextVisitDate.get(person.getPersonId()));
			
			row.addColumnValue(
			    new DataSetColumn("Appointment missed date Eth", "Appointment missed date Eth", String.class),
			    appointmentQuery.getEthiopianDate((Date) nextVisitDate.get(person.getPersonId())));
			row.addColumnValue(new DataSetColumn("No. of Missed Days", "No. of Missed Days", String.class),
			    getDaysDiff((Date) nextVisitDate.get(person.getPersonId()), dsd.getEndDate()));
			row.addColumnValue(new DataSetColumn("followUpStatus", "followUpStatus", String.class),
			    followUpStatus.get(person.getPersonId()));
			
			row.addColumnValue(new DataSetColumn("Adherance", "Adherance", String.class),
			    adherenceHashmap.get(person.getPersonId()));
			
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
