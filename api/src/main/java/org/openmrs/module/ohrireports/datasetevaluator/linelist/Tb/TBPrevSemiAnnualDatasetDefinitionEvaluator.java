package org.openmrs.module.ohrireports.datasetevaluator.linelist.Tb;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.TBQuery;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.openmrs.module.ohrireports.constants.EncounterType;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.openmrs.module.ohrireports.constants.Identifiers;
import org.openmrs.module.ohrireports.constants.IntakeAConceptQuestions;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.TBPrevSemiAnnualDatasetDefinition;
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

@Handler(supports = { TBPrevSemiAnnualDatasetDefinition.class })
public class TBPrevSemiAnnualDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	private PatientQueryService patientQuery;
	
	@Autowired
	private TBQuery tbQuery;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	@Autowired
	private TbQueryLineList tbQueryLineList;
	
	private TBPrevSemiAnnualDatasetDefinition hdsd;
	
	HashMap<Integer, Object> mrnIdentifierHashMap;
	
	HashMap<Integer, Object> uanIdentifierHashMap;
	
	private List<Integer> lastFollowUp;
	
	private HashMap<Integer, Object> artStartDictionary, followUpDate, followUpStatus, arvDoseDay, tptDiscontinuedDate,
	        tptType, tptEndDate, tptFollowUp, tptStartDate, tpDosDayType, tptAdherence, hiveConfirmedDate, nextVisitDate,
	        eligibleStatus, finalFollowUPStatus, latestFollowUpDate, latestRegimen, latestArvDoseDay, latestAdherence;
	
	private List<Integer> baseTPTEncounters;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		hdsd = (TBPrevSemiAnnualDatasetDefinition) dataSetDefinition;
		
		SimpleDataSet data = new SimpleDataSet(dataSetDefinition, evalContext);
		
		if (hdsd.getEndDate() == null) {
			hdsd.setEndDate(new Date());
		}
		
		// Check start date and end date are valid
		// If start date is greater than end date
		if (hdsd.getEndDate() == null) {
			//throw new EvaluationException("Start date cannot be greater than end date");
			DataSetRow row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("Error", "Error", Integer.class),
			    "Report start date cannot be after report end date");
			data.addRow(row);
			return data;
		}
		
		patientQuery = Context.getService(PatientQueryService.class);
		lastFollowUp = encounterQuery.getLatestDateByFollowUpDate(null, new Date());
		Date startDate = getPrevSixMonth();
		baseTPTEncounters = encounterQuery.getEncounters(Collections.singletonList(FollowUpConceptQuestions.TPT_START_DATE),
		    startDate, hdsd.getStartDate(), EncounterType.HTS_FOLLOW_UP_ENCOUNTER_TYPE);
		Cohort baseCohort = tbQuery.getCohort(baseTPTEncounters);
		
		if (hdsd.getTptStatus().equalsIgnoreCase("Numerator")) {
			baseTPTEncounters = encounterQuery.getEncounters(
			    Collections.singletonList(FollowUpConceptQuestions.TPT_COMPLETED_DATE), startDate, hdsd.getEndDate(),
			    baseCohort, EncounterType.HTS_FOLLOW_UP_ENCOUNTER_TYPE);
			baseCohort = tbQuery.getCohort(baseTPTEncounters);
		}
		
		loadColumnDictionary(baseCohort);
		List<Person> persons = LineListUtilities.sortPatientByName(patientQuery.getPersons(baseCohort));
		
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
			
			row.addColumnValue(new DataSetColumn("TPT Type", "TPT Type", String.class), tptType.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("TPT Follow-up Status", "TPT Follow-up Status", String.class),
			    tptFollowUp.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("TPT Dispensed Dose", "TPT Dispensed Dose", String.class),
			    tpDosDayType.get(person.getPersonId()));
			
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
	
	private Date getPrevSixMonth() {
		Calendar subSixMonth = Calendar.getInstance();
		subSixMonth.setTime(hdsd.getStartDate());
		subSixMonth.add(Calendar.MONTH, -6);
		return subSixMonth.getTime();
	}
	
	private void loadColumnDictionary(Cohort cohort) {
		
		artStartDictionary = tbQueryLineList.getObsValueDate(baseTPTEncounters, FollowUpConceptQuestions.ART_START_DATE,
		    cohort);
		mrnIdentifierHashMap = tbQueryLineList.getIdentifier(cohort, Identifiers.MRN_PATIENT_IDENTIFIERS);
		uanIdentifierHashMap = tbQueryLineList.getIdentifier(cohort, Identifiers.UAN_PATIENT_IDENTIFIERS);
		followUpDate = tbQueryLineList.getObsValueDate(baseTPTEncounters, FollowUpConceptQuestions.FOLLOW_UP_DATE, cohort);
		followUpStatus = tbQueryLineList.getFollowUpStatus(baseTPTEncounters, cohort);
		arvDoseDay = tbQueryLineList.getByResult(FollowUpConceptQuestions.ARV_DISPENSED_IN_DAYS, cohort, baseTPTEncounters);
		tptDiscontinuedDate = tbQueryLineList.getObsValueDate(baseTPTEncounters,
		    FollowUpConceptQuestions.TPT_DISCONTINUED_DATE, cohort);
		tptType = tbQueryLineList.getByResult(FollowUpConceptQuestions.TPT_TYPE, cohort, baseTPTEncounters);
		tptEndDate = tbQueryLineList.getObsValueDate(baseTPTEncounters, FollowUpConceptQuestions.TPT_COMPLETED_DATE, cohort);
		hiveConfirmedDate = tbQueryLineList.getObsValueDate(null, IntakeAConceptQuestions.HIV_CONFIRMED_DATE, cohort,
		    EncounterType.INTAKE_A_ENCOUNTER_TYPE);
		tptStartDate = tbQueryLineList.getObsValueDate(baseTPTEncounters, FollowUpConceptQuestions.TPT_START_DATE, cohort);
		tptFollowUp = tbQueryLineList.getByResult(FollowUpConceptQuestions.TPT_FOLLOW_UP_STATUS, cohort, baseTPTEncounters);
		tpDosDayType = tbQueryLineList
		        .getByResult(FollowUpConceptQuestions.TPT_DOSE_DAY_TYPE_INH, cohort, baseTPTEncounters);
		tptAdherence = tbQueryLineList.getByResult(FollowUpConceptQuestions.TPT_ADHERENCE, cohort, baseTPTEncounters);
		nextVisitDate = tbQueryLineList.getObsValueDate(baseTPTEncounters, FollowUpConceptQuestions.NEXT_VISIT_DATE, cohort);
		eligibleStatus = tbQueryLineList.getByResult(FollowUpConceptQuestions.REASON_FOR_ART_ELIGIBILITY, cohort,
		    baseTPTEncounters);
		finalFollowUPStatus = tbQueryLineList.getByResult(FollowUpConceptQuestions.FOLLOW_UP_STATUS, cohort, lastFollowUp);
		latestFollowUpDate = tbQueryLineList.getObsValueDate(lastFollowUp, FollowUpConceptQuestions.FOLLOW_UP_DATE, cohort);
		latestRegimen = tbQueryLineList.getByResult(FollowUpConceptQuestions.REGIMEN, cohort, lastFollowUp);
		latestArvDoseDay = tbQueryLineList.getByResult(FollowUpConceptQuestions.ARV_DISPENSED_IN_DAYS, cohort, lastFollowUp);
		latestAdherence = tbQueryLineList.getByResult(FollowUpConceptQuestions.ARV_ADHERENCE, cohort, lastFollowUp);
		
	}
	
}
