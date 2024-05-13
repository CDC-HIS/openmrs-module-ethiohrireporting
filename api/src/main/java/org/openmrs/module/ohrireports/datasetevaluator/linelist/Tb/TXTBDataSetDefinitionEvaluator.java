package org.openmrs.module.ohrireports.datasetevaluator.linelist.Tb;

import java.sql.Array;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.ss.formula.functions.Now;
import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.TBQuery;
import org.openmrs.module.ohrireports.api.impl.query.TBARTQuery;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
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

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Handler(supports = {TXTBDataSetDefinition.class})
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
    HashMap<Integer, Object> tbTxStartDictionary = new HashMap<>();
    private HashMap<Integer, Object> screenedResultHashMap;
    private HashMap<Integer, Object> regimentDictionary;
    private HashMap<Integer, Object> artStartDictionary, specimen, geneXpertResult, _LAMResults, OtherDiagnosticTest,
            OtherDiagResult, followUpStatus, followUpDate, tpTreatmentStatus, tpActiveDate, dispensDayHashMap, adherenceHashMap,
            pregnancyStatusHashMap, tbScreeningDateHashMap, tbScreeningResultsHashMap;
    private List<Integer> followUpEncounters;

    @Override
    public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {

        hdsd = (TXTBDataSetDefinition) dataSetDefinition;

        SimpleDataSet data = new SimpleDataSet(dataSetDefinition, evalContext);
        patientQuery = Context.getService(PatientQueryService.class);


        if (hdsd.getType().equals(TXTBReport.numerator)) {
            followUpEncounters = encounterQuery.getLatestDateByFollowUpDate(null, hdsd.getEndDate());
            tbQueryLineList.setEncountersByScreenDate(followUpEncounters);

            List<Integer> baseEncountersOfTreatmentStartDate = encounterQuery.getEncounters(Collections.singletonList(TB_TREATMENT_START_DATE), hdsd.getStartDate(), hdsd.getEndDate());
            cohort = tbQuery.getCohort(baseEncountersOfTreatmentStartDate);

            tbTxStartDictionary = tbQueryLineList.getTBTreatmentStartDate(cohort, baseEncountersOfTreatmentStartDate);
            getTXTbDictionary(cohort);
        } else if (hdsd.getType().equals(TXTBReport.denominator)) {
            if (hdsd.getEndDate() == null) {
                hdsd.setEndDate(new Date());
            }
            followUpEncounters = encounterQuery.getLatestDateByFollowUpDate(null, hdsd.getEndDate());
            tbQueryLineList.setEncountersByScreenDate(tbQuery.getFollowUpEncounter());

            List<Integer> baseEncountersOfTreatmentStartDate = encounterQuery.getEncounters(Collections.singletonList(TB_SCREENING_DATE), hdsd.getStartDate(), hdsd.getEndDate());
            cohort = tbQuery.getCohort(baseEncountersOfTreatmentStartDate);

            screenedResultHashMap = tbQueryLineList.getTBScreenedResult(cohort);
            tbTxStartDictionary = tbQueryLineList.getTBScreenedDate(cohort);
            getTXTbScreeningDictionary(baseEncountersOfTreatmentStartDate, cohort);
        } else {
            cohort = tbartQuery.getCohortByTBTreatmentStartDate(hdsd.getStartDate(), hdsd.getEndDate());
            followUpEncounters = tbartQuery.getBaseEncounter();
            tbTxStartDictionary = tbQueryLineList.getTBTreatmentStartDate(cohort, tbartQuery.getTbTreatmentStartDateEncounter());

            tpTreatmentStatus = tbQueryLineList.getByResult(TB_TREATMENT_STATUS, cohort, tbartQuery.getTbTreatmentStartDateEncounter());
            tpActiveDate = tbQueryLineList.getObsValueDate(tbartQuery.getActiveDiagnosticStartDateEncounter(), TB_ACTIVE_DATE, cohort);

        }

        mrnIdentifierHashMap = tbQueryLineList.getIdentifier(cohort, MRN_PATIENT_IDENTIFIERS);
        uanIdentifierHashMap = tbQueryLineList.getIdentifier(cohort, UAN_PATIENT_IDENTIFIERS);
        artStartDictionary = tbQueryLineList.getObsValueDate(followUpEncounters, ART_START_DATE, cohort);
        regimentDictionary = tbQueryLineList.getRegiment(followUpEncounters, cohort);
        followUpDate = tbQueryLineList.getObsValueDate(followUpEncounters, FOLLOW_UP_DATE, cohort);
        followUpStatus = tbQueryLineList.getFollowUpStatus(followUpEncounters, cohort);
        List<Person> persons = patientQuery.getPersons(cohort);

        DataSetRow row;

        if (!persons.isEmpty()) {
            row = new DataSetRow();
            row.addColumnValue(new DataSetColumn("#", "#", String.class), "TOTAL");
            row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", Integer.class), persons.size());
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

    private void getTXTbDictionary(Cohort cohort) {
        dispensDayHashMap = tbQueryLineList.getByResultTypeQuery(cohort, ART_DISPENSE_DOSE);
        adherenceHashMap = tbQueryLineList.getByResultTypeQuery(cohort, ARV_ADHERENCE);
        pregnancyStatusHashMap = tbQueryLineList.getByResultTypeQuery(cohort, PREGNANCY_STATUS);
        tbScreeningDateHashMap = tbQueryLineList.getObsValueDate(tbQueryLineList.getScreenedOnDateEncounters(), TB_SCREENING_DATE, cohort);
        tbScreeningResultsHashMap = tbQueryLineList.getTBScreenedResult(cohort);
        specimen = tbQueryLineList.getByResultTypeQuery(cohort, SPECIMEN_SENT);
        _LAMResults = tbQueryLineList.getByResultTypeQuery(cohort, LF_LAM_RESULT);
        OtherDiagnosticTest = tbQueryLineList.getByResultTypeQuery(cohort, DIAGNOSTIC_TEST);
        geneXpertResult = tbQueryLineList.getByResultTypeQuery(cohort, GENE_XPERT_RESULT);
        OtherDiagResult = tbQueryLineList.getByResultTypeQuery(cohort, TB_DIAGNOSTIC_TEST_RESULT);
    }

    private void getTXTbScreeningDictionary(List<Integer> encounters, Cohort cohort) {
        dispensDayHashMap = tbQueryLineList.getByResult(ART_DISPENSE_DOSE, cohort, encounters);
        adherenceHashMap = tbQueryLineList.getByResult(ARV_ADHERENCE, cohort, encounters);
        pregnancyStatusHashMap = tbQueryLineList.getByResult(PREGNANCY_STATUS, cohort, encounters);
        tbScreeningDateHashMap = tbQueryLineList.getObsValueDate(encounters, TB_SCREENING_DATE, cohort);
        tbScreeningResultsHashMap = tbQueryLineList.getByResult(TB_SCREENING_RESULT, cohort, encounters);
        specimen = tbQueryLineList.getByResult(SPECIMEN_SENT, cohort, encounters);
        _LAMResults = tbQueryLineList.getByResult(LF_LAM_RESULT, cohort, encounters);
        geneXpertResult = tbQueryLineList.getByResult(GENE_XPERT_RESULT, cohort, encounters);
        OtherDiagnosticTest = tbQueryLineList.getByResult(DIAGNOSTIC_TEST, cohort, encounters);
        OtherDiagResult = tbQueryLineList.getByResult(TB_DIAGNOSTIC_TEST_RESULT, cohort, encounters);
    }

    private void getRowForActiveTB(SimpleDataSet data, List<Person> persons) {
        DataSetRow row;
        if (persons.isEmpty()) {
            constructEmptyDataTable(data, Arrays.asList("Active TB diagnosed Date E.C", "TB Treatment Start Date E.C", "TB Treatment Status"));
        }
        int i = 0;
        for (Person person : persons) {

            row = new DataSetRow();
            Date treatmentStartDate = tbQueryLineList.getDate(tbTxStartDictionary.get(person.getPersonId()));
            String txEthiopianDate = tbQueryLineList.getEthiopianDate(treatmentStartDate);

            Date _activeTbDate = tbQueryLineList.getDate(tpActiveDate.get(person.getPersonId()));
            String activeTbEthiopianDate = tbQueryLineList.getEthiopianDate(_activeTbDate);

            addBaseColumns(row, person, i++);
            row.addColumnValue(new DataSetColumn("activeTbDiagnosedDateEth", "Active TB diagnosed Date E.C", String.class), activeTbEthiopianDate);
            row.addColumnValue(new DataSetColumn("TBTXTreatmentDateEth", "TB Treatment Start Date E.C", String.class), txEthiopianDate);
            row.addColumnValue(new DataSetColumn("tp-status", "TB Treatment Status", String.class), tpTreatmentStatus.get(person.getPersonId()));

            data.addRow(row);
        }
    }

    private void getRowForNumerator(SimpleDataSet data, List<Person> persons) {
        DataSetRow row;
        if (persons.isEmpty()) {
            constructEmptyDataTable(data, Collections.singletonList("TB Treatment Start Date E.C"));
        }
        int i = 1;
        for (Person person : persons) {

            row = new DataSetRow();
            Date treatmentStartDate = tbQueryLineList.getDate(tbTxStartDictionary.get(person.getPersonId()));
            String txEthiopianDate = tbQueryLineList.getEthiopianDate(treatmentStartDate);

            addBaseColumns(row, person, i++);
            getTxTb(row, person);
            row.addColumnValue(new DataSetColumn("TBTXTreatmentDateEth", "TB Treatment Start Date E.C", String.class), txEthiopianDate);

            data.addRow(row);
        }
    }

    private void getRowForDenominator(SimpleDataSet data, List<Person> persons) {
        DataSetRow row;
        if (persons.isEmpty()) {
            constructEmptyDataTable(data, Arrays.asList("Screen Result", "TB Screened Start Date E.C"));
        }
        int i = 1;
        for (Person person : persons) {

            row = new DataSetRow();
            Date treatmentStartDate = tbQueryLineList.getDate(tbTxStartDictionary.get(person.getPersonId()));
            String txEthiopianDate = tbQueryLineList.getEthiopianDate(treatmentStartDate);

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
        row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", String.class), person.getNames());
        row.addColumnValue(new DataSetColumn("MRN", "MRN", Integer.class), mrnIdentifierHashMap.get(person.getPersonId()));
        row.addColumnValue(new DataSetColumn("UAN", "UAN", String.class), uanIdentifierHashMap.get(person.getPersonId()));
        row.addColumnValue(new DataSetColumn("Age", "Age", Integer.class), person.getAge(hdsd.getEndDate()));
        row.addColumnValue(new DataSetColumn("Gender", "Sex", Integer.class), person.getGender());
        row.addColumnValue(new DataSetColumn("ArtStartDateEth", "Art Start Date E.C", String.class), artEthiopianDate);
        row.addColumnValue(new DataSetColumn("followUpDateEth", "FollowUp Date E.C", String.class), followUpEthiopianDate);
        row.addColumnValue(new DataSetColumn("followUpStatus", "FollowUp Status", String.class), followUpStatus.get(person.getPersonId()));
        row.addColumnValue(new DataSetColumn("Regimen", "Regimen", String.class), regimentDictionary.get(person.getPersonId()));

    }

    private void getTxTb(DataSetRow row, Person person) {
        Date tbScreeningETH = tbQueryLineList.getDate(tbScreeningDateHashMap.get(person.getPersonId()));
        String tbScreeningEthiopianDate = tbQueryLineList.getEthiopianDate(tbScreeningETH);

        row.addColumnValue(new DataSetColumn("ARV Dose Days ", "ARV Dose Days", String.class), dispensDayHashMap.get(person.getPersonId()));
        row.addColumnValue(new DataSetColumn("Adherence", "Adherence", String.class), adherenceHashMap.get(person.getPersonId()));
        row.addColumnValue(new DataSetColumn("Pregnancy/ Breastfeeding Status", "Pregnancy/ Breastfeeding Status", String.class), pregnancyStatusHashMap.get(person.getPersonId()));
        row.addColumnValue(new DataSetColumn("On PMTCT?", "On PMTCT?", String.class), "");
        row.addColumnValue(new DataSetColumn("TB Screening Date", "TB Screening Date", String.class), tbScreeningEthiopianDate);
        row.addColumnValue(new DataSetColumn("TB Screening Result", "TB Screening Result", String.class), tbScreeningResultsHashMap);
        row.addColumnValue(new DataSetColumn("Specimen Sent", "Specimen Sent", String.class), specimen);
        row.addColumnValue(new DataSetColumn("LF_LAM-result", "LF-LAM Result", String.class), _LAMResults.get(person.getPersonId()));
        row.addColumnValue(new DataSetColumn("Gene_Xpert-Result", "Gene-Xpert Result", String.class), geneXpertResult.get(person.getPersonId()));
        row.addColumnValue(new DataSetColumn("OtherTBDiagnosticTest", "Other TB Diagnostic Test Type", String.class), OtherDiagnosticTest.get(person.getPersonId()));
        row.addColumnValue(new DataSetColumn("OtherTBDiagnosticResult", "Other TB Diagnostic Test Result", String.class), OtherDiagResult.get(person.getPersonId()));
    }

    private void getTxTbScreening(DataSetRow row, Person person) {
        Date tbScreeningETH = tbQueryLineList.getDate(tbScreeningDateHashMap.get(person.getPersonId()));
        String tbScreeningEthiopianDate = tbQueryLineList.getEthiopianDate(tbScreeningETH);

        row.addColumnValue(new DataSetColumn("ARV Dose Days ", "ARV Dose Days", String.class), dispensDayHashMap.get(person.getPersonId()));
        row.addColumnValue(new DataSetColumn("Adherence", "Adherence", String.class), adherenceHashMap.get(person.getPersonId()));
        row.addColumnValue(new DataSetColumn("Pregnancy/ Breastfeeding Status", "Pregnancy/ Breastfeeding Status", String.class), pregnancyStatusHashMap.get(person.getPersonId()));
        row.addColumnValue(new DataSetColumn("On PMTCT?", "On PMTCT?", String.class), "");
        row.addColumnValue(new DataSetColumn("TB Screening Date", "TB Screening Date", String.class), tbScreeningEthiopianDate);
        row.addColumnValue(new DataSetColumn("TB Screening Result", "TB Screening Result", String.class), tbScreeningResultsHashMap.get(person.getPersonId()));
        row.addColumnValue(new DataSetColumn("Specimen Sent", "Specimen Sent", String.class), specimen.get(person.getPersonId()));
        row.addColumnValue(new DataSetColumn("LF_LAM-result", "LF-LAM Result", String.class), _LAMResults.get(person.getPersonId()));
        row.addColumnValue(new DataSetColumn("Gene_Xpert-Result", "Gene-Xpert Result", String.class), geneXpertResult.get(person.getPersonId()));
        row.addColumnValue(new DataSetColumn("OtherTBDiagnosticTest", "Other TB Diagnostic Test Type", String.class), OtherDiagnosticTest.get(person.getPersonId()));
        row.addColumnValue(new DataSetColumn("OtherTBDiagnosticResult", "Other TB Diagnostic Test Result", String.class), OtherDiagResult.get(person.getPersonId()));
    }

    private void constructEmptyDataTable(SimpleDataSet data, List<String> programColumns) {
        List<String> baseColumns = arrayListOfBaseEmptyColumn();

        data.addRow(LineListUtilities.buildEmptyRow(Stream.concat(baseColumns.stream(), programColumns.stream())
                .collect(Collectors.toList())));
    }

    private List<String> arrayListOfBaseEmptyColumn() {
        return Arrays.asList("#", "Patient Name", "MRN", "UAN", "Age", "Sex",
                "ART Start Date in E.C", "FollowUp Date E.C", "Regimen", "FollowUp Status");
    }

}
