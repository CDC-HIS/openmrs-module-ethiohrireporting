package org.openmrs.module.ohrireports.datasetevaluator.linelist.Tb;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.NEXT_VISIT_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TPT_DOSE_DAY_TYPE_ALTERNATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TPT_TYPE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HIV_CONFIRMED_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.FOLLOW_UP_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.FOLLOW_UP_STATUS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.ART_START_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.ARV_DISPENSED_IN_DAYS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TPT_DOSE_DAY_TYPE_INH;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.REASON_FOR_ART_ELIGIBILITY;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.MRN_PATIENT_IDENTIFIERS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TPT_DISCONTINUED_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TPT_ALTERNATE_TYPE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TPT_START_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TPT_ADHERENCE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TPT_COMPLETED_DATE;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.TBQuery;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.TBPrevDatasetDefinition;
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
	
	private List<Integer> lastFollowUp;
	
	private HashMap<Integer, Object> artStartDictionary, followUpDate, followUpStatus, arvDoseDay, tptDiscontinuedDate,
	        tptType, tptEndDate, tptStartDate, tpDosDayType, tptAdherence, hiveConfirmedDate, nextVisitDate, alternateType,
	        eligibleStatus, tptAlternateDoseDay, finalFollowUPStatus;
	
	private List<Integer> baseTPTStartDateEncounters;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		hdsd = (TBPrevDatasetDefinition) dataSetDefinition;
		
		SimpleDataSet data = new SimpleDataSet(dataSetDefinition, evalContext);
		patientQuery = Context.getService(PatientQueryService.class);
		lastFollowUp = encounterQuery.getLatestDateByFollowUpDate(hdsd.getEndDate());
		if (hdsd.getTptStatus().equals("start")) {
			baseTPTStartDateEncounters = encounterQuery.getEncounters(Arrays.asList(TPT_START_DATE), hdsd.getStartDate(),
			    hdsd.getEndDate());
			
		} else {
			baseTPTStartDateEncounters = encounterQuery.getEncounters(Arrays.asList(TPT_COMPLETED_DATE),
			    hdsd.getStartDate(), hdsd.getEndDate());
			
		}
		
		Cohort cohort = tbQuery.getTPTStartedCohort(null, baseTPTStartDateEncounters, "");
		loadColumnDictionary(cohort);
		List<Person> persons = patientQuery.getPersons(cohort);
		
		DataSetRow row;
		
		if (persons.size() > 0) {
			
			row = new DataSetRow();
			
			row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class), "TOTAL");
			row.addColumnValue(new DataSetColumn("Name", "Name", Integer.class), persons.size());
			
			data.addRow(row);
		}
		
		for (Person person : persons) {
			
			row = new DataSetRow();
			
			Date artStartDate = tbQueryLineList.getDate(artStartDictionary.get(person.getPersonId()));
			Date _followUpDate = tbQueryLineList.getDate(followUpDate.get(person.getPersonId()));
			Date hivDate = tbQueryLineList.getDate(hiveConfirmedDate.get(person.getPersonId()));
			Date tptSDate = tbQueryLineList.getDate(tptStartDate.get(person.getPersonId()));
			Date tptEnDate = tbQueryLineList.getDate(tptEndDate.get(person.getPersonId()));
			Date _tptDiscontinuedDate = tbQueryLineList.getDate(tptDiscontinuedDate.get(person.getPersonId()));
			Date visitDate = tbQueryLineList.getDate(nextVisitDate.get(person.getPersonId()));
			
			row.addColumnValue(new DataSetColumn("MRN", "MRN", Integer.class),
			    mrnIdentifierHashMap.get(person.getPersonId()));
			
			row.addColumnValue(new DataSetColumn("Name", "Name", String.class), person.getNames());
			row.addColumnValue(new DataSetColumn("Age", "Age", Integer.class), person.getAge(hdsd.getEndDate()));
			row.addColumnValue(new DataSetColumn("Gender", "Gender", Integer.class), person.getGender());
			
			row.addColumnValue(new DataSetColumn("followUpDate", "Follow Up Date", Date.class), _followUpDate);
			row.addColumnValue(new DataSetColumn("followUpDateEth", "Follow Up Date ETH", String.class),
			    tbQueryLineList.getEthiopianDate(_followUpDate));
			
			row.addColumnValue(new DataSetColumn("hivConfirmed", "Hiv Confirmed Date", Date.class), hivDate);
			row.addColumnValue(new DataSetColumn("hivConfirmedEth", "Hiv Confirmed Date ETH", String.class),
			    tbQueryLineList.getEthiopianDate(hivDate));
			
			row.addColumnValue(new DataSetColumn("ArtStartDate", "Art Start Date", Date.class), artStartDate);
			row.addColumnValue(new DataSetColumn("ArtStartDateEth", "Art Start Date ETH", String.class),
			    tbQueryLineList.getEthiopianDate(artStartDate));
			
			row.addColumnValue(new DataSetColumn("tptStartDate", "TPT Start  Date", Date.class), tptSDate);
			row.addColumnValue(new DataSetColumn("tptStartDateEth", "TPT Start  Date ETH", String.class),
			    tbQueryLineList.getEthiopianDate(tptSDate));
			
			row.addColumnValue(new DataSetColumn("tptend", "TPT end  Date", Date.class), tptEnDate);
			row.addColumnValue(new DataSetColumn("tptendEth", "TPT end  Date ETH", String.class),
			    tbQueryLineList.getEthiopianDate(tptEnDate));
			
			row.addColumnValue(new DataSetColumn("tptdiscontinued", "TPT Discontinued Date", Date.class),
			    _tptDiscontinuedDate);
			row.addColumnValue(new DataSetColumn("tptdiscontinuedEth", "TPT Discontinued Date ETH", String.class),
			    tbQueryLineList.getEthiopianDate(_tptDiscontinuedDate));
			
			Object alternate = alternateType.get(person.getPersonId());
			row.addColumnValue(new DataSetColumn("tpttype", "TPT Type", String.class),
			    Objects.isNull(alternate) ? tptType.get(person.getPersonId()) : alternate);
			
			row.addColumnValue(
			    new DataSetColumn("tptDose", "TPT Dose Days", String.class),
			    Objects.isNull(alternate) ? tpDosDayType.get(person.getPersonId()) : tptAlternateDoseDay.get(person
			            .getPersonId()));
			
			row.addColumnValue(new DataSetColumn("tptAdherence", "TPT Adherence", String.class),
			    tptAdherence.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("followUpstatus", "Follow Up Status", String.class),
			    followUpStatus.get(person.getPersonId()));
			
			row.addColumnValue(new DataSetColumn("arvDoseDay", "ARV Dose Day", String.class),
			    arvDoseDay.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("tptEligibility", "TI Status", String.class),
			    eligibleStatus.get(person.getPersonId()));
			
			row.addColumnValue(new DataSetColumn("visitDate", "Next Visit Date", Date.class), visitDate);
			row.addColumnValue(new DataSetColumn("visitDateEth", "Next Visit Date ETH", String.class),
			    tbQueryLineList.getEthiopianDate(visitDate));
			;
			row.addColumnValue(new DataSetColumn("finalFollowUpStatus", "Final Follow-up Status", String.class),
			    finalFollowUPStatus.get(person.getPersonId()));
			data.addRow(row);
			
		}
		return data;
		
	}
	
	private void loadColumnDictionary(Cohort cohort) {
		
		artStartDictionary = tbQueryLineList.getObsValueDate(baseTPTStartDateEncounters, ART_START_DATE, cohort);
		mrnIdentifierHashMap = tbQueryLineList.getIdentifier(cohort, MRN_PATIENT_IDENTIFIERS);
		followUpDate = tbQueryLineList.getObsValueDate(baseTPTStartDateEncounters, FOLLOW_UP_DATE, cohort);
		followUpStatus = tbQueryLineList.getFollowUpStatus(baseTPTStartDateEncounters, cohort);
		arvDoseDay = tbQueryLineList.getByResult(ARV_DISPENSED_IN_DAYS, cohort, baseTPTStartDateEncounters);
		tptDiscontinuedDate = tbQueryLineList.getObsValueDate(baseTPTStartDateEncounters, TPT_DISCONTINUED_DATE, cohort);
		tptType = tbQueryLineList.getByResult(TPT_TYPE, cohort, baseTPTStartDateEncounters);
		tptEndDate = tbQueryLineList.getObsValueDate(baseTPTStartDateEncounters, TPT_COMPLETED_DATE, cohort);
		hiveConfirmedDate = tbQueryLineList.getObsValueDate(baseTPTStartDateEncounters, HIV_CONFIRMED_DATE, cohort);
		tptStartDate = tbQueryLineList.getObsValueDate(baseTPTStartDateEncounters, TPT_START_DATE, cohort);
		tpDosDayType = tbQueryLineList.getByResult(TPT_DOSE_DAY_TYPE_INH, cohort, baseTPTStartDateEncounters);
		tptAdherence = tbQueryLineList.getByResult(TPT_ADHERENCE, cohort, baseTPTStartDateEncounters);
		nextVisitDate = tbQueryLineList.getObsValueDate(baseTPTStartDateEncounters, NEXT_VISIT_DATE, cohort);
		eligibleStatus = tbQueryLineList.getByResult(REASON_FOR_ART_ELIGIBILITY, cohort, baseTPTStartDateEncounters);
		alternateType = tbQueryLineList.getByResult(TPT_ALTERNATE_TYPE, cohort, baseTPTStartDateEncounters);
		tptAlternateDoseDay = tbQueryLineList.getByResult(TPT_DOSE_DAY_TYPE_ALTERNATE, cohort, baseTPTStartDateEncounters);
		finalFollowUPStatus = tbQueryLineList.getByResult(FOLLOW_UP_STATUS, cohort, lastFollowUp);
		
	}
	
}
