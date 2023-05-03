package org.openmrs.module.ohrireports.reports.datasetevaluator.datim.cxca_scrn;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.ART_START_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TREATMENT_END_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HPV_DNA_SCREENING_RESULT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.POSITIVE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.NEGATIVE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.UNKNOWN;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.VIA_SCREENING_RESULT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.VIA_NEGATIVE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.VIA_POSITIVE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.VIA_SUSPICIOUS_RESULT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.CYTOLOGY_RESULT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.CYTOLOGY_ASCUS_POSITIVE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.CYTOLOGY_NEGATIVE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.CYTOLOGY_GREATER_ASCUS_SUSPICIOUS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.CXCA_SCREENING_ACCEPTED_DATE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.module.ohrireports.reports.datasetdefinition.datim.cxca_scrn.CXCAAutoCalculateDatasetDefinition;
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

/**
 * CXCAAutoCalculateDatasetDefinitionEvaluator
 */
// ==============================
// Report ceriatria
/*
 * 1, Only female patient.
 * 2, Must be on art.
 * 3, Must be on regiment which means regiment end date should be >= report end
 * date.
 * 4, Cervical cancer test date should fall between the reporting date range.
 * 5, Cervical cancer screening identifiers
 * - Must be cervical cancer screening type be selected.
 * - Must be screening strategy be selected.
 * - Must have result.
 */
@Handler(supports = { CXCAAutoCalculateDatasetDefinition.class })
public class CXCAAutoCalculateDatasetDefinitionEvaluator implements DataSetEvaluator {
        private EvaluationContext context;
        List<Obs> obses = new ArrayList<>();
        private CXCAAutoCalculateDatasetDefinition cxcaDatasetDefinition;

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
                        cytologyGreaterASCUSSuspiciousConcept;

        @Autowired
        private ConceptService conceptService;

        @Autowired
        private EvaluationService evaluationService;
        private List<Integer> onArtFemalePatients;
        private List<Integer> currentPatients;
        private List<Integer> cxcaScreened;

        @Override
        public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext)
                        throws EvaluationException {

                cxcaDatasetDefinition = (CXCAAutoCalculateDatasetDefinition) dataSetDefinition;
                context = evalContext;
                loadConcepts();
                SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
                DataSetRow dataSetRow = new DataSetRow();
                dataSetRow.addColumnValue(new DataSetColumn("Numerator", "Numerator", Integer.class), GetAllCount());
                dataSet.addRow(dataSetRow);
                return dataSet;
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
                viaPositiveConcept = conceptService.getConceptByUuid(VIA_POSITIVE);
                viaSuspiciousConcept = conceptService.getConceptByUuid(VIA_SUSPICIOUS_RESULT);
                cytologyResultConcept = conceptService.getConceptByUuid(CYTOLOGY_RESULT);
                cytologyNegativeConcept = conceptService.getConceptByUuid(CYTOLOGY_NEGATIVE);
                cytologyASCUSPositiveConcept = conceptService.getConceptByUuid(CYTOLOGY_ASCUS_POSITIVE);
                cytologyGreaterASCUSSuspiciousConcept = conceptService
                                .getConceptByUuid(CYTOLOGY_GREATER_ASCUS_SUSPICIOUS);
        }

        private int GetAllCount() {
                List<Integer> hpvScreenPatientId = getHPVANDDNVScreeningPatients();
                List<Integer> viaScreenPatientId = getVIAScreeningPatients();
                List<Integer> cytologyPatientId = getCytologyScreeningPatients();

                Set<Integer> allMap = new HashSet<Integer>();
                allMap.addAll(hpvScreenPatientId);
                allMap.addAll(cytologyPatientId);
                allMap.addAll(viaScreenPatientId);
                return allMap.size();
        }

        private List<Integer> getCXCAScreened() {
                HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
                queryBuilder.select("distinct obs.personId");
                currentPatients = getCurrentPatients();

                if (currentPatients.size() == 0)
                        return new ArrayList<>();

                queryBuilder.from(Obs.class, "obs")
                                .whereEqual("obs.person.gender", "F")
                                .and()
                                .whereEqual("obs.encounter.encounterType", cxcaDatasetDefinition.getEncounterType())
                                .and()
                                .whereEqual("Obs.concept", cxcaScreenedConcept)
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
                                .whereLess("obv.obsDatetime", cxcaDatasetDefinition.getEndDate())
                                .whereIn("obv.personId", onArtFemalePatients);
                List<Integer> patientIds = evaluationService.evaluateToList(queryBuilder, Integer.class, context);
                return patientIds;
        }

        private List<Integer> getHPVANDDNVScreeningPatients() {
                HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
                
                cxcaScreened = getCXCAScreened();              
                if (cxcaScreened.size() == 0)
                        return new ArrayList<>();
             
                        queryBuilder.select("distinct obs.personId")
                                .from(Obs.class, "obs")
                                .whereEqual("Obs.concept", hpvAndDNAScreeningResultConcept)
                                .and()
                                .whereEqual("obs.encounter.encounterType", cxcaDatasetDefinition.getEncounterType())
                                .and()
                                .whereIn("obs.valueCoded", Arrays.asList(
                                                positiveConcept,
                                                negativeConcept,
                                                unknownConcept))
                                .whereBetweenInclusive("obs.obsDatetime", cxcaDatasetDefinition.getStartDate(),
                                                cxcaDatasetDefinition.getEndDate())
                                .and()
                                .whereIn("obs.personId", cxcaScreened);

                List<Integer> personIdList = evaluationService.evaluateToList(queryBuilder, Integer.class, context);
                return personIdList;
        }

        private List<Integer> getVIAScreeningPatients() {
                HqlQueryBuilder queryBuilder = new HqlQueryBuilder();

                cxcaScreened = getCXCAScreened();              
                if (cxcaScreened.size() == 0)
                        return new ArrayList<>();

                queryBuilder.select("distinct obs.personId")
                                .from(Obs.class, "obs")
                                .whereEqual("Obs.concept", viaScreeningResultConcept)
                                .and()
                                .whereEqual("obs.encounter.encounterType", cxcaDatasetDefinition.getEncounterType())
                                .and()
                                .whereIn("obs.valueCoded", Arrays.asList(
                                                viaNegativeConcept,
                                                viaPositiveConcept,
                                                viaSuspiciousConcept))
                                .whereBetweenInclusive("obs.obsDatetime", cxcaDatasetDefinition.getStartDate(),
                                                cxcaDatasetDefinition.getEndDate())
                                .and()
                                .whereIn("obs.personId", cxcaScreened);

                List<Integer> personIdList = evaluationService.evaluateToList(queryBuilder, Integer.class, context);
                return personIdList;
        }

        private List<Integer> getCytologyScreeningPatients() {
                HqlQueryBuilder queryBuilder = new HqlQueryBuilder();

                cxcaScreened = getCXCAScreened();              
                if (cxcaScreened.size() == 0)
                        return new ArrayList<>();

                queryBuilder.select("distinct obs.personId")
                                .from(Obs.class, "obs")
                                .whereEqual("Obs.concept", cytologyResultConcept)
                                .and()
                                .whereEqual("obs.encounter.encounterType", cxcaDatasetDefinition.getEncounterType())
                                .and()
                                .whereIn("obs.valueCoded", Arrays.asList(
                                                cytologyASCUSPositiveConcept,
                                                cytologyNegativeConcept,
                                                cytologyGreaterASCUSSuspiciousConcept))
                                .whereBetweenInclusive("obs.obsDatetime", cxcaDatasetDefinition.getStartDate(),
                                                cxcaDatasetDefinition.getEndDate())
                                .and()
                                .whereIn("obs.personId", cxcaScreened);

                List<Integer> personIdList = evaluationService.evaluateToList(queryBuilder, Integer.class, context);
                return personIdList;
        }
}