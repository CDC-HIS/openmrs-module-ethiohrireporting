package org.openmrs.module.ohrireports.datasetevaluator.linelist.Tb;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.TBQuery;
import org.openmrs.module.ohrireports.api.impl.query.TBARTQuery;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.openmrs.module.ohrireports.constants.EncounterType;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.openmrs.module.ohrireports.constants.Identifiers;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.TXTBDataSetDefinition;
import org.openmrs.module.ohrireports.datasetevaluator.linelist.LineListUtilities;
import org.openmrs.module.ohrireports.reports.linelist.TXTBReport;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { TXTBDataSetDefinition.class })
public class TXTBDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	private PatientQueryService patientQuery;
	
	@Autowired
	private TBQuery tbQuery;
	
	@Autowired
	private TBARTQuery tbartQuery;
	
	@Autowired
	private TbQueryLineList tbQueryLineList;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	private Cohort cohort;
	
	private TXTBDataSetDefinition hdsd;
	
	HashMap<Integer, Object> mrnIdentifierHashMap;
	
	HashMap<Integer, Object> uanIdentifierHashMap;
	
	private HashMap<Integer, Object> screenedResultHashMap;
	
	private HashMap<Integer, Object> regimentDictionary;
	
	private HashMap<Integer, Object> artStartDictionary, specimen, geneXpertResult, _LAMResults, OtherDiagnosticTest,
	        OtherDiagResult, followUpStatus, followUpDate, tbTreatmentStatus, tbActiveDate, dispensDayHashMap,
	        adherenceHashMap, pregnancyStatusHashMap, breastfeedingStatusHashMap, tbScreeningDateHashMap,
	        tbScreeningResultsHashMap, tbTxStartDateDictionary, tbTreatmentCompletedDateDictionary,
	        tbTreatmentDiscontinuedDateDictionary;
	
	private List<Integer> followUpEncounters;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		hdsd = (TXTBDataSetDefinition) dataSetDefinition;
		
		SimpleDataSet data = new SimpleDataSet(dataSetDefinition, evalContext);
		
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
		if (Objects.isNull(hdsd.getEndDate()))
			hdsd.setEndDate(new Date());
		
		if (hdsd.getType().equals(TXTBReport.numerator)) {
			
			List<Integer> baseEncountersOfTreatmentStartDate = encounterQuery.getEncountersByMaxObsDate(
			    Collections.singletonList(FollowUpConceptQuestions.TB_TREATMENT_START_DATE), hdsd.getStartDate(),
			    hdsd.getEndDate(), EncounterType.HTS_FOLLOW_UP_ENCOUNTER_TYPE);
			cohort = tbQuery.getCohort(baseEncountersOfTreatmentStartDate);
			
			getTXTbTreatmentAndTBArtDictionary(baseEncountersOfTreatmentStartDate, cohort);
		} else if (hdsd.getType().equals(TXTBReport.denominator)) {
			
			tbQueryLineList.setEncountersByScreenDate(tbQuery.getFollowUpEncounter());
			
			List<Integer> baseEncountersOfTreatmentStartDate = encounterQuery.getEncounters(
			    Collections.singletonList(FollowUpConceptQuestions.TB_SCREENING_DATE), hdsd.getStartDate(),
			    hdsd.getEndDate());
			cohort = tbQuery.getCohort(baseEncountersOfTreatmentStartDate);
			
			getTXTbScreeningDictionary(baseEncountersOfTreatmentStartDate, cohort);
		} else {
			
			cohort = tbartQuery.getCohortByTBTreatmentStartDate(hdsd.getStartDate(), hdsd.getEndDate());
			getTXTbTreatmentAndTBArtDictionary(tbartQuery.getTbArtEncounter(), cohort);
		}
		
		mrnIdentifierHashMap = tbQueryLineList.getIdentifier(cohort, Identifiers.MRN_PATIENT_IDENTIFIERS);
		uanIdentifierHashMap = tbQueryLineList.getIdentifier(cohort, Identifiers.UAN_PATIENT_IDENTIFIERS);
		List<Person> persons = LineListUtilities.sortPatientByName(patientQuery.getPersons(cohort));
		
		DataSetRow row;
		
		if (!persons.isEmpty()) {
			row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("#", "#", String.class), "TOTAL");
			row.addColumnValue(new DataSetColumn("GUID", "GUID", Integer.class), persons.size());
			data.addRow(row);
		}
		if (hdsd.getType().equals(TXTBReport.numerator)) {
			getRowForNumerator(data, persons);
		} else if (hdsd.getType().equals(TXTBReport.denominator)) {
			getRowForDenominator(data, persons);
		} else {
			getRowForActiveTB(data, persons);
		}
		
		return data;
	}
	
	private void getTXTbTreatmentAndTBArtDictionary(List<Integer> encounters, Cohort cohort) {
		artStartDictionary = tbQueryLineList.getObsValueDate(encounters, FollowUpConceptQuestions.ART_START_DATE, cohort);
		regimentDictionary = tbQueryLineList.getRegiment(encounters, cohort);
		followUpDate = tbQueryLineList.getObsValueDate(encounters, FollowUpConceptQuestions.FOLLOW_UP_DATE, cohort);
		followUpStatus = tbQueryLineList.getFollowUpStatus(encounters, cohort);
		dispensDayHashMap = tbQueryLineList.getByResult(FollowUpConceptQuestions.ART_DISPENSE_DOSE, cohort, encounters);
		adherenceHashMap = tbQueryLineList.getByResult(FollowUpConceptQuestions.ARV_ADHERENCE, cohort, encounters);
		pregnancyStatusHashMap = tbQueryLineList.getByResult(FollowUpConceptQuestions.PREGNANCY_STATUS, cohort, encounters);
		breastfeedingStatusHashMap = tbQueryLineList.getByResult(FollowUpConceptQuestions.CURRENTLY_BREAST_FEEDING_CHILD,
		    cohort, encounters);
		tbActiveDate = tbQueryLineList.getObsValueDate(encounters, FollowUpConceptQuestions.TB_ACTIVE_DATE, cohort);
		tbTxStartDateDictionary = tbQueryLineList.getObsValueDate(encounters,
		    FollowUpConceptQuestions.TB_TREATMENT_START_DATE, cohort);
		tbTreatmentStatus = tbQueryLineList.getByResult(FollowUpConceptQuestions.TB_TREATMENT_STATUS, cohort, encounters);
		tbTreatmentCompletedDateDictionary = tbQueryLineList.getObsValueDate(encounters,
		    FollowUpConceptQuestions.TB_TREATMENT_COMPLETED_DATE, cohort);
		tbTreatmentDiscontinuedDateDictionary = tbQueryLineList.getObsValueDate(encounters,
		    FollowUpConceptQuestions.TB_TREATMENT_DISCONTINUED_DATE, cohort);
		tbScreeningDateHashMap = tbQueryLineList.getObsValueDate(encounters, FollowUpConceptQuestions.TB_SCREENING_DATE,
		    cohort);
		tbScreeningResultsHashMap = tbQueryLineList.getByResult(FollowUpConceptQuestions.TB_SCREENING_RESULT, cohort,
		    encounters);
		_LAMResults = tbQueryLineList.getByResult(FollowUpConceptQuestions.LF_LAM_RESULT, cohort, encounters);
		geneXpertResult = tbQueryLineList.getByResult(FollowUpConceptQuestions.GENE_XPERT_RESULT, cohort, encounters);
		OtherDiagnosticTest = tbQueryLineList.getByResult(FollowUpConceptQuestions.OTHER_TB_DIAGNOSTIC_TEST, cohort,
		    encounters);
		OtherDiagResult = tbQueryLineList
		        .getByResult(FollowUpConceptQuestions.TB_DIAGNOSTIC_TEST_RESULT, cohort, encounters);
		
	}
	
	private void getTXTbScreeningDictionary(List<Integer> encounters, Cohort cohort) {
		artStartDictionary = tbQueryLineList.getObsValueDate(encounters, FollowUpConceptQuestions.ART_START_DATE, cohort);
		regimentDictionary = tbQueryLineList.getRegiment(encounters, cohort);
		followUpDate = tbQueryLineList.getObsValueDate(encounters, FollowUpConceptQuestions.FOLLOW_UP_DATE, cohort);
		followUpStatus = tbQueryLineList.getFollowUpStatus(encounters, cohort);
		dispensDayHashMap = tbQueryLineList.getByResult(FollowUpConceptQuestions.ART_DISPENSE_DOSE, cohort, encounters);
		adherenceHashMap = tbQueryLineList.getByResult(FollowUpConceptQuestions.ARV_ADHERENCE, cohort, encounters);
		pregnancyStatusHashMap = tbQueryLineList.getByResult(FollowUpConceptQuestions.PREGNANCY_STATUS, cohort, encounters);
		breastfeedingStatusHashMap = tbQueryLineList.getByResult(FollowUpConceptQuestions.CURRENTLY_BREAST_FEEDING_CHILD,
		    cohort, encounters);
		tbScreeningDateHashMap = tbQueryLineList.getObsValueDate(encounters, FollowUpConceptQuestions.TB_SCREENING_DATE,
		    cohort);
		tbScreeningResultsHashMap = tbQueryLineList.getByResult(FollowUpConceptQuestions.TB_SCREENING_RESULT, cohort,
		    encounters);
		specimen = tbQueryLineList.getByResult(FollowUpConceptQuestions.SPECIMEN_SENT, cohort, encounters);
		_LAMResults = tbQueryLineList.getByResult(FollowUpConceptQuestions.LF_LAM_RESULT, cohort, encounters);
		geneXpertResult = tbQueryLineList.getByResult(FollowUpConceptQuestions.GENE_XPERT_RESULT, cohort, encounters);
		OtherDiagnosticTest = tbQueryLineList.getByResult(FollowUpConceptQuestions.OTHER_TB_DIAGNOSTIC_TEST, cohort,
		    encounters);
		OtherDiagResult = tbQueryLineList
		        .getByResult(FollowUpConceptQuestions.TB_DIAGNOSTIC_TEST_RESULT, cohort, encounters);
		
		tbActiveDate = tbQueryLineList.getObsValueDate(encounters, FollowUpConceptQuestions.TB_ACTIVE_DATE, cohort);
		tbTxStartDateDictionary = tbQueryLineList.getObsValueDate(encounters,
		    FollowUpConceptQuestions.TB_TREATMENT_START_DATE, cohort);
		tbTreatmentStatus = tbQueryLineList.getByResult(FollowUpConceptQuestions.TB_TREATMENT_STATUS, cohort, encounters);
		tbTreatmentCompletedDateDictionary = tbQueryLineList.getObsValueDate(encounters,
		    FollowUpConceptQuestions.TB_TREATMENT_COMPLETED_DATE, cohort);
		tbTreatmentDiscontinuedDateDictionary = tbQueryLineList.getObsValueDate(encounters,
		    FollowUpConceptQuestions.TB_TREATMENT_DISCONTINUED_DATE, cohort);
	}
	
	private void getRowForActiveTB(SimpleDataSet data, List<Person> persons) {
		DataSetRow row;
		if (persons.isEmpty()) {
			data.addRow(LineListUtilities.buildEmptyRow(Arrays.asList("#", "GUID", "Patient Name", "MRN", "UAN", "Age",
			    "Sex", "ArtStartDateEth", "followUpDateEth", "followUpStatus", "Regimen", "ARV Dose Days", "Adherence",
			    "Pregnant?", "Breastfeeding?", "On PMTCT?", "TB Screening Date", "TB Screening Result", "Specimen Sent",
			    "LF_LAM-result", "Gene_Xpert-Result", "OtherTBDiagnosticTest", "OtherTBDiagnosticResult",
			    "Active TB Diagnosed Date", "TB Treatment Start Date", "TB Treatment Status", "TB Treatment Completed Date",
			    "TB Treatment Discontinued Date")));
		}
		int i = 1;
		for (Person person : persons) {
			
			row = new DataSetRow();
			addBaseColumns(row, person, i++);
			getTxTbTreatmentAndTbArt(row, person);
			data.addRow(row);
		}
	}
	
	private void getRowForNumerator(SimpleDataSet data, List<Person> persons) {
		DataSetRow row;
		if (persons.isEmpty()) {
			data.addRow(LineListUtilities.buildEmptyRow(Arrays.asList("#", "GUID", "Patient Name", "MRN", "UAN", "Age",
			    "Sex", "ArtStartDateEth", "followUpDateEth", "followUpStatus", "Regimen", "ARV Dose Days", "Adherence",
			    "Pregnant?", "Breastfeeding?", "On PMTCT?", "TB Screening Date", "TB Screening Result", "Specimen Sent",
			    "LF_LAM-result", "Gene_Xpert-Result", "OtherTBDiagnosticTest", "OtherTBDiagnosticResult",
			    "Active TB Diagnosed Date", "TB Treatment Start Date", "TB Treatment Status", "TB Treatment Completed Date",
			    "TB Treatment Discontinued Date")));
		}
		int i = 1;
		for (Person person : persons) {
			
			row = new DataSetRow();
			addBaseColumns(row, person, i++);
			getTxTbTreatmentAndTbArt(row, person);
			data.addRow(row);
		}
	}
	
	private void getRowForDenominator(SimpleDataSet data, List<Person> persons) {
		DataSetRow row;
		if (persons.isEmpty()) {
			data.addRow(LineListUtilities.buildEmptyRow(Arrays.asList("#", "GUID", "Patient Name", "MRN", "UAN", "Age",
			    "Sex", "ArtStartDateEth", "followUpDateEth", "followUpStatus", "Regimen", "ARV Dose Days", "Adherence",
			    "Pregnant?", "Breastfeeding?", "On PMTCT?", "TB Screening Date", "TB Screening Result", "Specimen Sent",
			    "LF_LAM-result", "Gene_Xpert-Result", "OtherTBDiagnosticTest", "OtherTBDiagnosticResult",
			    "Active TB Diagnosed Date", "TB Treatment Start Date", "TB Treatment Status", "TB Treatment Completed Date",
			    "TB Treatment Discontinued Date")));
		}
		int i = 1;
		for (Person person : persons) {
			
			row = new DataSetRow();
			addBaseColumns(row, person, i++);
			getTxTbScreening(row, person);
			data.addRow(row);
		}
	}
	
	private void addBaseColumns(DataSetRow row, Person person, int i) {
		Date artStartDate = tbQueryLineList.getDate(artStartDictionary.get(person.getPersonId()));
		String artEthiopianDate = tbQueryLineList.getEthiopianDate(artStartDate);
		
		Date _followUpDate = tbQueryLineList.getDate(followUpDate.get(person.getPersonId()));
		String followUpEthiopianDate = tbQueryLineList.getEthiopianDate(_followUpDate);
		
		row.addColumnValue(new DataSetColumn("#", "#", Integer.class), i);
		row.addColumnValue(new DataSetColumn("GUID", "GUID", String.class), person.getUuid());
		row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", String.class), person.getNames());
		row.addColumnValue(new DataSetColumn("MRN", "MRN", Integer.class), mrnIdentifierHashMap.get(person.getPersonId()));
		row.addColumnValue(new DataSetColumn("UAN", "UAN", String.class), uanIdentifierHashMap.get(person.getPersonId()));
		row.addColumnValue(new DataSetColumn("Age", "Age", Integer.class), person.getAge(_followUpDate));
		row.addColumnValue(new DataSetColumn("DateOfBirth", "Date of birth", Date.class), person.getBirthdate());
		row.addColumnValue(new DataSetColumn("Gender", "Sex", Integer.class), person.getGender());
		row.addColumnValue(new DataSetColumn("ArtStartDateEth", "Art Start Date E.C", String.class), artEthiopianDate);
		row.addColumnValue(new DataSetColumn("followUpDateEth", "FollowUp Date E.C", String.class), followUpEthiopianDate);
		row.addColumnValue(new DataSetColumn("followUpStatus", "FollowUp Status", String.class),
		    followUpStatus.get(person.getPersonId()));
		row.addColumnValue(new DataSetColumn("Regimen", "Regimen", String.class),
		    regimentDictionary.get(person.getPersonId()));
		
	}
	
	private void getTxTbScreening(DataSetRow row, Person person) {
		Date tbScreeningETH = tbQueryLineList.getDate(tbScreeningDateHashMap.get(person.getPersonId()));
		String tbScreeningEthiopianDate = tbQueryLineList.getEthiopianDate(tbScreeningETH);
		Date tbActiveTreatmentStartDate = tbQueryLineList.getDate(tbActiveDate.get(person.getPersonId()));
		Date tbTxStartDate = tbQueryLineList.getDate(tbTxStartDateDictionary.get(person.getPersonId()));
		Date tbTXCompletedDate = tbQueryLineList.getDate(tbTreatmentCompletedDateDictionary.get(person.getPersonId()));
		Date tbTxDiscontinuedDate = tbQueryLineList.getDate(tbTreatmentDiscontinuedDateDictionary.get(person.getPersonId()));
		
		row.addColumnValue(new DataSetColumn("ARV Dose Days", "ARV Dose Days", String.class),
		    dispensDayHashMap.get(person.getPersonId()));
		row.addColumnValue(new DataSetColumn("Adherence", "Adherence", String.class),
		    adherenceHashMap.get(person.getPersonId()));
		row.addColumnValue(new DataSetColumn("Pregnant?", "Pregnant?", String.class),
		    pregnancyStatusHashMap.get(person.getPersonId()));
		row.addColumnValue(new DataSetColumn("Breastfeeding?", "Breastfeeding?", String.class),
		    breastfeedingStatusHashMap.get(person.getPersonId()));
		
		row.addColumnValue(new DataSetColumn("On PMTCT?", "On PMTCT?", String.class), "");
		row.addColumnValue(new DataSetColumn("TB Screening Date", "TB Screening Date", String.class),
		    tbScreeningEthiopianDate);
		row.addColumnValue(new DataSetColumn("TB Screening Result", "TB Screening Result", String.class),
		    tbScreeningResultsHashMap.get(person.getPersonId()));
		row.addColumnValue(new DataSetColumn("Specimen Sent", "Specimen Sent", String.class),
		    specimen.get(person.getPersonId()));
		row.addColumnValue(new DataSetColumn("LF_LAM-result", "LF-LAM Result", String.class),
		    _LAMResults.get(person.getPersonId()));
		row.addColumnValue(new DataSetColumn("Gene_Xpert-Result", "Gene-Xpert Result", String.class),
		    geneXpertResult.get(person.getPersonId()));
		row.addColumnValue(new DataSetColumn("OtherTBDiagnosticTest", "Other TB Diagnostic Test Type", String.class),
		    OtherDiagnosticTest.get(person.getPersonId()));
		row.addColumnValue(new DataSetColumn("OtherTBDiagnosticResult", "Other TB Diagnostic Test Result", String.class),
		    OtherDiagResult.get(person.getPersonId()));
		row.addColumnValue(new DataSetColumn("Active TB Diagnosed Date", "Active TB Diagnosed Date", String.class),
		    tbQueryLineList.getEthiopianDate(tbActiveTreatmentStartDate));
		row.addColumnValue(new DataSetColumn("TB Treatment Start Date", "TB Treatment Start Date", String.class),
		    tbQueryLineList.getEthiopianDate(tbTxStartDate));
		row.addColumnValue(new DataSetColumn("TB Treatment Status", "TB Treatment Status", String.class),
		    tbTreatmentStatus.get(person.getPersonId()));
		row.addColumnValue(new DataSetColumn("TB Treatment Completed Date", "TB Treatment Completed Date", String.class),
		    tbQueryLineList.getEthiopianDate(tbTXCompletedDate));
		row.addColumnValue(new DataSetColumn("TB Treatment Discontinued Date", "TB Treatment Discontinued Date",
		        String.class), tbQueryLineList.getEthiopianDate(tbTxDiscontinuedDate));
	}
	
	private void getTxTbTreatmentAndTbArt(DataSetRow row, Person person) {
		Date tbActiveTreatmentStartDate = tbQueryLineList.getDate(tbActiveDate.get(person.getPersonId()));
		Date tbTxStartDate = tbQueryLineList.getDate(tbTxStartDateDictionary.get(person.getPersonId()));
		Date tbTXCompletedDate = tbQueryLineList.getDate(tbTreatmentCompletedDateDictionary.get(person.getPersonId()));
		Date tbTxDiscontinuedDate = tbQueryLineList.getDate(tbTreatmentDiscontinuedDateDictionary.get(person.getPersonId()));
		Date tbScreeningDate = tbQueryLineList.getDate(tbScreeningDateHashMap.get(person.getPersonId()));
		
		row.addColumnValue(new DataSetColumn("ARV Dose Days ", "ARV Dose Days", String.class),
		    dispensDayHashMap.get(person.getPersonId()));
		row.addColumnValue(new DataSetColumn("Adherence", "Adherence", String.class),
		    adherenceHashMap.get(person.getPersonId()));
		row.addColumnValue(new DataSetColumn("Pregnant?", "Pregnant?", String.class),
		    pregnancyStatusHashMap.get(person.getPersonId()));
		row.addColumnValue(new DataSetColumn("Breastfeeding?", "Breastfeeding?", String.class),
		    breastfeedingStatusHashMap.get(person.getPersonId()));
		row.addColumnValue(new DataSetColumn("On PMTCT?", "On PMTCT?", String.class), "");
		row.addColumnValue(new DataSetColumn("Active TB Diagnosed Date", "Active TB Diagnosed Date", String.class),
		    tbQueryLineList.getEthiopianDate(tbActiveTreatmentStartDate));
		row.addColumnValue(new DataSetColumn("TB Treatment Start Date", "TB Treatment Start Date", String.class),
		    tbQueryLineList.getEthiopianDate(tbTxStartDate));
		row.addColumnValue(new DataSetColumn("TB Treatment Status", "TB Treatment Status", String.class),
		    tbTreatmentStatus.get(person.getPersonId()));
		row.addColumnValue(new DataSetColumn("TB Treatment Completed Date", "TB Treatment Completed Date", String.class),
		    tbQueryLineList.getEthiopianDate(tbTXCompletedDate));
		row.addColumnValue(new DataSetColumn("TB Treatment Discontinued Date", "TB Treatment Discontinued Date",
		        String.class), tbQueryLineList.getEthiopianDate(tbTxDiscontinuedDate));
		row.addColumnValue(new DataSetColumn("TB Screening Date", "TB Screening Date", String.class),
		    tbQueryLineList.getEthiopianDate(tbScreeningDate));
		row.addColumnValue(new DataSetColumn("TB Screening Result", "TB Screening Result", String.class),
		    tbScreeningResultsHashMap.get(person.getPersonId()));
		row.addColumnValue(new DataSetColumn("LF-LAM Result", "LF-LAM Result", String.class),
		    _LAMResults.get(person.getPersonId()));
		row.addColumnValue(new DataSetColumn("Gene-Xpert Result", "Gene-Xpert Result", String.class),
		    geneXpertResult.get(person.getPersonId()));
		row.addColumnValue(
		    new DataSetColumn("Other TB Diagnostic Test Type", "Other TB Diagnostic Test Type", String.class),
		    OtherDiagnosticTest.get(person.getPersonId()));
		row.addColumnValue(new DataSetColumn("Other TB Diagnostic Test Result", "Other TB Diagnostic Test Result",
		        String.class), OtherDiagResult.get(person.getPersonId()));
		
	}
	
	private void constructEmptyDataTable(SimpleDataSet data, List<String> programColumns) {
		List<String> baseColumns = arrayListOfBaseEmptyColumn();
		
		data.addRow(LineListUtilities.buildEmptyRow(Stream.concat(baseColumns.stream(), programColumns.stream()).collect(
		    Collectors.toList())));
	}
	
	private List<String> arrayListOfBaseEmptyColumn() {
		return Arrays.asList("#", "GUID", "Patient Name", "MRN", "UAN", "Age", "Sex", "ART Start Date in E.C",
		    "FollowUp Date E.C", "Regimen", "FollowUp Status");
	}
	
}
