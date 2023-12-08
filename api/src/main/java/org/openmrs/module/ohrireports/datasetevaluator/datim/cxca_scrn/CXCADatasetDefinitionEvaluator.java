package org.openmrs.module.ohrireports.datasetevaluator.datim.cxca_scrn;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.module.ohrireports.api.impl.query.CervicalCancerQuery;
import org.openmrs.module.ohrireports.api.query.AggregateBuilder;
import org.openmrs.module.ohrireports.datasetdefinition.datim.cxca_scrn.CXCADatasetDefinition;
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

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Handler(supports = {CXCADatasetDefinition.class})
public class CXCADatasetDefinitionEvaluator implements DataSetEvaluator {
    private int total = 0;
    private int minCount = 0;
    private int maxCount = 4;
    List<Person> persons = new ArrayList<>();
    private CXCADatasetDefinition cxcaDatasetDefinition;
    private Concept artConcept,
            cxcaScreenedConcept,
            treatmentEndDateConcept,
            hpvAndDNAScreeningResultConcept,
            positiveConcept,
            negativeConcept,
            unknownConcept,
            viaScreeningResultConcept,
            viaNegativeConcept,
            viaPositiveConcept,
            viaSuspiciousConcept,
            cytologyResultConcept,
            cytologyNegativeConcept,
            cytologyASCUSPositiveConcept,
            cytologyGreaterASCUSSuspiciousConcept,
            cxcaScreeningTypeConcept;

    @Autowired
    private ConceptService conceptService;

    @Autowired
    private EvaluationService evaluationService;

    private EvaluationContext context;
    private List<Integer> onArtFemalePatients;
    private List<Integer> currentPatients;
    @Autowired
    private CervicalCancerQuery cervicalCancerQuery;

    @Autowired
    private AggregateBuilder aggregateBuilder;

    @Override
    public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext)
            throws EvaluationException {
        cxcaDatasetDefinition = (CXCADatasetDefinition) dataSetDefinition;
        context = evalContext;
        SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
        //loadConcepts();
        cervicalCancerQuery.setStartDate(cxcaDatasetDefinition.getStartDate());
        cervicalCancerQuery.setEndDate(cxcaDatasetDefinition.getEndDate());
        Cohort baseCohort = cervicalCancerQuery.getByScreeningType(cxcaDatasetDefinition.getScreeningType());



        Cohort negativeCohort = cervicalCancerQuery.getNegativeResult(baseCohort);
        List<Person> negativeCxCaPersonList = cervicalCancerQuery.getPersons(negativeCohort);
        aggregateBuilder.setPersonList(negativeCxCaPersonList);
        DataSetRow negativeCxCaRow = new DataSetRow();
        aggregateBuilder.buildDataSetColumnForScreening(negativeCxCaRow, "Negative");
        dataSet.addRow(negativeCxCaRow);

        Cohort positiveCohort = cervicalCancerQuery.getPositiveResult(baseCohort);
        List<Person> positiveCxCaPersonList = cervicalCancerQuery.getPersons(positiveCohort);
        aggregateBuilder.setPersonList(positiveCxCaPersonList);
        DataSetRow positiveCxCaRow = new DataSetRow();
        aggregateBuilder.buildDataSetColumnForScreening(positiveCxCaRow, "Positive");
        dataSet.addRow(positiveCxCaRow);

        Cohort suspiciousCohort = cervicalCancerQuery.getSuspectedResult(baseCohort);
        List<Person> suspeciousCxCaPersonList = cervicalCancerQuery.getPersons(suspiciousCohort);
        aggregateBuilder.setPersonList(suspeciousCxCaPersonList);
        DataSetRow suspiciousCxCaRow = new DataSetRow();
        aggregateBuilder.buildDataSetColumnForScreening(suspiciousCxCaRow, "Suspicious");
        dataSet.addRow(suspiciousCxCaRow);


        //DataSetRow positive




        //        List<Integer> personIdList = getCXCAScreened();
//        List<Integer> personFirstScreeningList = getPatientByScreeningType(personIdList);
//
//        Set<Integer> negativePatient = new HashSet<>();
//        negativePatient.addAll(
//                getScreeningTypeCohort(personFirstScreeningList, hpvAndDNAScreeningResultConcept, negativeConcept));
//        negativePatient.addAll(
//                getScreeningTypeCohort(personFirstScreeningList, viaScreeningResultConcept, viaNegativeConcept));
//        negativePatient.addAll(
//                getScreeningTypeCohort(personFirstScreeningList, cytologyResultConcept, cytologyNegativeConcept));
//
//        persons.addAll(getPatients(new ArrayList<>(negativePatient)));
//
//        DataSetRow negativeDataSetRow = new DataSetRow();
//        buildDataSet(negativeDataSetRow, "Negative");
//        dataSet.addRow(negativeDataSetRow);
//
//        Set<Integer> positivePatient = new HashSet<>();
//        positivePatient.addAll(
//                getScreeningTypeCohort(personFirstScreeningList, hpvAndDNAScreeningResultConcept, positiveConcept));
//        positivePatient.addAll(
//                getScreeningTypeCohort(personFirstScreeningList, viaScreeningResultConcept, viaPositiveConcept));
//        positivePatient.addAll(
//                getScreeningTypeCohort(personFirstScreeningList, cytologyResultConcept, cytologyASCUSPositiveConcept));
//
//        persons.addAll(getPatients(new ArrayList<>(positivePatient)));
//
//        DataSetRow positiveDataset = new DataSetRow();
//        buildDataSet(positiveDataset, "Positive");
//        dataSet.addRow(positiveDataset);
//
//        Set<Integer> suspectedPatient = new HashSet<>();
//        suspectedPatient.addAll(
//                getScreeningTypeCohort(personFirstScreeningList, hpvAndDNAScreeningResultConcept, unknownConcept));
//        suspectedPatient.addAll(
//                getScreeningTypeCohort(personFirstScreeningList, viaScreeningResultConcept, viaSuspiciousConcept));
//        suspectedPatient.addAll(getScreeningTypeCohort(personFirstScreeningList, cytologyResultConcept,
//                cytologyGreaterASCUSSuspiciousConcept));

        //  persons.addAll(getPatients(new ArrayList<>(suspectedPatient)));

//        DataSetRow suspectedDataSetRow = new DataSetRow();
//        buildDataSet(suspectedDataSetRow, "Suspected");
//        dataSet.addRow(suspectedDataSetRow);

        return dataSet;
    }

    private List<Integer> getPatientByScreeningType(List<Integer> personIdList) {
        if (personIdList.size() == 0)
            return new ArrayList<>();
        HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
        queryBuilder.select("obs.personId")
                .from(Obs.class, "obs")
                .whereEqual("obs.concept", cxcaScreeningTypeConcept)
                .and()
                .whereEqual("obs.encounter.encounterType", cxcaDatasetDefinition.getEncounterType())
                .and()
                .whereEqual("obs.valueCoded", cxcaDatasetDefinition.getScreeningType())
                .and()
                .whereBetweenInclusive("obs.obsDatetime", cxcaDatasetDefinition.getStartDate(),
                        cxcaDatasetDefinition.getEndDate())
                .whereIn("obs.personId", personIdList);

        return evaluationService.evaluateToList(queryBuilder, Integer.class, context);
    }

    private void loadConcepts() {
        artConcept = conceptService.getConceptByUuid(ART_START_DATE);
        cxcaScreenedConcept = conceptService.getConceptByUuid(CXCA_SCREENING_ACCEPTED_DATE);
        treatmentEndDateConcept = conceptService.getConceptByUuid(TREATMENT_END_DATE);
        hpvAndDNAScreeningResultConcept = conceptService.getConceptByUuid(HPV_DNA_SCREENING_RESULT);
        positiveConcept = conceptService.getConceptByUuid(POSITIVE);
        negativeConcept = conceptService.getConceptByUuid(NEGATIVE);
        unknownConcept = conceptService.getConceptByUuid(UNKNOWN);
        viaScreeningResultConcept = conceptService.getConceptByUuid(VIA_SCREENING_RESULT);
        viaNegativeConcept = conceptService.getConceptByUuid(VIA_NEGATIVE);
        viaPositiveConcept = conceptService.getConceptByUuid(VIA_POSITIVE_ELIGIBLE_FOR_CRYO);
        viaSuspiciousConcept = conceptService.getConceptByUuid(VIA_SUSPICIOUS_RESULT);
        cytologyResultConcept = conceptService.getConceptByUuid(CYTOLOGY_RESULT);
        cytologyNegativeConcept = conceptService.getConceptByUuid(CYTOLOGY_NEGATIVE);
        cytologyASCUSPositiveConcept = conceptService.getConceptByUuid(CYTOLOGY_ASCUS);
        cytologyGreaterASCUSSuspiciousConcept = conceptService.getConceptByUuid(CYTOLOGY_GREATER_ASCUS_SUSPICIOUS);
        cxcaScreeningTypeConcept = conceptService.getConceptByUuid(CXCA_TYPE_OF_SCREENING);

    }

    private List<Integer> getCXCAScreened() {
        HqlQueryBuilder queryBuilder = new HqlQueryBuilder();

        currentPatients = getCurrentPatients();
        if (currentPatients.size() == 0)
            return new ArrayList<>();

        queryBuilder.select("distinct obs.personId");
        queryBuilder.from(Obs.class, "obs")
                .whereEqual("obs.person.gender", "F")
                .and()
                .whereEqual("obs.encounter.encounterType", cxcaDatasetDefinition.getEncounterType())
                .and()
                .whereEqual("obs.concept", cxcaScreenedConcept)
                .and()
                .whereBetweenInclusive("obs.valueDatetime", cxcaDatasetDefinition.getStartDate(),
                        cxcaDatasetDefinition.getEndDate())
                .and()
                .whereIn("obs.personId", currentPatients);
        List<Integer> personId = evaluationService.evaluateToList(queryBuilder, Integer.class, context);
        return personId;
    }

    private List<Integer> getOnArtFemalePatients() {
        HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
        queryBuilder.select("distinct obs.personId").from(Obs.class, "obs")
                .whereEqual("obs.concept", artConcept)
                .and()
                .whereEqual("obs.encounter.encounterType", cxcaDatasetDefinition.getEncounterType())
                .and()
                .whereEqual("obs.person.gender", "F")
                .and()
                .whereLessOrEqualTo("obs.valueDatetime", cxcaDatasetDefinition.getEndDate());

        List<Integer> patientsId = evaluationService.evaluateToList(queryBuilder, Integer.class, context);

        return patientsId;
    }

    private List<Integer> getCurrentPatients() {
        HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
        queryBuilder.select("distinct obv.personId");
        onArtFemalePatients = getOnArtFemalePatients();
        if (onArtFemalePatients.size() == 0)
            return new ArrayList<>();
        queryBuilder.from(Obs.class, "obv")
                .whereEqual("obv.encounter.encounterType", cxcaDatasetDefinition.getEncounterType())
                .and()

                .whereEqual("obv.concept", treatmentEndDateConcept)
                .and()
                .whereGreaterOrEqualTo("obv.valueDatetime", cxcaDatasetDefinition.getEndDate())
                .and()
                .whereLessOrEqualTo("obv.obsDatetime", cxcaDatasetDefinition.getEndDate())
                .whereIn("obv.personId", onArtFemalePatients);
        List<Integer> patientIds = evaluationService.evaluateToList(queryBuilder, Integer.class, context);
        return patientIds;
    }

    private List<Integer> getScreeningTypeCohort(List<Integer> personIds, Concept strategy, Concept expectedResult) {
        if (personIds.size() == 0)
            return new ArrayList<>();
        HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
        queryBuilder.select("distinct obs.personId")
                .from(Obs.class, "obs")
                .whereEqual("obs.concept", strategy)
                .and()
                .whereEqual("obs.encounter.encounterType", cxcaDatasetDefinition.getEncounterType())
                .and()
                .whereEqual("obs.valueCoded", expectedResult)
                .whereBetweenInclusive("obs.obsDatetime", cxcaDatasetDefinition.getStartDate(),
                        cxcaDatasetDefinition.getEndDate())
                .and()
                .whereIn("obs.personId", personIds);

        List<Integer> personIdList = evaluationService.evaluateToList(queryBuilder, Integer.class, context);
        return personIdList;
    }

    private void buildDataSet(DataSetRow dataSet, String name) {
        total = 0;
        minCount = 15;
        maxCount = 19;
        dataSet.addColumnValue(new DataSetColumn("title", " ", String.class), "Cervical Cancer Screen: " + name);
        dataSet.addColumnValue(new DataSetColumn("unknownAge", "Unknown Age", Integer.class),
                getEnrolledByUnknownAge());

        while (minCount <= 50) {
            if (minCount == 50) {
                dataSet.addColumnValue(new DataSetColumn("50+", "50+", Integer.class),
                        getEnrolledByAgeAndGender(50, 200));
            } else {
                dataSet.addColumnValue(
                        new DataSetColumn(minCount + "-" + maxCount, minCount + "-" + maxCount, Integer.class),
                        getEnrolledByAgeAndGender(minCount, maxCount));
            }
            minCount = maxCount + 1;
            maxCount = minCount + 4;
        }
        dataSet.addColumnValue(new DataSetColumn("Sub-total", "Subtotal", Integer.class),
                total);
    }

    private List<Person> getPatients(List<Integer> personIds) {
        if (personIds.size() == 0)
            return new ArrayList<>();
        HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
        queryBuilder.select("person")
                .from(Person.class, "person")
                .whereIn("person.personId", personIds);
        return evaluationService.evaluateToList(queryBuilder, Person.class, context);
    }

    private int getEnrolledByAgeAndGender(int min, int max) {
        int count = 0;
        List<Integer> personIds = new ArrayList<>();
        for (Person person : persons) {

            if (personIds.contains(person.getPersonId()))
                continue;

            if (person.getAge() >= min && person.getAge() <= max) {
                personIds.add(person.getPersonId());
                count++;
            }
        }
        incrementTotalCount(count);
        clearCountedPerson(personIds);
        return count;
    }

    private int getEnrolledByUnknownAge() {
        int count = 0;
        List<Integer> personIds = new ArrayList<>();
        for (Person person : persons) {
            if (personIds.contains(person.getPersonId()))
                continue;

            if (Objects.isNull(person.getAge()) ||
                    person.getAge() <= 0) {
                count++;
                personIds.add(person.getPersonId());
            }

        }
        incrementTotalCount(count);
        clearCountedPerson(personIds);
        return count;
    }

    private void incrementTotalCount(int count) {
        if (count > 0)
            total = total + count;
    }

    private void clearCountedPerson(List<Integer> personIds) {
        for (int pId : personIds) {
            persons.removeIf(p -> p.getPersonId().equals(pId));
        }
    }

}
