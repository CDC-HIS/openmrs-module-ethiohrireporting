package org.openmrs.module.ohrireports.reports.datasetevaluator.datim.tx_pvls;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.HIV_VIRAL_LOAD_STATUS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.ART_START_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.FOLLOW_UP_STATUS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HIV_VIRAL_LOAD_SUPPRESSED;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HIV_VIRAL_LOAD_UNSUPPRESSED;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HIV_ROUTINE_VIRAL_LOAD_COUNT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HIV_TARGET_VIRAL_LOAD_COUNT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.ALIVE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.RESTART;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.module.ohrireports.reports.datasetdefinition.datim.tx_pvls.TX_PVLSDatasetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { TX_PVLSDatasetDefinition.class })
public class TX_PVLSDatasetDefinitionEvaluator implements DataSetEvaluator {
    @Autowired
    private ConceptService conceptService;
    @Autowired
    private EvaluationService evaluationService;

    private EvaluationContext context;
    private TX_PVLSDatasetDefinition txDatasetDefinition;
    private int minCount = 0;
    private int maxCount = 4;
    private int total = 0;
    private String routineDesc = "ROUTINE: Disaggregated by Age / Sex / Testing Indication (Fine Disaggregated). Must complete finer disaggregated unless permitted by program";
    private String targetDesc = "Targeted: Disaggregated by Age / Sex / Testing Indication (Fine Disaggregated). Must complete finer disaggregated unless permitted by program";
    List<Obs> obses = new ArrayList<>();
    List<Integer> countedPatientId = new ArrayList<>();
    List<Integer> patientIdList = new ArrayList<>();
    private Concept artConcept,
            hivViralLoadStatusConcept,
            hivViralLoadSuppressedConcept,
            hivViralLoadUnSuppressedConcept,
            aliveConcept,
            restartConcept,
            routineViralLoadConcept,
            targetViralLoadConcept,
            followUpStatusConcept;

    private static final int _VALID_MONTHS_OF_VIRAL_LOAD_TEST = 12;
    private LocalDate startDate;
    private int months;

    @Override
    public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext)
            throws EvaluationException {
        context = evalContext;
        txDatasetDefinition = (TX_PVLSDatasetDefinition) dataSetDefinition;

        loadConcepts();
         patientIdList = txDatasetDefinition.getIncludeUnSuppressed() ? getAllPatientWithViralLoudCount()
                : getSuppressedPatientList();
        countedPatientId.clear();
        SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
        DataSetRow routineDataSetRow = new DataSetRow();
        routineDataSetRow.addColumnValue(new DataSetColumn("label", "", String.class), routineDesc);
        dataSet.addRow(routineDataSetRow);
        buildDataSet(dataSet, routineViralLoadConcept);

        DataSetRow targetDataSetRow = new DataSetRow();
        targetDataSetRow.addColumnValue(new DataSetColumn("label", "", String.class), targetDesc);
        dataSet.addRow(targetDataSetRow);
        buildDataSet(dataSet, targetViralLoadConcept);
        return dataSet;
    }

    private void loadConcepts() {
        artConcept = conceptService.getConceptByUuid(ART_START_DATE);
        hivViralLoadStatusConcept = conceptService.getConceptByUuid(HIV_VIRAL_LOAD_STATUS);
        aliveConcept = conceptService.getConceptByUuid(ALIVE);
        restartConcept = conceptService.getConceptByUuid(RESTART);
        routineViralLoadConcept = conceptService.getConceptByUuid(HIV_ROUTINE_VIRAL_LOAD_COUNT);
        targetViralLoadConcept = conceptService.getConceptByUuid(HIV_TARGET_VIRAL_LOAD_COUNT);
        hivViralLoadSuppressedConcept = conceptService.getConceptByUuid(HIV_VIRAL_LOAD_SUPPRESSED);
        hivViralLoadUnSuppressedConcept = conceptService.getConceptByUuid(HIV_VIRAL_LOAD_UNSUPPRESSED);
        followUpStatusConcept = conceptService.getConceptByUuid(FOLLOW_UP_STATUS);
    }

    /*
     * Load all patient with viral load count has done in the last 12 month
     */

    private void buildDataSet(SimpleDataSet simpleDataSet, Concept viralLoadTypeConcept) {
        getAll("F", viralLoadTypeConcept);
        DataSetRow femaleSetRow = new DataSetRow();
        buildDataSetColumn(femaleSetRow, "F", viralLoadTypeConcept);
        simpleDataSet.addRow(femaleSetRow);

        getAll("M", viralLoadTypeConcept);
        DataSetRow maleSetRow = new DataSetRow();
        buildDataSetColumn(maleSetRow, "M", viralLoadTypeConcept);
        simpleDataSet.addRow(maleSetRow);
    }

    private void getAll(String gender, Concept viralLoadTypeConcept) {
        HqlQueryBuilder queryBuilder = new HqlQueryBuilder();

        queryBuilder.select("obs")
                .from(Obs.class, "obs")
                .whereEqual("obs.encounter.encounterType", txDatasetDefinition.getEncounterType())
                .and()
                .whereEqual("obs.concept", viralLoadTypeConcept)
                .and()
                .whereEqual("obs.person.gender", gender)
                .and()
                .whereLess("obs.obsDatetime",
                        txDatasetDefinition.getEndDate())
                .and()
                .whereIn("obs.personId", patientIdList)
                .orderDesc("obs.obsDatetime");

        obses = evaluationService.evaluateToList(queryBuilder, Obs.class, context);

    }

    private List<Integer> getAllPatientWithViralLoudCount() {
        List<Integer> patientIdList = getListOfALiveORRestartPatientObs();
        List<Integer> refinedPatientIdList = new ArrayList<>();
        HqlQueryBuilder queryBuilder = new HqlQueryBuilder();

        queryBuilder.select("obs")
                .from(Obs.class, "obs")
                .whereEqual("obs.encounter.encounterType", txDatasetDefinition.getEncounterType())
                .and()
                .whereEqual("obs.concept", hivViralLoadStatusConcept)
                .and()
                .whereIn("obs.valueCoded",
                        Arrays.asList(hivViralLoadSuppressedConcept, hivViralLoadUnSuppressedConcept))
                .and()
                .whereLess("obs.obsDatetime", txDatasetDefinition.getEndDate())
                .and()
                .whereIn("obs.personId", patientIdList)
                .orderDesc("obs.obsDatetime");

        List<Obs> obses = evaluationService.evaluateToList(queryBuilder, Obs.class, context);

        LocalDate endDate = new LocalDate(txDatasetDefinition.getEndDate());

        for (Obs obs : obses) {

            startDate = new LocalDate(obs.getObsDatetime());
            if (Months.monthsBetween(startDate, endDate)
                      .getMonths() > _VALID_MONTHS_OF_VIRAL_LOAD_TEST)
                continue;

            if (!refinedPatientIdList.contains(obs.getPersonId()))
                refinedPatientIdList.add(obs.getPersonId());
        }
        return refinedPatientIdList;
    }
    
    private List<Integer> getSuppressedPatientList() {
        List<Integer> patientIdList = getListOfALiveORRestartPatientObs();
        List<Integer> refinedPatientIdList = new ArrayList<>();
        HqlQueryBuilder queryBuilder = new HqlQueryBuilder();

        queryBuilder.select("obs")
                .from(Obs.class, "obs")
                .whereEqual("obs.encounter.encounterType", txDatasetDefinition.getEncounterType())
                .and()
                .whereEqual("obs.concept", hivViralLoadStatusConcept)
                .and()
                .whereEqual("obs.valueCoded", hivViralLoadSuppressedConcept)
                .and()
                .whereLess("obs.obsDatetime", txDatasetDefinition.getEndDate())
                .and()
                .whereIn("obs.personId", patientIdList)
                .orderDesc("obs.obsDatetime");

        List<Obs> obses = evaluationService.evaluateToList(queryBuilder, Obs.class, context);
        LocalDate endDate = new LocalDate(txDatasetDefinition.getEndDate());
        for (Obs obs : obses) {
            months = Months.monthsBetween(new LocalDate(obs.getObsDatetime()), endDate)
                    .getMonths();
            if (months > _VALID_MONTHS_OF_VIRAL_LOAD_TEST)
                continue;
            if (!refinedPatientIdList.contains(obs.getPersonId())) {
                refinedPatientIdList.add(obs.getPersonId());
            }
        }
        return refinedPatientIdList;
    }

    private List<Integer> getListOfALiveORRestartPatientObs() {

        List<Integer> personIdList = new ArrayList<>();
        HqlQueryBuilder queryBuilder = new HqlQueryBuilder();

        queryBuilder.select("distinct obs.personId")
                    .from(Obs.class, "obs")
                    .whereEqual("obs.encounter.encounterType", txDatasetDefinition.getEncounterType())
                    .and()
                    .whereEqual("obs.concept", followUpStatusConcept)
                    .and()
                    .whereIn("obs.valueCoded",
                            Arrays.asList(aliveConcept,
                                    restartConcept))
                    .and()
                    .whereLess("obs.obsDatetime", txDatasetDefinition.getEndDate())
                    .and()
                    .whereIdIn("obs.personId", getPatientsOnArt());

        personIdList = evaluationService.evaluateToList(queryBuilder, Integer.class, context);

        return personIdList;
    }
   
    private void buildDataSetColumn(DataSetRow dataSet, String gender, Concept viralLoadTypeConcept) {
        total = 0;
        minCount = 15;
        maxCount = 19;

        dataSet.addColumnValue(new DataSetColumn("ByAgeAndSexData", "Gender", Integer.class),
                gender.equals("F") ? "Female"
                        : "Male");
        dataSet.addColumnValue(new DataSetColumn("unknownAge", "Unknown Age", Integer.class),
                getEnrolledByUnknownAge(viralLoadTypeConcept));

        while (minCount <= 65) {
            if (minCount == 65) {
                dataSet.addColumnValue(new DataSetColumn("65+", "65+", Integer.class),
                        getEnrolledByAgeAndGender(65, 200, gender, viralLoadTypeConcept));
            } else {
                dataSet.addColumnValue(
                        new DataSetColumn(minCount + "-" + maxCount, minCount + "-" + maxCount, Integer.class),
                        getEnrolledByAgeAndGender(minCount, maxCount, gender, viralLoadTypeConcept));
            }
            minCount = maxCount + 1;
            maxCount = minCount + 4;
        }
        dataSet.addColumnValue(new DataSetColumn("Sub-total", "Subtotal", Integer.class), total);
    }

    private int getEnrolledByAgeAndGender(int min, int max, String gender, Concept viralLoadTypeConcept) {
        int count = 0;
        List<Obs> obsList = new ArrayList<>();
        for (Obs obs : obses) {

            if (countedPatientId.contains(obs.getPersonId()))
                continue;

            if (obs.getPerson().getAge() >= min && obs.getPerson().getAge() <= max) {
                obsList.add(obs);
                countedPatientId.add(obs.getPersonId());
                count++;
            }
        }
        incrementTotalCount(count);
        clearCountedObs(obsList);
        return count;
    }

    private int getEnrolledByUnknownAge(Concept viralLoadTypeConcept) {
        int count = 0;
        List<Obs> obsList = new ArrayList<>();
        for (Obs obs : obses) {

            if (obs.getConcept().equals(viralLoadTypeConcept)) {
                if (countedPatientId.contains(obs.getPersonId()))
                    continue;

                if (Objects.isNull(obs.getPerson().getAge()) ||
                        obs.getPerson().getAge() <= 0) {
                    count++;
                    obsList.add(obs);
                    countedPatientId.add(obs.getPersonId());
                }
            }

        }
        incrementTotalCount(count);
        clearCountedObs(obsList);
        return count;
    }
   
    private void incrementTotalCount(int count) {
        if (count > 0)
            total = total + count;
    }

    private void clearCountedObs(List<Obs> obsList) {
        for (Obs obs : obsList) {
            obses.removeIf(p -> p.getObsId().equals(obs.getId()));
        }
    }

    private List<Integer> getPatientsOnArt() {
        List<Integer> patientIdList = new ArrayList<>();

        HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
        queryBuilder.select("distinct obs.personId")
                .from(Obs.class, "obs")
                .whereEqual("obs.encounter.encounterType", txDatasetDefinition.getEncounterType())
                .and()
                .whereEqual("obs.concept", artConcept)
                .and()
                .whereLess("obs.valueDatetime", txDatasetDefinition.getEndDate());

        patientIdList = evaluationService.evaluateToList(queryBuilder, Integer.class, context);

        return patientIdList;

    }

}
