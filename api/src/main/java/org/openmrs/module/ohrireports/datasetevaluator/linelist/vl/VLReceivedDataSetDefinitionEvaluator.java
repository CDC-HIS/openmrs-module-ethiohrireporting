package org.openmrs.module.ohrireports.datasetevaluator.linelist.vl;

import java.util.*;

import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.VlQuery;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.VLReceivedDataSetDefinition;
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

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Handler(supports = {VLReceivedDataSetDefinition.class})
public class VLReceivedDataSetDefinitionEvaluator implements DataSetEvaluator {

    @Autowired
    private VlQuery vlQuery;

    @Autowired
    private EncounterQuery encounterQuery;

    @Override
    public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {

        VLReceivedDataSetDefinition _DataSetDefinition = (VLReceivedDataSetDefinition) dataSetDefinition;
        SimpleDataSet data = new SimpleDataSet(dataSetDefinition, evalContext);

        // Check start date and end date are valid
        // If start date is greater than end date
        if (_DataSetDefinition.getStartDate() != null && _DataSetDefinition.getEndDate() != null
                && _DataSetDefinition.getStartDate().compareTo(_DataSetDefinition.getEndDate()) > 0) {
            //throw new EvaluationException("Start date cannot be greater than end date");
            DataSetRow row = new DataSetRow();
            row.addColumnValue(new DataSetColumn("Error", "Error", Integer.class),
                    "Report start date cannot be after report end date");
            data.addRow(row);
            return data;
        }
        if (_DataSetDefinition.getEndDate() == null) {
            _DataSetDefinition.setEndDate(new Date());
        }

        PatientQueryService patientQueryService = Context.getService(PatientQueryService.class);

        List<Integer> baseEncounters = encounterQuery.getEncounters(
                Collections.singletonList(DATE_VIRAL_TEST_RESULT_RECEIVED), _DataSetDefinition.getStartDate(),
                _DataSetDefinition.getEndDate());
        vlQuery.loadInitialCohort(_DataSetDefinition.getStartDate(), _DataSetDefinition.getEndDate(), baseEncounters);

        List<Person> persons = LineListUtilities.sortPatientByName(patientQueryService.getPersons(vlQuery.cohort));

        List<Integer> latestFollowupEncounter = encounterQuery.getLatestDateByFollowUpDate(null, null);

        HashMap<Integer, Object> mrnIdentifierHashMap = vlQuery.getIdentifier(vlQuery.cohort, MRN_PATIENT_IDENTIFIERS);
        HashMap<Integer, Object> uanIdentifierHashMap = vlQuery.getIdentifier(vlQuery.cohort, UAN_PATIENT_IDENTIFIERS);
        HashMap<Integer, Object> weight = vlQuery.getByValueNumeric(WEIGHT, vlQuery.cohort, baseEncounters);
        HashMap<Integer, Object> cd4Count = vlQuery.getByValueNumeric(ADULT_CD4_COUNT, vlQuery.cohort, baseEncounters);
        HashMap<Integer, Object> artStartDictionary = vlQuery.getArtStartDate(vlQuery.cohort, null,
                _DataSetDefinition.getEndDate());
        HashMap<Integer, Object> followUpDateHashMap = vlQuery.getObsValueDate(baseEncounters, FOLLOW_UP_DATE,
                vlQuery.cohort);
        HashMap<Integer, Object> statusHashMap = vlQuery.getFollowUpStatus(baseEncounters, vlQuery.cohort);
        HashMap<Integer, Object> regimentDictionary = vlQuery.getRegiment(vlQuery.getVlTakenEncounters(), vlQuery.cohort);
        HashMap<Integer, Object> viralLoadReceivedDateHashMap = vlQuery.getObsValueDate(baseEncounters,
                DATE_VIRAL_TEST_RESULT_RECEIVED, vlQuery.cohort);
        HashMap<Integer, Object> routineTestTypeHashMap = vlQuery.getByResult(ROUTINE_VIRAL_LOAD, vlQuery.cohort,
                baseEncounters);
        HashMap<Integer, Object> targetedTestTypeHashMap = vlQuery.getByResult(TARGET_VIRAL_LOAD, vlQuery.cohort,
                baseEncounters);
        HashMap<Integer, Object> viralLoadStatus = vlQuery.getStatusViralLoad();
        HashMap<Integer, Object> dispensedDose = vlQuery.getArtDose();
        HashMap<Integer, Object> viralLoadCount = vlQuery.getViralLoadCount();
        HashMap<Integer, Object> viralLoadSentDateHashMap = vlQuery.getObsValueDate(baseEncounters, VL_SENT_DATE,
                vlQuery.cohort);
        HashMap<Integer, Object> adherenceHashMap = vlQuery.getByResult(ARV_ADHERENCE, vlQuery.cohort, baseEncounters);
        HashMap<Integer, Object> pregnancyStatus = vlQuery.getByResult(PREGNANCY_STATUS, vlQuery.cohort, baseEncounters);
        HashMap<Integer, Object> breastfeedingStatus = vlQuery.getByResult(CURRENTLY_BREAST_FEEDING_CHILD, vlQuery.cohort,
                baseEncounters);

        // fields from latest encounter
        HashMap<Integer, Object> latestFollowupDateHashMap = vlQuery.getObsValueDate(latestFollowupEncounter,
                FOLLOW_UP_DATE, vlQuery.cohort);
        HashMap<Integer, Object> latestFollowupStatusHashMap = vlQuery.getByResult(FOLLOW_UP_STATUS, vlQuery.cohort,
                latestFollowupEncounter);
        HashMap<Integer, Object> latestRegimenHashMap = vlQuery
                .getByResult(REGIMEN, vlQuery.cohort, latestFollowupEncounter);
        HashMap<Integer, Object> latestArvDoseDaysHashMap = vlQuery.getByResult(ART_DISPENSE_DOSE, vlQuery.cohort,
                latestFollowupEncounter);
        HashMap<Integer, Object> latestAdherenceHashMap = vlQuery.getByResult(ARV_ADHERENCE, vlQuery.cohort,
                latestFollowupEncounter);
        HashMap<Integer, Object> nextVisitDateHashMap = vlQuery.getObsValueDate(latestFollowupEncounter, NEXT_VISIT_DATE,
                vlQuery.cohort);
        HashMap<Integer, Object> treatmentEndDateHashMap = vlQuery.getObsValueDate(latestFollowupEncounter,
                TREATMENT_END_DATE, vlQuery.cohort);

        DataSetRow row;
        if (!persons.isEmpty()) {

            row = new DataSetRow();
            row.addColumnValue(new DataSetColumn("#", "#", Integer.class), "TOTAL");
            row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", Integer.class), persons.size());

            data.addRow(row);
        } else {
            data.addRow(LineListUtilities.buildEmptyRow(Arrays.asList("#", "Patient Name", "MRN", "UAN", "Age", "Sex",
                    "Weight", "CD4", "ART Start Date in E.C", "Follow-up Date in E.C", "Follow-up Status", "Regimen", "ARV Dose Days",
                    "Adherence", "PregnancyStatus", "BreastfeedingStatus", "On PMTCT?", "Viral Load Sent Date", "VL Received Date",
                    "TAT (in days)", "Routine Test type", "Targeted Test Type", "viral_load_status", "viral_load_count", "Latest Follow-up Date",
                    "Latest Follow-up Status", "Latest Regimen", "Latest ARV Dose Days ", "Latest Adherence", "Next Visit Date in E.C", "Treatment End Date in E.C.",
                    "Mobile No.")));
        }
        int i = 1;
        for (Person person : persons) {

            row = new DataSetRow();

            Date followupDate = vlQuery.getDate(followUpDateHashMap.get(person.getPersonId()));
            Date artStartDate = vlQuery.getDate(artStartDictionary.get(person.getPersonId()));
            Date viralLoadReceivedDate = vlQuery.getDate(viralLoadReceivedDateHashMap.get(person.getPersonId()));
            Date viralLoadSentDate = vlQuery.getDate(viralLoadSentDateHashMap.get(person.getPersonId()));
            Date latestFollowupDate = vlQuery.getDate(latestFollowupDateHashMap.get(person.getPersonId()));
            Date nextVisitDate = vlQuery.getDate(nextVisitDateHashMap.get(person.getPersonId()));
            Date treatmentEndDate = vlQuery.getDate(treatmentEndDateHashMap.get(person.getPersonId()));

            row.addColumnValue(new DataSetColumn("#", "#", Integer.class), i++);
            row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", String.class), person.getNames());
            row.addColumnValue(new DataSetColumn("MRN", "MRN", Integer.class),
                    mrnIdentifierHashMap.get(person.getPersonId()));
            row.addColumnValue(new DataSetColumn("UAN", "UAN", Integer.class),
                    uanIdentifierHashMap.get(person.getPersonId()));
            row.addColumnValue(new DataSetColumn("Age", "Age", Integer.class), person.getAge(followupDate));
            row.addColumnValue(new DataSetColumn("Gender", "Sex", Integer.class), person.getGender());
            row.addColumnValue(new DataSetColumn("Weight", "Weight", String.class), weight.get(person.getPersonId()));
            row.addColumnValue(new DataSetColumn("CD4", "CD4", String.class), cd4Count.get(person.getPersonId()));
            row.addColumnValue(new DataSetColumn("ARTStartDateETH", "ART Start Date in E.C", String.class),
                    vlQuery.getEthiopianDate(artStartDate));
            row.addColumnValue(new DataSetColumn("Follow-up Date in E.C", "Follow-up Date in E.C", String.class), vlQuery.getEthiopianDate(followupDate));
            row.addColumnValue(new DataSetColumn("Follow-up Status", "Follow-up Status", String.class), statusHashMap.get(person.getPersonId()));
            row.addColumnValue(new DataSetColumn("Regimen", "Regimen", String.class), regimentDictionary.get(person.getPersonId()));
            row.addColumnValue(new DataSetColumn("ArtDose", "ARV Dose Days", String.class), dispensedDose.get(person.getPersonId()));
            row.addColumnValue(new DataSetColumn("Adherence", "Adherence", String.class), adherenceHashMap.get(person.getPersonId()));
            row.addColumnValue(new DataSetColumn("PregnancyStatus", "Pregnant?", String.class), pregnancyStatus.get(person.getPersonId()));
            row.addColumnValue(new DataSetColumn("BreastfeedingStatus", "Breastfeeding?", String.class), breastfeedingStatus.get(person.getPersonId()));
            row.addColumnValue(new DataSetColumn("On PMTCT?", "On PMTCT?", String.class), "");
            row.addColumnValue(new DataSetColumn("ViralLoadSentDate", "VL Sent Date in E.C.", String.class), vlQuery.getEthiopianDate(viralLoadSentDate));
            row.addColumnValue(new DataSetColumn("vlReceiveDate", "VL Received Date in E.C.", String.class), vlQuery.getEthiopianDate(viralLoadReceivedDate));
            row.addColumnValue(new DataSetColumn("TAT", "TAT (in days)", String.class), LineListUtilities.getDayDifference(viralLoadReceivedDate, viralLoadSentDate));
            row.addColumnValue(new DataSetColumn("Routine Test type", "Routine Test type", String.class), routineTestTypeHashMap.get(person.getPersonId()));
            row.addColumnValue(new DataSetColumn("Targeted Test Type", "Targeted Test Type", String.class), targetedTestTypeHashMap.get(person.getPersonId()));
            row.addColumnValue(new DataSetColumn("viral_load_status", "VL Status", String.class), viralLoadStatus.get(person.getPersonId()));
            row.addColumnValue(new DataSetColumn("viral_load_count", "VL Count", Integer.class), viralLoadCount.get(person.getPersonId()));
            row.addColumnValue(new DataSetColumn("Latest Follow-up Date in E.C", "Latest Follow-up Date in E.C", String.class), vlQuery.getEthiopianDate(latestFollowupDate));
            row.addColumnValue(new DataSetColumn("Latest Follow-up Status", "Latest Follow-up Status", String.class), latestFollowupStatusHashMap.get(person.getPersonId()));
            row.addColumnValue(new DataSetColumn("Latest Regimen", "Latest Regimen", String.class), latestRegimenHashMap.get(person.getPersonId()));
            row.addColumnValue(new DataSetColumn("Latest ARV Dose Days ", "Latest ARV Dose Days", String.class), latestArvDoseDaysHashMap.get(person.getPersonId()));
            row.addColumnValue(new DataSetColumn("Latest Adherence", "Latest Adherence", String.class), latestAdherenceHashMap.get(person.getPersonId()));
            row.addColumnValue(new DataSetColumn("Next Visit Date in E.C", "Next Visit Date in E.C", String.class), vlQuery.getEthiopianDate(nextVisitDate));
            row.addColumnValue(new DataSetColumn("Treatment End Date in E.C.", "Treatment End Date in E.C.", String.class), vlQuery.getEthiopianDate(treatmentEndDate));
            row.addColumnValue(new DataSetColumn("Mobile No.", "Mobile No.", String.class), LineListUtilities.getPhone(person.getActiveAttributes()));

            data.addRow(row);
        }

        return data;

    }

    private void getDateRow(DataSetRow row, Date artDate, String labelName) {
        String artEthiopianDate = vlQuery.getEthiopianDate(artDate);
        row.addColumnValue(new DataSetColumn(labelName, labelName, Date.class), artDate);
        row.addColumnValue(new DataSetColumn(labelName + "-ETH", labelName + " ETH", String.class), artEthiopianDate);
    }

}
