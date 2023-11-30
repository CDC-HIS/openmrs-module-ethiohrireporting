package org.openmrs.module.ohrireports.datasetevaluator.linelist.Tb;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.DIAGNOSTIC_TEST;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.FOLLOW_UP_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.GENE_XPERT_RESULT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.LF_LAM_RESULT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.MRN_PATIENT_IDENTIFIERS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.SPECIMEN_SENT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TB_ACTIVE_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TB_DIAGNOSTIC_TEST_RESULT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TB_SCREENING_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TB_TREATMENT_START_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TB_TREATMENT_STATUS;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.TBQuery;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.TXTBDataSetDefinition;
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

@Handler(supports = {TXTBDataSetDefinition.class})
public class TXTBDataSetDefinitionEvaluator implements DataSetEvaluator {

    private PatientQueryService patientQuery;

    @Autowired
    private TBQuery tbQuery;

    @Autowired
    private TbQueryLineList tbQueryLineList;

    @Autowired
    private EncounterQuery encounterQuery;
    private Cohort cohort;
    private TXTBDataSetDefinition hdsd;
    HashMap<Integer, Object> mrnIdentifierHashMap;
    HashMap<Integer, Object> tbTxStartDictionary = new HashMap<>();

    private HashMap<Integer, Object> screenedResultHashMap;

    private HashMap<Integer, Object> regimentDictionary;

    private HashMap<Integer, Object> artStartDictionary, specimen, geneXpertResult, _LAMResults, OtherDiagnosticTest,
            OtherDiagResult, followUpStatus, followUpDate, tpTreatmentStatus, tpActiveDate;

    private List<Integer> followUpEncounters;

    @Override
    public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext)
            throws EvaluationException {

        hdsd = (TXTBDataSetDefinition) dataSetDefinition;

        SimpleDataSet data = new SimpleDataSet(dataSetDefinition, evalContext);
        patientQuery = Context.getService(PatientQueryService.class);
        Calendar subSixMonth = Calendar.getInstance();
        subSixMonth.setTime(hdsd.getStartDate());
        subSixMonth.add(Calendar.MONTH, -6);
        Date prevSixMonth = subSixMonth.getTime();

        followUpEncounters = encounterQuery.getLatestDateByFollowUpDate(hdsd.getEndDate());
        tbQueryLineList.setEncountersByScreenDate(
                followUpEncounters);

        if (hdsd.getType().equals(TXTBReport.numerator)) {
            List<Integer> baseEncountersOfTreatmentStartDate = encounterQuery.getEncounters(
                    Arrays.asList(TB_TREATMENT_START_DATE),
                    prevSixMonth, hdsd.getEndDate());
            cohort = tbQuery.getCohort(baseEncountersOfTreatmentStartDate);
            cohort = tbQuery.getTBTreatmentStartedCohort(cohort, "",followUpEncounters);
            tbTxStartDictionary = tbQueryLineList.getTBTreatmentStartDate(cohort, baseEncountersOfTreatmentStartDate);
            getTXTbDictionary(cohort);
        } else if (hdsd.getType().equals(TXTBReport.denominator)) {
            List<Integer> baseEncountersOfTreatmentStartDate = encounterQuery.getEncounters(
                    Arrays.asList(TB_SCREENING_DATE),
                    prevSixMonth, hdsd.getEndDate());
            cohort = tbQuery.getCohort(baseEncountersOfTreatmentStartDate);
            cohort = tbQuery.getTBScreenedCohort(cohort, prevSixMonth, hdsd.getEndDate());
            screenedResultHashMap = tbQueryLineList.getTBScreenedResult(cohort);
            tbTxStartDictionary = tbQueryLineList.getTBScreenedDate(cohort);
            getTXTbDictionary(cohort);
        } else {
            cohort = tbQuery.getActiveOnArtCohort("", null, hdsd.getEndDate(), null, followUpEncounters);
            cohort = tbQuery.getCurrentOnActiveTB(cohort, hdsd.getStartDate(), hdsd.getEndDate(), followUpEncounters);
            List<Integer> tpActiveEncounters = encounterQuery.getEncounters(Arrays.asList(TB_ACTIVE_DATE, TB_TREATMENT_START_DATE), hdsd.getStartDate(),
                    hdsd.getEndDate(), cohort);
            tbTxStartDictionary = tbQueryLineList.getTBTreatmentStartDate(cohort, tpActiveEncounters);

            tpTreatmentStatus = tbQueryLineList.getByResult(TB_TREATMENT_STATUS, cohort, tpActiveEncounters);
            tpActiveDate = tbQueryLineList.getObsValueDate(tpActiveEncounters, TB_ACTIVE_DATE, cohort);

        }

        mrnIdentifierHashMap = tbQueryLineList.getIdentifier(cohort, MRN_PATIENT_IDENTIFIERS);
        artStartDictionary = tbQueryLineList.getArtStartDate(cohort, null,
                hdsd.getEndDate());
        regimentDictionary = tbQueryLineList.getRegiment(
                followUpEncounters, cohort);
        followUpDate = tbQueryLineList.getObsValueDate(followUpEncounters, FOLLOW_UP_DATE, cohort);
        followUpStatus = tbQueryLineList.getFollowUpStatus(followUpEncounters, cohort);
        List<Person> persons = patientQuery.getPersons(cohort);

        DataSetRow row;

        if (persons.size() > 0) {

            row = new DataSetRow();

            row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class), "TOTAL");
            row.addColumnValue(new DataSetColumn("Name", "Name", Integer.class), persons.size());

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
        specimen = tbQueryLineList.getByResultTypeQuery(cohort, SPECIMEN_SENT);
        _LAMResults = tbQueryLineList.getByResultTypeQuery(cohort, LF_LAM_RESULT);
        OtherDiagnosticTest = tbQueryLineList.getByResultTypeQuery(cohort,
                DIAGNOSTIC_TEST);
        geneXpertResult = tbQueryLineList.getByResultTypeQuery(cohort,
                GENE_XPERT_RESULT);
        OtherDiagResult = tbQueryLineList.getByResultTypeQuery(cohort,
                TB_DIAGNOSTIC_TEST_RESULT);
    }

    private void getRowForActiveTB(SimpleDataSet data, List<Person> persons) {
        DataSetRow row = new DataSetRow();
        for (Person person : persons) {

            row = new DataSetRow();
            Date treatmentStartDate = tbQueryLineList.getDate(tbTxStartDictionary.get(person.getPersonId()));
            String txEthiopianDate = tbQueryLineList.getEthiopianDate(treatmentStartDate);

            Date _activeTbDate = tbQueryLineList.getDate(tpActiveDate.get(person.getPersonId()));
            String activeTbEthiopianDate = tbQueryLineList.getEthiopianDate(_activeTbDate);

            addBaseColumns(row, person);
            row.addColumnValue(new DataSetColumn("activeTbDiagnosedDate", "Active TB diagnosed Date", Date.class),
                    _activeTbDate);

            row.addColumnValue(
                    new DataSetColumn("activeTbDiagnosedDateEth", "Active TB diagnosed DateETH", String.class),
                    activeTbEthiopianDate);

            row.addColumnValue(new DataSetColumn("TBTreatmentStartDate", "TB Treatment Start Date", Date.class),
                    treatmentStartDate);

            row.addColumnValue(new DataSetColumn("TBTXTreatmentDateEth", "TB Treatment Start Date ETH", String.class),
                    txEthiopianDate);
            row.addColumnValue(new DataSetColumn("tp-status", "TB Treatment Status", String.class),
                    tpTreatmentStatus.get(person.getPersonId()));

            data.addRow(row);
        }
    }

    private void getRowForNumerator(SimpleDataSet data, List<Person> persons) {
        DataSetRow row = new DataSetRow();
        for (Person person : persons) {

            row = new DataSetRow();
            Date treatmentStartDate = tbQueryLineList.getDate(tbTxStartDictionary.get(person.getPersonId()));
            String txEthiopianDate = tbQueryLineList.getEthiopianDate(treatmentStartDate);

            addBaseColumns(row, person);
            getTxTb(row, person);
            row.addColumnValue(new DataSetColumn("TBTreatmentStartDate", "TB Treatment Start Date", Date.class),
                    treatmentStartDate);

            row.addColumnValue(new DataSetColumn("TBTXTreatmentDateEth", "TB Treatment Start Date ETH", String.class),
                    txEthiopianDate);

            data.addRow(row);
        }
    }

    private void getRowForDenominator(SimpleDataSet data, List<Person> persons) {
        DataSetRow row = new DataSetRow();
        for (Person person : persons) {

            row = new DataSetRow();
            Date treatmentStartDate = tbQueryLineList.getDate(tbTxStartDictionary.get(person.getPersonId()));
            String txEthiopianDate = tbQueryLineList.getEthiopianDate(treatmentStartDate);

            addBaseColumns(row, person);
            getTxTb(row, person);
            row.addColumnValue(new DataSetColumn("ScreenedResult", "Screen Result", String.class),
                    screenedResultHashMap.get(person.getPersonId()));

            row.addColumnValue(new DataSetColumn("TBScreenedDate", "TB Screened Date", Date.class), treatmentStartDate);

            row.addColumnValue(new DataSetColumn("TBTXScreenedDateEth", "TB Screened Start Date ETH", String.class),
                    txEthiopianDate);

            data.addRow(row);
        }
    }

    private void addBaseColumns(DataSetRow row, Person person) {
        Date artStartDate = tbQueryLineList.getDate(artStartDictionary.get(person.getPersonId()));
        String artEthiopianDate = tbQueryLineList.getEthiopianDate(artStartDate);

        Date _followUpDate = tbQueryLineList.getDate(followUpDate.get(person.getPersonId()));
        String followUpEthiopianDate = tbQueryLineList.getEthiopianDate(_followUpDate);

        row.addColumnValue(new DataSetColumn("MRN", "MRN", Integer.class),
                mrnIdentifierHashMap.get(person.getPersonId()));

        row.addColumnValue(new DataSetColumn("Name", "Name", String.class), person.getNames());
        row.addColumnValue(new DataSetColumn("Age", "Age", Integer.class), person.getAge(hdsd.getEndDate()));
        row.addColumnValue(new DataSetColumn("Gender", "Gender", Integer.class), person.getGender());

        row.addColumnValue(new DataSetColumn("ArtStartDate", "Art Start Date", Date.class), artStartDate);
        row.addColumnValue(new DataSetColumn("ArtStartDateEth", "Art Start Date ETH", String.class),
                artEthiopianDate);

        row.addColumnValue(new DataSetColumn("followUpDate", "FollowUp Date", Date.class),
                _followUpDate);

        row.addColumnValue(new DataSetColumn("followUpDateEth", "FollowUp Date ETH", String.class),
                followUpEthiopianDate);
        row.addColumnValue(new DataSetColumn("Regimen", "Regimen", String.class),
                regimentDictionary.get(person.getPersonId()));
        row.addColumnValue(new DataSetColumn("followUpStatus", "FollowUp Status", String.class),
                followUpStatus.get(person.getPersonId()));

    }

    private void getTxTb(DataSetRow row, Person person) {
        row.addColumnValue(new DataSetColumn("SpecimenSent", "Specimen Sent", String.class),
                specimen.get(person.getPersonId()));
        row.addColumnValue(new DataSetColumn("LF_LAM-result", "LF_LAM Result", String.class),
                _LAMResults.get(person.getPersonId()));
        row.addColumnValue(new DataSetColumn("Gene_Xpert-Result", "Gene_Xpert Result", String.class),
                geneXpertResult.get(person.getPersonId()));
        row.addColumnValue(new DataSetColumn("OtherTBDiagnosticTest", "Other TB Diagnostic Test", String.class),
                OtherDiagnosticTest.get(person.getPersonId()));
        row.addColumnValue(
                new DataSetColumn("OtherTBDiagnosticResult", "Other TB Diagnostic Test Result", String.class),
                OtherDiagResult.get(person.getPersonId()));
    }

}
