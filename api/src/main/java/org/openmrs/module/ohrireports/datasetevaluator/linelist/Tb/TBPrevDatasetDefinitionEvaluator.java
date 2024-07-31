package org.openmrs.module.ohrireports.datasetevaluator.linelist.Tb;

import java.util.*;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.TBQuery;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.openmrs.module.ohrireports.constants.*;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.TBPrevDatasetDefinition;
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

@Handler(supports = { TBPrevDatasetDefinition.class })
public class TBPrevDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	private PatientQueryService patientQuery;
	
	@Autowired
	private TBQuery tbQuery;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	@Autowired
	private TbQueryLineList tbQueryLineList;
	
	private TBPrevDatasetDefinition hdsd;
	
	HashMap<Integer, Object> mrnIdentifierHashMap;
	
	HashMap<Integer, Object> uanIdentifierHashMap;
	
	private List<Integer> lastFollowUp;
	
	private HashMap<Integer, Object> artStartDictionary, followUpDate, followUpStatus, arvDoseDay, tptDiscontinuedDate,
	        tptType, tptEndDate, tptStartDate, tpDosDayType, tptAdherence, hiveConfirmedDate, nextVisitDate, alternateType,
	        eligibleStatus, tptAlternateDoseDay, finalFollowUPStatus, latestFollowUpDate, latestRegimen, latestArvDoseDay,
	        latestAdherence;
	
	private List<Integer> baseTPTStartDateEncounters;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		hdsd = (TBPrevDatasetDefinition) dataSetDefinition;
		
		SimpleDataSet data = new SimpleDataSet(dataSetDefinition, evalContext);
		
		if (hdsd.getEndDate() == null) {
			hdsd.setEndDate(new Date());
		}
		
		// Check start date and end date are valid
		// If start date is greater than end date
		if (hdsd.getStartDate() != null && hdsd.getEndDate() != null && hdsd.getStartDate().compareTo(hdsd.getEndDate()) > 0) {
			//throw new EvaluationException("Start date cannot be greater than end date");
			DataSetRow row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("Error", "Error", Integer.class),
			    "Report start date cannot be after report end date");
			data.addRow(row);
			return data;
		}
		
		patientQuery = Context.getService(PatientQueryService.class);
		lastFollowUp = encounterQuery.getLatestDateByFollowUpDate(null, new Date());
		
		if (hdsd.getTptStatus().equals("start")) {
			baseTPTStartDateEncounters = encounterQuery.getEncountersByMaxObsDate(
			    Collections.singletonList(FollowUpConceptQuestions.TPT_START_DATE), hdsd.getStartDate(), hdsd.getEndDate());
			
		} else {
			baseTPTStartDateEncounters = encounterQuery.getEncountersByMaxObsDate(
			    Collections.singletonList(FollowUpConceptQuestions.TPT_COMPLETED_DATE), hdsd.getStartDate(),
			    hdsd.getEndDate());
			
		}
		
		Cohort cohort = tbQuery.getTPTStartedCohort(null, baseTPTStartDateEncounters, "");
		loadColumnDictionary(cohort);
		List<Person> persons = LineListUtilities.sortPatientByName(patientQuery.getPersons(cohort));
		
		DataSetRow row;
		
		if (!persons.isEmpty()) {
			
			row = new DataSetRow();
			
			row.addColumnValue(new DataSetColumn("#", "#", Integer.class), "TOTAL");
			row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", Integer.class), persons.size());
			
			data.addRow(row);
		} else {
			data.addRow(LineListUtilities.buildEmptyRow(Arrays.asList("#", "Patient Name", "MRN", "UAN", "Age", "Sex",
			    "HIV Confirmed Date in E.C.", "ART Start Date in E.C", "TPT Start Date in E.C.",
			    "TPT Completed Date in E.C.", "TPT Discontinued Date in E.C.", "TPT Type", "TPT Follow-up Status",
			    "TPT Dispensed Dose", "TPT Adherence", "Latest Follow-up Date in E.C", "Latest Follow-up Status",
			    "Latest Regimen", "Latest ARV Dose Days", "Latest Adherence", "Next Visit Date in E.C.",
			    "Treatment End Date in E.C.", "TI?")));
		}
		int i = 1;
		for (Person person : persons) {
			
			row = new DataSetRow();
			
			Date artStartDate = tbQueryLineList.getDate(artStartDictionary.get(person.getPersonId()));
			Date _followUpDate = tbQueryLineList.getDate(followUpDate.get(person.getPersonId()));
			Date _latestFollowupDate = tbQueryLineList.getDate(latestFollowUpDate.get(person.getPersonId()));
			Date hivDate = tbQueryLineList.getDate(hiveConfirmedDate.get(person.getPersonId()));
			Date tptSDate = tbQueryLineList.getDate(tptStartDate.get(person.getPersonId()));
			Date tptEnDate = tbQueryLineList.getDate(tptEndDate.get(person.getPersonId()));
			Date _tptDiscontinuedDate = tbQueryLineList.getDate(tptDiscontinuedDate.get(person.getPersonId()));
			Date visitDate = tbQueryLineList.getDate(nextVisitDate.get(person.getPersonId()));
			
			row.addColumnValue(new DataSetColumn("#", "#", Integer.class), i++);
			row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", String.class), person.getNames());
			row.addColumnValue(new DataSetColumn("MRN", "MRN", Integer.class),
			    mrnIdentifierHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("UAN", "UAN", Integer.class),
			    uanIdentifierHashMap.get(person.getPersonId()));
			
			row.addColumnValue(new DataSetColumn("Age", "Age", Integer.class), person.getAge(hdsd.getEndDate()));
			row.addColumnValue(new DataSetColumn("Sex", "Sex", Integer.class), person.getGender());
			
			row.addColumnValue(new DataSetColumn("HIV Confirmed Date in E.C.", "HIV Confirmed Date in E.C.", String.class),
			    tbQueryLineList.getEthiopianDate(hivDate));
			row.addColumnValue(new DataSetColumn("ART Start Date in E.C", "ART Start Date in E.C", String.class),
			    tbQueryLineList.getEthiopianDate(artStartDate));
			row.addColumnValue(new DataSetColumn("TPT Start Date in E.C.", "TPT Start Date in E.C.", String.class),
			    tbQueryLineList.getEthiopianDate(tptSDate));
			row.addColumnValue(new DataSetColumn("TPT Completed Date in E.C.", "TPT Completed Date in E.C.", String.class),
			    tbQueryLineList.getEthiopianDate(tptEnDate));
			row.addColumnValue(new DataSetColumn("TPT Discontinued Date in E.C.", "TPT Discontinued Date in E.C.",
			        String.class), tbQueryLineList.getEthiopianDate(_tptDiscontinuedDate));
			Object alternate = alternateType.get(person.getPersonId());
			row.addColumnValue(new DataSetColumn("TPT Type", "TPT Type", String.class),
			    Objects.isNull(alternate) ? tptType.get(person.getPersonId()) : alternate);
			row.addColumnValue(new DataSetColumn("TPT Follow-up Status", "TPT Follow-up Status", String.class),
			    followUpStatus.get(person.getPersonId()));
			row.addColumnValue(
			    new DataSetColumn("TPT Dispensed Dose", "TPT Dispensed Dose", String.class),
			    Objects.isNull(alternate) ? tpDosDayType.get(person.getPersonId()) : tptAlternateDoseDay.get(person
			            .getPersonId()));
			row.addColumnValue(new DataSetColumn("TPT Adherence", "TPT Adherence", String.class),
			    tptAdherence.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Latest Follow-up Date in E.C", "Latest Follow-up Date in E.C",
			        String.class), tbQueryLineList.getEthiopianDate(_latestFollowupDate));
			row.addColumnValue(new DataSetColumn("Latest Follow-up Status", "Latest Follow-up Status", String.class),
			    finalFollowUPStatus.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Latest Regimen", "Latest Regimen", String.class),
			    latestRegimen.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Latest ARV Dose Days ", "Latest ARV Dose Days ", String.class),
			    latestArvDoseDay.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Latest Adherence", "Latest Adherence", String.class),
			    latestAdherence.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Next Visit Date in E.C.", "Next Visit Date in E.C.", String.class),
			    tbQueryLineList.getEthiopianDate(visitDate));
			row.addColumnValue(new DataSetColumn("TI?", "TI?", String.class), eligibleStatus.get(person.getPersonId()));
			data.addRow(row);
			
		}
		return data;
		
	}
	
	private void loadColumnDictionary(Cohort cohort) {
		
		artStartDictionary = tbQueryLineList.getObsValueDate(baseTPTStartDateEncounters,
		    FollowUpConceptQuestions.ART_START_DATE, cohort);
		mrnIdentifierHashMap = tbQueryLineList.getIdentifier(cohort, Identifiers.MRN_PATIENT_IDENTIFIERS);
		uanIdentifierHashMap = tbQueryLineList.getIdentifier(cohort, Identifiers.UAN_PATIENT_IDENTIFIERS);
		followUpDate = tbQueryLineList.getObsValueDate(baseTPTStartDateEncounters, FollowUpConceptQuestions.FOLLOW_UP_DATE,
		    cohort);
		followUpStatus = tbQueryLineList.getFollowUpStatus(baseTPTStartDateEncounters, cohort);
		arvDoseDay = tbQueryLineList.getByResult(FollowUpConceptQuestions.ARV_DISPENSED_IN_DAYS, cohort,
		    baseTPTStartDateEncounters);
		tptDiscontinuedDate = tbQueryLineList.getObsValueDate(baseTPTStartDateEncounters,
		    FollowUpConceptQuestions.TPT_DISCONTINUED_DATE, cohort);
		tptType = tbQueryLineList.getByResult(FollowUpConceptQuestions.TPT_TYPE, cohort, baseTPTStartDateEncounters);
		tptEndDate = tbQueryLineList.getObsValueDate(baseTPTStartDateEncounters,
		    FollowUpConceptQuestions.TPT_COMPLETED_DATE, cohort);
		hiveConfirmedDate = tbQueryLineList.getObsValueDate(null, IntakeAConceptQuestions.HIV_CONFIRMED_DATE, cohort,
		    EncounterType.INTAKE_A_ENCOUNTER_TYPE);
		tptStartDate = tbQueryLineList.getObsValueDate(baseTPTStartDateEncounters, FollowUpConceptQuestions.TPT_START_DATE,
		    cohort);
		tpDosDayType = tbQueryLineList.getByResult(FollowUpConceptQuestions.TPT_DOSE_DAY_TYPE_INH, cohort,
		    baseTPTStartDateEncounters);
		tptAdherence = tbQueryLineList.getByResult(FollowUpConceptQuestions.TPT_ADHERENCE, cohort,
		    baseTPTStartDateEncounters);
		nextVisitDate = tbQueryLineList.getObsValueDate(baseTPTStartDateEncounters,
		    FollowUpConceptQuestions.NEXT_VISIT_DATE, cohort);
		eligibleStatus = tbQueryLineList.getByResult(FollowUpConceptQuestions.REASON_FOR_ART_ELIGIBILITY, cohort,
		    baseTPTStartDateEncounters);
		alternateType = tbQueryLineList.getByResult(FollowUpConceptQuestions.TPT_ALTERNATE_TYPE, cohort,
		    baseTPTStartDateEncounters);
		tptAlternateDoseDay = tbQueryLineList.getByResult(FollowUpConceptQuestions.TPT_DOSE_DAY_TYPE_ALTERNATE, cohort,
		    baseTPTStartDateEncounters);
		finalFollowUPStatus = tbQueryLineList.getByResult(FollowUpConceptQuestions.FOLLOW_UP_STATUS, cohort, lastFollowUp);
		latestFollowUpDate = tbQueryLineList.getObsValueDate(lastFollowUp, FollowUpConceptQuestions.FOLLOW_UP_DATE, cohort);
		latestRegimen = tbQueryLineList.getByResult(FollowUpConceptQuestions.REGIMEN, cohort, lastFollowUp);
		latestArvDoseDay = tbQueryLineList.getByResult(FollowUpConceptQuestions.ARV_DISPENSED_IN_DAYS, cohort, lastFollowUp);
		latestAdherence = tbQueryLineList.getByResult(FollowUpConceptQuestions.ARV_ADHERENCE, cohort, lastFollowUp);
		
	}
	
}
